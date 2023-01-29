package com.eloraam.redpower.machine;

import java.util.stream.IntStream;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileItemDetect
    extends TileMachine implements ITubeConnectable, IInventory, ISidedInventory {
    private TubeBuffer buffer = new TubeBuffer();
    private int count = 0;
    public byte mode = 0;
    protected ItemStack[] contents = new ItemStack[9];
    protected MachineLib.FilterMap filterMap = null;

    private void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.contents);
    }

    @Override
    public int getTubeConnectableSides() {
        return 3 << (super.Rotation & -2);
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
        } else if (side != (super.Rotation ^ 1) || state != 1) {
            return false;
        } else if (!this.buffer.isEmpty()) {
            return false;
        } else {
            this.buffer.add(item);
            if (this.filterMap == null) {
                this.regenFilterMap();
            }

            if (this.filterMap.size() == 0 || this.filterMap.containsKey(item.item)) {
                if (this.mode == 0) {
                    this.count += item.item.stackSize;
                } else if (this.mode == 1) {
                    ++this.count;
                }
            }

            super.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            this.drainBuffer();
            return true;
        }
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        return side == super.Rotation && state == 2
            || side == (super.Rotation ^ 1) && state == 1 && this.buffer.isEmpty();
    }

    @Override
    public int tubeWeight(int side, int state) {
        return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!this.handleItem(ti)) {
                this.buffer.plugged = true;
                if (this.mode == 2 && !super.Powered) {
                    super.Delay = false;
                    super.Powered = true;
                    this.count = 0;
                    this.updateBlockChange();
                }

                return;
            }

            this.buffer.pop();
            if (this.buffer.plugged) {
                if (this.mode == 2 && !super.Powered) {
                    super.Delay = false;
                    super.Powered = true;
                    this.count = 0;
                    this.updateBlockChange();
                }

                return;
            }
        }

        if (this.mode == 2 && super.Powered) {
            super.Powered = false;
            this.updateBlockChange();
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!super.worldObj.isRemote && this.mode != 2) {
            if (super.Powered) {
                if (super.Delay) {
                    super.Delay = false;
                    this.markDirty();
                } else {
                    super.Powered = false;
                    if (this.count > 0) {
                        super.Delay = true;
                    }

                    this.updateBlockChange();
                }
            } else if (this.count != 0) {
                if (super.Delay) {
                    super.Delay = false;
                    this.markDirty();
                } else {
                    --this.count;
                    super.Powered = true;
                    super.Delay = true;
                    this.updateBlockChange();
                }
            }
        }
    }

    @Override
    public boolean isPoweringTo(int side) {
        return side != (super.Rotation ^ 1) && super.Powered;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    6,
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
        return 4;
    }

    @Override
    public void onBlockRemoval() {
        this.buffer.onRemove(this);

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
    public void onTileTick() {
        if (!super.worldObj.isRemote) {
            if (!this.buffer.isEmpty()) {
                this.drainBuffer();
                if (!this.buffer.isEmpty()) {
                    this.scheduleTick(10);
                } else {
                    this.scheduleTick(5);
                }
            } else {
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
        return "tile.rpitemdet.name";
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

        this.buffer.readFromNBT(data);
        this.count = data.getInteger("cnt");
        this.mode = data.getByte("mode");
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
        data.setInteger("cnt", this.count);
        data.setByte("mode", this.mode);
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        return side != super.Rotation && side != (super.Rotation ^ 1)
            ? IntStream.range(0, 9).toArray()
            : new int[0];
    }

    public boolean canInsertItem(int slotID, ItemStack stack, int side) {
        return side != super.Rotation && side != (super.Rotation ^ 1);
    }

    public boolean canExtractItem(int slotID, ItemStack stack, int side) {
        return side != super.Rotation && side != (super.Rotation ^ 1);
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return true;
    }
}
