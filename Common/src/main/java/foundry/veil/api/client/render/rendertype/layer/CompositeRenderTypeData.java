package foundry.veil.api.client.render.rendertype.layer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import foundry.veil.api.client.util.VertexFormatCodec;
import foundry.veil.ext.CompositeStateExtension;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public record CompositeRenderTypeData(@Nullable String inject, int priority, boolean replace, VertexFormat format,
                                      VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sort,
                                      boolean outline, RenderTypeLayer[] layers) {

    public static final Codec<CompositeRenderTypeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("inject").forGetter(state -> Optional.ofNullable(state.inject())),
            Codec.INT.optionalFieldOf("priority", 1000).forGetter(CompositeRenderTypeData::priority),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(CompositeRenderTypeData::replace),
            VertexFormatCodec.CODEC.fieldOf("format").forGetter(CompositeRenderTypeData::format),
            VertexFormatCodec.MODE_CODEC.fieldOf("mode").forGetter(CompositeRenderTypeData::mode),
            VertexFormatCodec.BUFFER_SIZE_CODEC.fieldOf("bufferSize").forGetter(CompositeRenderTypeData::bufferSize),
            Codec.BOOL.optionalFieldOf("affectsCrumbling", false).forGetter(CompositeRenderTypeData::sort),
            Codec.BOOL.optionalFieldOf("sort", false).forGetter(CompositeRenderTypeData::sort),
            Codec.BOOL.optionalFieldOf("outline", false).forGetter(CompositeRenderTypeData::outline),
            RenderTypeLayer.CODEC.listOf().fieldOf("layers").forGetter(state -> Arrays.asList(state.layers()))
    ).apply(instance, (inject, priority, replace, format, mode, bufferSize, affectsCrumbling, sort, outline, layers) -> new CompositeRenderTypeData(inject.orElse(null), priority, replace, format, mode, bufferSize, affectsCrumbling, sort, outline, layers.toArray(new RenderTypeLayer[0]))));

    public RenderType createRenderType(String name) {
        if (this.inject != null) {
            throw new IllegalStateException("Only new render types can be created");
        }

        Builder builder = new Builder();
        for (RenderTypeLayer layer : this.layers) {
            layer.addLayer(builder);
        }

        return builder.create(name, this.format, this.mode, this.bufferSize, this.affectsCrumbling, this.sort, this.outline);
    }

    private static class Builder extends RenderType implements RenderTypeLayer.RenderTypeBuilder {

        private final CompositeState.CompositeStateBuilder builder;
        private final List<RenderStateShard> layers;

        @SuppressWarnings("DataFlowIssue")
        public Builder() {
            super(null, null, null, 0, false, false, null, null);
            this.builder = CompositeState.builder();
            this.layers = new LinkedList<>();
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setTextureState(EmptyTextureStateShard textureState) {
            this.builder.setTextureState(textureState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setShaderState(ShaderStateShard shaderState) {
            this.builder.setShaderState(shaderState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setTransparencyState(TransparencyStateShard transparencyState) {
            this.builder.setTransparencyState(transparencyState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setDepthTestState(DepthTestStateShard depthTestState) {
            this.builder.setDepthTestState(depthTestState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setCullState(CullStateShard cullState) {
            this.builder.setCullState(cullState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setLightmapState(LightmapStateShard lightmapState) {
            this.builder.setLightmapState(lightmapState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setOverlayState(OverlayStateShard overlayState) {
            this.builder.setOverlayState(overlayState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setLayeringState(LayeringStateShard layeringState) {
            this.builder.setLayeringState(layeringState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setOutputState(OutputStateShard outputState) {
            this.builder.setOutputState(outputState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setTexturingState(TexturingStateShard texturingState) {
            this.builder.setTexturingState(texturingState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setWriteMaskState(WriteMaskStateShard writeMaskState) {
            this.builder.setWriteMaskState(writeMaskState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setLineState(LineStateShard lineState) {
            this.builder.setLineState(lineState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder setColorLogicState(ColorLogicStateShard colorLogicState) {
            this.builder.setColorLogicState(colorLogicState);
            return this;
        }

        @Override
        public RenderTypeLayer.RenderTypeBuilder addLayer(RenderStateShard shard) {
            this.layers.add(shard);
            return this;
        }

        public RenderType create(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sort, boolean outline) {
            CompositeState state = this.builder.createCompositeState(outline);
            ((CompositeStateExtension) (Object) state).veil$addShards(this.layers);
            return create(name, format, mode, bufferSize, affectsCrumbling, sort, state);
        }
    }
}
