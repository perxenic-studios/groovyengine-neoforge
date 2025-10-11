package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import io.github.luckymcdev.groovyengine.lens.client.systems.easing.Easing;
import org.joml.Vector3f;

/**
 * Represents a single keyframe in an animation.
 */
@Deprecated
public class Keyframe {
    private final float time; // Time in seconds or ticks
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;
    private final Easing easing; // Easing function to use when interpolating TO this keyframe

    public Keyframe(float time, Vector3f position, Vector3f rotation, Vector3f scale, Easing easing) {
        this.time = time;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(scale);
        this.easing = easing != null ? easing : Easing.LINEAR;
    }

    public Keyframe(float time, Vector3f position, Vector3f rotation, Vector3f scale) {
        this(time, position, rotation, scale, Easing.LINEAR);
    }

    public float getTime() {
        return time;
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotation);
    }

    public Vector3f getScale() {
        return new Vector3f(scale);
    }

    public Easing getEasing() {
        return easing;
    }
}