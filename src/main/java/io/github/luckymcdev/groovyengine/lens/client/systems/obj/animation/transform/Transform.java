package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform;

import org.joml.Vector3f;

@Deprecated
public record Transform(Vector3f position, Vector3f rotation, Vector3f scale) {

    public static TransformBuilder builder() {
        return new TransformBuilder();
    }

    public static Transform identity() {
        return new Transform(
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1)
        );
    }
}