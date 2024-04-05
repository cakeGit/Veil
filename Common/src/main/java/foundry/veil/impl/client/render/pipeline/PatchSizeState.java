package foundry.veil.impl.client.render.pipeline;

import net.minecraft.client.renderer.RenderStateShard;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL40C;

import static org.lwjgl.opengl.GL40C.glPatchParameteri;

@ApiStatus.Internal
public class PatchSizeState extends RenderStateShard {

    public PatchSizeState(int patchVertices) {
        super("patch", () -> glPatchParameteri(GL40C.GL_PATCH_VERTICES, patchVertices), () -> {
        });
    }
}
