package io.github.luckymcdev.groovyengine;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.core.test.TestShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.PostProcessManager;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.CrtPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.post.test.SuperDuperPostShader;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.GeometryBuilder;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
        PostProcessManager.addInstances(List.of(SuperDuperPostShader.INSTANCE));
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
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.getY() < 200) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        poseStack.pushPose();
        RenderUtils.setupWorldRendering(poseStack);

        // Render the huge quad
        poseStack.translate(0, -65, 0);

        renderHugeQuad(poseStack, bufferSource, 2000f);

        // Render other geometry
        poseStack.translate(0, 165, 0);

        renderGraphicsWithCustomShader(poseStack, bufferSource);

        renderGraphicsTriangleThing(poseStack, bufferSource);

        RenderUtils.setPoseStackPosition(poseStack, new Vec3(0,75,0));

        poseStack.popPose();
    }

    static void renderGraphicsWithCustomShader(PoseStack poseStack, MultiBufferSource bufferSource) {
        GeometryBuilder.renderCube(
                RenderUtils.getConsumer(bufferSource),
                poseStack.last().pose(),
                0, 0, 0, // Position
                40f, // Size
                0f, 0f, 1f, 1f // UV coordinates
        );
    }

    static void renderHugeQuad(PoseStack poseStack, MultiBufferSource bufferSource, float size) {
        GeometryBuilder.renderPlane(
                RenderUtils.getConsumer(bufferSource),
                poseStack.last().pose(),
                0, 0, 0, // Position
                size, size, // Width and height
                new Vector3f(0, 1, 0), // Normal (facing up)
                0f, 0f, 1f, 1f // UV coordinates
        );
    }

    static void renderGraphicsTriangleThing(PoseStack poseStack, MultiBufferSource bufferSource) {
        GeometryBuilder.renderTriangle(
                RenderUtils.getConsumer(bufferSource),
                poseStack.last().pose(),
                0.0f, 1.0f, 0.0f, // Vertex 1
                -1.0f, -1.0f, 0.0f, // Vertex 2
                1.0f, -1.0f, 0.0f, // Vertex 3
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, // UV coordinates
                new Vector3f(0, 0, 1) // Normal
        );
    }
}