package io.github.luckymcdev.groovyengine.construct.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import io.github.luckymcdev.groovyengine.lens.client.rendering.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class WorldEditSelectionRenderer {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static Boolean worldEditLoaded = null; // Cache the check result

    private static boolean notIsWorldEditLoaded() {
        if (worldEditLoaded == null) {
            worldEditLoaded = ModList.get().isLoaded("worldedit");
        }
        return worldEditLoaded;
    }

    @SubscribeEvent
    private static void render(RenderLevelStageEvent event) {
        if (notIsWorldEditLoaded()) return; // Early return if WorldEdit not loaded
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
        renderSelection(event.getPoseStack(), minecraft.renderBuffers().bufferSource(), event.getCamera().getPosition());
    }

    public static void renderSelection(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos) {
        if (notIsWorldEditLoaded()) return; // Safety check
        if (minecraft.player == null || minecraft.level == null) return;

        try {
            LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(minecraft.player.getName().getString());
            if (session == null) return;

            com.sk89q.worldedit.world.World weWorld = session.getSelectionWorld();
            if (weWorld == null) return;

            Region selection = session.getSelection(weWorld);
            if (selection == null) return;

            AABB renderedBox = getAabb(cameraPos, selection);

            RenderUtils.setupWorldRendering(poseStack);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

            LevelRenderer.renderLineBox(
                    poseStack,
                    vertexConsumer,
                    renderedBox,
                    1.0f, 0.0f, 0.0f, 0.5f
            );

            renderPositionCubes(poseStack, vertexConsumer, cameraPos, selection);

        } catch (Exception ignored) {
            // Silently handle any WorldEdit-related exceptions
        }
    }

    private static void renderPositionCubes(PoseStack poseStack, VertexConsumer vertexConsumer, Vec3 cameraPos, Region selection) {
        BlockVector3 pos1 = selection.getMinimumPoint();
        BlockVector3 pos2 = selection.getMaximumPoint();

        double cubeSize = 0.6;
        double offset = (1.0 - cubeSize) / 2.0;

        AABB pos1Cube = new AABB(
                pos1.x() + offset, pos1.y() + offset, pos1.z() + offset,
                pos1.x() + offset + cubeSize, pos1.y() + offset + cubeSize, pos1.z() + offset + cubeSize
        );

        AABB pos2Cube = new AABB(
                pos2.x() + offset, pos2.y() + offset, pos2.z() + offset,
                pos2.x() + offset + cubeSize, pos2.y() + offset + cubeSize, pos2.z() + offset + cubeSize
        );

        LevelRenderer.renderLineBox(
                poseStack,
                vertexConsumer,
                pos1Cube,
                0.0f, 1.0f, 0.0f, 0.5f
        );

        LevelRenderer.renderLineBox(
                poseStack,
                vertexConsumer,
                pos2Cube,
                0.0f, 0.0f, 1.0f, 0.5f
        );
    }

    private static @NotNull AABB getAabb(Vec3 cameraPos, Region selection) {
        BlockVector3 min = selection.getMinimumPoint();
        BlockVector3 max = selection.getMaximumPoint();

        return new AABB(
                min.x(), min.y(), min.z(),
                max.x() + 1, max.y() + 1, max.z() + 1
        );
    }
}