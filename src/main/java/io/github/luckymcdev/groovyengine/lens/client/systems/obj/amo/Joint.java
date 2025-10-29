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

package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Represents a joint/bone in the animation skeleton.
 */
public class Joint {
    private final String name;
    private final int parentIndex;
    private final Vector3f position;
    private final Quaternionf rotation;
    private final Matrix4f localTransform;
    private final Matrix4f worldTransform;

    public Joint(String name, int parentIndex) {
        this.name = name;
        this.parentIndex = parentIndex;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Quaternionf();
        this.localTransform = new Matrix4f().identity();
        this.worldTransform = new Matrix4f().identity();
    }

    /**
     * Sets the position of the joint.
     * The position is given by the x, y, and z values.
     * The local transform of the joint is updated after setting the position.
     *
     * @param x The x component of the position.
     * @param y The y component of the position.
     * @param z The z component of the position.
     */
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        updateLocalTransform();
    }

    /**
     * Sets the rotation of the joint.
     * The rotation is given by the x, y, z, and w values of a quaternion.
     * The local transform of the joint is updated after setting the rotation.
     *
     * @param x The x component of the quaternion.
     * @param y The y component of the quaternion.
     * @param z The z component of the quaternion.
     * @param w The w component of the quaternion.
     */
    public void setRotation(float x, float y, float z, float w) {
        this.rotation.set(x, y, z, w);
        updateLocalTransform();
    }

    /**
     * Updates the local transform of the joint based on its position and rotation.
     * The local transform is set to the identity matrix, then translated by the joint's position,
     * and then rotated by the joint's rotation.
     */
    private void updateLocalTransform() {
        localTransform.identity()
                .translate(position)
                .rotate(rotation);
    }

    /**
     * Update world transform based on parent joint.
     */
    public void updateWorldTransform(Joint parent) {
        if (parent != null) {
            worldTransform.set(parent.worldTransform).mul(localTransform);
        } else {
            worldTransform.set(localTransform);
        }
    }

    /**
     * Transform a point by this joint's world transform.
     */
    public Vector3f transformPoint(Vector3f point) {
        Vector3f result = new Vector3f();
        worldTransform.transformPosition(point, result);
        return result;
    }

    /**
     * Returns the name of the joint.
     *
     * @return The name of the joint.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the index of the parent joint in the joints array.
     * If the joint has no parent, the value is -1.
     *
     * @return The index of the parent joint.
     */
    public int getParentIndex() {
        return parentIndex;
    }

    /**
     * Returns the position of this joint.
     *
     * @return The position of this joint.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Returns the rotation of this joint.
     * <p>
     * The rotation is given as a quaternion (x, y, z, w) and is relative to the parent joint.
     * If the joint has no parent, the rotation is relative to the world coordinate system.
     *
     * @return The rotation of this joint.
     */
    public Quaternionf getRotation() {
        return rotation;
    }

    /**
     * Returns the world transform of this joint.
     *
     * @return The world transform of this joint.
     */
    public Matrix4f getWorldTransform() {
        return worldTransform;
    }
}