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

package io.github.luckymcdev.groovyengine.threads.core.scripting.attachment;

import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.threads.api.attachments.global.ServerAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Central dispatcher for all attachment-related events.
 * This class listens to NeoForge events and delegates them to the appropriate attachments.
 */
public class AttachmentEventDispatcher {

    private final AttachmentManager manager = AttachmentManager.getInstance();

    // Server Events
    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        manager.getServerAttachments().forEach(ServerAttachment::onServerStart);
    }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        manager.getServerAttachments().forEach(ServerAttachment::onServerStop);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        manager.getServerAttachments().forEach(ServerAttachment::onServerTick);
    }

    // Client Events - Note: Client events are now handled in AttachmentEventManagerImpl
    // to avoid duplicate registration issues

    // Player Events
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        manager.getServerAttachments().forEach(a -> a.onPlayerJoin(event.getEntity().getName().getString()));
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        manager.getServerAttachments().forEach(a -> a.onPlayerLeave(event.getEntity().getName().getString()));
    }

    @SubscribeEvent
    public void onPlayerCraft(PlayerEvent.ItemCraftedEvent event) {
        manager.getItemAttachments(event.getCrafting().getItem()).forEach(a -> a.onCraftedBy(event.getCrafting(), event.getEntity().level(), event.getEntity()));
    }

    // World Events
    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            manager.getServerAttachments().forEach(a -> a.onWorldLoad(event.getLevel().toString()));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            manager.getServerAttachments().forEach(a -> a.onWorldUnload(event.getLevel().toString()));
        }
    }

    // Item Events
    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        manager.getItemAttachments(event.getItemStack().getItem()).forEach(a -> a.onUse(event.getLevel(), event.getEntity(), event.getHand(), event.getItemStack()));
    }

    @SubscribeEvent
    public void onItemUseOnBlock(PlayerInteractEvent.RightClickBlock event) {
        manager.getItemAttachments(event.getItemStack().getItem()).forEach(a -> a.onUseOn(new net.minecraft.world.item.context.UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()), event.getItemStack()));
        manager.getBlockAttachments(event.getLevel().getBlockState(event.getPos()).getBlock()).forEach(a -> a.onUse(event.getLevel().getBlockState(event.getPos()), event.getLevel(), event.getPos(), event.getEntity(), event.getHand(), event.getHitVec()));
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget() instanceof Player) return; // Avoid firing for player-on-player
        manager.getItemAttachments(event.getEntity().getMainHandItem().getItem()).forEach(a -> a.onLeftClickEntity(event.getEntity().getMainHandItem(), event.getEntity(), event.getTarget()));
    }

    // Block Events
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player) {
            manager.getBlockAttachments(event.getPlacedBlock().getBlock()).forEach(a -> a.onPlace(event.getLevel(), event.getPos(), event.getPlacedBlock(), (Player) event.getEntity()));
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        manager.getBlockAttachments(event.getState().getBlock()).forEach(a -> a.onBreak(event.getLevel(), event.getPos(), event.getState(), event.getPlayer()));
    }

    @SubscribeEvent
    public void onBlockAttack(PlayerInteractEvent.LeftClickBlock event) {
        manager.getBlockAttachments(event.getLevel().getBlockState(event.getPos()).getBlock()).forEach(a -> a.onAttack(event.getLevel().getBlockState(event.getPos()), event.getLevel(), event.getPos(), event.getEntity()));
    }

    @SubscribeEvent
    public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        // Iterate through all notified sides and fire events for each
        for (net.minecraft.core.Direction direction : event.getNotifiedSides()) {
            net.minecraft.core.BlockPos neighborPos = event.getPos().relative(direction);
            manager.getBlockAttachments(event.getState().getBlock()).forEach(a -> a.onNeighborChanged(event.getState(), event.getLevel(), event.getPos(), event.getLevel().getBlockState(neighborPos).getBlock(), neighborPos, false));
        }
    }

    // Entity Events
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinLevelEvent event) {
        manager.getEntityAttachments(event.getEntity().getType()).forEach(a -> a.onSpawn(event.getEntity(), event.getLevel()));
    }

    @SubscribeEvent
    public void onEntityTick(PlayerTickEvent.Post event) {
        // Handle player tick events
        manager.getEntityAttachments(event.getEntity().getType()).forEach(a -> a.onTick(event.getEntity()));
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        manager.getEntityAttachments(event.getTarget().getType()).forEach(a -> a.onInteract(event.getTarget(), event.getEntity(), event.getHand()));
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity) {
            manager.getEntityAttachments(event.getEntity().getType()).forEach(a -> a.onHurt(event.getEntity(), event.getSource(), event.getNewDamage()));
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            manager.getEntityAttachments(event.getEntity().getType()).forEach(a -> a.onDeath(event.getEntity(), event.getSource()));
        }
    }
}