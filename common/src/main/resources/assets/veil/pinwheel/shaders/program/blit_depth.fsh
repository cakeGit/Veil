#include veil:deferred_utils

uniform sampler2D DiffuseDepthSampler;

in vec2 texCoord;

out vec4 OutColor;

void main() {
    gl_FragDepth = texture(DiffuseDepthSampler, texCoord).r;
    OutColor = vec4(vec3(depthSampleToWorldDepth(gl_FragDepth)), 1.0);
}
