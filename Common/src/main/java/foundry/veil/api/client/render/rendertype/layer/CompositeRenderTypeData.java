package foundry.veil.api.client.render.rendertype.layer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.rendertype.VeilRenderTypeBuilder;
import foundry.veil.api.client.util.VertexFormatCodec;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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

    @ApiStatus.Internal
    public RenderType createRenderType(String name) {
        if (this.inject != null) {
            throw new IllegalStateException("Only new render types can be created");
        }

        VeilRenderTypeBuilder builder = VeilRenderBridge.create(RenderType.CompositeState.builder());
        for (RenderTypeLayer layer : this.layers) {
            layer.addLayer(builder);
        }

        RenderType.CompositeState state = builder.create(this.outline);
        return RenderType.create(name, this.format, this.mode, this.bufferSize, this.affectsCrumbling, this.sort, state);
    }
}
