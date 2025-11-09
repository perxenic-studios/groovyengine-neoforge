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

package io.github.luckymcdev.groovyengine.lens.rendering.util;

import io.github.luckymcdev.groovyengine.lens.rendering.core.LensRenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Utility interface for registering buffer objects to be destroyed when the game closes.
 */
@OnlyIn(Dist.CLIENT)
public interface IBufferObject {
    /**
     * Registers this buffer object to be destroyed when the game closes.
     * <p>
     * This method is a no-op by default, but can be overridden if needed.
     */
    default void registerBufferObject() {
        LensRenderSystem.registerBufferObject(this);
    }

    /**
     * Destroys this buffer object.
     * <p>
     * This method is called when the game closes, and is responsible for cleaning up any resources allocated by the buffer object.
     */
    void destroy();
}