package io.github.luckymcdev.groovyengine.threads.api.attachments.client;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;

import java.util.List;

public abstract class ClientAttachment implements BaseAttachment {

    @Override
    public Object getTarget() {
        return "client";
    }

    @Override
    public List<Object> getTargets() {
        return List.of("client");
    }

    // Client-side Events
    public void onClientTick() {
    }

    // Input Events
    public void onKeyPress(int key, int action, int modifiers) {
    }

    public void onMouseClick(int button, int action, int modifiers) {
    }

    public void onMouseScroll(double horizontal, double vertical) {
    }

    // Client-only lifecycle
    public void onClientStart() {
    }
}