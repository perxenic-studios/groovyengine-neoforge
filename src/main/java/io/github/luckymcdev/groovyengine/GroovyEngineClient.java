package io.github.luckymcdev.groovyengine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.luckymcdev.groovyengine.lens.client.rendering.GERenderTypes;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.vertex.ExtendedPoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.core.config.DefaultAdvertiser;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.List;

@Mod(value = GE.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GE.MODID, value = Dist.CLIENT)
public class GroovyEngineClient {

    public GroovyEngineClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        PostProcessManager.addInstances(
                List.of(
                        SuperDuperPostShader.INSTANCE
                )
        );

        PostProcessManager.addStageInstance(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS, CrtPostShader.INSTANCE);

        CrtPostShader.INSTANCE.setActive(false);
        SuperDuperPostShader.INSTANCE.setActive(false);

    }

    @SubscribeEvent
    static void onRegisterShader(RegisterShadersEvent event) {
        try {
            TestShader.INSTANCE.register(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        poseStack.pushPose();
        setUpPoseForWorld(poseStack);
        poseStack.translate(0, 100, 0);

        ShaderInstance shader = TestShader.INSTANCE.getShader();
        if (shader != null) {
            shader.safeGetUniform("ModelViewMat").set(poseStack.last().pose());
        }

        renderGraphicsWithCustomShader(poseStack, bufferSource);

        poseStack.popPose();
    }

    static void renderGraphicsWithCustomShader(PoseStack poseStack, MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(GERenderTypes.CUSTOM_QUADS);
        Matrix4f matrix = poseStack.last().pose();

        float size = 100f;
        float minX = -size;
        float minY = -size;
        float minZ = -size;
        float maxX = size;
        float maxY = size;
        float maxZ = size;

        consumer.addVertex(matrix, minX, minY, maxZ).setUv(0.0f, 0.0f);
        consumer.addVertex(matrix, maxX, minY, maxZ).setUv(1.0f, 0.0f);
        consumer.addVertex(matrix, maxX, maxY, maxZ).setUv(1.0f, 1.0f);
        consumer.addVertex(matrix, minX, maxY, maxZ).setUv(0.0f, 1.0f);

        consumer.addVertex(matrix, maxX, minY, minZ).setUv(0.0f, 0.0f);
        consumer.addVertex(matrix, minX, minY, minZ).setUv(1.0f, 0.0f);
        consumer.addVertex(matrix, minX, maxY, minZ).setUv(1.0f, 1.0f);
        consumer.addVertex(matrix, maxX, maxY, minZ).setUv(0.0f, 1.0f);

        consumer.addVertex(matrix, minX, maxY, maxZ).setUv(0.0f, 0.0f);
        consumer.addVertex(matrix, maxX, maxY, maxZ).setUv(1.0f, 0.0f);
        consumer.addVertex(matrix, maxX, maxY, minZ).setUv(1.0f, 1.0f);
        consumer.addVertex(matrix, minX, maxY, minZ).setUv(0.0f, 1.0f);

        consumer.addVertex(matrix, minX, minY, minZ).setUv(0.0f, 0.0f);
        consumer.addVertex(matrix, maxX, minY, minZ).setUv(1.0f, 0.0f);
        consumer.addVertex(matrix, maxX, minY, maxZ).setUv(1.0f, 1.0f);
        consumer.addVertex(matrix, minX, minY, maxZ).setUv(0.0f, 1.0f);

        consumer.addVertex(matrix, maxX, minY, maxZ).setUv(0.0f, 0.0f);
        consumer.addVertex(matrix, maxX, minY, minZ).setUv(1.0f, 0.0f);
        consumer.addVertex(matrix, maxX, maxY, minZ).setUv(1.0f, 1.0f);
        consumer.addVertex(matrix, maxX, maxY, maxZ).setUv(0.0f, 1.0f);

        consumer.addVertex(matrix, minX, minY, minZ).setUv(0.0f, 0.0f);
        consumer.addVertex(matrix, minX, minY, maxZ).setUv(1.0f, 0.0f);
        consumer.addVertex(matrix, minX, maxY, maxZ).setUv(1.0f, 1.0f);
        consumer.addVertex(matrix, minX, maxY, minZ).setUv(0.0f, 1.0f);
    }

    public static void setUpPoseForWorld(PoseStack stack) {
        Vec3 pos =Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.translate(-pos.x, -pos.y, -pos.z);
    }

    static void renderGraphicsTriangleThing(PoseStack poseStack, MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());
        Matrix4f matrix = poseStack.last().pose();

        consumer.addVertex(matrix, 0.0f, 1.0f, 0.0f)
                .setColor(1.0f, 0.0f, 0.0f, 1.0f);

        consumer.addVertex(matrix, -1.0f, -1.0f, 0.0f)
                .setColor(0.0f, 1.0f, 0.0f, 1.0f);

        consumer.addVertex(matrix, 1.0f, -1.0f, 0.0f)
                .setColor(0.0f, 0.0f, 1.0f, 1.0f);

        consumer.addVertex(matrix, 0.0f, 1.0f, 0.0f)
                .setColor(1.0f, 0.0f, 0.0f, 1.0f);
    }
}
