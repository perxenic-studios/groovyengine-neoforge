package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import org.joml.Vector3f;

/**
 * Holds transformation data from keyframe interpolation.
 */
@Deprecated
public record KeyframeTransform(Vector3f position, Vector3f rotation, Vector3f scale) {
}
