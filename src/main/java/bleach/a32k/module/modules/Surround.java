package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Surround extends Module
{
    public Surround()
    {
        super("Surround", 0, Category.COMBAT, "Build obsidian around you to protect you from crystals", Arrays.asList(new SettingMode("Mode: ", new String[] {"1x1", "2x2", "Smart"}), new SettingToggle(true, "Switch Back"), new SettingToggle(false, "2 High"), new SettingToggle(true, "2b Bypass")));
    }

    public void onUpdate()
    {
        int obsidian = -1;

        int cap;
        for (cap = 0; cap < 9; ++cap)
        {
            if (this.mc.player.inventory.getStackInSlot(cap).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN))
            {
                obsidian = cap;
                break;
            }
        }

        cap = 0;
        List<BlockPos> poses = new ArrayList();
        boolean rotate = this.getSettings().get(3).toToggle().state;
        if (this.getSettings().get(0).toMode().mode == 0)
        {
            poses.addAll(Arrays.asList((new BlockPos(this.mc.player.getPositionVector())).add(0, 0, 1), (new BlockPos(this.mc.player.getPositionVector())).add(1, 0, 0), (new BlockPos(this.mc.player.getPositionVector())).add(0, 0, -1), (new BlockPos(this.mc.player.getPositionVector())).add(-1, 0, 0)));
        } else if (this.getSettings().get(0).toMode().mode == 1)
        {
            poses.addAll(Arrays.asList((new BlockPos(this.mc.player.getPositionVector())).add(0, 0, 2), (new BlockPos(this.mc.player.getPositionVector())).add(2, 0, 0), (new BlockPos(this.mc.player.getPositionVector())).add(0, 0, -2), (new BlockPos(this.mc.player.getPositionVector())).add(-2, 0, 0)));
        } else if (this.getSettings().get(0).toMode().mode == 2)
        {
            poses.addAll(Arrays.asList((new BlockPos(this.mc.player.getPositionVector().add(0.0D, 0.0D, (double) (-this.mc.player.width)))).add(0, 0, -1), (new BlockPos(this.mc.player.getPositionVector().add((double) (-this.mc.player.width), 0.0D, 0.0D))).add(-1, 0, 0), (new BlockPos(this.mc.player.getPositionVector().add(0.0D, 0.0D, (double) this.mc.player.width))).add(0, 0, 1), (new BlockPos(this.mc.player.getPositionVector().add((double) this.mc.player.width, 0.0D, 0.0D))).add(1, 0, 0)));
        }

        Iterator var5 = (new ArrayList(poses)).iterator();

        while (var5.hasNext())
        {
            BlockPos b = (BlockPos) var5.next();
            poses.add(0, b.down());
            if (this.getSettings().get(2).toToggle().state)
            {
                poses.add(0, b.up());
            }
        }

        if (obsidian != -1)
        {
            int hand = this.mc.player.inventory.currentItem;
            Iterator var9 = poses.iterator();

            while (var9.hasNext())
            {
                BlockPos b = (BlockPos) var9.next();
                if (WorldUtils.placeBlock(b, obsidian, rotate, false))
                {
                    ++cap;
                }

                if (cap > 2)
                {
                    break;
                }
            }

            if (this.getSettings().get(1).toToggle().state)
            {
                this.mc.player.inventory.currentItem = hand;
            }
        }

    }
}
