package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.core.systems.packs.loader.GroovyEngineRepositorySource;
import io.github.luckymcdev.groovyengine.util.CachedSupplier;
import io.github.luckymcdev.groovyengine.util.DecoratedLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
