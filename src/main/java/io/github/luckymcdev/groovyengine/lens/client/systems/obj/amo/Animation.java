package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

/**
 * Represents a named animation with keyframes for position and rotation.
 */
public class Animation {
    private final String name;
    private final Map<Integer, List<PositionKeyframe>> positionKeyframes;
    private final Map<Integer, List<RotationKeyframe>> rotationKeyframes;
    private float duration;

    public Animation(String name) {
        this.name = name;
        this.positionKeyframes = new HashMap<>();
        this.rotationKeyframes = new HashMap<>();
        this.duration = 0;
    }

    /**
     * Add a keyframe to the animation for the joint at the specified index.
     * The keyframe is for the position of the joint at the given timestamp.
     * The position is given by the x, y, and z values.
     * The duration of the animation is updated to be the maximum of its current value and the given timestamp.
     *
     * @param jointIndex The index of the joint to add the keyframe for.
     * @param timestamp The timestamp of the keyframe in seconds.
     * @param x The x component of the position at the keyframe.
     * @param y The y component of the position at the keyframe.
     * @param z The z component of the position at the keyframe.
     */
    public void addPositionKeyframe(int jointIndex, float timestamp, float x, float y, float z) {
        positionKeyframes.computeIfAbsent(jointIndex, k -> new ArrayList<>())
                .add(new PositionKeyframe(timestamp, new Vector3f(x, y, z)));
        duration = Math.max(duration, timestamp);
    }

    /**
     * Add a keyframe to the animation for the joint at the specified index.
     * The keyframe is for the rotation of the joint at the given timestamp.
     * The rotation is given by the x, y, z, and w values of a quaternion.
     * The duration of the animation is updated to be the maximum of its current value and the given timestamp.
     *
     * @param jointIndex The index of the joint to add the keyframe for.
     * @param timestamp The timestamp of the keyframe in seconds.
     * @param x The x component of the quaternion at the keyframe.
     * @param y The y component of the quaternion at the keyframe.
     * @param z The z component of the quaternion at the keyframe.
     * @param w The w component of the quaternion at the keyframe.
     */
    public void addRotationKeyframe(int jointIndex, float timestamp, float x, float y, float z, float w) {
        rotationKeyframes.computeIfAbsent(jointIndex, k -> new ArrayList<>())
                .add(new RotationKeyframe(timestamp, new Quaternionf(x, y, z, w)));
        duration = Math.max(duration, timestamp);
    }


    /**
     * Applies the animation at the given time to the given joints.
     * The function wraps the time if it exceeds the animation duration.
     * It then applies the position and rotation keyframes at the given time
     * to the corresponding joints, and updates the joint hierarchy transforms.
     *
     * @param time The time at which to apply the animation.
     * @param joints The joints to apply the animation to.
     */
    public void applyAtTime(float time, Joint[] joints) {

        // Wrap time if it exceeds duration
        if (duration > 0) {
            time = time % duration;
        }

        // Apply position keyframes
        for (Map.Entry<Integer, List<PositionKeyframe>> entry : positionKeyframes.entrySet()) {
            int jointIndex = entry.getKey();
            if (jointIndex >= 0 && jointIndex < joints.length) {
                Vector3f position = interpolatePosition(entry.getValue(), time);
                joints[jointIndex].setPosition(position.x, position.y, position.z);
            }
        }

        // Apply rotation keyframes
        for (Map.Entry<Integer, List<RotationKeyframe>> entry : rotationKeyframes.entrySet()) {
            int jointIndex = entry.getKey();
            if (jointIndex >= 0 && jointIndex < joints.length) {
                Quaternionf rotation = interpolateRotation(entry.getValue(), time);
                joints[jointIndex].setRotation(rotation.x, rotation.y, rotation.z, rotation.w);
            }
        }

        // Update joint hierarchy transforms
        updateJointHierarchy(joints);
    }

