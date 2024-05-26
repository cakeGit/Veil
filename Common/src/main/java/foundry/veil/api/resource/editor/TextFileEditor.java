package foundry.veil.api.resource.editor;

import foundry.veil.Veil;
import foundry.veil.api.client.imgui.CodeEditor;
import foundry.veil.api.resource.VeilEditorEnvironment;
import foundry.veil.api.resource.VeilResourceInfo;
import foundry.veil.api.resource.VeilResourceManager;
import foundry.veil.api.resource.type.VeilTextResource;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class TextFileEditor implements ResourceFileEditor<VeilTextResource<?>> {

    private final CodeEditor editor;

    public TextFileEditor() {
        this.editor = new CodeEditor("Save");
    }

    @Override
    public void render() {
        this.editor.renderWindow();
        if (ImGui.beginPopupModal("###open_failed")) {
            ImGui.text("Failed to open file");
            ImGui.endPopup();
        }
    }

    @Override
    public void open(VeilEditorEnvironment environment, VeilTextResource<?> resource) {
        VeilResourceInfo info = resource.resourceInfo();
        TextEditorLanguageDefinition languageDefinition = resource.languageDefinition();
        VeilResourceManager resourceManager = environment.getResourceManager();

        this.editor.show(info.fileName(), "");
        this.editor.setSaveCallback(null);
        this.editor.getEditor().setReadOnly(true);
        this.editor.getEditor().setColorizerEnable(false);

        resourceManager.resources(info).getResource(info.location()).ifPresentOrElse(data -> CompletableFuture.supplyAsync(() -> {
            try (InputStream stream = data.open()) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, Util.ioPool()).handleAsync((contents, error) -> {
            if (error != null) {
                this.editor.hide();
                ImGui.openPopup("###open_failed");
                Veil.LOGGER.error("Failed to open file", error);
                return null;
            }

            this.editor.show(info.fileName(), contents);

            VeilResourceInfo resourceInfo = resource.resourceInfo();
            boolean readOnly = resourceInfo.isStatic();
            this.editor.setSaveCallback((source, errorConsumer) -> CompletableFuture.runAsync(() -> {
                try {
                    if (readOnly) {
                        throw new IOException("Read-only resource");
                    }

                    Path path = resourceInfo.filePath();
                    try (OutputStream os = Files.newOutputStream(path)) {
                        os.write(source.getBytes(StandardCharsets.UTF_8));
                    }

                    Path modPath = info.modResourcePath();
                    if (modPath == null) {
                        return;
                    }

                    // Copy from build to resources
                    try (InputStream is = Files.newInputStream(path); OutputStream os = Files.newOutputStream(modPath, StandardOpenOption.TRUNCATE_EXISTING)) {
                        IOUtils.copyLarge(is, os);
                    }
                } catch (Exception e) {
                    Veil.LOGGER.error("Failed to write resource: {}", resourceInfo.location(), e);
                }
            }, Util.ioPool()).thenRunAsync(resource::hotReload, Minecraft.getInstance()).exceptionally(e -> {
                Veil.LOGGER.error("Failed to hot-swap resource: {}", resourceInfo.location(), e);
                return null;
            }));

            TextEditor textEditor = this.editor.getEditor();
            textEditor.setReadOnly(readOnly);
            if (languageDefinition != null) {
                textEditor.setColorizerEnable(true);
                textEditor.setLanguageDefinition(languageDefinition);
            }
            return null;
        }, Minecraft.getInstance()), () -> {
            this.editor.hide();
            ImGui.openPopup("###open_failed");
        });
    }
}
