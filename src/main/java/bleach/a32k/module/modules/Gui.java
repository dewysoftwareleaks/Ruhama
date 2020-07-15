package bleach.a32k.module.modules;

import bleach.a32k.gui.AdvancedText;
import bleach.a32k.gui.TextWindow;
import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.module.ModuleManager;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Gui extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingSlider(0.0D, 255.0D, 235.0D, 0, "Ruhama R: "), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "Ruhama G: "), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "Ruhama B: "), new SettingToggle(true, "RainbowList"), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "List R: "), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "List G: "), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "List B: "));
    public static int arrayListEnd = 160;

    public Gui()
    {
        super("Gui", 0, Category.RENDER, "The Ingame ruhama gui", settings);
        this.getWindows().add(new TextWindow(2, 150, "Arraylist"));
    }

    public void onOverlay()
    {
        this.getWindows().get(0).clearText();
        int color = (new Color((int) this.getSettings().get(0).toSlider().getValue(), (int) this.getSettings().get(1).toSlider().getValue(), (int) this.getSettings().get(2).toSlider().getValue())).getRGB();
        String s = "Ruhama Client 0.8";
        ((TextWindow) this.getWindows().get(0)).addText(new AdvancedText(s, true, color));
        if (this.getSettings().get(3).toToggle().state)
        {
            int age = (int) (System.currentTimeMillis() / 20L % 510L);
            color = (new Color(255, MathHelper.clamp(age > 255 ? 510 - age : age, 0, 255), MathHelper.clamp(255 - (age > 255 ? 510 - age : age), 0, 255))).getRGB();
        } else
        {
            color = (new Color((int) this.getSettings().get(4).toSlider().getValue(), (int) this.getSettings().get(5).toSlider().getValue(), (int) this.getSettings().get(6).toSlider().getValue())).getRGB();
        }

        List<Module> arrayList = ModuleManager.getModules();
        arrayList.remove(this);
        arrayList.sort((a, b) ->
        {
            return Integer.compare(this.mc.fontRenderer.getStringWidth(b.getName()), this.mc.fontRenderer.getStringWidth(a.getName()));
        });
        Iterator var4 = arrayList.iterator();

        while (var4.hasNext())
        {
            Module m = (Module) var4.next();
            if (m.isToggled())
            {
                ((TextWindow) this.getWindows().get(0)).addText(new AdvancedText(m.getName(), true, color));
            }
        }

    }
}
