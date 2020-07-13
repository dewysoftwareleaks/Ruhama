package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;

import java.util.Arrays;
import java.util.List;

public class Peek extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(true, "Map"), new SettingToggle(true, "Book"), new SettingToggle(true, "Shulker Cmd"));

    public Peek()
    {
        super("Peek", 0, Category.MISC, "Shows content of stuff", settings);
    }
}
