package bleach.a32k.command;

import bleach.a32k.utils.ReflectUtils;
import bleach.a32k.utils.RuhamaLogger;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Session;
import net.minecraftforge.client.IClientCommand;

import java.net.Proxy;

public class LoginCmd extends CommandBase implements IClientCommand
{
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message)
    {
        return false;
    }

    public String getName()
    {
        return "ruhamalogin";
    }

    public String getUsage(ICommandSender sender)
    {
        return null;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        try
        {
            if (this.login(args[0], args[1]) == "")
            {
                RuhamaLogger.log("Logged in");
            } else
            {
                RuhamaLogger.log("Invalid login");
            }
        } catch (Exception var5)
        {
        }

    }

    public String login(String email, String password)
    {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "")).createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);

        try
        {
            auth.logIn();
            ReflectUtils.getField(Minecraft.class, "session", "session").set(Minecraft.getMinecraft(), new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"));
            return "";
        } catch (Exception var5)
        {
            var5.printStackTrace();
            return "ï¿½4ï¿½loops!";
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }
}
