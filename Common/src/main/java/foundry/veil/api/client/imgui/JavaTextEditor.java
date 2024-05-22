package foundry.veil.api.client.imgui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class JavaTextEditor {

    private final List<String> lines = new ArrayList<>();
    private final List<Component> renderLines = new ArrayList<>();

    private final Selection selection = new Selection();
    private final Vector2i cursorPos = new Vector2i(-1);

    private final Vector2f textStartPos = new Vector2f(0);
    private final Vector2i getPos = new Vector2i(-1);

    private final float lineSpacing = 1.0F;
    private double lastClick = -1.0;

    public JavaTextEditor() {
        for (int i = 0; i < 50; i++) {
            this.lines.add("This is a testing string " + i);
            this.updateLine(i);
        }
    }

    private Component getRenderLine(String line) {
        return Component.literal(line).withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(new ItemStack(Items.DIAMOND_CHESTPLATE)))));
    }

    private void setLine(int line, String text) {
        this.lines.set(line, text);
        this.updateLine(line);
    }

    private void updateLine(int line) {
        Component text = this.getRenderLine(this.lines.get(line));
        while (line > this.renderLines.size()) {
            this.renderLines.add(Component.empty());
        }
        if (line == this.renderLines.size()) {
            this.renderLines.add(text);
        } else {
            this.renderLines.set(line, text);
        }
    }

    private void insertText(String text) {
        if (this.cursorPos.x == -1 || this.cursorPos.y == -1) {
            return;
        }

        int cursorLine = this.cursorPos.x;
        String[] parts = text.split("\n");

        for (int i = 0; i < parts.length; i++) {
            String insert = parts[i];
            if (i == 0) {
                String line = this.lines.get(cursorLine);
                int splitPos = Math.min(line.length(), this.cursorPos.y);
                this.setLine(cursorLine, line.substring(0, splitPos) + insert + line.substring(splitPos));
                this.cursorPos.y += insert.length();
                continue;
            }
        }
    }

    public void render() {
        ImFont font = ImGui.getFont();
        float xAdvance = font.getCharAdvance(' ');
        float yAdvance = ImGui.getTextLineHeightWithSpacing() * this.lineSpacing;

        float paddingX = ImGui.getStyle().getWindowPaddingX();
        float paddingY = ImGui.getStyle().getWindowPaddingY();

        ImGui.pushAllowKeyboardFocus(true);

        if (ImGui.beginChild("text", 0.0F, 0.0F, true, ImGuiWindowFlags.HorizontalScrollbar | ImGuiWindowFlags.NoMove)) {
            float sizeX = ImGui.getContentRegionAvailX();
            float sizeY = ImGui.getContentRegionAvailY();
            ImDrawList drawList = ImGui.getWindowDrawList();

            this.textStartPos.set(ImGui.getCursorScreenPosX(), ImGui.getCursorScreenPosY());
            float scrollX = ImGui.getScrollX();
            float scrollY = ImGui.getScrollY();
            int startLine = Math.max(0, Mth.floor((scrollY - paddingY) / yAdvance));
            int lineEnd = Math.min(this.renderLines.size(), Mth.ceil((scrollY + sizeY + paddingY) / yAdvance));

            if (startLine > 0) {
                ImGui.dummy(sizeX - paddingX * 2, startLine * yAdvance);
            }

            int selectionStartLine = this.selection.getStartLine();
            int selectionStartCol = this.selection.getStartColumn();
            int selectionEndLine = this.selection.getEndLine();
            int selectionEndCol = this.selection.getEndColumn();

            for (int i = startLine; i < lineEnd; i++) {
                Component line = this.renderLines.get(i);
                String string = line.getString();

                float lineX = this.textStartPos.x;
                float lineY = this.textStartPos.y + i * yAdvance;

                if (i >= selectionStartLine && i <= this.selection.getEndLine()) {
                    float startX = i == selectionStartLine
                            ? font.calcTextSizeAX(font.getFontSize(),
                            Float.MAX_VALUE,
                            0.0F,
                            string.substring(0, Mth.clamp(selectionStartCol, 0, string.length())))
                            : 0;
                    float endX = font.calcTextSizeAX(font.getFontSize(),
                            Float.MAX_VALUE,
                            0.0F,
                            i == selectionEndLine ? string.substring(0, Mth.clamp(selectionEndCol, 0, string.length())) : string);
                    if (i != selectionEndLine) {
                        endX += xAdvance + 2;
                    }
                    drawList.addRectFilled(lineX + startX, lineY, lineX + endX, lineY + yAdvance, VeilImGuiUtil.toImColor(0x802060a0));
                }

                if (i == this.cursorPos.x && (ImGui.getTime() - this.lastClick) / 0.4 % 2 < 1) {
                    float startX = font.calcTextSizeAX(font.getFontSize(),
                            Float.MAX_VALUE,
                            0.0F,
                            string.substring(0, Mth.clamp(this.cursorPos.y, 0, string.length())));

                    drawList.addRectFilled(lineX + startX, lineY, lineX + startX + 1, lineY + yAdvance, 0xFFFFFFFF);
                }

                VeilImGuiUtil.component(line);
            }

            ImGui.dummy(sizeX - paddingX * 2, (this.renderLines.size() - lineEnd) * yAdvance);

            // Keyboard
            if (ImGui.isWindowFocused()) {
                this.handleKeyboardInput();
                ImGuiIO io = ImGui.getIO();
                io.setWantCaptureKeyboard(true);
                io.setWantTextInput(true);
            }

            // Mouse
            if (ImGui.isWindowHovered()) {
                this.handleMouseInput();
                ImGui.setMouseCursor(ImGuiMouseCursor.TextInput);
            }
        }
        ImGui.endChild();

        ImGui.popAllowKeyboardFocus();
    }

    public @Nullable String getSelectionText() {
        if (!this.selection.hasSelection()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = this.selection.getStartLine(); i < Math.min(this.renderLines.size(), this.selection.getEndLine() + 1); i++) {
            String text = this.renderLines.get(i).getString();
            int start = i == this.selection.getStartLine() ? Math.min(this.selection.getStartColumn(), text.length()) : 0;
            int end = i == this.selection.getEndLine() ? Math.min(this.selection.getEndColumn(), text.length()) : text.length();
            builder.append(text, start, end).append('\n');
        }
        return builder.toString();
    }

    private void handleKeyboardInput() {
        ImGuiIO io = ImGui.getIO();
        if (io.getKeysDown(GLFW_KEY_A) && Screen.isSelectAll(GLFW_KEY_A)) {
            this.selection.setSelectionStart(0, 0);
            this.selection.setSelectionEnd(this.renderLines.size() + 1, this.renderLines.stream().mapToInt(component -> component.getString().length()).max().orElse(0));
        } else if (ImGui.isKeyPressed(GLFW_KEY_C) && Screen.isCopy(GLFW_KEY_C)) {
            String selectionText = this.getSelectionText();
            if (selectionText != null) {
                ImGui.setClipboardText(selectionText);
            }
        } else if (ImGui.isKeyPressed(GLFW_KEY_V) && Screen.isPaste(GLFW_KEY_V)) {
            this.insertText(ImGui.getClipboardText());
            this.selection.clear();
        } else if (ImGui.isKeyPressed(GLFW_KEY_X) && Screen.isCut(GLFW_KEY_X)) {
            String selectionText = this.getSelectionText();
            if (selectionText != null) {
                ImGui.setClipboardText(selectionText);
            }
            // TODO
        } else if (ImGui.isKeyPressed(GLFW_KEY_ENTER)) {
            if (this.cursorPos.x != -1 && this.cursorPos.y != -1) {
                String line = this.lines.get(this.cursorPos.x);
                this.setLine(this.cursorPos.x, line.substring(0, this.cursorPos.y));

                this.lines.add(this.cursorPos.x + 1, line.substring(Math.min(this.cursorPos.y, line.length())));
                this.renderLines.add(this.cursorPos.x + 1, this.getRenderLine(this.lines.get(this.cursorPos.x + 1)));

                this.cursorPos.x++;
                this.cursorPos.y = 0;
            }
        } else if (this.cursorPos.x != -1 && this.cursorPos.y != -1) {
            if (ImGui.isKeyPressed(GLFW_KEY_TAB)) {
                String line = this.lines.get(this.cursorPos.x);
                this.setLine(this.cursorPos.x, line.substring(0, this.cursorPos.y) + '\t' + Math.min(this.cursorPos.y, line.length()));
                this.cursorPos.y++;
            } else if (ImGui.isKeyPressed(GLFW_KEY_BACKSPACE)) {
                String line = this.lines.get(this.cursorPos.x);
                this.setLine(this.cursorPos.x, line.substring(0, this.cursorPos.y) + '\t' + Math.min(this.cursorPos.y, line.length()));
                this.cursorPos.y--;
            }
        }

        if (this.cursorPos.x != -1 && this.cursorPos.y != -1 && VeilImGuiUtil.hasTypedChar()) {
            if (this.selection.hasSelection()) {
                int endLine = this.selection.getEndLine();
                int startLine = this.selection.getStartLine();
                int line = startLine;
                
                for (int i = startLine; i < Math.min(this.lines.size(), endLine + 1); i++) {
                    if (i == startLine) {
                        int startColumn = this.selection.getStartColumn();
                        if (startColumn != 0) {
                            this.setLine(i, this.lines.get(i).substring(startColumn));
                            continue;
                        }
                    }

                    this.lines.remove(i);
                    this.renderLines.remove(i);
                    i--;
                    endLine--;
                }
            }
            this.selection.clear();

            StringBuilder line = new StringBuilder(this.lines.get(this.cursorPos.x));
            VeilImGuiUtil.forEachTypedChar(codePoint -> {
                if (Character.isBmpCodePoint(codePoint)) {
                    line.insert(this.cursorPos.y, (char) codePoint);
                    this.cursorPos.y++;
                } else {
                    char[] chars = Character.toChars(codePoint);
                    for (int i = 0; i < chars.length; i++) {
                        line.insert(this.cursorPos.y + i, chars[i]);
                    }
                    this.cursorPos.y += chars.length;
                }
            });
            this.setLine(this.cursorPos.x, line.toString());
        }
    }

    private void handleMouseInput() {
        ImGuiIO io = ImGui.getIO();
        float mouseX = io.getMousePosX();
        float mouseY = io.getMousePosY();

        if (Screen.hasShiftDown()) {
            if (ImGui.isAnyMouseDown()) {
                Vector2i textPos = this.getTextPos(mouseX, mouseY);
                if (textPos != null) {
                    this.cursorPos.set(textPos.x, textPos.y);
                }

                this.selection.setSelectionEnd(this.cursorPos.x, this.cursorPos.y);
                this.selection.setBoxSelect(false);
            }
            return;
        }

        boolean click = ImGui.isMouseClicked(0);
        boolean doubleClick = ImGui.isMouseDoubleClicked(0);
        boolean tripleClick = click && !doubleClick && this.lastClick >= 0 && ImGui.getTime() - this.lastClick < io.getMouseDoubleClickTime();

//        boolean ctrl = Minecraft.ON_OSX ? io.getKeySuper() : io.getKeyCtrl();
        if (tripleClick) {
            this.lastClick = -1.0;
        } else if (doubleClick) {
            this.lastClick = ImGui.getTime();
        } else if (click || ImGui.isMouseClicked(2)) {
            this.lastClick = ImGui.getTime();

            Vector2i textPos = this.getTextPos(mouseX, mouseY);
            if (textPos != null) {
                this.cursorPos.set(textPos);
                this.selection.clear();
                this.selection.setSelectionStart(this.cursorPos.x, this.cursorPos.y);
            }
        } else {
            boolean boxSelect = ImGui.isMouseDragging(2) && ImGui.isMouseDown(2);
            if ((ImGui.isMouseDragging(0) && ImGui.isMouseDown(0)) || boxSelect) {
                Vector2i textPos = this.getTextPos(mouseX, mouseY);
                if (textPos != null) {
                    this.cursorPos.set(textPos.x, textPos.y);
                }

                this.selection.setSelectionEnd(this.cursorPos.x, this.cursorPos.y);
                this.selection.setBoxSelect(boxSelect);
            }
        }
    }

    public Selection getSelection() {
        return this.selection;
    }

    private @Nullable Vector2i getTextPos(float x, float y) {
        ImFont font = ImGui.getFont();
        float yAdvance = ImGui.getTextLineHeightWithSpacing() * this.lineSpacing;
        int clickedLine = Mth.floor((y - this.textStartPos.y) / yAdvance);

        if (clickedLine >= 0 && clickedLine < this.renderLines.size()) {
            String text = this.renderLines.get(clickedLine).getString();

            int clickedColumn = 0;
            float i = 0;
            for (int codePoint : text.codePoints().toArray()) {
                float charAdvance = font.getCharAdvance(codePoint);
                float minX = i;
                float maxX = i + charAdvance;

                if (x >= this.textStartPos.x + minX - charAdvance / 2 && x < this.textStartPos.x + maxX - charAdvance / 2) {
                    break;
                }

                clickedColumn++;
                i = maxX;
            }

            return this.getPos.set(clickedLine, clickedColumn);
        }

        this.getPos.set(-1);
        return null;
    }

    public static class Selection {

        private int startLine;
        private int startColumn;
        private int endLine;
        private int endColumn;
        private boolean reversed;
        private boolean boxSelect;

        public Selection() {
            this.clear();
        }

        public void clear() {
            this.startLine = -1;
            this.startColumn = -1;
            this.endLine = -1;
            this.endColumn = -1;
            this.reversed = false;
            this.boxSelect = false;
        }

        public void setSelectionStart(int line, int column) {
            this.startLine = line;
            this.startColumn = column;
        }

        public void setSelectionEnd(int line, int column) {
            this.endLine = line;
            this.endColumn = column;
            this.reversed = this.endLine == this.startLine ? this.endColumn < this.startColumn : this.endLine < this.startLine;
        }

        public void setBoxSelect(boolean boxSelect) {
            this.boxSelect = boxSelect;
        }

        public boolean hasSelection() {
            return this.startLine != -1 && this.endLine != -1;
        }

        public int getStartLine() {
            return this.reversed ? this.endLine : this.startLine;
        }

        public int getStartColumn() {
            return this.reversed ? this.endColumn : this.startColumn;
        }

        public int getEndLine() {
            return this.reversed ? this.startLine : this.endLine;
        }

        public int getEndColumn() {
            return this.reversed ? this.startColumn : this.endColumn;
        }

        public boolean isBoxSelect() {
            return this.boxSelect;
        }
    }
}
