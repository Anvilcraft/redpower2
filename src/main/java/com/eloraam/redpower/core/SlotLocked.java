package com.eloraam.redpower.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLocked extends Slot {
    public SlotLocked(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    public ItemStack decrStackSize(int amount) {
        return null;
    }

    public void putStack(ItemStack stack) {}
}
