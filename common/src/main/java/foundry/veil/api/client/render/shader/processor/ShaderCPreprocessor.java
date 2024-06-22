package foundry.veil.api.client.render.shader.processor;

import foundry.veil.Veil;
import io.github.douira.glsl_preprocessor.DefaultPreprocessorListener;
import io.github.douira.glsl_preprocessor.Feature;
import io.github.douira.glsl_preprocessor.Preprocessor;
import io.github.douira.glsl_preprocessor.StringLexerSource;
import io.github.douira.glsl_transformer.ast.transform.TransformationException;
import io.github.douira.glsl_transformer.parser.ParsingException;

import java.io.IOException;

public class ShaderCPreprocessor implements ShaderPreProcessor {

    @Override
    public String modify(Context ctx, String source) throws IOException {
        try (Preprocessor preprocessor = new Preprocessor()) {
            preprocessor.setListener(new DefaultPreprocessorListener());
            preprocessor.addInput(new StringLexerSource(source, true));
            preprocessor.addFeature(Feature.KEEPCOMMENTS);
            preprocessor.addFeature(Feature.GLSL_PASSTHROUGH);

            return preprocessor.printToString();
        } catch (TransformationException | ParsingException | IllegalStateException | IllegalArgumentException e) {
            Veil.LOGGER.error("Failed to parse shader: {}", ctx.name(), e);
        }
        return source;
    }
}
