package foundry.veil.api.resource;

import foundry.veil.impl.resource.type.McMetaResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jetbrains.annotations.Nullable;

public interface VeilResourceManager {

    @Nullable
    VeilResource<?> getVeilResource(String namespace, String path);

    default @Nullable VeilResource<?> getVeilResource(ResourceLocation location) {
        return this.getVeilResource(location.getNamespace(), location.getPath());
    }

    default @Nullable ResourceMetadata getResourceMetadata(ResourceLocation location) {
        return this.getResourceMetadata(location.getNamespace(), location.getPath());
    }

    default @Nullable ResourceMetadata getResourceMetadata(String namespace, String path) {
        VeilResource<?> resource = this.getVeilResource(namespace, path);
        return resource instanceof McMetaResource mcMetaResource ? mcMetaResource.metadata() : null;
    }
}
