package foundry.veil.api.client.necromancer;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.graveyard.skeleton.InterpolatedSkeleton;
import foundry.veil.api.client.graveyard.skeleton.InterpolatedSkeletonParent;
import net.minecraft.client.renderer.MultiBufferSource;

// todo:
// figure out a schema for constraint application that's, like, not entirely order dependent.
// probably some integration solver or something idk.
public interface Constraint {
    void apply();

    default void renderDebugInfo(Skeleton skeleton, float partialTime, PoseStack poseStack, MultiBufferSource pBuffer) {}
}
