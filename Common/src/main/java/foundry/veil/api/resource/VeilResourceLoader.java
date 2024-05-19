package foundry.veil.api.resource;

import foundry.veil.impl.resource.VeilResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public interface VeilResourceLoader<T extends VeilResource<?>> {

    /**
     * @param path        The path to load the resource from
     * @param filePath    The file path of the resource
     * @param modResource Whether the file is located in the mod resources for the current dev environment
     * @return If this resource loader recognizes & can load the specified extension
     */
    boolean canLoad(ResourceLocation path, @Nullable Path filePath, boolean modResource);


    /**
     * Loads the resource from the specified path
     *
     * @param path        The path to load the resource from
     * @param filePath    The file path of the resource
     * @param modResource Whether the file is located in the mod resources for the current dev environment
     * @return The loaded resource
     */
    VeilResource<T> load(VeilResourceManager resourceManager, ResourceProvider provider, ResourceLocation path, @Nullable Path filePath, boolean modResource) throws IOException;
}
