package com.eloraam.redpower.control;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.DiskLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.IBlockAccess;

public class TileDiskDrive
    extends TileExtended implements IRedbusConnectable, IInventory, IFrameSupport {
    public int Rotation = 0;
    public boolean hasDisk = false;
    public boolean Active = false;
    private ItemStack[] contents = new ItemStack[1];
    private int accessTime = 0;
    private byte[] databuf = new byte[128];
    private int sector = 0;
    private int cmdreg = 0;
    private int rbaddr = 2;

    @Override
    public int rbGetAddr() {
        return this.rbaddr;
    }

    @Override
    public void rbSetAddr(int addr) {
        this.rbaddr = addr;
    }

    @Override
    public int rbRead(int reg) {
        if (reg < 128) {
            return this.databuf[reg] & 0xFF;
        } else {
            switch (reg) {
                case 128:
                    return this.sector & 0xFF;
                case 129:
                    return this.sector >> 8;
                case 130:
                    return this.cmdreg & 0xFF;
                default:
                    return 0;
            }
        }
    }

    @Override
    public void rbWrite(int reg, int dat) {
        this.markDirty();
        if (reg < 128) {
            this.databuf[reg] = (byte) dat;
        } else {
            switch (reg) {
                case 128:
                    this.sector = this.sector & 0xFF00 | dat;
                    break;
                case 129:
                    this.sector = this.sector & 0xFF | dat << 8;
                    break;
                case 130:
                    this.cmdreg = dat;
            }
        }
    }

    @Override
    public int getConnectableMask() {
        return 16777215;
    }

    @Override
    public int getConnectClass(int side) {
        return 66;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        this.Rotation
            = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 0.5) + 1 & 3;
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (!super.worldObj.isRemote) {
            if (this.hasDisk && this.contents[0] != null && !this.Active) {
                this.ejectDisk();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public Block getBlockType() {
        return RedPowerControl.blockPeripheral;
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    @Override
    public void onBlockRemoval() {
        for (int i = 0; i < 1; ++i) {
            ItemStack ist = this.contents[i];
            if (ist != null && ist.stackSize > 0) {
                CoreLib.dropItem(
                    super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist
                );
            }
        }
    }

    boolean setDisk(ItemStack ist) {
        if (this.contents[0] != null) {
            return false;
        } else {
            this.setInventorySlotContents(0, ist);
            return true;
        }
    }

    private NBTTagCompound getDiskTags() {
        NBTTagCompound tags = this.contents[0].stackTagCompound;
        if (tags == null) {
            this.contents[0].setTagCompound(new NBTTagCompound());
            tags = this.contents[0].stackTagCompound;
        }

        return tags;
    }

    private File startDisk() {
        if (this.contents[0].getItemDamage() > 0) {
            return null;
        } else {
            NBTTagCompound tags = this.getDiskTags();
            File savedir = DiskLib.getSaveDir(super.worldObj);
            if (tags.hasKey("serno")) {
                return DiskLib.getDiskFile(savedir, tags.getString("serno"));
            } else {
                String serno = null;

                while (true) {
                    serno = DiskLib.generateSerialNumber(super.worldObj);
                    File diskFile = DiskLib.getDiskFile(savedir, serno);

                    try {
                        if (diskFile.createNewFile()) {
                            tags.setString("serno", serno);
                            return diskFile;
                        }
                    } catch (IOException var6) {
                        var6.printStackTrace();
                        return null;
                    }
                }
            }
        }
    }

    private void runCmd1() {
        Arrays.fill(this.databuf, (byte) 0);
        String nm = "";
        if (this.contents[0].getItemDamage() > 0) {
            nm = "System Disk";
        } else {
            NBTTagCompound e = this.contents[0].stackTagCompound;
            if (e == null) {
                return;
            }

            nm = e.getString("label");
        }

        try {
            byte[] e1 = nm.getBytes("US-ASCII");
            System.arraycopy(e1, 0, this.databuf, 0, Math.min(e1.length, 128));
        } catch (UnsupportedEncodingException var3) {}
    }

    private void runCmd2() {
        if (this.contents[0].getItemDamage() > 0) {
            this.cmdreg = -1;
        } else {
            NBTTagCompound tags = this.getDiskTags();
            int len = 0;

            while (this.databuf[len] != 0 && len < 64) {
                ++len;
            }

            this.cmdreg = 0;

            try {
                String e = new String(this.databuf, 0, len, "US-ASCII");
                tags.setString("label", e);
            } catch (UnsupportedEncodingException var4) {}
        }
    }

    private void runCmd3() {
        Arrays.fill(this.databuf, (byte) 0);
        String nm = "";
        if (this.contents[0].getItemDamage() > 0) {
            nm = String.format("%016d", this.contents[0].getItemDamage());
        } else {
            NBTTagCompound e = this.getDiskTags();
            this.startDisk();
            if (e == null) {
                return;
            }

            nm = e.getString("serno");
        }

        try {
            byte[] e1 = nm.getBytes("US-ASCII");
            System.arraycopy(e1, 0, this.databuf, 0, Math.min(e1.length, 128));
        } catch (UnsupportedEncodingException var3) {}
    }

    private void runCmd4() {
        if (this.sector > 2048) {
            this.cmdreg = -1;
        } else {
            long l = (long) (this.sector * 128);
            if (this.contents[0].getItemDamage() > 0) {
                InputStream file = null;
                switch (this.contents[0].getItemDamage()) {
                    case 1:
                        file = RedPowerControl.class.getResourceAsStream(
                            "/assets/rpcontrol/forth/redforth.img"
                        );
                        break;
                    case 2:
                        file = RedPowerControl.class.getResourceAsStream(
                            "/assets/rpcontrol/forth/redforthxp.img"
                        );
                }

                try {
                    if (file.skip(l) == l) {
                        if (file.read(this.databuf) == 128) {
                            this.cmdreg = 0;
                            return;
                        }

                        this.cmdreg = -1;
                        return;
                    }

                    this.cmdreg = -1;
                } catch (IOException var36) {
                    var36.printStackTrace();
                    this.cmdreg = -1;
                    return;
                } finally {
                    try {
                        if (file != null) {
                            file.close();
                        }
                    } catch (IOException var33) {}
                }

                return;
            } else {
                File file1 = this.startDisk();
                if (file1 == null) {
                    this.cmdreg = -1;
                } else {
                    RandomAccessFile raf = null;

                    try {
                        raf = new RandomAccessFile(file1, "r");
                        raf.seek(l);
                        if (raf.read(this.databuf) == 128) {
                            this.cmdreg = 0;
                            return;
                        }

                        this.cmdreg = -1;
                    } catch (IOException var34) {
                        var34.printStackTrace();
                        this.cmdreg = -1;
                    } finally {
                        try {
                            if (raf != null) {
                                raf.close();
                            }
                        } catch (IOException var32) {}
                    }
                }
            }
        }
    }

    private void runCmd5() {
        if (this.contents[0].getItemDamage() > 0) {
            this.cmdreg = -1;
        } else if (this.sector > 2048) {
            this.cmdreg = -1;
        } else {
            long l = (long) (this.sector * 128);
            File file = this.startDisk();
            if (file == null) {
                this.cmdreg = -1;
            } else {
                RandomAccessFile raf = null;

                try {
                    raf = new RandomAccessFile(file, "rw");
                    raf.seek(l);
                    raf.write(this.databuf);
                    raf.close();
                    raf = null;
                    this.cmdreg = 0;
                } catch (IOException var14) {
                    this.cmdreg = -1;
                } finally {
                    try {
                        if (raf != null) {
                            raf.close();
                        }
                    } catch (IOException var13) {}
                }
            }
        }
    }

    private void runDiskCmd() {
        this.markDirty();
        if (this.contents[0] == null) {
            this.cmdreg = -1;
        } else if (!(this.contents[0].getItem() instanceof ItemDisk)) {
            this.cmdreg = -1;
        } else {
            switch (this.cmdreg) {
                case 1:
                    this.runCmd1();
                    this.cmdreg = 0;
                    break;
                case 2:
                    this.runCmd2();
                    break;
                case 3:
                    this.runCmd3();
                    this.cmdreg = 0;
                    break;
                case 4:
                    this.runCmd4();
                    break;
                case 5:
                    this.runCmd5();
                    break;
                default:
                    this.cmdreg = -1;
            }

            this.accessTime = 5;
            if (!this.Active) {
                this.Active = true;
                this.updateBlock();
            }
        }
    }

    private void ejectDisk() {
        if (this.contents[0] != null) {
            MachineLib.ejectItem(
                super.worldObj,
                new WorldCoord(this),
                this.contents[0],
                CoreLib.rotToSide(this.Rotation) ^ 1
            );
            this.contents[0] = null;
            this.hasDisk = false;
            this.updateBlock();
        }
    }

    public void markDirty() {
        super.markDirty();
        if (this.contents[0] != null
            && !(this.contents[0].getItem() instanceof ItemDisk)) {
            this.ejectDisk();
        }
    }

    @Override
    public void updateEntity() {
        if (this.cmdreg != 0 && this.cmdreg != -1) {
            this.runDiskCmd();
        }

        if (this.accessTime > 0 && --this.accessTime == 0) {
            this.Active = false;
            this.updateBlock();
        }
    }

    public int getSizeInventory() {
        return 1;
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
        this.hasDisk = this.contents[i] != null;
        this.updateBlock();
    }

    public String getInventoryName() {
        return "tile.rpdiskdrive.name";
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

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("fl", (byte) ((this.hasDisk ? 1 : 0) | (this.Active ? 2 : 0)));
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        int fl = tag.getByte("fl");
        this.hasDisk = (fl & 1) > 0;
        this.Active = (fl & 2) > 0;
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
        this.Rotation = data.getByte("rot");
        this.accessTime = data.getByte("actime");
        this.sector = data.getShort("sect");
        this.cmdreg = data.getByte("cmd") & 255;
        this.rbaddr = data.getByte("rbaddr") & 255;
        byte fl = data.getByte("fl");
        this.hasDisk = (fl & 1) > 0;
        this.Active = (fl & 2) > 0;
        this.databuf = data.getByteArray("dbuf");
        if (this.databuf.length != 128) {
            this.databuf = new byte[128];
        }

        NBTTagList items = data.getTagList("Items", 10);
        this.contents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int j = item.getByte("Slot") & 255;
            if (j >= 0 && j < this.contents.length) {
                this.contents[j] = ItemStack.loadItemStackFromNBT(item);
            }
        }

        this.hasDisk = this.contents[0] != null;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("rot", (byte) this.Rotation);
        int fl = (this.hasDisk ? 1 : 0) | (this.Active ? 2 : 0);
        data.setByte("fl", (byte) fl);
        data.setByte("actime", (byte) this.accessTime);
        data.setByteArray("dbuf", this.databuf);
        data.setShort("sect", (short) this.sector);
        data.setByte("cmd", (byte) this.cmdreg);
        data.setByte("rbaddr", (byte) this.rbaddr);
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

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        int fl = tag.getByte("fl");
        this.hasDisk = (fl & 1) > 0;
        this.Active = (fl & 2) > 0;
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("fl", (byte) ((this.hasDisk ? 1 : 0) | (this.Active ? 2 : 0)));
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public void openInventory() {}

    public void closeInventory() {}

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return slotID == 0 && stack.getItem() instanceof ItemDisk;
    }
}
