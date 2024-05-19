package foundry.veil.api.resource;

import java.util.OptionalInt;

public interface VeilResourceAction<T extends VeilResource<?>> {

    /**
     * @return The name of the action
     */
    String getName();

    /**
     * @return A brief description of the action
     */
    String getDescription();

    /**
     * @return The icon to display for the action
     */
    OptionalInt getIcon();

    /**
     * Performs the action on the specified resource
     */
    void perform(VeilEditorEnvironment environment, T resource);

}
