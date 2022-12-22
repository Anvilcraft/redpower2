package com.eloraam.redpower.core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPartialCraft extends Item {
   private ItemStack emptyItem = null;

   public ItemPartialCraft() {
      this.setMaxStackSize(1);
      this.setNoRepair();
   }

   public void setEmptyItem(ItemStack ei) {
      this.emptyItem = ei;
   }

   public ItemStack getContainerItem(ItemStack ist) {
      int dmg = ist.getItemDamage();
      if (dmg == ist.getMaxDamage() && this.emptyItem != null) {
         return CoreLib.copyStack(this.emptyItem, 1);
      } else {
         ItemStack tr = CoreLib.copyStack(ist, 1);
         tr.setItemDamage(dmg + 1);
         return tr;
      }
   }

   public boolean hasContainerItem(ItemStack stack) {
      return true;
   }

   public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist) {
      return false;
   }
}
