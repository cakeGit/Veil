package foundry.veil.api.quasar.emitters.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.RandomSource;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import java.util.Locale;

public interface EmitterShape {

    Vector3d getPoint(RandomSource randomSource, Vector3fc dimensions, Vector3fc rotation, Vector3dc position, boolean fromSurface);

    void renderShape(PoseStack stack, VertexConsumer consumer, Vector3fc dimensions, Vector3fc rotation);

    enum Shape implements EmitterShape {
        POINT(new Point()),
        HEMISPHERE(new Hemisphere()),
        CYLINDER(new Cylinder()),
        SPHERE(new Sphere()),
        CUBE(new Cube()),
        TORUS(new Torus()),
        DISC(new Disc()),
        PLANE(new Plane());

        public static final Codec<Shape> CODEC = Codec.STRING.flatXmap(name -> {
            for (Shape value : Shape.values()) {
                if (value.name().equalsIgnoreCase(name)) {
                    return DataResult.success(value);
                }
            }
            return DataResult.error(() -> "Unknown shape: " + name, POINT);
        }, shape -> DataResult.success(shape.name().toLowerCase(Locale.ROOT)));
        private final EmitterShape shape;

        Shape(EmitterShape shape) {
            this.shape = shape;
        }

        @Override
        public Vector3d getPoint(RandomSource randomSource, Vector3fc dimensions, Vector3fc rotation, Vector3dc position, boolean fromSurface) {
            return this.shape.getPoint(randomSource, dimensions, rotation, position, fromSurface);
        }

        @Override
        public void renderShape(PoseStack stack, VertexConsumer consumer, Vector3fc dimensions, Vector3fc rotation) {
            this.shape.renderShape(stack, consumer, dimensions, rotation);
        }
    }
}
