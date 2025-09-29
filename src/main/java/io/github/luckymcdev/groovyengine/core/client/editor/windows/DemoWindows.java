package io.github.luckymcdev.groovyengine.core.client.editor.windows;

import imgui.ImGui;
import imgui.ImGuiIO;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemoWindows {

    public static class AboutWindow extends EditorWindow {
        public AboutWindow() {
            super("About", "about");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGui.showAboutWindow();
        }
    }

    public static class DemoWindow extends EditorWindow {
        public DemoWindow() {
            super("Demo", "demo");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGui.showDemoWindow();
        }
    }

    public static class MetricsWindow extends EditorWindow {
        public MetricsWindow() {
            super("Metrics", "metrics");
        }

        @Override
        public void render(ImGuiIO io) {
            ImGui.showMetricsWindow();
        }
    }

    public static class IconsWindow extends EditorWindow {

        public IconsWindow() {
            super(ImIcons.CIRCLE.get()+" Icons Window");
        }

        @Override
        public void render(ImGuiIO io) {
            ImUtil.window("IconsWindow", () -> {
                ImGui.spacing();

                ImGui.beginChild("IconsGrid");

                if (ImGui.collapsingHeader("Basic Shapes")) {
                    renderIconSection(new String[]{
                            "SQUARE", "CIRCLE", "HEXAGON", "ASTERIX", "STAR", "HEART"
                    });
                }

                if (ImGui.collapsingHeader("Arrows & Navigation")) {
                    renderIconSection(new String[]{
                            "ARROW_LEFT", "ARROW_RIGHT", "ARROW_UP", "ARROW_DOWN",
                            "EXPAND", "COLLAPSE", "SWAP", "ROTATE", "NAVIGATION", "TARGET"
                    });
                }

                if (ImGui.collapsingHeader("Actions & Operations")) {
                    renderIconSection(new String[]{
                            "ADD", "ADD_CIRCLE", "REMOVE", "DELETE", "CLOSE", "CHECK", "CHECK_CIRCLE",
                            "EDIT", "COPY", "CUT", "PASTE", "UNDO", "REDO", "RELOAD", "REPEAT",
                            "BACKSPACE", "OPEN", "DOWNLOAD", "SELECT", "BLOCK"
                    });
                }

                if (ImGui.collapsingHeader("Toggles & States")) {
                    renderIconSection(new String[]{
                            "TOGGLE_OFF", "TOGGLE_ON", "VISIBLE", "INVISIBLE",
                            "LOCK", "LOCK_OPEN", "FULLSCREEN"
                    });
                }

                if (ImGui.collapsingHeader("UI Elements")) {
                    renderIconSection(new String[]{
                            "MENU", "MORE", "MORE_VERTICAL", "SETTINGS", "WRENCH",
                            "SEARCH", "FILTER", "SORT", "QUESTION", "LABEL", "NO_LABEL",
                            "WIDGETS", "DASHBOARD", "LAYERS", "STACKS"
                    });
                }

                if (ImGui.collapsingHeader("Status & Alerts")) {
                    renderIconSection(new String[]{
                            "ERROR", "WARNING"
                    });
                }

                if (ImGui.collapsingHeader("Media Controls")) {
                    renderIconSection(new String[]{
                            "PLAY", "PAUSE", "STOP", "PLAY_CIRCLE", "PAUSE_CIRCLE", "STOP_CIRCLE",
                            "FAST_REWIND", "FAST_FORWARD", "MUSIC_NOTE", "MUSIC_NOTE_OFF", "TUNE"
                    });
                }

                if (ImGui.collapsingHeader("File System")) {
                    renderIconSection(new String[]{
                            "HOME_FILE", "FOLDER", "DOCUMENT", "TEXT_DOCUMENT", "NOTES",
                            "STORAGE", "DATABASE", "OPEN_IN_NEW"
                    });
                }

                if (ImGui.collapsingHeader("Data Visualization")) {
                    renderIconSection(new String[]{
                            "GRAPH", "FLOWCHART", "POLYLINE", "NUMBERS", "PERCENT"
                    });
                }

                if (ImGui.collapsingHeader("Nature & Environment")) {
                    renderIconSection(new String[]{
                            "SUN", "MOON", "FREEZE", "FIRE", "LEAF", "TREE", "WORLD",
                            "VOLCANO", "BOLT", "ROCKET", "ROCKET_LAUNCH", "EXPLOSION", "BOMB"
                    });
                }

                if (ImGui.collapsingHeader("Science & Technology")) {
                    renderIconSection(new String[]{
                            "SCIENCE", "SCIENCE_OFF", "EXPERIMENT", "HIVE", "BUG",
                            "MEMORY", "CODE", "ANIMATION", "BLUR"
                    });
                }

                if (ImGui.collapsingHeader("Animals & Biology")) {
                    renderIconSection(new String[]{
                            "PAW", "BONE", "EGG"
                    });
                }

                if (ImGui.collapsingHeader("Security & Access")) {
                    renderIconSection(new String[]{
                            "KEY", "PASSWORD", "PASSKEY", "ACCOUNT", "PERSON", "SHIELD"
                    });
                }

                if (ImGui.collapsingHeader("Tools & Utilities")) {
                    renderIconSection(new String[]{
                            "WRENCH", "TOOLTIP", "PALETTE", "CAMERA", "APERTURE",
                            "LIGHTBULB", "LIGHTBULB_OFF", "ANCHOR", "FLAG", "DIAMOND"
                    });
                }

                if (ImGui.collapsingHeader("Time & Scheduling")) {
                    renderIconSection(new String[]{
                            "SCHEDULE", "TIMELAPSE", "SPEED"
                    });
                }

                if (ImGui.collapsingHeader("Commerce")) {
                    renderIconSection(new String[]{
                            "MONEY", "PAID"
                    });
                }

                if (ImGui.collapsingHeader("Objects")) {
                    renderIconSection(new String[]{
                            "BACKPACK", "BACKPACK_OFF", "FRAMED_CUBE"
                    });
                }

                if (ImGui.collapsingHeader("Accessibility")) {
                    renderIconSection(new String[]{
                            "SWITCH_ACCESS", "SLASH"
                    });
                }

                ImGui.endChild();
            });
        }

        private void renderIconSection(String[] iconNames) {
            final int columns = 6;
            int iconCount = 0;

            for (String iconName : iconNames) {
                try {
                    java.lang.reflect.Field field = ImIcons.class.getField(iconName);
                    Object iconObj = field.get(null);

                    if (iconObj instanceof io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcon) {
                        io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcon icon =
                                (io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcon) iconObj;

                        if (iconCount % columns != 0) {
                            ImGui.sameLine();
                        }

                        ImUtil.pushIconScale();

                        ImGui.text(icon.get());

                        ImUtil.popIconScale();

                        if (ImGui.isItemHovered()) {
                            ImGui.beginTooltip();

                            ImUtil.pushIconScale(1.5f);
                            ImGui.text(icon.get());
                            ImUtil.popIconScale();

                            ImGui.sameLine();
                            ImGui.text(iconName);
                            ImGui.text("Unicode: U+" + Integer.toHexString(icon.toChar()).toUpperCase());
                            ImGui.text("Click to copy character");
                            ImGui.endTooltip();
                        }

                        if (ImGui.isItemClicked()) {
                            ImGui.setClipboardText(icon.get());
                        }

                        iconCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Icon not found: " + iconName);
                }
            }

            if (iconCount > 0) {
                ImGui.spacing();
            }
        }
    }
}