package foundry.veil.impl.client.imgui;

import imgui.glfw.ImGuiImplGlfw;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class VeilImGuiImplGlfw extends ImGuiImplGlfw {

    private final IntList typedCharacters;

    public VeilImGuiImplGlfw() {
        this.typedCharacters = new IntArrayList();
    }

    @Override
    public void charCallback(long windowId, int c) {
        super.charCallback(windowId, c);

        this.typedCharacters.add(c);
    }

    public IntList getTypedCharacters() {
        return this.typedCharacters;
    }
}
