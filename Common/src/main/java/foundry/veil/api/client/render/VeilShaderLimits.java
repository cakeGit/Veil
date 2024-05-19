package foundry.veil.api.client.render;

import org.lwjgl.opengl.GLCapabilities;

import static org.lwjgl.opengl.GL11C.glGetInteger;

/**
 * Shader stage specific resource limits.
 *
 * @param maxUniformComponents
 * @param maxUniformBlocks
 * @param maxInputComponents
 * @param maxOutputComponents
 * @param maxTextureImageUnits
 * @param maxImageUniforms
 * @param maxAtomicCounters
 * @param maxAtomicCountBuffers
 * @param maxShaderStorageBlocks
 */
public record VeilShaderLimits(int maxUniformComponents,
                               int maxUniformBlocks,
                               int maxInputComponents,
                               int maxOutputComponents,
                               int maxTextureImageUnits,
                               int maxImageUniforms,
                               int maxAtomicCounters,
                               int maxAtomicCountBuffers,
                               int maxShaderStorageBlocks) {

    public VeilShaderLimits(GLCapabilities caps,
                            int maxUniformComponents,
                            int maxUniformBlocks,
                            int maxInputComponents,
                            int maxOutputComponents,
                            int maxTextureImageUnits,
                            int maxImageUniforms,
                            int maxAtomicCounters,
                            int maxAtomicCountBuffers,
                            int maxShaderStorageBlocks) {
        this(glGetInteger(maxUniformComponents),
                glGetInteger(maxUniformBlocks),
                glGetInteger(maxInputComponents),
                glGetInteger(maxOutputComponents),
                glGetInteger(maxTextureImageUnits),
                caps.OpenGL42 || caps.GL_ARB_shader_image_load_store ? glGetInteger(maxImageUniforms) : 0,
                caps.OpenGL42 || caps.GL_ARB_shader_atomic_counters ? glGetInteger(maxAtomicCounters) : 0,
                caps.OpenGL42 || caps.GL_ARB_shader_atomic_counters ? glGetInteger(maxAtomicCountBuffers) : 0,
                caps.OpenGL43 || caps.GL_ARB_shader_storage_buffer_object ? glGetInteger(maxShaderStorageBlocks) : 0);
    }
}
