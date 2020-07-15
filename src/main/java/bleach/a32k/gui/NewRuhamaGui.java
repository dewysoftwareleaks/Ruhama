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
import java.util.Objects;

public class NewRuhamaGui extends GuiScreen
{
    public static List<MutableTriple<Module, Integer, TextWindow>> textWins = new ArrayList<>();
    public List<ModuleWindow> tabs = new ArrayList<>();

    public void initWindows()
    {
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.COMBAT), "Combat", 70, 30, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.RENDER), "Render", 70, 105, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.MISC), "Misc", 70, 180, 35));
        this.tabs.add(new ModuleWindowDark(ModuleManager.getModulesInCat(Category.EXPLOITS), "Exploits", 70, 255, 35));

        for (Module m : ModuleManager.getModules())
        {
            int i = 0;

            for (Iterator windows = m.getWindows().iterator(); windows.hasNext(); ++i)
            {
                TextWindow t = (TextWindow) windows.next();
                textWins.add(new MutableTriple<>(m, i, t));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.fontRenderer.drawStringWithShadow("Ruhama Client", 2.0F, 2.0F, 3166352);

        Iterator tabsIter = this.tabs.iterator();

        while (tabsIter.hasNext())
        {
            ModuleWindow w = (ModuleWindow) tabsIter.next();
            w.draw(mouseX, mouseY, 70);
        }

        tabsIter = textWins.iterator();

        while (tabsIter.hasNext())
        {
            MutableTriple<Module, Integer, TextWindow> e = (MutableTriple<Module, Integer, TextWindow>) tabsIter.next();
            Objects.requireNonNull(ModuleManager.getModuleByName(e.left.getName())).getWindows().set(e.middle, e.right);
            if (Objects.requireNonNull(ModuleManager.getModuleByName(e.left.getName())).isToggled())
            {
                e.right.draw(mouseX, mouseY);
            }
        }
    }

    protected boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY)
    {
        return mX > minX && mX < maxX && mY > minY && mY < maxY;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        Iterator windowsIter;

        ModuleWindow w;

        if (mouseButton == 0)
        {
            windowsIter = this.tabs.iterator();

            while (windowsIter.hasNext())
            {
                w = (ModuleWindow) windowsIter.next();
                w.onLmPressed();
            }

            windowsIter = textWins.iterator();

            while (windowsIter.hasNext())
            {
                MutableTriple<Module, Integer, TextWindow> e = (MutableTriple<Module, Integer, TextWindow>) windowsIter.next();
                if (Objects.requireNonNull(ModuleManager.getModuleByName(e.left.getName())).isToggled())
                {
                    e.right.onLmPressed();
                }
            }
        } else if (mouseButton == 1)
        {
            windowsIter = this.tabs.iterator();

            while (windowsIter.hasNext())
            {
                w = (ModuleWindow) windowsIter.next();
                w.onRmPressed();
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            Iterator fernflowerMoment = this.tabs.iterator();

            while (fernflowerMoment.hasNext())
            {
                ModuleWindow w = (ModuleWindow) fernflowerMoment.next();
                w.onLmReleased();
            }

            fernflowerMoment = textWins.iterator();

            while (fernflowerMoment.hasNext())
            {
                MutableTriple<Module, Integer, TextWindow> e = (MutableTriple<Module, Integer, TextWindow>) fernflowerMoment.next();

                if (Objects.requireNonNull(ModuleManager.getModuleByName(e.left.getName())).isToggled())
                {
                    e.right.onLmReleased();
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException
    {
        for (ModuleWindow w : this.tabs)
        {
            w.onKeyPressed(keyCode);
        }

        super.keyTyped(typedChar, keyCode);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
