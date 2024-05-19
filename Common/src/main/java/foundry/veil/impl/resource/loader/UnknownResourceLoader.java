package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.impl.resource.type.UnknownResource;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public class UnknownResourceLoader implements VeilResourceLoader<UnknownResource> {
    public static final UnknownResourceLoader INSTANCE = new UnknownResourceLoader();

    @Override
    public boolean canLoad(ResourceLocation path, Path filePath) {
        return false;
    }

    @Override
    public VeilResource<UnknownResource> load(ResourceLocation path, Path filePath) {
        return new UnknownResource(path, filePath);
    }
}
