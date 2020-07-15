package bleach.a32k.module.modules;

import bleach.a32k.gui.NewRuhamaGui;
import bleach.a32k.module.Category;
import bleach.a32k.module.Module;

import java.util.List;

public class ClickGui extends Module
{
    public static NewRuhamaGui clickGui = new NewRuhamaGui();

    public ClickGui()
    {
        super("ClickGui", 0, Category.RENDER, "Clickgui", (List) null);
    }

    public void onEnable()
    {
        this.mc.displayGuiScreen(clickGui);
        this.setToggled(false);
    }
}
