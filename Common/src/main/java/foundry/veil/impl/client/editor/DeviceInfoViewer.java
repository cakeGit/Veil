package foundry.veil.impl.client.editor;

import foundry.veil.api.client.editor.SingleWindowEditor;
import foundry.veil.api.client.imgui.VeilImGuiUtil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderLimits;
import foundry.veil.api.opencl.VeilOpenCL;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTreeNodeFlags;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL30C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL42C.*;
import static org.lwjgl.opengl.GL43C.*;

@ApiStatus.Internal
public class DeviceInfoViewer extends SingleWindowEditor {

    private static final Map<Integer, String> SHADER_TYPES;
    private static final int TEXT_COLOR = 0xFFAAAAAA;

    static {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(GL_VERTEX_SHADER, "Vertex Shader");
        map.put(GL_TESS_CONTROL_SHADER, "Tesselation Control Shader");
        map.put(GL_TESS_EVALUATION_SHADER, "Tesselation Evaluation Shader");
        map.put(GL_GEOMETRY_SHADER, "Geometry Shader");
        map.put(GL_FRAGMENT_SHADER, "Fragment Shader");
        map.put(GL_COMPUTE_SHADER, "Compute Shader");
        SHADER_TYPES = Collections.unmodifiableMap(map);
    }

    private static void text(String text, @Nullable String value, @Nullable String tooltip) {
        if (value != null) {
            ImGui.text(text);
            ImGui.sameLine(0);
            ImGui.textColored(0xFFFFFFFF, value);
        } else {
            ImGui.textDisabled(text + " Unsupported");
        }
        if (tooltip != null) {
            ImGui.sameLine();
            VeilImGuiUtil.tooltip(tooltip);
        }
    }

    private static void flagText(String text, boolean supported, @Nullable String tooltip) {
        ImGui.text(text);
        ImGui.sameLine(0);
        ImGui.textColored(supported ? 0xFF00FF00 : 0xFF0000FF, (supported ? "Yes" : "No"));
        if (tooltip != null) {
            ImGui.sameLine();
            VeilImGuiUtil.tooltip(tooltip);
        }
    }

