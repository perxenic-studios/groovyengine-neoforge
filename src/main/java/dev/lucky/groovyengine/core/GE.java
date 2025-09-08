package dev.lucky.groovyengine.core;

import dev.lucky.groovyengine.core.internal.packs.loader.GroovyEngineRepositorySource;
import dev.lucky.groovyengine.core.util.CachedSupplier;
import dev.lucky.groovyengine.core.util.DecoratedLogger;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.text.DecimalFormat;

public class GE {
    public static final String MODID = "groovyengine";
    public static final String NAME = "GroovyEngine";

    public static final DecoratedLogger LOG = new DecoratedLogger(GE.class);

    public static final DecimalFormat DECIMAL_2 = new DecimalFormat("#.##");

    public static final CachedSupplier<RepositorySource> DATA_SOURCE = CachedSupplier.cache(() -> new GroovyEngineRepositorySource(PackType.SERVER_DATA));
    public static final CachedSupplier<RepositorySource> RESOURCE_SOURCE = CachedSupplier.cache(() -> new GroovyEngineRepositorySource(PackType.CLIENT_RESOURCES));
}
