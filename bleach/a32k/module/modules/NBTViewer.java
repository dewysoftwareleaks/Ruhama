package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class NBTViewer extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(false, "Mobs Only"));

    public NBTViewer()
    {
        super("NBTViewer", 0, Category.MISC, "Shows nbt when hovering over a entity", settings);
    }

    public void onOverlay()
    {
        Entity e = this.mc.objectMouseOver.entityHit;
        if (e != null)
        {
            String[] text = e.serializeNBT().toString().split("(?=((\\{)|(?<=\\G.{100})))");
            int count = 30;
            boolean color1 = true;
            String[] var5 = text;
            int var6 = text.length;

            for (int var7 = 0; var7 < var6; ++var7)
            {
                String s = var5[var7];
                String s1 = "";
                char[] var10 = (s).toCharArray();
                int var11 = var10.length;

                for (int var12 = 0; var12 < var11; ++var12)
                {
                    Character c = var10[var12];
                    if (c.toString().contains("{"))
                    {
                        color1 = !color1;
                    }

                    s1 = s1 + (color1 ? TextFormatting.LIGHT_PURPLE.toString() : TextFormatting.DARK_PURPLE.toString()) + c;
                }

                this.mc.fontRenderer.drawStringWithShadow(s1, 40.0F, (float) count, -1);
                count += 10;
            }

        }
    }
}
