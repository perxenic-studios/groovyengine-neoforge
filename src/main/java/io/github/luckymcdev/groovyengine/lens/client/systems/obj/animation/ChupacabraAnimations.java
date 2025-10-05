package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import io.github.luckymcdev.groovyengine.lens.client.systems.easing.Easing;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform.Transform;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform.TransformBuilder;
import org.joml.Vector3f;

/**
 * Simple head bobbing animation for the chupacabra model with easing support.
 * Add this to your GroovyEngineClient class.
 */
public class ChupacabraAnimations {

    private static AnimationController controller;

    public static final String HEAD_BOB = "head_bob";
    public static final String HEAD_BOUNCE = "head_bounce";
    public static final String HEAD_WIGGLE = "head_wiggle";

    /**
     * Initialize the animation controller with the chupacabra model.
     * Call this after the model is loaded.
     */
    public static void initialize(ObjModel chupacabraModel) {
        controller = new AnimationController(chupacabraModel);

        // Create the head bob animation with smooth easing
        Animation headBob = AnimationBuilder.create(HEAD_BOB)
                .looping(true)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.LINEAR)
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, 0.1f, 0)
                        .build(), Easing.SINE_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.SINE_IN)
                .build();

        // Example: Create a more bouncy animation
        Animation headBounce = AnimationBuilder.create(HEAD_BOUNCE)
                .looping(true)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.LINEAR)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, 0.15f, 0)
                        .build(), Easing.BOUNCE_OUT)
                .keyframe(0.8f, Transform.identity(), Easing.BOUNCE_IN)
                .build();

        // Example: Create an elastic wiggle animation
        Animation headWiggle = AnimationBuilder.create(HEAD_WIGGLE)
                .looping(true)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.LINEAR)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0, 0.2f, 0)  // rotate slightly
                        .build(), Easing.ELASTIC_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.ELASTIC_IN)
                .build();

        controller.addAnimation(headBob);
        controller.addAnimation(headBounce);
        controller.addAnimation(headWiggle);

        controller.play(HEAD_BOB);  // Start with smooth bobbing
        controller.setPlaybackSpeed(1.0f); // Normal speed
    }

    /**
     * Update the animation. Call this every frame/tick.
     * @param deltaTime Time since last update (in seconds or ticks)
     */
    public static void update(float deltaTime) {
        if (controller != null) {
            controller.update(deltaTime);
        }
    }

    /**
     * Get the animation controller for manual control if needed.
     */
    public static AnimationController getController() {
        return controller;
    }

    /**
     * Switch between different animations.
     */
    public static void playAnimation(String animationName) {
        if (controller != null) {
            controller.play(animationName);
        }
    }

    /**
     * Stop all animations.
     */
    public static void stopAll() {
        if (controller != null) {
            controller.stop();
        }
    }

    /**
     * Set animation playback speed.
     */
    public static void setSpeed(float speed) {
        if (controller != null) {
            controller.setPlaybackSpeed(speed);
        }
    }
}