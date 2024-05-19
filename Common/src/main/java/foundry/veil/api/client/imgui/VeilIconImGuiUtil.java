package foundry.veil.api.client.imgui;

import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import imgui.ImGui;

public class VeilIconImGuiUtil {

    /**
     * Renders an icon with the remixicon font
     * @param code The icon code (ex. &#xED0F;)
     */
    public static void icon(int code) {
        ImGui.pushFont(VeilRenderSystem.renderer().getEditorManager().getFont(Veil.veilPath("remixicon"), false, false));
        ImGui.text("" + (char) code);
        ImGui.popFont();
    }

    /**
     * Renders an icon with the remixicon font and a color
     *
     * @param code The icon code (ex. &#xED0F;)
     * @param color The color of the icon
     */
    public static void icon(int code, int color) {
        ImGui.pushFont(VeilRenderSystem.renderer().getEditorManager().getFont(Veil.veilPath("remixicon"), false, false));
        ImGui.textColored(color, "" + (char) code);
        ImGui.popFont();
    }

}