    /**
     * Interpolate a position keyframe at the given time.
     * The function linearly interpolates between the two nearest keyframes.
     * If the time is before the first keyframe, it returns the first keyframe's position.
     * If the time is after the last keyframe, it returns the last keyframe's position.
     * If the time is exactly on a keyframe, it returns that keyframe's position.
     *
     * @param keyframes The list of position keyframes to interpolate.
     * @param time The time at which to interpolate the position.
     * @return The interpolated position at the given time.
     */
    private Vector3f interpolatePosition(List<PositionKeyframe> keyframes, float time) {
        if (keyframes.isEmpty()) return new Vector3f();
        if (keyframes.size() == 1) return new Vector3f(keyframes.get(0).position);

        // Sort keyframes by timestamp
        keyframes.sort(Comparator.comparing(k -> k.timestamp));

        // Find surrounding keyframes
        PositionKeyframe prev = keyframes.get(0);
        PositionKeyframe next = keyframes.get(keyframes.size() - 1);

        for (int i = 0; i < keyframes.size() - 1; i++) {
            if (keyframes.get(i).timestamp <= time && keyframes.get(i + 1).timestamp >= time) {
                prev = keyframes.get(i);
                next = keyframes.get(i + 1);
                break;
            }
        }

        // Linear interpolation
        if (prev == next) return new Vector3f(prev.position);

        float t = (time - prev.timestamp) / (next.timestamp - prev.timestamp);
        return new Vector3f().set(prev.position).lerp(next.position, t);
    }

    /**
     * Interpolate a rotation keyframe at the given time.
     * The function uses spherical linear interpolation (SLERP) to smoothly transition between the two nearest keyframes.
     * If the time is before the first keyframe, it returns the first keyframe's rotation.
     * If the time is after the last keyframe, it returns the last keyframe's rotation.
     * If the time is exactly on a keyframe, it returns that keyframe's rotation.
     *
     * @param keyframes The list of rotation keyframes to interpolate.
     * @param time The time at which to interpolate the rotation.
     * @return The interpolated rotation at the given time.
     */
    private Quaternionf interpolateRotation(List<RotationKeyframe> keyframes, float time) {
        if (keyframes.isEmpty()) return new Quaternionf();
        if (keyframes.size() == 1) return new Quaternionf(keyframes.get(0).rotation);

        // Sort keyframes by timestamp
        keyframes.sort(Comparator.comparing(k -> k.timestamp));

        // Find surrounding keyframes
        RotationKeyframe prev = keyframes.get(0);
        RotationKeyframe next = keyframes.get(keyframes.size() - 1);

        for (int i = 0; i < keyframes.size() - 1; i++) {
            if (keyframes.get(i).timestamp <= time && keyframes.get(i + 1).timestamp >= time) {
                prev = keyframes.get(i);
                next = keyframes.get(i + 1);
                break;
            }
        }

        // Spherical linear interpolation (SLERP)
        if (prev == next) return new Quaternionf(prev.rotation);

        float t = (time - prev.timestamp) / (next.timestamp - prev.timestamp);
        return new Quaternionf().set(prev.rotation).slerp(next.rotation, t);
    }

    /**
     * Updates the world transform of each joint in the hierarchy.
     * It iterates through the list of joints and updates each joint's world transform based on its parent joint.
     * If a joint does not have a parent, its world transform is set to its local transform.
     *
     * @param joints The list of joints to update.
     */
    private void updateJointHierarchy(Joint[] joints) {
        for (int i = 0; i < joints.length; i++) {
            Joint joint = joints[i];
            int parentIndex = joint.getParentIndex();
            Joint parent = (parentIndex >= 0 && parentIndex < joints.length) ? joints[parentIndex] : null;
            joint.updateWorldTransform(parent);
        }
    }

    /**
     * Returns the name of the animation.
     * @return The name of the animation.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the duration of the animation in seconds.
     *
     * @return The duration of the animation in seconds.
     */
    public float getDuration() {
        return duration;
    }

    /**
     * A record for a Position keyframe
     * @param timestamp the Timestamp of the Keyframe
     * @param position the Position of the Keyframe
     */
    private record PositionKeyframe(float timestamp, Vector3f position) {
    }

    /**
     * A record for a Rotation keyframe
     * @param timestamp the Timestamp of the Keyframe
     * @param rotation the Rotation of the Keyframe
     */
    private record RotationKeyframe(float timestamp, Quaternionf rotation) {
    }
}