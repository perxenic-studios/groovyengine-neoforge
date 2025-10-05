package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import io.github.luckymcdev.groovyengine.lens.client.systems.easing.Easing;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform.Transform;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform.TransformBuilder;
import org.joml.Vector3f;

/**
 * Fluent builder for creating animations more easily.
 */
public class AnimationBuilder {
    private final Animation animation;
    private String currentObject;

    public AnimationBuilder(String animationName) {
        this.animation = new Animation(animationName);
    }

    /**
     * Set whether animation loops.
     */
    public AnimationBuilder looping(boolean loop) {
        animation.setLooping(loop);
        return this;
    }

    /**
     * Start adding keyframes for a specific object.
     */
    public AnimationBuilder forObject(String objectName) {
        this.currentObject = objectName;
        return this;
    }

    /**
     * Add a keyframe at the specified time with transform and easing.
     */
    public AnimationBuilder keyframe(float time, Transform transform, Easing easing) {
        if (currentObject == null) {
            throw new IllegalStateException("Must call forObject() before adding keyframes");
        }
        animation.addKeyframe(currentObject, new Keyframe(time, transform.position, transform.rotation, transform.scale, easing));
        return this;
    }

    /**
     * Add a keyframe at the specified time with transform (linear easing).
     */
    public AnimationBuilder keyframe(float time, Transform transform) {
        return keyframe(time, transform, Easing.LINEAR);
    }

    /**
     * Add a keyframe with only position (rotation and scale default, linear easing).
     */
    public AnimationBuilder keyframe(float time, Vector3f position) {
        return keyframe(time,
                TransformBuilder.create()
                        .position(position)
                        .build(),
                Easing.LINEAR
        );
    }

    /**
     * Add a keyframe with only position and easing.
     */
    public AnimationBuilder keyframe(float time, Vector3f position, Easing easing) {
        return keyframe(time,
                TransformBuilder.create()
                        .position(position)
                        .build(),
                easing
        );
    }

    /**
     * Add a keyframe with position and rotation (linear easing).
     */
    public AnimationBuilder keyframe(float time, Vector3f position, Vector3f rotation) {
        return keyframe(time,
                TransformBuilder.create()
                        .position(position)
                        .rotation(rotation)
                        .build(),
                Easing.LINEAR
        );
    }

    /**
     * Add a keyframe with position, rotation and easing.
     */
    public AnimationBuilder keyframe(float time, Vector3f position, Vector3f rotation, Easing easing) {
        return keyframe(time,
                TransformBuilder.create()
                        .position(position)
                        .rotation(rotation)
                        .build(),
                easing
        );
    }

    /**
     * Add a keyframe using individual float values for convenience (linear easing).
     */
    public AnimationBuilder keyframe(float time,
                                     float px, float py, float pz,
                                     float rx, float ry, float rz,
                                     float sx, float sy, float sz) {
        return keyframe(time,
                TransformBuilder.create()
                        .position(px, py, pz)
                        .rotation(rx, ry, rz)
                        .scale(sx, sy, sz)
                        .build(),
                Easing.LINEAR
        );
    }

    /**
     * Add a keyframe using individual float values with easing.
     */
    public AnimationBuilder keyframe(float time,
                                     float px, float py, float pz,
                                     float rx, float ry, float rz,
                                     float sx, float sy, float sz,
                                     Easing easing) {
        return keyframe(time,
                TransformBuilder.create()
                        .position(px, py, pz)
                        .rotation(rx, ry, rz)
                        .scale(sx, sy, sz)
                        .build(),
                easing
        );
    }

    /**
     * Build the animation.
     */
    public Animation build() {
        return animation;
    }

    /**
     * Static factory method for cleaner syntax.
     */
    public static AnimationBuilder create(String name) {
        return new AnimationBuilder(name);
    }
}