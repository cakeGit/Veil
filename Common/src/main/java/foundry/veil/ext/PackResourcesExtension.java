package foundry.veil.ext;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public interface PackResourcesExtension {

    void veil$listResources(PackType packType, BiConsumer<ResourceLocation, Path> consumer) throws IOException;

}
