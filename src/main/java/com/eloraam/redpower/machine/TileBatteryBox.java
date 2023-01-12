package com.eloraam.redpower.machine;

import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class TileBatteryBox extends TileExtended
    implements IBluePowerConnectable, ISidedInventory, IFrameSupport {
    BluePowerConductor cond = new BluePowerConductor() {
        @Override
        public TileEntity getParent() {
            return TileBatteryBox.this;
        }

        @Override
        public double getInvCap() {
            return 0.25;
        }
    };
    protected ItemStack[] contents = new ItemStack[2];
    public int Charge = 0;
    public int Storage = 0;
    public int ConMask = -1;
    public boolean Powered = false;

    @Override
    public int getConnectableMask() {
        return 1073741823;
    }

    @Override
    public int getConnectClass(int side) {
        return 65;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public BluePowerConductor getBlueConductor(int side) {
        return this.cond;
    }

    public int[] getAccessibleSlotsFromSide(int side) {
        switch (side) {
            case 0:
                return new int[] { 0 };
            case 1:
                return new int[] { 1 };
            default:
                return new int[0];
        }
    }

    @Override
    public void addHarvestContents(List<ItemStack> ist) {
        ItemStack is = new ItemStack(this.getBlockType(), 1, this.getExtendedID());
        if (this.Storage > 0) {
            is.setTagCompound(new NBTTagCompound());
            is.stackTagCompound.setShort("batLevel", (short) this.Storage);
        }

        ist.add(is);
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        if (ist.stackTagCompound != null) {
            this.Storage = ist.stackTagCompound.getShort("batLevel");
        }

        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    @Override
    public int getExtendedID() {
        return 6;
    }

    public Block getBlockType() {
        return RedPowerMachine.blockMachine;
    }

    public int getMaxStorage() {
        return 6000;
    }

    public int getStorageForRender() {
        return Math.max(0, Math.min(this.Storage * 8 / this.getMaxStorage(), 8));
    }

    public int getChargeScaled(int i) {
        return Math.min(i, i * this.Charge / 1000);
    }

    public int getStorageScaled(int i) {
        return Math.min(i, i * this.Storage / this.getMaxStorage());
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
            this.Charge = (int) (this.cond.getVoltage() * 10.0);
            int rs = this.getStorageForRender();
            if (this.contents[0] != null && this.Storage > 0) {
                if (this.contents[0].getItem() == RedPowerMachine.itemBatteryEmpty) {
                    this.contents[0] = new ItemStack(
                        RedPowerMachine.itemBatteryPowered,
                        1,
                        RedPowerMachine.itemBatteryPowered.getMaxDamage()
                    );
                    this.markDirty();
                }

                if (this.contents[0].getItem() == RedPowerMachine.itemBatteryPowered) {
                    int n = Math.min(this.contents[0].getItemDamage() - 1, this.Storage);
                    n = Math.min(n, 25);
                    this.Storage -= n;
                    this.contents[0].setItemDamage(this.contents[0].getItemDamage() - n);
                    this.markDirty();
                }
            }

            if (this.contents[1] != null
                && this.contents[1].getItem() == RedPowerMachine.itemBatteryPowered) {
                int n = Math.min(
                    this.contents[1].getMaxDamage() - this.contents[1].getItemDamage(),
                    this.getMaxStorage() - this.Storage
                );
                n = Math.min(n, 25);
                this.Storage += n;
                this.contents[1].setItemDamage(this.contents[1].getItemDamage() + n);
                if (this.contents[1].getItemDamage() == this.contents[1].getMaxDamage()) {
                    this.contents[1] = new ItemStack(RedPowerMachine.itemBatteryEmpty, 1);
                }

                this.markDirty();
            }

            if (this.Charge > 900 && this.Storage < this.getMaxStorage()) {
                int n = Math.min((this.Charge - 900) / 10, 10);
                n = Math.min(n, this.getMaxStorage() - this.Storage);
                this.cond.drawPower((double) (n * 1000));
                this.Storage += n;
            } else if (this.Charge < 800 && this.Storage > 0 && !this.Powered) {
                int n = Math.min((800 - this.Charge) / 10, 10);
                n = Math.min(n, this.Storage);
                this.cond.applyPower((double) (n * 1000));
                this.Storage -= n;
            }

            if (rs != this.getStorageForRender()) {
                this.updateBlock();
            }
        }
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
        return "tile.rpbatbox.name";
    }

    public int getInventoryStackLimit() {
        return 1;
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
    public void onBlockNeighborChange(Block block) {
        this.ConMask = -1;
        if (RedPowerLib.isPowered(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63
            )) {
            if (!this.Powered) {
                this.Powered = true;
                this.markDirty();
            }
        } else if (this.Powered) {
            this.Powered = false;
            this.markDirty();
        }
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    8,
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
        super.onBlockRemoval();

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
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setInteger("stor", this.Storage);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.Storage = tag.getInteger("stor");
    }

    @Override
    public void onFrameRefresh(IBlockAccess iba) {}

    @Override
    public void onFramePickup(IBlockAccess iba) {}

    @Override
    public void onFrameDrop() {}

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        NBTTagList items = data.getTagList("Items", 10);
        this.contents = new ItemStack[this.getSizeInventory()];

        for (int k = 0; k < items.tagCount(); ++k) {
            NBTTagCompound item = items.getCompoundTagAt(k);
            int j = item.getByte("Slot") & 255;
            if (j >= 0 && j < this.contents.length) {
                this.contents[j] = ItemStack.loadItemStackFromNBT(item);
            }
        }

        this.cond.readFromNBT(data);
        this.Charge = data.getShort("chg");
        this.Storage = data.getShort("stor");
        byte var6 = data.getByte("ps");
        this.Powered = (var6 & 1) > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagList items = new NBTTagList();

        for (int ps = 0; ps < this.contents.length; ++ps) {
            if (this.contents[ps] != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) ps);
                this.contents[ps].writeToNBT(item);
                items.appendTag(item);
            }
        }

        data.setTag("Items", items);
        this.cond.writeToNBT(data);
        data.setShort("chg", (short) this.Charge);
        data.setShort("stor", (short) this.Storage);
        int var5 = this.Powered ? 1 : 0;
        data.setByte("ps", (byte) var5);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        this.Storage = tag.getInteger("stor");
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setInteger("stor", this.Storage);
    }

    public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
        return true;
    }

    public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
        return true;
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return stack.getItem() == RedPowerMachine.itemBatteryEmpty
            || stack.getItem() == RedPowerMachine.itemBatteryPowered;
    }
}
