package foundry.veil.impl.resource.type;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public record UnknownResource(ResourceLocation path, Path filePath, boolean modResource) implements VeilResource<UnknownResource> {

    @Override
    public boolean hidden() {
        return false;
    }

    @Override
    public Collection<VeilResourceAction<UnknownResource>> getActions() {
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
        return 0xED13; // Unknown file icon
    }
}