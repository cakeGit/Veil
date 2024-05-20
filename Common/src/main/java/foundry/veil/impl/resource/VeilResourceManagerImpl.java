package foundry.veil.impl.resource;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.api.util.CompositeReloadListener;
import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.impl.resource.loader.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NativeResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * Manages all veil resources
 */
public class VeilResourceManagerImpl implements VeilResourceManager, NativeResource {

    private final List<VeilResourceLoader<?>> loaders = new ObjectArrayList<>(8);
    private final List<VeilPackResources> packResources = new LinkedList<>();

    private ResourceManager serverResourceManager = ResourceManager.Empty.INSTANCE;

    public void addVeilLoaders(VeilRenderer renderer) {
        this.addLoader(new ShaderResourceLoader(renderer.getShaderManager()));
        this.addLoader(new ShaderResourceLoader(renderer.getDeferredRenderer().getDeferredShaderManager()));
        this.addLoader(new TextureResourceLoader());
        this.addLoader(new McMetaResourceLoader());
        this.addLoader(new TextResourceLoader());
        this.addLoader(new JsonResourceLoader());
    }

    /**
     * Adds a resource loader to the resource manager
     */
    public void addLoader(VeilResourceLoader<?> loader) {
        this.loaders.add(loader);
    }

    private void loadPack(ResourceManager resourceManager, VeilPackResources resources, PackResources packResources) {
        if (packResources instanceof PackResourcesExtension ext) {
            try {
                ext.veil$listResources((packType, loc, path, modResourcePath) -> {
                    try {
                        this.visitResource(packType, resources, resourceManager, loc, path, modResourcePath);
                    } catch (Exception e) {
                        Veil.LOGGER.error("Error loading resource: {}", loc, e);
                    }
                });
                return;
            } catch (Exception e) {
                Veil.LOGGER.error("Failed to load resources from {}", this.getClass().getSimpleName(), e);
            }
        }

        for (PackType packType : PackType.values()) {
            for (String namespace : packResources.getNamespaces(PackType.CLIENT_RESOURCES)) {
                packResources.listResources(packType, namespace, "", (loc, inputStreamIoSupplier) -> {
                    try {
                        this.visitResource(packType, resources, resourceManager, loc, null, null);
                    } catch (Exception e) {
                        Veil.LOGGER.error("Error loading resource: {}", loc, e);
                    }
                });
            }
        }
    }

    private void visitResource(@Nullable PackType packType, VeilPackResources resources, ResourceProvider provider, ResourceLocation loc, @Nullable Path path, @Nullable Path modResourcePath) throws IOException {
        for (VeilResourceLoader<?> loader : this.loaders) {
            if (loader.canLoad(loc, path, modResourcePath)) {
                try {
                    resources.add(packType, loc, loader.load(this, provider, packType, loc, path, modResourcePath));
                } catch (Exception e) {
                    Veil.LOGGER.error("Error loading resource: {}", loc, e);
                }
                return;
            }
        }

        // If no loaders can load the resource, add it as an unknown resource
        resources.add(packType, loc, UnknownResourceLoader.INSTANCE.load(this, provider, packType, loc, path, modResourcePath));
    }

    public PreparableReloadListener createReloadListener() {
        return CompositeReloadListener.of(this::reloadClient, this::reloadServer);
    }

    private CompletableFuture<Void> reloadClient(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            List<VeilPackResources> packs = new LinkedList<>();
            resourceManager.listPacks().flatMap(pack -> pack instanceof PackResourcesExtension extension ? extension.veil$listPacks() : Stream.of(pack)).forEach(pack -> {
                VeilPackResources resources = new VeilPackResources(pack.packId());
                this.loadPack(resourceManager, resources, pack);
                packs.add(resources);

                if (pack instanceof PackResourcesExtension extension) {
                    IoSupplier<InputStream> icon = extension.veil$getIcon();
                    if (icon != null) {
                        try {
                            NativeImage image = NativeImage.read(icon.get());
                            RenderSystem.recordRenderCall(() -> {
                                try (image) {
                                    resources.loadIcon(image);
                                }
                            });
                        } catch (Exception e) {
                            Veil.LOGGER.error("Failed to load icon for pack: {}", pack.packId(), e);
                        }
                    }
                }
            });
            return packs;
        }, backgroundExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(list -> {
            this.free();
            this.packResources.addAll(list);
        }, gameExecutor);
    }

    private CompletableFuture<Void> reloadServer(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> this.serverResourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, resourceManager.listPacks().toList()), backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(serverResourceManager -> this.serverResourceManager = serverResourceManager, gameExecutor);
    }

    @Override
    public ResourceManager clientResources() {
        return Minecraft.getInstance().getResourceManager();
    }

    @Override
    public ResourceManager serverResources() {
        return this.serverResourceManager;
    }

    @Override
    public @Nullable VeilResource<?> getVeilResource(String namespace, String path) {
        for (VeilPackResources pack : this.packResources) {
            VeilResource<?> resource = pack.getVeilResource(namespace, path);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public void free() {
        for (VeilPackResources resources : this.packResources) {
            resources.free();
        }
        this.packResources.clear();
    }

    /**
     * @return All pack folders
     */
    public Collection<VeilPackResources> getAllPacks() {
        return this.packResources;
    }
}