    private void renderOpenGL() {
        ImGui.pushStyleColor(ImGuiCol.Text, 0xFFFFFFFF);
        ImGui.text("Vendor: " + glGetString(GL_VENDOR));
        ImGui.text("Renderer: " + glGetString(GL_RENDERER));
        ImGui.text("Version: " + glGetString(GL_VERSION));
        ImGui.popStyleColor();
        ImGui.separator();

        ImGui.textColored(0xFFFFFFFF, "Feature Flags:");
        flagText("Compute?", VeilRenderSystem.computeSupported(), "Whether compute shaders can be used");
        flagText("Atomic Counter?", VeilRenderSystem.atomicCounterSupported(), "Whether atomic counters can be used in shaders");
        flagText("Transform Feedback?", VeilRenderSystem.transformFeedbackSupported(), "Whether transform feedback can be used");
        flagText("Texture Multi-bind?", VeilRenderSystem.textureMultibindSupported(), "Whether glBindTextures can be used instead of glBindTexture");
        flagText("Sparse Buffers?", VeilRenderSystem.sparseBuffersSupported(), "Whether sparse buffers can be used");
        ImGui.separator();

        GLCapabilities caps = GL.getCapabilities();
        ImGui.popStyleColor();
        for (Map.Entry<Integer, String> entry : SHADER_TYPES.entrySet()) {
            if (ImGui.collapsingHeader(entry.getValue())) {
                ImGui.pushID(entry.getKey());
                ImGui.indent();
                ImGui.pushStyleColor(ImGuiCol.Text, TEXT_COLOR);

                VeilShaderLimits limits = VeilRenderSystem.shaderLimits(entry.getKey());
                text("Max Uniform Components", "" + limits.maxUniformComponents(), "This is the number of active components of uniform variables that can be defined outside of a uniform block. The term \"component\" is meant as the basic component of a vector/matrix. So a vec3 takes up 3 components. The minimum value here is 1024, enough room for 256 vec4s.");
                text("Max Uniform Blocks", "" + limits.maxUniformBlocks(), "The maximum number of uniform blocks that this shader stage can access. The OpenGL-required minimum is 12 in GL 3.3, and 14 in GL 4.3.");
                if (entry.getKey() != GL_COMPUTE_SHADER) {
                    text("Max Input Components", "" + limits.maxInputComponents(), "The maximum number of components that this stage can take as input. The required minimum value differs from shader stage to shader stage.");
                    text("Max Output Components", "" + limits.maxOutputComponents(), "The maximum number of components that this stage can output. The required minimum value differs from shader stage to shader stage.");
                }
                text("Max Texture Image Units", "" + limits.maxTextureImageUnits(), "The maximum number of texture image units that the sampler in this shader can access. The OpenGL-required minimum value is 16 for each stage.");
                text("Max Image Uniforms", limits.maxImageUniforms() > 0 ? "" + limits.maxImageUniforms() : null, "The maximum number of image variables for this shader stage. The OpenGL-required minimum is 8 for fragment and compute shaders, and 0 for the rest. This means implementations may not allow you to use image variables in non-fragment or compute stages.");

                boolean atomicCounters = caps.OpenGL42 || caps.GL_ARB_shader_atomic_counters;
                text("Max Atomic Counters", atomicCounters ? "" + limits.maxAtomicCounters() : null, "The maximum number of Atomic Counter variables that this stage can define. The OpenGL-required minimum is 8 for fragment and compute shaders, and 0 for the rest.");
                text("Max Atomic Counter Buffers", atomicCounters ? "" + limits.maxAtomicCountBuffers() : null, "The maximum number of different buffers that the atomic counter variables can come from. The OpenGL-required minimum is 1 for fragment shaders, 8 for compute shaders (note: possible spec typo), and again 0 for the rest.");
                text("Max Shader Storage Blocks", caps.OpenGL43 || caps.GL_ARB_shader_storage_buffer_object ? "" + limits.maxShaderStorageBlocks() : null, "The maximum number of different shader storage blocks that a stage can use. For fragment and compute shaders, the OpenGL-required minimum is 8; for the rest, it is 0.");

                ImGui.popStyleColor();
                ImGui.unindent();
                ImGui.popID();
            }
        }

        ImGui.pushStyleColor(ImGuiCol.Text, TEXT_COLOR);
        ImGui.textColored(0xFFFFFFFF, "Uniform:");
        text("Max Uniform Buffer Bindings:", "" + VeilRenderSystem.maxUniformBuffersBindings(), "The limit on the number of uniform buffer binding points. This is the limit for glBindBufferRange when using GL_UNIFORM_BUFFER.");
        text("Max Combined Uniform Blocks:", "" + glGetInteger(GL_MAX_COMBINED_UNIFORM_BLOCKS), "The maximum number of uniform blocks that all of the active programs can use. If two (or more) shader stages use the same block, they count separately towards this limit.");
        text("Max Combined Texture Image Units:", "" + VeilRenderSystem.maxCombinedTextureUnits(), "The total number of texture units that can be used from all active programs. This is the limit on glActiveTexture(GL_TEXTURE0 + i) and glBindSampler.");
        ImGui.separator();

        ImGui.textColored(0xFFFFFFFF, "Transform Feedback:");
        text("Max Transform Feedback Separate Attributes:", "" + glGetInteger(GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS), "When doing separate mode Transform Feedback, this is the maximum number of varying variables that can be captured.");
        text("Max Transform Feedback Separate Components:", "" + glGetInteger(GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS), "When doing separate mode Transform Feedback, this is the maximum number of components for a single varying variable (note that varyings can be arrays or structs) that can be captured.");
        text("Max Transform Feedback Interleaved Components:", "" + glGetInteger(GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS), "When doing interleaved Transform Feedback, this is the total number of components that can be captured within a single buffer.");
        text("Max Transform Feedback Buffers:", VeilRenderSystem.transformFeedbackSupported() ? "" + VeilRenderSystem.maxTransformFeedbackBindings() : null, "The maximum number of buffers that can be written to in transform feedback operations.");
        ImGui.separator();

        boolean atomicCounters = caps.OpenGL42 || caps.GL_ARB_shader_atomic_counters;
        ImGui.textColored(0xFFFFFFFF, "Atomic Counter:");
        text("Max Atomic Counter Buffer Bindings:", atomicCounters ? "" + VeilRenderSystem.maxAtomicCounterBufferBindings() : null, "The total number of atomic counter buffer binding points. This is the limit for glBindBufferRange when using GL_ATOMIC_COUNTER_BUFFER.");
        text("Max Combined Atomic Counter Buffers:", atomicCounters ? "" + glGetInteger(GL_MAX_COMBINED_ATOMIC_COUNTER_BUFFERS) : null, "The maximum number of atomic counter buffers variables across all active programs.");
        text("Max Combined Atomic Counters:", atomicCounters ? "" + glGetInteger(GL_MAX_COMBINED_ATOMIC_COUNTERS) : null, "The maximum number of atomic counter variables across all active programs.");
        text("Max Shader Storage Buffer Bindings:", atomicCounters ? "" + VeilRenderSystem.maxShaderStorageBufferBindings() : null, "The total number of shader storage buffer binding points. This is the limit for glBindBufferRange when using GL_SHADER_STORAGE_BUFFER.");
        ImGui.separator();

        boolean shaderStorageBuffers = caps.OpenGL43 || caps.GL_ARB_shader_storage_buffer_object;
        ImGui.textColored(0xFFFFFFFF, "Shader Storage:");
        text("Max Combined Shader Storage Blocks:", shaderStorageBuffers ? "" + glGetInteger(GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS) : null, "The maximum number of shader storage blocks across all active programs. As with UBOs, blocks that are the same between stages are counted for each stage.");
        text("Max Combined Shader Output Resources:", shaderStorageBuffers ? "" + glGetInteger(GL_MAX_COMBINED_SHADER_OUTPUT_RESOURCES) : null, "The total number of shader storage blocks, image variables, and fragment shader outputs across all active programs cannot exceed this number. This is the \"amount of stuff\" that a sequence of shaders can write to (barring Transform Feedback).");
        ImGui.separator();

        ImGui.textColored(0xFFFFFFFF, "Framebuffer:");
        text("Max Framebuffer Color Attachments:", "" + VeilRenderSystem.maxColorAttachments(), null);
        text("Max Framebuffer Samples:", "" + VeilRenderSystem.maxSamples(), null);
    }

