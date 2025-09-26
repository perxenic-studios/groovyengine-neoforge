package io.github.luckymcdev.groovyengine.construct.core.commands;

public class CommandInfo {
    private final String name;
    private final String description;
    private final String usage;
    private final String[] aliases;
    private final String category;

    public CommandInfo(String name, String description, String usage, String category, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.aliases = aliases;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public String getCategory() { return category; }
    public String[] getAliases() { return aliases; }

    // Utility methods
    public boolean matches(String commandName) {
        if (name.equalsIgnoreCase(commandName)) return true;
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(commandName)) return true;
        }
        return false;
    }

    public String getFullUsage() {
        return "/" + name + " " + usage;
    }

    @Override
    public String toString() {
        return String.format("§6%s§r - §7%s§r\nUsage: §e%s§r\nAliases: §9%s§r",
                name, description, getFullUsage(),
                aliases.length > 0 ? String.join(", ", aliases) : "None");
    }

    public String toShortString() {
        return String.format("§6%s§r - §7%s§r", name, description);
    }
}