package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.TextResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class TextResourceLoader implements VeilResourceLoader<TextResource> {

    @Override
    public boolean canLoad(PackType packType, ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) {
        for (TextResource.Type type : TextResource.Type.values()) {
            if (path.getPath().endsWith(type.getExtension())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public VeilResource<TextResource> load(VeilResourceManager resourceManager, ResourceProvider provider, PackType packType, ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) throws IOException {
        for (TextResource.Type type : TextResource.Type.values()) {
            if (path.getPath().endsWith(type.getExtension())) {
                return new TextResource(new VeilResourceInfo(packType, path, filePath, modResourcePath, false), type);
            }
        }
        throw new IOException("Unknown text resource: " + path);
    }
}
