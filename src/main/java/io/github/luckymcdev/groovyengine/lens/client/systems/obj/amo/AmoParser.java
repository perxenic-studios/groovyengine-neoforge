package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjParser;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;
import net.minecraft.world.phys.Vec2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmoParser extends ObjParser {
    private List<Vector4i> vertexJoints = new ArrayList<>();
    private List<Vector4f> vertexWeights = new ArrayList<>();
    private List<Joint> joints = new ArrayList<>();
    private Map<String, Animation> animations = new HashMap<>();
    private Animation currentAnimation;

    @Override
    public void parseObjFile(Resource resource) throws IOException {
        // Initialize default object
        currentObjectName = "default";
        currentObject = new AmoObject(currentObjectName);
        objects.put(currentObjectName, currentObject);

        InputStream inputStream = resource.open();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("ao ")) {
                // Animated object definition
                String[] tokens = line.split("\\s+", 2);
                currentObjectName = tokens.length > 1 ? tokens[1] : "unnamed";
                currentObject = new AmoObject(currentObjectName);
                objects.put(currentObjectName, currentObject);

            } else if (line.startsWith("vj ")) {
                // Vertex joints
                String[] tokens = line.split("\\s+");
                int j1 = Integer.parseInt(tokens[1]);
                int j2 = tokens.length > 2 ? Integer.parseInt(tokens[2]) : 0;
                int j3 = tokens.length > 3 ? Integer.parseInt(tokens[3]) : 0;
                int j4 = tokens.length > 4 ? Integer.parseInt(tokens[4]) : 0;
                vertexJoints.add(new Vector4i(j1, j2, j3, j4));

            } else if (line.startsWith("vw ")) {
                // Vertex weights
                String[] tokens = line.split("\\s+");
                float w1 = Float.parseFloat(tokens[1]);
                float w2 = tokens.length > 2 ? Float.parseFloat(tokens[2]) : 0f;
                float w3 = tokens.length > 3 ? Float.parseFloat(tokens[3]) : 0f;
                float w4 = tokens.length > 4 ? Float.parseFloat(tokens[4]) : 0f;
                vertexWeights.add(new Vector4f(w1, w2, w3, w4));

            } else if (line.startsWith("j ")) {
                // Joint definition
                String[] tokens = line.split("\\s+");
                String name = tokens[1];
                int parentIndex = Integer.parseInt(tokens[2]);
                joints.add(new Joint(name, parentIndex));

            } else if (line.startsWith("a ")) {
                // Animation group
                String[] tokens = line.split("\\s+", 2);
                String animName = tokens.length > 1 ? tokens[1] : "unnamed";
                currentAnimation = new Animation(animName);
                animations.put(animName, currentAnimation);

            } else if (line.startsWith("ap ")) {
                // Animate position
                if (currentAnimation != null) {
                    String[] tokens = line.split("\\s+");
                    float timestamp = Float.parseFloat(tokens[1]);
                    int jointIndex = Integer.parseInt(tokens[2]) - 1; // Convert to 0-based
                    float x = Float.parseFloat(tokens[3]);
                    float y = Float.parseFloat(tokens[4]);
                    float z = Float.parseFloat(tokens[5]);
                    currentAnimation.addPositionKeyframe(jointIndex, timestamp, x, y, z);
                }

            } else if (line.startsWith("ar ")) {
                // Animate rotation
                if (currentAnimation != null) {
                    String[] tokens = line.split("\\s+");
                    float timestamp = Float.parseFloat(tokens[1]);
                    int jointIndex = Integer.parseInt(tokens[2]) - 1; // Convert to 0-based
                    float x = Float.parseFloat(tokens[3]);
                    float y = Float.parseFloat(tokens[4]);
                    float z = Float.parseFloat(tokens[5]);
                    float w = Float.parseFloat(tokens[6]);
                    currentAnimation.addRotationKeyframe(jointIndex, timestamp, x, y, z, w);
                }

            } else if (line.startsWith("f ")) {
                // Face with additional joint/weight indices
                AmoFace face = parseAmoFace(line);
                faces.add(face);
                if (currentObject instanceof AmoObject amoObj) {
                    amoObj.addFace(face);
                }

            } else {
                // Handle standard OBJ elements (v, vn, vt, o)
                parseStandardObjLine(line);
            }
        }
        reader.close();
    }

    private void parseStandardObjLine(String line) {
        if (line.startsWith("o ")) {
            String[] tokens = line.split("\\s+", 2);
            currentObjectName = tokens.length > 1 ? tokens[1] : "unnamed";
            currentObject = objects.get(currentObjectName);
            if (currentObject == null) {
                currentObject = new AmoObject(currentObjectName);
                objects.put(currentObjectName, currentObject);
            }

        } else if (line.startsWith("v ")) {
            String[] tokens = line.split("\\s+");
            float x = Float.parseFloat(tokens[1]);
            float y = Float.parseFloat(tokens[2]);
            float z = Float.parseFloat(tokens[3]);
            vertices.add(new Vector3f(x, y, z));

        } else if (line.startsWith("vn ")) {
            String[] tokens = line.split("\\s+");
            float x = Float.parseFloat(tokens[1]);
            float y = Float.parseFloat(tokens[2]);
            float z = Float.parseFloat(tokens[3]);
            normals.add(new Vector3f(x, y, z));

        } else if (line.startsWith("vt ")) {
            String[] tokens = line.split("\\s+");
            float u = Float.parseFloat(tokens[1]);
            float v = Float.parseFloat(tokens[2]);
            uvs.add(new Vec2(u, v));
        }
    }

    private AmoFace parseAmoFace(String line) {
        String[] tokens = line.trim().split("\\s+");
        List<AmoVertex> faceVertices = new ArrayList<>();

        for (int i = 1; i < tokens.length; i++) {
            String[] parts = tokens[i].split("/");

            int vertexIndex = Integer.parseInt(parts[0]) - 1;
            int textureIndex = (parts.length > 1 && !parts[1].isEmpty()) ? Integer.parseInt(parts[1]) - 1 : -1;
            int normalIndex = (parts.length > 2 && !parts[2].isEmpty()) ? Integer.parseInt(parts[2]) - 1 : -1;
            int jointsIndex = (parts.length > 3 && !parts[3].isEmpty()) ? Integer.parseInt(parts[3]) - 1 : -1;
            int weightsIndex = (parts.length > 4 && !parts[4].isEmpty()) ? Integer.parseInt(parts[4]) - 1 : -1;

            Vector3f position = safeGetVertex(vertexIndex);
            Vector3f normal = safeGetNormal(normalIndex);
            Vec2 uv = safeGetUV(textureIndex);
            Vector4i jointIndices = safeGetJoints(jointsIndex);
            Vector4f weights = safeGetWeights(weightsIndex);

            faceVertices.add(new AmoVertex(position, normal, uv, jointIndices, weights));
        }

        return new AmoFace(faceVertices);
    }

    private Vector4i safeGetJoints(int index) {
        if (index >= 0 && index < vertexJoints.size()) {
            return vertexJoints.get(index);
        }
        return new Vector4i(0, 0, 0, 0);
    }

    private Vector4f safeGetWeights(int index) {
        if (index >= 0 && index < vertexWeights.size()) {
            return vertexWeights.get(index);
        }
        return new Vector4f(1, 0, 0, 0);
    }

    public List<Joint> getJoints() {
        return joints;
    }

    public Map<String, Animation> getAnimations() {
        return animations;
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }
}