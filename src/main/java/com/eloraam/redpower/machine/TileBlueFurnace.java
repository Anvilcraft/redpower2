package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;

public class TileBlueFurnace
    extends TileAppliance implements IInventory, ISidedInventory, IBluePowerConnectable {
    BluePowerEndpoint cond = new BluePowerEndpoint() {
        @Override
        public TileEntity getParent() {
            return TileBlueFurnace.this;
        }
    };
    private ItemStack[] contents = new ItemStack[2];
    public int cooktime = 0;
    public int ConMask = -1;

    @Override
    public int getConnectableMask() {
        return 1073741823;
    }

    @Override
    public int getConnectClass(int side) {
        return 64;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public BluePowerConductor getBlueConductor(int side) {
        return this.cond;
    }

    private void updateLight() {
        super.worldObj.updateLightByType(
            EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord
        );
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!super.worldObj.isRemote) {
            if (this.ConMask < 0) {
                this.ConMask = RedPowerLib.getConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                this.cond.recache(this.ConMask, 0);
            }

            this.cond.iterate();
            this.markDirty();
            if (this.cond.getVoltage() < 60.0) {
                if (super.Active && this.cond.Flow == 0) {
                    super.Active = false;
                    this.updateBlock();
                    this.updateLight();
                }
            } else {
                boolean cs = this.canSmelt();
                if (cs) {
                    if (!super.Active) {
                        super.Active = true;
                        this.updateBlock();
                        this.updateLight();
                    }

                    ++this.cooktime;
                    this.cond.drawPower(1000.0);
                    if (this.cooktime >= 100) {
                        this.cooktime = 0;
                        this.smeltItem();
                        this.markDirty();
                    }
                } else {
                    if (super.Active) {
                        super.Active = false;
                        this.updateBlock();
                        this.updateLight();
                    }

                    this.cooktime = 0;
                }
            }
        }
    }

    private boolean canSmelt() {
        if (this.contents[0] == null) {
            return false;
        } else {
            ItemStack ist = FurnaceRecipes.smelting().getSmeltingResult(this.contents[0]);
            if (ist == null) {
                return false;
            } else if (this.contents[1] == null) {
                return true;
            } else if (!this.contents[1].isItemEqual(ist)) {
                return false;
            } else {
                int st = this.contents[1].stackSize + ist.stackSize;
                return st <= this.getInventoryStackLimit() && st <= ist.getMaxStackSize();
            }
        }
    }

    private void smeltItem() {
        if (this.canSmelt()) {
            ItemStack ist = FurnaceRecipes.smelting().getSmeltingResult(this.contents[0]);
            if (this.contents[1] == null) {
                this.contents[1] = ist.copy();
            } else if (this.contents[1].isItemEqual(ist)) {
                this.contents[1].stackSize += ist.stackSize;
            }

            if (this.contents[0].getItem().getContainerItem() != null) {
                this.contents[0]
                    = new ItemStack(this.contents[0].getItem().getContainerItem());
            } else {
                --this.contents[0].stackSize;
            }

            if (this.contents[0].stackSize <= 0) {
                this.contents[0] = null;
            }
        }
    }

    int getCookScaled(int i) {
        return this.cooktime * i / 100;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    3,
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
        for (int i = 0; i < 2; ++i) {
            ItemStack ist = this.contents[i];
            if (ist != null && ist.stackSize > 0) {
                CoreLib.dropItem(
                    super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist
                );
            }
        }
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        this.ConMask = -1;
    }

    public int getSizeInventory() {
        return 2;
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
        return "tile.rpbfurnace.name";
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

        this.cooktime = data.getShort("CookTime");
        this.cond.readFromNBT(data);
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
        data.setShort("CookTime", (short) this.cooktime);
        this.cond.writeToNBT(data);
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        switch (side) {
            case 0:
                return new int[] { 1 };
            case 1:
                return new int[] { 0 };
            default:
                return new int[0];
        }
    }

    public boolean canInsertItem(int slotID, ItemStack stack, int side) {
        return side == 1 && slotID == 0;
    }

    public boolean canExtractItem(int slotID, ItemStack stack, int side) {
        return side == 0 && slotID == 1;
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return slotID == 0;
    }
}
