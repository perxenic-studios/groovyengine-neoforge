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

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        updateLocalTransform();
    }

    public void setRotation(float x, float y, float z, float w) {
        this.rotation.set(x, y, z, w);
        updateLocalTransform();
    }

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

    public String getName() {
        return name;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Matrix4f getWorldTransform() {
        return worldTransform;
    }
}