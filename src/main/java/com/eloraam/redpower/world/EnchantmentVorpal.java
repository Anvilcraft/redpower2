package com.eloraam.redpower.world;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class EnchantmentVorpal extends EnchantmentDamage {
    public EnchantmentVorpal(int i, int j) {
        super(i, j, 0);
    }

    public int getMinEnchantability(int i) {
        return 20 + 10 * (i - 1);
    }

    public int getMaxEnchantability(int i) {
        return this.getMinEnchantability(i) + 50;
    }

    public int getMaxLevel() {
        return 4;
    }

    public void func_151368_a(EntityLivingBase attacker, Entity target, int damage) {
        if (target instanceof EntityLivingBase) {
            EntityLivingBase targetLiving = (EntityLivingBase) target;
            if (target.worldObj.rand.nextInt(100) < 2 * damage * damage) {
                targetLiving.attackEntityFrom(DamageSource.magic, 100.0F);
            }
        }
    }

    public String getName() {
        return "enchantment.damage.vorpal";
    }

    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != this;
    }
}
