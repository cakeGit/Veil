package foundry.veil.api.client.necromancer;

import net.minecraft.util.Mth;
import org.joml.*;

import javax.annotation.Nullable;
import java.util.List;

public class Bone {
    String name;

    @Nullable
    Bone parent;
    List<Bone> children;

    Vector3f position;
    Vector3f previousPosition;

    Quaternionf rotation;
    Quaternionf previousRotation;

    float red, green, blue, alpha;
    float previousRed, previousGreen, previousBlue, previousAlpha;

    private Matrix4x3f composedTransform = new Matrix4x3f();

    public Matrix4x3fc getTransform(float partialTime) {
        return this.composedTransform.translationRotate(
                // translation
                Mth.lerp(partialTime, this.previousPosition.x, this.position.x),
                Mth.lerp(partialTime, this.previousPosition.y, this.position.y),
                Mth.lerp(partialTime, this.previousPosition.z, this.position.z),
                // rotation
                Mth.lerp(partialTime, this.previousRotation.x, this.rotation.x),
                Mth.lerp(partialTime, this.previousRotation.y, this.rotation.y),
                Mth.lerp(partialTime, this.previousRotation.z, this.rotation.z),
                Mth.lerp(partialTime, this.previousRotation.w, this.rotation.w)
        );
    }

    public void updatePrevious() {
        this.previousPosition.set(this.position);
        this.previousRotation.set(this.rotation);

        this.previousRed = this.red;
        this.previousGreen = this.green;
        this.previousBlue = this.blue;
        this.previousAlpha = this.alpha;
    }
}
