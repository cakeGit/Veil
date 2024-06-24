package foundry.veil.api.client.util;

import com.mojang.blaze3d.platform.GlStateManager;

import java.util.Map;

// TODO finish
public record TransparencyState(GlStateManager.SourceFactor srcColorFactor, GlStateManager.DestFactor dstColorFactor,
                                GlStateManager.SourceFactor srcAlphaFactor, GlStateManager.DestFactor dstAlphaFactor) {

    private static final Map<String, TransparencyState> DEFAULT_STATES = Map.of(
            "ADDITIVE_TRANSPARENCY", new TransparencyState(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE),
            "LIGHTNING_TRANSPARENCY", new TransparencyState(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE),
            "GLINT_TRANSPARENCY", new TransparencyState(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE),
            "CRUMBLING_TRANSPARENCY", new TransparencyState(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO),
            "TRANSLUCENT_TRANSPARENCY", new TransparencyState(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
    );
//    private static final Codec<TransparencyState> FULL_CODEC = RecordCodecBuilder.create(instance->instance.group(
//
//    ));

    public TransparencyState(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor) {
        this(srcFactor, dstFactor, srcFactor, dstFactor);
    }
}
