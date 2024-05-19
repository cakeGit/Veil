package foundry.veil.impl.resource.type;

import foundry.veil.api.resource.VeilResourceAction;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public record VeilShaderFileResource(ResourceLocation path, Path filePath, boolean modResource, boolean hidden) implements VeilShaderResource {

    @Override
    public Collection<VeilResourceAction<VeilShaderResource>> getActions() {
        return List.of();
    }

    @Override
    public boolean canHotReload() {
        return false;
    }

    @Override
    public void hotReload() {
    }

    @Override
    public int getIconCode() {
        return 0xED0F; // Text file icon
    }
}
