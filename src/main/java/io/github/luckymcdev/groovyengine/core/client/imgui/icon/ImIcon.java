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

package io.github.luckymcdev.groovyengine.core.client.imgui.icon;

public class ImIcon {
    private final char unicode;
    private final String name;

    public ImIcon(char unicode) {
        this(unicode, String.valueOf(unicode));
    }

    public ImIcon(char unicode, String name) {
        this.unicode = unicode;
        this.name = name;
    }

    /**
     * Returns the name of the icon.
     *
     * @return The name of the icon.
     */
    public String iconName() {
        return name;
    }

    /**
     * Returns the Unicode character for the icon.
     *
     * @return The Unicode character for the icon.
     */
    public char toChar() {
        return unicode;
    }

    /**
     * Returns the icon as a string, in the format of a single Unicode character.
     * This is useful for passing the icon to ImGui functions that expect a string.
     *
     * @return The icon as a string, in the format of a single Unicode character.
     */
    public String get() {
        return String.valueOf(toChar());
    }

    /**
     * Formats the icon and a label together into a string.
     * If the label is empty, returns the string representation of the icon.
     * Otherwise, returns a string consisting of the icon, followed by a space, followed by the label.
     *
     * @param label The label to format together with the icon.
     * @return The formatted string.
     */
    public String formatLabel(String label) {
        if (label.isEmpty()) {
            return toString();
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        var builder = new StringBuilder(2 + label.length());
        builder.append(toChar());
        builder.append(' ');
        builder.append(label);
        return builder.toString();
    }

    /**
     * Returns a string representation of the icon, in the format of a single Unicode character.
     *
     * @return A string representation of the icon, in the format of a single Unicode character.
     */
    @Override
    public String toString() {
        return String.valueOf(unicode);
    }
}