    private void renderOpenAL() {
        ImGui.pushStyleColor(ImGuiCol.Text, 0xFFFFFFFF);
        ImGui.text("Vendor: " + alGetString(AL_VENDOR));
        ImGui.text("Renderer: " + alGetString(AL_RENDERER));
        ImGui.text("Version: " + alGetString(AL_VERSION));
        ImGui.popStyleColor();
    }

    private void renderOpenCL() {
        VeilOpenCL cl = VeilOpenCL.get();
        VeilOpenCL.PlatformInfo[] platforms = cl.getPlatforms();

        ImGui.textColored(0xFFFFFFFF, "Platforms:");
        for (int i = 0; i < platforms.length; i++) {
            VeilOpenCL.PlatformInfo platform = platforms[i];
            if (!ImGui.collapsingHeader(platform.name() + " (0x%X)".formatted(platform.id()), i == 0 ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
                continue;
            }

            ImGui.pushStyleColor(ImGuiCol.Text, TEXT_COLOR);
            text("Profile:", platform.profile(), null);
            text("CL Version:", platform.version(), null);
            text("Vendor:", platform.vendor(), null);
            ImGui.popStyleColor();

            ImGui.separator();

            VeilOpenCL.DeviceInfo[] devices = platform.devices();
            ImGui.textColored(0xFFFFFFFF, "Devices:");
            for (int j = 0; j < devices.length; j++) {
                VeilOpenCL.DeviceInfo device = devices[i];
                if (!ImGui.collapsingHeader(device.name() + " (0x%X)".formatted(device.id()), i == 0 ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
                    continue;
                }

                ImGui.pushStyleColor(ImGuiCol.Text, TEXT_COLOR);
                ImGui.indent();
                List<String> types = new ArrayList<>(1);
                if (device.isDefault()) {
                    types.add("Default");
                }
                if (device.isCpu()) {
                    types.add("CPU");
                }
                if (device.isGpu()) {
                    types.add("GPU");
                }
                if (device.isAccelerator()) {
                    types.add("Accelerator");
                }
                text("Type:", String.join(", ", types), null);
                text("Vendor ID:", "0x%X".formatted(device.vendorId()), null);
                text("Max Compute Units:", "" + device.maxComputeUnits(), null);
                text("Max Work Item Dimensions:", "" + device.maxWorkItemDimensions(), null);
                text("Max Work Group Size:", "" + device.maxWorkGroupSize(), null);
                text("Max Clock Frequency:", device.maxClockFrequency() + " MHz", null);
                text("Address Size:", device.addressBits() + " bits", null);
                flagText("Available:", device.available(), null);
                flagText("Compiler Available:", device.compilerAvailable(), null);
                flagText("Requires Manual CL/GL Sync:", device.requireManualInteropSync(), null);
                text("Vendor:", device.vendor(), null);
                text("Version:", device.version(), null);
                text("Driver Version:", device.driverVersion(), null);
                text("Profile:", device.profile(), null);
                text("OpenCL C Version:", device.openclCVersion(), null);
                ImGui.unindent();
                ImGui.popStyleColor();
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Device Info";
    }

    @Override
    public @Nullable String getGroup() {
        return "Info";
    }

    @Override
    protected void renderComponents() {
        if (ImGui.beginTabBar("##info")) {
            if (ImGui.beginTabItem("OpenCL")) {
                this.renderOpenCL();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("OpenGL")) {
                ImGui.pushStyleColor(ImGuiCol.Text, TEXT_COLOR);
                this.renderOpenGL();
                ImGui.popStyleColor();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("OpenAL")) {
                ImGui.pushStyleColor(ImGuiCol.Text, TEXT_COLOR);
                this.renderOpenAL();
                ImGui.popStyleColor();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
    }

    @Override
    public void render() {
        ImGui.setNextWindowSizeConstraints(400, 460, Float.MAX_VALUE, Float.MAX_VALUE);
        super.render();
    }
}
