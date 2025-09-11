package io.github.luckymcdev.groovyengine.api.components.types;

public interface ScriptComponent<T> {
    default void onInit() {}
    default void onUpdate() {}
    default void onDestroy() {}
    T getTarget();
}