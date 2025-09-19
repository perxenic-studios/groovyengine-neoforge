package io.github.luckymcdev.groovyengine.core.client.imgui.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureHelper {
    public static int loadMinecraftTexture(ResourceLocation location) {
        try (var input = Minecraft.getInstance().getResourceManager().open(location)) {
            NativeImage img = NativeImage.read(input);

            DynamicTexture dynTex = new DynamicTexture(img);
            int id = dynTex.getId();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}


