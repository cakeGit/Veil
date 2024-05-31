package foundry.veil.mixin.client.pipeline;

import com.mojang.blaze3d.pipeline.RenderTarget;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.ext.RenderTargetExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTarget.class)
public class RenderTargetMixin implements RenderTargetExtension {

    @Unique
    private AdvancedFbo veil$wrapper;

    @Override
    public void veil$setWrapper(@Nullable AdvancedFbo fbo) {
        this.veil$wrapper = fbo;
    }

    @Inject(method = "bindRead", at = @At("HEAD"), cancellable = true)
    public void bindRead(CallbackInfo ci) {
        if (this.veil$wrapper != null) {
            if (this.veil$wrapper.isColorTextureAttachment(0)) {
                this.veil$wrapper.getColorTextureAttachment(0).bind();
            }
            ci.cancel();
        }
    }

    @Inject(method = "bindWrite", at = @At("HEAD"), cancellable = true)
    public void bindWrite(CallbackInfo ci) {
        if (this.veil$wrapper != null) {
            this.veil$wrapper.bind(true);
            ci.cancel();
        }
    }
}
