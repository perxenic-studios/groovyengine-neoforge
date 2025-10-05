package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import org.joml.Vector3f;

/**
 * Holds transformation data from keyframe interpolation.
 */
public class KeyframeTransform {
    public final Vector3f position;
    public final Vector3f rotation;
    public final Vector3f scale;

    public KeyframeTransform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
}
