package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform;

import org.joml.Vector3f;

public class Transform {
    public final Vector3f position;
    public final Vector3f rotation;
    public final Vector3f scale;

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

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