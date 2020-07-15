package bleach.a32k.settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SettingMode extends SettingBase
{
    public String[] modes;
    public int mode;
    public String text;

    public SettingMode(String text, String... modes)
    {
        this.modes = modes;
        this.text = text;
    }

    public static String getHwid()
    {
        try
        {
            MessageDigest hash = MessageDigest.getInstance("MD5");
            String s = System.getenv("os") + System.getProperty("os.name") + System.getProperty("user.language") + System.getProperty("os.version") + System.getProperty("os.arch") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("NUMBER_OF_PROCESSORS") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("SystemRoot");
            byte[] bytes = hash.digest(s.getBytes());
            char[] hexChars = new char[bytes.length * 2];

            for (int j = 0; j < bytes.length; ++j)
            {
                int v = bytes[j] & 255;
                hexChars[j * 2] = "0123456789ABCDEF".toCharArray()[v >>> 4];
                hexChars[j * 2 + 1] = "0123456789ABCDEF".toCharArray()[v & 15];
            }

            return new String(hexChars);
        } catch (NoSuchAlgorithmException var6)
        {
            throw new Error("We did an oopsy poopsy, woopsy.", var6);
        }
    }

    public int getNextMode()
    {
        return this.mode + 1 >= this.modes.length ? 0 : this.mode + 1;
    }
}
