package foundry.veil.impl.client.render.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.AdvancedFboTextureWrapperAttachment;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

import static org.lwjgl.opengl.GL11C.GL_DEPTH;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14C.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL30C.GL_COLOR_ATTACHMENT0;

@ApiStatus.Internal
public final class VeilFirstPersonRenderer {

    // TODO add options

    private static AdvancedFbo firstPerson;

    private VeilFirstPersonRenderer() {
    }

    public static void bind() {
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        int w = mainRenderTarget.width;
        int h = mainRenderTarget.height;
        if (firstPerson == null || firstPerson.getWidth() != w || firstPerson.getHeight() != h) {
            free();
            firstPerson = AdvancedFbo.withSize(w, h)
                    .addColorTextureWrapper(mainRenderTarget.getColorTextureId())
                    .setFormat(FramebufferAttachmentDefinition.Format.DEPTH_COMPONENT)
                    .setDepthTextureBuffer()
                    .build(true);
        }
        firstPerson.bind(false);
    }

    public static void unbind() {
        AdvancedFbo.unbind();
    }

    public static void free() {
        if (firstPerson != null) {
            firstPerson.free();
            firstPerson = null;
        }
    }
}
