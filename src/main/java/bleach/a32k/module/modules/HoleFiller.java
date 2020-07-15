package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HoleFiller extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingSlider(1.0D, 5.0D, 3.0D, 2, "Radius: "), new SettingSlider(1.0D, 10.0D, 5.0D, 2, "Range: "), new SettingToggle(true, "2b Bypass"));

    public HoleFiller()
    {
        super("HoleFiller", 0, Category.COMBAT, "Fills holes that other people can jump into", settings);
    }

    public void onUpdate()
    {
        int v7 = -1;

        for (int i = 0; i < 8; ++i)
        {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN))
            {
                v7 = i;
            }
        }

        if (v7 != -1)
        {
            Iterator<EntityPlayer> playersIter = this.mc.world.playerEntities.iterator();

            ferngayler:

            while (true)
            {
                EntityPlayer v;

                do
                {
                    if (!playersIter.hasNext())
                    {
                        return;
                    }

                    v = playersIter.next();
                } while (v.getUniqueID().equals(this.mc.player.getUniqueID()));

                int v2 = (int) this.getSettings().get(0).toSlider().getValue();

                BlockPos v3 = v.getPosition();

                Iterable<BlockPos> v4 = BlockPos.getAllInBox(v3.add(-v2, -v2, -v2), v3.add(v2, v2, v2));
                Iterator<BlockPos> blockIter = v4.iterator();

                while (true)
                {
                    BlockPos v5;
                    do
                    {
                        do
                        {
                            do
                            {
                                if (!blockIter.hasNext())
                                {
                                    continue ferngayler;
                                }

                                v5 = (BlockPos) blockIter.next();
                            } while (this.mc.player.getDistanceSqToCenter(v5) > this.getSettings().get(1).toSlider().getValue());
                        } while (!this.mc.world.getBlockState(v5).getMaterial().isReplaceable());
                    } while (!this.mc.world.getBlockState(v5.add(0, 1, 0)).getMaterial().isReplaceable());

                    boolean v9 = this.mc.world.getBlockState(v5.add(0, -1, 0)).getMaterial().isSolid() && this.mc.world.getBlockState(v5.add(1, 0, 0)).getMaterial().isSolid() && this.mc.world.getBlockState(v5.add(0, 0, 1)).getMaterial().isSolid() && this.mc.world.getBlockState(v5.add(-1, 0, 0)).getMaterial().isSolid() && this.mc.world.getBlockState(v5.add(0, 0, -1)).getMaterial().isSolid() && this.mc.world.getBlockState(v5.add(0, 0, 0)).getMaterial() == Material.AIR && this.mc.world.getBlockState(v5.add(0, 1, 0)).getMaterial() == Material.AIR && this.mc.world.getBlockState(v5.add(0, 2, 0)).getMaterial() == Material.AIR;

                    if (v9 && this.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(v5)).isEmpty())
                    {
                        int v8 = this.mc.player.inventory.currentItem;
                        this.mc.player.inventory.currentItem = v7;

                        WorldUtils.placeBlock(v5, v7, this.getSettings().get(2).toToggle().state, this.getSettings().get(2).toToggle().state);
                        this.mc.player.inventory.currentItem = v8;
                    }
                }
            }
        }
    }
}
