package foundry.veil.api.client.imgui;

import imgui.ImGui;

/**
 * Extra components and helpers for ImGui.
 */
public class VeilImGuiUtil {

    /**
     * Displays a (?) with a hover tooltip. Useful for example information.
     * @param text The tooltip text
     */
    public static void tooltip(String text) {
        ImGui.textDisabled("(?)");
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(text);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }
}
