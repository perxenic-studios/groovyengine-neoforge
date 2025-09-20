package io.github.luckymcdev.groovyengine.threads.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorCoordinates;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.texteditor.GETextEditorLanguageDefinitions;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;

import java.util.*;

public class ThreadsWindow extends EditorWindow {

    private static final TextEditor editor = new TextEditor();
    private static final CodeAssistant codeAssistant = new CodeAssistant();

    // UI state
    private boolean showAutocomplete = false;
    private List<String> autocompleteSuggestions = new ArrayList<>();
    private int selectedSuggestion = 0;
    private String currentLanguage = "Java";
    private boolean showHoverTooltip = false;
    private String hoverText = "";
    private ImVec2 hoverPos = new ImVec2();

    // File management
    private String currentFileName = "untitled.java";
    private boolean hasUnsavedChanges = false;
    private String lastSavedContent = "";

    // Input handling
    private String previousText = "";
    private TextEditorCoordinates previousCursorPos = new TextEditorCoordinates(0, 0);
    private boolean spaceKeyConsumed = false;

    public ThreadsWindow() {
        super("Code Editor");
        initializeEditor();
    }

    private void initializeEditor() {
        // Set initial Java content
        String initialCode = """
            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello, World!");
                    
                    // Try typing 'System.' to see autocomplete
                    // Or 'String.' for string methods
                }
            }""";

        editor.setText(initialCode);
        editor.setLanguageDefinition(GETextEditorLanguageDefinitions.getJavaDef());
        editor.setPalette(editor.getDarkPalette());
        editor.setShowWhitespaces(false);
        editor.setTabSize(4);

        lastSavedContent = initialCode;
        previousText = initialCode;
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window("Code Editor", () -> {
            renderMenuBar();
            renderEditorArea(io);
            renderStatusBar();

            // Handle autocomplete popup
            if (showAutocomplete) {
                renderAutocompletePopup();
            }

            // Handle hover tooltip
            if (showHoverTooltip) {
                renderHoverTooltip();
            }
        });

        // Check for unsaved changes
        String currentContent = editor.getText();
        hasUnsavedChanges = !currentContent.equals(lastSavedContent);
    }

