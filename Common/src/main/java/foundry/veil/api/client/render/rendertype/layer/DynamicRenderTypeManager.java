package foundry.veil.api.client.render.rendertype.layer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import foundry.veil.Veil;
import foundry.veil.api.CodecReloadListener;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DynamicRenderTypeManager extends CodecReloadListener<List<CompositeRenderTypeData>> {

    private static final Codec<List<CompositeRenderTypeData>> LAYER_CODEC = Codec.either(CompositeRenderTypeData.CODEC.listOf(), CompositeRenderTypeData.CODEC)
            .flatXmap(either -> DataResult.success(either.map(layers -> layers, Collections::singletonList)),
                    layers -> {
                        if (layers.isEmpty()) {
                            return DataResult.error(() -> "At least 1 layer must be specified");
                        }
                        if (layers.size() == 1) {
                            return DataResult.success(Either.right(layers.get(0)));
                        }
                        return DataResult.success(Either.left(layers));
                    });

    public DynamicRenderTypeManager() {
        super(LAYER_CODEC, FileToIdConverter.json("pinwheel/rendertypes"));
    }

    @Override
    protected void apply(Map<ResourceLocation, List<CompositeRenderTypeData>> values, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Veil.LOGGER.info("Loaded {} render types", values.size());
    }
}
