package foundry.veil.api.client.render.rendertype.layer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import foundry.veil.Veil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DynamicRenderTypeManager extends SimplePreparableReloadListener<Map<ResourceLocation, byte[]>> {

    private static final FileToIdConverter CONVERTER = FileToIdConverter.json("pinwheel/rendertypes");

    @Override
    protected @NotNull Map<ResourceLocation, byte[]> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<ResourceLocation, byte[]> data = new HashMap<>();

        Map<ResourceLocation, Resource> resources = CONVERTER.listMatchingResources(resourceManager);
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            ResourceLocation id = CONVERTER.fileToId(location);

            try (InputStream stream = entry.getValue().open()) {
                data.put(id, stream.readAllBytes());
            } catch (Exception e) {
                Veil.LOGGER.error("Couldn't read data file {} from {}", id, location, e);
            }
        }

        return data;
    }

    @Override
    protected void apply(Map<ResourceLocation, byte[]> fileData, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, RenderType> renderTypes = new HashMap<>();
        int injections = 0;

        for (Map.Entry<ResourceLocation, byte[]> entry : fileData.entrySet()) {
            ResourceLocation id = entry.getKey();

            try (Reader reader = new InputStreamReader(new ByteArrayInputStream(entry.getValue()))) {
                JsonElement element = JsonParser.parseReader(reader);
                DataResult<CompositeRenderTypeData> result = CompositeRenderTypeData.CODEC.parse(JsonOps.INSTANCE, element);

                if (result.error().isPresent()) {
                    throw new JsonSyntaxException(result.error().get().message());
                }

                CompositeRenderTypeData data = result.result().orElseThrow();
                if (data.inject() != null) {
                    // TODO inject
                    injections++;
                    continue;
                }

                if (renderTypes.put(id, data.createRenderType(id.toString())) != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                }
            } catch (Exception e) {
                Veil.LOGGER.error("Couldn't parse data file {} from {}", id, CONVERTER.idToFile(id), e);
            }
        }
        Veil.LOGGER.info("Loaded {} render types", renderTypes.size() + injections);
    }
}
