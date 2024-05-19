package foundry.veil.mixin.resource;

import foundry.veil.Veil;
import foundry.veil.ext.PackResourcesExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Mixin(VanillaPackResources.class)
public class VanillaPackResourcesMixin implements PackResourcesExtension {

    @Shadow
    @Final
    private List<Path> rootPaths;

    @Override
    public void veil$listResources(PackType packType, BiConsumer<ResourceLocation, Path> consumer) {
        for (Path rootPath : this.rootPaths) {
            try (Stream<Path> stream = Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)) {
                stream.forEach(path -> {
                    // the path will be something like /assets/minecraft/textures/block/stone.png
                    // we want a resource location that has a namespace "minecraft" and a path "textures/block/stone.png"
                    List<String> dirs = new ArrayList<>(5);

                    Path traversal = path;
                    while (traversal != null) {
                        Path fileName = traversal.getFileName();

                        if (fileName == null) {
                            break;
                        }

                        dirs.add(0, fileName.toString());
                        traversal = traversal.getParent();
                    }

                    if (dirs.size() > 1 && dirs.get(0).equals("assets")) {
                        String namespace = dirs.get(1);
                        String pathStr = String.join("/", dirs.subList(2, dirs.size()));

                        if (Files.isRegularFile(path)) {
                            consumer.accept(new ResourceLocation(namespace, pathStr), path);
                        }
                    }
                });
            } catch (IOException e) {
                Veil.LOGGER.error("Error loading vanilla pack resources", e);
            }
        }
    }
}
