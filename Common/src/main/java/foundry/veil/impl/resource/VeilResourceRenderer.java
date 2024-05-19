package foundry.veil.impl.resource;

import foundry.veil.VeilClient;
import foundry.veil.api.client.imgui.VeilIconImGuiUtil;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import foundry.veil.api.resource.VeilResource;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
            if (ImGui.menuItem("Open in Explorer")) {
                Path filePath = resource.filePath();

                if (filePath != null && filePath.getFileSystem() == FileSystems.getDefault()) {
                    Minecraft client = Minecraft.getInstance();
                    CompletableFuture.runAsync(() -> Util.getPlatform().openFile(filePath.getParent().toFile()), client);
                }
            }

            ImGui.endPopup();
        }
    }

    public static void renderFilename(VeilResource<?> resource) {
        renderFilename(resource, true);
    }

}
