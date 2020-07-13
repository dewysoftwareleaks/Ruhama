package bleach.a32k.module;

import bleach.a32k.module.modules.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ModuleManager
{
    private static final List<Module> mods = Arrays.asList(new AntiChunkBan(), new Aura(), new Auto32k(), new AutoLog(), new AutoTotem(), new AutoWither(), new BedAura(), new ClickGui(), new Crasher(), new CrystalAura(), new CrystalAura2(), new DispenserAura(), new ElytraFly(), new ElytraReplace(), new Gui(), new HoleFiller(), new HoleFinderESP(), new HopperNuker(), new HopperRadius(), new InvSorter(), new NBTViewer(), new NewAuto32k(), new ObsidianTrap(), new PearlViewer(), new Peek(), new PlayerRadar(), new RuhamaOntop(), new ShulkerAura(), new StashFinder(), new StrengthESP(), new Surround(), new ThunderHack(), new TreeAura(), new TunnelESP(), new Welcomer());

    public static List<Module> getModules()
    {
        return new ArrayList(mods);
    }

    public static Module getModuleByName(String name)
    {
        Iterator var1 = mods.iterator();

        Module m;
        do
        {
            if (!var1.hasNext())
            {
                return null;
            }

            m = (Module) var1.next();
        } while (!name.equals(m.getName()));

        return m;
    }

    public static List<Module> getModulesInCat(Category cat)
    {
        List<Module> mds = new ArrayList();
        Iterator var2 = mods.iterator();

        while (var2.hasNext())
        {
            Module m = (Module) var2.next();
            if (m.getCategory().equals(cat))
            {
                mds.add(m);
            }
        }

        return mds;
    }

    public static void onUpdate()
    {
        Iterator var0 = mods.iterator();

        while (var0.hasNext())
        {
            Module m = (Module) var0.next();

            try
            {
                if (m.isToggled())
                {
                    m.onUpdate();
                }
            } catch (Exception var3)
            {
                var3.printStackTrace();
            }
        }

    }

    public static void onRender()
    {
        Iterator var0 = mods.iterator();

        while (var0.hasNext())
        {
            Module m = (Module) var0.next();

            try
            {
                if (m.isToggled())
                {
                    m.onRender();
                }
            } catch (Exception var3)
            {
                var3.printStackTrace();
            }
        }

    }

    public static void onOverlay()
    {
        Iterator var0 = mods.iterator();

        while (var0.hasNext())
        {
            Module m = (Module) var0.next();

            try
            {
                if (m.isToggled())
                {
                    m.onOverlay();
                }
            } catch (Exception var3)
            {
                var3.printStackTrace();
            }
        }

    }

    public static boolean onPacketRead(Packet<?> packet)
    {
        Iterator var1 = mods.iterator();

        while (var1.hasNext())
        {
            Module m = (Module) var1.next();

            try
            {
                if (m.isToggled() && m.onPacketRead(packet))
                {
                    return true;
                }
            } catch (Exception var4)
            {
                var4.printStackTrace();
            }
        }

        return false;
    }

    public static boolean onPacketSend(Packet<?> packet)
    {
        Iterator var1 = mods.iterator();

        while (var1.hasNext())
        {
            Module m = (Module) var1.next();

            try
            {
                if (m.isToggled() && m.onPacketSend(packet))
                {
                    return true;
                }
            } catch (Exception var4)
            {
                var4.printStackTrace();
            }
        }

        return false;
    }

    public static void updateKeys()
    {
        if (Minecraft.getMinecraft().currentScreen == null)
        {
            Iterator var0 = mods.iterator();

            while (var0.hasNext())
            {
                Module m = (Module) var0.next();

                try
                {
                    if (Keyboard.isKeyDown(m.getKey().getKeyCode()) && !m.keyActive)
                    {
                        m.keyActive = true;
                        m.toggle();
                    } else if (!Keyboard.isKeyDown(m.getKey().getKeyCode()))
                    {
                        m.keyActive = false;
                    }
                } catch (Exception var3)
                {
                }
            }

        }
    }
}
