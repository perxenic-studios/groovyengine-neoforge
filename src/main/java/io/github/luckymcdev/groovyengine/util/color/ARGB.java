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

package io.github.luckymcdev.groovyengine.util.color;

public class ARGB {
    /**
     * Converts an ARGB color value to an ABGR color value.
     * An ARGB color value is an integer with the following layout:
     * <pre>
     * 0xAARRRGGGB
     * </pre>
     * Where AA is the alpha component, RR is the red component,
     * GG is the green component, and BB is the blue component.
     * An ABGR color value is an integer with the following layout:
     * <pre>
     * 0xAABBGGRR
     * </pre>
     * Where AA is the alpha component, BB is the blue component,
     * GG is the green component, and RR is the red component.
     *
     * @param argb the ARGB color value to convert
     * @return the ABGR color value
     */
    public static int toABGR(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }
}