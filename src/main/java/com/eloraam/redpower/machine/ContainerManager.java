package com.eloraam.redpower.machine;

import java.util.List;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerManager extends Container implements IHandleGuiEvent {
   public int charge = 0;
   public int flow = 0;
   public int color = 0;
   public int mode = 0;
   public int priority = 0;
   private TileManager tileManager;

   public ContainerManager(IInventory inv, TileManager tf) {
      this.tileManager = tf;

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 6; ++j) {
            this.addSlotToContainer(new Slot(tf, j + i * 6, 44 + 18 * j, 18 + 18 * i));
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
      return this.tileManager.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      ItemStack itemstack = null;
      Slot slot = (Slot)super.inventorySlots.get(i);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (i < 24) {
            if (!this.mergeItemStack(itemstack1, 24, 60, true)) {
               return null;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 24, false)) {
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
         if (this.charge != this.tileManager.cond.Charge) {
            ic.sendProgressBarUpdate(this, 0, this.tileManager.cond.Charge);
         }

         if (this.flow != this.tileManager.cond.Flow) {
            ic.sendProgressBarUpdate(this, 1, this.tileManager.cond.Flow);
         }

         if (this.mode != this.tileManager.mode) {
            ic.sendProgressBarUpdate(this, 2, this.tileManager.mode);
         }

         if (this.color != this.tileManager.color) {
            ic.sendProgressBarUpdate(this, 3, this.tileManager.color);
         }

         if (this.priority != this.tileManager.priority) {
            ic.sendProgressBarUpdate(this, 4, this.tileManager.priority);
         }
      }

      this.charge = this.tileManager.cond.Charge;
      this.flow = this.tileManager.cond.Flow;
      this.mode = this.tileManager.mode;
      this.color = this.tileManager.color;
      this.priority = this.tileManager.priority;
   }

   public void updateProgressBar(int i, int j) {
      switch(i) {
         case 0:
            this.tileManager.cond.Charge = j;
            break;
         case 1:
            this.tileManager.cond.Flow = j;
            break;
         case 2:
            this.tileManager.mode = (byte)j;
            break;
         case 3:
            this.tileManager.color = (byte)j;
            break;
         case 4:
            this.tileManager.priority = j;
      }

   }

   @Override
   public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
      try {
         switch(message.eventId) {
            case 1:
               this.tileManager.mode = message.parameters[0];
               this.tileManager.markDirty();
               break;
            case 2:
               this.tileManager.color = message.parameters[0];
               this.tileManager.markDirty();
               break;
            case 3:
               this.tileManager.priority = message.parameters[0];
               this.tileManager.markDirty();
         }
      } catch (Throwable var3) {
      }

   }
}
