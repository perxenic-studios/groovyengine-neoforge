package io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation;

import io.github.luckymcdev.groovyengine.lens.client.systems.easing.Easing;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjModel;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform.Transform;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.animation.transform.TransformBuilder;

import java.util.List;

public class ChupacabraAnimations {

    private static AnimationController controller;

    // Animation names
    public static final String IDLE_BREATHING = "idle_breathing";
    public static final String IDLE_LOOK_AROUND = "idle_look_around";
    public static final String WALK = "walk";
    public static final String RUN = "run";
    public static final String ATTACK_BITE = "attack_bite";
    public static final String HOWL = "howl";
    public static final String TAIL_WAG = "tail_wag";
    public static final String SNIFF = "sniff";
    public static final String CROUCH = "crouch";
    public static final String JUMP = "jump";

    public static void initialize(ObjModel chupacabraModel) {
        controller = new AnimationController(chupacabraModel);

        // Subtle breathing animation
        Animation idleBreathing = AnimationBuilder.create(IDLE_BREATHING)
                .looping(true)
                .forObject("Body")
                .keyframe(0.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(1.5f, TransformBuilder.create()
                        .scale(1.02f, 1.01f, 1.02f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(3.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(1.5f, TransformBuilder.create()
                        .position(0, -0.02f, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(3.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .build();

        // Look around animation
        Animation idleLookAround = AnimationBuilder.create(IDLE_LOOK_AROUND)
                .looping(true)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(0, 0.3f, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(2.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(3.0f, TransformBuilder.create()
                        .rotation(0, -0.3f, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(4.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .build();

        // Walking animation
        Animation walk = AnimationBuilder.create(WALK)
                .looping(true)
                // Body bob
                .forObject("Body")
                .keyframe(0.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(0.25f, TransformBuilder.create()
                        .position(0, -0.05f, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(0.75f, TransformBuilder.create()
                        .position(0, -0.05f, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.SINE_IN_OUT)
                // Head bob
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(0.25f, TransformBuilder.create()
                        .position(0, 0.03f, 0.02f)
                        .rotation(0.1f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, Transform.identity(), Easing.SINE_IN_OUT)
                .keyframe(0.75f, TransformBuilder.create()
                        .position(0, 0.03f, 0.02f)
                        .rotation(0.1f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.SINE_IN_OUT)
                // Front legs
                .forObject("LegLeftFront")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .forObject("LegRightFront")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                // Back legs
                .forObject("LegLeftBack")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .forObject("LegRightBack")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                // Tail sway
                .forObject("TailLeft")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0, 0, -0.2f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0, 0, 0.2f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(0, 0, -0.2f)
                        .build(), Easing.SINE_IN_OUT)
                .forObject("TailRight")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0, 0, 0.2f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0, 0, -0.2f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.0f, TransformBuilder.create()
                        .rotation(0, 0, 0.2f)
                        .build(), Easing.SINE_IN_OUT)
                .build();

        // Running animation (faster, more exaggerated)
        Animation run = AnimationBuilder.create(RUN)
                .looping(true)
                // Body lean forward and bob
                .forObject("Body")
                .keyframe(0.0f, TransformBuilder.create()
                        .position(0, -0.1f, 0)
                        .rotation(0.2f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .position(0, 0.05f, 0)
                        .rotation(0.15f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, -0.1f, 0)
                        .rotation(0.2f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                // Head forward
                .forObject("Head")
                .keyframe(0.0f, TransformBuilder.create()
                        .position(0, 0, 0.1f)
                        .rotation(-0.2f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .position(0, 0.05f, 0.12f)
                        .rotation(-0.15f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, 0, 0.1f)
                        .rotation(-0.2f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                // Front legs - bigger motion
                .forObject("LegLeftFront")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .forObject("LegRightFront")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                // Back legs
                .forObject("LegLeftBack")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .forObject("LegRightBack")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                // Tail streaming behind
                .forObject("TailLeft")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(-0.3f, 0, -0.3f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(-0.4f, 0, 0.3f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(-0.3f, 0, -0.3f)
                        .build(), Easing.SINE_IN_OUT)
                .forObject("TailRight")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(-0.3f, 0, 0.3f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(-0.4f, 0, -0.3f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(-0.3f, 0, 0.3f)
                        .build(), Easing.SINE_IN_OUT)
                .build();

        // Attack bite animation
        Animation attackBite = AnimationBuilder.create(ATTACK_BITE)
                .looping(false)
                // Wind up
                .forObject("Body")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.2f, TransformBuilder.create()
                        .position(0, 0, -0.1f)
                        .rotation(-0.2f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, 0.05f, 0.15f)
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(0.6f, Transform.identity(), Easing.LINEAR)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.2f, TransformBuilder.create()
                        .position(0, 0, -0.05f)
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, 0.1f, 0.2f)
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(0.6f, Transform.identity(), Easing.LINEAR)
                .forObject("Mouth")
                .keyframe(0.0f, Transform.identity(), Easing.LINEAR)
                .keyframe(0.25f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .scale(1.1f, 1.2f, 1.1f)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.35f, TransformBuilder.create()
                        .rotation(-0.2f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(0.6f, Transform.identity(), Easing.LINEAR)
                .build();

        // Howl animation
        Animation howl = AnimationBuilder.create(HOWL)
                .looping(false)
                .forObject("Body")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, -0.15f, 0)
                        .rotation(-0.3f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(2.0f, TransformBuilder.create()
                        .position(0, -0.1f, 0)
                        .rotation(-0.25f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(2.5f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, 0.3f, 0)
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(2.0f, TransformBuilder.create()
                        .position(0, 0.32f, 0)
                        .rotation(-0.85f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(2.5f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("Mouth")
                .keyframe(0.5f, Transform.identity(), Easing.LINEAR)
                .keyframe(0.8f, TransformBuilder.create()
                        .rotation(0.4f, 0, 0)
                        .scale(1.0f, 1.3f, 1.0f)
                        .build(), Easing.QUAD_OUT)
                .keyframe(2.0f, TransformBuilder.create()
                        .rotation(0.35f, 0, 0)
                        .scale(1.0f, 1.25f, 1.0f)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(2.3f, Transform.identity(), Easing.QUAD_IN)
                .build();

        // Tail wagging (happy)
        Animation tailWag = AnimationBuilder.create(TAIL_WAG)
                .looping(true)
                .forObject("TailLeft")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0, 0, -0.5f)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(0, 0, 0.5f)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(0, 0, -0.5f)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("TailRight")
                .keyframe(0.0f, TransformBuilder.create()
                        .rotation(0, 0, 0.5f)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(0.15f, TransformBuilder.create()
                        .rotation(0, 0, -0.5f)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(0, 0, 0.5f)
                        .build(), Easing.QUAD_IN_OUT)
                .build();

        // Sniffing animation
        Animation sniff = AnimationBuilder.create(SNIFF)
                .looping(false)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, -0.15f, 0)
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, -0.12f, 0)
                        .rotation(0.35f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(0.7f, TransformBuilder.create()
                        .position(0, -0.15f, 0)
                        .rotation(0.4f, 0, 0)
                        .build(), Easing.SINE_IN_OUT)
                .keyframe(1.2f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("Mouth")
                .keyframe(0.3f, Transform.identity(), Easing.LINEAR)
                .keyframe(0.4f, TransformBuilder.create()
                        .scale(1.05f, 1.05f, 1.05f)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.5f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.6f, TransformBuilder.create()
                        .scale(1.05f, 1.05f, 1.05f)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.7f, Transform.identity(), Easing.QUAD_IN)
                .build();

        // Crouch (preparing to pounce)
        Animation crouch = AnimationBuilder.create(CROUCH)
                .looping(false)
                .forObject("Body")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, -0.2f, 0)
                        .rotation(0.2f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, -0.05f, 0.1f)
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("LegLeftFront")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("LegRightFront")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("LegLeftBack")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("LegRightBack")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("TailLeft")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .forObject("TailRight")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_IN_OUT)
                .build();

        // Jump animation
        Animation jump = AnimationBuilder.create(JUMP)
                .looping(false)
                // Crouch
                .forObject("Body")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.2f, TransformBuilder.create()
                        .position(0, -0.15f, 0)
                        .rotation(0.2f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                // Launch
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, 0.3f, 0)
                        .rotation(-0.3f, 0, 0)
                        .build(), Easing.QUAD_IN)
                // Peak
                .keyframe(0.5f, TransformBuilder.create()
                        .position(0, 0.4f, 0)
                        .rotation(-0.2f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                // Fall
                .keyframe(0.7f, TransformBuilder.create()
                        .position(0, 0.1f, 0)
                        .rotation(0.2f, 0, 0)
                        .build(), Easing.QUAD_IN)
                // Land
                .keyframe(0.8f, TransformBuilder.create()
                        .position(0, -0.1f, 0)
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.BOUNCE_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                // Head
                .forObject("Head")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.2f, TransformBuilder.create()
                        .position(0, -0.05f, 0)
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .position(0, 0.1f, 0)
                        .rotation(-0.4f, 0, 0)
                        .build(), Easing.LINEAR)
                .keyframe(0.5f, TransformBuilder.create()
                        .rotation(-0.3f, 0, 0)
                        .build(), Easing.LINEAR)
                .keyframe(0.8f, TransformBuilder.create()
                        .position(0, 0.05f, 0)
                        .rotation(0.2f, 0, 0)
                        .build(), Easing.BOUNCE_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                // Legs tucked during jump
                .forObject("LegLeftFront")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.25f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.6f, TransformBuilder.create()
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("LegRightFront")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.25f, TransformBuilder.create()
                        .rotation(-0.8f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.6f, TransformBuilder.create()
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("LegLeftBack")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.25f, TransformBuilder.create()
                        .rotation(-0.9f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.6f, TransformBuilder.create()
                        .rotation(-0.6f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("LegRightBack")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_IN)
                .keyframe(0.25f, TransformBuilder.create()
                        .rotation(-0.9f, 0, 0)
                        .build(), Easing.QUAD_OUT)
                .keyframe(0.6f, TransformBuilder.create()
                        .rotation(-0.6f, 0, 0)
                        .build(), Easing.QUAD_IN)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                // Tail
                .forObject("TailLeft")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.LINEAR)
                .keyframe(0.8f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.BOUNCE_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .forObject("TailRight")
                .keyframe(0.0f, Transform.identity(), Easing.QUAD_OUT)
                .keyframe(0.3f, TransformBuilder.create()
                        .rotation(-0.5f, 0, 0)
                        .build(), Easing.LINEAR)
                .keyframe(0.8f, TransformBuilder.create()
                        .rotation(0.3f, 0, 0)
                        .build(), Easing.BOUNCE_OUT)
                .keyframe(1.0f, Transform.identity(), Easing.QUAD_IN_OUT)
                .build();

        // Register all animations
        controller.addAnimations(List.of(
                idleBreathing, idleLookAround,
                walk, run, attackBite, howl,
                tailWag, sniff, crouch, jump
                )
        );

        // Start with idle breathing
        controller.play(IDLE_BREATHING);
        controller.setPlaybackSpeed(1.0f);
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
     * Play a specific animation.
     */
    public static void playAnimation(String animationName) {
        if (controller != null) {
            controller.play(animationName);
        }
    }

    /**
     * Stop all animations.
     */
    public static void stop() {
        if (controller != null) {
            controller.stop();
        }
    }

    /**
     * Pause current animation.
     */
    public static void pause() {
        if (controller != null) {
            controller.pause();
        }
    }

    /**
     * Resume current animation.
     */
    public static void resume() {
        if (controller != null) {
            controller.resume();
        }
    }

    /**
     * Reset current animation to start.
     */
    public static void reset() {
        if (controller != null) {
            controller.reset();
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

    /**
     * Check if an animation is currently playing.
     */
    public static boolean isPlaying() {
        return controller != null && controller.isPlaying();
    }

    /**
     * Get current animation name.
     */
    public static String getCurrentAnimationName() {
        if (controller != null && controller.getCurrentAnimation() != null) {
            return controller.getCurrentAnimation().getName();
        }
        return null;
    }

    /**
     * Get current playback time.
     */
    public static float getCurrentTime() {
        return controller != null ? controller.getCurrentTime() : 0f;
    }

    // ===== CONVENIENCE METHODS FOR COMMON ANIMATION SEQUENCES =====

    /**
     * Play idle animation (breathing + occasional look around).
     */
    public static void playIdle() {
        playAnimation(IDLE_BREATHING);
    }

    /**
     * Play walking animation.
     */
    public static void playWalk() {
        playAnimation(WALK);
    }

    /**
     * Play running animation.
     */
    public static void playRun() {
        playAnimation(RUN);
    }

    /**
     * Play attack animation.
     */
    public static void playAttack() {
        playAnimation(ATTACK_BITE);
    }

    /**
     * Play howl animation.
     */
    public static void playHowl() {
        playAnimation(HOWL);
    }

    /**
     * Play sniff animation.
     */
    public static void playSniff() {
        playAnimation(SNIFF);
    }

    /**
     * Play crouch animation (preparing to attack).
     */
    public static void playCrouch() {
        playAnimation(CROUCH);
    }

    /**
     * Play jump animation.
     */
    public static void playJump() {
        playAnimation(JUMP);
    }

    /**
     * Play tail wag animation (happy/excited).
     */
    public static void playTailWag() {
        playAnimation(TAIL_WAG);
    }
}