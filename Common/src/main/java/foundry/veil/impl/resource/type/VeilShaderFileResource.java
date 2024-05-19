package foundry.veil.impl.resource.type;

import foundry.veil.api.client.imgui.VeilLanguageDefinitions;
import foundry.veil.api.client.render.shader.ShaderManager;
import foundry.veil.api.client.render.shader.ShaderSourceSet;
import foundry.veil.api.client.render.shader.program.ProgramDefinition;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.impl.resource.action.IngameEditAction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record VeilShaderFileResource(VeilResourceInfo resourceInfo, ShaderManager shaderManager) implements VeilShaderResource {

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
        ShaderSourceSet sourceSet = this.shaderManager.getSourceSet();
        Set<ResourceLocation> programs = new HashSet<>();
        for (Map.Entry<ResourceLocation, ShaderProgram> entry : this.shaderManager.getShaders().entrySet()) {
            ProgramDefinition definition = entry.getValue().getDefinition();
            if (definition == null) {
                continue;
            }

            for (Int2ObjectMap.Entry<ResourceLocation> shaderEntry : definition.shaders().int2ObjectEntrySet()) {
                ResourceLocation sourceName = shaderEntry.getValue();
                if (sourceName == null) {
                    continue;
                }

                if (sourceSet.getTypeConverter(shaderEntry.getIntKey()).fileToId(this.resourceInfo.path()).equals(sourceName)) {
                    programs.add(entry.getKey());
                    break;
                }
            }
        }

        // It's better to copy the set and add them all here so we don't make the other threads wait
        for (ResourceLocation program : programs) {
            this.shaderManager.scheduleRecompile(program);
        }
    }

    @Override
    public int getIconCode() {
        return 0xECD1; // Code file icon
    }
}
