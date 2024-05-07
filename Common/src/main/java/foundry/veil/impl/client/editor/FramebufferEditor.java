package foundry.veil.impl.client.editor;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.editor.SingleWindowEditor;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.AdvancedFboTextureAttachment;
import foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition;
import foundry.veil.api.client.render.framebuffer.FramebufferManager;
import foundry.veil.api.client.util.TextureDownloader;
import imgui.ImGui;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.opengl.GL11C.*;

@ApiStatus.Internal
public class FramebufferEditor extends SingleWindowEditor {

    private AdvancedFbo downloadBuffer;

    @Override
    public String getDisplayName() {
        return "Framebuffer";
    }

    @Override
    public @Nullable String getGroup() {
        return "Renderer";
    }

    @Override
    protected void renderComponents() {
        VeilRenderer renderer = VeilRenderSystem.renderer();

        if (ImGui.beginTabBar("Framebuffers")) {
            FramebufferManager framebufferManager = renderer.getFramebufferManager();
            for (Map.Entry<ResourceLocation, AdvancedFbo> entry : framebufferManager.getFramebuffers().entrySet()) {
                this.drawBuffers(entry.getKey().toString(), entry.getValue());
            }
            ImGui.endTabBar();
        }
    }

    @Override
    public void renderLast() {
        super.renderLast();

        if (this.downloadBuffer != null) {
            try {
                Minecraft client = Minecraft.getInstance();
                Path outputFolder = Paths.get(client.gameDirectory.toURI()).resolve("debug-out").resolve("deferred");
                if (!Files.exists(outputFolder)) {
                    Files.createDirectories(outputFolder);
                } else {
                    Files.walkFileTree(outputFolder, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }

                List<CompletableFuture<?>> result = new LinkedList<>();
                for (int i = 0; i < this.downloadBuffer.getColorAttachments(); i++) {
                    if (this.downloadBuffer.isColorTextureAttachment(i)) {
                        AdvancedFboTextureAttachment attachment = this.downloadBuffer.getColorTextureAttachment(i);
                        String name = attachment.getName() != null ? attachment.getName() : "Attachment " + i;
                        result.add(TextureDownloader.save(name, outputFolder, attachment.getId(), true));
                    }
                }

                CompletableFuture.allOf(result.toArray(new CompletableFuture[0])).thenRunAsync(() -> Util.getPlatform().openFile(outputFolder.toFile()), client);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.downloadBuffer = null;
        }
    }

    private void drawBuffers(String name, @Nullable AdvancedFbo buffer) {
        ImGui.beginDisabled(buffer == null);
        if (ImGui.beginTabItem(name)) {
            if (buffer != null) {
                if (ImGui.button("Download")) {
                    this.downloadBuffer = buffer;
                }

                int columns = (int) Math.ceil(Math.sqrt(buffer.getColorAttachments() + (buffer.isDepthTextureAttachment() ? 1 : 0)));
                float width = ImGui.getContentRegionAvailX() / columns;
                float height = width * buffer.getHeight() / buffer.getWidth();
                int i;
                for (i = 0; i < buffer.getColorAttachments(); i++) {
                    if (!buffer.isColorTextureAttachment(i)) {
                        continue;
                    }

                    if (i % columns != 0) {
                        ImGui.sameLine();
                    }
                    ImGui.beginGroup();
                    AdvancedFboTextureAttachment attachment = buffer.getColorTextureAttachment(i);
                    ImGui.text(this.getAttachmentName(i, attachment));
                    ImGui.image(attachment.getId(), width, height, 0, 1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                    ImGui.endGroup();
                }

                if (buffer.isDepthTextureAttachment()) {
                    if (i % columns != 0) {
                        ImGui.sameLine();
                    }
                    ImGui.beginGroup();
                    AdvancedFboTextureAttachment attachment = buffer.getDepthTextureAttachment();
                    ImGui.text(this.getAttachmentName(-1, attachment));
                    ImGui.image(attachment.getId(), width, height, 0, 1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                    ImGui.endGroup();
                }
            }
            ImGui.endTabItem();
        }
        ImGui.endDisabled();
    }

    private String getAttachmentName(int index, AdvancedFboTextureAttachment attachment) {
        RenderSystem.bindTexture(attachment.getId());
        StringBuilder attachmentName = new StringBuilder(attachment.getName() != null ? attachment.getName() : index == -1 ? "Depth" : ("Attachment " + index));

        int internalFormat = glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        for (FramebufferAttachmentDefinition.Format format : FramebufferAttachmentDefinition.Format.values()) {
            if (internalFormat == format.getInternalId()) {
                attachmentName.append(" (").append(format.name()).append(")");
                return attachmentName.toString();
            }
        }

        attachmentName.append(" (0x").append(Integer.toHexString(internalFormat).toUpperCase(Locale.ROOT)).append(")");
        return attachmentName.toString();
    }
}
