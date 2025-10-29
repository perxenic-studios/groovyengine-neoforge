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

package io.github.luckymcdev.groovyengine.lens.client.systems.obj;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class ObjModel {
    public ArrayList<Face> faces = new ArrayList<>();
    public Map<String, ObjObject> objects;
    public ResourceLocation modelLocation;
    protected ObjParser objParser;

    public ObjModel(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
        this.objParser = new ObjParser();
    }

    /**
     * Loads the model from the given resource location.
     * This method will throw a RuntimeException if the resource is not found.
     * It will also log the loaded model data.
     *
     * @throws RuntimeException if the resource is not found.
     */
    public void loadModel() {
        GE.LENS_LOG.info("Loading model: {}", modelLocation);
        String modID = this.modelLocation.getNamespace();
        String fileName = this.modelLocation.getPath();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(modID, "obj/" + fileName + ".obj");
        Optional<Resource> resourceO = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        if (resourceO.isEmpty()) {
            throw new RuntimeException("Resource not found: " + resourceLocation);
        }
        Resource resource = resourceO.get();
        try {
            this.objParser.parseObjFile(resource);
            this.faces = objParser.getFaces();
            this.objects = objParser.getObjects();
            GE.LENS_LOG.info("Loaded {} objects from {}", objects.size(), modelLocation);
            objects.keySet().forEach(name ->
                    GE.LENS_LOG.info("  Object: {} with {} faces", name, objects.get(name).getFaces().size())
            );
        } catch (IOException e) {
            GE.LENS_LOG.error("Error parsing OBJ file: {}", resourceLocation, e);
        }
    }

    /**
     * Renders the entire model as one piece.
     *
     * @param poseStack   The pose stack.
     * @param renderType  The render type.
     * @param packedLight The packed light.
     */
    public void renderModel(PoseStack poseStack, RenderType renderType, int packedLight) {
        faces.forEach(face -> face.renderFace(poseStack, renderType, packedLight));
    }

    /**
     * Renders the entire model as one piece.
     *
     * @param poseStack   The pose stack.
     * @param material    The material.
     * @param packedLight The packed light.
     */
    public void renderModel(PoseStack poseStack, Material material, int packedLight) {
        faces.forEach(face -> face.renderFace(poseStack, material.renderType(), packedLight));
    }

    /**
     * Renders model with object transformations applied (for animation).
     *
     * @param poseStack   The pose stack.
     * @param renderType  The render type.
     * @param packedLight The packed light.
     */
    @Deprecated
    public void renderModelAnimated(PoseStack poseStack, RenderType renderType, int packedLight) {
        objects.values().forEach(obj -> obj.render(poseStack, renderType, packedLight));
    }

    /**
     * Renders model with object transformations applied (for animation).
     *
     * @param poseStack   The pose stack.
     * @param material    The material.
     * @param packedLight The packed light.
     */
    @Deprecated
    public void renderModelAnimated(PoseStack poseStack, Material material, int packedLight) {
        objects.values().forEach(obj -> obj.render(poseStack, material.renderType(), packedLight));
    }

    /**
     * Gets an object by name for animation control.
     *
     * @param name The object name from the OBJ file.
     * @return The ObjObject, or null if not found.
     */
    public ObjObject getObject(String name) {
        return objects.get(name);
    }

    /**
     * Gets all objects in the model.
     *
     * @return Map of object names to ObjObject instances.
     */
    public Map<String, ObjObject> getObjects() {
        return objects;
    }
}