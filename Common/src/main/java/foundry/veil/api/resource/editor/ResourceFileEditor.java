package foundry.veil.api.resource.editor;

import foundry.veil.api.resource.VeilEditorEnvironment;
import foundry.veil.api.resource.VeilResource;

public interface ResourceFileEditor<T extends VeilResource<?>> {

    /**
     * Renders this editor to the screen.
     */
    void render();

    /**
     * Opens the specified resource in the environment.
     *
     * @param environment The environment to open the resource in
     * @param resource    The resource to open
     */
    void open(VeilEditorEnvironment environment, T resource);

}
