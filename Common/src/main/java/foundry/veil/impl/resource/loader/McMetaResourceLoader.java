package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.McMetaResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class McMetaResourceLoader implements VeilResourceLoader<McMetaResource> {

    @Override
    public boolean canLoad(ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) {
        return path.getPath().endsWith(".mcmeta");
    }

    @Override
    public VeilResource<McMetaResource> load(VeilResourceManager resourceManager, ResourceProvider provider, PackType packType, ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) throws IOException {
        Optional<Resource> optional = provider.getResource(path.withPath(s -> s.substring(0, s.length() - 7)));
        if (optional.isPresent()) {
            return new McMetaResource(new VeilResourceInfo(packType, path, filePath, modResourcePath, true), optional.get().metadata());
        }

        try (InputStream stream = provider.open(path)) {
            return new McMetaResource(new VeilResourceInfo(packType, path, filePath, modResourcePath, false), ResourceMetadata.fromJsonStream(stream));
        }
    }
}
