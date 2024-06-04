package foundry.veil.api.client.render.shader.processor;

import java.io.IOException;

public record ShaderMultiProcessor(ShaderPreProcessor[] processors) implements ShaderPreProcessor {

    @Override
    public void prepare() {
        for (ShaderPreProcessor processor : this.processors) {
            processor.prepare();
        }
    }

    @Override
    public String modify(Context context) throws IOException {
        String source = context.sourceCode();
        for (ShaderPreProcessor preProcessor : this.processors) {
            source = preProcessor.modify(context);
            context = context.withSource(context.name(), source);
        }
        return source;
    }
}
