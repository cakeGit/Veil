package foundry.veil.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;


public class VeilUITooltipRenderer {
    public static final VeilIGuiOverlay OVERLAY = VeilUITooltipRenderer::renderOverlay;

    public static int hoverTicks = 0;
    public static BlockPos lastHoveredPos = null;

    public static void renderOverlay(Gui gui, PoseStack stack, float partialTicks, int width, int height){
        Minecraft mc = Minecraft.getInstance();
        if(mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;
        HitResult result = mc.hitResult;
        if(!(result instanceof BlockHitResult)){
            hoverTicks = 0;
            lastHoveredPos = null;
            return;
        }
        BlockHitResult blockHitResult = (BlockHitResult) result;
        ClientLevel world = mc.level;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(!(blockEntity instanceof Tooltippable)){
            hoverTicks = 0;
            lastHoveredPos = null;
            return;
        }
        Tooltippable tooltippable = (Tooltippable) blockEntity;
        int prevHoverTicks = hoverTicks;
        hoverTicks++;
        lastHoveredPos = pos;
        boolean shouldShowTooltip = VeilUITooltipHandler.shouldShowTooltip();
        List<Component> tooltip = tooltippable.getTooltip();
        if(tooltip.isEmpty()){
            hoverTicks = 0;
            return;
        }
        stack.pushPose();
        int tooltipTextWidth = 0;
        for(FormattedText line : tooltip){
            int textLineWidth = mc.font.width(line);
            if(textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }
        int tooltipHeight = 8;
        if(tooltip.size() > 1)
            tooltipHeight += 2 + (tooltip.size() - 1) * 10;
        int tooltipX = (width / 2) + 20;
        int tooltipY = (height / 2);

        tooltipX = Math.min(tooltipX, width - tooltipTextWidth - 20);
        tooltipY = Math.min(tooltipY, height - tooltipHeight - 20);

        float fade = Mth.clamp((hoverTicks + partialTicks) / 24f, 0, 1);
        Color background = Color.VANILLA_TOOLTIP_BACKGROUND.multiply(1,1,1,0.75f);
        Color borderTop = Color.VANILLA_TOOLTIP_BORDER_TOP;
        Color borderBottom = Color.VANILLA_TOOLTIP_BORDER_BOTTOM;

        if(fade < 1){
            stack.translate(Math.pow(fade, 2) * 20, 0, 0);
            background = background.multiply(1,1,1,fade);
            borderTop = borderTop.multiply(1,1,1,fade);
            borderBottom = borderBottom.multiply(1,1,1,fade);
        }

        UIUtils.drawHoverText(ItemStack.EMPTY, stack, tooltip, tooltipX, tooltipY, width, height, -1, background.getRGB(), borderTop.getRGB(), borderBottom.getRGB(), mc.font);
    }
}
