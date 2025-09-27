package io.github.luckymcdev.groovyengine.threads.core.scripting.attachment;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.api.attachments.BaseAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.stream.Collectors;

public class AttachmentManagerImpl implements AttachmentManager {

    public static AttachmentManager INSTANCE = new AttachmentManagerImpl();

    private final Set<BaseAttachment<?>> attachments = new HashSet<>();

    @Override
    public void register(BaseAttachment<?> attachment) {
        if (attachments.add(attachment)) {
            GE.LOG.info("Registered attachment: {} for targets: {}",
                    attachment.getClass().getSimpleName(),
                    attachment.getTargets().size());
            attachment.onInit();
        }
    }

    @Override
    public void unregister(BaseAttachment<?> attachment) {
        if (attachments.remove(attachment)) {
            GE.LOG.info("Unregistered attachment: {}", attachment.getClass().getSimpleName());
            attachment.onDestroy();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<BaseAttachment<T>> getAttachments(T target, Class<T> targetType) {
        return attachments.stream()
                .filter(attachment -> {
                    try {
                        return attachment.appliesTo(target);
                    } catch (ClassCastException e) {
                        return false;
                    }
                })
                .map(attachment -> (BaseAttachment<T>) attachment)
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseAttachment<Block>> getBlockAttachments(Block block) {
        return getAttachments(block, Block.class);
    }

    @Override
    public List<BaseAttachment<Item>> getItemAttachments(Item item) {
        return getAttachments(item, Item.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BaseAttachment<EntityType<?>>> getEntityAttachments(EntityType<?> entityType) {
        return attachments.stream()
                .filter(attachment -> attachment.appliesTo(entityType))
                .map(attachment -> (BaseAttachment<EntityType<?>>) attachment)
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseAttachment<Object>> getScriptAttachments(String scriptId) {
        return getAttachments((Object) scriptId, Object.class);
    }

    public static AttachmentManager getInstance() {
        return INSTANCE;
    }
}