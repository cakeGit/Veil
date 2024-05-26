package foundry.veil.impl.resource.loader;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.VeilShaderIncludeResource;
import foundry.veil.impl.resource.type.VeilShaderResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class ShaderIncludeLoader implements VeilResourceLoader<VeilShaderResource> {

    @Override
    public boolean canLoad(PackType packType, ResourceLocation location, Path filePath, @Nullable Path modResourcePath) {
        if (packType != PackType.CLIENT_RESOURCES) {
            return false;
        }

        String path = location.getPath();
        return path.startsWith("pinwheel/shaders/include") && path.endsWith(".glsl");
    }

    @Override
    public VeilResource<VeilShaderResource> load(VeilResourceManager resourceManager, ResourceProvider provider, PackType packType, ResourceLocation location, @Nullable Path filePath, @Nullable Path modResourcePath) throws IOException {
        return new VeilShaderIncludeResource(new VeilResourceInfo(packType, location, filePath, modResourcePath, false));
    }
}
