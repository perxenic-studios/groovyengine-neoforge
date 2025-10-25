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