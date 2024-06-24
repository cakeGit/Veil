package foundry.veil.api.client.necromancer;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class Skin {
    Map<String, Mesh> boneToMesh;
    ResourceLocation shader;
    ResourceLocation texture;

    // todo: this.
    // instancing? no idea how to set that up hahaha
    // it'll be cool tho
    public void render(Skeleton skeleton) {}
}
