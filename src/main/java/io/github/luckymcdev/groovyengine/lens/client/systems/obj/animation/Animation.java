/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import java.util.HashMap;
import java.util.Map;

/**
 * Complete animation with multiple tracks.
 */
@Deprecated
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
        if (keyframe.time() > duration) {
            duration = keyframe.time();
        }
    }

    public String getName() {
        return name;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public Map<String, AnimationTrack> getTracks() {
        return tracks;
    }

    /**
     * Get all transforms for this animation at the given time.
     *
     * @param time the time to get the transformations at
     * @return the transformations.
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
