package foundry.veil.impl.client.editor;

import foundry.veil.VeilClient;
import foundry.veil.api.client.editor.SingleWindowEditor;
import foundry.veil.api.client.imgui.VeilIconImGuiUtil;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.impl.resource.VeilResourceManager;
import foundry.veil.impl.resource.VeilResourceRenderer;
import foundry.veil.impl.resource.tree.VeilResourceFolder;
import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImInt;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@ApiStatus.Internal
public class ResourceManagerEditor extends SingleWindowEditor {

    public ResourceManagerEditor() {

    }

    @Override
    public void renderComponents() {
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
        VeilIconImGuiUtil.icon(0xF43B);
        ImGui.sameLine();
        ImGui.text(folder.getName());

        if (open) {
            this.renderFolderContents(folder);
            ImGui.treePop();
        }
    }

    private void renderFolderContents(VeilResourceFolder folder) {
        for (VeilResourceFolder subFolder : folder.getSubFolders()) {
            this.renderFolder(subFolder);
        }

        for (VeilResource<?> resource : folder.getResources()) {
            ImGui.indent();
            VeilResourceRenderer.renderFilename(resource);
            ImGui.unindent();
        }
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
