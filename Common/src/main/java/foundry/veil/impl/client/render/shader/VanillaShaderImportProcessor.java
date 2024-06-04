package foundry.veil.impl.client.render.shader;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.api.client.render.shader.processor.ShaderBindingProcessor;
import foundry.veil.api.client.render.shader.processor.ShaderImportProcessor;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import foundry.veil.api.client.render.shader.program.ProgramDefinition;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Allows vanilla shaders to use <code>#define namespace:id</code> imports
 */
@ApiStatus.Internal
public class VanillaShaderImportProcessor {

    private static ShaderPreProcessor processor;

    public static void setup(ResourceProvider resourceProvider) {
        processor = ShaderPreProcessor.allOf(new ShaderImportProcessor(resourceProvider), new ShaderBindingProcessor());
    }

    public static void free() {
        processor = null;
    }

    public static String modify(String source) throws IOException {
        if (processor == null) {
            throw new NullPointerException("Processor not initialized");
        }
        return processor.modify(new Context(processor, source));
    }

    private record Context(ShaderPreProcessor processor, String source) implements ShaderPreProcessor.Context {

        @Override
        public String modify(@Nullable ResourceLocation name, String source) throws IOException {
            return this.processor.modify(this.withSource(name, source));
        }

        @Override
        public void addUniformBinding(String name, int binding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addDefinitionDependency(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addInclude(ResourceLocation name) {
        }

        @Override
        public @Nullable ResourceLocation name() {
            return null;
        }

        @Override
        public String sourceCode() {
            return this.source;
        }

        @Override
        public int type() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileToIdConverter idConverter() {
            return VeilRenderSystem.renderer().getShaderManager().getSourceSet().getTypeConverter(this.type());
        }

        @Override
        public boolean isSourceFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Nullable ProgramDefinition definitions() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ShaderPreDefinitions preDefinitions() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ShaderPreProcessor.Context withSource(@Nullable ResourceLocation name, String source) {
            return new Context(this.processor, source);
        }
    }
}
