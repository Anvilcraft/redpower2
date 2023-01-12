package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentDisjunction extends Enchantment {
    public EnchantmentDisjunction(int i, int j) {
        super(i, j, EnumEnchantmentType.weapon);
    }

    public int getMinEnchantability(int i) {
        return 5 + 8 * i;
    }

    public int getMaxEnchantability(int i) {
        return this.getMinEnchantability(i) + 20;
    }

    public int getMaxLevel() {
        return 5;
    }

    public String getName() {
        return "enchantment.damage.disjunction";
    }

    public boolean canApply(ItemStack ist) {
        return ist.getItem() == RedPowerWorld.itemAthame;
    }

    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment == this ? false : !(enchantment instanceof EnchantmentDamage);
    }
}
