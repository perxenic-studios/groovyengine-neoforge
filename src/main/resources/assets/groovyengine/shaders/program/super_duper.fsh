#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    // Create wave distortion first
    float wave = sin(texCoord.y * 20.0) * 0.01;
    vec2 distorted = clamp(texCoord + vec2(wave, 0.0), 0.0, 1.0);

    // Apply RGB shift to the distorted coordinates
    float shiftAmount = 0.005;
    float r = texture(DiffuseSampler, clamp(distorted + vec2(shiftAmount, 0.0), 0.0, 1.0)).r;
    float g = texture(DiffuseSampler, distorted).g;
    float b = texture(DiffuseSampler, clamp(distorted - vec2(shiftAmount, 0.0), 0.0, 1.0)).b;

    fragColor = vec4(r, g, b, 1.0);
}