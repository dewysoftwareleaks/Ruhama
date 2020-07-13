package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DispenserAura extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingMode("Mode: ", new String[] {"Block", "Mine"}), new SettingToggle(true, "2b Bypass"));
    public int breakingSlot = 0;
    public BlockPos breakingBlock;

    public DispenserAura()
    {
        super("DispenserAura", 0, Category.COMBAT, "Tries to block dispenser 32ks", settings);
    }

    public void onUpdate()
    {
        if (!(this.mc.currentScreen instanceof GuiContainer))
        {
            TileEntityDispenser dispenser = null;
            Iterator var2 = this.mc.world.loadedTileEntityList.iterator();

            while (var2.hasNext())
            {
                TileEntity t = (TileEntity) var2.next();
                if (t instanceof TileEntityDispenser && this.mc.player.getDistance((double) t.getPos().getX() + 0.5D, (double) t.getPos().getY() + 0.5D, (double) t.getPos().getZ() + 0.5D) <= (this.getSettings().get(0).toMode().mode == 0 ? 4.5D : 5.5D))
                {
                    dispenser = (TileEntityDispenser) t;
                    break;
                }
            }

            if (dispenser != null)
            {
                int i;
                if (this.getSettings().get(0).toMode().mode == 0)
                {
                    BlockPos jamPos = dispenser.getPos().offset((EnumFacing) this.mc.world.getBlockState(dispenser.getPos()).getValue(PropertyDirection.create("facing")));
                    if (this.mc.player.getDistance((double) jamPos.getX() + 0.5D, (double) jamPos.getY() + 0.5D, (double) jamPos.getZ() + 0.5D) > 4.25D)
                    {
                        return;
                    }

                    i = 0;

                    for (int i = 0; i <= 8; ++i)
                    {
                        Item item = this.mc.player.inventory.getStackInSlot(i).getItem();
                        if (item instanceof ItemBlock && !(((ItemBlock) item).getBlock() instanceof BlockShulkerBox) && ((ItemBlock) item).getBlock().getDefaultState().isFullCube())
                        {
                            i = i;
                            break;
                        }
                    }

                    WorldUtils.placeBlock(jamPos, i, this.getSettings().get(1).toToggle().state, false);
                } else if (this.getSettings().get(0).toMode().mode == 1)
                {
                    int pickaxeSlot = this.mc.player.inventory.currentItem;

                    for (i = 0; i < 8; ++i)
                    {
                        if (this.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe)
                        {
                            pickaxeSlot = i;
                        }
                    }

                    if (this.breakingBlock != null)
                    {
                        this.mc.player.inventory.currentItem = this.mc.world.getBlockState(this.breakingBlock).getBlock() == Blocks.AIR ? this.breakingSlot : pickaxeSlot;
                        if (this.mc.world.getBlockState(this.breakingBlock).getBlock() != Blocks.AIR && this.mc.player.getPositionVector().distanceTo((new Vec3d(this.breakingBlock)).add(0.5D, 0.5D, 0.5D)) <= 4.5D)
                        {
                            this.mc.playerController.onPlayerDamageBlock(this.breakingBlock, EnumFacing.UP);
                            this.mc.player.swingArm(EnumHand.MAIN_HAND);
                            return;
                        }

                        this.breakingBlock = null;
                        return;
                    }

                    this.breakingSlot = this.mc.player.inventory.currentItem;
                    this.mc.player.inventory.currentItem = pickaxeSlot;
                    this.mc.playerController.onPlayerDamageBlock(dispenser.getPos(), EnumFacing.UP);
                    this.mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.breakingBlock = dispenser.getPos();
                }

            }
        }
    }
}
