package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.core.client.imgui.icon.ImIcons;
import io.github.luckymcdev.groovyengine.core.systems.packs.loader.GroovyEngineRepositorySource;
import io.github.luckymcdev.groovyengine.util.CachedSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.stream.ImageInputStream;
import java.text.DecimalFormat;

public class GE {
    public static final String MODID = "groovyengine";
    public static final String NAME = "GroovyEngine";

    public static final Logger LOG = LoggerFactory.getLogger("GroovyEngine");
    public static final Logger CONSTRUCT_LOG = LoggerFactory.getLogger("GroovyEngine/Construct");
    public static final Logger CORE_LOG = LoggerFactory.getLogger("GroovyEngine/Core");
    public static final Logger LENS_LOG = LoggerFactory.getLogger("GroovyEngine/Lens");
    public static final Logger SCRIBE_LOG = LoggerFactory.getLogger("GroovyEngine/Scribe");
    public static final Logger THREADS_LOG = LoggerFactory.getLogger("GroovyEngine/Threads");

    public static final Logger SCRIPT_LOG = LoggerFactory.getLogger("GroovyEngine/Script");

    public static final DecimalFormat DECIMAL_2 = new DecimalFormat("#.##");

    public static final CachedSupplier<RepositorySource> DATA_SOURCE = CachedSupplier.cache(() -> new GroovyEngineRepositorySource(PackType.SERVER_DATA));
    public static final CachedSupplier<RepositorySource> RESOURCE_SOURCE = CachedSupplier.cache(() -> new GroovyEngineRepositorySource(PackType.CLIENT_RESOURCES));


    /**
     * Creates a ResourceLocation from the given path, using the {@link GE#MODID} as the namespace.
     * @param path The path to use for the ResourceLocation
     * @return A ResourceLocation with the given path and the {@link GE#MODID} as the namespace
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static class Categories {
        public static final String IMGUI = ImIcons.STACKS.get() + " ImGui";
        public static final String CONSTRUCT = ImIcons.WRENCH.get() + " Construct";
        public static final String LENS = ImIcons.CAMERA.get() + " Lens";
        public static final String THREADS = ImIcons.CODE.get() + " Threads";
        public static final String SCRIBE = ImIcons.EDIT.get() + " Scribe";
        public static final String DEBUG = ImIcons.SETTINGS.get() + " Debug";
    }
}
