package foundry.veil.mixin.accessor;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NativeImage.class)
public interface NativeImageAccessor {

    @Invoker
    void invokeCheckAllocated();

    @Accessor
    long getPixels();
}
