package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import io.github.luckymcdev.groovyengine.GE;
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

    public void addPositionKeyframe(int jointIndex, float timestamp, float x, float y, float z) {
        positionKeyframes.computeIfAbsent(jointIndex, k -> new ArrayList<>())
                .add(new PositionKeyframe(timestamp, new Vector3f(x, y, z)));
        duration = Math.max(duration, timestamp);
    }

    public void addRotationKeyframe(int jointIndex, float timestamp, float x, float y, float z, float w) {
        rotationKeyframes.computeIfAbsent(jointIndex, k -> new ArrayList<>())
                .add(new RotationKeyframe(timestamp, new Quaternionf(x, y, z, w)));
        duration = Math.max(duration, timestamp);
    }

    /**
     * Apply animation at a specific time to the joint array.
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

    private void updateJointHierarchy(Joint[] joints) {
        for (int i = 0; i < joints.length; i++) {
            Joint joint = joints[i];
            int parentIndex = joint.getParentIndex();
            Joint parent = (parentIndex >= 0 && parentIndex < joints.length) ? joints[parentIndex] : null;
            joint.updateWorldTransform(parent);
        }
    }

    public String getName() {
        return name;
    }

    public float getDuration() {
        return duration;
    }

    private record PositionKeyframe(float timestamp, Vector3f position) {}
    private record RotationKeyframe(float timestamp, Quaternionf rotation) {}
}