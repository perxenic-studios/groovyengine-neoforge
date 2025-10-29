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