package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ElytraReplace extends Module
{
    public ElytraReplace()
    {
        super("ElytraReplace", 0, Category.MISC, "Automatically replaces your elytra when its low", (List) null);
    }

    public void onUpdate()
    {
        if (this.mc.player.inventoryContainer.getSlot(6).getStack().getItem() instanceof ItemElytra && this.mc.player.inventoryContainer.getSlot(6).getStack().getMaxDamage() - this.mc.player.inventoryContainer.getSlot(6).getStack().getItemDamage() < 9)
        {
            int i = 9;

            for (byte n = 9; i <= 44; i = ++n)
            {
                ItemStack stack;
                if ((stack = this.mc.player.inventoryContainer.getSlot(n).getStack()) != ItemStack.EMPTY && stack.getItem() instanceof ItemElytra && stack.getCount() == 1 && stack.getMaxDamage() - stack.getItemDamage() > 5)
                {
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, this.mc.player);
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, n, 0, ClickType.QUICK_MOVE, this.mc.player);
                    this.mc.playerController.windowClick(this.mc.player.inventoryContainer.windowId, n, 0, ClickType.PICKUP, this.mc.player);
                }
            }
        }

    }
}
