package foundry.veil.api.resource;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface VeilResourceLoader<T extends VeilResource<?>> {

    /**
     * @param path The path to load the resource from
     * @param filePath The file path of the resource
     * @return If this resource loader recognizes & can load the specified extension
     */
    boolean canLoad(ResourceLocation path, @Nullable Path filePath);

    /**
     * Loads the resource from the specified path
     *
     * @param path The path to load the resource from
     * @param filePath The file path of the resource
     * @return The loaded resource
     */
    VeilResource<T> load(ResourceLocation path, @Nullable Path filePath);

}
