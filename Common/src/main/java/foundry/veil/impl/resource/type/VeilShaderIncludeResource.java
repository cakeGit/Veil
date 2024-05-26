package foundry.veil.impl.resource.type;

import foundry.veil.api.client.imgui.VeilLanguageDefinitions;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.CompiledShader;
import foundry.veil.api.client.render.shader.ShaderManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.impl.resource.action.IngameEditAction;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record VeilShaderIncludeResource(VeilResourceInfo resourceInfo) implements VeilShaderResource {

    @Override
    public List<VeilResourceAction<VeilShaderResource>> getActions() {
        return List.of(new IngameEditAction<>(VeilLanguageDefinitions.glsl()));
    }

    @Override
    public boolean canHotReload() {
        return true;
    }

    @Override
    public void hotReload() {
        ResourceLocation id = ShaderManager.INCLUDE_LISTER.fileToId(this.resourceInfo.path());

        ShaderManager shaderManager = VeilRenderSystem.renderer().getShaderManager();
        ShaderManager deferredShaderManager = VeilRenderSystem.renderer().getDeferredRenderer().getDeferredShaderManager();
        Set<ResourceLocation> programs = getShaders(id, shaderManager);
        Set<ResourceLocation> deferredPrograms = getShaders(id, deferredShaderManager);

        for (ResourceLocation program : programs) {
            shaderManager.scheduleRecompile(program);
        }

        for (ResourceLocation program : deferredPrograms) {
            deferredShaderManager.scheduleRecompile(program);
        }
    }

    private static Set<ResourceLocation> getShaders(ResourceLocation id, ShaderManager shaderManager) {
        Set<ResourceLocation> programs = new HashSet<>();
        for (Map.Entry<ResourceLocation, ShaderProgram> entry : shaderManager.getShaders().entrySet()) {
            ShaderProgram program = entry.getValue();
            for (CompiledShader shader : program.getShaders().values()) {
                if (shader.includes().contains(id)) {
                    programs.add(entry.getKey());
                    break;
                }
            }
        }
        return programs;
    }

    @Override
    public int getIconCode() {
        return 0xECD1; // Code file icon
    }
}
