package bleach.a32k.utils;

import bleach.a32k.gui.ModuleWindow;
import bleach.a32k.gui.NewRuhamaGui;
import bleach.a32k.gui.TextWindow;
import bleach.a32k.module.Module;
import bleach.a32k.module.ModuleManager;
import bleach.a32k.module.modules.ClickGui;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileMang
{
    private static Path dir;

    public static void init()
    {
        dir = Paths.get(Minecraft.getMinecraft().gameDir.getPath(), "bleach", "ruhama/");
        if (!dir.toFile().exists())
        {
            dir.toFile().mkdirs();
        }

    }

    public static Path getDir()
    {
        return dir;
    }

    public static List<String> readFileLines(String... file)
    {
        try
        {
            return Files.readAllLines(stringsToPath(file));
        } catch (IOException var2)
        {
            System.out.println("Error Reading File: " + stringsToPath(file));
            var2.printStackTrace();
            return new ArrayList();
        }
    }

    public static void createFile(String... file)
    {
        try
        {
            if (fileExists(file))
            {
                return;
            }

            dir.toFile().mkdirs();
            Files.createFile(stringsToPath(file));
        } catch (IOException var2)
        {
            System.out.println("Error Creating File: " + file);
            var2.printStackTrace();
        }

    }

    public static void createEmptyFile(String... file)
    {
        try
        {
            dir.toFile().mkdirs();
            if (!fileExists(file))
            {
                Files.createFile(stringsToPath(file));
            }

            FileWriter writer = new FileWriter(stringsToPath(file).toFile());
            writer.write("");
            writer.close();
        } catch (IOException var2)
        {
            System.out.println("Error Clearing/Creating File: " + file);
            var2.printStackTrace();
        }

    }

    public static void appendFile(String content, String... file)
    {
        try
        {
            FileWriter writer = new FileWriter(stringsToPath(file).toFile(), true);
            writer.write(content + "\n");
            writer.close();
        } catch (IOException var3)
        {
            System.out.println("Error Appending File: " + file);
            var3.printStackTrace();
        }

    }

    public static boolean fileExists(String... file)
    {
        try
        {
            return stringsToPath(file).toFile().exists();
        } catch (Exception var2)
        {
            return false;
        }
    }

    public static void deleteFile(String... file)
    {
        try
        {
            Files.deleteIfExists(stringsToPath(file));
        } catch (Exception var2)
        {
            System.out.println("Error Deleting File: " + file);
            var2.printStackTrace();
        }

    }

    public static Path stringsToPath(String... strings)
    {
        Path path = dir;
        String[] var2 = strings;
        int var3 = strings.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            String s = var2[var4];
            path = path.resolve(s);
        }

        return path;
    }

    public static void saveSettings()
    {
        createEmptyFile("settings.txt");
        String lines = "";

        String line;
        for (Iterator var1 = ModuleManager.getModules().iterator(); var1.hasNext(); lines = lines + line + "\n")
        {
            Module m = (Module) var1.next();
            line = m.getName();
            int count = 0;

            for (Iterator var5 = m.getSettings().iterator(); var5.hasNext(); ++count)
            {
                SettingBase set = (SettingBase) var5.next();
                if (set instanceof SettingSlider)
                {
                    line = line + ":" + m.getSettings().get(count).toSlider().getValue();
                }

                if (set instanceof SettingMode)
                {
                    line = line + ":" + m.getSettings().get(count).toMode().mode;
                }

                if (set instanceof SettingToggle)
                {
                    line = line + ":" + m.getSettings().get(count).toToggle().state;
                }
            }
        }

        appendFile(lines, "settings.txt");
    }

    public static void readSettings()
    {
        List<String> lines = readFileLines("settings.txt");
        Iterator var1 = ModuleManager.getModules().iterator();

        label51:
        while (var1.hasNext())
        {
            Module m = (Module) var1.next();
            Iterator var3 = lines.iterator();

            while (true)
            {
                String[] line;
                do
                {
                    if (!var3.hasNext())
                    {
                        continue label51;
                    }

                    String s = (String) var3.next();
                    line = s.split(":");
                } while (!line[0].startsWith(m.getName()));

                int count = 0;

                for (Iterator var7 = m.getSettings().iterator(); var7.hasNext(); ++count)
                {
                    SettingBase set = (SettingBase) var7.next();

                    try
                    {
                        if (set instanceof SettingSlider)
                        {
                            m.getSettings().get(count).toSlider().value = Double.parseDouble(line[count + 1]);
                        }

                        if (set instanceof SettingMode)
                        {
                            m.getSettings().get(count).toMode().mode = Integer.parseInt(line[count + 1]);
                        }

                        if (set instanceof SettingToggle)
                        {
                            m.getSettings().get(count).toToggle().state = Boolean.parseBoolean(line[count + 1]);
                        }
                    } catch (Exception var10)
                    {
                    }
                }
            }
        }

    }

    public static void saveClickGui()
    {
        createEmptyFile("clickgui.txt");
        String text = "";

        ModuleWindow w;
        for (Iterator var1 = ClickGui.clickGui.tabs.iterator(); var1.hasNext(); text = text + w.getPos()[0] + ":" + w.getPos()[1] + "\n")
        {
            w = (ModuleWindow) var1.next();
        }

        appendFile(text, "clickgui.txt");
        createEmptyFile("clickguitext.txt");
        String text2 = "";

        MutableTriple e;
        for (Iterator var5 = NewRuhamaGui.textWins.iterator(); var5.hasNext(); text2 = text2 + ((Module) e.left).getName() + ":" + e.middle + ":" + ((TextWindow) e.getRight()).posX + ":" + ((TextWindow) e.getRight()).posY + "\n")
        {
            e = (MutableTriple) var5.next();
        }

        appendFile(text2, "clickguitext.txt");
    }

    public static void readClickGui()
    {
        List lines = readFileLines("clickgui.txt");

        try
        {
            int c = 0;

            for (Iterator var2 = ClickGui.clickGui.tabs.iterator(); var2.hasNext(); ++c)
            {
                ModuleWindow w = (ModuleWindow) var2.next();
                w.setPos(Integer.parseInt(((String) lines.get(c)).split(":")[0]), Integer.parseInt(((String) lines.get(c)).split(":")[1]));
            }
        } catch (Exception var8)
        {
        }

        Iterator var9 = readFileLines("clickguitext.txt").iterator();

        while (var9.hasNext())
        {
            String s = (String) var9.next();
            String[] split = s.split(":");
            Iterator var4 = NewRuhamaGui.textWins.iterator();

            while (var4.hasNext())
            {
                MutableTriple e = (MutableTriple) var4.next();

                try
                {
                    if (((Module) e.left).getName().equals(split[0]) && ((Integer) e.middle).equals(Integer.parseInt(split[1])))
                    {
                        ((TextWindow) e.right).posX = Integer.parseInt(split[2]);
                        ((TextWindow) e.right).posY = Integer.parseInt(split[3]);
                    }
                } catch (Exception var7)
                {
                    var7.printStackTrace();
                }
            }
        }

    }

    public static void saveModules()
    {
        createEmptyFile("modules.txt");
        String lines = "";
        Iterator var1 = ModuleManager.getModules().iterator();

        while (var1.hasNext())
        {
            Module m = (Module) var1.next();
            if (m.getName() != "ClickGui" && m.getName() != "Freecam")
            {
                lines = lines + m.getName() + ":" + m.isToggled() + "\n";
            }
        }

        appendFile(lines, "modules.txt");
    }

    public static void readModules()
    {
        List<String> lines = readFileLines("modules.txt");
        Iterator var1 = ModuleManager.getModules().iterator();

        while (var1.hasNext())
        {
            Module m = (Module) var1.next();
            Iterator var3 = lines.iterator();

            while (var3.hasNext())
            {
                String s = (String) var3.next();
                String[] line = s.split(":");

                try
                {
                    if (line[0].contains(m.getName()) && line[1].contains("true"))
                    {
                        m.toggle();
                        break;
                    }
                } catch (Exception var7)
                {
                }
            }
        }

    }

    public static void saveBinds()
    {
        createEmptyFile("binds.txt");
        String lines = "";

        Module m;
        for (Iterator var1 = ModuleManager.getModules().iterator(); var1.hasNext(); lines = lines + m.getName() + ":" + m.getKey().getKeyCode() + "\n")
        {
            m = (Module) var1.next();
        }

        appendFile(lines, "binds.txt");
    }

    public static void readBinds()
    {
        List<String> lines = readFileLines("binds.txt");
        Iterator var1 = ModuleManager.getModules().iterator();

        while (var1.hasNext())
        {
            Module m = (Module) var1.next();
            Iterator var3 = lines.iterator();

            while (var3.hasNext())
            {
                String s = (String) var3.next();
                String[] line = s.split(":");
                if (line[0].startsWith(m.getName()))
                {
                    try
                    {
                        m.getKey().setKeyCode(Integer.parseInt(line[line.length - 1]));
                    } catch (Exception var7)
                    {
                    }
                }
            }
        }

    }
}
