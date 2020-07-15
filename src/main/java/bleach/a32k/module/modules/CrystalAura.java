package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import bleach.a32k.utils.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.util.*;
import java.util.stream.Collectors;

public class CrystalAura extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(true, "AutoSwitch"), new SettingToggle(true, "Players"), new SettingToggle(false, "Mobs"), new SettingToggle(false, "Animals"), new SettingToggle(true, "Place"), new SettingToggle(false, "AlwaysPlace"), new SettingSlider(0.0D, 6.0D, 4.5D, 2, "PlaceRange: "), new SettingSlider(0.0D, 6.0D, 4.5D, 2, "HitRange: "), new SettingSlider(0.0D, 1000.0D, 50.0D, 2, "PlaceDelay: "));
    private BlockPos render;
    private Entity renderEnt;
    private long systemTime = -1L;
    private boolean togglePitch = false;
    private boolean isSpoofingAngles;
    private long placeDelay = 0L;

    public CrystalAura()
    {
        super("CrystalAura", 0, Category.COMBAT, "Attacks Crystals", settings);
    }

    public void onUpdate()
    {
        EntityEnderCrystal crystal = this.mc.world.loadedEntityList.stream().filter((entityx) ->
        {
            return entityx instanceof EntityEnderCrystal;
        }).map((entityx) ->
        {
            return (EntityEnderCrystal) entityx;
        }).min(Comparator.comparing((c) ->
        {
            return this.mc.player.getDistance(c);
        })).orElse(null);
        if (crystal != null && (double) this.mc.player.getDistance(crystal) <= this.getSettings().get(7).toSlider().getValue())
        {
            if (System.nanoTime() / 1000000L - this.systemTime >= 250L)
            {
                this.mc.playerController.attackEntity(this.mc.player, crystal);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.systemTime = System.nanoTime() / 1000000L;
            }

        } else
        {
            this.resetRotation();
            int crystalSlot = this.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? this.mc.player.inventory.currentItem : -1;
            if (crystalSlot == -1)
            {
                for (int l = 0; l < 9; ++l)
                {
                    if (this.mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL)
                    {
                        crystalSlot = l;
                        break;
                    }
                }
            }

            boolean offhand = false;
            if (this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
            {
                offhand = true;
            } else if (crystalSlot == -1)
            {
                return;
            }

            List<BlockPos> blocks = this.findCrystalBlocks();
            List<Entity> entities = this.mc.world.loadedEntityList.stream().filter((e) ->
            {
                return e instanceof EntityPlayer && this.getSettings().get(1).toToggle().state || e.isCreatureType(EnumCreatureType.MONSTER, false) && this.getSettings().get(2).toToggle().state || this.isPassive(e) && this.getSettings().get(3).toToggle().state;
            }).collect(Collectors.toList());
            BlockPos q = this.mc.player.getPosition().add(0, 2, 0);
            double damage = 0.5D;
            Iterator var9 = entities.iterator();

            label130:
            while (true)
            {
                Entity entity;
                do
                {
                    do
                    {
                        if (!var9.hasNext())
                        {
                            if (damage == 0.5D)
                            {
                                this.render = null;
                                this.renderEnt = null;
                                this.resetRotation();
                                return;
                            }

                            this.render = q;
                            if (this.getSettings().get(4).toToggle().state && (this.getSettings().get(0).toToggle().state || this.mc.player.inventory.getCurrentItem().getItem() == Items.END_CRYSTAL) && (double) this.placeDelay + this.getSettings().get(8).toSlider().getValue() < (double) System.currentTimeMillis())
                            {
                                this.placeDelay = System.currentTimeMillis();
                                if (!offhand && this.mc.player.inventory.currentItem != crystalSlot)
                                {
                                    if (this.getSettings().get(0).toToggle().state)
                                    {
                                        this.mc.player.inventory.currentItem = crystalSlot;
                                        this.resetRotation();
                                    }

                                    return;
                                }

                                this.lookAtPacket((double) q.getX() + 0.5D, (double) q.getY() - 0.5D, (double) q.getZ() + 0.5D, this.mc.player);
                                RayTraceResult result = this.mc.world.rayTraceBlocks(new Vec3d(this.mc.player.posX, this.mc.player.posY + (double) this.mc.player.getEyeHeight(), this.mc.player.posZ), new Vec3d((double) q.getX() + 0.5D, (double) q.getY() - 0.5D, (double) q.getZ() + 0.5D));
                                EnumFacing f;
                                if (result != null && result.sideHit != null)
                                {
                                    f = result.sideHit;
                                } else
                                {
                                    f = EnumFacing.UP;
                                }

                                this.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                            }

                            if (this.isSpoofingAngles)
                            {
                                EntityPlayerSP var10000;
                                if (this.togglePitch)
                                {
                                    var10000 = this.mc.player;
                                    var10000.rotationPitch = (float) ((double) var10000.rotationPitch + 4.0E-4D);
                                    this.togglePitch = false;
                                } else
                                {
                                    var10000 = this.mc.player;
                                    var10000.rotationPitch = (float) ((double) var10000.rotationPitch - 4.0E-4D);
                                    this.togglePitch = true;
                                }
                            }

                            return;
                        }

                        entity = (Entity) var9.next();
                    } while (entity == this.mc.player);
                } while (((EntityLivingBase) entity).getHealth() <= 0.0F);

                Iterator var11 = blocks.iterator();

                while (true)
                {
                    BlockPos blockPos;
                    double d;
                    double self;
                    do
                    {
                        do
                        {
                            do
                            {
                                double b;
                                do
                                {
                                    if (!var11.hasNext())
                                    {
                                        continue label130;
                                    }

                                    blockPos = (BlockPos) var11.next();
                                    b = entity.getDistanceSq(blockPos);
                                } while (b >= 169.0D);

                                d = this.calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, entity);
                            } while (d <= damage);

                            self = this.calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, this.mc.player);
                        } while (self > d && d >= (double) ((EntityLivingBase) entity).getHealth() && !this.getSettings().get(5).toToggle().state);
                    } while (self - 0.5D > (double) this.mc.player.getHealth() && !this.getSettings().get(5).toToggle().state);

                    damage = d;
                    q = blockPos;
                    this.renderEnt = entity;
                }
            }
        }
    }

    public void onRender()
    {
        if (this.render != null)
        {
            RenderUtils.drawFilledBlockBox(new AxisAlignedBB(this.render), 1.0F, 1.0F, 1.0F, 0.3F);
            if (this.renderEnt != null)
            {
            }
        }

    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me)
    {
        double[] v = this.calculateLookAt(px, py, pz, me);
        this.setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos)
    {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (this.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || this.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && this.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && this.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && this.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
    }

    public BlockPos getPlayerPos()
    {
        return new BlockPos(Math.floor(this.mc.player.posX), Math.floor(this.mc.player.posY), Math.floor(this.mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks()
    {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(this.getSphere(this.getPlayerPos(), (float) this.getSettings().get(6).toSlider().getValue(), (int) this.getSettings().get(6).toSlider().getValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y)
    {
        List<BlockPos> circleblocks = new ArrayList();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x)
        {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z)
            {
                for (int y = sphere ? cy - (int) r : cy; (float) y < (sphere ? (float) cy + r : (float) (cy + h)); ++y)
                {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F))))
                    {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity)
    {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase)
        {
            finald = this.getBlastReduction((EntityLivingBase) entity, this.getDamageMultiplied(damage), new Explosion(this.mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finald;
    }

    public float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp((float) k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(Potion.getPotionById(11)))
            {
                damage -= damage / 4.0F;
            }

            damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        } else
        {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private float getDamageMultiplied(float damage)
    {
        int diff = this.mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
    }

    public float calculateDamage(EntityEnderCrystal crystal, Entity entity)
    {
        return this.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    private void setYawAndPitch(float yaw1, float pitch1)
    {
        this.isSpoofingAngles = true;
    }

    private void resetRotation()
    {
        if (this.isSpoofingAngles)
        {
            this.isSpoofingAngles = false;
        }

    }

    public void onDisable()
    {
        this.render = null;
        this.renderEnt = null;
        this.resetRotation();
    }

    public boolean isPassive(Entity e)
    {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry())
        {
            return false;
        } else if (!(e instanceof EntityAnimal) && !(e instanceof EntityAgeable) && !(e instanceof EntityTameable) && !(e instanceof EntityAmbientCreature) && !(e instanceof EntitySquid))
        {
            return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
        } else
        {
            return true;
        }
    }

    public Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z)
    {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public double[] calculateLookAt(double px, double py, double pz, EntityPlayer me)
    {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0D / 3.141592653589793D;
        yaw = yaw * 180.0D / 3.141592653589793D;
        yaw += 90.0D;
        return new double[] {yaw, pitch};
    }

    public Vec3d getInterpolatedPos(Entity entity, float ticks)
    {
        return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(this.getInterpolatedAmount(entity, ticks, ticks, ticks));
    }

    public Vec3d getInterpolatedRenderPos(Entity entity, float ticks)
    {
        double[] rPos = RenderUtils.rPos();
        return this.getInterpolatedPos(entity, ticks).subtract(rPos[0], rPos[1], rPos[2]);
    }
}
