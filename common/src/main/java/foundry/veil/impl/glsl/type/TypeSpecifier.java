package foundry.veil.impl.glsl.type;

import foundry.veil.impl.glsl.GlslLexer;

public interface TypeSpecifier {

    static TypeSpecifier simple(GlslLexer.Token token) {
        if (!token.type().isType()) {
            throw new IllegalArgumentException("Invalid type: " + token);
        }
        return new SimpleType(token);
    }

    static TypeSpecifier struct(StructSpecifier structSpecifier) {
        return new Struct(structSpecifier);
    }

    record SimpleType(GlslLexer.Token type) implements TypeSpecifier {
    }

    record Struct(StructSpecifier structSpecifier) implements TypeSpecifier {
    }

    record Name(String name) implements TypeSpecifier {
    }
}
