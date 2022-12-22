package com.eloraam.redpower.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDeploy extends Container {
   private TileDeploy tileDeploy;

   public ContainerDeploy(IInventory inv, TileDeploy td) {
      this.tileDeploy = td;

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new Slot(td, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
      }

   }

   public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
      return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileDeploy.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (i < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
            return null;
         }

         if (itemstack1.stackSize == 0) {
            slot.putStack(null);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.stackSize == itemstack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(player, itemstack1);
      }

      return itemstack;
   }
}
