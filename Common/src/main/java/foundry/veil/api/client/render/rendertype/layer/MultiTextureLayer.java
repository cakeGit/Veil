package foundry.veil.api.client.render.rendertype.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import foundry.veil.api.client.registry.RenderTypeLayerRegistry;
import foundry.veil.api.client.render.rendertype.VeilRenderTypeBuilder;
import net.minecraft.client.renderer.RenderStateShard;

import java.util.Arrays;

public record MultiTextureLayer(TextureLayer[] textures) implements RenderTypeLayer {

    public static final Codec<MultiTextureLayer> CODEC = TextureLayer.CODEC.listOf()
            .flatXmap(textures -> textures.size() < 2 ? DataResult.error(() -> "At least 2 textures must be specified") : DataResult.success(new MultiTextureLayer(textures.toArray(TextureLayer[]::new))),
                    layer -> DataResult.success(Arrays.asList(layer.textures))).fieldOf("textures").codec();

    @Override
    public void addLayer(VeilRenderTypeBuilder builder) {
        RenderStateShard.MultiTextureStateShard.Builder textureBuilder = RenderStateShard.MultiTextureStateShard.builder();
        for (TextureLayer texture : this.textures) {
            textureBuilder.add(texture.texture(), texture.blur(), texture.mipmap());
        }
        builder.textureState(textureBuilder.build());
    }

    @Override
    public RenderTypeLayerRegistry.LayerType<?> getType() {
        return RenderTypeLayerRegistry.MULTI_TEXTURE.get();
    }
}
