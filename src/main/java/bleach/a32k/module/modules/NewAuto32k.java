package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.RuhamaLogger;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NewAuto32k extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(true, "2b Bypass"), new SettingToggle(true, "Killaura"), new SettingSlider(0.0D, 20.0D, 20.0D, 0, "CPS: "), new SettingMode("CPS: ", new String[] {"Clicks/Sec", "Clicks/Tick", "Tick Delay"}), new SettingToggle(false, "Timeout"), new SettingMode("Place: ", new String[] {"Auto", "Looking"}));
    private BlockPos pos;
    private int hopper;
    private int dispenser;
    private int redstone;
    private int shulker;
    private int block;
    private int[] rot;
    private boolean active;
    private boolean openedDispenser;
    private int dispenserTicks;
    private int ticksPassed;
    private int timer = 0;

    public NewAuto32k()
    {
        super("NewAuto32k", 0, Category.COMBAT, "Dispenser Auto32k", settings);
    }

    public void onEnable()
    {
        this.ticksPassed = 0;
        this.hopper = -1;
        this.dispenser = -1;
        this.redstone = -1;
        this.shulker = -1;
        this.block = -1;
        this.active = false;
        this.openedDispenser = false;
        this.dispenserTicks = 0;
        this.timer = 0;

        int x;
        for (x = 0; x <= 8; ++x)
        {
            Item item = this.mc.player.inventory.getStackInSlot(x).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER))
            {
                this.hopper = x;
            } else if (item == Item.getItemFromBlock(Blocks.DISPENSER))
            {
                this.dispenser = x;
            } else if (item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
            {
                this.redstone = x;
            } else if (item instanceof ItemShulkerBox)
            {
                this.shulker = x;
            } else if (item instanceof ItemBlock)
            {
                this.block = x;
            }
        }

        if (this.hopper == -1)
        {
            RuhamaLogger.log("Missing Hopper");
        } else if (this.dispenser == -1)
        {
            RuhamaLogger.log("Missing Dispenser");
        } else if (this.redstone == -1)
        {
            RuhamaLogger.log("Missing Redstone Block");
        } else if (this.shulker == -1)
        {
            RuhamaLogger.log("Missing Shulker");
        } else if (this.block == -1)
        {
            RuhamaLogger.log("Missing Generic Block");
        }

        if (this.hopper != -1 && this.dispenser != -1 && this.redstone != -1 && this.shulker != -1 && this.block != -1)
        {
            if (this.getSettings().get(5).toMode().mode == 1)
            {
                RayTraceResult ray = this.mc.player.rayTrace(5.0D, this.mc.getRenderPartialTicks());
                this.pos = ray.getBlockPos().up();
                double x = (double) this.pos.getX() - this.mc.player.posX;
                double z = (double) this.pos.getZ() - this.mc.player.posZ;
                this.rot = Math.abs(x) > Math.abs(z) ? (x > 0.0D ? new int[] {-1, 0} : new int[] {1, 0}) : (z > 0.0D ? new int[] {0, -1} : new int[] {0, 1});
                if (WorldUtils.canPlaceBlock(this.pos) && WorldUtils.isBlockEmpty(this.pos) && WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 0, this.rot[1])) && WorldUtils.isBlockEmpty(this.pos.add(0, 1, 0)) && WorldUtils.isBlockEmpty(this.pos.add(0, 2, 0)) && WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 1, this.rot[1])))
                {
                    boolean rotate = this.getSettings().get(0).toToggle().state;
                    WorldUtils.placeBlock(this.pos, this.block, rotate, false);
                    WorldUtils.rotatePacket((double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getX() + 0.5D, (double) (this.pos.getY() + 1), (double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getZ() + 0.5D);
                    WorldUtils.placeBlock(this.pos.add(0, 1, 0), this.dispenser, false, false);
                } else
                {
                    RuhamaLogger.log("Unable to place 32k");
                    this.setToggled(false);
                }
            } else
            {
                for (x = -2; x <= 2; ++x)
                {
                    for (int y = -1; y <= 1; ++y)
                    {
                        for (int z = -2; z <= 2; ++z)
                        {
                            this.rot = Math.abs(x) > Math.abs(z) ? (x > 0 ? new int[] {-1, 0} : new int[] {1, 0}) : (z > 0 ? new int[] {0, -1} : new int[] {0, 1});
                            this.pos = this.mc.player.getPosition().add(x, y, z);
                            if (this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add((double) (x - this.rot[0] / 2), (double) y + 0.5D, (double) (z + this.rot[1] / 2))) <= 4.5D && this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add((double) x + 0.5D, (double) y + 2.5D, (double) z + 0.5D)) <= 4.5D && WorldUtils.canPlaceBlock(this.pos) && WorldUtils.isBlockEmpty(this.pos) && WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 0, this.rot[1])) && WorldUtils.isBlockEmpty(this.pos.add(0, 1, 0)) && WorldUtils.isBlockEmpty(this.pos.add(0, 2, 0)) && WorldUtils.isBlockEmpty(this.pos.add(this.rot[0], 1, this.rot[1])))
                            {
                                boolean rotate = this.getSettings().get(0).toToggle().state;
                                WorldUtils.placeBlock(this.pos, this.block, rotate, false);
                                WorldUtils.rotatePacket((double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getX() + 0.5D, (double) (this.pos.getY() + 1), (double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getZ() + 0.5D);
                                WorldUtils.placeBlock(this.pos.add(0, 1, 0), this.dispenser, false, false);
                                return;
                            }
                        }
                    }
                }

                RuhamaLogger.log("Unable to place 32k");
                this.setToggled(false);
            }
        } else
        {
            this.setToggled(false);
        }
    }

    public void onUpdate()
    {
        if ((!this.getSettings().get(4).toToggle().state || this.active || this.ticksPassed <= 25) && (!this.active || this.mc.currentScreen instanceof GuiHopper))
        {
            if (this.active && this.getSettings().get(1).toToggle().state && this.timer == 0)
            {
                this.killAura();
            }

            if (this.mc.currentScreen instanceof GuiDispenser)
            {
                this.openedDispenser = true;
            }

            if (this.mc.currentScreen instanceof GuiHopper)
            {
                GuiHopper gui = (GuiHopper) this.mc.currentScreen;

                int slot;
                for (slot = 32; slot <= 40; ++slot)
                {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, gui.inventorySlots.getSlot(slot).getStack()) > 5)
                    {
                        this.mc.player.inventory.currentItem = slot - 32;
                        break;
                    }
                }

                this.active = true;
                if (this.active)
                {
                    if (this.getSettings().get(3).toMode().mode == 0)
                    {
                        this.timer = (long) this.timer >= Math.round(20.0D / this.getSettings().get(2).toSlider().getValue()) ? 0 : this.timer + 1;
                    } else if (this.getSettings().get(3).toMode().mode == 1)
                    {
                        this.timer = 0;
                    } else if (this.getSettings().get(3).toMode().mode == 2)
                    {
                        this.timer = (double) this.timer >= this.getSettings().get(2).toSlider().getValue() ? 0 : this.timer + 1;
                    }
                }

                if (!(((Slot) gui.inventorySlots.inventorySlots.get(0)).getStack().getItem() instanceof ItemAir) && this.active)
                {
                    slot = this.mc.player.inventory.currentItem;
                    boolean pull = false;

                    for (int i = 40; i >= 32; --i)
                    {
                        if (gui.inventorySlots.getSlot(i).getStack().isEmpty())
                        {
                            slot = i;
                            pull = true;
                            break;
                        }
                    }

                    if (pull)
                    {
                        this.mc.playerController.windowClick(gui.inventorySlots.windowId, 0, 0, ClickType.PICKUP, this.mc.player);
                        this.mc.playerController.windowClick(gui.inventorySlots.windowId, slot, 0, ClickType.PICKUP, this.mc.player);
                    }
                }
            }

            if (this.ticksPassed == 0)
            {
                WorldUtils.openBlock(this.pos.add(0, 1, 0));
            }

            if (this.openedDispenser && this.dispenserTicks == 0)
            {
                this.mc.playerController.windowClick(this.mc.player.openContainer.windowId, 36 + this.shulker, 0, ClickType.QUICK_MOVE, this.mc.player);
            }

            if (this.dispenserTicks == 1)
            {
                this.mc.displayGuiScreen((GuiScreen) null);
                WorldUtils.placeBlock(this.pos.add(0, 2, 0), this.redstone, this.getSettings().get(0).toToggle().state, false);
            }

            if (this.mc.world.getBlockState(this.pos.add(this.rot[0], 1, this.rot[1])).getBlock() instanceof BlockShulkerBox && this.mc.world.getBlockState(this.pos.add(this.rot[0], 0, this.rot[1])).getBlock() != Blocks.HOPPER)
            {
                WorldUtils.placeBlock(this.pos.add(this.rot[0], 0, this.rot[1]), this.hopper, this.getSettings().get(0).toToggle().state, false);
                WorldUtils.openBlock(this.pos.add(this.rot[0], 0, this.rot[1]));
            }

            if (this.openedDispenser)
            {
                ++this.dispenserTicks;
            }

            ++this.ticksPassed;
        } else
        {
            this.setToggled(false);
        }
    }

    public void killAura()
    {
        for (int i = 0; (double) i < (this.getSettings().get(3).toMode().mode == 1 ? this.getSettings().get(2).toSlider().getValue() : 1.0D); ++i)
        {
            Entity target = null;

            try
            {
                List<Entity> players = new ArrayList(this.mc.world.loadedEntityList);
                Iterator var4 = (new ArrayList(players)).iterator();

                while (var4.hasNext())
                {
                    Entity e = (Entity) var4.next();
                    if (!(e instanceof EntityLivingBase))
                    {
                        players.remove(e);
                    }
                }

                players.remove(this.mc.player);
                players.sort((a, b) ->
                {
                    return Float.compare(a.getDistance(this.mc.player), b.getDistance(this.mc.player));
                });
                if (((Entity) players.get(0)).getDistance(this.mc.player) < 8.0F)
                {
                    target = players.get(0);
                }
            } catch (Exception var6)
            {
            }

            if (target == null)
            {
                return;
            }

            WorldUtils.rotateClient(target.posX, target.posY + 1.0D, target.posZ);
            if (target.getDistance(this.mc.player) > 6.0F)
            {
                return;
            }

            this.mc.playerController.attackEntity(this.mc.player, target);
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
        }

    }
}
