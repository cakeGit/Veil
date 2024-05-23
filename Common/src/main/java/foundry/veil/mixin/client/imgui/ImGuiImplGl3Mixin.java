package foundry.veil.mixin.client.imgui;

import imgui.gl3.ImGuiImplGl3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ImGuiImplGl3.class, remap = false)
public interface ImGuiImplGl3Mixin {

    @Accessor
    int getGShaderHandle();
}
