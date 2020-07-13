package bleach.a32k.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class RuhamaLogger
{
    public static void log(String text)
    {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(TextFormatting.RED + "Ruhama: " + TextFormatting.RESET + text));
    }
}
