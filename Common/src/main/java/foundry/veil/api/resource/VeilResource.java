package foundry.veil.api.resource;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;

public interface VeilResource<T extends VeilResource<?>> {

    /**
     * @return The resource location path this resource is located at
     */
    ResourceLocation path();

    /**
     * @return The file path of this resource
     */
    @Nullable
    Path filePath();

    /**
     * @return Whether the file is located in the mod resources for the current dev environment
     */
    boolean modResource();

    /**
     * @return All actions that can be performed on this resource
     */
    Collection<VeilResourceAction<T>> getActions();

    /**
     * @return If this resource can be hot-reloaded
     */
    boolean canHotReload();

    /**
     * Hot-reloads the resource
     */
    void hotReload();

    /**
     * Gets the icon code for this resource (ex. 0xED0F)
     */
    int getIconCode();

    /**
     * @return The file name of this resource
     */
    default String fileName() {
        String path = this.path().getPath();
        String[] split = path.split("/");
        return split[split.length - 1];
    }
}
