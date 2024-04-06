package foundry.veil.api.client.render.framebuffer;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class AdvancedFboTextureWrapperAttachment extends AdvancedFboTextureAttachment {

    private final int textureId;

    public AdvancedFboTextureWrapperAttachment(int textureId, int attachmentType, int width, int height) {
        super(attachmentType, 0, 0, 0, width, height, 0, false, null);
        this.textureId = textureId;
    }

    @Override
    public void create() {
    }

    @Override
    public AdvancedFboTextureWrapperAttachment createCopy() {
        return new AdvancedFboTextureWrapperAttachment(this.textureId, this.getAttachmentType(), this.getWidth(), this.getHeight());
    }

    @Override
    public void releaseId() {
    }

    @Override
    public int getId() {
        return this.textureId;
    }
}
