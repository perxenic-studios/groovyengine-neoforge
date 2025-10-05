package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls animation playback for an ObjModel.
 */
public class AnimationController {
    private final ObjModel model;
    private final Map<String, Animation> animations;
    private Animation currentAnimation;
    private float currentTime;
    private boolean playing;
    private float playbackSpeed;

    public AnimationController(ObjModel model) {
        this.model = model;
        this.animations = new HashMap<>();
        this.currentTime = 0;
        this.playing = false;
        this.playbackSpeed = 1.0f;
    }

    /**
     * Register an animation.
     */
    public void addAnimation(Animation animation) {
        animations.put(animation.getName(), animation);
    }

    /**
     * Play an animation by name.
     */
    public void play(String animationName) {
        Animation anim = animations.get(animationName);
        if (anim != null) {
            this.currentAnimation = anim;
            this.currentTime = 0;
            this.playing = true;
        }
    }

    /**
     * Stop the current animation.
     */
    public void stop() {
        this.playing = false;
    }

    /**
     * Pause the current animation.
     */
    public void pause() {
        this.playing = false;
    }

    /**
     * Resume the current animation.
     */
    public void resume() {
        if (currentAnimation != null) {
            this.playing = true;
        }
    }

    /**
     * Reset animation to start.
     */
    public void reset() {
        this.currentTime = 0;
    }

    /**
     * Set playback speed (1.0 = normal speed).
     */
    public void setPlaybackSpeed(float speed) {
        this.playbackSpeed = speed;
    }

    /**
     * Update animation (call this every frame/tick).
     * @param deltaTime Time since last update in seconds or ticks
     */
    public void update(float deltaTime) {
        if (!playing || currentAnimation == null) {
            return;
        }

        currentTime += deltaTime * playbackSpeed;

        // Handle looping
        if (currentAnimation.isLooping()) {
            if (currentAnimation.getDuration() > 0) {
                currentTime = currentTime % currentAnimation.getDuration();
            }
        } else if (currentTime >= currentAnimation.getDuration()) {
            currentTime = currentAnimation.getDuration();
            playing = false;
        }

        // Apply transforms to model objects
        applyTransforms();
    }

    /**
     * Apply current animation transforms to model objects.
     */
    private void applyTransforms() {
        if (currentAnimation == null) {
            return;
        }

        Map<String, KeyframeTransform> transforms = currentAnimation.getTransformsAtTime(currentTime);

        for (Map.Entry<String, KeyframeTransform> entry : transforms.entrySet()) {
            ObjObject obj = model.getObject(entry.getKey());
            if (obj != null) {
                KeyframeTransform transform = entry.getValue();
                obj.setPosition(transform.position);
                obj.setRotation(transform.rotation);
                obj.setScale(transform.scale);
            }
        }
    }

    /**
     * Get the current animation.
     */
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Get current playback time.
     */
    public float getCurrentTime() {
        return currentTime;
    }

    /**
     * Check if animation is playing.
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Get all registered animations.
     */
    public Map<String, Animation> getAnimations() {
        return animations;
    }
}