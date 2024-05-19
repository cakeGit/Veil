package foundry.veil.impl.client.editor;

import foundry.veil.api.client.editor.SingleWindowEditor;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiDragDropFlags;
import imgui.type.ImInt;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@ApiStatus.Internal
public class PostEditor extends SingleWindowEditor {

    private final Set<ResourceLocation> removedPipelines;

    public PostEditor() {
        this.removedPipelines = new HashSet<>(1);
    }

    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 0);
        super.render();
    }

    @Override
    public void renderComponents() {
        this.removedPipelines.clear();
        PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();

        float availableWidth = ImGui.getContentRegionAvailX();

        ImGui.setNextItemWidth(availableWidth / 2);
        ImGui.beginGroup();
        ImGui.text("Inactive Pipelines:");
        if (ImGui.beginListBox("##available_pipelines", availableWidth / 2, 0)) {
            for (ResourceLocation entry : postProcessingManager.getPipelines()) {
                if (postProcessingManager.isActive(entry)) {
                    continue;
                }

                VeilImGuiUtil.resourceLocation(entry);

                if (ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
                    ImGui.setDragDropPayload("POST_PIPELINE", entry, ImGuiCond.Once);
                    VeilImGuiUtil.resourceLocation(entry);

                    ImGui.endDragDropSource();
                }
            }

            ImGui.endListBox();
        }

        if (ImGui.beginDragDropTarget()) {

            ResourceLocation payload = ImGui.acceptDragDropPayload("POST_PIPELINE");

            if (payload != null) {
                this.removedPipelines.add(payload);
            }

            ImGui.endDragDropTarget();
        }

        ImGui.endGroup();

        ImGui.sameLine();

        ImGui.setNextItemWidth(availableWidth / 2);
        ImGui.beginGroup();
        ImGui.text("Active Pipelines:");

        if (ImGui.beginListBox("##shaders", availableWidth / 2, 0)) {
            for (PostProcessingManager.ProfileEntry entry : postProcessingManager.getActivePipelines()) {
                ResourceLocation id = entry.getPipeline();
                ImInt editPriority = new ImInt(entry.getPriority());

                ImGui.pushID(id.toString());
                if (ImGui.beginChild(id.toString())) {
                    VeilImGuiUtil.resourceLocation(id);

                    if (ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
                        ImGui.setDragDropPayload("POST_PIPELINE", id, ImGuiCond.Once);
                        VeilImGuiUtil.resourceLocation(id);
                        ImGui.endDragDropSource();
                    }

                    float priorityWidth = ImGui.calcTextSize("999999").x;
                    ImGui.sameLine(ImGui.getContentRegionAvailX() - priorityWidth - 2);
                    ImGui.setNextItemWidth(priorityWidth);
                    if (ImGui.dragScalar("##priority", ImGuiDataType.S32, editPriority, 1)) {
                        entry.setPriority(editPriority.get());
                    }
                }
                ImGui.endChild();
                ImGui.popID();


                ImGui.sameLine();
            }

            ImGui.endListBox();
        }

        if (ImGui.beginDragDropTarget()) {
            ResourceLocation payload = ImGui.acceptDragDropPayload("POST_PIPELINE");

            if (payload != null) {
                postProcessingManager.add(payload);
            }

            ImGui.endDragDropTarget();
        }
        ImGui.endGroup();


        for (ResourceLocation id : this.removedPipelines) {
            postProcessingManager.remove(id);
        }
    }

    @Override
    public String getDisplayName() {
        return "Post Shaders";
    }

    @Override
    public @Nullable String getGroup() {
        return "Renderer";
    }
}
