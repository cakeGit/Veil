package foundry.veil.impl.client.editor;

import foundry.veil.VeilClient;
import foundry.veil.api.client.editor.SingleWindowEditor;
import foundry.veil.api.client.imgui.VeilIconImGuiUtil;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.impl.resource.VeilResourceManager;
import foundry.veil.impl.resource.VeilResourceRenderer;
import foundry.veil.impl.resource.tree.VeilResourceFolder;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import net.minecraft.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
public class ResourceManagerEditor extends SingleWindowEditor {

    private VeilResource<?> contextResource;
    private List<? extends VeilResourceAction<?>> actions;

    public ResourceManagerEditor() {
    }

    @Override
    public void renderComponents() {
        this.contextResource = null;
        this.actions = Collections.emptyList();

        VeilResourceManager resourceManager = VeilClient.resourceManager();
        if (ImGui.beginListBox("##file_tree", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY())) {
            for (VeilResourceFolder folder : resourceManager.getAllModResources()) {
                String modid = folder.getName();
                int color = VeilImGuiUtil.colorOf(modid);

                boolean open = ImGui.treeNodeEx("##" + modid, ImGuiTreeNodeFlags.SpanAvailWidth);

                ImGui.pushStyleColor(ImGuiCol.Text, color);
                ImGui.sameLine();
                VeilIconImGuiUtil.icon(0xEA7D, color);
                ImGui.sameLine();
                ImGui.text(modid);
                ImGui.popStyleColor();

                if (open) {
                    this.renderFolderContents(folder);
                    ImGui.treePop();
                }

                ImGui.separator();
            }
            ImGui.endListBox();
        }
    }

    private void renderFolder(VeilResourceFolder folder) {
        boolean open = ImGui.treeNodeEx("##" + folder.getName(), ImGuiTreeNodeFlags.SpanAvailWidth);
        ImGui.sameLine();
        VeilIconImGuiUtil.icon(open ? 0xED6F : 0xF43B);
        ImGui.sameLine();
        ImGui.text(folder.getName());

        if (open) {
            this.renderFolderContents(folder);
            ImGui.treePop();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void renderFolderContents(VeilResourceFolder folder) {
        for (VeilResourceFolder subFolder : folder.getSubFolders()) {
            this.renderFolder(subFolder);
        }

        ImGui.indent();
        for (VeilResource<?> resource : folder.getResources()) {
            if (resource.hidden()) {
                continue;
            }

            if (ImGui.selectable("##" + resource.hashCode())) {
            }

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
            ImGui.setItemAllowOverlap();
            ImGui.sameLine();
            ImGui.popStyleVar();
            VeilResourceRenderer.renderFilename(resource);

            if (ImGui.beginPopupContextItem("" + resource.hashCode())) {
                if (resource != this.contextResource) {
                    this.contextResource = resource;
                    this.actions = resource.getActions();
                }

                if (ImGui.selectable("##copy_path")) {
                    ImGui.setClipboardText(resource.path().toString());
                }

                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
                ImGui.setItemAllowOverlap();
                ImGui.sameLine();
                VeilIconImGuiUtil.icon(0xEB91);
                ImGui.sameLine();
                ImGui.popStyleVar();
                ImGui.text("Copy Path");

                Path filePath = resource.filePath();
                ImGui.beginDisabled(filePath == null || filePath.getFileSystem() != FileSystems.getDefault());
                if (ImGui.selectable("##open_folder")) {
                    Util.getPlatform().openFile(filePath.getParent().toFile());
                }

                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
                ImGui.setItemAllowOverlap();
                ImGui.sameLine();
                VeilIconImGuiUtil.icon(0xECAF);
                ImGui.sameLine();
                ImGui.popStyleVar();
                ImGui.text("Open in Explorer");
                ImGui.endDisabled();

                for (int i = 0; i < this.actions.size(); i++) {
                    VeilResourceAction action = this.actions.get(i);
                    if (ImGui.selectable("##action" + i)) {
                        action.perform(resource);
                    }

                    ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
                    ImGui.setItemAllowOverlap();
                    ImGui.sameLine();
                    action.getIcon().ifPresent(icon -> {
                        VeilIconImGuiUtil.icon(icon);
                        ImGui.sameLine();
                    });
                    ImGui.popStyleVar();
                    ImGui.text(action.getName());
                }

                ImGui.endPopup();
            }
        }
        ImGui.unindent();
    }

    @Override
    public String getDisplayName() {
        return "Resource Browser";
    }

    @Override
    public @Nullable String getGroup() {
        return "Resources";
    }
}
