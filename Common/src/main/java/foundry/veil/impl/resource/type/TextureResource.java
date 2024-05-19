package foundry.veil.impl.resource.type;

import foundry.veil.api.client.imgui.VeilIconImGuiUtil;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public record TextureResource(ResourceLocation path, Path filePath, boolean modResource, boolean hidden) implements VeilResource<TextureResource> {

    @Override
    public void render(boolean dragging) {
        float size = ImGui.getTextLineHeight();
        int texture = Minecraft.getInstance().getTextureManager().getTexture(this.path).getId();

        ImGui.pushStyleColor(ImGuiCol.Text, this.isStatic() ? 0xFFAAAAAA : 0xFFFFFFFF);
        if (dragging) {
            ImGui.image(texture, size * 8, size * 8);
        } else {
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
            ImGui.setItemAllowOverlap();
            ImGui.image(texture, size, size);
            ImGui.sameLine();
            ImGui.popStyleVar();

            if (ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.image(texture, size * 16, size * 16);
                ImGui.endTooltip();
            }
            ImGui.sameLine();
            ImGui.text(this.fileName());
        }
        ImGui.popStyleColor();
    }

    @Override
    public List<VeilResourceAction<TextureResource>> getActions() {
        return List.of();
    }

    @Override
    public boolean canHotReload() {
        return true;
    }

    @Override
    public void hotReload() {
    }

    @Override
    public int getIconCode() {
        return 0xF3C5; // Image file icon
    }
}
