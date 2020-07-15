package bleach.a32k;

import bleach.a32k.command.*;
import bleach.a32k.gui.AdvancedText;
import bleach.a32k.gui.NewRuhamaGui;
import bleach.a32k.gui.TextWindow;
import bleach.a32k.module.Module;
import bleach.a32k.module.ModuleManager;
import bleach.a32k.module.modules.ClickGui;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.utils.FileMang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@Mod(
        modid = "ruhama",
        name = "Ruhama",
        version = "0.8",
        acceptedMinecraftVersions = "[1.12.2]"
)
public class Ruhama
{
    public static final String VERSION = "0.8";
    public static final boolean PLUS = false;
    public static Minecraft mc = Minecraft.getMinecraft();
    public static HashMap<BlockPos, Integer> friendBlocks = new HashMap();
    private long timer = 0L;
    private boolean timerStart = false;

    @EventHandler
    public void cuckfuck(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        ClickGui.clickGui.initWindows();
        FileMang.init();
        FileMang.readModules();
        FileMang.readSettings();
        FileMang.readClickGui();
        FileMang.readBinds();
        FileMang.createFile("friends.txt");
        Iterator var2 = ModuleManager.getModules().iterator();

        while (var2.hasNext())
        {
            Module m = (Module) var2.next();
            Iterator var4 = m.getSettings().iterator();

            while (var4.hasNext())
            {
                SettingBase s = (SettingBase) var4.next();
                if (s instanceof SettingMode)
                {
                    s.toMode().mode = MathHelper.clamp(s.toMode().mode, 0, s.toMode().modes.length - 1);
                } else if (s instanceof SettingSlider)
                {
                    s.toSlider().value = MathHelper.clamp(s.toSlider().value, s.toSlider().min, s.toSlider().max);
                }
            }
        }

        this.timerStart = true;
    }

    @EventHandler
    public void cuckfucksuck(FMLPostInitializationEvent event)
    {
        ClientCommandHandler.instance.registerCommand(new PeekCmd.PeekCommand());
        ClientCommandHandler.instance.registerCommand(new LoginCmd());
        ClientCommandHandler.instance.registerCommand(new InvSorterCmd());
        ClientCommandHandler.instance.registerCommand(new StashFinderCmd());
        ClientCommandHandler.instance.registerCommand(new EntityDesyncCmd());
        MinecraftForge.EVENT_BUS.register(new PeekCmd());
    }

    @SubscribeEvent
    public void suckfuck(RenderWorldLastEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (mc.world.isBlockLoaded(mc.player.getPosition()))
            {
                ModuleManager.onRender();
            }
        }
    }

    @SubscribeEvent
    public void fuckcuck(Text event)
    {
        if (event.getType().equals(ElementType.TEXT))
        {
            if (!(mc.currentScreen instanceof NewRuhamaGui))
            {
                Iterator var2 = NewRuhamaGui.textWins.iterator();

                label41:
                while (true)
                {
                    MutableTriple e;
                    do
                    {
                        if (!var2.hasNext())
                        {
                            break label41;
                        }

                        e = (MutableTriple) var2.next();
                    } while (!ModuleManager.getModuleByName(((Module) e.left).getName()).isToggled());

                    int h = 2;

                    for (Iterator var5 = ((TextWindow) e.right).getText().iterator(); var5.hasNext(); h += 10)
                    {
                        AdvancedText s = (AdvancedText) var5.next();
                        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
                        int x = (double) ((TextWindow) e.right).posX > (double) scale.getScaledWidth() / 1.5D ? ((TextWindow) e.right).posX + ((TextWindow) e.right).len - mc.fontRenderer.getStringWidth(s.text) - 2 : (((TextWindow) e.right).posX < scale.getScaledWidth() / 3 ? ((TextWindow) e.right).posX + 2 : ((TextWindow) e.right).posX + ((TextWindow) e.right).len / 2 - mc.fontRenderer.getStringWidth(s.text) / 2);
                        if (s.shadow)
                        {
                            mc.fontRenderer.drawStringWithShadow(s.text, (float) x, (float) (((TextWindow) e.right).posY + h), s.color);
                        } else
                        {
                            mc.fontRenderer.drawString(s.text, x, ((TextWindow) e.right).posY + h, s.color);
                        }
                    }
                }
            }

            ModuleManager.onOverlay();
        }
    }

    @SubscribeEvent
    public void suckcuck(ClientChatEvent event)
    {
        if (ModuleManager.getModuleByName("RuhamaOntop").isToggled() && !event.getMessage().contains("ʀᴜʜᴀᴍᴀ") && !event.getMessage().startsWith("/"))
        {
            event.setCanceled(true);
            mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
            mc.player.sendChatMessage(event.getMessage() + " ｜ ʀᴜʜᴀᴍᴀ");
        }

    }

    @SubscribeEvent
    public void fucksuck(ClientTickEvent event)
    {
        if (System.currentTimeMillis() - 5000L > this.timer && this.timerStart)
        {
            this.timer = System.currentTimeMillis();
            FileMang.saveClickGui();
            FileMang.saveSettings();
            FileMang.saveModules();
            FileMang.saveBinds();
        }

        if (event.phase == Phase.START && mc.player != null && mc.world != null)
        {
            if (mc.world.isBlockLoaded(new BlockPos(mc.player.posX, 0.0D, mc.player.posZ)))
            {
                ModuleManager.onUpdate();
                ModuleManager.updateKeys();

                Entry e;
                try
                {
                    for (Iterator var2 = friendBlocks.entrySet().iterator(); var2.hasNext(); friendBlocks.replace(e.getKey(), (Integer) e.getValue() - 1))
                    {
                        e = (Entry) var2.next();
                        if ((Integer) e.getValue() <= 0)
                        {
                            friendBlocks.remove(e.getKey());
                        }
                    }
                } catch (Exception var4)
                {
                }

            }
        }
    }
}
