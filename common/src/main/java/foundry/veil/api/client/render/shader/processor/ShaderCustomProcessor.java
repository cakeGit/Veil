package foundry.veil.api.client.render.shader.processor;

import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Processes all veil custom directives.
 *
 * @author Ocelot
 */
public class ShaderCustomProcessor implements ShaderPreProcessor {

    private static final String CUSTOM_KEY = "#custom ";

    private final ShaderImportProcessor importProcessor;

    /**
     * Creates a new import processor that loads import files from the specified resource provider.
     *
     * @param resourceProvider The provider for import resources
     */
    public ShaderCustomProcessor(ResourceProvider resourceProvider) {
        this.importProcessor = new ShaderImportProcessor(resourceProvider);
    }

    @Override
    public void prepare() {
        this.importProcessor.prepare();
    }

    @Override
    public String modify(Context context, String source) throws IOException {
        List<String> inputLines = source.lines().toList();
        List<String> output = new LinkedList<>();

        for (String line : inputLines) {
            if (!line.startsWith(ShaderCustomProcessor.CUSTOM_KEY)) {
                output.add(line);
                continue;
            }

            String[] parts = line.split(" ", 3);
            if (parts.length < 3) {
                throw new IOException("Invalid Veil custom directive syntax: " + line);
            }

            String directive = parts[1];
            if ("veil:include".equalsIgnoreCase(directive)) {
                output.add(this.importProcessor.modify(context, "#include " + parts[2]));
                continue;
            }

            throw new IOException("Invalid Veil custom directive: " + directive);
        }

        return String.join("\n", output);
    }
}
