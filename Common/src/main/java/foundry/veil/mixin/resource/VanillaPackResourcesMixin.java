package foundry.veil.mixin.resource;

import foundry.veil.ext.PackResourcesExtension;
import foundry.veil.impl.resource.VeilFileVisitor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(VanillaPackResources.class)
public class VanillaPackResourcesMixin implements PackResourcesExtension {

    @Shadow
    @Final
    private List<Path> rootPaths;

    @Override
    public void veil$listResources(PackType packType, PackResourceConsumer consumer) throws IOException {
        for (Path rootPath : this.rootPaths) {
            Files.walkFileTree(rootPath, new VeilFileVisitor(rootPath, consumer, true));
        }
    }
}
