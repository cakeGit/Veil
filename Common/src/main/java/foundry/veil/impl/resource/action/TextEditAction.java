package foundry.veil.impl.resource.action;

import foundry.veil.api.client.registry.VeilResourceEditorRegistry;
import foundry.veil.api.resource.VeilEditorEnvironment;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;

import java.util.OptionalInt;

public record TextEditAction<T extends VeilResource<?>>() implements VeilResourceAction<T> {

    @Override
    public String getName() {
        return "Open in Veil Text Editor";
    }

    @Override
    public String getDescription() {
        return "Opens the in-game text editor";
    }

    @Override
    public OptionalInt getIcon() {
        return OptionalInt.of(0xECDB); // Edit file line icon
    }

    @Override
    public void perform(VeilEditorEnvironment environment, T resource) {
        environment.open(resource, VeilResourceEditorRegistry.TEXT.getId());
    }
}
