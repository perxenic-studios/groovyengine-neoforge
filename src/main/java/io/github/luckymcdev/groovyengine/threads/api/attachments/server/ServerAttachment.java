package io.github.luckymcdev.groovyengine.threads.api.attachments.server;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;

import java.util.List;

public abstract class ServerAttachment implements BaseAttachment {

    @Override
    public Object getTarget() {
        return "server";
    }

    @Override
    public List<Object> getTargets() {
        return List.of("server");
    }

    // Server Lifecycle Events
    public void onServerStart() {
    }

    public void onServerStop() {
    }

    public void onServerTick() {
    }

    // World Events
    public void onWorldLoad(String worldName) {
    }

    public void onWorldUnload(String worldName) {
    }

    // Player Management Events
    public void onPlayerJoin(String playerName) {
    }

    public void onPlayerLeave(String playerName) {
    }
}