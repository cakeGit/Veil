package foundry.veil.impl.glsl.type;

import foundry.veil.impl.glsl.node.GlslNode;
import org.jetbrains.annotations.Nullable;

public interface GlslTypeQualifier {

    record Storage(Type type, String[] typeNames) implements GlslTypeQualifier {

        public Storage(Type type) {
            this(type, new String[0]);
        }

        public Storage(String[] typeNames) {
            this(Type.SUBROUTINE, typeNames);
        }

        public enum Type {
            CONST,
            IN,
            OUT,
            INOUT,
            CENTROID,
            PATCH,
            SAMPLE,
            UNIFORM,
            BUFFER,
            SHARED,
            COHERENT,
            VOLATILE,
            RESTRICT,
            READONLY,
            WRITEONLY,
            SUBROUTINE
        }
    }

    record Layout(String identifier, @Nullable GlslNode constantExpression) implements GlslTypeQualifier {

        public boolean shared() {
            return "shared".equals(this.identifier);
        }

        @Override
        public @Nullable GlslNode constantExpression() {
            return this.shared() ? null : this.constantExpression;
        }
    }

    enum Precision implements GlslTypeQualifier {
        HIGH_PRECISION,
        MEDIUM_PRECISION,
        LOW_PRECISION
    }

    enum Interpolation implements GlslTypeQualifier {
        SMOOTH,
        FLAT,
        NOPERSPECTIVE
    }

    enum Invariant implements GlslTypeQualifier {
        INVARIANT
    }

    enum Precise implements GlslTypeQualifier {
        PRECISE
    }
}
