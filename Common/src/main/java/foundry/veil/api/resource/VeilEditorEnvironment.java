package foundry.veil.api.resource;

import imgui.extension.texteditor.TextEditorLanguageDefinition;
import org.jetbrains.annotations.Nullable;

/**
 * An environment where files can be opened, edited, and managed.
 */
public interface VeilEditorEnvironment {

    void open(VeilResource<?> resource, @Nullable TextEditorLanguageDefinition languageDefinition);

    VeilResourceManager getResourceManager();
}
