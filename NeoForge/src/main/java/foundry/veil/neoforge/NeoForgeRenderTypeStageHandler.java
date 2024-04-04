package foundry.veil.neoforge;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import foundry.veil.Veil;
import foundry.veil.ext.LevelRendererBlockLayerExtension;
import foundry.veil.mixin.client.deferred.BufferSourceAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Veil.MODID, value = Dist.CLIENT)
public class NeoForgeRenderTypeStageHandler {

    private static final Map<RenderLevelStageEvent.Stage, Set<RenderType>> STAGE_RENDER_TYPES = new HashMap<>();
    private static Set<RenderType> CUSTOM_BLOCK_LAYERS;
    private static List<RenderType> BLOCK_LAYERS;

    public static synchronized void register(@Nullable RenderLevelStageEvent.Stage stage, RenderType renderType) {
        Map<RenderType, BufferBuilder> fixedBuffers = ((BufferSourceAccessor) Minecraft.getInstance().renderBuffers().bufferSource()).getFixedBuffers();
        fixedBuffers.computeIfAbsent(renderType, type -> new BufferBuilder(type.bufferSize()));

        if (stage != null) {
            STAGE_RENDER_TYPES.computeIfAbsent(stage, unused -> new HashSet<>()).add(renderType);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStageEnd(RenderLevelStageEvent event) {
        Set<RenderType> stages = STAGE_RENDER_TYPES.get(event.getStage());
        if (stages != null) {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            stages.forEach(renderType -> {
                if (CUSTOM_BLOCK_LAYERS.contains(renderType)) {
                    Vec3 pos = event.getCamera().getPosition();
                    ((LevelRendererBlockLayerExtension) event.getLevelRenderer()).veil$drawBlockLayer(renderType, event.getPoseStack(), pos.x, pos.y, pos.z, event.getProjectionMatrix());
                }
                bufferSource.endBatch(renderType);
            });
        }
    }

    public static List<RenderType> getBlockLayers() {
        return BLOCK_LAYERS;
    }

    public static void setBlockLayers(ImmutableList.Builder<RenderType> blockLayers) {
        CUSTOM_BLOCK_LAYERS = new HashSet<>(blockLayers.build());
        blockLayers.addAll(RenderType.chunkBufferLayers());
        BLOCK_LAYERS = blockLayers.build();
    }
}