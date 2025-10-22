package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import io.github.luckymcdev.groovyengine.lens.client.systems.easing.Easing;
import org.joml.Vector3f;

/**
 * Represents a single keyframe in an animation.
 *
 * @param time   Time in seconds or ticks
 * @param easing Easing function to use when interpolating TO this keyframe
 */
@Deprecated
public record Keyframe(float time, Vector3f position, Vector3f rotation, Vector3f scale, Easing easing) {
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

    @Override
    public Vector3f position() {
        return new Vector3f(position);
    }

    @Override
    public Vector3f rotation() {
        return new Vector3f(rotation);
    }

    @Override
    public Vector3f scale() {
        return new Vector3f(scale);
    }
}