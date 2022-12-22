package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;

public class ItemAthame extends ItemSword {
   public ItemAthame() {
      super(ToolMaterial.EMERALD);
      this.setMaxDamage(100);
      this.setTextureName("rpworld:athame");
      this.setCreativeTab(CreativeTabs.tabCombat);
   }

   public float func_150893_a(ItemStack stack, Block block) {
      return 1.0F;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase hunter) {
      stack.damageItem(1, hunter);
      if (victim instanceof EntityEnderman || victim instanceof EntityDragon) {
         victim.attackEntityFrom(DamageSource.causeMobDamage(hunter), 25.0F);
      }

      return true;
   }

   public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
      return ist2.isItemEqual(RedPowerBase.itemIngotSilver);
   }

   public int getItemEnchantability() {
      return 30;
   }

   public Multimap getAttributeModifiers(ItemStack stack) {
      return HashMultimap.create();
   }
}
