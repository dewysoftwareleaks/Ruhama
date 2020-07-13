package bleach.a32k.command;

import bleach.a32k.module.ModuleManager;
import bleach.a32k.settings.SettingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class PeekCmd
{
    public static int metadataTicks = -1;
    public static int guiTicks = -1;
    public static ItemStack shulker;
    public static EntityItem drop;
    public static InventoryBasic toOpen;

    static
    {
        shulker = ItemStack.EMPTY;
    }

    public static NBTTagCompound getShulkerNBT(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag", 10))
        {
            NBTTagCompound tags = compound.getCompoundTag("BlockEntityTag");
            if (tags.hasKey("Items", 9))
            {
                return tags;
            }
        }

        return null;
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityItem)
        {
            drop = (EntityItem) entity;
            metadataTicks = 0;
        }

    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event)
    {
        if (ModuleManager.getModuleByName("Peek").isToggled() && ((SettingBase) ModuleManager.getModuleByName("Peek").getSettings().get(2)).toToggle().state)
        {
            if (event.phase == Phase.END)
            {
                if (guiTicks > -1)
                {
                    ++guiTicks;
                }

                if (metadataTicks > -1)
                {
                    ++metadataTicks;
                }
            }

            if (metadataTicks == 20)
            {
                metadataTicks = -1;
                if (drop.getItem().getItem() instanceof ItemShulkerBox)
                {
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("New shulker found! use /peek to view its content " + TextFormatting.GREEN + "(" + drop.getItem().getDisplayName() + ")"));
                    shulker = drop.getItem();
                }
            }

            if (guiTicks == 20)
            {
                guiTicks = -1;
                Minecraft.getMinecraft().player.displayGUIChest(toOpen);
            }

        }
    }

    public static class PeekCommand extends CommandBase implements IClientCommand
    {
        public boolean allowUsageWithoutPrefix(ICommandSender sender, String message)
        {
            return false;
        }

        public String getName()
        {
            return "peek";
        }

        public String getUsage(ICommandSender sender)
        {
            return null;
        }

        public void execute(MinecraftServer server, ICommandSender sender, String[] args)
        {
            if (ModuleManager.getModuleByName("Peek").isToggled() && ((SettingBase) ModuleManager.getModuleByName("Peek").getSettings().get(2)).toToggle().state)
            {
                if (!PeekCmd.shulker.isEmpty())
                {
                    NBTTagCompound shulkerNBT = PeekCmd.getShulkerNBT(PeekCmd.shulker);
                    TileEntityShulkerBox fakeShulker = new TileEntityShulkerBox();
                    String customName = "container.shulkerBox";
                    boolean hasCustomName = false;
                    if (shulkerNBT != null)
                    {
                        fakeShulker.loadFromNbt(shulkerNBT);
                        if (shulkerNBT.hasKey("CustomName", 8))
                        {
                            customName = shulkerNBT.getString("CustomName");
                            hasCustomName = true;
                        }
                    }

                    InventoryBasic inv = new InventoryBasic(customName, hasCustomName, 27);

                    for (int i = 0; i < 27; ++i)
                    {
                        ItemStack stack = fakeShulker.getStackInSlot(i);
                        inv.setInventorySlotContents(i, stack == null ? new ItemStack(Items.AIR) : stack);
                    }

                    PeekCmd.toOpen = inv;
                    PeekCmd.guiTicks = 0;
                } else
                {
                    sender.sendMessage(new TextComponentString("No shulker detected! please drop and pickup your shulker."));
                }

            }
        }

        public boolean checkPermission(MinecraftServer server, ICommandSender sender)
        {
            return true;
        }
    }
}
