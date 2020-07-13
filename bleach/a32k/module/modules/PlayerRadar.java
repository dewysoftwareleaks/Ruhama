package bleach.a32k.module.modules;

import bleach.a32k.gui.AdvancedText;
import bleach.a32k.gui.TextWindow;
import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlayerRadar extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(false, "Round"));

    public PlayerRadar()
    {
        super("PlayerRadar", 0, Category.RENDER, "Shows nearby people", settings);
        this.getWindows().add(new TextWindow(100, 150, "PlayerRadar"));
    }

    public void onOverlay()
    {
        int c = Gui.arrayListEnd + 10;
        this.getWindows().get(0).clearText();
        Iterator var2 = this.mc.world.playerEntities.iterator();

        while (var2.hasNext())
        {
            EntityPlayer e = (EntityPlayer) var2.next();
            if (e != this.mc.player)
            {
                int color = 0;

                try
                {
                    color = e.getHealth() + e.getAbsorptionAmount() > 20.0F ? 2158832 : MathHelper.hsvToRGB((e.getHealth() + e.getAbsorptionAmount()) / 20.0F / 3.0F, 1.0F, 1.0F);
                } catch (Exception var11)
                {
                }

                double health = (new BigDecimal((double) (e.getHealth() + e.getAbsorptionAmount()))).setScale(1, RoundingMode.HALF_UP).doubleValue();
                double dist = (new BigDecimal((double) e.getDistance(this.mc.player))).setScale(1, RoundingMode.HALF_UP).doubleValue();
                boolean round = this.getSettings().get(0).toToggle().state;
                boolean dead = e.getHealth() <= 0.0F;
                if (round)
                {
                    if (dead)
                    {
                        ((TextWindow) this.getWindows().get(0)).addText(new AdvancedText((int) health + " " + e.getName() + " " + (int) dist + "m", true, color));
                    } else
                    {
                        ((TextWindow) this.getWindows().get(0)).addText(new AdvancedText((int) health + " " + (this.mc.objectMouseOver.entityHit == e ? TextFormatting.GOLD.toString() : TextFormatting.GRAY.toString()) + e.getName() + " " + TextFormatting.DARK_GRAY.toString() + (int) dist + "m", true, color));
                    }
                } else if (dead)
                {
                    ((TextWindow) this.getWindows().get(0)).addText(new AdvancedText(health + " " + e.getName() + " " + dist + "m", true, color));
                } else
                {
                    ((TextWindow) this.getWindows().get(0)).addText(new AdvancedText(health + " " + (this.mc.objectMouseOver.entityHit == e ? TextFormatting.GOLD.toString() : TextFormatting.GRAY.toString()) + e.getName() + " " + TextFormatting.DARK_GRAY.toString() + dist + "m", true, color));
                }

                c += 10;
            }
        }

        Gui.arrayListEnd = c;
    }
}
