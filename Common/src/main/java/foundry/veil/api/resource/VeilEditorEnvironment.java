package foundry.veil.api.resource;

import net.minecraft.resources.ResourceLocation;

/**
 * An environment where files can be opened, edited, and managed.
 */
public interface VeilEditorEnvironment {

    void open(VeilResource<?> resource, ResourceLocation editor);

    VeilResourceManager getResourceManager();
}
