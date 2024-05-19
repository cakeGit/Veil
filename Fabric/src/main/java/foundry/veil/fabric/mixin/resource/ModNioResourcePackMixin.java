package foundry.veil.fabric.mixin.resource;

import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.impl.resource.VeilFileVisitor;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(ModNioResourcePack.class)
public class ModNioResourcePackMixin implements PackResourcesExtension {

    @Shadow
    @Final
    private List<Path> basePaths;

    @Shadow
    @Final
    private ModMetadata modInfo;

    @Override
    public void veil$listResources(PackType packType, PackResourceConsumer consumer) throws IOException {
        String id = this.modInfo.getId();
        if (!"fabric-api".equalsIgnoreCase(id) && id.startsWith("fabric") && this.modInfo.containsCustomValue("fabric-api:module-lifecycle")) {
            // Skip fabric apis
            return;
        }

        for (Path rootPath : this.basePaths) {
            Files.walkFileTree(rootPath, new VeilFileVisitor(rootPath, consumer, true));
        }
    }
}
