package com.eloraam.redpower.machine;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBatteryBox extends Container {
    private TileBatteryBox tileBB;
    public int charge;
    public int storage;

    public ContainerBatteryBox(IInventory inv, TileBatteryBox tf) {
        this.tileBB = tf;
        this.addSlotToContainer(new Slot(tf, 0, 120, 27));
        this.addSlotToContainer(new Slot(tf, 1, 120, 55));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(
                    new Slot(inv, j + i * 9 + 9, 8 + j * 18, 88 + i * 18)
                );
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 146));
        }
    }

    public boolean canInteractWith(EntityPlayer player) {
        return player.worldObj.isRemote || this.tileBB.isUseableByPlayer(player);
    }

    public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
        return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) super.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < 2) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return null;
                }
            } else {
                Slot sl0 = (Slot) super.inventorySlots.get(0);
                ItemStack slst = sl0.getStack();
                if (slst != null && slst.stackSize != 0) {
                    return null;
                }

                sl0.putStack(itemstack1.splitStack(1));
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

        for (ICrafting ic : (List<ICrafting>) super.crafters) {
            if (this.charge != this.tileBB.Charge) {
                ic.sendProgressBarUpdate(this, 0, this.tileBB.Charge);
            }

            if (this.storage != this.tileBB.Storage) {
                ic.sendProgressBarUpdate(this, 1, this.tileBB.Storage);
            }
        }

        this.charge = this.tileBB.Charge;
        this.storage = this.tileBB.Storage;
    }

    public void updateProgressBar(int i, int j) {
        switch (i) {
            case 0:
                this.tileBB.Charge = j;
                break;
            case 1:
                this.tileBB.Storage = j;
        }
    }
}
