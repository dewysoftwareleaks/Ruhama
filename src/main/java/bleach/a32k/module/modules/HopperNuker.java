package bleach.a32k.module.modules;

import bleach.a32k.Ruhama;
import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HopperNuker extends Module
{
    public int breakingSlot = 0;
    public BlockPos breakingBlock;

    public HopperNuker()
    {
        super("HopperNuker", 0, Category.COMBAT, "Nukes Hoppers Arond you", null);
    }

    public void onUpdate()
    {
        if (!(this.mc.currentScreen instanceof GuiContainer))
        {
            if (this.breakingBlock != null && !Ruhama.friendBlocks.containsKey(this.breakingBlock))
            {
                if (this.mc.world.getBlockState(this.breakingBlock).getBlock() != Blocks.AIR && this.mc.player.getPositionVector().distanceTo((new Vec3d(this.breakingBlock)).add(0.5D, 0.5D, 0.5D)) <= 4.5D)
                {
                    this.mc.playerController.onPlayerDamageBlock(this.breakingBlock, EnumFacing.UP);
                    this.mc.player.swingArm(EnumHand.MAIN_HAND);
                } else
                {
                    this.breakingBlock = null;
                    this.mc.player.inventory.currentItem = this.breakingSlot;
                }
            } else
            {
                this.breakingSlot = this.mc.player.inventory.currentItem;
                int pickaxeSlot = this.mc.player.inventory.currentItem;

                int x;
                for (x = 0; x < 8; ++x)
                {
                    if (this.mc.player.inventory.getStackInSlot(x).getItem() instanceof ItemPickaxe)
                    {
                        pickaxeSlot = x;
                    }
                }

                for (x = -4; x <= 4; ++x)
                {
                    for (int y = -4; y <= 4; ++y)
                    {
                        for (int z = -4; z <= 4; ++z)
                        {
                            BlockPos pos = this.mc.player.getPosition().add(x, y, z);
                            if (this.mc.world.getBlockState(pos).getBlock() instanceof BlockHopper && this.mc.world.getBlockState(pos.up()).getBlock() instanceof BlockShulkerBox && !Ruhama.friendBlocks.containsKey(pos) && this.mc.player.getPositionVector().distanceTo((new Vec3d(pos)).add(0.5D, 0.5D, 0.5D)) <= 5.25D)
                            {
                                this.mc.player.inventory.currentItem = pickaxeSlot;
                                this.mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                                this.breakingBlock = pos;
                            }
                        }
                    }
                }

            }
        }
    }
}
