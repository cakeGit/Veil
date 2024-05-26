package foundry.veil.impl.resource.action;

import foundry.veil.api.resource.VeilEditorEnvironment;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import net.minecraft.resources.ResourceLocation;

import java.util.OptionalInt;

public record IngameEditAction<T extends VeilResource<?>>(ResourceLocation editor) implements VeilResourceAction<T> {

    @Override
    public String getName() {
        return "Open in Veil Editor";
    }

    @Override
    public String getDescription() {
        return "Opens the in-game editor";
    }

    @Override
    public OptionalInt getIcon() {
        return OptionalInt.of(0xECDB); // Edit file line icon
    }

    @Override
    public void perform(VeilEditorEnvironment environment, T resource) {
        environment.open(resource, this.editor);
    }
}
