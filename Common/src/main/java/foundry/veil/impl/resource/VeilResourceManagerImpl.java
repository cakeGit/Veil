package foundry.veil.impl.resource;

import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.impl.resource.loader.*;
import foundry.veil.impl.resource.tree.VeilResourceFolder;
import foundry.veil.impl.resource.type.McMetaResource;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Manages all veil resources
 */
public class VeilResourceManagerImpl implements VeilResourceManager, PreparableReloadListener {

    private final List<VeilResourceLoader<?>> loaders = new ObjectArrayList<>(8);
    private final Map<String, VeilResourceFolder> modResources = new TreeMap<>();

    public VeilResourceManagerImpl() {
    }

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

    private void loadPack(ResourceManager resourceManager, Map<String, VeilResourceFolder> modResources, PackResources packResources) {
        if (packResources instanceof PackResourcesExtension ext) {
            try {
                ext.veil$listResources(PackType.CLIENT_RESOURCES, (loc, path, modResourcePath) -> {
                    try {
                        this.visitResource(modResources, resourceManager, loc, path, modResourcePath);
                    } catch (IOException e) {
                        Veil.LOGGER.error("Error loading resource: {}", loc, e);
                    }
                });
                return;
            } catch (Exception e) {
                Veil.LOGGER.error("Failed to load resources from {}", this.getClass().getSimpleName(), e);
            }
        }

        for (String namespace : packResources.getNamespaces(PackType.CLIENT_RESOURCES)) {
            packResources.listResources(PackType.CLIENT_RESOURCES, namespace, "", (loc, inputStreamIoSupplier) -> {
                try {
                    this.visitResource(modResources, resourceManager, loc, null, null);
                } catch (IOException e) {
                    Veil.LOGGER.error("Error loading resource: {}", loc, e);
                }
            });
        }
    }

    private void visitResource(Map<String, VeilResourceFolder> modResources, ResourceProvider provider, ResourceLocation loc, @Nullable Path path, @Nullable Path modResourcePath) throws IOException {
        for (VeilResourceLoader<?> loader : this.loaders) {
            if (loader.canLoad(loc, path, modResourcePath)) {
                VeilResourceFolder modFolder = modResources.computeIfAbsent(loc.getNamespace(), VeilResourceFolder::new);
                modFolder.addResource(loc.getPath(), loader.load(this, provider, loc, path, modResourcePath));
                return;
            }
        }

        // If no loaders can load the resource, add it as an unknown resource
        VeilResourceFolder modFolder = modResources.computeIfAbsent(loc.getNamespace(), VeilResourceFolder::new);
        modFolder.addResource(loc.getPath(), UnknownResourceLoader.INSTANCE.load(this, provider, loc, path, modResourcePath));
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, VeilResourceFolder> modResources = new HashMap<>();
            resourceManager.listPacks().forEach(pack -> this.loadPack(resourceManager, modResources, pack));
            return modResources;
        }, backgroundExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(map -> {
            this.modResources.clear();
            this.modResources.putAll(map);
        }, gameExecutor);
    }

    @Override
    public @Nullable VeilResource<?> getVeilResource(String namespace, String path) {
        VeilResourceFolder folder = this.modResources.get(namespace);
        if (folder == null) {
            return null;
        }

        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            folder = folder.getFolder(parts[i]);
            if (folder == null) {
                return null;
            }
        }

        return folder.getResource(parts[parts.length - 1]);
    }

    /**
     * @return All mod resource folders
     */
    public Collection<VeilResourceFolder> getAllModResources() {
        return this.modResources.values();
    }
}
