package bleach.a32k.gui;

public class AdvancedText
{
    public String text = "";
    public boolean shadow = true;
    public int color = -1;

    public AdvancedText(String text)
    {
        this.text = text;
    }

    public AdvancedText(String text, boolean shadow, int color)
    {
        this.text = text;
        this.shadow = shadow;
        this.color = color;
    }
}
