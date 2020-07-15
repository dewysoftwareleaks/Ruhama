package bleach.a32k.module.modules;

import bleach.a32k.Ruhama;
import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ShulkerAura extends Module
{
    public boolean inShulker = false;
    public HashMap<BlockPos, Integer> openedShulkers = new HashMap();

    public ShulkerAura()
    {
        super("ShulkerAura", 0, Category.COMBAT, "Automatically opens shulkers", (List) null);
    }

    public void onUpdate()
    {
        HashMap<BlockPos, Integer> tempShulkers = new HashMap(this.openedShulkers);

        Entry e;
        for (Iterator var2 = this.openedShulkers.entrySet().iterator(); var2.hasNext(); tempShulkers.replace(e.getKey(), (Integer) e.getValue() - 1))
        {
            e = (Entry) var2.next();
            if ((Integer) e.getValue() <= 0)
            {
                tempShulkers.remove(e.getKey());
            }
        }

        this.openedShulkers.clear();
        this.openedShulkers.putAll(tempShulkers);
        if (!(this.mc.currentScreen instanceof GuiContainer) || this.mc.currentScreen instanceof GuiShulkerBox)
        {
            if (this.mc.currentScreen instanceof GuiShulkerBox)
            {
                if (this.inShulker)
                {
                    this.mc.displayGuiScreen((GuiScreen) null);
                }

                this.inShulker = false;
            } else
            {
                for (int x = -4; x <= 4; ++x)
                {
                    for (int y = -4; y <= 4; ++y)
                    {
                        for (int z = -4; z <= 4; ++z)
                        {
                            BlockPos pos = this.mc.player.getPosition().add(x, y, z);
                            if (this.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox && !Ruhama.friendBlocks.containsKey(pos) && !this.openedShulkers.containsKey(pos) && this.mc.player.getPositionVector().distanceTo((new Vec3d(pos)).add(0.5D, 0.5D, 0.5D)) <= 5.25D)
                            {
                                WorldUtils.openBlock(pos);
                                this.openedShulkers.put(pos, 300);
                                this.inShulker = true;
                                return;
                            }
                        }
                    }
                }

            }
        }
    }
}
