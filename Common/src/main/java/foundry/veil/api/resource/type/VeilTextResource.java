package foundry.veil.api.resource.type;

import foundry.veil.api.client.registry.VeilResourceEditorRegistry;
import foundry.veil.api.resource.VeilResource;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.impl.resource.action.IngameEditAction;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import org.jetbrains.annotations.Nullable;

public interface VeilTextResource<T extends VeilTextResource<?>> extends VeilResource<T> {

    @Nullable
    TextEditorLanguageDefinition languageDefinition();

    default VeilResourceAction<T> createTextEditAction() {
        return new IngameEditAction<>(VeilResourceEditorRegistry.TEXT.getId());
    }
}
