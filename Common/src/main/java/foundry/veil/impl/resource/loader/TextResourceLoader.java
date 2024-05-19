package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.TextResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class TextResourceLoader implements VeilResourceLoader<TextResource> {

    @Override
    public boolean canLoad(ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) {
        return path.getPath().endsWith(".txt");
    }

    @Override
    public VeilResource<TextResource> load(VeilResourceManager resourceManager, ResourceProvider provider, ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) throws IOException {
        return new TextResource(new VeilResourceInfo(path, filePath, modResourcePath, false), TextResource.Type.TEXT);
    }
}
