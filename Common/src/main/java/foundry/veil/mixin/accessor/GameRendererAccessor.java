package foundry.veil.mixin.accessor;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor
    Map<String, ShaderInstance> getShaders();

    @Accessor
    void setRenderDistance(float renderDistance);
}
