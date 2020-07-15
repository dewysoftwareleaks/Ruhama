package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Aura extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(true, "WeaponFilter"), new SettingToggle(true, "1.9 Delay"), new SettingToggle(true, "Thru Walls"), new SettingToggle(true, "Crits"), new SettingSlider(0.0D, 6.0D, 4.5D, 2, "Range: "), new SettingSlider(0.0D, 20.0D, 8.0D, 0, "CPS: "));
    private int delay = 0;

    public Aura()
    {
        super("Aura", 0, Category.COMBAT, "Attacks Players", settings);
    }

    public void onUpdate()
    {
        ++this.delay;

        int reqDelay = (int) Math.round(20.0D / this.getSettings().get(5).toSlider().getValue());

        if (!this.getSettings().get(0).toToggle().state || this.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || this.mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)
        {
            Iterator var2 = this.mc.world.playerEntities.iterator();

            while (true)
            {
                EntityPlayer e;

                // holy shit
                do
                {
                    do
                    {
                        do
                        {
                            do
                            {
                                do
                                {
                                    do
                                    {
                                        do
                                        {
                                            if (!var2.hasNext())
                                            {
                                                return;
                                            }

                                            e = (EntityPlayer) var2.next();
                                        } while ((double) this.mc.player.getDistance(e) > this.getSettings().get(4).toSlider().getValue());
                                    } while (e.getHealth() <= 0.0F);
                                } while (e == this.mc.player);
                            } while (e == this.mc.player.getRidingEntity());
                        } while (e == this.mc.getRenderViewEntity());
                    } while (!this.mc.player.canEntityBeSeen(e) && !this.getSettings().get(2).toToggle().state);
                } while ((this.delay <= reqDelay && reqDelay != 0 || this.getSettings().get(1).toToggle().state) && (this.mc.player.getCooledAttackStrength(this.mc.getRenderPartialTicks()) < 1.0F || !this.getSettings().get(1).toToggle().state));

                if (this.getSettings().get(3).toToggle().state)
                {
                    Random rng = new Random();

                    double n = 1.282622531E-314D + 1.282622531E-314D * (1.0D + (double) rng.nextInt(rng.nextBoolean() ? 34 : 43));
                    double[] array = new double[] {1.531232163E-314D + n, 0.0D, 1.135895857E-315D + n, 0.0D};

                    for (double d : array)
                    {
                        this.mc.player.connection.sendPacket(new Position(this.mc.player.posX, this.mc.player.posY + d, this.mc.player.posZ, false));
                    }
                }

                this.mc.player.connection.sendPacket(new CPacketUseEntity(e, EnumHand.MAIN_HAND));
                this.mc.playerController.attackEntity(this.mc.player, e);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);

                this.delay = 0;
            }
        }
    }
}
