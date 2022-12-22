package com.eloraam.redpower.base;

import com.eloraam.redpower.core.SlotLocked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBag extends Container {
   private ItemStack itemBag;
   private int hotbarIndex;

   public ContainerBag(InventoryPlayer inv, IInventory bag, ItemStack stack) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new ContainerBag.SlotBag(bag, j + i * 9, 8 + j * 18, 18 + i * 18));
         }
      }

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 86 + i * 18));
         }
      }

      for(int i = 0; i < 9; ++i) {
         if (inv.currentItem == i) {
            this.addSlotToContainer(new SlotLocked(inv, i, 8 + i * 18, 144));
         } else {
            this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 144));
         }
      }

      this.itemBag = stack;
      this.hotbarIndex = inv.currentItem;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return player.worldObj.isRemote || this.itemBag == player.getHeldItem();
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
      if (!player.worldObj.isRemote && this.itemBag != player.getHeldItem()) {
         player.closeScreen();
         return null;
      } else {
         ItemStack result = null;
         Slot slot = (Slot)super.inventorySlots.get(slotId);
         if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (slotStack.getItem() instanceof ItemBag) {
               return null;
            }

            result = slotStack.copy();
            if (slotId < 27) {
               if (!this.mergeItemStack(slotStack, 27, 63, true)) {
                  return null;
               }
            } else if (!this.mergeItemStack(slotStack, 0, 27, false)) {
               return null;
            }

            if (slotStack.stackSize == 0) {
               slot.putStack(null);
            } else {
               slot.onSlotChanged();
            }

            if (slotStack.stackSize == result.stackSize) {
               return null;
            }

            slot.onPickupFromSlot(player, slotStack);
         }

         return result;
      }
   }

   public ItemStack slotClick(int slotId, int dragModeOrBtn, int mode, EntityPlayer player) {
      if (!this.canInteractWith(player)) {
         return null;
      } else {
         if (mode == 2 && dragModeOrBtn >= 0 && dragModeOrBtn < 9) {
            Slot hotbarSlot = this.getSlot(54 + dragModeOrBtn);
            if (hotbarSlot instanceof SlotLocked) {
               return null;
            }
         }

         return super.slotClick(slotId, dragModeOrBtn, mode, player);
      }
   }

   public static class SlotBag extends Slot {
      public SlotBag(IInventory inv, int index, int x, int y) {
         super(inv, index, x, y);
      }

      public boolean isItemValid(ItemStack stack) {
         return !(stack.getItem() instanceof ItemBag);
      }
   }
}
