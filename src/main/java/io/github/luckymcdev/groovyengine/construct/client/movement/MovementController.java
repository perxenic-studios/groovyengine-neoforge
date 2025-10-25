package io.github.luckymcdev.groovyengine.construct.client.movement;

import io.github.luckymcdev.groovyengine.core.registry.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
/**
 * Handles flying movement modifications.
 * This class is responsible for updating the player's flying speed
 * and applying custom flying behavior when the flying changes toggle is enabled.
 */
public class MovementController {

    /**
     * Whether custom flying behavior is enabled.
     */
    private static boolean flyingChanges = true;

    /**
     * Event handler for the client tick event.
     * This method updates the player's flying speed when custom flying behavior is enabled.
     *
     * @param event the client tick event
     */
    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.getAbilities().flying) return;

        if (flyingChanges) {
            applyFlyingChanges(player);
        } else {
            player.setData(ModAttachmentTypes.FLY_SPEED, 0.1f);
            player.getAbilities().setFlyingSpeed(player.getData(ModAttachmentTypes.FLY_SPEED));
        }
    }

    /**
     * Toggles the flying changes flag.
     */
    public static void toggleFlyingChanges() {
        flyingChanges = !flyingChanges;
    }

    /**
     * Returns whether custom flying behavior is enabled.
     *
     * @return true if custom flying behavior is enabled, false otherwise
     */
    public static boolean flyingChangesEnabled() {
        return flyingChanges;
    }

    /**
     * Applies custom flying behavior to the player.
     *
     * @param player the player to apply custom flying behavior to
     */
    private static void applyFlyingChanges(LocalPlayer player) {
        // Update fly speed from attachment
        player.getAbilities().setFlyingSpeed(player.getData(ModAttachmentTypes.FLY_SPEED));

        // Stop motion if no inputs
        if (!isPlayerMoving(player)) {
            player.setDeltaMovement(Vec3.ZERO);
        }
    }

    /**
     * Returns whether the player is currently moving.
     *
     * @param player the player to check
     * @return true if the player is moving, false otherwise
     */
    private static boolean isPlayerMoving(LocalPlayer player) {
        return player.input.up || player.input.down
                || player.input.left || player.input.right
                || player.input.jumping || player.input.shiftKeyDown;
    }
}
