package io.github.luckymcdev.groovyengine.threads.api.attachments.entity;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.util.Collections;
import java.util.List;

public abstract class EntityAttachment implements BaseAttachment {

    public final List<EntityType<?>> targetEntityTypes;

    /**
     * Constructor for single entity type attachment
     */
    public EntityAttachment(EntityType<?> entityType) {
        this.targetEntityTypes = List.of(entityType);
    }

    /**
     * Constructor for multi-entity type attachment
     */
    public EntityAttachment(List<EntityType<?>> entityTypes) {
        this.targetEntityTypes = List.copyOf(entityTypes);
    }

    /**
     * Constructor for multi-entity type attachment (varargs)
     */
    @SafeVarargs
    public EntityAttachment(EntityType<?>... entityTypes) {
        this.targetEntityTypes = List.of(entityTypes);
    }

    @Override
    public EntityType<?> getTarget() {
        return targetEntityTypes.isEmpty() ? null : targetEntityTypes.get(0);
    }

    @Override
    public List<Object> getTargets() {
        return Collections.singletonList(targetEntityTypes);
    }

    // Lifecycle Events
    public void onSpawn(Entity entity, LevelAccessor level) {
    }

    public void onRemove(Entity entity, Entity.RemovalReason reason) {
    }

    public void onTick(Entity entity) {
    }

    // Interaction Events
    public InteractionResult onInteract(Entity entity, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    // Combat Events (for LivingEntity)
    public void onHurt(LivingEntity entity, DamageSource source, float ammount) {
    }

    public void onDeath(LivingEntity entity, DamageSource source) {
    }
}