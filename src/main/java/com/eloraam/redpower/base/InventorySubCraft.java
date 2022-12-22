package com.eloraam.redpower.base;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventorySubCraft extends InventoryCrafting {
   private Container eventHandler;
   private IInventory parent;

   public InventorySubCraft(Container container, IInventory par) {
      super(container, 3, 3);
      this.parent = par;
      this.eventHandler = container;
   }

   public int getSizeInventory() {
      return 9;
   }

   public ItemStack getStackInSlot(int i) {
      return i >= 9 ? null : this.parent.getStackInSlot(i);
   }

   public ItemStack getStackInRowAndColumn(int i, int j) {
      if (i >= 0 && i < 3) {
         int k = i + j * 3;
         return this.getStackInSlot(k);
      } else {
         return null;
      }
   }

   public ItemStack decrStackSize(int i, int j) {
      ItemStack tr = this.parent.decrStackSize(i, j);
      if (tr != null) {
         this.eventHandler.onCraftMatrixChanged(this);
      }

      return tr;
   }

   public void setInventorySlotContents(int i, ItemStack ist) {
      this.parent.setInventorySlotContents(i, ist);
      this.eventHandler.onCraftMatrixChanged(this);
   }
}
