package foundry.veil.impl.resource.type;

import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.impl.resource.action.IngameEditAction;

import java.util.List;

public record TextResource(VeilResourceInfo resourceInfo, Type type) implements VeilResource<TextResource> {

    @Override
    public List<VeilResourceAction<TextResource>> getActions() {
        return List.of(new IngameEditAction<>(null));
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
        return this.type.getIcon();
    }

    public enum Type {
        TEXT(0xED0F),
        JSON(0xECCD);

        private final int icon;

        Type(int icon) {
            this.icon = icon;
        }

        public int getIcon() {
            return this.icon;
        }
    }
}
