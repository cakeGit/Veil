package foundry.veil.impl.resource.type;

import foundry.veil.api.client.render.shader.ShaderManager;
import foundry.veil.api.resource.VeilResource;

public interface VeilShaderResource extends VeilResource<VeilShaderResource> {

    ShaderManager shaderManager();
}
