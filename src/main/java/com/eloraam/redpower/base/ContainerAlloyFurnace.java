package com.eloraam.redpower.base;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAlloyFurnace extends Container {
   private TileAlloyFurnace tileFurnace;
   public int totalburn = 0;
   public int burntime = 0;
   public int cooktime = 0;

   public ContainerAlloyFurnace(InventoryPlayer inv, TileAlloyFurnace td) {
      this.tileFurnace = td;

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new Slot(td, j + i * 3, 48 + j * 18, 17 + i * 18));
         }
      }

      this.addSlotToContainer(new Slot(td, 9, 17, 42));
      this.addSlotToContainer(new SlotAlloyFurnace(inv.player, td, 10, 141, 35));

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
      return player.worldObj.isRemote || this.tileFurnace.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (i < 11) {
            if (!this.mergeItemStack(itemstack1, 11, 47, true)) {
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

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(ICrafting ic : (List<ICrafting> )super.crafters) {
         if (this.totalburn != this.tileFurnace.totalburn) {
            ic.sendProgressBarUpdate(this, 0, this.tileFurnace.totalburn);
         }

         if (this.burntime != this.tileFurnace.burntime) {
            ic.sendProgressBarUpdate(this, 1, this.tileFurnace.burntime);
         }

         if (this.cooktime != this.tileFurnace.cooktime) {
            ic.sendProgressBarUpdate(this, 2, this.tileFurnace.cooktime);
         }
      }

      this.totalburn = this.tileFurnace.totalburn;
      this.cooktime = this.tileFurnace.cooktime;
      this.burntime = this.tileFurnace.burntime;
   }

   public void updateProgressBar(int id, int value) {
      switch(id) {
         case 0:
            this.tileFurnace.totalburn = value;
            break;
         case 1:
            this.tileFurnace.burntime = value;
            break;
         case 2:
            this.tileFurnace.cooktime = value;
      }

   }
}
