package io.github.luckymcdev.groovyengine.threads.api.attachments;

import io.github.luckymcdev.groovyengine.threads.core.scripting.attachment.AttachmentManagerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface AttachmentManager {

    static AttachmentManager getInstance() {
        return AttachmentManagerImpl.INSTANCE;
    }

    void register(BaseAttachment<?> attachment);
    void unregister(BaseAttachment<?> attachment);

    // Utility methods for finding attachments by target
    <T> List<BaseAttachment<T>> getAttachments(T target, Class<T> targetType);

    List<BaseAttachment<Block>> getBlockAttachments(Block block);
    List<BaseAttachment<Item>> getItemAttachments(Item item);
    List<BaseAttachment<EntityType<?>>> getEntityAttachments(EntityType<?> entityType);
    List<BaseAttachment<Object>> getScriptAttachments(String scriptId);
}