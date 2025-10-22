package io.github.luckymcdev.groovyengine.threads.core.scripting.attachment;

import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager;
import io.github.luckymcdev.groovyengine.threads.api.attachments.BaseAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AttachmentManagerImpl implements AttachmentManager {

    public static AttachmentManager INSTANCE = new AttachmentManagerImpl();

    private final Set<BaseAttachment> attachments = new HashSet<>();

    public static AttachmentManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(BaseAttachment attachment) {
        if (attachments.add(attachment)) {
            GE.THREADS_LOG.info("Registered attachment: {} for targets: {}",
                    attachment.getClass().getSimpleName(),
                    attachment.getTargets().size());
            attachment.onInit();
        }
    }

    @Override
    public void unregister(BaseAttachment attachment) {
        if (attachments.remove(attachment)) {
            GE.THREADS_LOG.info("Unregistered attachment: {}", attachment.getClass().getSimpleName());
            attachment.onDestroy();
        }
    }

    @Override
    public List<BaseAttachment> getAttachments(Object target) {
        return attachments.stream()
                .filter(attachment -> attachment.appliesTo(target))
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseAttachment> getBlockAttachments(Block block) {
        return getAttachments(block);
    }

    @Override
    public List<BaseAttachment> getItemAttachments(Item item) {
        return getAttachments(item);
    }

    @Override
    public List<BaseAttachment> getEntityAttachments(EntityType<?> entityType) {
        return getAttachments(entityType);
    }

    @Override
    public List<BaseAttachment> getScriptAttachments(String scriptId) {
        return getAttachments(scriptId);
    }

    @Override
    public List<BaseAttachment> getClientAttachments() {
        return getAttachments("client");
    }

    @Override
    public List<BaseAttachment> getServerAttachments() {
        return getAttachments("server");
    }

    @Override
    public List<BaseAttachment> getRegistryAttachments() {
        return getAttachments("registry");
    }
}