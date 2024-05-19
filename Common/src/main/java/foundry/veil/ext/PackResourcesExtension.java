package foundry.veil.ext;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public interface PackResourcesExtension {

    void veil$listResources(PackType packType, PackResourceConsumer consumer) throws IOException;

    @FunctionalInterface
    interface PackResourceConsumer {

        void accept(ResourceLocation path, Path filePath, @Nullable Path modResourcePath);
    }

}
