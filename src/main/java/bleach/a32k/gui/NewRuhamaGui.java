package bleach.a32k.gui;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.module.ModuleManager;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewRuhamaGui extends GuiScreen
{
    public static List<MutableTriple<Module, Integer, TextWindow>> textWins = new ArrayList();
    public List<ModuleWindow> tabs = new ArrayList();

    public void initWindows()
    {
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.COMBAT), "Combat", 70, 30, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.RENDER), "Render", 70, 105, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.MISC), "Misc", 70, 180, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.EXPLOITS), "Exploits", 70, 255, 35));
        Iterator var1 = ModuleManager.getModules().iterator();

        while (var1.hasNext())
        {
            Module m = (Module) var1.next();
            int i = 0;

            for (Iterator var4 = m.getWindows().iterator(); var4.hasNext(); ++i)
            {
                TextWindow t = (TextWindow) var4.next();
                textWins.add(new MutableTriple(m, i, t));
            }
        }

    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.fontRenderer.drawStringWithShadow("Ruhama Client", 2.0F, 2.0F, 3166352);
        Iterator var4 = this.tabs.iterator();

        while (var4.hasNext())
        {
            ModuleWindow w = (ModuleWindow) var4.next();
            w.draw(mouseX, mouseY, 70);
        }

        var4 = textWins.iterator();

        while (var4.hasNext())
        {
            MutableTriple<Module, Integer, TextWindow> e = (MutableTriple) var4.next();
            ModuleManager.getModuleByName(((Module) e.left).getName()).getWindows().set((Integer) e.middle, e.right);
            if (ModuleManager.getModuleByName(((Module) e.left).getName()).isToggled())
            {
                ((TextWindow) e.right).draw(mouseX, mouseY);
            }
        }

    }

    protected boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY)
    {
        return mX > minX && mX < maxX && mY > minY && mY < maxY;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        Iterator var4;
        ModuleWindow w;
        if (mouseButton == 0)
        {
            var4 = this.tabs.iterator();

            while (var4.hasNext())
            {
                w = (ModuleWindow) var4.next();
                w.onLmPressed();
            }

            var4 = textWins.iterator();

            while (var4.hasNext())
            {
                MutableTriple<Module, Integer, TextWindow> e = (MutableTriple) var4.next();
                if (ModuleManager.getModuleByName(((Module) e.left).getName()).isToggled())
                {
                    ((TextWindow) e.right).onLmPressed();
                }
            }
        } else if (mouseButton == 1)
        {
            var4 = this.tabs.iterator();

            while (var4.hasNext())
            {
                w = (ModuleWindow) var4.next();
                w.onRmPressed();
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            Iterator var4 = this.tabs.iterator();

            while (var4.hasNext())
            {
                ModuleWindow w = (ModuleWindow) var4.next();
                w.onLmReleased();
            }

            var4 = textWins.iterator();

            while (var4.hasNext())
            {
                MutableTriple<Module, Integer, TextWindow> e = (MutableTriple) var4.next();
                if (ModuleManager.getModuleByName(((Module) e.left).getName()).isToggled())
                {
                    ((TextWindow) e.right).onLmReleased();
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException
    {
        Iterator var3 = this.tabs.iterator();

        while (var3.hasNext())
        {
            ModuleWindow w = (ModuleWindow) var3.next();
            w.onKeyPressed(keyCode);
        }

        super.keyTyped(typedChar, keyCode);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
