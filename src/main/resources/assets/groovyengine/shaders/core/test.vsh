#version 150

in vec4 Position;
in vec2 UV0;

out vec2 texCoord0;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * Position;
    texCoord0 = UV0;
}