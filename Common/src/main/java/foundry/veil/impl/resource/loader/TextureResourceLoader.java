package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.impl.resource.type.TextureResource;
import foundry.veil.impl.resource.type.VeilShaderDefinitionResource;
import foundry.veil.impl.resource.type.VeilShaderFileResource;
import foundry.veil.impl.resource.type.VeilShaderResource;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Set;

public class TextureResourceLoader implements VeilResourceLoader<TextureResource> {

    private static final Set<String> EXTENSIONS = Set.of(
            ".png",
            ".jpg",
            ".jpeg"
    );

    @Override
    public boolean canLoad(ResourceLocation path, Path filePath, boolean modResource) {
        for (String extension : EXTENSIONS) {
            if (path.getPath().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public VeilResource<TextureResource> load(ResourceLocation path, Path filePath, boolean modResource) {
        return new TextureResource(path, filePath, modResource, false);
    }
}
