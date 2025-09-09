package dev.lucky.groovyengine.core.systems.packs.loader;

import dev.lucky.groovyengine.GE;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Consumer;

public class GroovyEngineRepositorySource implements RepositorySource {

    private static final PackSource SOURCE = PackSource.create(packText -> packText, true);
    private final PackType type;
    private final File packLocation;

    public GroovyEngineRepositorySource(PackType type) {
        this.type = type;

        // Change to .minecraft/groovyengine/src/resources
        this.packLocation = new File(FMLPaths.GAMEDIR.get().toFile(),
                GE.MODID + "/src/resources");

        GE.LOG.info("GroovyEngine pack location for {}: {}", type.name(), packLocation.getAbsolutePath());
    }

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        GE.LOG.info("Scan started for {} in directory: {}", type.name(), packLocation.getAbsolutePath());
        final long startTime = System.nanoTime();

        int validPackCount = loadFromLocation(consumer);

        final long endTime = System.nanoTime();
        GE.LOG.info("Located {} packs. Took {}ms.", validPackCount, GE.DECIMAL_2.format((endTime - startTime) / 1000000d));
    }

    private int loadFromLocation(@NotNull Consumer<Pack> consumer) {
        if (!packLocation.exists()) {
            GE.LOG.warn("Resources directory does not exist: {}", packLocation.getAbsolutePath());
            return 0;
        }

        // Create pack.mcmeta if it doesn't exist
        File mcmetaFile = new File(packLocation, "pack.mcmeta");
        if (!mcmetaFile.exists()) {
            try {
                String mcmetaContent = "{\n" +
                        "  \"pack\": {\n" +
                        "    \"pack_format\": 48,\n" +
                        "    \"description\": \"GroovyEngine Resources\"\n" +
                        "  }\n" +
                        "}";
                Files.write(mcmetaFile.toPath(), mcmetaContent.getBytes());
                GE.LOG.info("Created pack.mcmeta file at: {}", mcmetaFile.getAbsolutePath());
            } catch (IOException e) {
                GE.LOG.error("Failed to create pack.mcmeta file", e);
                return 0;
            }
        }

        PackContentType contentType = PackContentType.fromResourcesDirectory(packLocation.toPath());

        if (contentType.isFor(this.type)) {
            final PackLocationInfo locationInfo = new PackLocationInfo(
                    GE.MODID + "/" + type.name().toLowerCase(),
                    Component.literal("GroovyEngine " + type.name()),
                    SOURCE,
                    Optional.empty()
            );
            final PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);

            Pack pack = Pack.readMetaAndCreate(
                    locationInfo,
                    PackFileType.FOLDER.createPackSupplier(packLocation),
                    this.type,
                    selectionConfig
            );

            if (pack != null) {
                consumer.accept(pack);
                GE.LOG.info("Successfully loaded GroovyEngine pack for {}", type.name());
                return 1;
            } else {
                GE.LOG.warn("Failed to create pack for {}", type.name());
            }
        } else {
            GE.LOG.info("No content found for pack type {} in {}", type.name(), packLocation.getAbsolutePath());
        }

        return 0;
    }
}