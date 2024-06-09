package foundry.veil.impl.client.render.shader.modifier;

import foundry.veil.impl.client.render.shader.transformer.VeilJobParameters;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.Version;
import io.github.douira.glsl_transformer.ast.node.VersionStatement;
import io.github.douira.glsl_transformer.ast.node.statement.Statement;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.regex.Matcher;

@ApiStatus.Internal
public class SimpleShaderModification implements ShaderModification {

    private final int version;
    private final int priority;
    private final ResourceLocation[] includes;
    private final String output;
    private final String uniform;
    private final Function[] functions;

    public SimpleShaderModification(int version, int priority, ResourceLocation[] includes, @Nullable String output, @Nullable String uniform, Function[] functions) {
        this.version = version;
        this.priority = priority;
        this.includes = includes;
        this.output = output;
        this.uniform = uniform;
        this.functions = functions;
    }

    @Override
    public void inject(ASTParser parser, TranslationUnit tree, VeilJobParameters parameters) throws IOException {
        if (parameters.applyVersion()) {
            tree.ensureVersionStatement();
            VersionStatement statement = tree.getVersionStatement();
            if (statement.version.number < this.version) {
                statement.version = Version.fromNumber(this.version);
            }
        }

        StringBuilder includes = new StringBuilder();
        for (ResourceLocation include : this.includes) {
            includes.append("#custom veil:include ").append(include).append("\n");
        }
        tree.parseAndInjectNode(parser, ASTInjectionPoint.BEFORE_DECLARATIONS, includes.toString());

        if (!StringUtil.isNullOrEmpty(this.uniform)) {
            tree.parseAndInjectNode(parser, ASTInjectionPoint.BEFORE_DECLARATIONS, this.fillPlaceholders(this.uniform) + '\n');
        }

        if (!StringUtil.isNullOrEmpty(this.output)) {
            tree.parseAndInjectNode(parser, ASTInjectionPoint.BEFORE_DECLARATIONS, this.fillPlaceholders(this.output) + '\n');
        }

        for (Function function : this.functions) {
            Statement statement = parser.parseStatement(tree.getRoot(), this.fillPlaceholders("{" + function.code() + "}"));

            if (function.head()) {
                tree.prependFunctionBody(function.name(), statement);
            } else {
                tree.appendFunctionBody(function.name(), statement);
            }
        }
    }

    public String fillPlaceholders(String code) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(code);
        if (!matcher.find()) {
            return code;
        }

        StringBuilder sb = new StringBuilder();
        matcher.appendReplacement(sb, this.getPlaceholder(matcher.group(1)));
        while (matcher.find()) {
            matcher.appendReplacement(sb, this.getPlaceholder(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    protected String getPlaceholder(String key) {
        return key;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    public String getOutput() {
        return this.output;
    }
}
