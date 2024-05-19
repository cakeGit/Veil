package foundry.veil.api.client.imgui;


import foundry.veil.Veil;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.editor.EditorManager;
import foundry.veil.api.client.render.VeilRenderSystem;
import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/**
 * Extra components and helpers for ImGui.
 */
public class VeilImGuiUtil {

    private static final ImGuiCharSink IM_GUI_CHAR_SINK = new ImGuiCharSink();
    private static final StringSplitter IM_GUI_SPLITTER = new StringSplitter((charId, style) -> getStyleFont(style).getCharAdvance(charId));
    private static final ResourceLocation ICON_FONT = Veil.veilPath("remixicon");

    /**
     * Displays a (?) with a hover tooltip. Useful for example information.
     *
     * @param text The tooltip text
     */
    public static void tooltip(String text) {
        ImGui.textColored(0xFF555555, "(?)");
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textColored(0xFFFFFFFF, text);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

    public static void component(FormattedText text) {
        component(text, Float.POSITIVE_INFINITY);
    }

    public static void component(FormattedText text, float wrapWidth) {
        IM_GUI_CHAR_SINK.reset();
        for (FormattedCharSequence part : Language.getInstance().getVisualOrder(IM_GUI_SPLITTER.splitLines(text, (int) wrapWidth, Style.EMPTY))) {
            part.accept(IM_GUI_CHAR_SINK);
            IM_GUI_CHAR_SINK.finish();
            ImGui.newLine();
        }
    }

    /**
     * Renders an icon with the remixicon font
     *
     * @param code The icon code (ex. &#xED0F;)
     */
    public static void icon(int code) {
        ImGui.pushFont(VeilRenderSystem.renderer().getEditorManager().getFont(ICON_FONT, false, false));
        ImGui.text("" + (char) code);
        ImGui.popFont();
    }

    /**
     * Renders an icon with the remixicon font and a color
     *
     * @param code  The icon code (ex. &#xED0F;)
     * @param color The color of the icon
     */
    public static void icon(int code, int color) {
        ImGui.pushFont(VeilRenderSystem.renderer().getEditorManager().getFont(ICON_FONT, false, false));
        ImGui.textColored(color, "" + (char) code);
        ImGui.popFont();
    }

    public static ImFont getStyleFont(Style style) {
        return VeilRenderSystem.renderer().getEditorManager().getFont(Style.DEFAULT_FONT.equals(style.getFont()) ? EditorManager.DEFAULT : style.getFont(), style.isBold(), style.isItalic());
    }

    public static StringSplitter getStringSplitter() {
        return IM_GUI_SPLITTER;
    }

    private static class ImGuiCharSink implements FormattedCharSink {

        private ImFont font;
        private int textColor;
        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;

        private final StringBuilder buffer;

        private ImGuiCharSink() {
            this.buffer = new StringBuilder();
            this.reset();
        }

        public void reset() {
            this.font = ImGui.getFont();
            this.textColor = -1;
            this.buffer.setLength(0);
            this.hoverEvent = null;
            this.clickEvent = null;
        }

        @Override
        public boolean accept(int unknown, Style style, int codePoint) {
            ImFont font = getStyleFont(style);
            int styleColor = style.getColor() != null ? style.getColor().getValue() : this.textColor;
            if (font != this.font || styleColor != this.textColor || style.getHoverEvent() != this.hoverEvent || style.getClickEvent() != this.clickEvent) {
                if (!this.buffer.isEmpty()) {
                    this.finish();
                }
                this.font = getStyleFont(style);
                this.textColor = styleColor;
                this.hoverEvent = style.getHoverEvent();
                this.clickEvent = style.getClickEvent();
            }
            this.buffer.appendCodePoint(codePoint);
            return true;
        }

        public void finish() {
            if (!this.buffer.isEmpty()) {
                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
                ImGui.pushFont(this.font);
                ImGui.textColored(0xFF000000 | (this.textColor & 0xFF0000) >> 16 | (this.textColor & 0xFF00) | (this.textColor & 0xFF) << 16, this.buffer.toString());

                if (ImGui.isItemClicked() && this.clickEvent != null) {
                    this.handleClick();
                }
                if (ImGui.isItemHovered() && this.hoverEvent != null) {
                    this.handleHover();
                }

                ImGui.sameLine();
                ImGui.popFont();
                ImGui.popStyleVar();
                this.buffer.setLength(0);
            }
        }

        private void handleClick() {
            Minecraft minecraft = Minecraft.getInstance();
            String value = this.clickEvent.getValue();
            if (this.clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                try {
                    URI uri = new URI(value);
                    String scheme = uri.getScheme();
                    if (scheme == null) {
                        throw new URISyntaxException(value, "Missing protocol");
                    }

                    if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                        throw new URISyntaxException(value, "Unsupported protocol: " + scheme.toLowerCase(Locale.ROOT));
                    }

                    Util.getPlatform().openUri(uri);
                } catch (URISyntaxException e) {
                    Veil.LOGGER.error("Can't open url for {}", this.clickEvent, e);
                }
                return;
            }

            if (this.clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                Util.getPlatform().openUri(new File(value).toURI());
                return;
            }

            // TODO
            if (this.clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                return;
            }

            if (this.clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                return;
            }

            if (this.clickEvent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                minecraft.keyboardHandler.setClipboard(value);
                return;
            }

            Veil.LOGGER.error("Don't know how to handle {}", this.clickEvent);
        }

        private void handleHover() {
            Minecraft minecraft = Minecraft.getInstance();
            HoverEvent.ItemStackInfo stack = this.hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
            if (stack != null) {
                ImGui.beginTooltip();
                List<Component> tooltip = Screen.getTooltipFromItem(minecraft, stack.getItemStack());
                for (Component line : tooltip) {
                    component(line, ImGui.getFontSize() * 35.0f);
                }
                ImGui.endTooltip();
                return;
            }

            HoverEvent.EntityTooltipInfo entity = this.hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (entity != null) {
                if (minecraft.options.advancedItemTooltips) {
                    ImGui.beginTooltip();
                    List<Component> tooltip = entity.getTooltipLines();
                    for (Component line : tooltip) {
                        component(line, ImGui.getFontSize() * 35.0f);
                    }
                    ImGui.endTooltip();
                }
                return;
            }

            Component showText = this.hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
            if (showText != null) {
                ImGui.beginTooltip();
                component(showText, ImGui.getFontSize() * 35.0f);
                ImGui.endTooltip();
            }
        }
    }

    /**
     * Displays a resource location with a dimmed namespace
     *
     * @param loc The resource location
     */
    public static void resourceLocation(ResourceLocation loc) {
        ImGui.beginGroup();
        ImGui.textColored(colorOf(loc.getNamespace()), loc.getNamespace() + ":");

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        ImGui.sameLine();
        ImGui.text(loc.getPath());
        ImGui.popStyleVar();

        ImGui.endGroup();

        if (ImGui.beginPopupContextItem(loc.toString())) {
            if (ImGui.menuItem("Copy")) {
                ImGui.setClipboardText(loc.toString());
            }
            ImGui.endPopup();
        }
    }

    /**
     * Obtains the color of the modid
     *
     * @param modid The modid to get the color of
     * @return color The color based on the hash of the modid
     */
    public static int colorOf(String modid) {
        int color = (modid.hashCode() & 11184810) + 4473924;

        Color c = Color.of(0xff | (color << 8));
        c.mix(Color.WHITE.darkenCopy(0.4f), 0.35F);

        return c.getRGBA();
    }
}
