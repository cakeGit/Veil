package foundry.veil.api.resource.type;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.resource.VeilResourceAction;
import foundry.veil.api.resource.VeilResourceInfo;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.InactiveProfiler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record FramebufferResource(VeilResourceInfo resourceInfo) implements VeilTextResource<FramebufferResource> {

    @Override
    public List<VeilResourceAction<FramebufferResource>> getActions() {
        return List.of(this.createTextEditAction());
    }

    @Override
    public boolean canHotReload() {
        return true;
    }

    @Override
    public void hotReload() {
        // TODO add way to reload single framebuffer
        Minecraft client = Minecraft.getInstance();
        VeilRenderSystem.renderer().getFramebufferManager().reload(CompletableFuture::completedFuture, client.getResourceManager(), InactiveProfiler.INSTANCE, InactiveProfiler.INSTANCE, Util.backgroundExecutor(), client);
    }

    @Override
    public int getIconCode() {
        return 0xED0F;
    }

    @Override
    public @Nullable TextEditorLanguageDefinition languageDefinition() {
        return null;
    }
}
