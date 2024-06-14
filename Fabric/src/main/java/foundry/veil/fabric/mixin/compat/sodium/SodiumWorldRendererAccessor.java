package foundry.veil.fabric.mixin.compat.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(SodiumWorldRenderer.class)
public interface SodiumWorldRendererAccessor {

    @Accessor(remap = false)
    RenderSectionManager getRenderSectionManager();
}
