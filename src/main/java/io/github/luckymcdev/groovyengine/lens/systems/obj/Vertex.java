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

package io.github.luckymcdev.groovyengine.lens.systems.obj;

import net.minecraft.world.phys.Vec2;
import org.joml.Vector3f;

/**
 * A record to keep track of a Vertex.
 *
 * @param position the Position of the Vertex
 * @param normal   the Normal of the Vertex
 * @param uv       the UV cord of the Vertex
 */
public record Vertex(Vector3f position, Vector3f normal, Vec2 uv) {
}