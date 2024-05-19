package foundry.veil.impl.resource;

import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.impl.resource.loader.UnknownResourceLoader;
import foundry.veil.impl.resource.tree.VeilResourceFolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * Manages all veil resources
 */
public class VeilResourceManager implements PreparableReloadListener {
    private final List<VeilResourceLoader<?>> loaders = new ObjectArrayList<>(8);
    private final Map<String, VeilResourceFolder> modResources = new Object2ObjectOpenHashMap<>();

    /**
     * Adds a resource loader to the resource manager
     */
    public void addLoader(VeilResourceLoader<?> loader) {
        this.loaders.add(loader);
    }

    /**
     * Clears all saved veil resources, and loads all client resources
     */
    public void load(ResourceManager resourceManager) {
        this.modResources.clear();

        resourceManager.listPacks().forEach(this::loadPack);
    }

    /**
     * Loads all resources in a pack into the tree
     * @param packResources The pack to load resources from
     */
    private void loadPack(PackResources packResources) {
        if (packResources instanceof PackResourcesExtension ext) {
            ext.veil$listResources(PackType.CLIENT_RESOURCES, this::visitResource);
            return;
        }

            for (String namespace : packResources.getNamespaces(PackType.CLIENT_RESOURCES)) {
                if (namespace.startsWith("fabric-")) { // TODO: Evaluate a more proper solution
                    continue;
                }

                packResources.listResources(PackType.CLIENT_RESOURCES, namespace, "", (loc, inputStreamIoSupplier) -> this.visitResource(loc, null));
            }
    }

    /**
     * Visits a resource, and asks all resource loaders if they can load the resource.
     */
    private void visitResource(ResourceLocation loc, @Nullable Path path) {
        for (VeilResourceLoader<?> loader : this.loaders) {
            if (loader.canLoad(loc, path)) {
                VeilResourceFolder modFolder = this.modResources.computeIfAbsent(loc.getNamespace(), VeilResourceFolder::new);
                modFolder.addResource(loc.getPath(), loader.load(loc, path));

                return;
            }
        }

        // If no loaders can load the resource, add it as an unknown resource
        VeilResourceFolder modFolder = this.modResources.computeIfAbsent(loc.getNamespace(), VeilResourceFolder::new);
        modFolder.addResource(loc.getPath(), UnknownResourceLoader.INSTANCE.load(loc, path));
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            this.load(resourceManager);
        }, backgroundExecutor).thenCompose(preparationBarrier::wait);
    }


    /**
     * @return All mod resource folders
     */
    public Collection<VeilResourceFolder> getAllModResources() {
        return this.modResources.values();
    }
}
