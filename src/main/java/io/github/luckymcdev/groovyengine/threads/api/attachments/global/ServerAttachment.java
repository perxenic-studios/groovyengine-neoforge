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

package io.github.luckymcdev.groovyengine.threads.api.attachments.global;

import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;

import java.util.List;

/**
 * Attachment for server-side only events.
 * Use this for world management, player tracking, server lifecycle, etc.
 */
public abstract class ServerAttachment implements GlobalAttachment<Void> {

    /**
     * Called when the server starts up
     */
    public void onServerStart() {
    }

    /**
     * Called when the server shuts down
     */
    public void onServerStop() {
    }

    /**
     * Called every server tick
     */
    public void onServerTick() {
    }

    /**
     * Called when a world/dimension is loaded
     */
    public void onWorldLoad(String worldName) {
    }

    /**
     * Called when a world/dimension is unloaded
     */
    public void onWorldUnload(String worldName) {
    }

    /**
     * Called when a player joins the server
     */
    public void onPlayerJoin(String playerName) {
    }

    /**
     * Called when a player leaves the server
     */
    public void onPlayerLeave(String playerName) {
    }
}
