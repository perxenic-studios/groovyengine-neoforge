package common
//priority=0
import io.github.luckymcdev.groovyengine.GE
import io.github.luckymcdev.groovyengine.threads.api.attachments.AttachmentManager

GE.LOG.info('Hello from common scripts!')

// Get the attachment manager for common use
def attachmentManager = AttachmentManager.getInstance()
GE.LOG.info('Attachment Manager loaded: {}', attachmentManager.getClass().getSimpleName())

// Add your common script logic here