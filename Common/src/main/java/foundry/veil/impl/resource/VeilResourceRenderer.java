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
        ImGui.pushID(resource.hashCode());
        ImGui.beginGroup();
        resource.render(dragging);
        ImGui.endGroup();
        ImGui.popID();

        if (!dragging && ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
            ImGui.setDragDropPayload("VEIL_RESOURCE", resource, ImGuiCond.Once);
            renderFilename(resource, true);
            ImGui.endDragDropSource();
        }
    }

    public static void renderFilename(VeilResource<?> resource) {
        renderFilename(resource, false);
    }

}
