package io.github.luckymcdev.groovyengine.scribe.ui.data;

import io.github.luckymcdev.groovyengine.scribe.client.editor.ScribeWindow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ChestUIData {
    public String name = "New Chest UI";
    public String title = "Custom Chest";
    private Map<Integer, ChestSlotData> slots = new HashMap<>();

    public ChestSlotData getSlot(int index) {
        return slots.get(index);
    }

    public void setSlot(int index, ChestSlotData data) {
        slots.put(index, data);
    }

    public void clearSlot(int index) {
        slots.remove(index);
    }

    public void clearAllSlots() {
        slots.clear();
    }

    // Add this method to expose the slots map
    public Map<Integer, ChestSlotData> getSlots() {
        return slots;
    }

    public String toJson() {
        // Simple JSON representation for now
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(name).append("\",\n");
        json.append("  \"title\": \"").append(title).append("\",\n");
        json.append("  \"slots\": {\n");

        boolean first = true;
        for (Map.Entry<Integer, ChestSlotData> entry : slots.entrySet()) {
            if (!first) json.append(",\n");
            json.append("    \"").append(entry.getKey()).append("\": ");
            json.append(entry.getValue().toJson());
            first = false;
        }

        json.append("\n  }\n");
        json.append("}");
        return json.toString();
    }
}