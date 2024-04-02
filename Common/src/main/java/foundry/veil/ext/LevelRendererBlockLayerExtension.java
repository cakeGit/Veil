package foundry.veil.ext;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

@ApiStatus.Internal
public interface LevelRendererBlockLayerExtension {

    void veil$drawBlockLayer(RenderType renderType, PoseStack poseStack, double x, double y, double z, Matrix4f projection);
}
