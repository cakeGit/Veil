package foundry.veil.impl.client.imgui;

import foundry.veil.api.client.imgui.VeilImGui;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class InactiveVeilImGuiImpl implements VeilImGui {

    private static final IntList TYPED_CHARACTERS = IntList.of();

    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }

    @Override
    public void onGrabMouse() {
    }

    @Override
    public void toggle() {
    }

    @Override
    public void updateFonts() {
    }

    @Override
    public boolean mouseButtonCallback(long window, int button, int action, int mods) {
        return false;
    }

    @Override
    public boolean scrollCallback(long window, double xOffset, double yOffset) {
        return false;
    }

    @Override
    public boolean keyCallback(long window, int key, int scancode, int action, int mods) {
        return false;
    }

    @Override
    public boolean charCallback(long window, int codepoint) {
        return false;
    }

    @Override
    public boolean shouldHideMouse() {
        return false;
    }

    @Override
    public IntList getTypedCharacters() {
        return TYPED_CHARACTERS;
    }

    @Override
    public void free() {
    }
}
