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
package de.groovyengine;

import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.global.*;
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.BlockAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.EntityAttachment;
import io.github.luckymcdev.groovyengine.threads.api.attachments.local.ItemAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface IAttachmentManager {

    static IAttachmentManager getInstance() {
        return AttachmentManager.getInstance();
    }

    void register(BaseAttachment<?> attachment);

    void registerBlock(BlockAttachment attachment);

    void registerItem(ItemAttachment attachment);

    void registerEntity(EntityAttachment attachment);

    void registerScript(ScriptAttachment attachment);

    void registerClient(ClientAttachment attachment);

    void registerServer(ServerAttachment attachment);

    void registerRegistry(RegistryAttachment attachment);

    void registerRecipe(RecipeAttachment attachment);

    void unregister(Object attachment);

    List<BlockAttachment> getBlockAttachments(Block block);

    List<ItemAttachment> getItemAttachments(Item item);

    List<EntityAttachment> getEntityAttachments(EntityType<?> entityType);

    List<ScriptAttachment> getScriptAttachments(String scriptId);

    List<ClientAttachment> getClientAttachments();

    List<ServerAttachment> getServerAttachments();

    List<RegistryAttachment> getRegistryAttachments();

    List<RecipeAttachment> getRecipeAttachments();

    void clearCaches();
}
