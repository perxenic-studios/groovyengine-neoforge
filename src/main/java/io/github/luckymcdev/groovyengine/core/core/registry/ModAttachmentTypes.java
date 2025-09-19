package io.github.luckymcdev.groovyengine.core.core.registry;

import com.mojang.serialization.Codec;
import io.github.luckymcdev.groovyengine.GE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GE.MODID);

    // Fly speed, default 0.2f (same as vanilla)
    public static final Supplier<AttachmentType<Float>> FLY_SPEED = ATTACHMENT_TYPES.register(
            "fly_speed",
            () -> AttachmentType.builder(() -> 0.2f)
                    .serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat))
                    .copyOnDeath()
                    .build()

    );

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
