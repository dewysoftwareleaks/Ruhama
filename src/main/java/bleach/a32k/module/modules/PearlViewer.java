package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.RenderUtils;
import bleach.a32k.utils.RuhamaLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.Map.Entry;

public class PearlViewer extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(true, "Chat"), new SettingToggle(true, "Render"), new SettingSlider(0.0D, 20.0D, 5.0D, 1, "Render Time: "), new SettingSlider(0.0D, 10.0D, 3.5D, 2, "Thick: "));
    private final HashMap<UUID, List<Vec3d>> poses = new HashMap<>();
    private final HashMap<UUID, Double> time = new HashMap<>();

    public PearlViewer()
    {
        super("PearlViewer", 0, Category.RENDER, "Shows Where Enderpearls Are Going", settings);
    }

    public void onUpdate()
    {
        Iterator iter = (new HashMap(this.time)).entrySet().iterator();

        while (iter.hasNext())
        {
            Entry<UUID, Double> e = (Entry) iter.next();

            if (e.getValue() <= 0.0D)
            {
                this.poses.remove(e.getKey());
                this.time.remove(e.getKey());
            } else
            {
                this.time.replace(e.getKey(), e.getValue() - 0.05D);
            }
        }

        iter = this.mc.world.loadedEntityList.iterator();

        while (true)
        {
            while (true)
            {
                Entity e;
                do
                {
                    if (!iter.hasNext())
                    {
                        return;
                    }

                    e = (Entity) iter.next();
                } while (!(e instanceof EntityEnderPearl));

                if (!this.poses.containsKey(e.getUniqueID()))
                {
                    if (this.getSettings().get(0).toToggle().state)
                    {
                        for (net.minecraft.entity.player.EntityPlayer entityPlayer : this.mc.world.playerEntities)
                        {
                            if (entityPlayer.getDistance(e) < 4.0F && ((Entity) entityPlayer).getName() != this.mc.player.getName())
                            {
                                RuhamaLogger.log(((Entity) entityPlayer).getName() + " Threw a pearl");

                                break;
                            }
                        }
                    }

                    this.poses.put(e.getUniqueID(), new ArrayList<>(Collections.singletonList(e.getPositionVector())));
                    this.time.put(e.getUniqueID(), this.getSettings().get(2).toSlider().getValue());
                } else
                {
                    this.time.replace(e.getUniqueID(), this.getSettings().get(2).toSlider().getValue());
                    List<Vec3d> v = this.poses.get(e.getUniqueID());
                    v.add(e.getPositionVector());
                }
            }
        }
    }

    public void onRender()
    {
        if (this.getSettings().get(1).toToggle().state)
        {
            RenderUtils.glSetup();
            GL11.glLineWidth((float) this.getSettings().get(3).toSlider().getValue());
            Iterator posIter = this.poses.entrySet().iterator();

            while (true)
            {
                Entry e;
                do
                {
                    if (!posIter.hasNext())
                    {
                        RenderUtils.glCleanup();
                        return;
                    }

                    e = (Entry) posIter.next();
                } while (((List) e.getValue()).size() <= 2);

                GL11.glBegin(1);
                Random rand = new Random(e.getKey().hashCode());
                double r = 0.5D + rand.nextDouble() / 2.0D;
                double g = 0.5D + rand.nextDouble() / 2.0D;
                double b = 0.5D + rand.nextDouble() / 2.0D;
                GL11.glColor3d(r, g, b);
                double[] rPos = RenderUtils.rPos();

                for (int i = 1; i < ((List) e.getValue()).size(); ++i)
                {
                    GL11.glVertex3d(((Vec3d) ((List) e.getValue()).get(i)).x - rPos[0], ((Vec3d) ((List) e.getValue()).get(i)).y - rPos[1], ((Vec3d) ((List) e.getValue()).get(i)).z - rPos[2]);
                    GL11.glVertex3d(((Vec3d) ((List) e.getValue()).get(i - 1)).x - rPos[0], ((Vec3d) ((List) e.getValue()).get(i - 1)).y - rPos[1], ((Vec3d) ((List) e.getValue()).get(i - 1)).z - rPos[2]);
                }

                GL11.glEnd();
            }
        }
    }
}
