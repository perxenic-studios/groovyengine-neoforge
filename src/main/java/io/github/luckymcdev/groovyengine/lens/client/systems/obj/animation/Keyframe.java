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
 * A keyframe defines the state of an object at a specific point in time.
 * This class is deprecated and will be removed in a future version.
 *
 * @param time     Time in seconds or ticks at which this keyframe is located.
 * @param position The position of the object at this keyframe.
 * @param rotation The rotation of the object at this keyframe, represented as Euler angles in degrees.
 * @param scale    The scale of the object at this keyframe.
 * @param easing   Easing function to use when interpolating TO this keyframe from the previous one.
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
