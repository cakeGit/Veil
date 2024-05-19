package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.UnknownResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class UnknownResourceLoader implements VeilResourceLoader<UnknownResource> {

    public static final UnknownResourceLoader INSTANCE = new UnknownResourceLoader();

    @Override
    public boolean canLoad(ResourceLocation path, Path filePath, @Nullable Path modResourcePath) {
        return false;
    }

    @Override
    public VeilResource<UnknownResource> load(VeilResourceManager resourceManager, ResourceProvider provider, ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) throws IOException {
        return new UnknownResource(new VeilResourceInfo(path, filePath, modResourcePath, false));
    }
}
