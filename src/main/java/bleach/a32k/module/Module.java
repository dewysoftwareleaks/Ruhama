package bleach.a32k.module;

import bleach.a32k.gui.TextWindow;
import bleach.a32k.settings.SettingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.List;

public class Module
{
    private final Category category;
    private final String desc;
    
    private final List<TextWindow> windows = new ArrayList<>();
    
    public boolean keyActive = false;
    
    protected Minecraft mc = Minecraft.getMinecraft();
    
    private final String name;
    private KeyBinding key;
    
    private boolean toggled;
    private List<SettingBase> settings = new ArrayList<>();

    public Module(String nm, int k, Category c, String d, List<SettingBase> s)
    {
        this.name = nm;
        this.registerBind(nm, k);
        this.category = c;
        this.desc = d;
        
        if (s != null)
        {
            this.settings = s;
        }

        this.toggled = false;
    }

    public void toggle()
    {
        this.toggled = !this.toggled;
        
        if (this.toggled)
        {
            try
            {
                this.onEnable();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        } else
        {
            try
            {
                this.onDisable();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onEnable()
    {
    }

    public void onDisable()
    {
    }

    public void onUpdate()
    {
    }

    public void onRender()
    {
    }

    public void onOverlay()
    {
    }

    public boolean onPacketRead(Packet<?> packet)
    {
        return false;
    }

    public boolean onPacketSend()
    {
        return false;
    }

    public String getName()
    {
        return this.name;
    }
    
    public Category getCategory()
    {
        return this.category;
    }

    public String getDesc()
    {
        return this.desc;
    }

    public KeyBinding getKey()
    {
        return this.key;
    }
    
    public List<SettingBase> getSettings()
    {
        return this.settings;
    }

    public List<TextWindow> getWindows()
    {
        return this.windows;
    }

    public boolean isToggled()
    {
        return this.toggled;
    }

    public void setToggled(boolean toggled)
    {
        this.toggled = toggled;
    }

    public void registerBind(String name, int keycode)
    {
        this.key = new KeyBinding(name, keycode, "Ruhama");
        
        ClientRegistry.registerKeyBinding(this.key);
    }
}
