package foundry.veil.impl.client.render.shader.modifier;

import foundry.veil.impl.client.render.shader.transformer.VeilJobParameters;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.declaration.DeclarationMember;
import io.github.douira.glsl_transformer.ast.node.external_declaration.ExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.type.specifier.TypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.query.match.AutoHintedMatcher;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.parser.ParseShape;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class VertexShaderModification extends SimpleShaderModification {

    public static final AutoHintedMatcher<ExternalDeclaration> INPUT = new AutoHintedMatcher<>("in type name;", ParseShape.EXTERNAL_DECLARATION) {
        {
            Root root = this.pattern.getRoot();
            this.markClassWildcard("type", root.identifierIndex.getUnique("type").getAncestor(TypeSpecifier.class));
            this.markClassWildcard("name", root.identifierIndex.getUnique("name").getAncestor(DeclarationMember.class));
        }
    };

    private final Attribute[] attributes;
    private final Map<String, String> mapper;

    public VertexShaderModification(int version, int priority, ResourceLocation[] includes, @Nullable String output, @Nullable String uniform, Function[] functions, Attribute[] attributes) {
        super(version, priority, includes, output, uniform, functions);
        this.attributes = attributes;
        this.mapper = new HashMap<>(this.attributes.length);
    }

    @Override
    public void inject(ASTParser parser, TranslationUnit tree, VeilJobParameters parameters) throws IOException {
        if (this.attributes.length > 0) {
            Map<Integer, Attribute> validInputs = new Int2ObjectArrayMap<>();

            tree.getRoot().processMatches(parser, INPUT, a -> {
                System.out.println(a);
            });

//            Matcher matcher = IN_PATTERN.matcher(builder);
//            while (matcher.find()) {
//                pointer = matcher.end();
//                validInputs.put(validInputs.size(), new Attribute(validInputs.size(), matcher.group(1), matcher.group(2)));
//            }
//
//            matcher.reset();
//            this.mapper.clear();
//            for (Attribute attribute : this.attributes) {
//                Attribute sourceAttribute = validInputs.get(attribute.index);
//                if (sourceAttribute == null) {
//                    // TODO this might be messed up on mac. It needs to be tested
//                    builder.insert(pointer, "layout(location = " + attribute.index + ") in " + attribute.type + " " + attribute.name + ";\n");
//                    this.mapper.put(attribute.name, attribute.name);
//                    continue;
//                }
//
//                if (!sourceAttribute.type.equals(attribute.type)) {
//                    throw new IOException("Expected attribute " + attribute.index + " to be " + attribute.type + " but was " + sourceAttribute.type);
//                }
//
//                this.mapper.put(attribute.name, sourceAttribute.name);
//            }
        }

        super.inject(parser, tree, parameters);
    }

//    @Override
//    protected void processBody(int pointer, StringBuilder builder) throws IOException {
//        if (this.attributes.length > 0) {
//            Map<Integer, Attribute> validInputs = new Int2ObjectArrayMap<>();
//
//            Matcher matcher = IN_PATTERN.matcher(builder);
//            while (matcher.find()) {
//                pointer = matcher.end();
//                validInputs.put(validInputs.size(), new Attribute(validInputs.size(), matcher.group(1), matcher.group(2)));
//            }
//
//            matcher.reset();
//            this.mapper.clear();
//            for (Attribute attribute : this.attributes) {
//                Attribute sourceAttribute = validInputs.get(attribute.index);
//                if (sourceAttribute == null) {
//                    // TODO this might be messed up on mac. It needs to be tested
//                    builder.insert(pointer, "layout(location = " + attribute.index + ") in " + attribute.type + " " + attribute.name + ";\n");
//                    this.mapper.put(attribute.name, attribute.name);
//                    continue;
//                }
//
//                if (!sourceAttribute.type.equals(attribute.type)) {
//                    throw new IOException("Expected attribute " + attribute.index + " to be " + attribute.type + " but was " + sourceAttribute.type);
//                }
//
//                this.mapper.put(attribute.name, sourceAttribute.name);
//            }
//        }
//
//        super.processBody(pointer, builder);
//    }

    @Override
    protected String getPlaceholder(String key) {
        String name = this.mapper.get(key);
        return name != null ? name : super.getPlaceholder(key);
    }

    public record Attribute(int index, String type, String name) {
    }
}
