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

package io.github.luckymcdev.groovyengine.core.registry;

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

    private ModAttachmentTypes() {}

    /**
     * The attachment type for fly speed.
     * This attachment type is used to store the fly speed of a player.
     */
    public static final Supplier<AttachmentType<Float>> FLY_SPEED = ATTACHMENT_TYPES.register(
            "fly_speed",
            () -> AttachmentType.builder(() -> 0.2f)
                    .serialize(Codec.FLOAT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat))
                    .copyOnDeath()
                    .build()

    );

    /**
     * Registers the attachment types to the given event bus.
     *
     * @param bus the event bus to register to
     */
    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
