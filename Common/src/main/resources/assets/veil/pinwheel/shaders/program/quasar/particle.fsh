#include veil:fog

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = linear_fog(texture(Sampler0, texCoord0) * vertexColor * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
	fragColor.rgb = vec3(1, 0, 1);
}
