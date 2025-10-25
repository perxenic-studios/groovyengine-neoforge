package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.rendering.material.Material;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
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

    public AmoModel(ResourceLocation modelLocation) {
        super(modelLocation);
        this.objParser = new AmoParser();
        this.animationTime = 0;
        this.isPlaying = false;
    }

    /**
     * Loads the animated model from the resource location.
     *
     * @implSpec
     * @implNote This method loads the animated model from the resource location.
     * It first checks if the resource is present, and if so, it parses the OBJ file
     * and loads the model data. Finally, it logs the loaded model data.
     *
     * @throws RuntimeException if the resource is not found.
     */
    @Override
    public void loadModel() {
        GE.LENS_LOG.info("Loading animated model: {}", modelLocation);
        String modID = this.modelLocation.getNamespace();
        String fileName = this.modelLocation.getPath();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(modID, "obj/" + fileName + ".amo");
        Optional<Resource> resourceO = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        if (resourceO.isEmpty()) {
            throw new RuntimeException("Resource not found: " + resourceLocation);
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
                .filter(o -> o instanceof AmoObject)
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
     */
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Get current animation time.
     */
    public float getAnimationTime() {
        return animationTime;
    }

    /**
     * Set animation time manually.
     */
    public void setAnimationTime(float time) {
        this.animationTime = time;
        if (currentAnimation != null) {
            currentAnimation.applyAtTime(time, joints);
        }
    }

    /**
     * Check if animation is playing.
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
     */
    public Joint[] getJoints() {
        return joints;
    }

    /**
     * Get a specific joint by index.
     */
    public Joint getJoint(int index) {
        if (index >= 0 && index < joints.length) {
            return joints[index];
        }
        return null;
    }

    /**
     * Find a joint by name.
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