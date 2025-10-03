package io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme;

import imgui.ImGui;
import imgui.ImGuiStyle;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.theme.defaults.*;

public class ImThemes {
    private static final ImGuiStyle STYLE = ImGui.getStyle();

    private static final BessDarkImTheme BESS_DARK_IM_THEME = new BessDarkImTheme();
    private static final CatpuccinMochaImTheme CATPUCCIN_MOCHA_IM_THEME = new CatpuccinMochaImTheme();
    private static final ModernDarkImTheme MODERN_DARK_IM_THEME = new ModernDarkImTheme();
    private static final DarkImTheme DARK_IM_THEME = new DarkImTheme();
    private static final CherryImTheme CHERRY_IM_THEME = new CherryImTheme();


    public static void applyBessDark() {
        BESS_DARK_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: "+BESS_DARK_IM_THEME.getName());
    }

    public static void applyCherry() {
        CHERRY_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: "+CHERRY_IM_THEME.getName());
    }

    public static void applyCatpuccinMocha() {
        CATPUCCIN_MOCHA_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: "+CATPUCCIN_MOCHA_IM_THEME.getName());
    }

    public static void applyModernDark() {
        MODERN_DARK_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: "+MODERN_DARK_IM_THEME.getName());
    }

    public static void applyDark() {
        DARK_IM_THEME.applyTheme(STYLE);
        GE.CORE_LOG.debug("Applied ImGui theme: "+DARK_IM_THEME.getName());
    }

}
