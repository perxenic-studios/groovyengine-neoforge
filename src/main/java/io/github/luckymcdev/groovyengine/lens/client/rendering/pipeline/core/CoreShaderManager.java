package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber
public class CoreShaderManager {
    public static TestShader TESTSHADER = new TestShader();

    @SubscribeEvent
    private static void registerShaders(RegisterShadersEvent event) throws IOException {
        TESTSHADER.register(event);
    }
}
