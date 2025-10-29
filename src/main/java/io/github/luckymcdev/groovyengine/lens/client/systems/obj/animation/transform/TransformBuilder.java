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

package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform;

import org.joml.Vector3f;

@Deprecated
public class TransformBuilder {
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);

    public static TransformBuilder create() {
        return new TransformBuilder();
    }

    public static Transform identity() {
        return Transform.identity();
    }

    public TransformBuilder position(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
        return this;
    }

    public TransformBuilder position(Vector3f position) {
        this.position = new Vector3f(position);
        return this;
    }

    public TransformBuilder rotation(float x, float y, float z) {
        this.rotation = new Vector3f(x, y, z);
        return this;
    }

    public TransformBuilder rotation(Vector3f rotation) {
        this.rotation = new Vector3f(rotation);
        return this;
    }

    public TransformBuilder scale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public TransformBuilder scale(float uniform) {
        this.scale = new Vector3f(uniform, uniform, uniform);
        return this;
    }

    public TransformBuilder scale(Vector3f scale) {
        this.scale = new Vector3f(scale);
        return this;
    }

    public Transform build() {
        return new Transform(position, rotation, scale);
    }
}