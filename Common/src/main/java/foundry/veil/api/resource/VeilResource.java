package foundry.veil.api.resource;

import foundry.veil.api.client.imgui.VeilIconImGuiUtil;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;

public interface VeilResource<T extends VeilResource<?>> {

    /**
     * Rebders this resource into the resource panel.
     *
     * @param dragging Whether the user is dragging the resource
     */
    default void render(boolean dragging) {
        VeilIconImGuiUtil.icon(this.getIconCode());
        ImGui.sameLine();

        ImGui.pushStyleColor(ImGuiCol.Text, this.isStatic() ? 0xFFAAAAAA : 0xFFFFFFFF);
        if (dragging) {
            ImGui.text(this.fileName());
        } else {
            VeilImGuiUtil.resourceLocation(this.path());
        }
        ImGui.popStyleColor();
    }

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
     * @return Whether this resource should appear in the resource panel
     */
    boolean hidden();

    /**
     * @return If this file cannot be accessed by the native file system
     */
    default boolean isStatic() {
        Path filePath = this.filePath();
        return filePath == null || filePath.getFileSystem() != FileSystems.getDefault();
    }

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
