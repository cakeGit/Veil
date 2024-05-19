package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.impl.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.TextureResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

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
    public VeilResource<TextureResource> load(VeilResourceManager resourceManager, ResourceProvider provider, ResourceLocation path, Path filePath, boolean modResource) {
//        VeilResource<?> metaFile = resourceManager.getVeilResource(path.getNamespace(), path.getPath() + ".mcmeta");
//        AnimationMetadataSection animation = null;
//        if (metaFile != null) {
//            ResourceMetadata metadata = ((McMetaResource) metaFile).metadata();
//            animation = metadata.getSection(AnimationMetadataSection.SERIALIZER).orElse(null);
//        }
        return new TextureResource(path, filePath, modResource, false);
    }
}
