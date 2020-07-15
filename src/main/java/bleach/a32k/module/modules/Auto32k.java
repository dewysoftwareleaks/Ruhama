package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingMode;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.WorldUtils;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Auto32k extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingMode("Mode: ", new String[] {"Auto", "Looking"}), new SettingMode("Protect: ", new String[] {"Off", "Hopper", "Obby"}), new SettingToggle(true, "Aura"), new SettingSlider(0.0D, 20.0D, 10.0D, 0, "CPS: "), new SettingMode("CPS: ", new String[] {"Clicks/Sec", "Clicks/Tick", "Tick Delay"}), new SettingToggle(false, "SafeShuker"), new SettingToggle(false, "AntiAim"), new SettingToggle(true, "2b Bypass"));
    private BlockPos placedHopperPos;
    private boolean ready;
    private boolean active;
    private boolean tickPassed;
    private int timer = 0;

    public Auto32k()
    {
        super("Auto32k", 0, Category.COMBAT, "Automatically places 32ks", settings);
    }

    public void onEnable()
    {
        this.tickPassed = false;
        int obsidian = -1;
        int shulker = -1;
        int hopper = -1;

        int cap2;
        for (cap2 = 0; cap2 < 9; ++cap2)
        {
            if (this.mc.player.inventory.getStackInSlot(cap2).getItem() == Item.getItemFromBlock(Blocks.HOPPER))
            {
                hopper = cap2;
            }

            if (this.mc.player.inventory.getStackInSlot(cap2).getItem() instanceof ItemShulkerBox)
            {
                shulker = cap2;
            }

            if (this.mc.player.inventory.getStackInSlot(cap2).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN))
            {
                obsidian = cap2;
            }
        }

        if (shulker != -1 && hopper != -1)
        {
            int y;
            int x;
            if (this.getSettings().get(0).toMode().mode == 1)
            {
                RayTraceResult ray = this.mc.player.rayTrace(4.25D, this.mc.getRenderPartialTicks());
                if (WorldUtils.isBlockEmpty(ray.getBlockPos()))
                {
                    return;
                }

                WorldUtils.placeBlock(ray.getBlockPos().up(), hopper, this.getSettings().get(7).toToggle().state, false);
                WorldUtils.placeBlock(ray.getBlockPos().up(2), shulker, this.getSettings().get(7).toToggle().state, false);
                WorldUtils.openBlock(ray.getBlockPos().up());
                this.placedHopperPos = ray.getBlockPos().up();
                this.ready = true;
            } else
            {
                label142:
                for (cap2 = -2; cap2 <= 2; ++cap2)
                {
                    for (y = -1; y <= 2; ++y)
                    {
                        for (x = -2; x <= 2; ++x)
                        {
                            if ((cap2 != 0 || y != 0 || x != 0) && (cap2 != 0 || y != 1 || x != 0) && WorldUtils.isBlockEmpty(this.mc.player.getPosition().add(cap2, y, x)) && this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add((double) cap2 + 0.5D, (double) y + 0.5D, (double) x + 0.5D)) < 4.5D && WorldUtils.isBlockEmpty(this.mc.player.getPosition().add(cap2, y + 1, x)) && this.mc.player.getPositionEyes(this.mc.getRenderPartialTicks()).distanceTo(this.mc.player.getPositionVector().add((double) cap2 + 0.5D, (double) y + 1.5D, (double) x + 0.5D)) < 4.5D)
                            {
                                boolean r = this.getSettings().get(7).toToggle().state;
                                WorldUtils.placeBlock(this.mc.player.getPosition().add(cap2, y, x), hopper, r, false);
                                WorldUtils.placeBlock(this.mc.player.getPosition().add(cap2, y + 1, x), shulker, r, false);
                                WorldUtils.openBlock(this.mc.player.getPosition().add(cap2, y, x));
                                this.placedHopperPos = this.mc.player.getPosition().add(cap2, y, x);
                                this.ready = true;
                                break label142;
                            }
                        }
                    }
                }
            }

            if (this.getSettings().get(1).toMode().mode != 2 || obsidian != -1)
            {
                if (this.getSettings().get(1).toMode().mode != 0)
                {
                    cap2 = 0;
                    this.mc.player.inventory.currentItem = this.getSettings().get(1).toMode().mode == 1 ? hopper : obsidian;

                    for (y = -1; y <= 1; ++y)
                    {
                        for (x = -1; x <= 1; ++x)
                        {
                            for (int z = -1; z <= 1; ++z)
                            {
                                if ((x != 0 || z != 0) && (x == 0 || z == 0) && WorldUtils.placeBlock(this.placedHopperPos.add(x, y, z), this.mc.player.inventory.currentItem, this.getSettings().get(7).toToggle().state, false))
                                {
                                    ++cap2;
                                    if (cap2 > (this.getSettings().get(1).toMode().mode == 1 ? 1 : 2))
                                    {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public void onUpdate()
    {
        if (!this.active && !this.ready)
        {
            this.setToggled(false);
        }

        if (this.active && this.getSettings().get(2).toToggle().state)
        {
            this.killAura();
        }

        if (this.active && this.getSettings().get(6).toToggle().state)
        {
            this.mc.player.connection.sendPacket(new Rotation(this.mc.player.rotationYaw, 90.0F, this.mc.player.onGround));
        }

        int obsidian = -1;

        for (int i = 0; i < 9; ++i)
        {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN))
            {
                obsidian = i;
            }
        }

        if (this.tickPassed && this.getSettings().get(5).toToggle().state && obsidian != -1)
        {
            WorldUtils.placeBlock(this.placedHopperPos.add(0, 2, 0), obsidian, this.getSettings().get(7).toToggle().state, false);
        }

        this.tickPassed = true;
        if (this.mc.currentScreen instanceof GuiHopper)
        {
            GuiHopper gui = (GuiHopper) this.mc.currentScreen;
            if (this.ready)
            {
                this.active = true;
                this.ready = false;
            }

            int slot;
            for (slot = 32; slot <= 40; ++slot)
            {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, gui.inventorySlots.getSlot(slot).getStack()) > 5)
                {
                    this.mc.player.inventory.currentItem = slot - 32;
                    break;
                }
            }

            if (this.active)
            {
                if (this.getSettings().get(4).toMode().mode == 0)
                {
                    this.timer = (long) this.timer >= Math.round(20.0D / this.getSettings().get(3).toSlider().getValue()) ? 0 : this.timer + 1;
                } else if (this.getSettings().get(4).toMode().mode == 1)
                {
                    this.timer = 0;
                } else if (this.getSettings().get(4).toMode().mode == 2)
                {
                    this.timer = (double) this.timer >= this.getSettings().get(3).toSlider().getValue() ? 0 : this.timer + 1;
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
        } else
        {
            this.active = false;
            this.timer = 0;
        }

    }

    public void killAura()
    {
        for (int i = 0; (double) i < (this.getSettings().get(4).toMode().mode == 1 ? this.getSettings().get(4).toSlider().getValue() : 1.0D); ++i)
        {
            Entity target = null;

            try
            {
                List<Entity> players = new ArrayList(this.mc.world.playerEntities);
                players.remove(this.mc.player);
                players.sort((a, b) ->
                {
                    return Float.compare(a.getDistance(this.mc.player), b.getDistance(this.mc.player));
                });
                if (((Entity) players.get(0)).getDistance(this.mc.player) < 8.0F)
                {
                    target = players.get(0);
                }
            } catch (Exception var4)
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
