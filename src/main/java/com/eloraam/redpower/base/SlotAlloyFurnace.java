package com.eloraam.redpower.base;

import com.eloraam.redpower.core.AchieveLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotAlloyFurnace extends Slot {
    private EntityPlayer thePlayer;
    int totalCrafted;

    public SlotAlloyFurnace(EntityPlayer player, IInventory inv, int x, int y, int z) {
        super(inv, x, y, z);
        this.thePlayer = player;
    }

    public boolean isItemValid(ItemStack ist) {
        return false;
    }

    public ItemStack decrStackSize(int num) {
        if (this.getHasStack()) {
            this.totalCrafted += Math.min(num, this.getStack().stackSize);
        }

        return super.decrStackSize(num);
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack ist) {
        this.onCrafting(ist);
        super.onPickupFromSlot(player, ist);
    }

    protected void onCrafting(ItemStack ist, int num) {
        this.totalCrafted += num;
        this.onCrafting(ist);
    }

    protected void onCrafting(ItemStack ist) {
        ist.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.totalCrafted);
        this.totalCrafted = 0;
        AchieveLib.onAlloy(this.thePlayer, ist);
    }
}
