package foundry.veil.impl.client.render.pipeline;

import net.minecraft.client.renderer.RenderStateShard;

import static org.lwjgl.opengl.GL40C.GL_PATCH_VERTICES;
import static org.lwjgl.opengl.GL40C.glPatchParameteri;

/**
 * Sets the patch vertices
 */
public class PatchState extends RenderStateShard {

    public PatchState(int patchVertices) {
        super("patch", () -> glPatchParameteri(GL_PATCH_VERTICES, patchVertices), () -> glPatchParameteri(GL_PATCH_VERTICES, 1));
    }
}
