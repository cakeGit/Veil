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
    public String modify(Context ctx, String source) throws IOException {
        for (ShaderPreProcessor preProcessor : this.processors) {
            source = preProcessor.modify(ctx, source);
        }
        return source;
    }
}
