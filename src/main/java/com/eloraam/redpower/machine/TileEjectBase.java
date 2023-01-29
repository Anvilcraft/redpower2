package com.eloraam.redpower.machine;

import java.util.stream.IntStream;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEjectBase
    extends TileMachine implements ISidedInventory, ITubeConnectable {
    TubeBuffer buffer = new TubeBuffer();
    protected ItemStack[] contents = new ItemStack[9];

    @Override
    public int getTubeConnectableSides() {
        return 1 << super.Rotation;
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        if (side == super.Rotation && state == 2) {
            this.buffer.addBounce(item);
            super.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        return side == super.Rotation && state == 2;
    }

    @Override
    public int tubeWeight(int side, int state) {
        return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
    }

    protected void addToBuffer(ItemStack ist) {
        this.buffer.addNew(ist);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    12,
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

        this.buffer.onRemove(this);
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!this.handleItem(ti)) {
                this.buffer.plugged = true;
                return;
            }

            this.buffer.pop();
            if (this.buffer.plugged) {
                return;
            }
        }
    }

    @Override
    public void onTileTick() {
        if (!super.worldObj.isRemote) {
            if (!this.buffer.isEmpty()) {
                this.drainBuffer();
                if (!this.buffer.isEmpty()) {
                    this.scheduleTick(10);
                } else {
                    this.scheduleTick(5);
                }
            } else if (!super.Powered) {
                super.Active = false;
                this.updateBlock();
            }
        }
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
        return "tile.rpeject.name";
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

        this.buffer.readFromNBT(data);
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
        this.buffer.writeToNBT(data);
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        return IntStream.range(0, 9).toArray();
    }

    public boolean canInsertItem(int slotID, ItemStack stack, int side) {
        return side != super.Rotation;
    }

    public boolean canExtractItem(int slotID, ItemStack stack, int side) {
        return side != super.Rotation;
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return true;
    }
}
