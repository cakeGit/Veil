package foundry.veil.mixin.client.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.render.CameraMatrices;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.ext.LevelRendererExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements LevelRendererExtension {

    @Shadow
    private Frustum cullingFrustum;

    @Shadow
    @Nullable
    private Frustum capturedFrustum;

    @Shadow
    protected abstract void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_254039_);

    @Unique
    private final Vector3f veil$tempCameraPos = new Vector3f();

    @Inject(method = "prepareCullFrustum", at = @At("HEAD"))
    public void veil$setupLevelCamera(PoseStack modelViewStack, Vec3 pos, Matrix4f projection, CallbackInfo ci) {
        CameraMatrices matrices = VeilRenderSystem.renderer().getCameraMatrices();
        matrices.update(RenderSystem.getProjectionMatrix(), modelViewStack.last().pose(), this.veil$tempCameraPos.set(pos.x(), pos.y(), pos.z()), 0.05F, Minecraft.getInstance().gameRenderer.getDepthFar());
    }

    @Override
    public CullFrustum veil$getCullFrustum() {
        return VeilRenderBridge.create(this.capturedFrustum != null ? this.capturedFrustum : this.cullingFrustum);
    }

    @Override
    public void veil$drawBlockLayer(RenderType renderType, PoseStack poseStack, double x, double y, double z, Matrix4f projection) {
        this.renderChunkLayer(renderType, poseStack, x, y, z, projection);
    }
}