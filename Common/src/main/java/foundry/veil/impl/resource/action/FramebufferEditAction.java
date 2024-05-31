package foundry.veil.impl.resource.action;

import foundry.veil.api.client.registry.VeilResourceEditorRegistry;
import foundry.veil.api.resource.VeilEditorEnvironment;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.type.FramebufferResource;

import java.util.OptionalInt;

public class FramebufferEditAction implements VeilResourceAction<FramebufferResource> {

    @Override
    public String getName() {
        return "Open in Framebuffer Visual Editor";
    }

    @Override
    public String getDescription() {
        return "Opens the in-game framebuffer editor";
    }

    @Override
    public OptionalInt getIcon() {
        return OptionalInt.empty();
    }

    @Override
    public void perform(VeilEditorEnvironment environment, FramebufferResource resource) {
        environment.open(resource, VeilResourceEditorRegistry.FRAMEBUFFER.getId());
    }
}
