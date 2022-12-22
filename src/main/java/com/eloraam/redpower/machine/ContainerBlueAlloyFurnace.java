package com.eloraam.redpower.machine;

import java.util.List;

import com.eloraam.redpower.base.SlotAlloyFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlueAlloyFurnace extends Container {
   public int cooktime = 0;
   private TileBlueAlloyFurnace tileFurnace;
   public int charge = 0;
   public int flow = 0;

   public ContainerBlueAlloyFurnace(InventoryPlayer inv, TileBlueAlloyFurnace td) {
      this.tileFurnace = td;

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new Slot(td, j + i * 3, 48 + j * 18, 17 + i * 18));
         }
      }

      this.addSlotToContainer(new SlotAlloyFurnace(inv.player, td, 9, 141, 35));

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
      return this.tileFurnace.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (i < 10) {
            if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
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

      for(ICrafting ic : (List<ICrafting>)super.crafters) {
         if (this.cooktime != this.tileFurnace.cooktime) {
            ic.sendProgressBarUpdate(this, 0, this.tileFurnace.cooktime);
         }

         if (this.charge != this.tileFurnace.cond.Charge) {
            ic.sendProgressBarUpdate(this, 1, this.tileFurnace.cond.Charge);
         }

         if (this.flow != this.tileFurnace.cond.Flow) {
            ic.sendProgressBarUpdate(this, 2, this.tileFurnace.cond.Flow);
            ic.sendProgressBarUpdate(this, 3, this.tileFurnace.cond.Flow >> 16);
         }
      }

      this.cooktime = this.tileFurnace.cooktime;
      this.charge = this.tileFurnace.cond.Charge;
      this.flow = this.tileFurnace.cond.Flow;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
         case 0:
            this.tileFurnace.cooktime = j;
            break;
         case 1:
            this.tileFurnace.cond.Charge = j;
            break;
         case 2:
            this.tileFurnace.cond.Flow = this.tileFurnace.cond.Flow & -65536 | j;
            break;
         case 3:
            this.tileFurnace.cond.Flow |= j << 16;
      }

   }
}
