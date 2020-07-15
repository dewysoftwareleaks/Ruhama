package bleach.a32k.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class RenderUtils
{
    public static double[] rPos()
    {
        try
        {
            return new double[] {(Double) Objects.requireNonNull(ReflectUtils.getField(RenderManager.class, "renderPosX", "renderPosX")).get(Minecraft.getMinecraft().getRenderManager()), (Double) Objects.requireNonNull(ReflectUtils.getField(RenderManager.class, "renderPosY", "renderPosY")).get(Minecraft.getMinecraft().getRenderManager()), (Double) Objects.requireNonNull(ReflectUtils.getField(RenderManager.class, "renderPosZ", "renderPosZ")).get(Minecraft.getMinecraft().getRenderManager())};
        } catch (Exception e)
        {
            return new double[] {0.0D, 0.0D, 0.0D};
        }
    }

    public static void drawFilledBlockBox(AxisAlignedBB box, float r, float g, float b, float a)
    {
        try
        {
            glSetup();

            double[] rPos = rPos();

            box = new AxisAlignedBB(box.minX - rPos[0], box.minY - rPos[1], box.minZ - rPos[2], box.maxX - rPos[0], box.maxY - rPos[1], box.maxZ - rPos[2]);

            RenderGlobal.renderFilledBox(box, r, g, b, a);
            RenderGlobal.drawSelectionBoundingBox(box, r, g, b, a * 1.5F);

            glCleanup();
        } catch (Exception ignored)
        {
        }
    }

    public static void glSetup()
    {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0F);
    }

    public static void glCleanup()
    {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
