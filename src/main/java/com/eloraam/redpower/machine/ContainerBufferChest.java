package com.eloraam.redpower.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBufferChest extends Container {
   private TileBufferChest tileBuffer;

   public ContainerBufferChest(IInventory inv, TileBufferChest td) {
      this.tileBuffer = td;

      for(int i = 0; i < 5; ++i) {
         for(int j = 0; j < 4; ++j) {
            this.addSlotToContainer(new Slot(td, j + i * 4, 44 + i * 18, 18 + j * 18));
         }
      }

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
         }
      }

      for(int i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 162));
      }

   }

   public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
      return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileBuffer.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (i < 20) {
            if (!this.mergeItemStack(itemstack1, 20, 56, true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 20, false)) {
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
