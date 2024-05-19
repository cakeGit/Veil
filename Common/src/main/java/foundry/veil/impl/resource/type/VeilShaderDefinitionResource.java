package foundry.veil.impl.resource.type;

import foundry.veil.api.client.render.shader.ShaderManager;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.impl.resource.action.IngameEditAction;

import java.util.List;

public record VeilShaderDefinitionResource(VeilResourceInfo resourceInfo, ShaderManager shaderManager) implements VeilShaderResource {

    @Override
    public List<VeilResourceAction<VeilShaderResource>> getActions() {
        return List.of(new IngameEditAction<>(null));
    }

    @Override
    public boolean canHotReload() {
        return true;
    }

    @Override
    public void hotReload() {
        this.shaderManager.scheduleRecompile(this.shaderManager.getSourceSet().getShaderDefinitionLister().fileToId(this.resourceInfo.path()));
    }

    @Override
    public int getIconCode() {
        return 0xED0F; // Text file icon
    }
}
