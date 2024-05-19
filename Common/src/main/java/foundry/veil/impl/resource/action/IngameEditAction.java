package foundry.veil.impl.resource.action;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;

import java.util.OptionalInt;

public class IngameEditAction<T extends VeilResource<?>> implements VeilResourceAction<T> {

    @Override
    public String getName() {
        return "Open in Veil Editor";
    }

    @Override
    public String getDescription() {
        return "Opens the resource in the in-game text editor";
    }

    @Override
    public OptionalInt getIcon() {
        return OptionalInt.of(0xECDB); // Edit file line icon
    }

    @Override
    public void perform(T resource) {
        System.out.println("TODO pass to editor");
    }
}
