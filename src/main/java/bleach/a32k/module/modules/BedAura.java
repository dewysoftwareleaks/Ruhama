package bleach.a32k.module.modules;

import bleach.a32k.module.Category;
import bleach.a32k.module.Module;
import bleach.a32k.settings.SettingBase;
import bleach.a32k.settings.SettingSlider;
import bleach.a32k.settings.SettingToggle;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import java.util.Arrays;
import java.util.List;

public class BedAura extends Module
{
    private static final List<SettingBase> settings = Arrays.asList(new SettingToggle(true, "AutoSwitch"), new SettingToggle(true, "Players"), new SettingToggle(false, "Mobs"), new SettingToggle(false, "Animals"), new SettingToggle(true, "Place"), new SettingToggle(true, "Explode"), new SettingToggle(false, "Chat Alert"), new SettingToggle(false, "Anti Weakness"), new SettingToggle(false, "Slow"), new SettingToggle(false, "Rotate"), new SettingToggle(false, "RayTrace"), new SettingSlider(0.0D, 6.0D, 4.25D, 2, "Range: "));

    public BedAura()
    {
        super("BedAura", 0, Category.COMBAT, "enderperl guy want bedaur ok", settings);
    }

    public void onUpdate()
    {
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity)
    {
        double doubleExplosionSize = 12.0D;
        double distancedsize = entity.getDistance(posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 9.0D * doubleExplosionSize + 1.0D));
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
}
