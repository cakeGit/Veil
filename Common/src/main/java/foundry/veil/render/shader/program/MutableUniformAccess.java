package foundry.veil.render.shader.program;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.render.framebuffer.AdvancedFbo;
import foundry.veil.render.framebuffer.AdvancedFboTextureAttachment;
import foundry.veil.render.shader.texture.ShaderTextureSource;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41C.*;

/**
 * Provides read and write access to all uniform variables in a shader program.
 *
 * @author Ocelot
 */
public interface MutableUniformAccess extends UniformAccess {

    /**
     * Sets default uniforms based on what {@link RenderSystem} provides.
     */
    default void applyRenderSystem() {
        this.setMatrix("RenderModelViewMat", RenderSystem.getModelViewMatrix());
        this.setMatrix("RenderProjMat", RenderSystem.getProjectionMatrix());
        float[] color = RenderSystem.getShaderColor();
        this.setVector("ColorModulator", color[0], color[1], color[2], color[3]);
        this.setFloat("GameTime", RenderSystem.getShaderGameTime());
    }

    /**
     * Applies the {@link RenderSystem} textures to <code>Sampler0</code>-<code>Sampler11</code>.
     */
    default void addRenderSystemTextures() {
        for (int i = 0; i < 12; ++i) {
            this.addSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
        }
    }

    /**
     * Sets <code>DiffuseSampler0</code>-<code>DiffuseSamplerMax</code>
     * to the color buffers in the specified framebuffer.
     *
     * @param framebuffer The framebuffer to bind samplers from
     */
    default void setFramebufferSamplers(AdvancedFbo framebuffer) {
        int activeTexture = GlStateManager._getActiveTexture();
        for (int i = 0; i < framebuffer.getColorAttachments(); i++) {
            if (!framebuffer.isColorTextureAttachment(i)) {
                continue;
            }

            AdvancedFboTextureAttachment attachment = framebuffer.getColorTextureAttachment(i);
            this.addSampler("DiffuseSampler" + i, attachment.getId());
            if (attachment.getName() != null) {
                this.addSampler(attachment.getName(), attachment.getId());
            }
        }

        if (framebuffer.isDepthTextureAttachment()) {
            AdvancedFboTextureAttachment attachment = framebuffer.getDepthTextureAttachment();
            this.addSampler("DiffuseDepthSampler", framebuffer.getDepthTextureAttachment().getId());
            if (attachment.getName() != null) {
                this.addSampler(attachment.getName(), attachment.getId());
            }
        }

        RenderSystem.activeTexture(activeTexture);
    }

    /**
     * Loads the samplers set by {@link #addSampler(CharSequence, int)} into the shader.
     *
     * @param sampler The sampler to start binding to
     * @return The next available sampler
     */
    default int applyShaderSamplers(int sampler) {
        return this.applyShaderSamplers(ShaderTextureSource.GLOBAL_CONTEXT, sampler);
    }

    /**
     * Loads the samplers set by {@link #addSampler(CharSequence, int)} into the shader.
     *
     * @param context The context for setting built-in shader samplers or <code>null</code> to ignore normal samplers
     * @param sampler The sampler to start binding to
     * @return The next available sampler
     */
    int applyShaderSamplers(@Nullable ShaderTextureSource.Context context, int sampler);

    /**
     * Adds a texture that is dynamically bound and sets texture units.
     *
     * @param name      The name of the texture to set
     * @param textureId The id of the texture to bind and assign a texture unit
     */
    void addSampler(CharSequence name, int textureId);

    /**
     * Removes the specified sampler binding.
     *
     * @param name The name of the sampler to remove
     */
    void removeSampler(CharSequence name);

    /**
     * Clears all samplers.
     */
    void clearSamplers();

    /**
     * Sets the binding to use for the specified uniform block.
     *
     * @param name    The name of the block to set
     * @param binding The binding to use for that block
     */
    default void setUniformBlock(CharSequence name, int binding) {
        int index = this.getUniformBlock(name);
        if (index != GL_INVALID_INDEX) {
            glUniformBlockBinding(this.getProgram(), index, binding);
        }
    }

