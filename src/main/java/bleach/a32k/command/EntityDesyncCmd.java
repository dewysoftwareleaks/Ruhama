package bleach.a32k.command;

import bleach.a32k.utils.RuhamaLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class EntityDesyncCmd extends CommandBase implements IClientCommand
{
    private final Minecraft mc = Minecraft.getMinecraft();

    public Entity entity;
    public boolean dismounted;

    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message)
    {
        return false;
    }

    public String getName()
    {
        return "entitydesync";
    }

    public String getUsage(ICommandSender sender)
    {
        return null;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 1 && (args[0].equalsIgnoreCase("dismount") || args[0].equalsIgnoreCase("remount")))
        {
            if (args[0].equalsIgnoreCase("dismount"))
            {
                this.dismounted = true;
            } else if (args[0].equalsIgnoreCase("remount"))
            {
                this.dismounted = false;
            }

            if (this.dismounted)
            {
                if (this.mc.player.getRidingEntity() == null)
                {
                    RuhamaLogger.log("No entity to dismount");
                    return;
                }

                this.mc.renderGlobal.loadRenderers();
                this.entity = this.mc.player.getRidingEntity();
                this.mc.player.dismountRidingEntity();
                this.mc.world.removeEntity(this.entity);

                MinecraftForge.EVENT_BUS.register(this);
                RuhamaLogger.log("Dismounted");
            } else
            {
                if (this.entity == null)
                {
                    RuhamaLogger.log("No entity to remount");
                    return;
                }

                this.entity.isDead = false;
                this.mc.world.loadedEntityList.add(this.entity);
                this.mc.player.startRiding(this.entity, true);

                this.entity = null;

                MinecraftForge.EVENT_BUS.unregister(this);
                RuhamaLogger.log("Remounted");
            }
        } else
        {
            RuhamaLogger.log("Invalid syntax, /entitydesync (dismount/remount)");
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event)
    {
        if (event.phase == Phase.START && this.mc.player != null && this.mc.world != null)
        {
            if (this.mc.world.isBlockLoaded(new BlockPos(this.mc.player.posX, 0.0D, this.mc.player.posZ)))
            {
                if (this.mc.player.getRidingEntity() != null)
                {
                    this.entity = null;
                }

                if (this.entity == null && this.mc.player.getRidingEntity() != null)
                {
                    this.entity = this.mc.player.getRidingEntity();

                    this.mc.player.dismountRidingEntity();
                    this.mc.world.removeEntity(this.entity);
                }

                if (this.entity != null)
                {
                    this.entity.setPosition(this.mc.player.posX, this.mc.player.posY, this.mc.player.posZ);
                    this.mc.player.connection.sendPacket(new CPacketVehicleMove(this.entity));
                }
            }
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }
}
