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

package io.github.luckymcdev.groovyengine.threads.api.attachments;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.api.attachments.global.*;
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.BlockAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.EntityAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.ItemAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central registry for all attachments.
 * Manages registration, lookup, and dispatching of attachment events.
 */
public class AttachmentManager {

    private static final AttachmentManager INSTANCE = new AttachmentManager();

    // Separate registries for each attachment type for faster lookup
    private final Set<BlockAttachment> blockAttachments = ConcurrentHashMap.newKeySet();
    private final Set<ItemAttachment> itemAttachments = ConcurrentHashMap.newKeySet();
    private final Set<EntityAttachment> entityAttachments = ConcurrentHashMap.newKeySet();
    private final Set<ScriptAttachment> scriptAttachments = ConcurrentHashMap.newKeySet();
    private final Set<ClientAttachment> clientAttachments = ConcurrentHashMap.newKeySet();
    private final Set<ServerAttachment> serverAttachments = ConcurrentHashMap.newKeySet();
    private final Set<RegistryAttachment> registryAttachments = ConcurrentHashMap.newKeySet();
    private final Set<RecipeAttachment> recipeAttachments = ConcurrentHashMap.newKeySet();

    // Cached lookups for better performance
    private final Map<Block, List<BlockAttachment>> blockCache = new ConcurrentHashMap<>();
    private final Map<Item, List<ItemAttachment>> itemCache = new ConcurrentHashMap<>();
    private final Map<EntityType<?>, List<EntityAttachment>> entityCache = new ConcurrentHashMap<>();

    public static AttachmentManager getInstance() {
        return INSTANCE;
    }

    // ============= REGISTRATION =============

    /**
     * Register a block attachment
     */
    public void registerBlock(BlockAttachment attachment) {
        if (blockAttachments.add(attachment)) {
            blockCache.clear(); // Invalidate cache
            attachment.onInit();
            GE.THREADS_LOG.info("Registered block attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register an item attachment
     */
    public void registerItem(ItemAttachment attachment) {
        if (itemAttachments.add(attachment)) {
            itemCache.clear(); // Invalidate cache
            attachment.onInit();
            GE.THREADS_LOG.info("Registered item attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register an entity attachment
     */
    public void registerEntity(EntityAttachment attachment) {
        if (entityAttachments.add(attachment)) {
            entityCache.clear(); // Invalidate cache
            attachment.onInit();
            GE.THREADS_LOG.info("Registered entity attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register a script attachment
     */
    public void registerScript(io.github.luckymcdev.groovyengine.threads.api.attachments.global.ScriptAttachment attachment) {
        if (scriptAttachments.add(attachment)) {
            attachment.onInit();
            GE.THREADS_LOG.info("Registered script attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register a client attachment
     */
    public void registerClient(io.github.luckymcdev.groovyengine.threads.api.attachments.global.ClientAttachment attachment) {
        if (clientAttachments.add(attachment)) {
            attachment.onInit();
            GE.THREADS_LOG.info("Registered client attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register a server attachment
     */
    public void registerServer(io.github.luckymcdev.groovyengine.threads.api.attachments.global.ServerAttachment attachment) {
        if (serverAttachments.add(attachment)) {
            attachment.onInit();
            GE.THREADS_LOG.info("Registered server attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register a registry attachment
     */
    public void registerRegistry(io.github.luckymcdev.groovyengine.threads.api.attachments.global.RegistryAttachment attachment) {
        if (registryAttachments.add(attachment)) {
            attachment.onInit();
            GE.THREADS_LOG.info("Registered registry attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    /**
     * Register a recipe attachment
     */
    public void registerRecipe(io.github.luckymcdev.groovyengine.threads.api.attachments.global.RecipeAttachment attachment) {
        if (recipeAttachments.add(attachment)) {
            attachment.onInit();
            GE.THREADS_LOG.info("Registered recipe attachment: {}", attachment.getClass().getSimpleName());
        }
    }


    public void unregister(Object attachment) {
        boolean removed = blockAttachments.remove(attachment) ||
                itemAttachments.remove(attachment) ||
                entityAttachments.remove(attachment) ||
                scriptAttachments.remove(attachment) ||
                clientAttachments.remove(attachment) ||
                serverAttachments.remove(attachment) ||
                registryAttachments.remove(attachment) ||
                recipeAttachments.remove(attachment);

        if (removed) {
            // Clear caches
            blockCache.clear();
            itemCache.clear();
            entityCache.clear();

            if (attachment instanceof BaseAttachment) {
                ((BaseAttachment<?>) attachment).onDestroy();
            }
            GE.THREADS_LOG.info("Unregistered attachment: {}", attachment.getClass().getSimpleName());
        }
    }

    // ============= LOOKUP METHODS =============

    /**
     * Get all block attachments that apply to the given block.
     * Results are cached for performance.
     */
    public List<BlockAttachment> getBlockAttachments(Block block) {
        return blockCache.computeIfAbsent(block, b ->
                blockAttachments.stream()
                        .filter(att -> att.appliesTo(b))
                        .sorted(Comparator.comparingInt(BlockAttachment::getPriority).reversed())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get all item attachments that apply to the given item.
     * Results are cached for performance.
     */
    public List<ItemAttachment> getItemAttachments(Item item) {
        return itemCache.computeIfAbsent(item, i ->
                itemAttachments.stream()
                        .filter(att -> att.appliesTo(i))
                        .sorted(Comparator.comparingInt(ItemAttachment::getPriority).reversed())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get all entity attachments that apply to the given entity type.
     * Results are cached for performance.
     */
    public List<EntityAttachment> getEntityAttachments(EntityType<?> entityType) {
        return entityCache.computeIfAbsent(entityType, et ->
                entityAttachments.stream()
                        .filter(att -> att.appliesTo(et))
                        .sorted(Comparator.comparingInt(EntityAttachment::getPriority).reversed())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get all script attachments that apply to the given script ID
     */
    public List<ScriptAttachment> getScriptAttachments(String scriptId) {
        return scriptAttachments.stream()
                .filter(att -> att.appliesTo(scriptId))
                .sorted(Comparator.comparingInt(ScriptAttachment::getPriority).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all client attachments
     */
    public List<ClientAttachment> getClientAttachments() {
        return new ArrayList<>(clientAttachments);
    }

    /**
     * Get all server attachments
     */
    public List<ServerAttachment> getServerAttachments() {
        return new ArrayList<>(serverAttachments);
    }

    /**
     * Get all registry attachments
     */
    public List<RegistryAttachment> getRegistryAttachments() {
        return new ArrayList<>(registryAttachments);
    }

    /**
     * Get all recipe attachments
     */
    public List<RecipeAttachment> getRecipeAttachments() {
        return new ArrayList<>(recipeAttachments);
    }

    /**
     * Clear all caches. Call this if you modify attachments at runtime.
     */
    public void clearCaches() {
        blockCache.clear();
        itemCache.clear();
        entityCache.clear();
    }
}