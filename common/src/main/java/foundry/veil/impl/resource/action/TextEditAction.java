package foundry.veil.impl.resource.action;

import foundry.veil.api.client.registry.VeilResourceEditorRegistry;
import foundry.veil.api.resource.VeilEditorEnvironment;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import net.minecraft.network.chat.Component;

import java.util.OptionalInt;

public record TextEditAction<T extends VeilResource<?>>() implements VeilResourceAction<T> {

    private static final Component NAME = Component.translatable("editor.veil.resource.action.text_edit");
    private static final Component DESC = Component.translatable("editor.veil.resource.action.text_edit.desc");

    @Override
    public Component getName() {
        return NAME;
    }

    @Override
    public Component getDescription() {
        return DESC;
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
