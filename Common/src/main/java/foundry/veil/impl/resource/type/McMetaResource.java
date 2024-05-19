package foundry.veil.impl.resource.type;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public record McMetaResource(ResourceLocation path, Path filePath, boolean modResource, ResourceMetadata metadata) implements VeilResource<McMetaResource> {

    @Override
    public boolean hidden() {
        return true;
    }

    @Override
    public Collection<VeilResourceAction<McMetaResource>> getActions() {
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
        return 0xECEA; // Info file icon
    }
}
