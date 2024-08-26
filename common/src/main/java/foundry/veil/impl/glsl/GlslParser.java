package foundry.veil.impl.glsl;

import foundry.veil.impl.glsl.node.DeclarationNode;
import foundry.veil.impl.glsl.node.GlslNode;
import foundry.veil.impl.glsl.node.GlslTree;
import foundry.veil.impl.glsl.node.GlslVersion;
import org.jetbrains.annotations.Nullable;

public final class GlslParser {

    private GlslParser() {
    }

    public static GlslTree parse(GlslLexer.Token[] tokens) throws GlslSyntaxException {
        TokenReader reader = new TokenReader(tokens);

        GlslVersion version = new GlslVersion(110, true);

        // Try to parse version statements
        GlslLexer.Token token = reader.peek();
        if (token.type() == GlslLexer.TokenType.DIRECTIVE && token.value().startsWith("#version ")) {
            reader.skip();
            String[] parts = token.value().substring(9).split(" +", 2);
            try {
                int ver = Integer.parseInt(parts[0]);
                boolean core = parts.length == 1 || parts[1].equals("core");
                version = new GlslVersion(ver, core);
            } catch (NumberFormatException e) {
                throw reader.error("Invalid Version: " + token.value() + ". " + e.getMessage());
            }
        }

        while (reader.canRead()) {
            GlslNode node = parseExpression(reader);
            System.out.println(node);
        }

        return new GlslTree(version);
    }

    private static GlslNode parseExpression(TokenReader reader) throws GlslSyntaxException {
        GlslLexer.Token token = reader.peek();

        GlslNode left;

        // Declaration
        if (token.type().isType()) {
            reader.skip();
            GlslLexer.Token name = reader.consume(GlslLexer.TokenType.IDENTIFIER);
            left = new DeclarationNode(token, name.value());
        } else {
            throw reader.error("Invalid expression: " + token.value());
        }

        token = reader.peek();
        if (token.type() == GlslLexer.TokenType.SEMICOLON) {
            reader.skip();
            return left;
        }

        switch (token.type()) {
            case EQUAL -> {
                reader.skip();
            }
        }
    }

    private static GlslLexer.Token parseTypeSpecifier(TokenReader reader) throws GlslSyntaxException {
        GlslLexer.Token token = reader.peek();
        if (!token.type().isType()) {
            throw reader.error("Invalid type specifier: " + token.value());
        }
        return token;
    }

    private static class TokenReader {

        private final GlslLexer.Token[] tokens;
        private int cursor;

        public TokenReader(GlslLexer.Token[] tokens) {
            this.tokens = tokens;
        }

        public String getString() {
            StringBuilder builder = new StringBuilder();
            for (GlslLexer.Token token : this.tokens) {
                builder.append(token.value());
            }
            return builder.toString();
        }

        public boolean canRead(int length) {
            return this.cursor + length <= this.tokens.length;
        }

        public boolean canRead() {
            return this.canRead(1);
        }

        public int getCursorOffset() {
            int offset = 0;
            for (int i = 0; i <= Math.min(this.cursor, this.tokens.length - 1); i++) {
                offset += this.tokens[i].value().length();
            }
            return offset;
        }

        public GlslLexer.Token peek() {
            return this.peek(0);
        }

        public @Nullable GlslLexer.Token peek(int amount) {
            return this.cursor + amount < this.tokens.length ? this.tokens[this.cursor + amount] : null;
        }

        public GlslLexer.Token consume(GlslLexer.TokenType token) throws GlslSyntaxException {
            if (!this.canRead() || this.peek().type() != token) {
                throw this.error("Expected " + token);
            }
            this.cursor++;
            return this.peek(-1);
        }

        public GlslSyntaxException error(String error) {
            return new GlslSyntaxException(error, this.getString(), this.getCursorOffset());
        }

        public void skip() {
            this.cursor++;
        }
    }
}
