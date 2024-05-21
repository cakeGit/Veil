package foundry.veil.api.quasar.emitters.module.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.deferred.light.PointLight;
import foundry.veil.api.quasar.data.module.init.LightModuleData;
import foundry.veil.api.quasar.emitters.module.RenderParticleModule;
import foundry.veil.api.quasar.particle.QuasarParticle;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class StaticLightModule implements RenderParticleModule {

    private final Vector4fc color;
    private final float brightness;
    private final float radius;

    private PointLight light;
    private boolean enabled;

    public StaticLightModule(LightModuleData data) {
        this(data.color().getColor(0.0F), data.brightness().getConstant(), data.radius().getConstant());
    }

    public StaticLightModule(Vector4fc color, float brightness, float radius) {
        this.color = new Vector4f(color);
        this.brightness = brightness * this.color.w();
        this.radius = radius;
        this.light = null;
    }

    public boolean isVisible() {
        return this.color.lengthSquared() < 0.1 && this.brightness < 0.1;
    }

    @Override
    public void render(QuasarParticle particle, float partialTicks) {
        VeilDeferredRenderer deferredRenderer = VeilRenderSystem.renderer().getDeferredRenderer();
        this.enabled = deferredRenderer.isEnabled();
        if (!this.enabled) {
            this.onRemove();
            return;
        }

        if (this.light == null) {
            this.light = new PointLight()
                    .setColor(this.color.x(), this.color.y(), this.color.z())
                    .setBrightness(this.brightness)
                    .setRadius(this.radius);
            deferredRenderer.getLightRenderer().addLight(this.light);
        }

        this.light.setPosition(particle.getRenderData().getRenderPosition());
    }

    @Override
    public void onRemove() {
        if (this.light != null) {
            VeilDeferredRenderer deferredRenderer = VeilRenderSystem.renderer().getDeferredRenderer();
            if (deferredRenderer.isEnabled()) {
                deferredRenderer.getLightRenderer().removeLight(this.light);
            }
            this.light = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return this.enabled || VeilRenderSystem.renderer().getDeferredRenderer().isEnabled();
    }
}