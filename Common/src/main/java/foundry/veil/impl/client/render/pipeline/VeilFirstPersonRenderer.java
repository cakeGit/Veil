package foundry.veil.impl.client.render.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

@ApiStatus.Internal
public final class VeilFirstPersonRenderer {

    // TODO add options

    private static final ResourceLocation FIRST_PERSON = Veil.veilPath("first_person");

    private static boolean printedError;
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
        VeilRenderSystem.renderer().getFramebufferManager().setFramebuffer(VeilFramebuffers.FIRST_PERSON, firstPerson);
        firstPerson.bind(false);
    }

    public static void unbind() {
        VeilRenderer renderer = VeilRenderSystem.renderer();
        PostProcessingManager postProcessingManager = renderer.getPostProcessingManager();

        PostPipeline pipeline = postProcessingManager.getPipeline(FIRST_PERSON);
        if (pipeline == null) {
            if (!printedError) {
                Veil.LOGGER.error("Failed to apply first person pipeline");
                printedError = true;
            }
            AdvancedFbo.unbind();
            return;
        }

        AdvancedFbo postFramebuffer = renderer.getFramebufferManager().getFramebuffer(VeilFramebuffers.POST);
        if (postFramebuffer != null) {
            AdvancedFbo.getMainFramebuffer().resolveToAdvancedFbo(postFramebuffer);
        }

        postProcessingManager.runPipeline(pipeline);

        if (postFramebuffer != null) {
            postFramebuffer.resolveToFramebuffer(Minecraft.getInstance().getMainRenderTarget());
        }
    }

    public static void free() {
        if (firstPerson != null) {
            VeilRenderSystem.renderer().getFramebufferManager().removeFramebuffer(VeilFramebuffers.FIRST_PERSON);
            firstPerson.free();
            firstPerson = null;
        }
        printedError = false;
    }
}
