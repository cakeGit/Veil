package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.impl.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.McMetaResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class McMetaResourceLoader implements VeilResourceLoader<McMetaResource> {

    @Override
    public boolean canLoad(ResourceLocation path, @Nullable Path filePath, boolean modResource) {
        return path.getPath().endsWith(".mcmeta");
    }

    @Override
    public VeilResource<McMetaResource> load(VeilResourceManager resourceManager, ResourceProvider provider, ResourceLocation path, @Nullable Path filePath, boolean modResource) throws IOException {
        return new McMetaResource(path, filePath, modResource, provider.getResourceOrThrow(path.withPath(s -> s.substring(0, s.length() - 7))).metadata());
    }
}