    private void renderMenuBar() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New", "Ctrl+N")) {
                    newFile();
                }
                if (ImGui.menuItem("Save", "Ctrl+S")) {
                    saveFile();
                }
                if (ImGui.menuItem("Save As...", "Ctrl+Shift+S")) {
                    saveAsFile();
                }
                ImGui.separator();
                if (ImGui.beginMenu("Language")) {
                    if (ImGui.menuItem("Java", "", currentLanguage.equals("Java"))) {
                        setLanguage("Java");
                    }
                    if (ImGui.menuItem("Groovy", "", currentLanguage.equals("Groovy"))) {
                        setLanguage("Groovy");
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Undo", "Ctrl+Z", false, editor.canUndo())) {
                    editor.undo();
                }
                if (ImGui.menuItem("Redo", "Ctrl+Y", false, editor.canRedo())) {
                    editor.redo();
                }
                ImGui.separator();
                if (ImGui.menuItem("Cut", "Ctrl+X", false, editor.hasSelection())) {
                    editor.cut();
                }
                if (ImGui.menuItem("Copy", "Ctrl+C", false, editor.hasSelection())) {
                    editor.copy();
                }
                if (ImGui.menuItem("Paste", "Ctrl+V")) {
                    editor.paste();
                }
                ImGui.separator();
                if (ImGui.menuItem("Select All", "Ctrl+A")) {
                    editor.selectAll();
                }
                if (ImGui.menuItem("Select Word", "Ctrl+W")) {
                    editor.selectWordUnderCursor();
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                boolean showWhitespaces = editor.isShowingWhitespaces();
                if (ImGui.menuItem("Show Whitespaces", "", showWhitespaces)) {
                    editor.setShowWhitespaces(!showWhitespaces);
                }

                boolean readOnly = editor.isReadOnly();
                if (ImGui.menuItem("Read Only", "", readOnly)) {
                    editor.setReadOnly(!readOnly);
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Tools")) {
                if (ImGui.menuItem("Trigger Autocomplete", "Ctrl+Space")) {
                    triggerAutocomplete();
                }
                if (ImGui.menuItem("Add Error Marker")) {
                    addTestErrorMarker();
                }
                if (ImGui.menuItem("Clear Error Markers")) {
                    clearErrorMarkers();
                }
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }
    }

    private void renderEditorArea(ImGuiIO io) {
        // Get available space for editor
        ImVec2 availableSize = ImGui.getContentRegionAvail();
        availableSize.y -= 25; // Reserve space for status bar

        // Handle keyboard shortcuts BEFORE rendering editor
        boolean shouldRenderEditor = handleKeyboardShortcuts(io);

        // Only render editor if no special keys were consumed
        if (shouldRenderEditor) {
            // Render the text editor
            editor.render("TextEditor", availableSize);
        } else {
            // Render editor in read-only mode temporarily to prevent input
            boolean wasReadOnly = editor.isReadOnly();
            editor.setReadOnly(true);
            editor.render("TextEditor", availableSize);
            editor.setReadOnly(wasReadOnly);
        }

        // Handle autocomplete and text changes
        handleTextChanges();

        // Handle hover information
        handleHoverInfo();
    }

    private boolean handleKeyboardShortcuts(ImGuiIO io) {
        if (io.getKeyCtrl()) {
            // Use character codes for key detection
            if (ImGui.isKeyPressed(83)) { // S key
                if (io.getKeyShift()) {
                    saveAsFile();
                } else {
                    saveFile();
                }
                return true;
            } else if (ImGui.isKeyPressed(78)) { // N key
                newFile();
                return true;
            } else if (ImGui.isKeyPressed(32)) { // Space key
                if (!spaceKeyConsumed) {
                    triggerAutocomplete();
                    spaceKeyConsumed = true;
                }
                return false; // Prevent editor from processing this frame
            } else if (ImGui.isKeyPressed(65)) { // A key
                editor.selectAll();
                return true;
            } else if (ImGui.isKeyPressed(87)) { // W key
                editor.selectWordUnderCursor();
                return true;
            }
        }

        // Reset space key flag when key is released
        if (!ImGui.isKeyPressed(32)) {
            spaceKeyConsumed = false;
        }

        return true; // Allow normal editor processing
    }

    private void handleTextChanges() {
        String currentText = editor.getText();
        TextEditorCoordinates currentCursor = editor.getCursorPosition();

        // Check if text or cursor position changed
        boolean textChanged = editor.isTextChanged() || !currentText.equals(previousText);
        boolean cursorChanged = editor.isCursorPositionChanged() || !currentCursor.equals(previousCursorPos);

        if (textChanged || cursorChanged) {
            checkAutocompleteTrigger(currentText, currentCursor);
            previousText = currentText;
            previousCursorPos = new TextEditorCoordinates(currentCursor);
        }

        // Handle autocomplete navigation
        if (showAutocomplete) {
            if (ImGui.isKeyPressed(264)) { // Down arrow
                selectedSuggestion = Math.min(autocompleteSuggestions.size() - 1, selectedSuggestion + 1);
            } else if (ImGui.isKeyPressed(265)) { // Up arrow
                selectedSuggestion = Math.max(0, selectedSuggestion - 1);
            } else if (ImGui.isKeyPressed(257) || ImGui.isKeyPressed(258)) { // Enter or Tab
                if (selectedSuggestion < autocompleteSuggestions.size()) {
                    insertAutocompleteSuggestion(autocompleteSuggestions.get(selectedSuggestion));
                }
                showAutocomplete = false;
            } else if (ImGui.isKeyPressed(256)) { // Escape
                showAutocomplete = false;
            }
        }
    }

    private void checkAutocompleteTrigger(String currentText, TextEditorCoordinates cursorPos) {
        // Get current line
        String currentLine = editor.getCurrentLineText();

        // Check if we should show autocomplete
        if (cursorPos.mColumn > 0 && cursorPos.mColumn <= currentLine.length()) {
            String beforeCursor = currentLine.substring(0, cursorPos.mColumn);

            if (beforeCursor.endsWith(".")) {
                String objectName = extractObjectName(beforeCursor);
                if (!objectName.isEmpty()) {
                    autocompleteSuggestions = codeAssistant.getSuggestions(objectName, currentLanguage);
                    if (!autocompleteSuggestions.isEmpty()) {
                        showAutocomplete = true;
                        selectedSuggestion = 0;
                        return;
                    }
                }
            }
        }

        // Hide autocomplete if conditions not met
        if (showAutocomplete && (cursorPos.mColumn == 0 ||
                !currentLine.substring(0, Math.min(cursorPos.mColumn, currentLine.length())).matches(".*\\w+\\.$"))) {
            showAutocomplete = false;
        }
    }

    private void handleHoverInfo() {
        if (ImGui.isItemHovered()) {
            // Get word under cursor
            String wordUnderCursor = getWordUnderCursor();
            if (!wordUnderCursor.isEmpty()) {
                String hoverInfo = codeAssistant.getHoverInfo(wordUnderCursor, currentLanguage);
                if (!hoverInfo.isEmpty()) {
                    hoverText = hoverInfo;
                    hoverPos = ImGui.getMousePos();
                    showHoverTooltip = true;
                } else {
                    showHoverTooltip = false;
                }
            } else {
                showHoverTooltip = false;
            }
        } else {
            showHoverTooltip = false;
        }
    }

    private String getWordUnderCursor() {
        TextEditorCoordinates cursor = editor.getCursorPosition();
        String currentLine = editor.getCurrentLineText();

        if (cursor.mColumn < currentLine.length()) {
            // Find word boundaries
            int start = cursor.mColumn;
            int end = cursor.mColumn;

            // Move start backward to beginning of word
            while (start > 0 && Character.isLetterOrDigit(currentLine.charAt(start - 1))) {
                start--;
            }

            // Move end forward to end of word
            while (end < currentLine.length() && Character.isLetterOrDigit(currentLine.charAt(end))) {
                end++;
            }

            if (end > start) {
                return currentLine.substring(start, end);
            }
        }
        return "";
    }

    private void renderAutocompletePopup() {
        // Calculate the position based on the text editor cursor position
        TextEditorCoordinates cursor = editor.getCursorPosition();

        float charWidth = ImGui.calcTextSize("M").x; // width of one monospaced char
        float lineHeight = ImGui.getTextLineHeightWithSpacing(); // line height including spacing

        // Get the current window position and the editor's position within it
        ImVec2 windowPos = ImGui.getWindowPos();
        ImVec2 editorStart = new ImVec2(windowPos.x + 8, windowPos.y + 60); // Approximate editor start position

        // Calculate popup position based on cursor
        float popupX = editorStart.x + (cursor.mColumn * charWidth);
        float popupY = editorStart.y + ((cursor.mLine + 1) * lineHeight);

        ImGui.setNextWindowPos(popupX, popupY);
        ImGui.setNextWindowSize(300, Math.min(200, autocompleteSuggestions.size() * 22 + 10));

        if (ImGui.begin("##Autocomplete", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoFocusOnAppearing)) {

            for (int i = 0; i < autocompleteSuggestions.size(); i++) {
                String suggestion = autocompleteSuggestions.get(i);
                boolean isSelected = (i == selectedSuggestion);

                if (isSelected) {
                    ImGui.pushStyleColor(imgui.flag.ImGuiCol.Text, 1.0f, 1.0f, 0.0f, 1.0f);
                }

                if (ImGui.selectable(suggestion, isSelected)) {
                    insertAutocompleteSuggestion(suggestion);
                    showAutocomplete = false;
                }

                if (isSelected) {
                    ImGui.popStyleColor();
                    ImGui.setItemDefaultFocus();
                }
            }

            ImGui.end();
        }
    }

    private void renderHoverTooltip() {
        ImGui.setNextWindowPos(hoverPos.x + 10, hoverPos.y - 25);
        ImGui.setTooltip(hoverText);
    }

    private void renderStatusBar() {
        TextEditorCoordinates cursorPos = editor.getCursorPosition();
        String status = String.format("%s | Line: %d, Column: %d | %s%s | Total Lines: %d",
                currentLanguage,
                cursorPos.mLine + 1,
                cursorPos.mColumn + 1,
                currentFileName,
                hasUnsavedChanges ? " *" : "",
                editor.getTotalLines());

        if (editor.hasSelection()) {
            String selectedText = editor.getSelectedText();
            if (selectedText != null) {
                int lineCount = selectedText.split("\n").length;
                status += String.format(" | Sel: %d chars (%d lines)",
                        selectedText.length(), lineCount);
            }
        }

        if (editor.isOverwrite()) {
            status += " | OVR";
        }

        if (editor.isReadOnly()) {
            status += " | READ-ONLY";
        }

        ImGui.text(status);
    }

    // Helper methods
    private void triggerAutocomplete() {
        autocompleteSuggestions = codeAssistant.getGeneralSuggestions(currentLanguage);
        if (!autocompleteSuggestions.isEmpty()) {
            showAutocomplete = true;
            selectedSuggestion = 0;
        }
    }

    private String extractObjectName(String text) {
        text = text.trim();
        if (text.endsWith(".")) {
            text = text.substring(0, text.length() - 1);
        }

        // Split by whitespace and operators to get the last identifier
        String[] parts = text.split("[\\s\\(\\)\\{\\}\\[\\]\\+\\-\\*\\/\\=\\!\\<\\>\\&\\|\\^\\%\\;\\,]+");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            lastPart = lastPart.replaceAll("[^a-zA-Z0-9_]", "");
            return lastPart;
        }
        return "";
    }

    private void insertAutocompleteSuggestion(String suggestion) {
        // Use the TextEditor's insertText method
        editor.insertText(suggestion);
        showAutocomplete = false;
    }

    private void addTestErrorMarker() {
        // Add an error marker at the current line
        TextEditorCoordinates cursor = editor.getCursorPosition();
        Map<Integer, String> errorMarkers = new HashMap<>();
        errorMarkers.put(cursor.mLine, "Test error: This is a sample error message");
        editor.setErrorMarkers(errorMarkers);
    }

    private void clearErrorMarkers() {
        editor.setErrorMarkers(new HashMap<>());
    }

    private void newFile() {
        editor.setText("");
        currentFileName = "untitled." + (currentLanguage.equals("Java") ? "java" : "groovy");
        lastSavedContent = "";
        hasUnsavedChanges = false;
        previousText = "";
        showAutocomplete = false;
    }

    private void saveFile() {
        // TODO: Implement actual file saving logic here
        lastSavedContent = editor.getText();
        hasUnsavedChanges = false;
        System.out.println("File saved: " + currentFileName);
    }

    private void saveAsFile() {
        // TODO: Implement save as dialog here
        saveFile();
    }

    private void setLanguage(String language) {
        currentLanguage = language;
        if (language.equals("Java")) {
            editor.setLanguageDefinition(GETextEditorLanguageDefinitions.getJavaDef());
            if (currentFileName.endsWith(".groovy")) {
                currentFileName = currentFileName.replace(".groovy", ".java");
            }
        } else if (language.equals("Groovy")) {
            editor.setLanguageDefinition(GETextEditorLanguageDefinitions.getGroovyDef());
            if (currentFileName.endsWith(".java")) {
                currentFileName = currentFileName.replace(".java", ".groovy");
            }
        }
    }
}