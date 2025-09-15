#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

in vec2 texCoord;

uniform vec2 InSize;
uniform vec2 OutSize;

out vec4 fragColor;

void main() {
    // Test 1: Check if we can sample from the main texture
    // vec4 color = texture(DiffuseSampler, texCoord);
    // fragColor = vec4(color.rgb * 0.5 + vec3(0.5, 0.0, 0.0), 1.0); // Tint red to indicate we're in shader
    // return;

    // Test 2: Check depth sampler existence
    float depthValue = texture(DepthSampler, texCoord).r;

    // Draw debug information based on depth value
    if (depthValue > 0.001 && depthValue < 0.999) {
        // Valid depth range - show as grayscale
        fragColor = vec4(vec3(depthValue), 1.0);
    } else if (depthValue == 0.0) {
        // Depth is exactly 0 - likely not copied properly
        // Draw red diagonal stripes
        vec2 screenPos = gl_FragCoord.xy / InSize;
        if (mod(screenPos.x + screenPos.y, 0.1) < 0.05) {
            fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Red stripes = depth copy failed
        } else {
            fragColor = vec4(0.5, 0.0, 0.0, 1.0);
        }
    } else if (depthValue == 1.0) {
        // Depth is exactly 1.0 - might be cleared but not written to
        // Draw blue grid
        vec2 grid = fract(texCoord * 20.0);
        if (grid.x < 0.1 || grid.y < 0.1) {
            fragColor = vec4(0.0, 0.0, 1.0, 1.0); // Blue grid = depth cleared but not written
        } else {
            fragColor = vec4(0.0, 0.0, 0.5, 1.0);
        }
    } else {
        // Some other value - show as green
        fragColor = vec4(0.0, 1.0, 0.0, 1.0); // Green = unexpected depth value
    }
}