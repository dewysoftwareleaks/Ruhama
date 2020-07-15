package bleach.a32k.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextWindow
{
    private final List<AdvancedText> text = new ArrayList<>();
    public FontRenderer font;

    public int posX;
    public int posY;

    public int mouseX;
    public int mouseY;
    public int prevmX;
    public int prevmY;

    public boolean lmDown;
    public boolean lmHeld;
    public boolean dragging;

    public String title;
    public int len = 10;

    public TextWindow(int x, int y, String title)
    {
        this.posX = x;
        this.posY = y;

        this.title = title;
    }

    public void clearText()
    {
        this.text.clear();
    }

    public List<AdvancedText> getText()
    {
        return this.text;
    }

    public void addText(AdvancedText string)
    {
        this.text.add(string);
    }

    public void draw(int mX, int mY)
    {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());

        this.mouseX = mX;
        this.mouseY = mY;

        this.font = Minecraft.getMinecraft().fontRenderer;

        int height = 2 + this.text.size() * 10;
        this.len = this.font.getStringWidth(this.title) + 4;

        for (AdvancedText s : this.text)
        {
            int i = this.font.getStringWidth(s.text) + 6;

            if (this.len < i)
            {
                this.len = i;
            }
        }

        if (this.posX < 3 && this.posX > -3)
        {
            this.posX = 0;
        }

        if (this.posX + this.len > scale.getScaledWidth() - 3 && this.posX + this.len < scale.getScaledWidth() + 3)
        {
            this.posX = scale.getScaledWidth() - this.len;
        }

        if (this.posY - 10 < 2 && this.posY - 10 > -2)
        {
            this.posY = 10;
        }

        if (this.posY < 2 && this.posY > -2)
        {
            this.posY = 0;
        }

        if (this.posY + height > scale.getScaledHeight() - 3 && this.posX + height < scale.getScaledHeight() + 3)
        {
            this.posY = scale.getScaledHeight() - height;
        }

        GuiScreen.drawRect(this.posX, this.posY - 10, this.posX + this.len, this.posY, this.mouseOver(this.posX, this.posY - 10, this.posX + this.len, this.posY) ? -1877995504 : -1875890128);
        GuiScreen.drawRect(this.posX, this.posY, this.posX + this.len, this.posY + height, 1879048192);

        int h = 2;

        for (Iterator textIter = this.text.iterator(); textIter.hasNext(); h += 10)
        {
            AdvancedText s = (AdvancedText) textIter.next();
            int x = (double) this.posX > (double) scale.getScaledWidth() / 1.5D ? this.posX + this.len - this.font.getStringWidth(s.text) - 2 : (this.posX < scale.getScaledWidth() / 3 ? this.posX + 2 : this.posX + this.len / 2 - this.font.getStringWidth(s.text) / 2);

            if (s.shadow)
            {
                this.font.drawStringWithShadow(s.text, (float) x, (float) (this.posY + h), s.color);
            } else
            {
                this.font.drawString(s.text, x, this.posY + h, s.color);
            }
        }

        this.font.drawStringWithShadow(this.title, (float) (this.posX + this.len / 2 - this.font.getStringWidth(this.title) / 2), (float) (this.posY - 9), 7384992);

        if (this.mouseOver(this.posX, this.posY - 10, this.posX + this.len, this.posY + height) && this.lmDown)
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
    }

    protected boolean mouseOver(int minX, int minY, int maxX, int maxY)
    {
        return this.mouseX > minX && this.mouseX < maxX && this.mouseY > minY && this.mouseY < maxY;
    }

    public void onLmPressed()
    {
        this.lmDown = true;
        this.lmHeld = true;
    }

    public void onLmReleased()
    {
        this.lmHeld = false;
    }
}
