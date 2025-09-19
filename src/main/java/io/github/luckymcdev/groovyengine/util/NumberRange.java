package io.github.luckymcdev.groovyengine.util;

public record NumberRange(float min, float max) {

    public boolean contains(float value) {
        return value >= min && value <= max;
    }

    public float clamp(float value) {
        return Math.max(min, Math.min(max, value));
    }
}
