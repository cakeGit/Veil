package foundry.veil.impl.resource;

import foundry.veil.Veil;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.impl.resource.loader.ShaderResourceLoader;
import foundry.veil.impl.resource.loader.UnknownResourceLoader;
import foundry.veil.impl.resource.tree.VeilResourceFolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Manages all veil resources
 */
public class VeilResourceManager implements PreparableReloadListener {

    private final List<VeilResourceLoader<?>> loaders = new ObjectArrayList<>(8);
    private final Map<String, VeilResourceFolder> modResources = new TreeMap<>();

    public VeilResourceManager() {
        this.addLoader(new ShaderResourceLoader());
    }

    /**
     * Adds a resource loader to the resource manager
     */
    public void addLoader(VeilResourceLoader<?> loader) {
        this.loaders.add(loader);
    }

    private void loadPack(Map<String, VeilResourceFolder> modResources, PackResources packResources) {
        if (packResources instanceof PackResourcesExtension ext) {
            try {
                ext.veil$listResources(PackType.CLIENT_RESOURCES, (resourceLocation, path, modResource) -> this.visitResource(modResources, resourceLocation, path, modResource));
                return;
            } catch (Exception e) {
                Veil.LOGGER.error("Failed to load resources from {}", this.getClass().getSimpleName(), e);
            }
        }

        for (String namespace : packResources.getNamespaces(PackType.CLIENT_RESOURCES)) {
            packResources.listResources(PackType.CLIENT_RESOURCES, namespace, "", (loc, inputStreamIoSupplier) -> this.visitResource(modResources, loc, null, false));
        }
    }

    private void visitResource(Map<String, VeilResourceFolder> modResources, ResourceLocation loc, @Nullable Path path, boolean modResource) {
        for (VeilResourceLoader<?> loader : this.loaders) {
            if (loader.canLoad(loc, path, modResource)) {
                VeilResourceFolder modFolder = modResources.computeIfAbsent(loc.getNamespace(), VeilResourceFolder::new);
                modFolder.addResource(loc.getPath(), loader.load(loc, path, modResource));
                return;
            }
        }

        // If no loaders can load the resource, add it as an unknown resource
        VeilResourceFolder modFolder = modResources.computeIfAbsent(loc.getNamespace(), VeilResourceFolder::new);
        modFolder.addResource(loc.getPath(), UnknownResourceLoader.INSTANCE.load(loc, path, modResource));
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, VeilResourceFolder> modResources = new HashMap<>();
            resourceManager.listPacks().forEach(pack -> this.loadPack(modResources, pack));
            return modResources;
        }, backgroundExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(map -> {
            this.modResources.clear();
            this.modResources.putAll(map);
        }, gameExecutor);
    }


    /**
     * @return All mod resource folders
     */
    public Collection<VeilResourceFolder> getAllModResources() {
        return this.modResources.values();
    }
}
