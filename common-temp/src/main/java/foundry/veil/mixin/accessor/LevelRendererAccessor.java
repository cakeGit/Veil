package foundry.veil.mixin.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostChain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {

    @Accessor
    @Nullable
    PostChain getEntityEffect();

    @Accessor
    @Nullable
    PostChain getTransparencyChain();
}
