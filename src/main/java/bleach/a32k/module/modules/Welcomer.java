package bleach.a32k.module.modules;

import bleach.a32k.gui.AdvancedText;
import bleach.a32k.gui.TextWindow;
import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Welcomer extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingSlider(0.0D, 255.0D, 235.0D, 0, "Text R: "), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "Text G: "), new SettingSlider(0.0D, 255.0D, 235.0D, 0, "Text B: "), new SettingToggle(false, "Shadow"));

    public Welcomer()
    {
        super("Welcomer", 0, Category.RENDER, "Welcomes you", settings);

        this.getWindows().add(new TextWindow(50, 12, "Welcomer"));
    }

    public void onOverlay()
    {
        boolean shadow = this.getSettings().get(3).toToggle().state;

        int color = (new Color((int) this.getSettings().get(0).toSlider().getValue(), (int) this.getSettings().get(1).toSlider().getValue(), (int) this.getSettings().get(2).toSlider().getValue())).getRGB();

        this.getWindows().get(0).clearText();
        this.getWindows().get(0).addText(new AdvancedText("Hello " + this.mc.player.getName() + " :^)", shadow, color));
    }
}
