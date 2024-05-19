package foundry.veil.api.resource;

import foundry.veil.api.client.imgui.VeilImGuiUtil;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

import java.util.List;

public interface VeilResource<T extends VeilResource<?>> {

    /**
     * Rebders this resource into the resource panel.
     *
     * @param dragging Whether the user is dragging the resource
     */
    default void render(boolean dragging) {
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        ImGui.setItemAllowOverlap();
        VeilImGuiUtil.icon(this.getIconCode());
        ImGui.sameLine();
        ImGui.popStyleVar();

        VeilResourceInfo resource = this.resourceInfo();
        ImGui.pushStyleColor(ImGuiCol.Text, resource.isStatic() ? 0xFFAAAAAA : 0xFFFFFFFF);
        if (dragging) {
            VeilImGuiUtil.resourceLocation(resource.path());
        } else {
            ImGui.text(resource.fileName());
        }
        ImGui.popStyleColor();
    }

    VeilResourceInfo resourceInfo();

    /**
     * @return All actions that can be performed on this resource
     */
    List<VeilResourceAction<T>> getActions();

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
}
