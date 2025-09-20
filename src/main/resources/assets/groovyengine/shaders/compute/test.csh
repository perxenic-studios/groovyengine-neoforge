#version 430
layout(local_size_x = 128) in;

layout(std430, binding = 0) buffer Data {
    vec4 values[];
};

void main() {
    uint idx = gl_GlobalInvocationID.x;
    values[idx] += vec4(1.0); // simple increment
}
