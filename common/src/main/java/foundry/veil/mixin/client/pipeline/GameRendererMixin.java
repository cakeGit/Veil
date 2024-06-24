package foundry.veil.mixin.client.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.ext.GameRendererExtension;
import foundry.veil.impl.client.render.pipeline.VeilFirstPersonRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements GameRendererExtension {

    @Unique
    private long veil$frameStartNanos;

    @Inject(method = "resize", at = @At(value = "HEAD"))
    public void veil$resizeListener(int pWidth, int pHeight, CallbackInfo ci) {
        VeilRenderSystem.resize(pWidth, pHeight);
        VeilFirstPersonRenderer.free(); // The old texture is deleted, so we have to remake the framebuffer
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void veil$captureFrameStart(float partialTicks, long time, boolean renderLevel, CallbackInfo ci) {
        this.veil$frameStartNanos = time;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = At.Shift.AFTER))
    public void veil$renderPost(float partialTicks, long time, boolean renderLevel, CallbackInfo ci) {
        VeilRenderSystem.renderPost();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V", shift = At.Shift.AFTER))
    public void veil$updateGuiCamera(float partialTicks, long time, boolean renderLevel, CallbackInfo ci) {
        VeilRenderer renderer = VeilRenderSystem.renderer();
        renderer.getCameraMatrices().updateGui();
        renderer.getGuiInfo().update();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void veil$unbindGuiCamera(float partialTicks, long time, boolean renderLevel, CallbackInfo ci) {
        VeilRenderSystem.renderer().getGuiInfo().unbind();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", shift = At.Shift.BEFORE))
    public void veil$bindFirstPerson(float partialTicks, long time, PoseStack poseStack, CallbackInfo ci) {
        VeilFirstPersonRenderer.bind();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V", shift = At.Shift.AFTER))
    public void veil$unbindFirstPerson(float partialTicks, long time, PoseStack poseStack, CallbackInfo ci) {
        VeilFirstPersonRenderer.unbind();
    }

    @Inject(method = "close", at = @At("TAIL"))
    public void veil$free(CallbackInfo ci) {
        VeilFirstPersonRenderer.free();
    }

    @Override
    public long veil$getFrameStartNanos() {
        return this.veil$frameStartNanos;
    }
}