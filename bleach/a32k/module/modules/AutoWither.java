package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class AutoWither extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(false, "Rename"), new SettingToggle(true, "2b Bypass"));
    private int tick = 0;
    private BlockPos pos;
    private int rotation;
    private boolean nameTagTime;

    public AutoWither()
    {
        super("AutoWither", 0, Category.MISC, "Automatically creates a wither", settings);
    }

    public void onEnable()
    {
        this.tick = 0;
        this.nameTagTime = false;
        BlockPos player = this.mc.player.getPosition();

        for (int x = -2; x <= 2; ++x)
        {
            for (int y = -2; y <= 1; ++y)
            {
                for (int z = -2; z <= 2; ++z)
                {
                    for (int r = 0; r <= 1; ++r)
                    {
                        BlockPos newPos = player.add(x, y, z);
                        if (!this.witherBoxIntersects(newPos, r) && this.isAreaEmpty(newPos, r) && WorldUtils.canPlaceBlock(newPos))
                        {
                            this.pos = newPos;
                            this.rotation = r;
                            return;
                        }
                    }
                }
            }
        }

        this.setToggled(false);
    }

    public void onUpdate()
    {
        if (this.tick > 7)
        {
            this.setToggled(false);
        }

        ++this.tick;
        int sand = -1;
        int skull = -1;
        int tag = -1;

        for (int i = 0; i < 9; ++i)
        {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND))
            {
                sand = i;
            } else if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.SKULL)
            {
                skull = i;
            } else if (this.mc.player.inventory.getStackInSlot(i).getItem() == Items.NAME_TAG)
            {
                tag = i;
            }
        }

        if (this.nameTagTime && tag != -1)
        {
            Iterator var9 = this.mc.world.loadedEntityList.iterator();

            Entity e;
            do
            {
                if (!var9.hasNext())
                {
                    return;
                }

                e = (Entity) var9.next();
            } while (!(e instanceof EntityWither) || e.getName().equals(this.mc.player.inventory.getStackInSlot(tag).getDisplayName()) || (double) this.mc.player.getDistance(e) > 5.5D);

            this.mc.player.inventory.currentItem = tag;
            this.mc.playerController.interactWithEntity(this.mc.player, e, EnumHand.MAIN_HAND);
            this.setToggled(false);
        } else if (skull != -1 && sand != -1)
        {
            LinkedHashMap<BlockPos, Integer> blocks = new LinkedHashMap();
            blocks.put(this.pos, sand);
            blocks.put(this.pos.add(0, 1, 0), sand);
            blocks.put(this.pos.add(0, 2, 0), skull);
            if (this.rotation == 0)
            {
                blocks.put(this.pos.add(-1, 1, 0), sand);
                blocks.put(this.pos.add(1, 1, 0), sand);
                blocks.put(this.pos.add(-1, 2, 0), skull);
                blocks.put(this.pos.add(1, 2, 0), skull);
            } else
            {
                blocks.put(this.pos.add(0, 1, -1), sand);
                blocks.put(this.pos.add(0, 1, 1), sand);
                blocks.put(this.pos.add(0, 2, -1), skull);
                blocks.put(this.pos.add(0, 2, 1), skull);
            }

            int cap = 0;
            Iterator var6 = blocks.entrySet().iterator();

            while (var6.hasNext())
            {
                Entry<BlockPos, Integer> e = (Entry) var6.next();
                if (cap >= 2)
                {
                    return;
                }

                if (WorldUtils.placeBlock((BlockPos) e.getKey(), (Integer) e.getValue(), this.getSettings().get(1).toToggle().state, e.equals(blocks.entrySet().toArray()[blocks.size() - 1]) && this.getSettings().get(1).toToggle().state))
                {
                    ++cap;
                }
            }

            if (this.getSettings().get(0).toToggle().state)
            {
                this.nameTagTime = true;
            } else
            {
                this.setToggled(false);
            }

        } else
        {
            this.setToggled(false);
        }
    }

    public boolean isAreaEmpty(BlockPos p, int rot)
    {
        for (int x = -1; x <= 1; ++x)
        {
            for (int y = 0; y <= 2; ++y)
            {
                if (rot == 0 && this.mc.world.getBlockState(p.add(x, y, 0)).getBlock() != Blocks.AIR)
                {
                    return false;
                }

                if (this.mc.world.getBlockState(p.add(0, y, x)).getBlock() != Blocks.AIR)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean witherBoxIntersects(BlockPos p, int rot)
    {
        Vec3d vec = new Vec3d(p);
        AxisAlignedBB box = rot == 0 ? new AxisAlignedBB(vec.add(-1.0D, 0.0D, 0.0D), vec.add(2.0D, 3.0D, 1.0D)) : new AxisAlignedBB(vec.add(0.0D, 0.0D, -1.0D), vec.add(1.0D, 3.0D, 2.0D));
        Iterator var5 = this.mc.world.loadedEntityList.iterator();

        Entity e;
        do
        {
            if (!var5.hasNext())
            {
                return false;
            }

            e = (Entity) var5.next();
        } while (!(e instanceof EntityLivingBase) || !box.intersects(e.getEntityBoundingBox()));

        return true;
    }
}
