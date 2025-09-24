package io.github.luckymcdev.groovyengine.util.color;

public class Color {
    public final float r;
    public final float g;
    public final float b;
    public final float a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int argb) {
        this.a = ((argb >> 24) & 0xFF) / 255.0f;
        this.r = ((argb >> 16) & 0xFF) / 255.0f;
        this.g = ((argb >> 8) & 0xFF) / 255.0f;
        this.b = (argb & 0xFF) / 255.0f;
    }

    public int argb() {
        return ((int)(a * 255) << 24) |
                ((int)(r * 255) << 16) |
                ((int)(g * 255) << 8) |
                (int)(b * 255);
    }

    public Color lerp(float factor, Color other) {
        return new Color(
                r + factor * (other.r - r),
                g + factor * (other.g - g),
                b + factor * (other.b - b),
                a + factor * (other.a - a)
        );
    }

    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    public float a() {
        return a;
    }
}