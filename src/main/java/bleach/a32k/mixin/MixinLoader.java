package bleach.a32k.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class MixinLoader implements IFMLLoadingPlugin
{
    public MixinLoader()
    {
        System.out.println("mixin loading \n\n\n\n\n\n\n\n\n\n");

        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.a32k.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");

        System.out.println(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass()
    {
        return new String[0];
    }

    public String getModContainerClass()
    {
        return null;
    }

    public String getSetupClass()
    {
        return null;
    }

    public void injectData(Map<String, Object> data)
    {
    }

    public String getAccessTransformerClass()
    {
        return null;
    }
}
