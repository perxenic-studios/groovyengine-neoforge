package io.github.luckymcdev.groovyengine.construct.client;

import io.github.luckymcdev.groovyengine.core.registry.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class MovementController {

    private static boolean flyingChanges = true;

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.getAbilities().flying) return;

        if (flyingChanges) {
            applyFlyingChanges(player);
        } else {
            player.setData(ModAttachmentTypes.FLY_SPEED, 0.2f);
            player.getAbilities().setFlyingSpeed(player.getData(ModAttachmentTypes.FLY_SPEED));
        }
    }

    // Public controls
    public static void toggleFlyingChanges() {
        flyingChanges = !flyingChanges;
    }

    public static boolean flyingChangesEnabled() {
        return flyingChanges;
    }

    // Internals
    private static void applyFlyingChanges(LocalPlayer player) {
        // Update fly speed from attachment
        player.getAbilities().setFlyingSpeed(player.getData(ModAttachmentTypes.FLY_SPEED));

        // Stop motion if no inputs
        if (!isPlayerMoving(player)) {
            player.setDeltaMovement(Vec3.ZERO);
        }
    }

    private static boolean isPlayerMoving(LocalPlayer player) {
        return player.input.up || player.input.down
                || player.input.left || player.input.right
                || player.input.jumping || player.input.shiftKeyDown;
    }
}
