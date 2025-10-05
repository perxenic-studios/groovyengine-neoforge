package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import java.util.HashMap;
import java.util.Map;

/**
 * Complete animation with multiple tracks.
 */
public class Animation {
    private final String name;
    private final Map<String, AnimationTrack> tracks;
    private float duration;
    private boolean looping;

    public Animation(String name) {
        this.name = name;
        this.tracks = new HashMap<>();
        this.duration = 0;
        this.looping = false;
    }

    public void addKeyframe(String objectName, Keyframe keyframe) {
        AnimationTrack track = tracks.computeIfAbsent(objectName, AnimationTrack::new);
        track.addKeyframe(keyframe);

        // Update duration
        if (keyframe.getTime() > duration) {
            duration = keyframe.getTime();
        }
    }

    public String getName() {
        return name;
    }

    public float getDuration() {
        return duration;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public boolean isLooping() {
        return looping;
    }

    public Map<String, AnimationTrack> getTracks() {
        return tracks;
    }

    /**
     * Get all transforms for this animation at the given time.
     */
    public Map<String, KeyframeTransform> getTransformsAtTime(float time) {
        Map<String, KeyframeTransform> transforms = new HashMap<>();

        float animTime = looping && duration > 0 ? time % duration : time;

        for (Map.Entry<String, AnimationTrack> entry : tracks.entrySet()) {
            transforms.put(entry.getKey(), entry.getValue().getTransformAtTime(animTime));
        }

        return transforms;
    }
}
