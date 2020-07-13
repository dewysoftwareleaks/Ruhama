package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObsidianTrap extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(false, "2b Bypass"));
    BlockPos blockpos1;
    BlockPos blockpos2;
    BlockPos blockpos3;
    BlockPos blockpos4;
    BlockPos blockpos5;
    BlockPos blockpos6;
    BlockPos blockpos7;
    BlockPos blockpos8;
    BlockPos blockpos9;
    BlockPos blockpos10;
    private EntityPlayer target;
    private List<EntityPlayer> targets;

    public ObsidianTrap()
    {
        super("ObsidianTrap", 0, Category.COMBAT, "Boxes players in a obsidian box", settings);
    }

    public boolean isInBlockRange(Entity target)
    {
        return target.getDistance(this.mc.player) <= 4.0F;
    }

    public boolean isValid(EntityPlayer entity)
    {
        return entity instanceof EntityPlayer && this.isInBlockRange(entity) && entity.getHealth() > 0.0F && !entity.isDead;
    }

    public void loadTargets()
    {
        Iterator var1 = this.mc.world.playerEntities.iterator();

        while (var1.hasNext())
        {
            EntityPlayer player = (EntityPlayer) var1.next();
            if (!(player instanceof EntityPlayerSP))
            {
                if (this.isValid(player))
                {
                    this.targets.add(player);
                } else if (this.targets.contains(player))
                {
                    this.targets.remove(player);
                }
            }
        }

    }

    private boolean isStackObby(ItemStack stack)
    {
        return stack != null && stack.getItem() == Item.getItemById(49);
    }

    private boolean doesHotbarHaveObby()
    {
        for (int i = 36; i < 45; ++i)
        {
            ItemStack stack = this.mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && this.isStackObby(stack))
            {
                return true;
            }
        }

        return false;
    }

    public void onUpdate()
    {
        if (!this.mc.player.isHandActive())
        {
            if (!this.isValid(this.target) || this.target == null)
            {
                this.updateTarget();
            }

            Iterator var1 = this.mc.world.playerEntities.iterator();

            EntityPlayer player;
            do
            {
                if (!var1.hasNext())
                {
                    if (this.isValid(this.target) && this.mc.player.getDistance(this.target) < 4.0F)
                    {
                        this.trap(this.target);
                    }

                    return;
                }

                player = (EntityPlayer) var1.next();
            } while (player instanceof EntityPlayerSP || !this.isValid(player) || player.getDistance(this.mc.player) >= this.target.getDistance(this.mc.player));

            this.target = player;
        }
    }

    private void trap(EntityPlayer player)
    {
        if (this.doesHotbarHaveObby())
        {
            this.blockpos1 = new BlockPos(player.posX, player.posY + 2.0D, player.posZ);
            this.blockpos2 = new BlockPos(player.posX + 1.0D, player.posY + 2.0D, player.posZ);
            this.blockpos3 = new BlockPos(player.posX + 1.0D, player.posY, player.posZ);
            this.blockpos4 = new BlockPos(player.posX, player.posY, player.posZ + 1.0D);
            this.blockpos5 = new BlockPos(player.posX - 1.0D, player.posY, player.posZ);
            this.blockpos6 = new BlockPos(player.posX, player.posY, player.posZ - 1.0D);
            this.blockpos7 = new BlockPos(player.posX + 1.0D, player.posY + 1.0D, player.posZ);
            this.blockpos8 = new BlockPos(player.posX, player.posY + 1.0D, player.posZ + 1.0D);
            this.blockpos9 = new BlockPos(player.posX - 1.0D, player.posY + 1.0D, player.posZ);
            this.blockpos10 = new BlockPos(player.posX, player.posY + 1.0D, player.posZ - 1.0D);

            for (int i = 36; i < 45; ++i)
            {
                ItemStack stack = this.mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && this.isStackObby(stack))
                {
                    int oldSlot = this.mc.player.inventory.currentItem;
                    if (this.mc.world.getBlockState(this.blockpos1).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos3).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos4).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos5).getMaterial().isReplaceable() || this.mc.world.getBlockState(this.blockpos6).getMaterial().isReplaceable())
                    {
                        this.mc.player.inventory.currentItem = i - 36;
                        if (this.mc.world.getBlockState(this.blockpos3).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos3, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos4).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos4, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos5).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos5, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos6).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos6, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos7).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos7, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos8).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos8, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos9).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos9, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos10).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos10, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos2).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos2, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, false);
                        }

                        if (this.mc.world.getBlockState(this.blockpos1).getMaterial().isReplaceable())
                        {
                            WorldUtils.placeBlock(this.blockpos1, this.mc.player.inventory.currentItem, this.getSettings().get(0).toToggle().state, this.getSettings().get(0).toToggle().state);
                        }

                        this.mc.player.inventory.currentItem = oldSlot;
                        break;
                    }
                }
            }
        }

    }

    public void onDisable()
    {
        this.target = null;
    }

    public void updateTarget()
    {
        Iterator var1 = this.mc.world.playerEntities.iterator();

        while (var1.hasNext())
        {
            EntityPlayer player = (EntityPlayer) var1.next();
            if (!(player instanceof EntityPlayerSP) && !(player instanceof EntityPlayerSP) && this.isValid(player))
            {
                this.target = player;
            }
        }

    }
}
