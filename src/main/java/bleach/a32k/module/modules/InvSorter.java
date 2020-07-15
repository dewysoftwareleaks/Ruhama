package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.utils.FileMang;
import bleach.a32k.utils.RuhamaLogger;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

import java.util.Iterator;
import java.util.List;

public class InvSorter extends Module
{
    private List<String> items;

    public InvSorter()
    {
        super("InvSorter", 0, Category.MISC, "Sorts your hotbar, use /invsorter command to save", null);
    }

    public void onEnable()
    {
        FileMang.createFile("invsorter.txt");
        this.items = FileMang.readFileLines("invsorter.txt");
        if (this.items.size() < 9)
        {
            RuhamaLogger.log("No Inventory Saved, Use /invsorter to save your hotbar");
            this.setToggled(false);
        }
    }

    public void onUpdate()
    {
        int index = -1;
        int done = 0;
        Iterator var3 = this.items.iterator();

        while (true)
        {
            String s;
            do
            {
                do
                {
                    if (!var3.hasNext())
                    {
                        if (done == 0)
                        {
                            this.setToggled(false);
                        }

                        return;
                    }

                    s = (String) var3.next();
                    ++index;
                } while (s == "");
            } while (s.equals(this.mc.player.inventory.getStackInSlot(index).getItem().getRegistryName().toString()));

            for (int i = 9; i <= 45; ++i)
            {
                if (this.mc.player.inventory.getStackInSlot(i).getItem().getRegistryName().toString().equals(s))
                {
                    if (s.equals(Items.AIR.getRegistryName().toString()))
                    {
                        this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 36 + index, 0, ClickType.QUICK_MOVE, this.mc.player);
                    } else if (this.mc.player.inventory.getStackInSlot(index).getItem() == Items.AIR)
                    {
                        this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                        this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 36 + index, 0, ClickType.PICKUP, this.mc.player);
                    } else
                    {
                        this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                        this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 36 + index, 0, ClickType.PICKUP, this.mc.player);
                        this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, this.mc.player);
                    }

                    int var6 = done + 1;
                    return;
                }
            }
        }
    }
}
