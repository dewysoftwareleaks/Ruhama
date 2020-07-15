package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;

public class NBTViewer extends Module
{
    private static final List<SettingBase> settings = Collections.singletonList(new SettingToggle(false, "Mobs Only"));

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

            for (String s : text)
            {
                StringBuilder s1 = new StringBuilder();
                char[] textChars = (s).toCharArray();

                for (Character c : textChars)
                {
                    if (c.toString().contains("{"))
                    {
                        color1 = !color1;
                    }

                    s1.append(color1 ? TextFormatting.LIGHT_PURPLE.toString() : TextFormatting.DARK_PURPLE.toString()).append(c);
                }

                this.mc.fontRenderer.drawStringWithShadow(s1.toString(), 40.0F, (float) count, -1);
                count += 10;
            }
        }
    }
}
