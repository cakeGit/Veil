package foundry.veil.mixin.client.shader;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.impl.client.render.shader.VanillaShaderImportProcessor;
import foundry.veil.impl.client.render.shader.modifier.ShaderModification;
import foundry.veil.impl.client.render.shader.transformer.VeilJobParameters;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.List;

@Mixin(Program.class)
public class ProgramMixin {

    @Unique
    private static ResourceLocation veil$captureId;

    @Inject(method = "compileShaderInternal", at = @At("HEAD"))
    private static void veil$captureId(Program.Type type, String name, InputStream stream, String pack, GlslPreprocessor glslPreprocessor, CallbackInfoReturnable<Integer> cir) {
        ResourceLocation loc = new ResourceLocation(name);
        String s = "shaders/core/" + loc.getPath() + type.getExtension();
        veil$captureId = new ResourceLocation(loc.getNamespace(), s);
    }

    @Inject(method = "compileShaderInternal", at = @At("RETURN"))
    private static void veil$clear(Program.Type type, String name, InputStream stream, String pack, GlslPreprocessor glslPreprocessor, CallbackInfoReturnable<Integer> cir) {
        veil$captureId = null;
    }

    @ModifyArg(method = "compileShaderInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;glShaderSource(ILjava/util/List;)V"), index = 1)
    private static List<String> veil$modifyVanillaShader(List<String> sourceLines) {
        try {
            StringBuilder source = new StringBuilder();
            for (String sourceLine : sourceLines) {
                source.append(sourceLine);
            }

            String transformed = VeilRenderSystem.renderer().getShaderModificationManager().applyModifiers(veil$captureId, source.toString(), VeilJobParameters.APPLY_VERSION | VeilJobParameters.ALLOW_OUT);
            return List.of(VanillaShaderImportProcessor.modify(transformed));
        } catch (Exception e) {
            Veil.LOGGER.error("Failed to modify vanilla source for shader: {}", veil$captureId, e);
        }
        return sourceLines;
    }
}
