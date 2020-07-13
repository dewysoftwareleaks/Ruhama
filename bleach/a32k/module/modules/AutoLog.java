package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AutoLog extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(false, "Health"), new SettingSlider(0.0D, 20.0D, 5.0D, 0, "Health: "), new SettingToggle(true, "Totems"), new SettingSlider(0.0D, 6.0D, 0.0D, 0, "Totems: "), new SettingMode("Crystal: ", new String[] {"None", "Near", "Near+No Totem", "Near+Health"}), new SettingSlider(0.0D, 8.0D, 4.0D, 2, "CrystalRange: "), new SettingToggle(false, "Nearby Player"), new SettingSlider(0.0D, 100.0D, 20.0D, 1, "Range: "));

    public AutoLog()
    {
        super("AutoLog", 0, Category.COMBAT, "Automatically Logs out when ___", settings);
    }

    public void onUpdate()
    {
        if (!this.mc.player.capabilities.isCreativeMode && !this.mc.isIntegratedServerRunning())
        {
            if (this.getSettings().get(0).toToggle().state && (double) this.mc.player.getHealth() < this.getSettings().get(1).toSlider().getValue())
            {
                this.logOut("Logged Out At " + this.mc.player.getHealth() + " Health");
            } else
            {
                if (this.getSettings().get(2).toToggle().state)
                {
                    int t = this.getTotems();
                    if (t <= (int) this.getSettings().get(3).toSlider().getValue())
                    {
                        this.logOut("Logged Out With " + t + " Totems Left");
                        return;
                    }
                }

                Iterator var5;
                if (this.getSettings().get(4).toMode().mode != 0)
                {
                    var5 = this.mc.world.loadedEntityList.iterator();

                    while (var5.hasNext())
                    {
                        Entity e = (Entity) var5.next();
                        if (e instanceof EntityEnderCrystal)
                        {
                            double d = (double) this.mc.player.getDistance(e);
                            if (d <= this.getSettings().get(5).toSlider().getValue() && (this.getSettings().get(4).toMode().mode == 1 || this.getSettings().get(4).toMode().mode == 2 && this.getTotems() <= (int) this.getSettings().get(3).toSlider().getValue() || this.getSettings().get(4).toMode().mode == 3 && (double) this.mc.player.getHealth() < this.getSettings().get(1).toSlider().getValue()))
                            {
                                this.logOut("Logged Out " + d + " Blocks Away From A Crystal");
                                return;
                            }
                        }
                    }
                }

                if (this.getSettings().get(6).toToggle().state)
                {
                    var5 = this.mc.world.playerEntities.iterator();

                    while (var5.hasNext())
                    {
                        EntityPlayer e = (EntityPlayer) var5.next();
                        if (e.getName() != this.mc.player.getName() && (double) this.mc.player.getDistance(e) <= this.getSettings().get(7).toSlider().getValue())
                        {
                            this.logOut("Logged Out " + this.mc.player.getDistance(e) + " Blocks Away From A Player (" + e.getName() + ")");
                        }
                    }
                }

            }
        }
    }

    private int getTotems()
    {
        int c = 0;

        for (int i = 0; i < 45; ++i)
        {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING)
            {
                ++c;
            }
        }

        return c;
    }

    private void logOut(String reason)
    {
        this.mc.player.connection.getNetworkManager().closeChannel(new TextComponentString(reason));
        this.setToggled(false);
    }
}
