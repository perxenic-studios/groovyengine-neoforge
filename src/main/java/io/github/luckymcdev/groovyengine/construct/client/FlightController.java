package io.github.luckymcdev.groovyengine.construct.client;

import io.github.luckymcdev.groovyengine.core.core.registry.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class FlightController {

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;
        if (!player.getAbilities().flying) return;

        // Modify fly speed
        player.getAbilities().setFlyingSpeed(player.getData(ModAttachmentTypes.FLY_SPEED));

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
        }
    }
}