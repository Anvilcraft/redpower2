package com.eloraam.redpower.machine;

import java.util.stream.IntStream;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TubeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileFilter extends TileTranspose implements ISidedInventory {
    protected ItemStack[] contents = new ItemStack[9];
    protected MachineLib.FilterMap filterMap = null;
    public byte color = 0;

    protected void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.contents);
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        if (side == (super.Rotation ^ 1) && state == 1) {
            if (this.filterMap == null) {
                this.regenFilterMap();
            }

            return this.filterMap.size() == 0 ? super.tubeItemEnter(side, state, item)
                                              : this.filterMap.containsKey(item.item)
                    && super.tubeItemEnter(side, state, item);
        } else {
            return super.tubeItemEnter(side, state, item);
        }
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        if (side == (super.Rotation ^ 1) && state == 1) {
            if (this.filterMap == null) {
                this.regenFilterMap();
            }

            return this.filterMap.size() == 0 ? super.tubeItemCanEnter(side, state, item)
                                              : this.filterMap.containsKey(item.item)
                    && super.tubeItemCanEnter(side, state, item);
        } else {
            return super.tubeItemCanEnter(side, state, item);
        }
    }

    @Override
    protected void addToBuffer(ItemStack ist) {
        super.buffer.addNewColor(ist, this.color);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    2,
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
    public int getExtendedID() {
        return 3;
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();

        for (int i = 0; i < 9; ++i) {
            ItemStack ist = this.contents[i];
            if (ist != null && ist.stackSize > 0) {
                CoreLib.dropItem(
                    super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist
                );
            }
        }
    }

    @Override
    protected boolean handleExtract(IInventory inv, int[] slots) {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }

        if (this.filterMap.size() == 0) {
            ItemStack sm1 = MachineLib.collectOneStack(inv, slots, null);
            if (sm1 == null) {
                return false;
            } else {
                super.buffer.addNewColor(sm1, this.color);
                this.drainBuffer();
                return true;
            }
        } else {
            int sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
            if (sm < 0) {
                return false;
            } else {
                ItemStack coll
                    = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
                super.buffer.addNewColor(coll, this.color);
                this.drainBuffer();
                return true;
            }
        }
    }

    @Override
    protected boolean suckFilter(ItemStack ist) {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }

        return this.filterMap.size() == 0 || this.filterMap.containsKey(ist);
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
        return "tile.rpfilter.name";
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

    public void markDirty() {
        this.filterMap = null;
        super.markDirty();
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

        this.color = data.getByte("color");
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
        data.setByte("color", this.color);
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        return side != super.Rotation && side != (super.Rotation ^ 1)
            ? IntStream.range(0, 9).toArray()
            : new int[0];
    }

    public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
        return side != super.Rotation && side != (super.Rotation ^ 1);
    }

    public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
        return side != super.Rotation && side != (super.Rotation ^ 1);
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
        return true;
    }
}
