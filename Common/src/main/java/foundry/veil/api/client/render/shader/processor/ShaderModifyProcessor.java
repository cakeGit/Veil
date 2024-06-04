package foundry.veil.api.client.render.shader.processor;

import foundry.veil.api.client.render.shader.ShaderManager;
import foundry.veil.api.client.render.shader.ShaderModificationManager;
import foundry.veil.impl.client.render.shader.modifier.ShaderModification;
import foundry.veil.impl.client.render.shader.transformer.VeilJobParameters;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Modifies shader sources with the shader modification system.
 *
 * @author Ocelot
 */
public class ShaderModifyProcessor implements ShaderPreProcessor {

    private final ShaderModificationManager shaderModificationManager;
    private final Set<ResourceLocation> appliedModifications;

    public ShaderModifyProcessor(ShaderModificationManager shaderModificationManager) {
        this.shaderModificationManager = shaderModificationManager;
        this.appliedModifications = new HashSet<>();
    }

    @Override
    public void prepare() {
        this.appliedModifications.clear();
    }

    @Override
    public String modify(Context context) throws IOException {
        ResourceLocation name = context.name();
        if (name == null || !this.appliedModifications.add(name)) {
            return context.sourceCode();
        }
        int flags = context.isSourceFile() ? VeilJobParameters.APPLY_VERSION | VeilJobParameters.ALLOW_OUT : 0;
        FileToIdConverter converter = context.isSourceFile() ? context.idConverter() : ShaderManager.INCLUDE_LISTER;
        return context.modify(context.name(), this.shaderModificationManager.applyModifiers(converter.idToFile(name), context.sourceCode(), flags));
    }
}
