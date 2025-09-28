package io.github.luckymcdev.groovyengine.scribe.ui.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestSlotData {
    public Item item = null;
    public int stackSize = 1;
    public String displayName = null;
    public String leftClickAction = null;
    public String rightClickAction = null;
    public boolean moveable = false;
    public boolean visible = true;

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        if (item != null) {
            json.append("      \"item\": \"").append(BuiltInRegistries.ITEM.getKey(item)).append("\",\n");
        }
        json.append("      \"stackSize\": ").append(stackSize).append(",\n");
        if (displayName != null) {
            json.append("      \"displayName\": \"").append(displayName).append("\",\n");
        }
        if (leftClickAction != null) {
            json.append("      \"leftClickAction\": \"").append(leftClickAction).append("\",\n");
        }
        if (rightClickAction != null) {
            json.append("      \"rightClickAction\": \"").append(rightClickAction).append("\",\n");
        }
        json.append("      \"moveable\": ").append(moveable).append(",\n");
        json.append("      \"visible\": ").append(visible).append("\n");
        json.append("    }");
        return json.toString();
    }
}