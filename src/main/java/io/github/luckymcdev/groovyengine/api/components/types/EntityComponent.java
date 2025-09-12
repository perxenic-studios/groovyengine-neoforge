// EntityComponent.java - Enhanced with more events
package io.github.luckymcdev.groovyengine.api.components.types;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityComponent<T extends Entity> implements BaseComponent<T> {

    public void onTick(Entity entity) {}
    public void onInteract(Entity entity) {}
    public void onDamage(Entity entity, DamageSource source) {}
    public void onDeath(Entity entity, DamageSource source) {}
    public void onSpawn(Entity entity) {}
    public void onCollide(Entity entity, Entity other) {}
    public void onMount(Entity entity, Entity passenger) {}
    public void onDismount(Entity entity, Entity passenger) {}
}