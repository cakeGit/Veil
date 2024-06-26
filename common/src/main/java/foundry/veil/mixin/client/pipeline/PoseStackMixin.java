package foundry.veil.mixin.client.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.render.MatrixStack;
import org.joml.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Deque;

@Mixin(PoseStack.class)
public abstract class PoseStackMixin implements MatrixStack {

    @Unique
    private final Quaternionf veil$castQuat = new Quaternionf();
    @Unique
    private final Matrix4f veil$castTransform = new Matrix4f();
    @Unique
    private final Matrix3f veil$castNormal = new Matrix3f();
    @Unique
    private static final Matrix3f veil$IDENTITY_NORMAL = new Matrix3f();

    @Shadow
    public abstract void shadow$translate(float x, float y, float z);

    @Shadow
    public abstract void shadow$scale(float x, float y, float z);

    @Shadow
    public abstract void shadow$mulPose(Quaternionf quaternion);

    @Shadow
    public abstract void shadow$rotateAround(Quaternionf quaternion, float x, float y, float z);

    @Shadow
    public abstract void shadow$pushPose();

    @Shadow
    public abstract void shadow$popPose();

    @Shadow
    public abstract boolean shadow$clear();

    @Shadow
    public abstract void shadow$setIdentity();

    @Shadow
    public abstract void shadow$mulPoseMatrix(Matrix4f matrix);

    @Shadow
    public abstract PoseStack.Pose shadow$last();

    @Shadow
    @Final
    private Deque<PoseStack.Pose> poseStack;

    @Override
    public void clear() {
        while (this.poseStack.size() > 1) {
            this.shadow$popPose();
        }
    }

    @Override
    public void translate(float x, float y, float z) {
        this.shadow$translate(x, y, z);
    }

    @Override
    public void rotate(Quaterniondc rotation) {
        this.shadow$mulPose(this.veil$castQuat.set(rotation));
    }

    @Override
    public void rotate(Quaternionfc rotation) {
        this.shadow$mulPose(this.veil$castQuat.set(rotation));
    }

    @Override
    public void rotate(float angle, float x, float y, float z) {
        this.shadow$mulPose(this.veil$castQuat.identity().rotateAxis(angle, x, y, z));
    }

    @Override
    public void rotateXYZ(float x, float y, float z) {
        this.shadow$mulPose(this.veil$castQuat.identity().rotateXYZ(x, y, z));
    }

    @Override
    public void rotateZYX(float z, float y, float x) {
        this.shadow$mulPose(this.veil$castQuat.identity().rotateZYX(z, y, x));
    }

    @Override
    public void rotateAround(Quaterniondc rotation, double x, double y, double z) {
        this.shadow$rotateAround(this.veil$castQuat.set(rotation), (float) x, (float) y, (float) z);
    }

    @Override
    public void rotateAround(Quaternionfc rotation, float x, float y, float z) {
        this.shadow$rotateAround(this.veil$castQuat.set(rotation), x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        this.shadow$scale(x, y, z);
    }

    @Override
    public void setIdentity() {
        this.shadow$setIdentity();
    }

    @Override
    public boolean isIdentity() {
        PoseStack.Pose pose = this.pose();
        return (pose.pose().properties() & Matrix4fc.PROPERTY_IDENTITY) != 0 && pose.normal().equals(veil$IDENTITY_NORMAL);
    }

    @Override
    public boolean isEmpty() {
        return this.shadow$clear();
    }

    @Override
    public void pushMatrix() {
        this.shadow$pushPose();
    }

    @Override
    public void popMatrix() {
        this.shadow$popPose();
    }

    @Override
    public PoseStack.Pose pose() {
        return this.shadow$last();
    }
}
