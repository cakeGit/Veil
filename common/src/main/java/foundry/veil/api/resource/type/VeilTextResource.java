package foundry.veil.api.resource.type;

import foundry.veil.api.resource.VeilResource;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import org.jetbrains.annotations.Nullable;

public interface VeilTextResource<T extends VeilTextResource<?>> extends VeilResource<T> {

    @Nullable
    TextEditorLanguageDefinition languageDefinition();
}
