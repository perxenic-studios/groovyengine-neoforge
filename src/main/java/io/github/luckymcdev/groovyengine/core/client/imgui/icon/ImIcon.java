package io.github.luckymcdev.groovyengine.core.client.imgui.icon;

import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;

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

    public String iconName() {
        return name;
    }

    public char toChar() {
        return unicode;
    }

    public String get() {
        return String.valueOf(toChar());
    }

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

    @Override
    public String toString() {
        return String.valueOf(unicode);
    }
}
