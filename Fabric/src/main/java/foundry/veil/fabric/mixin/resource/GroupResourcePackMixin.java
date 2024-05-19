package foundry.veil.fabric.mixin.resource;

import foundry.veil.ext.PackResourcesExtension;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(GroupResourcePack.class)
public class GroupResourcePackMixin implements PackResourcesExtension {

    @Shadow
    @Final
    protected List<? extends PackResources> packs;

    @Override
    public void veil$listResources(PackType packType, PackResourceConsumer consumer) throws IOException {
        for (PackResources pack : this.packs) {
            if (pack instanceof PackResourcesExtension extension) {
                extension.veil$listResources(packType, consumer);
            }
        }
    }
}
