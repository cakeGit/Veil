package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.impl.resource.type.VeilShaderDefinitionResource;
import foundry.veil.impl.resource.type.VeilShaderFileResource;
import foundry.veil.impl.resource.type.VeilShaderResource;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Set;

public class ShaderResourceLoader implements VeilResourceLoader<VeilShaderResource> {

    private static final Set<String> EXTENSIONS = Set.of(
            ".json",
            ".glsl",
            ".vsh",
            ".tcsh",
            ".tesh",
            ".gsh",
            ".fsh",
            ".comp"
    );

    @Override
    public boolean canLoad(ResourceLocation path, Path filePath, boolean modResource) {
        if (!path.getPath().startsWith("pinwheel/shaders")) {
            return false;
        }

        for (String extension : EXTENSIONS) {
            if (path.getPath().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public VeilResource<VeilShaderResource> load(ResourceLocation path, Path filePath, boolean modResource) {
        return path.getPath().endsWith(".json") ? new VeilShaderDefinitionResource(path, filePath, modResource) : new VeilShaderFileResource(path, filePath, modResource, !path.getPath().startsWith("pinwheel/shaders/include"));
    }
}
