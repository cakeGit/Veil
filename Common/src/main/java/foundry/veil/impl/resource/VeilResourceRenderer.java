package foundry.veil.impl.resource;

import foundry.veil.api.client.imgui.VeilIconImGuiUtil;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import foundry.veil.api.resource.VeilResource;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import net.minecraft.Util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class VeilResourceRenderer {

    public static void renderFilename(VeilResource<?> resource, boolean seated) {
        String id = "" + resource.hashCode();
        ImGui.pushID(id);
        ImGui.beginGroup();
        VeilIconImGuiUtil.icon(resource.getIconCode());
        ImGui.sameLine();

        if (seated) {
            ImGui.text(resource.fileName());
        } else {
            VeilImGuiUtil.resourceLocation(resource.path());
        }

        ImGui.endGroup();
        ImGui.popID();

        if (seated && ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
            ImGui.setDragDropPayload("VEIL_RESOURCE", resource, ImGuiCond.Once);
            renderFilename(resource, false);
            ImGui.endDragDropSource();
        }

        if (ImGui.beginPopupContextItem(id)) {
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
        renderFilename(resource, true);
    }

}
