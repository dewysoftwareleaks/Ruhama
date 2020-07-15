package bleach.a32k.gui;

import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleWindowDark extends ModuleWindow
{
    public ModuleWindowDark(List<Module> mods, String name, int len, int posX, int posY)
    {
        super(mods, name, len, posX, posY);
    }

    public void draw(int mX, int mY, int leng)
    {
        this.mouseX = mX;
        this.mouseY = mY;
        this.len = leng;
        this.font = Minecraft.getMinecraft().fontRenderer;
        GuiScreen.drawRect(this.posX, this.posY - 10, this.posX + this.len, this.posY, this.mouseOver(this.posX, this.posY - 10, this.posX + this.len, this.posY) ? -1877995504 : -1875890128);
        this.font.drawStringWithShadow(this.name, (float) (this.posX + this.len / 2 - this.font.getStringWidth(this.name) / 2), (float) (this.posY - 9), 7384992);
        if (this.mouseOver(this.posX, this.posY - 10, this.posX + this.len, this.posY) && this.lmDown)
        {
            this.dragging = true;
        }

        if (!this.lmHeld)
        {
            this.dragging = false;
        }

        if (this.dragging)
        {
            this.posX = this.mouseX - (this.prevmX - this.posX);
            this.posY = this.mouseY - (this.prevmY - this.posY);
        }

        this.prevmX = this.mouseX;
        this.prevmY = this.mouseY;
        int count = 0;
        Iterator var5 = (new LinkedHashMap(this.mods)).entrySet().iterator();

        while (var5.hasNext())
        {
            Entry m = (Entry) var5.next();

            int c2;
            try
            {
                GuiScreen.drawRect(this.posX, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, this.mouseOver(this.posX, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14) ? 1882206320 : 1879048192);
                this.font.drawStringWithShadow(this.cutText(((Module) m.getKey()).getName(), this.len), (float) (this.posX + 2), (float) (this.posY + 3 + count * 14), ((Module) m.getKey()).isToggled() ? 7401440 : 12632256);
                GuiScreen.drawRect((Boolean) m.getValue() ? this.posX + this.len - 2 : this.posX + this.len - 1, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, (Boolean) m.getValue() ? -1619984400 : 1601241072);
                if (this.mouseOver(this.posX, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14))
                {
                    GL11.glTranslated(0.0D, 0.0D, 300.0D);
                    Matcher mat = Pattern.compile("\\b.{1,22}\\b\\W?").matcher(((Module) m.getKey()).getDesc());
                    c2 = 0;

                    int c3;
                    for (c3 = 0; mat.find(); ++c2)
                    {
                    }

                    mat.reset();

                    while (mat.find())
                    {
                        GuiScreen.drawRect(this.posX + this.len + 3, this.posY - 1 + count * 14 - c2 * 10 + c3 * 10, this.posX + this.len + 6 + this.font.getStringWidth(mat.group().trim()), this.posY + count * 14 - c2 * 10 + c3 * 10 + 9, -1879048144);
                        this.font.drawStringWithShadow(mat.group(), (float) (this.posX + this.len + 5), (float) (this.posY + count * 14 - c2 * 10 + c3 * 10), -1);
                        ++c3;
                    }

                    if (this.lmDown)
                    {
                        ((Module) m.getKey()).toggle();
                    }

                    if (this.rmDown)
                    {
                        this.mods.replace(m.getKey(), !(Boolean) m.getValue());
                    }

                    GL11.glTranslated(0.0D, 0.0D, -300.0D);
                }

                if ((Boolean) m.getValue())
                {
                    for (Iterator var14 = ((Module) m.getKey()).getSettings().iterator(); var14.hasNext(); GuiScreen.drawRect(this.posX + this.len - 1, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, -1619984400))
                    {
                        SettingBase s = (SettingBase) var14.next();
                        ++count;
                        if (s instanceof SettingMode)
                        {
                            this.drawModeSetting(s.toMode(), this.posX, this.posY + count * 14);
                        }

                        if (s instanceof SettingToggle)
                        {
                            this.drawToggleSetting(s.toToggle(), this.posX, this.posY + count * 14);
                        }

                        if (s instanceof SettingSlider)
                        {
                            this.drawSliderSetting(s.toSlider(), this.posX, this.posY + count * 14);
                        }
                    }

                    ++count;
                    this.drawBindSetting((Module) m.getKey(), this.keyDown, this.posX, this.posY + count * 14);
                    GuiScreen.drawRect(this.posX + this.len - 1, this.posY + count * 14, this.posX + this.len, this.posY + 14 + count * 14, -1619984400);
                }

                ++count;
            } catch (Exception var13)
            {
                c2 = 10;
                StackTraceElement[] var9 = var13.getStackTrace();
                int var10 = var9.length;

                for (int var11 = 0; var11 < var10; ++var11)
                {
                    StackTraceElement e69 = var9[var11];
                    this.font.drawStringWithShadow(e69.toString(), 10.0F, (float) c2, 16719904);
                    c2 += 10;
                }
            }
        }

        this.lmDown = false;
        this.rmDown = false;
        this.keyDown = -1;
    }

    public void drawBindSetting(Module m, int key, int x, int y)
    {
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        if (key != -1 && this.mouseOver(x, y, x + this.len, y + 14))
        {
            m.getKey().setKeyCode(key != 211 && key != 1 ? key : 0);
        }

        String name = Keyboard.getKeyName(m.getKey().getKeyCode());
        if (name == null)
        {
            name = "KEY" + m.getKey();
        }

        if (name.isEmpty())
        {
            name = "NONE";
        }

        this.font.drawStringWithShadow("Bind: " + name + (this.mouseOver(x, y, x + this.len, y + 14) ? "..." : ""), (float) (x + 2), (float) (y + 3), this.mouseOver(x, y, x + this.len, y + 14) ? 13616079 : 13623503);
    }

    public void drawModeSetting(SettingMode s, int x, int y)
    {
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        this.font.drawStringWithShadow(s.text + s.modes[s.mode], (float) (x + 2), (float) (y + 3), this.mouseOver(x, y, x + this.len, y + 14) ? 13616079 : 13623503);
        if (this.mouseOver(x, y, x + this.len, y + 14) && this.lmDown)
        {
            s.mode = s.getNextMode();
        }

    }

    public void drawToggleSetting(SettingToggle s, int x, int y)
    {
        int color;
        String color2;
        if (s.state)
        {
            if (this.mouseOver(x, y, x + this.len, y + 14))
            {
                color = -1876885728;
                color2 = "§2";
            } else
            {
                color = 1881210656;
                color2 = "§a";
            }
        } else if (this.mouseOver(x, y, x + this.len, y + 14))
        {
            color = -1862328288;
            color2 = "§4";
        } else
        {
            color = 1895768096;
            color2 = "§c";
        }

        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        GuiScreen.drawRect(x, y, x + 1, y + 14, color);
        this.font.drawStringWithShadow(color2 + s.text, (float) (x + 3), (float) (y + 3), -1);
        if (this.mouseOver(x, y, x + this.len, y + 14) && this.lmDown)
        {
            s.state = !s.state;
        }

    }

    public void drawSliderSetting(SettingSlider s, int x, int y)
    {
        int pixels = (int) Math.round(MathHelper.clamp((double) this.len * ((s.getValue() - s.min) / (s.max - s.min)), 0.0D, (double) this.len));
        GuiScreen.drawRect(x, y, x + this.len, y + 14, 1879048192);
        GuiScreen.drawRect(x, y, x + pixels, y + 14, -265256800);
        this.font.drawStringWithShadow(s.text + (s.round == 0 && s.value > 100.0D ? Integer.toString((int) s.value) : s.value), (float) (x + 2), (float) (y + 3), this.mouseOver(x, y, x + this.len, y + 14) ? 13616079 : 13623503);
        if (this.mouseOver(x, y, x + this.len, y + 14) && this.lmHeld)
        {
            int percent = (this.mouseX - x) * 100 / this.len;
            s.value = s.round((double) percent * ((s.max - s.min) / 100.0D) + s.min, s.round);
        }

    }
}
