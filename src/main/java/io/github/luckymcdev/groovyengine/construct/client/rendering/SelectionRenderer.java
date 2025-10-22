package io.github.luckymcdev.groovyengine.construct.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.luckymcdev.groovyengine.construct.core.selection.Selection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@OnlyIn(Dist.CLIENT)
public class SelectionRenderer {

    private static final Minecraft mc = Minecraft.getInstance();
    // Colors for the selection box (RGBA, 0-1 range)
    private static final float POS1_RED = 1.0f;
    private static final float POS1_GREEN = 0.3f;
    private static final float POS1_BLUE = 0.3f;
    private static final float POS1_ALPHA = 0.4f;
    private static final float POS2_RED = 0.3f;
    private static final float POS2_GREEN = 0.3f;
    private static final float POS2_BLUE = 1.0f;
    private static final float POS2_ALPHA = 0.4f;
    private static final float SELECTION_RED = 0.3f;
    private static final float SELECTION_GREEN = 1.0f;
    private static final float SELECTION_BLUE = 0.3f;
    private static final float SELECTION_ALPHA = 0.4f;
    private Selection selection;

    public SelectionRenderer(Selection selection) {
        this.selection = selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        if (selection == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 camPos = event.getCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());

        // Render individual position markers
        BlockPos pos1 = selection.getPos1();
        BlockPos pos2 = selection.getPos2();

        if (pos1 != null) {
            renderBlockOutline(poseStack, builder, pos1,
                    POS1_RED, POS1_GREEN, POS1_BLUE, POS1_ALPHA);
        }

        if (pos2 != null) {
            renderBlockOutline(poseStack, builder, pos2,
                    POS2_RED, POS2_GREEN, POS2_BLUE, POS2_ALPHA);
        }

        // Render full selection if both positions are set
        if (selection.hasValidSelection()) {
            BlockPos min = selection.getPos1();
            BlockPos max = selection.getPos2();

            // Ensure min/max are correctly ordered
            int minX = Math.min(min.getX(), max.getX());
            int minY = Math.min(min.getY(), max.getY());
            int minZ = Math.min(min.getZ(), max.getZ());
            int maxX = Math.max(min.getX(), max.getX());
            int maxY = Math.max(min.getY(), max.getY());
            int maxZ = Math.max(min.getZ(), max.getZ());

            AABB box = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);

            LevelRenderer.renderLineBox(poseStack, builder, box,
                    SELECTION_RED, SELECTION_GREEN, SELECTION_BLUE, SELECTION_ALPHA);
        }

        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }

    private void renderBlockOutline(PoseStack poseStack, VertexConsumer builder,
                                    BlockPos pos, float r, float g, float b, float a) {
        AABB box = new AABB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        LevelRenderer.renderLineBox(poseStack, builder, box, r, g, b, a);
    }
}