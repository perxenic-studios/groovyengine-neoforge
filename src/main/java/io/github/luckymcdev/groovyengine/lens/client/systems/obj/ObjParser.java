package io.github.luckymcdev.groovyengine.lens.client.systems.obj;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.phys.Vec2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjParser {
    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Vec2> uvs = new ArrayList<>();
    public List<Face> faces = new ArrayList<>();

    public void parseObjFile(Resource resource) throws IOException {
        InputStream inputStream = resource.open();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("v ")) {
                String[] tokens = line.split(" ");
                float x = Float.parseFloat(tokens[1]);
                float y = Float.parseFloat(tokens[2]);
                float z = Float.parseFloat(tokens[3]);
                vertices.add(new Vector3f(x, y, z));

            } else if (line.startsWith("vn ")) {
                String[] tokens = line.split(" ");
                float x = Float.parseFloat(tokens[1]);
                float y = Float.parseFloat(tokens[2]);
                float z = Float.parseFloat(tokens[3]);
                normals.add(new Vector3f(x, y, z));

            } else if (line.startsWith("vt ")) {
                String[] tokens = line.split(" ");
                float u = Float.parseFloat(tokens[1]);
                float v = Float.parseFloat(tokens[2]);
                uvs.add(new Vec2(u, v));

            } else if (line.startsWith("f ")) {
                Face face = getFace(line);
                faces.add(face);
            }
        }
        reader.close();
    }

    private @NotNull Face getFace(String line) {
        String[] tokens = line.trim().split("\\s+");
        List<Vertex> faceVertices = new ArrayList<>();

        for (int i = 1; i < tokens.length; i++) {
            String[] parts = tokens[i].split("/");

            int vertexIndex = Integer.parseInt(parts[0]) - 1;
            int textureIndex = (parts.length > 1 && !parts[1].isEmpty()) ? Integer.parseInt(parts[1]) - 1 : -1;
            int normalIndex  = (parts.length > 2 && !parts[2].isEmpty()) ? Integer.parseInt(parts[2]) - 1 : -1;

            Vector3f position = safeGetVertex(vertexIndex);
            Vector3f normal = safeGetNormal(normalIndex);
            Vec2 uv = safeGetUV(textureIndex);

            faceVertices.add(new Vertex(position, normal, uv));
        }

        return new Face(faceVertices);
    }

    private Vector3f safeGetNormal(int index) {
        if (index >= 0 && index < normals.size()) {
            return normals.get(index);
        }
        return new Vector3f(0, 0, 0);
    }

    private Vec2 safeGetUV(int index) {
        if (index >= 0 && index < uvs.size()) {
            return uvs.get(index);
        }
        return new Vec2(0, 0);
    }

    private Vector3f safeGetVertex(int index) {
        if (index >= 0 && index < vertices.size()) {
            return vertices.get(index);
        }
        throw new IllegalArgumentException("Invalid vertex index: " + index);
    }

    public ArrayList<Face> getFaces() {
        return (ArrayList<Face>) faces;
    }
}