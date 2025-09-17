package io.github.luckymcdev.groovyengine.construct.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class FlightController {

    public static float CUSTOM_FLY_SPEED = 0.2f;

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;
        if (!player.getAbilities().flying) return;

        // Modify fly speed
        player.getAbilities().setFlyingSpeed(CUSTOM_FLY_SPEED);

        handleClientSideMovement(player);
    }

    private static void handleClientSideMovement(LocalPlayer player) {
        // If player is not actively moving, stop them completely
        boolean isMoving = player.input.up || player.input.down ||
                player.input.left || player.input.right ||
                player.input.jumping || player.input.shiftKeyDown;

        if (!isMoving && player.getAbilities().flying) {
            // Stop all movement when no input is pressed
            player.setDeltaMovement(Vec3.ZERO);
            return;
        }

        // For more responsive movement, modify the movement vector directly
        if (player.getAbilities().flying) {
            Vec3 look = player.getLookAngle();
            Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
            Vec3 forward = look.multiply(1, 0, 1).normalize(); // Remove Y component for horizontal movement
            Vec3 up = new Vec3(0, 1, 0);

            Vec3 moveDirection = Vec3.ZERO;

            if (player.input.up) moveDirection = moveDirection.add(forward);
            if (player.input.down) moveDirection = moveDirection.subtract(forward);
            if (player.input.left) moveDirection = moveDirection.subtract(right);
            if (player.input.right) moveDirection = moveDirection.add(right);
            if (player.input.jumping) moveDirection = moveDirection.add(up);
            if (player.input.shiftKeyDown) moveDirection = moveDirection.subtract(up);

            if (moveDirection.length() > 0) {
                moveDirection = moveDirection.normalize().scale(CUSTOM_FLY_SPEED);
                player.setDeltaMovement(moveDirection);
            }
        }
    }
}