    /**
     * Sets a float in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setFloat(CharSequence name, float value) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform1f(this.getProgram(), location, value);
        }
    }

    /**
     * Sets a vector in the shader.
     *
     * @param name The name of the uniform to set
     * @param x    The x component of the vector
     * @param y    The y component of the vector
     */
    default void setVector(CharSequence name, float x, float y) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform2f(this.getProgram(), location, x, y);
        }
    }

    /**
     * Sets a vector in the shader.
     *
     * @param name The name of the uniform to set
     * @param x    The x component of the vector
     * @param y    The y component of the vector
     * @param z    The z component of the vector
     */
    default void setVector(CharSequence name, float x, float y, float z) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform3f(this.getProgram(), location, x, y, z);
        }
    }

    /**
     * Sets a vector in the shader.
     *
     * @param name The name of the uniform to set
     * @param x    The x component of the vector
     * @param y    The y component of the vector
     * @param z    The z component of the vector
     * @param w    The w component of the vector
     */
    default void setVector(CharSequence name, float x, float y, float z, float w) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform4f(this.getProgram(), location, x, y, z, w);
        }
    }

    /**
     * Sets a vector in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setVector(CharSequence name, Vector2fc value) {
        this.setVector(name, value.x(), value.y());
    }

    /**
     * Sets a vector in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setVector(CharSequence name, Vector3fc value) {
        this.setVector(name, value.x(), value.y(), value.z());
    }

    /**
     * Sets a vector in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setVector(CharSequence name, Vector4fc value) {
        this.setVector(name, value.x(), value.y(), value.z(), value.w());
    }

    /**
     * Sets an integer in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setInt(CharSequence name, int value) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform1i(this.getProgram(), location, value);
        }
    }

    /**
     * Sets an integer vector in the shader.
     *
     * @param name The name of the uniform to set
     * @param x    The x component of the vector
     * @param y    The y component of the vector
     */
    default void setVectorI(CharSequence name, int x, int y) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform2i(this.getProgram(), location, x, y);
        }
    }

    /**
     * Sets an integer vector in the shader.
     *
     * @param name The name of the uniform to set
     * @param x    The x component of the vector
     * @param y    The y component of the vector
     * @param z    The z component of the vector
     */
    default void setVectorI(CharSequence name, int x, int y, int z) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform3i(this.getProgram(), location, x, y, z);
        }
    }

    /**
     * Sets an integer vector in the shader.
     *
     * @param name The name of the uniform to set
     * @param x    The x component of the vector
     * @param y    The y component of the vector
     * @param z    The z component of the vector
     * @param w    The w component of the vector
     */
    default void setVectorI(CharSequence name, int x, int y, int z, int w) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform4i(this.getProgram(), location, x, y, z, w);
        }
    }

    /**
     * Sets an integer vector in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setVectorI(CharSequence name, Vector2ic value) {
        this.setVectorI(name, value.x(), value.y());
    }

    /**
     * Sets an integer vector in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setVectorI(CharSequence name, Vector3ic value) {
        this.setVectorI(name, value.x(), value.y(), value.z());
    }

    /**
     * Sets an integer vector in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setVectorI(CharSequence name, Vector4ic value) {
        this.setVectorI(name, value.x(), value.y(), value.z(), value.w());
    }

    /**
     * Sets an array of floats in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setFloats(CharSequence name, float... values) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform1fv(this.getProgram(), location, values);
        }
    }

    /**
     * Sets an array of vectors in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setVectors(CharSequence name, Vector2fc... values) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(values.length * 2);
            for (int i = 0; i < values.length; i++) {
                values[i].get(i * 2, buffer);
            }
            glProgramUniform2fv(this.getProgram(), location, buffer);
        }
    }

    /**
     * Sets an array of vectors in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setVectors(CharSequence name, Vector3fc... values) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(values.length * 3);
            for (int i = 0; i < values.length; i++) {
                values[i].get(i * 3, buffer);
            }
            glProgramUniform3fv(this.getProgram(), location, buffer);
        }
    }

    /**
     * Sets an array of vectors in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setVectors(CharSequence name, Vector4fc... values) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(values.length * 4);
            for (int i = 0; i < values.length; i++) {
                values[i].get(i * 4, buffer);
            }
            glProgramUniform4fv(this.getProgram(), location, buffer);
        }
    }

    /**
     * Sets an array of integers in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setInts(CharSequence name, int... values) {
        int location = this.getUniform(name);
        if (location != -1) {
            glProgramUniform1iv(this.getProgram(), location, values);
        }
    }

    /**
     * Sets an array of integer vectors in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setVectors(CharSequence name, Vector2ic... values) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(values.length * 2);
            for (int i = 0; i < values.length; i++) {
                values[i].get(i * 2, buffer);
            }
            glProgramUniform2iv(this.getProgram(), location, buffer);
        }
    }

    /**
     * Sets an array of integer vectors in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setVectors(CharSequence name, Vector3ic... values) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(values.length * 3);
            for (int i = 0; i < values.length; i++) {
                values[i].get(i * 3, buffer);
            }
            glProgramUniform3iv(this.getProgram(), location, buffer);
        }
    }

    /**
     * Sets an array of integer vectors in the shader.
     *
     * @param name   The name of the uniform to set
     * @param values The values to set in order
     */
    default void setVectors(CharSequence name, Vector4ic... values) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(values.length * 4);
            for (int i = 0; i < values.length; i++) {
                values[i].get(i * 4, buffer);
            }
            glProgramUniform4iv(this.getProgram(), location, buffer);
        }
    }

    /**
     * Sets a matrix in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setMatrix(CharSequence name, Matrix2fc value) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2 * 2);
            value.get(buffer);
            glProgramUniformMatrix2fv(this.getProgram(), location, false, buffer);
        }
    }

    /**
     * Sets a matrix in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setMatrix(CharSequence name, Matrix3fc value) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 3);
            value.get(buffer);
            glProgramUniformMatrix3fv(this.getProgram(), location, false, buffer);
        }
    }

    /**
     * Sets a matrix in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setMatrix(CharSequence name, Matrix3x2fc value) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * 2);
            value.get(buffer);
            glProgramUniformMatrix3x2fv(this.getProgram(), location, false, buffer);
        }
    }

    /**
     * Sets a matrix in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setMatrix(CharSequence name, Matrix4fc value) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.get(buffer);
            glProgramUniformMatrix4fv(this.getProgram(), location, false, buffer);
        }
    }

    /**
     * Sets a matrix in the shader.
     *
     * @param name  The name of the uniform to set
     * @param value The value to set
     */
    default void setMatrix(CharSequence name, Matrix4x3fc value) {
        int location = this.getUniform(name);
        if (location == -1) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 3);
            value.get(buffer);
            glProgramUniformMatrix4x3fv(this.getProgram(), location, false, buffer);
        }
    }
}
