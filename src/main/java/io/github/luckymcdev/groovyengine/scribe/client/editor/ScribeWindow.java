package io.github.luckymcdev.groovyengine.scribe.client.editor;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import io.github.luckymcdev.groovyengine.core.client.editor.core.window.EditorWindow;
import io.github.luckymcdev.groovyengine.core.client.imgui.styles.ImGraphics;
import io.github.luckymcdev.groovyengine.core.client.imgui.util.ImUtil;
import io.github.luckymcdev.groovyengine.scribe.ui.data.ChestSlotData;
import io.github.luckymcdev.groovyengine.scribe.ui.data.ChestUIData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.io.File;
import java.io.IOException;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ScribeWindow extends EditorWindow {

    private static final ResourceLocation CHEST_GUI_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final float SLOT_SIZE = 18.0f;
    private static final float CHEST_WIDTH = 260.0f;
    private static final float CHEST_HEIGHT = 260.0f;

    // UI State
    private ChestUIData currentUI = new ChestUIData();
    private int selectedSlot = -1;
    private boolean showItemPicker = false;
    private boolean showScriptEditor = false;
    private String searchFilter = "";
    private List<Item> filteredItems = new ArrayList<>();
    private String tempLeftClick = "";
    private String tempRightClick = "";

    // UI Layout
    private final int chestRows = 6; // 6 rows for large chest
    private final int chestCols = 9; // 9 columns

    public ScribeWindow() {
        super("UI Editor");
        initializeFilteredItems();
    }

    private void initializeFilteredItems() {
        filteredItems.clear();
        String filter = searchFilter.toLowerCase();

        for (Item item : BuiltInRegistries.ITEM) {
            String itemName = BuiltInRegistries.ITEM.getKey(item).toString();
            if (filter.isEmpty() || itemName.toLowerCase().contains(filter)) {
                filteredItems.add(item);
            }
        }

        // Limit to first 100 items for performance
        if (filteredItems.size() > 100) {
            filteredItems = filteredItems.subList(0, 100);
        }
    }

    @Override
    public void render(ImGuiIO io) {
        ImUtil.window("UI Editor", () -> {
            renderMenuBar();

            // Main layout - split into chest area and properties panel
            ImVec2 availableSize = ImGui.getContentRegionAvail();
            float panelWidth = 300;
            float chestAreaWidth = availableSize.x - panelWidth - 10;

            // Left side - Chest editor
            ImGui.beginChild("ChestArea", chestAreaWidth, 0, true);
            renderChestEditor();
            ImGui.endChild();

            ImGui.sameLine();

            // Right side - Properties panel
            ImGui.beginChild("PropertiesPanel", panelWidth, 0, true);
            renderPropertiesPanel();
            ImGui.endChild();

            // Modals
            if (showItemPicker) {
                renderItemPickerModal();
            }

            if (showScriptEditor) {
                renderScriptEditorModal();
            }
        });
    }

    private void renderMenuBar() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New", "Ctrl+N")) {
                    newChestUI();
                }
                if (ImGui.menuItem("Load", "Ctrl+O")) {
                    loadChestUI();
                }
                if (ImGui.menuItem("Save", "Ctrl+S")) {
                    saveChestUI();
                }
                if (ImGui.menuItem("Export", "Ctrl+E")) {
                    exportChestUI();
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Clear All Slots")) {
                    clearAllSlots();
                }
                if (ImGui.menuItem("Copy Selected Slot", "Ctrl+C", false, selectedSlot != -1)) {
                    copySlot();
                }
                if (ImGui.menuItem("Paste Slot", "Ctrl+V", false, hasClipboardData())) {
                    pasteSlot();
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Reset Zoom")) {
                    // Could implement zoom functionality
                }
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }
    }

    private void renderChestEditor() {
        ImGui.text("Chest UI Designer");
        ImGui.separator();

        // Center the chest in the available area
        ImVec2 availableSize = ImGui.getContentRegionAvail();
        float centerX = (availableSize.x - CHEST_WIDTH) * 0.5f;
        float centerY = 50; // Some padding from top

        ImGui.setCursorPos(centerX, centerY);

        // Draw chest background
        ImVec2 chestPos = ImGui.getCursorScreenPos();
        ImGraphics.texture(CHEST_GUI_TEXTURE, CHEST_WIDTH, CHEST_HEIGHT);

        // Draw slots and items
        renderChestSlots(chestPos);

        // Instructions
        ImGui.setCursorPosY(centerY + CHEST_HEIGHT + 20);
        ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "Click a slot to select it, then use the properties panel to configure items and actions.");
        ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "Right-click a slot to quickly clear it.");

        if(ImGui.button("Save")) {
            saveChestUI();
        }
    }

    private void renderChestSlots(ImVec2 chestPos) {
        // Calculate slot positions (based on Minecraft chest GUI layout)
        float startX = 8; // Offset from chest texture edge
        float startY = 18; // Offset from chest texture top

        for (int row = 0; row < chestRows; row++) {
            for (int col = 0; col < chestCols; col++) {
                int slotIndex = row * chestCols + col;

                float slotX = chestPos.x + startX + (col * SLOT_SIZE);
                float slotY = chestPos.y + startY + (row * SLOT_SIZE);

                // Check if mouse is over this slot
                ImVec2 mousePos = ImGui.getMousePos();
                boolean isHovered = mousePos.x >= slotX && mousePos.x < slotX + SLOT_SIZE &&
                        mousePos.y >= slotY && mousePos.y < slotY + SLOT_SIZE;

                // Highlight selected slot
                boolean isSelected = selectedSlot == slotIndex;

                if (isSelected || isHovered) {
                    int color = isSelected ? 0x80FFFFFF : 0x40FFFFFF; // White highlight
                    ImGui.getWindowDrawList().addRectFilled(
                            slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, color
                    );
                }

                // Draw item if present
                ChestSlotData slotData = currentUI.getSlot(slotIndex);
                if (slotData != null && slotData.item != null) {
                    renderItemInSlot(slotData.item, slotX, slotY);

                    // Draw stack size if > 1
                    if (slotData.stackSize > 1) {
                        ImGui.getWindowDrawList().addText(
                                slotX + 10, slotY + 10, 0xFFFFFFFF, String.valueOf(slotData.stackSize)
                        );
                    }
                }

                // Handle clicks
                if (isHovered && ImGui.isMouseClicked(0)) { // Left click
                    selectedSlot = slotIndex;
                }
                if (isHovered && ImGui.isMouseClicked(1)) { // Right click
                    currentUI.clearSlot(slotIndex);
                    if (selectedSlot == slotIndex) {
                        selectedSlot = -1;
                    }
                }
            }
        }
    }

    private void renderItemInSlot(Item item, float x, float y) {
        ItemStack stack = new ItemStack(item);
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon();
        ResourceLocation atlas = InventoryMenu.BLOCK_ATLAS;

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();

        long textureId = ImGraphics.getTextureId(atlas);
        ImGui.getWindowDrawList().addImage(
                textureId,
                x, y,
                x + SLOT_SIZE, y + SLOT_SIZE,
                u0, v0, u1, v1
        );

        ImVec2 mousePos = ImGui.getMousePos();
        if (mousePos.x >= x && mousePos.x < x + SLOT_SIZE && mousePos.y >= y && mousePos.y < y + SLOT_SIZE) {
            ImGui.setTooltip(BuiltInRegistries.ITEM.getKey(item).toString());
        }
    }

    private void renderPropertiesPanel() {
        ImGui.text("Properties");
        ImGui.separator();

        // UI Name
        ImString uiName = new ImString(currentUI.name, 256);
        if (ImGui.inputText("UI Name", uiName)) {
            currentUI.name = uiName.get();
        }

        // UI Title (displayed to player)
        ImString uiTitle = new ImString(currentUI.title, 256);
        if (ImGui.inputText("Display Title", uiTitle)) {
            currentUI.title = uiTitle.get();
        }

        ImGui.separator();

        if (selectedSlot == -1) {
            ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "Select a slot to edit its properties");
            return;
        }

        ImGui.text("Slot " + selectedSlot + " Properties");
        ImGui.separator();

        ChestSlotData slotData = currentUI.getSlot(selectedSlot);
        if (slotData == null) {
            slotData = new ChestSlotData();
            currentUI.setSlot(selectedSlot, slotData);
        }

        // Item selection
        String itemDisplayName = slotData.item != null ?
                BuiltInRegistries.ITEM.getKey(slotData.item).toString() : "None";

        if (ImGui.button("Item: " + itemDisplayName, 250, 0)) {
            showItemPicker = true;
        }

        // Stack size
        ImInt stackSize = new ImInt(slotData.stackSize);
        if (ImGui.sliderInt("Stack Size", stackSize.getData(), 1, 64)) {
            slotData.stackSize = stackSize.get();
        }

        // Display name override
        ImString displayName = new ImString(slotData.displayName != null ? slotData.displayName : "", 256);
        if (ImGui.inputText("Display Name", displayName)) {
            slotData.displayName = displayName.get().isEmpty() ? null : displayName.get();
        }

        ImGui.separator();

        // Script actions
        ImGui.text("Click Actions");

        // Left click action
        String leftClickDisplay = slotData.leftClickAction != null ? slotData.leftClickAction : "None";
        if (ImGui.button("Left Click: " + leftClickDisplay, 250, 0)) {
            tempLeftClick = slotData.leftClickAction != null ? slotData.leftClickAction : "";
            showScriptEditor = true;
        }

        // Right click action
        String rightClickDisplay = slotData.rightClickAction != null ? slotData.rightClickAction : "None";
        if (ImGui.button("Right Click: " + rightClickDisplay, 250, 0)) {
            tempRightClick = slotData.rightClickAction != null ? slotData.rightClickAction : "";
            showScriptEditor = true;
        }

        ImGui.separator();

        // Slot options
        ImBoolean moveable = new ImBoolean(slotData.moveable);
        if (ImGui.checkbox("Players can move this item", moveable)) {
            slotData.moveable = moveable.get();
        }

        ImBoolean visible = new ImBoolean(slotData.visible);
        if (ImGui.checkbox("Slot is visible", visible)) {
            slotData.visible = visible.get();
        }

        // Clear slot button
        if (ImGui.button("Clear Slot", 100, 0)) {
            currentUI.clearSlot(selectedSlot);
            selectedSlot = -1;
        }
    }

    private void renderItemPickerModal() {
        ImGui.setNextWindowSize(500, 400);
        if (ImGui.beginPopupModal("Select Item", ImGuiWindowFlags.NoResize)) {

            // Search filter
            ImString searchArray = new ImString(searchFilter, 256);
            if (ImGui.inputText("Search", searchArray)) {
                searchFilter = searchArray.get();
                initializeFilteredItems();
            }

            ImGui.separator();

            // Item list
            if (ImGui.beginListBox("Items", -1, 300)) {
                for (Item item : filteredItems) {
                    String itemName = BuiltInRegistries.ITEM.getKey(item).toString();
                    if (ImGui.selectable(itemName)) {
                        ChestSlotData slotData = currentUI.getSlot(selectedSlot);
                        if (slotData == null) {
                            slotData = new ChestSlotData();
                            currentUI.setSlot(selectedSlot, slotData);
                        }
                        slotData.item = item;
                        showItemPicker = false;
                        ImGui.closeCurrentPopup();
                    }
                }
                ImGui.endListBox();
            }

            if (ImGui.button("Cancel")) {
                showItemPicker = false;
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }

        if (showItemPicker && !ImGui.isPopupOpen("Select Item")) {
            ImGui.openPopup("Select Item");
        }
    }

    private void renderScriptEditorModal() {
        ImGui.setNextWindowSize(400, 300);
        if (ImGui.beginPopupModal("Edit Click Action", ImGuiWindowFlags.NoResize)) {

            ImGui.text("Enter script method in format: ScriptName.methodName");
            ImGui.text("Example: MyCustomScript.stickLeftClick");
            ImGui.separator();

            // Left click action input
            ImGui.text("Left Click Action:");
            ImString leftClickArray = new ImString(tempLeftClick, 256);
            ImGui.inputText("##leftclick", leftClickArray);
            tempLeftClick = leftClickArray.get();

            ImGui.separator();

            // Right click action input
            ImGui.text("Right Click Action:");
            ImString rightClickArray = new ImString(tempRightClick, 256);
            ImGui.inputText("##rightclick", rightClickArray);
            tempRightClick = rightClickArray.get();

            ImGui.separator();

            // Validation
            boolean leftValid = validateScriptMethod(tempLeftClick);
            boolean rightValid = validateScriptMethod(tempRightClick);

            if (!tempLeftClick.isEmpty() && !leftValid) {
                ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, "Invalid left click format");
            }
            if (!tempRightClick.isEmpty() && !rightValid) {
                ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, "Invalid right click format");
            }

            // Buttons
            if (ImGui.button("Save")) {
                ChestSlotData slotData = currentUI.getSlot(selectedSlot);
                if (slotData != null) {
                    slotData.leftClickAction = tempLeftClick.isEmpty() ? null : tempLeftClick;
                    slotData.rightClickAction = tempRightClick.isEmpty() ? null : tempRightClick;
                }
                showScriptEditor = false;
                ImGui.closeCurrentPopup();
            }

            ImGui.sameLine();

            if (ImGui.button("Cancel")) {
                showScriptEditor = false;
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }

        if (showScriptEditor && !ImGui.isPopupOpen("Edit Click Action")) {
            ImGui.openPopup("Edit Click Action");
        }
    }

    private boolean validateScriptMethod(String scriptMethod) {
        if (scriptMethod == null || scriptMethod.trim().isEmpty()) {
            return true; // Empty is valid (means no action)
        }

        String trimmed = scriptMethod.trim();
        return trimmed.matches("^[A-Za-z_][A-Za-z0-9_]*\\.[A-Za-z_][A-Za-z0-9_]*$");
    }

    // Helper methods
    private void newChestUI() {
        currentUI = new ChestUIData();
        selectedSlot = -1;
    }

    private void loadChestUI() {
        // TODO: Implement file loading
        System.out.println("Load UI - Not implemented yet");
    }

    private void saveChestUI() {
        // Save UI JSON
        System.out.println("Save UI: \n" + currentUI.toJson());
    }

    private void exportChestUI() {
        // TODO: Implement export to game format
        System.out.println("Export UI - Not implemented yet");
    }

    private void clearAllSlots() {
        currentUI.clearAllSlots();
        selectedSlot = -1;
    }

    private void copySlot() {
        // TODO: Implement clipboard functionality
        System.out.println("Copy slot " + selectedSlot);
    }

    private void pasteSlot() {
        // TODO: Implement paste functionality
        System.out.println("Paste to slot " + selectedSlot);
    }

    private boolean hasClipboardData() {
        // TODO: Check if clipboard has slot data
        return false;
    }
}