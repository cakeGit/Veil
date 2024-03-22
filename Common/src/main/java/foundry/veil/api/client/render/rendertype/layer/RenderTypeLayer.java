package foundry.veil.api.client.render.rendertype.layer;

import com.mojang.serialization.Codec;
import foundry.veil.api.client.registry.RenderTypeLayerRegistry;
import net.minecraft.client.renderer.RenderStateShard;

public interface RenderTypeLayer {

    Codec<RenderTypeLayer> CODEC = RenderTypeLayerRegistry.REGISTRY.byNameCodec().dispatch(RenderTypeLayer::getType, RenderTypeLayerRegistry.LayerType::codec);

    void addLayer(RenderTypeBuilder builder);

    RenderTypeLayerRegistry.LayerType<?> getType();

    interface RenderTypeBuilder {

        RenderTypeBuilder setTextureState(RenderStateShard.EmptyTextureStateShard textureState);

        RenderTypeBuilder setShaderState(RenderStateShard.ShaderStateShard shaderState);

        RenderTypeBuilder setTransparencyState(RenderStateShard.TransparencyStateShard transparencyState);

        RenderTypeBuilder setDepthTestState(RenderStateShard.DepthTestStateShard depthTestState);

        RenderTypeBuilder setCullState(RenderStateShard.CullStateShard cullState);

        RenderTypeBuilder setLightmapState(RenderStateShard.LightmapStateShard lightmapState);

        RenderTypeBuilder setOverlayState(RenderStateShard.OverlayStateShard overlayState);

        RenderTypeBuilder setLayeringState(RenderStateShard.LayeringStateShard layeringState);

        RenderTypeBuilder setOutputState(RenderStateShard.OutputStateShard outputState);

        RenderTypeBuilder setTexturingState(RenderStateShard.TexturingStateShard texturingState);

        RenderTypeBuilder setWriteMaskState(RenderStateShard.WriteMaskStateShard writeMaskState);

        RenderTypeBuilder setLineState(RenderStateShard.LineStateShard lineState);

        RenderTypeBuilder setColorLogicState(RenderStateShard.ColorLogicStateShard colorLogicState);

        RenderTypeBuilder addLayer(RenderStateShard shard);
    }
}
