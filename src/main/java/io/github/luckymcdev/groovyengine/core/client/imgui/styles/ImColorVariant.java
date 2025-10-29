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

package io.github.luckymcdev.groovyengine.core.client.imgui.styles;

import io.github.luckymcdev.groovyengine.util.color.Color;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ImColorVariant {
    DEFAULT(new Color(0.26f, 0.59f, 0.98f, 0.40f),
            new Color(0.26f, 0.59f, 0.98f, 1.00f),
            new Color(0.06f, 0.53f, 0.98f, 1.00f),
            new Color(1.0f, 1.0f, 1.0f, 1.0f)),
    RED(new Color(0.78f, 0.17f, 0.17f, 0.40f),
            new Color(0.78f, 0.17f, 0.17f, 1.00f),
            new Color(0.88f, 0.27f, 0.27f, 1.00f),
            new Color(1.0f, 0.5f, 0.5f, 1.0f)),
    GREEN(new Color(0.17f, 0.78f, 0.17f, 0.40f),
            new Color(0.17f, 0.78f, 0.17f, 1.00f),
            new Color(0.27f, 0.88f, 0.27f, 1.00f),
            new Color(0.5f, 1.0f, 0.5f, 1.0f)),
    BLUE(new Color(0.17f, 0.17f, 0.78f, 0.40f),
            new Color(0.17f, 0.17f, 0.78f, 1.00f),
            new Color(0.27f, 0.27f, 0.88f, 1.00f),
            new Color(0.5f, 0.5f, 1.0f, 1.0f)),
    YELLOW(new Color(0.78f, 0.78f, 0.17f, 0.40f),
            new Color(0.78f, 0.78f, 0.17f, 1.00f),
            new Color(0.88f, 0.88f, 0.27f, 1.00f),
            new Color(1.0f, 1.0f, 0.5f, 1.0f));

    public final Color color;
    public final Color hoverColor;
    public final Color activeColor;
    public final Color textColor;

    ImColorVariant(Color color, Color hoverColor, Color activeColor, Color textColor) {
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.textColor = textColor;
    }
}