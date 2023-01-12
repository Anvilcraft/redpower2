package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.EnvironLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IMultiblock;
import com.eloraam.redpower.core.MultiLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileWindTurbine
    extends TileMachine implements IInventory, IBluePowerConnectable, IMultiblock {
    BluePowerConductor cond = new BluePowerConductor() {
        @Override
        public TileEntity getParent() {
            return TileWindTurbine.this;
        }

        @Override
        public double getInvCap() {
            return 0.25;
        }
    };
    private byte[] rayTrace = null;
    private int efficiency = 0;
    private int tracer = 0;
    public int windSpeed = 0;
    public int speed = 0;
    public int phase = 0;
    private int power = 0;
    private int propTicks = 0;
    public boolean hasBlades = false;
    public boolean hasBrakes = false;
    public byte windmillType = 0;
    protected ItemStack[] contents = new ItemStack[1];
    public int ConMask = -1;
    public int EConMask = -1;

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
        return 2;
    }

    @Override
    public BluePowerConductor getBlueConductor(int side) {
        return this.cond;
    }

    @Override
    public void setPartRotation(int part, boolean sec, int rot) {
        this.teardownBlades();
        super.setPartRotation(part, sec, rot);
    }

    @Override
    public void onMultiRemoval(int num) {
        ItemStack ist = this.contents[0];
        if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(
                super.worldObj, super.xCoord, super.yCoord + 1, super.zCoord, ist
            );
        }

        this.contents[0] = null;
        this.markDirty();
        this.teardownBlades();
    }

    @Override
    public AxisAlignedBB getMultiBounds(int num) {
        switch (this.windmillType) {
            case 1:
                return AxisAlignedBB.getBoundingBox(-2.5, 1.3, -2.5, 3.5, 9.0, 3.5);
            case 2:
                WorldCoord wc = new WorldCoord(0, 0, 0);
                int right = WorldCoord.getRightDir(super.Rotation);
                wc.step(super.Rotation ^ 1);
                WorldCoord wc2 = wc.coordStep(super.Rotation ^ 1);
                wc.step(right, 8);
                wc2.step(right, -8);
                return AxisAlignedBB.getBoundingBox(
                    (double) Math.min(wc.x, wc2.x) + 0.5,
                    -7.5,
                    Math.min((double) wc.z, (double) wc2.z + 0.5),
                    (double) Math.max(wc.x, wc2.x) + 0.5,
                    8.5,
                    (double) Math.max(wc.z, wc2.z) + 0.5
                );
            default:
                return AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }
    }

    @Override
    public float getMultiBlockStrength(int num, EntityPlayer player) {
        return 0.08F;
    }

    @Override
    public int getExtendedID() {
        return 9;
    }

    @Override
    public Block getBlockType() {
        return RedPowerMachine.blockMachine;
    }

    public List<WorldCoord> getRelayBlockList(int wmt) {
        List<WorldCoord> tr = new ArrayList();
        int right = WorldCoord.getRightDir(super.Rotation);
        switch (wmt) {
            case 1:
                for (int x = -3; x <= 3; ++x) {
                    for (int y = -3; y <= 3; ++y) {
                        for (int i = 1; i < 8; ++i) {
                            tr.add(new WorldCoord(
                                x + super.xCoord, i + super.yCoord, y + super.zCoord
                            ));
                        }
                    }
                }
                break;
            case 2:
                for (int x = -8; x <= 8; ++x) {
                    for (int y = -8; y <= 8; ++y) {
                        WorldCoord nc = new WorldCoord(this);
                        nc.step(super.Rotation ^ 1);
                        nc.step(right, x);
                        nc.y += y;
                        tr.add(nc);
                    }
                }
        }

        return tr;
    }

    private void teardownBlades() {
        this.hasBlades = false;
        this.efficiency = 0;
        this.speed = 0;
        this.rayTrace = null;
        this.updateBlock();
        List<WorldCoord> rbl = this.getRelayBlockList(this.windmillType);
        MultiLib.removeRelays(super.worldObj, new WorldCoord(this), rbl);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!super.worldObj.isRemote) {
            if (!this.isTickScheduled()) {
                this.scheduleTick(5);
            }

            if (this.ConMask < 0) {
                this.ConMask = RedPowerLib.getConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                this.EConMask = RedPowerLib.getExtConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                this.cond.recache(this.ConMask, this.EConMask);
            }

            this.cond.iterate();
            this.markDirty();
            if (this.hasBlades) {
                if (this.contents[0] == null
                    || !(this.contents[0].getItem() instanceof ItemWindmill)) {
                    this.teardownBlades();
                    return;
                }

                ItemWindmill iwm = (ItemWindmill) this.contents[0].getItem();
                if (iwm.windmillType != this.windmillType) {
                    this.teardownBlades();
                    return;
                }

                if (this.propTicks <= 0) {
                    this.contents[0].setItemDamage(this.contents[0].getItemDamage() + 1);
                    if (this.contents[0].getItemDamage()
                        > this.contents[0].getMaxDamage()) {
                        this.contents[0] = null;
                        this.markDirty();
                        this.teardownBlades();
                        this.contents[0] = iwm.getBrokenItem();
                        this.markDirty();
                        return;
                    }

                    this.markDirty();
                    this.propTicks += 6600;
                }

                if (this.hasBrakes) {
                    return;
                }

                --this.propTicks;
                if (this.cond.getVoltage() > 130.0) {
                    return;
                }

                this.cond.applyPower((double) (this.power / 5));
            }
        } else if (this.hasBrakes) {
            this.phase = (int) ((double) this.phase + (double) this.speed * 0.1);
        } else {
            this.phase += this.speed;
        }
    }

    private void traceAir0() {
        int yh = super.yCoord + 1 + this.tracer / 28;
        int xp = this.tracer % 7;
        WorldCoord tp;
        byte var6;
        switch (this.tracer / 7 % 4) {
            case 0:
                var6 = 2;
                tp = new WorldCoord(super.xCoord - 3 + xp, yh, super.zCoord - 4);
                break;
            case 1:
                var6 = 4;
                tp = new WorldCoord(super.xCoord - 4, yh, super.zCoord - 3 + xp);
                break;
            case 2:
                var6 = 3;
                tp = new WorldCoord(super.xCoord - 3 + xp, yh, super.zCoord + 4);
                break;
            default:
                var6 = 5;
                tp = new WorldCoord(super.xCoord + 4, yh, super.zCoord - 3 + xp);
        }

        int i;
        for (i = 0; i < 10 && super.worldObj.getBlock(tp.x, tp.y, tp.z) == Blocks.air;
             ++i) {
            tp.step(var6);
        }

        if (this.rayTrace == null) {
            this.rayTrace = new byte[224];
        }

        this.efficiency = this.efficiency - this.rayTrace[this.tracer] + i;
        this.rayTrace[this.tracer] = (byte) i;
        ++this.tracer;
        if (this.tracer >= 224) {
            this.tracer = 0;
        }
    }

    private void traceAir1() {
        int yh = this.tracer / 17;
        int xp = this.tracer % 17;
        int dir2 = WorldCoord.getRightDir(super.Rotation);
        WorldCoord tp = new WorldCoord(this);
        tp.step(super.Rotation ^ 1, 2);
        tp.step(dir2, xp - 8);
        tp.y += yh;

        int i;
        for (i = 0; i < 20 && super.worldObj.getBlock(tp.x, tp.y, tp.z) == Blocks.air;
             ++i) {
            tp.step(super.Rotation ^ 1);
        }

        if (this.rayTrace == null) {
            this.rayTrace = new byte[289];
        }

        this.efficiency = this.efficiency - this.rayTrace[this.tracer] + i;
        this.rayTrace[this.tracer] = (byte) i;
        ++this.tracer;
        if (this.tracer >= 289) {
            this.tracer = 0;
        }
    }

    public int getWindScaled(int i) {
        return Math.min(i, i * this.windSpeed / 13333);
    }

    private void tryDeployBlades() {
        ItemWindmill iwm = (ItemWindmill) this.contents[0].getItem();
        if (iwm.canFaceDirection(super.Rotation)) {
            List<WorldCoord> rbl = this.getRelayBlockList(iwm.windmillType);
            if (MultiLib.isClear(super.worldObj, new WorldCoord(this), rbl)) {
                this.windmillType = (byte) iwm.windmillType;
                this.hasBlades = true;
                MultiLib.addRelays(super.worldObj, new WorldCoord(this), 0, rbl);
                this.updateBlock();
            }
        }
    }

    @Override
    public void onTileTick() {
        if (!this.hasBlades && this.contents[0] != null
            && this.contents[0].getItem() instanceof ItemWindmill) {
            this.tryDeployBlades();
        }

        if (!this.hasBrakes && this.cond.getVoltage() > 110.0) {
            this.hasBrakes = true;
        } else if (this.hasBrakes && this.cond.getVoltage() < 100.0) {
            this.hasBrakes = false;
        }

        this.windSpeed = (int
        ) (10000.0 * EnvironLib.getWindSpeed(super.worldObj, new WorldCoord(this)));
        if (this.hasBlades) {
            switch (this.windmillType) {
                case 1:
                    this.power = 2 * this.windSpeed * this.efficiency / 2240;
                    this.speed = this.power * this.power / 20000;
                    this.traceAir0();
                    break;
                case 2:
                    this.power = this.windSpeed * this.efficiency / 5780;
                    this.speed = this.power * this.power / 5000;
                    this.traceAir1();
            }

            this.updateBlock();
        }

        this.scheduleTick(20);
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
    }

    public String getInventoryName() {
        return "gui.windturbine";
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
    public void onBlockNeighborChange(Block block) {
        this.ConMask = -1;
        this.EConMask = -1;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!super.worldObj.isRemote) {
                player.openGui(
                    RedPowerMachine.instance,
                    15,
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
        if (this.hasBlades) {
            this.teardownBlades();
        }

        ItemStack ist = this.contents[0];
        if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist
            );
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 1048576.0;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        NBTTagList items = data.getTagList("Items", 10);
        this.contents = new ItemStack[this.getSizeInventory()];

        for (int rt = 0; rt < items.tagCount(); ++rt) {
            NBTTagCompound i = items.getCompoundTagAt(rt);
            int j = i.getByte("Slot") & 255;
            if (j >= 0 && j < this.contents.length) {
                this.contents[j] = ItemStack.loadItemStackFromNBT(i);
            }
        }

        this.windmillType = data.getByte("wmt");
        this.hasBlades = this.windmillType > 0;
        this.efficiency = 0;
        byte[] rays = data.getByteArray("rays");
        if (rays != null) {
            switch (this.windmillType) {
                case 1:
                    if (rays.length != 224) {
                        rays = null;
                    }
                    break;
                case 2:
                    if (rays.length != 289) {
                        rays = null;
                    }
                    break;
                default:
                    rays = null;
            }
        }

        this.rayTrace = rays;
        if (rays != null) {
            for (byte b : rays) {
                this.efficiency += b;
            }
        }

        this.tracer = data.getInteger("tracer");
        this.speed = data.getInteger("speed");
        this.power = data.getInteger("spdpwr");
        this.propTicks = data.getInteger("proptick");
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
        if (!this.hasBlades) {
            this.windmillType = 0;
        }

        data.setByte("wmt", this.windmillType);
        if (this.rayTrace != null) {
            data.setByteArray("rays", this.rayTrace);
        }

        data.setInteger("tracer", this.tracer);
        data.setInteger("speed", this.speed);
        data.setInteger("spdpwr", this.power);
        data.setInteger("proptick", this.propTicks);
        this.cond.writeToNBT(data);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        super.readFromPacket(tag);
        int ps = tag.getByte("ps");
        this.hasBlades = (ps & 1) > 0;
        this.hasBrakes = (ps & 2) > 0;
        this.windmillType = tag.getByte("wmt");
        this.speed = tag.getInteger("speed");
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        super.writeToPacket(tag);
        int ps = (this.hasBlades ? 1 : 0) | (this.hasBrakes ? 2 : 0);
        tag.setByte("ps", (byte) ps);
        tag.setByte("wmt", this.windmillType);
        tag.setInteger("speed", this.speed);
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public void openInventory() {}

    public void closeInventory() {}

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY
        );
    }

    public boolean isItemValidForSlot(int slotID, ItemStack stack) {
        return stack.getItem() == RedPowerMachine.itemWoodWindmill
            || stack.getItem() == RedPowerMachine.itemWoodTurbine;
    }
}
