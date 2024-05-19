package foundry.veil.impl.resource;

import foundry.veil.api.resource.VeilResource;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import net.minecraft.Util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class VeilResourceRenderer {

    public static void renderFilename(VeilResource<?> resource, boolean dragging) {
        int id = resource.hashCode();
        ImGui.pushID(id);
        ImGui.beginGroup();
        resource.render(dragging);
        ImGui.endGroup();
        ImGui.popID();

        if (!dragging && ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
            ImGui.setDragDropPayload("VEIL_RESOURCE", resource, ImGuiCond.Once);
            renderFilename(resource, true);
            ImGui.endDragDropSource();
        }

        if (ImGui.beginPopupContextItem("" + id)) {
            if (ImGui.menuItem("Copy Path")) {
                ImGui.setClipboardText(resource.path().toString());
            }

            Path filePath = resource.filePath();
            ImGui.beginDisabled(filePath == null || filePath.getFileSystem() != FileSystems.getDefault());
            if (ImGui.menuItem("Open in Explorer")) {
                Util.getPlatform().openFile(filePath.getParent().toFile());
            }
            ImGui.endDisabled();

            ImGui.endPopup();
        }
    }

    public static void renderFilename(VeilResource<?> resource) {
        renderFilename(resource, false);
    }

}
