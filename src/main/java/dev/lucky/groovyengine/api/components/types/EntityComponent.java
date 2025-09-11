package dev.lucky.groovyengine.api.components.types;

import net.minecraft.world.entity.Entity;

public abstract class EntityComponent<T extends Entity> implements ScriptComponent<T> {

    public void onTick(Entity entity) {}
    public void onInteract(Entity entity) {}
    public void onDamage(Entity entity) {}
}