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

package io.github.luckymcdev.groovyengine.lens.systems.obj.amo;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.rendering.material.Material;
import io.github.luckymcdev.groovyengine.lens.systems.obj.ObjModel;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Model class for Animated OBJ files with skeletal animation support.
 */
public class AmoModel extends ObjModel {
    private static final Minecraft mc = Minecraft.getInstance();
    private Joint[] joints;
    private Map<String, Animation> animations;
    private Animation currentAnimation;
    private float animationTime;
    private boolean isPlaying;

    /**
     * Creates an AmoModel via a .amo File.
     * @param modelLocation the location of the .amo file
     */
    public AmoModel(ResourceLocation modelLocation) {
        super(modelLocation);
        this.objParser = new AmoParser();
        this.animationTime = 0;
        this.isPlaying = false;
    }

    /**
     * Loads the animated model from the resource location.
     *
     * @throws RuntimeException if the resource is not found.
     * @implNote This method loads the animated model from the resource location.
     * It first checks if the resource is present, and if so, it parses the OBJ file
     * and loads the model data. Finally, it logs the loaded model data.
     */
    @Override
    public void loadModel() {
        GE.LENS_LOG.info("Loading animated model: {}", modelLocation);
        String modID = this.modelLocation.getNamespace();
        String fileName = this.modelLocation.getPath();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(modID, "obj/" + fileName + ".amo");
        Optional<Resource> resourceO = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        if (resourceO.isEmpty()) {
            throw new ResourceLocationException("Resource not found: " + resourceLocation);
        }
        Resource resource = resourceO.get();
        try {
            AmoParser amoParser = (AmoParser) this.objParser;
            amoParser.parseObjFile(resource);
            this.faces = amoParser.getFaces();
            this.objects = amoParser.getObjects();

            // Load animation data
            this.joints = amoParser.getJoints().toArray(new Joint[0]);
            this.animations = amoParser.getAnimations();

            GE.LENS_LOG.info("Loaded {} objects and {} joints from {}",
                    objects.size(), joints.length, modelLocation);
            GE.LENS_LOG.info("Available animations: {}", animations.keySet());

            objects.keySet().forEach(name ->
                    GE.LENS_LOG.info("  Object: {} with {} faces", name, objects.get(name).getFaces().size())
            );
        } catch (IOException e) {
            GE.LENS_LOG.error("Error parsing AMO file: {}", resourceLocation, e);
        }
    }

    /**
     * Update animation time (call this in your tick/update method).
     *
     * @param deltaTime Time since last frame in seconds.
     */
    public void updateAnimation(float deltaTime) {
        if (isPlaying && currentAnimation != null && !mc.isPaused()) {
            animationTime += deltaTime;
            currentAnimation.applyAtTime(animationTime, joints);
        }
    }

    /**
     * Play a specific animation by name.
     *
     * @param animationName The name of the animation to play.
     */
    public void playAnimation(String animationName) {
        Animation anim = animations.get(animationName);
        if (anim != null) {
            currentAnimation = anim;
            animationTime = 0;
            isPlaying = true;
            GE.LENS_LOG.info("Playing animation: {}", animationName);
        } else {
            GE.LENS_LOG.warn("Animation not found: {}", animationName);
        }
    }

    /**
     * Stop the current animation.
     */
    public void stopAnimation() {
        isPlaying = false;
    }

    /**
     * Pause the current animation.
     */
    public void pauseAnimation() {
        isPlaying = false;
    }

    /**
     * Resume the current animation.
     */
    public void resumeAnimation() {
        isPlaying = true;
    }

    /**
     * Render model with skeletal animation.
     *
     * @param poseStack   The pose stack.
     * @param renderType  The render type.
     * @param packedLight The packed light.
     */
    public void renderAnimated(PoseStack poseStack, RenderType renderType, int packedLight) {
        if (objects == null) {
            GE.LENS_LOG.warn("Attempted to render AMO model before it was loaded: {}", modelLocation);
            return;
        }

        List<AmoObject> amoObjects = objects.values().stream()
                .filter(AmoObject.class::isInstance)
                .map(o -> (AmoObject) o)
                .toList();

        for (AmoObject obj : amoObjects) {
            try {
                obj.renderAnimated(poseStack, renderType, packedLight, joints);
            } catch (Exception e) {
                GE.LENS_LOG.error("Error rendering AmoObject {}: {}", obj.getName(), e.getMessage(), e);
            }
        }
    }

    /**
     * Render model with skeletal animation using material.
     *
     * @param poseStack   The pose stack.
     * @param material    The material.
     * @param packedLight The packed light.
     */
    public void renderAnimated(PoseStack poseStack, Material material, int packedLight) {
        renderAnimated(poseStack, material.renderType(), packedLight);
    }

    /**
     * Get all available animation names.
     */

    /**
     * Gets a set of all available animation names for this model.
     *
     * @return A set of animation names.
     */
    public java.util.Set<String> getAnimationNames() {
        return animations.keySet();
    }

    /**
     * Get the current animation.
     * @return returns the current animation
     */
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Get current animation time.
     * @return returns the current animation time.
     */
    public float getAnimationTime() {
        return animationTime;
    }

    /**
     * Set animation time manually.
     * @param time the time to set
     */
    public void setAnimationTime(float time) {
        this.animationTime = time;
        if (currentAnimation != null) {
            currentAnimation.applyAtTime(time, joints);
        }
    }

    /**
     * Check if animation is playing.
     * @return if an animation is playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Checks if the model is loaded.
     *
     * @return True if the model is loaded, false otherwise.
     */
    public boolean isModelLoaded() {
        return getObjects() != null;
    }

    /**
     * Get all joints in the skeleton.
     * @return all joints.
     */
    public Joint[] getJoints() {
        return joints;
    }

    /**
     * Get a specific joint by index.
     * @param index the index of which to get the joint.
     * @return the joint by index.
     */
    public Joint getJoint(int index) {
        if (index >= 0 && index < joints.length) {
            return joints[index];
        }
        return null;
    }

    /**
     * Find a joint by name.
     * @param name the name to search for
     * @return the joint
     */
    public Joint findJoint(String name) {
        for (Joint joint : joints) {
            if (joint.getName().equals(name)) {
                return joint;
            }
        }
        return null;
    }
}