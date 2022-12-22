package com.eloraam.redpower.world;

import com.eloraam.redpower.core.SlotLocked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSeedBag extends Container {
   private ItemStack itemBag;
   private IInventory baginv;
   private int hotbarIndex;

   public ContainerSeedBag(InventoryPlayer inv, IInventory bag, ItemStack stack) {
      this.baginv = bag;

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new ContainerSeedBag.SlotSeeds(bag, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int i = 0; i < 9; ++i) {
         if (inv.currentItem == i) {
            this.addSlotToContainer(new SlotLocked(inv, i, 8 + i * 18, 142));
         } else {
            this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
         }
      }

      this.itemBag = stack;
      this.hotbarIndex = inv.currentItem;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return player.inventory.getCurrentItem() == this.itemBag;
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
      if (!player.worldObj.isRemote && this.itemBag != player.getHeldItem()) {
         player.closeScreen();
         return null;
      } else {
         ItemStack result = null;
         Slot slot = (Slot)super.inventorySlots.get(slotId);
         if (slot != null && slot.getHasStack()) {
            ItemStack outStack = slot.getStack();
            if (!ItemSeedBag.canAdd(this.baginv, outStack)) {
               return null;
            }

            result = outStack.copy();
            if (slotId < 9) {
               if (!this.mergeItemStack(outStack, 9, 45, true)) {
                  return null;
               }
            } else if (!this.mergeItemStack(outStack, 0, 9, false)) {
               return null;
            }

            if (outStack.stackSize == 0) {
               slot.putStack(null);
            } else {
               slot.onSlotChanged();
            }

            if (outStack.stackSize == result.stackSize) {
               return null;
            }

            slot.onPickupFromSlot(player, outStack);
         }

         return result;
      }
   }

   public ItemStack slotClick(int slotId, int dragModeOrBtn, int mode, EntityPlayer player) {
      if (!this.canInteractWith(player)) {
         return null;
      } else {
         if (mode == 2 && dragModeOrBtn >= 0 && dragModeOrBtn < 9) {
            Slot hotbarSlot = this.getSlot(36 + dragModeOrBtn);
            if (hotbarSlot instanceof SlotLocked) {
               return null;
            }
         }

         return super.slotClick(slotId, dragModeOrBtn, mode, player);
      }
   }

   public static class SlotSeeds extends Slot {
      public SlotSeeds(IInventory inv, int par2, int par3, int par4) {
         super(inv, par2, par3, par4);
      }

      public boolean isItemValid(ItemStack ist) {
         return ItemSeedBag.canAdd(super.inventory, ist);
      }
   }
}
