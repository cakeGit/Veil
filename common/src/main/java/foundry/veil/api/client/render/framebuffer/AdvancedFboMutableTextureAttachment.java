package foundry.veil.api.client.render.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_ATTACHMENT;

@ApiStatus.Internal
public class AdvancedFboMutableTextureAttachment extends AdvancedFboTextureAttachment {

    private int textureTarget;
    private int textureId;
    private int width;
    private int height;

    public AdvancedFboMutableTextureAttachment(int textureId, int textureTarget, int attachmentType, int width, int height) {
        super(attachmentType, 0, 0, 0, 0, 0, 0, false, null);
        this.setTexture(textureTarget, textureId, width, height);
    }

    @Override
    public void attach(int attachment) {
        int attachmentType = this.getAttachmentType();
        Validate.isTrue(attachmentType < GL_DEPTH_ATTACHMENT || attachment == 0, "Only one depth buffer attachment is supported.");
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER,
                attachmentType + attachment,
                this.textureTarget,
                this.getId(),
                0); // Only draw into the first level
    }

    @Override
    public void create() {
    }

    @Override
    public AdvancedFboMutableTextureAttachment clone() {
        return new AdvancedFboMutableTextureAttachment(this.textureId, this.textureTarget, this.getAttachmentType(), this.getWidth(), this.getHeight());
    }

    @Override
    public void releaseId() {
    }

    @Override
    public int getId() {
        return this.textureId;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public boolean setTexture(int textureTarget, int textureId, int width, int height) {
        if (this.textureTarget == textureTarget && this.textureId == textureId && this.width == width && this.height == height) {
            return false;
        }

        this.textureTarget = textureTarget;
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        return true;
    }
}
