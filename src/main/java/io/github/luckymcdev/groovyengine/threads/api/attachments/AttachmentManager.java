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

    void register(BaseAttachment attachment);

    void unregister(BaseAttachment attachment);

    // Utility methods for finding attachments by target
    <T> List<BaseAttachment> getAttachments(T target);

    List<BaseAttachment> getBlockAttachments(Block block);

    List<BaseAttachment> getItemAttachments(Item item);

    List<BaseAttachment> getEntityAttachments(EntityType<?> entityType);

    List<BaseAttachment> getScriptAttachments(String scriptId);

    // Global attachment getters
    List<BaseAttachment> getClientAttachments();

    List<BaseAttachment> getServerAttachments();

    List<BaseAttachment> getRegistryAttachments();
}