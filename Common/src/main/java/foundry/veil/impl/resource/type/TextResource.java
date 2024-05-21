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
        TEXT(".txt",0xED0F),
        JSON(".json",0xECCD);

        private final String extension;
        private final int icon;

        Type(String extension, int icon) {
            this.extension = extension;
            this.icon = icon;
        }

        public String getExtension() {
            return this.extension;
        }

        public int getIcon() {
            return this.icon;
        }
    }
}
