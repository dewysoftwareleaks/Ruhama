package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.ReflectUtils;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HoleFinderESP extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingSlider(5.0D, 25.0D, 10.0D, 0, "Range: "), new SettingMode("Draw: ", new String[] {"Full", "Flat"}), new SettingToggle(true, "Rainbow"), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Obby-R: "), new SettingSlider(0.0D, 255.0D, 255.0D, 0, "Obby-G: "), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Obby-B: "), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Bedrk-R: "), new SettingSlider(0.0D, 255.0D, 100.0D, 0, "Bedrk-G: "), new SettingSlider(0.0D, 255.0D, 255.0D, 0, "Bedrk-B: "));
    public Vec3d prevPos;
    private final List<BlockPos> poses = new ArrayList();
    private double[] rPos;

    public HoleFinderESP()
    {
        super("HoleFinderESP", 0, Category.RENDER, "Finds Holes In Bedrock/Obsidian", settings);
        this.prevPos = Vec3d.ZERO;
    }

    public void onUpdate()
    {
        if (this.mc.player.ticksExisted % 100 == 0 || this.mc.player.getPositionVector().distanceTo(this.prevPos) > 5.0D && this.mc.player.ticksExisted % 10 == 0)
        {
            this.update((int) this.getSettings().get(0).toSlider().getValue());
        }

    }

    public void update(int range)
    {
        this.poses.clear();
        BlockPos player = this.mc.player.getPosition();
        this.prevPos = this.mc.player.getPositionVector();

        for (int y = -Math.min(range, player.getY()); y < Math.min(range, 255 - player.getY()); ++y)
        {
            for (int x = -range; x < range; ++x)
            {
                for (int z = -range; z < range; ++z)
                {
                    BlockPos pos = player.add(x, y, z);
                    if ((this.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) && this.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && (this.mc.world.getBlockState(pos.up().east()).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(pos.up().east()).getBlock() == Blocks.OBSIDIAN) && (this.mc.world.getBlockState(pos.up().west()).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(pos.up().west()).getBlock() == Blocks.OBSIDIAN) && (this.mc.world.getBlockState(pos.up().north()).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(pos.up().north()).getBlock() == Blocks.OBSIDIAN) && (this.mc.world.getBlockState(pos.up().south()).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(pos.up().south()).getBlock() == Blocks.OBSIDIAN) && this.mc.world.getBlockState(pos.up(2)).getBlock() == Blocks.AIR && this.mc.world.getBlockState(pos.up(3)).getBlock() == Blocks.AIR)
                    {
                        this.poses.add(pos.up());
                    }
                }
            }
        }

    }

    public void onRender()
    {
        try
        {
            this.rPos = new double[] {(Double) ReflectUtils.getField(RenderManager.class, "renderPosX", "renderPosX").get(this.mc.getRenderManager()), (Double) ReflectUtils.getField(RenderManager.class, "renderPosY", "renderPosY").get(this.mc.getRenderManager()), (Double) ReflectUtils.getField(RenderManager.class, "renderPosZ", "renderPosZ").get(this.mc.getRenderManager())};
        } catch (Exception var5)
        {
            this.rPos = new double[] {0.0D, 0.0D, 0.0D};
        }

        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0F);
        float blue = (float) (System.currentTimeMillis() / 10L % 512L) / 255.0F;
        float red = (float) (System.currentTimeMillis() / 16L % 512L) / 255.0F;
        if (blue > 1.0F)
        {
            blue = 1.0F - blue;
        }

        if (red > 1.0F)
        {
            red = 1.0F - red;
        }

        Iterator var3 = this.poses.iterator();

        while (var3.hasNext())
        {
            BlockPos p = (BlockPos) var3.next();
            this.drawFilledBlockBox(p, red, 0.7F, blue, 0.25F);
        }

        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void drawFilledBlockBox(BlockPos blockPos, float r, float g, float b, float a)
    {
        try
        {
            double x = (double) blockPos.getX() - this.rPos[0];
            double y = (double) blockPos.getY() - this.rPos[1];
            double z = (double) blockPos.getZ() - this.rPos[2];
            float or = (float) (this.getSettings().get(3).toSlider().getValue() / 255.0D);
            float og = (float) (this.getSettings().get(4).toSlider().getValue() / 255.0D);
            float ob = (float) (this.getSettings().get(5).toSlider().getValue() / 255.0D);
            float br = (float) (this.getSettings().get(6).toSlider().getValue() / 255.0D);
            float bg = (float) (this.getSettings().get(7).toSlider().getValue() / 255.0D);
            float bb = (float) (this.getSettings().get(8).toSlider().getValue() / 255.0D);
            if (this.getSettings().get(2).toToggle().state)
            {
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0D, y, z + 1.0D), r, g, b, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y, z + 1.0D), r, g, b, a * 1.5F);
            } else if (this.mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.OBSIDIAN)
            {
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0D, y, z + 1.0D), or, og, ob, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y, z + 1.0D), or, og, ob, a * 1.5F);
            } else
            {
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0D, y, z + 1.0D), br, bg, bb, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y, z + 1.0D), br, bg, bb, a * 1.5F);
            }

            if (this.getSettings().get(1).toMode().mode == 1)
            {
                return;
            }

            if (this.getSettings().get(2).toToggle().state)
            {
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z), r, g, b, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z), r, g, b, a * 1.5F);
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x, y + 1.0D, z + 1.0D), r, g, b, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x, y + 1.0D, z + 1.0D), r, g, b, a * 1.5F);
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), r, g, b, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), r, g, b, a * 1.5F);
                RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), r, g, b, a);
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), r, g, b, a * 1.5F);
            } else
            {
                if (this.mc.world.getBlockState(blockPos.north()).getBlock() == Blocks.OBSIDIAN)
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z), or, og, ob, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z), or, og, ob, a * 1.5F);
                } else
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z), br, bg, bb, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z), br, bg, bb, a * 1.5F);
                }

                if (this.mc.world.getBlockState(blockPos.west()).getBlock() == Blocks.OBSIDIAN)
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x, y + 1.0D, z + 1.0D), or, og, ob, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
                } else
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x, y + 1.0D, z + 1.0D), br, bg, bb, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x, y + 1.0D, z + 1.0D), br, bg, bb, a * 1.5F);
                }

                if (this.mc.world.getBlockState(blockPos.east()).getBlock() == Blocks.OBSIDIAN)
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
                } else
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), br, bg, bb, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), br, bg, bb, a * 1.5F);
                }

                if (this.mc.world.getBlockState(blockPos.south()).getBlock() == Blocks.OBSIDIAN)
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
                } else
                {
                    RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), br, bg, bb, a);
                    RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), br, bg, bb, a * 1.5F);
                }
            }
        } catch (Exception var18)
        {
        }

    }
}
