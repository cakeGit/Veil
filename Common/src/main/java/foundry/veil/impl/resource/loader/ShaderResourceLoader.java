package foundry.veil.impl.resource.loader;

import foundry.veil.api.client.render.shader.ShaderManager;
import foundry.veil.api.client.render.shader.ShaderSourceSet;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceLoader;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.impl.resource.type.McMetaResource;
import foundry.veil.impl.resource.type.VeilShaderDefinitionResource;
import foundry.veil.impl.resource.type.VeilShaderFileResource;
import foundry.veil.impl.resource.type.VeilShaderResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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

    private final ShaderManager shaderManager;

    public ShaderResourceLoader(ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }

    @Override
    public boolean canLoad(ResourceLocation path, Path filePath, @Nullable Path modResourcePath) {
        if (!path.getPath().startsWith(this.shaderManager.getSourceSet().getFolder())) {
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
    public VeilResource<VeilShaderResource> load(VeilResourceManager resourceManager, ResourceProvider provider, PackType packType, ResourceLocation path, @Nullable Path filePath, @Nullable Path modResourcePath) throws IOException {
        VeilResourceInfo info = new VeilResourceInfo(packType, path, filePath, modResourcePath, false);
        return path.getPath().endsWith(".json") ? new VeilShaderDefinitionResource(info, this.shaderManager) : new VeilShaderFileResource(info, this.shaderManager);
    }
}
