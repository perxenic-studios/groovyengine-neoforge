/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package io.github.luckymcdev.groovyengine.threads.api.attachments.local;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Attachment for adding custom behavior to entities.
 * Supports targeting specific entity types or matching entities based on a predicate.
 */
public abstract class EntityAttachment implements BaseAttachment<EntityType<?>> {

    private final Set<EntityType<?>> targetEntityTypes;
    private final Predicate<EntityType<?>> entityMatcher;

    /**
     * Constructor for single entity type attachment
     */
    protected EntityAttachment(EntityType<?> entityType) {
        this.targetEntityTypes = Set.of(entityType);
        this.entityMatcher = null;
    }

    /**
     * Constructor for multiple specific entity types
     */
    @SafeVarargs
    protected EntityAttachment(EntityType<?>... entityTypes) {
        this.targetEntityTypes = new HashSet<>(Arrays.asList(entityTypes));
        this.entityMatcher = null;
    }

    /**
     * Constructor for predicate-based matching (e.g., all hostile mobs, all animals)
     */
    protected EntityAttachment(Predicate<EntityType<?>> entityMatcher) {
        this.targetEntityTypes = Set.of();
        this.entityMatcher = entityMatcher;
    }

    @Override
    public final boolean appliesTo(EntityType<?> entityType) {
        if (entityMatcher != null) {
            return entityMatcher.test(entityType);
        }
        return targetEntityTypes.contains(entityType);
    }

    // === Entity Lifecycle Events ===

    /**
     * Called when an entity spawns or is added to the world
     */
    public void onSpawn(Entity entity, LevelAccessor level) {
    }

    /**
     * Called when an entity is removed from the world
     */
    public void onRemove(Entity entity, Entity.RemovalReason reason) {
    }

    /**
     * Called every tick for the entity
     */
    public void onTick(Entity entity) {
    }

    // === Interaction Events ===

    /**
     * Called when a player interacts with the entity (right-click)
     *
     * @return The interaction result. Return PASS to allow other attachments to process.
     */
    public InteractionResult onInteract(Entity entity, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    // === Combat Events (for LivingEntity) ===

    /**
     * Called when a living entity takes damage
     */
    public void onHurt(LivingEntity entity, DamageSource source, float amount) {
    }

    /**
     * Called when a living entity dies
     */
    public void onDeath(LivingEntity entity, DamageSource source) {
    }
}