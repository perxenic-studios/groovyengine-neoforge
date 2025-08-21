package dev.lucky.groovyengine.internal.packs;

import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public record PackContentType(boolean data, boolean resources) {

    private static final PackContentType INVALID = new PackContentType(false, false);

    public boolean isFor(PackType type) {
        return (type == PackType.SERVER_DATA && this.data) || (type == PackType.CLIENT_RESOURCES && this.resources);
    }

    public static PackContentType from(Path filePath) {
        // Archive
        if (Files.isRegularFile(filePath)) {
            try (FileSystem fs = FileSystems.newFileSystem(filePath)) {
                if (Files.isRegularFile(fs.getPath("pack.mcmeta"))) {
                    return new PackContentType(Files.isDirectory(fs.getPath("data/")), Files.isDirectory(fs.getPath("assets/")));
                }
            }
            catch (IOException e) {
                // no-op
            }
        }
        // Folder
        else if (Files.isDirectory(filePath)) {
            if (Files.isRegularFile(filePath.resolve("pack.mcmeta"))) {
                return new PackContentType(Files.isDirectory(filePath.resolve("data")), Files.isDirectory(filePath.resolve("assets")));
            }
        }
        return INVALID;
    }

    public static PackContentType fromResourcesDirectory(Path resourcesPath) {
        if (Files.isDirectory(resourcesPath)) {
            boolean hasData = Files.isDirectory(resourcesPath.resolve("data"));
            boolean hasAssets = Files.isDirectory(resourcesPath.resolve("assets"));

            // Create a virtual pack.mcmeta if either data or assets exist
            if (hasData || hasAssets) {
                return new PackContentType(hasData, hasAssets);
            }
        }
        return INVALID;
    }
}