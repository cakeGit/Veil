package foundry.veil.impl.client.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class LevelPerspectiveCamera extends Camera {

    private static final Vector3f EULER_ANGLES = new Vector3f();

    private final Entity dummyCameraEntity;
    private ClientLevel level;

    public LevelPerspectiveCamera() {
        this.dummyCameraEntity = new Entity(EntityType.OCELOT, null) {
            @Override
            protected void defineSynchedData() {
            }

            @Override
            protected void readAdditionalSaveData(CompoundTag var1) {
            }

            @Override
            protected void addAdditionalSaveData(CompoundTag var1) {
            }

            @Override
            public Level level() {
                return LevelPerspectiveCamera.this.level;
            }
        };
    }

    public void setup(Vector3dc position, @Nullable Entity cameraEntity, ClientLevel level, Quaternionfc orientation) {
        this.level = level;
        super.setup(level, cameraEntity != null ? cameraEntity : this.dummyCameraEntity, true, false, 1.0F);
        this.setPosition(position.x(), position.y(), position.z());

        orientation.getEulerAnglesYXZ(EULER_ANGLES);
        super.setRotation((float) (-EULER_ANGLES.y * 180 / Math.PI), (float) (EULER_ANGLES.x * 180 / Math.PI));
        this.rotation().set(orientation);
        this.getLookVector().set(0.0F, 0.0F, 1.0F).rotate(orientation);
        this.getUpVector().set(0.0F, 1.0F, 0.0F).rotate(orientation);
        this.getLeftVector().set(1.0F, 0.0F, 0.0F).rotate(orientation);
    }
}
