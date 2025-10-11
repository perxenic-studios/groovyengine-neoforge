package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Animation track for a single object.
 */
@Deprecated
public class AnimationTrack {
    private final String objectName;
    private final List<Keyframe> keyframes;

    public AnimationTrack(String objectName) {
        this.objectName = objectName;
        this.keyframes = new ArrayList<>();
    }

    public void addKeyframe(Keyframe keyframe) {
        keyframes.add(keyframe);
        // Keep keyframes sorted by time
        keyframes.sort(Comparator.comparing(Keyframe::getTime));
    }

    public String getObjectName() {
        return objectName;
    }

    public List<Keyframe> getKeyframes() {
        return keyframes;
    }

    /**
     * Get interpolated transform at given time.
     */
    public KeyframeTransform getTransformAtTime(float time) {
        if (keyframes.isEmpty()) {
            return new KeyframeTransform(
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 0, 0),
                    new Vector3f(1, 1, 1)
            );
        }

        // Find surrounding keyframes
        Keyframe before = null;
        Keyframe after = null;

        for (int i = 0; i < keyframes.size(); i++) {
            Keyframe kf = keyframes.get(i);
            if (kf.getTime() <= time) {
                before = kf;
            }
            if (kf.getTime() >= time && after == null) {
                after = kf;
                break;
            }
        }

        // If time is before first keyframe
        if (before == null) {
            return new KeyframeTransform(
                    keyframes.get(0).getPosition(),
                    keyframes.get(0).getRotation(),
                    keyframes.get(0).getScale()
            );
        }

        // If time is after last keyframe
        if (after == null) {
            return new KeyframeTransform(
                    before.getPosition(),
                    before.getRotation(),
                    before.getScale()
            );
        }

        // If exactly on a keyframe
        if (before == after) {
            return new KeyframeTransform(
                    before.getPosition(),
                    before.getRotation(),
                    before.getScale()
            );
        }

        // Calculate linear interpolation factor
        float linearT = (time - before.getTime()) / (after.getTime() - before.getTime());

        // Apply easing function from the 'after' keyframe
        // The easing is applied TO the target keyframe, so we use after.getEasing()
        float easedT = after.getEasing().ease(linearT, 0f, 1f, 1f);

        // Interpolate between keyframes using the eased factor
        Vector3f position = new Vector3f();
        before.getPosition().lerp(after.getPosition(), easedT, position);

        Vector3f rotation = new Vector3f();
        before.getRotation().lerp(after.getRotation(), easedT, rotation);

        Vector3f scale = new Vector3f();
        before.getScale().lerp(after.getScale(), easedT, scale);

        return new KeyframeTransform(position, rotation, scale);
    }
}