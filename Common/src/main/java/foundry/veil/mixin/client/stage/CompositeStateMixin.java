package foundry.veil.mixin.client.stage;

import com.google.common.collect.ImmutableList;
import foundry.veil.ext.CompositeStateExtension;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(RenderType.CompositeState.class)
public class CompositeStateMixin implements CompositeStateExtension {

    @Mutable
    @Final
    @Shadow
    ImmutableList<RenderStateShard> states;

    @Override
    public void veil$addShards(Collection<RenderStateShard> shards) {
        if (shards.isEmpty()) {
            return;
        }

        ImmutableList.Builder<RenderStateShard> builder = new ImmutableList.Builder<>();
        builder.addAll(this.states);
        builder.addAll(shards);
        this.states = builder.build();
    }
}
