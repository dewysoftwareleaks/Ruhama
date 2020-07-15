package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HopperRadius extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingSlider(0.0D, 255.0D, 128.0D, 0, "Red: "), new SettingSlider(0.0D, 255.0D, 128.0D, 0, "Green: "), new SettingSlider(0.0D, 255.0D, 128.0D, 0, "Blue: "), new SettingToggle(true, "Fill"), new SettingToggle(true, "Outline"));

    public HopperRadius()
    {
        super("HopperRadius", 0, Category.RENDER, "Shows the line around hoppers where they close", settings);
    }

    public void onRender()
    {
        RenderUtils.glSetup();
        double red = this.getSettings().get(0).toSlider().getValue() / 255.0D;
        double green = this.getSettings().get(1).toSlider().getValue() / 255.0D;
        double blue = this.getSettings().get(2).toSlider().getValue() / 255.0D;
        Iterator var7 = this.mc.world.loadedTileEntityList.iterator();

        while (true)
        {
            Vec3d pos;
            int i;
            do
            {
                TileEntity t;
                do
                {
                    if (!var7.hasNext())
                    {
                        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
                        RenderUtils.glCleanup();
                        return;
                    }

                    t = (TileEntity) var7.next();
                } while (!(t instanceof TileEntityHopper));

                pos = new Vec3d((double) t.getPos().getX() + 0.5D - RenderUtils.rPos()[0], (double) t.getPos().getY() - RenderUtils.rPos()[1], (double) t.getPos().getZ() + 0.5D - RenderUtils.rPos()[2]);
                if (this.getSettings().get(3).toToggle().state)
                {
                    GL11.glBegin(9);
                    GL11.glColor4d(red, green, blue, 0.25D);

                    for (i = 0; i <= 360; ++i)
                    {
                        GL11.glVertex3d(pos.x + Math.sin((double) i * 3.141592653589793D / 180.0D) * 7.35D, pos.y, pos.z + Math.cos((double) i * 3.141592653589793D / 180.0D) * 7.35D);
                    }

                    GL11.glEnd();
                }
            } while (!this.getSettings().get(4).toToggle().state);

            GL11.glBegin(1);
            GL11.glColor4d(red, green, blue, 0.7D);

            for (i = 0; i <= 360; ++i)
            {
                GL11.glVertex3d(pos.x + Math.sin((double) i * 3.141592653589793D / 180.0D) * 7.35D, pos.y, pos.z + Math.cos((double) i * 3.141592653589793D / 180.0D) * 7.35D);
            }

            GL11.glEnd();
        }
    }
}
