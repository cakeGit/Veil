package foundry.veil.api.client.render.rendertype.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import foundry.veil.api.client.registry.RenderTypeLayerRegistry;
import net.minecraft.client.renderer.RenderStateShard;

import java.util.Arrays;

public record MultiTextureLayer(TextureLayer[] textures) implements RenderTypeLayer {

    public static final Codec<MultiTextureLayer> CODEC = TextureLayer.CODEC.listOf()
            .flatXmap(textures -> textures.isEmpty() ? DataResult.error(() -> "At least 1 texture must be specified") : DataResult.success(new MultiTextureLayer(textures.toArray(TextureLayer[]::new))),
                    layer -> DataResult.success(Arrays.asList(layer.textures))).fieldOf("textures").codec();

    @Override
    public void addLayer(RenderTypeBuilder builder) {
        if (this.textures.length == 1) {
            this.textures[0].addLayer(builder);
        } else {
            RenderStateShard.MultiTextureStateShard.Builder textureBuilder = RenderStateShard.MultiTextureStateShard.builder();
            for (TextureLayer texture : this.textures) {
                textureBuilder.add(texture.texture(), texture.blur(), texture.mipmap());
            }
            builder.setTextureState(textureBuilder.build());
        }
    }

    @Override
    public RenderTypeLayerRegistry.LayerType<?> getType() {
        return RenderTypeLayerRegistry.MULTI_TEXTURE.get();
    }
}
