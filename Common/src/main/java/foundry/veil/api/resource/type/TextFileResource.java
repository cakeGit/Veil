package foundry.veil.api.resource.type;

import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.VeilResourceInfo;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TextFileResource(VeilResourceInfo resourceInfo, Type type) implements VeilTextResource<TextFileResource> {

    @Override
    public List<VeilResourceAction<TextFileResource>> getActions() {
        return List.of(this.createTextEditAction());
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

    @Override
    public @Nullable TextEditorLanguageDefinition languageDefinition() {
        return null;
    }

    public enum Type {
        TEXT(".txt", 0xED0F),
        JSON(".json", 0xECCD);

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
