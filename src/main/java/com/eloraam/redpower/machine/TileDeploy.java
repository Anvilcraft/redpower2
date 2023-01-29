package com.eloraam.redpower.machine;

import java.util.stream.IntStream;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.FakePlayer;

public class TileDeploy extends TileDeployBase implements ISidedInventory {
    private ItemStack[] contents = new ItemStack[9];

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    1,
                    super.worldObj,
                    super.xCoord,
                    super.yCoord,
                    super.zCoord
                );
            }

            return true;
        }
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 9; ++i) {
            ItemStack ist = this.contents[i];
            if (ist != null && ist.stackSize > 0) {
                CoreLib.dropItem(
                    super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist
                );
            }
        }
    }

    protected void packInv(ItemStack[] bkup, FakePlayer player) {
        for (int i = 0; i < 9; ++i) {
            bkup[i] = player.inventory.getStackInSlot(i);
            player.inventory.setInventorySlotContents(i, this.contents[i]);
        }
    }

    protected void unpackInv(ItemStack[] bkup, FakePlayer player) {
        for (int i = 0; i < 9; ++i) {
            this.contents[i] = player.inventory.getStackInSlot(i);
            player.inventory.setInventorySlotContents(i, bkup[i]);
        }
    }

    @Override
    public void enableTowards(WorldCoord wc) {
        ItemStack[] bkup = new ItemStack[9];
        FakePlayer player = CoreLib.getRedpowerPlayer(
            super.worldObj,
            super.xCoord,
            super.yCoord,
            super.zCoord,
            super.Rotation,
            super.Owner
        );
        this.packInv(bkup, player);

        for (int i = 0; i < 9; ++i) {
            ItemStack ist = this.contents[i];
            if (ist != null && ist.stackSize > 0
                && this.tryUseItemStack(ist, wc.x, wc.y, wc.z, i, player)) {
                if (player.isUsingItem()) {
                    player.stopUsingItem();
                }

                this.unpackInv(bkup, player);
                if (this.contents[i].stackSize == 0) {
                    this.contents[i] = null;
                }

                this.markDirty();
                return;
            }
        }

        this.unpackInv(bkup, player);
    }

    public int getSizeInventory() {
        return 9;
    }

    public ItemStack getStackInSlot(int i) {
        return this.contents[i];
    }

    public ItemStack decrStackSize(int i, int j) {
        if (this.contents[i] == null) {
            return null;
        } else if (this.contents[i].stackSize <= j) {
            ItemStack tr = this.contents[i];
            this.contents[i] = null;
            this.markDirty();
            return tr;
        } else {
            ItemStack tr = this.contents[i].splitStack(j);
            if (this.contents[i].stackSize == 0) {
                this.contents[i] = null;
            }

            this.markDirty();
            return tr;
        }
    }

    public ItemStack getStackInSlotOnClosing(int i) {
        if (this.contents[i] == null) {
            return null;
        } else {
            ItemStack ist = this.contents[i];
            this.contents[i] = null;
            return ist;
        }
    }

    public void setInventorySlotContents(int i, ItemStack ist) {
        this.contents[i] = ist;
        if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
            ist.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public String getInventoryName() {
        return "tile.rpdeploy.name";
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return !this.isInvalid()
            && super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord)
            == this
            && player.getDistanceSq(
                   (double) super.xCoord + 0.5,
                   (double) super.yCoord + 0.5,
                   (double) super.zCoord + 0.5
               )
            <= 64.0;
    }

    public void closeInventory() {}

    public void openInventory() {}

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        NBTTagList items = data.getTagList("Items", 10);
        this.contents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int j = item.getByte("Slot") & 255;
            if (j >= 0 && j < this.contents.length) {
                this.contents[j] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagList items = new NBTTagList();

        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                this.contents[i].writeToNBT(item);
                items.appendTag(item);
            }
        }

        data.setTag("Items", items);
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        return side != (super.Rotation ^ 1) ? IntStream.range(0, 9).toArray()
                                            : new int[0];
    }

    public boolean canInsertItem(int slotID, ItemStack stack, int side) {
        return side != (super.Rotation ^ 1);
    }

    public boolean canExtractItem(int slotID, ItemStack stack, int side) {
        return side != (super.Rotation ^ 1);
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return true;
    }
}
