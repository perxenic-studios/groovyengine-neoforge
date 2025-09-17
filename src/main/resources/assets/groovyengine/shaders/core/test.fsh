#version 150

in vec2 texCoord0;
out vec4 fragColor;

uniform sampler2D DiffuseSampler;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord0);
    fragColor = color;
}
