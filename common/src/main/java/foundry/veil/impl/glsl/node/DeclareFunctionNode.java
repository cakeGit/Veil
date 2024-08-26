package foundry.veil.impl.glsl.node;

import foundry.veil.impl.glsl.GlslLexer;

public class DeclareFunctionNode implements GlslNode {

    @Override
    public void visit(GlslVisitor visitor) {

    }

    public record ParameterDeclaration() {
    }
}
