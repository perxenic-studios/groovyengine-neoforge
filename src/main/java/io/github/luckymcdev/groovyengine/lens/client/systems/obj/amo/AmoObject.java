package io.github.luckymcdev.groovyengine.lens.client.systems.obj.amo;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.luckymcdev.groovyengine.GE;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.Face;
import io.github.luckymcdev.groovyengine.lens.client.systems.obj.ObjObject;
import net.minecraft.client.renderer.RenderType;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

/**
 * Animated object that can use joint transformations for skeletal animation.
 */
public class AmoObject extends ObjObject {
    private final List<AmoFace> amoFaces;

    public AmoObject(String name) {
        super(name);
        this.amoFaces = new ArrayList<>();
    }

    /**
     * Adds a face to this object.
     * If the face is an instance of AmoFace, it will be added to the list of animated faces.
     * @param face The face to add.
     */
    @Override
    public void addFace(Face face) {
        super.addFace(face);
        if (face instanceof AmoFace amoFace) {
            amoFaces.add(amoFace);
        }
    }

    /**
     * Render with animation applied using joint transformations.
     */
    public void renderAnimated(PoseStack poseStack, RenderType renderType, int packedLight, Joint[] joints) {

        poseStack.pushPose();

        // Apply object transformations
        poseStack.translate(getPosition().x, getPosition().y, getPosition().z);
        poseStack.mulPose(new Quaternionf().rotateXYZ(
                getRotation().x, getRotation().y, getRotation().z
        ));
        poseStack.scale(getScale().x, getScale().y, getScale().z);

        // Render each face with joint animation
        int facesRendered = 0;
        for (AmoFace face : amoFaces) {
            try {
                face.renderAnimated(poseStack, renderType, packedLight, joints);
                facesRendered++;
            } catch (Exception e) {
                GE.LENS_LOG.error("Error rendering AmoFace: {}", e.getMessage(), e);
            }
        }

        poseStack.popPose();
    }

    /**
     * Get the list of animated faces for this object.
     * These faces use joint transformations for skeletal animation.
     * @return The list of animated faces.
     */
    public List<AmoFace> getAmoFaces() {
        return amoFaces;
    }
}