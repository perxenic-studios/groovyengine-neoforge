#version 150

in vec4 Position;
in vec2 UV0;

out vec2 texCoord0;

void main() {
    gl_Position = Position;
    texCoord0 = UV0;
}
