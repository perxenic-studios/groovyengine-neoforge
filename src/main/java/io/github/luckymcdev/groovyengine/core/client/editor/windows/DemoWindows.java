package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.ImGe;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcon;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.lang.reflect.Field;

@OnlyIn(Dist.CLIENT)
public class DemoWindows {

    public static class AboutWindow extends EditorWindow {
        public AboutWindow() {
            super(ImIcons.QUESTION.get() + " About", "about");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGe.showAboutWindow();
        }
    }

    public static class DemoWindow extends EditorWindow {
        public DemoWindow() {
            super(ImIcons.DASHBOARD.get() + " Demo", "demo");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGe.showDemoWindow();
        }
    }

    public static class ImGuiMetricsWindow extends EditorWindow {
        public ImGuiMetricsWindow() {
            super(ImIcons.GRAPH.get() + " Metrics", "metrics");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGe.showMetricsWindow();
        }
    }

    public static class IconsWindow extends EditorWindow {

        public IconsWindow() {
            super(ImIcons.CIRCLE.get() + " Icons");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGe.window("IconsWindow", () -> {
                ImGe.spacing();

                ImGe.beginChild("IconsGrid");

                if (ImGe.collapsingHeader("Basic Shapes")) {
                    renderIconSection(new String[]{
                            "SQUARE", "CIRCLE", "HEXAGON", "ASTERIX", "STAR", "HEART"
                    });
                }

                if (ImGe.collapsingHeader("Arrows & Navigation")) {
                    renderIconSection(new String[]{
                            "ARROW_LEFT", "ARROW_RIGHT", "ARROW_UP", "ARROW_DOWN",
                            "EXPAND", "COLLAPSE", "SWAP", "ROTATE", "NAVIGATION", "TARGET"
                    });
                }

                if (ImGe.collapsingHeader("Actions & Operations")) {
                    renderIconSection(new String[]{
                            "ADD", "ADD_CIRCLE", "REMOVE", "DELETE", "CLOSE", "CHECK", "CHECK_CIRCLE",
                            "EDIT", "COPY", "CUT", "PASTE", "UNDO", "REDO", "RELOAD", "REPEAT",
                            "BACKSPACE", "OPEN", "DOWNLOAD", "SELECT", "BLOCK"
                    });
                }

                if (ImGe.collapsingHeader("Toggles & States")) {
                    renderIconSection(new String[]{
                            "TOGGLE_OFF", "TOGGLE_ON", "VISIBLE", "INVISIBLE",
                            "LOCK", "LOCK_OPEN", "FULLSCREEN"
                    });
                }

                if (ImGe.collapsingHeader("UI Elements")) {
                    renderIconSection(new String[]{
                            "MENU", "MORE", "MORE_VERTICAL", "SETTINGS", "WRENCH",
                            "SEARCH", "FILTER", "SORT", "QUESTION", "LABEL", "NO_LABEL",
                            "WIDGETS", "DASHBOARD", "LAYERS", "STACKS"
                    });
                }

                if (ImGe.collapsingHeader("Status & Alerts")) {
                    renderIconSection(new String[]{
                            "ERROR", "WARNING"
                    });
                }

                if (ImGe.collapsingHeader("Media Controls")) {
                    renderIconSection(new String[]{
                            "PLAY", "PAUSE", "STOP", "PLAY_CIRCLE", "PAUSE_CIRCLE", "STOP_CIRCLE",
                            "FAST_REWIND", "FAST_FORWARD", "MUSIC_NOTE", "MUSIC_NOTE_OFF", "TUNE"
                    });
                }

                if (ImGe.collapsingHeader("File System")) {
                    renderIconSection(new String[]{
                            "HOME_FILE", "FOLDER", "DOCUMENT", "TEXT_DOCUMENT", "NOTES",
                            "STORAGE", "DATABASE", "OPEN_IN_NEW"
                    });
                }

                if (ImGe.collapsingHeader("Data Visualization")) {
                    renderIconSection(new String[]{
                            "GRAPH", "FLOWCHART", "POLYLINE", "NUMBERS", "PERCENT"
                    });
                }

                if (ImGe.collapsingHeader("Nature & Environment")) {
                    renderIconSection(new String[]{
                            "SUN", "MOON", "FREEZE", "FIRE", "LEAF", "TREE", "WORLD",
                            "VOLCANO", "BOLT", "ROCKET", "ROCKET_LAUNCH", "EXPLOSION", "BOMB"
                    });
                }

                if (ImGe.collapsingHeader("Science & Technology")) {
                    renderIconSection(new String[]{
                            "SCIENCE", "SCIENCE_OFF", "EXPERIMENT", "HIVE", "BUG",
                            "MEMORY", "CODE", "ANIMATION", "BLUR"
                    });
                }

                if (ImGe.collapsingHeader("Animals & Biology")) {
                    renderIconSection(new String[]{
                            "PAW", "BONE", "EGG"
                    });
                }

                if (ImGe.collapsingHeader("Security & Access")) {
                    renderIconSection(new String[]{
                            "KEY", "PASSWORD", "PASSKEY", "ACCOUNT", "PERSON", "SHIELD"
                    });
                }

                if (ImGe.collapsingHeader("Tools & Utilities")) {
                    renderIconSection(new String[]{
                            "WRENCH", "TOOLTIP", "PALETTE", "CAMERA", "APERTURE",
                            "LIGHTBULB", "LIGHTBULB_OFF", "ANCHOR", "FLAG", "DIAMOND"
                    });
                }

                if (ImGe.collapsingHeader("Time & Scheduling")) {
                    renderIconSection(new String[]{
                            "SCHEDULE", "TIMELAPSE", "SPEED"
                    });
                }

                if (ImGe.collapsingHeader("Commerce")) {
                    renderIconSection(new String[]{
                            "MONEY", "PAID"
                    });
                }

                if (ImGe.collapsingHeader("Objects")) {
                    renderIconSection(new String[]{
                            "BACKPACK", "BACKPACK_OFF", "FRAMED_CUBE"
                    });
                }

                if (ImGe.collapsingHeader("Accessibility")) {
                    renderIconSection(new String[]{
                            "SWITCH_ACCESS", "SLASH"
                    });
                }

                ImGe.endChild();
            });
        }

        private void renderIconSection(String[] iconNames) {
            final int columns = 6;
            int iconCount = 0;

            for (String iconName : iconNames) {
                try {
                    Field field = ImIcons.class.getField(iconName);
                    Object iconObj = field.get(null);

                    if (iconObj instanceof ImIcon icon) {

                        if (iconCount % columns != 0) {
                            ImGe.sameLine();
                        }

                        ImGe.pushIconScale();

                        ImGe.text(icon.get());

                        ImGe.popIconScale();

                        if (ImGe.isItemHovered()) {
                            ImGe.beginTooltip();

                            ImGe.pushIconScale(1.5f);
                            ImGe.text(icon.get());
                            ImGe.popIconScale();

                            ImGe.sameLine();
                            ImGe.text(iconName);
                            ImGe.text("Unicode: U+" + Integer.toHexString(icon.toChar()).toUpperCase());
                            ImGe.text("Click to copy character");
                            ImGe.endTooltip();
                        }

                        if (ImGe.isItemClicked()) {
                            ImGe.setClipboardText(icon.get());
                        }

                        iconCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Icon not found: " + iconName);
                }
            }

            if (iconCount > 0) {
                ImGe.spacing();
            }
        }
    }
}