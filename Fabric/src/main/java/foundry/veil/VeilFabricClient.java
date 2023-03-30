package foundry.veil;

import foundry.veil.postprocessing.PostProcessingHandler;
import foundry.veil.ui.VeilUITooltipRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class VeilFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VeilClient.init();
        WorldRenderEvents.LAST.register(last -> {
            PostProcessingHandler.onLevelRenderLast(last.matrixStack());
        });
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            VeilUITooltipRenderer.OVERLAY.render(Minecraft.getInstance().gui, matrices, tickDelta, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
        });
    }
}
