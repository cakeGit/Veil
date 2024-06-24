package foundry.veil.api.client.necromancer;

import com.mojang.blaze3d.vertex.BufferBuilder;

// todo: make this more generic? just as a mesh class for everyone to use in veil.
public interface Mesh {

    BufferBuilder.RenderedBuffer createMesh();
}
