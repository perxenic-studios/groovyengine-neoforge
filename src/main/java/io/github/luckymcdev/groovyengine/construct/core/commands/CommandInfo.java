package io.github.luckymcdev.groovyengine.construct.core.commands;

public class CommandInfo {
    private final String name;
    private final String description;
    private final String usage;
    private final String[] aliases;

    public CommandInfo(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public String[] getAliases() { return aliases; }

    public String name() { return name; }
    public String description() { return description; }
    public String usage() { return usage; }
    public String[] aliases() { return aliases; }

    @Override
    public String toString() {
        return String.format("%s - %s\nUsage: %s\nAliases: %s",
                name, description, usage,
                aliases.length > 0 ? String.join(", ", aliases) : "None");
    }
}