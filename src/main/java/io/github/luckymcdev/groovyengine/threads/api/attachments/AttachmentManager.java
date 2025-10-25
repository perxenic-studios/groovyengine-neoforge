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

    /**
     * Registers the given attachment with the attachment manager.
     * <p>
     * The attachment manager will store the attachment and call its
     * {@link BaseAttachment#onInit()} method.
     * <p>
     * Attachments can only be registered once.
     * <p>
     * If the attachment is already registered, the method will do nothing.
     *
     * @param attachment The attachment to register
     */
    void register(BaseAttachment attachment);

    /**
     * Unregisters the given attachment.
     *
     * @param attachment The attachment to unregister
     */
    void unregister(BaseAttachment attachment);


    /**
     * Retrieves all attachments associated with the given target.
     *
     * @param <T> target The target to retrieve attachments for
     * @return A list of attachments associated with the target
     */
    <T> List<BaseAttachment> getAttachments(T target);

    /**
     * Retrieves all attachments associated with the given block.
     *
     * @param block The block to retrieve attachments for
     * @return A list of attachments associated with the block
     */
    List<BaseAttachment> getBlockAttachments(Block block);

    /**
     * Retrieves all attachments associated with the given item.
     *
     * @param item The item to retrieve attachments for
     * @return A list of attachments associated with the item
     */
    List<BaseAttachment> getItemAttachments(Item item);

    /**
     * Retrieves all attachments associated with the given entity type.
     *
     * @param entityType The entity type to retrieve attachments for
     * @return A list of attachments associated with the entity type
     */
    List<BaseAttachment> getEntityAttachments(EntityType<?> entityType);

    /**
     * Retrieves all attachments associated with the given script ID.
     *
     * @param scriptId The script ID to retrieve attachments for
     * @return A list of attachments associated with the script ID
     */
    List<BaseAttachment> getScriptAttachments(String scriptId);

    /**
     * Retrieves all attachments associated with the client.
     *
     * @return A list of attachments associated with the client
     */
    List<BaseAttachment> getClientAttachments();

    /**
     * Retrieves all attachments associated with the server.
     *
     * @return A list of attachments associated with the server
     */
    List<BaseAttachment> getServerAttachments();

    /**
     * Retrieves all attachments associated with the registry.
     *
     * @return A list of attachments associated with the registry
     */
    List<BaseAttachment> getRegistryAttachments();
}