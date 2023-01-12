package com.eloraam.redpower.compat;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyReceiver;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileBlueEngine
    extends TileMachineCompat implements IBluePowerConnectable, IEnergyConnection {
    private BluePowerEndpoint cond = new BluePowerEndpoint() {
        @Override
        public TileEntity getParent() {
            return TileBlueEngine.this;
        }
    };
    public int ConMask = -1;
    public byte PumpTick = 0;
    public byte PumpSpeed = 16;
    private int Flywheel = 0;

    @Override
    public int getConnectableMask() {
        int wm = RedPowerLib.getConDirMask(super.Rotation ^ 1)
            | 15 << ((super.Rotation ^ 1) << 2);
        return 16777215 & ~wm | 16777216 << super.Rotation;
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

    @Override
    public void onBlockNeighborChange(Block bl) {
        this.ConMask = -1;
        int cm = this.getConnectableMask();
        if (RedPowerLib.isPowered(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, cm, cm >> 24
            )) {
            if (!super.Powered) {
                super.Powered = true;
                this.updateBlock();
            }
        } else {
            super.Powered = false;
            this.updateBlock();
        }
    }

    protected void deliverPower() {
        WorldCoord pos = new WorldCoord(this);
        pos.step(super.Rotation ^ 1);
        IEnergyReceiver ipr
            = CoreLib.getTileEntity(super.worldObj, pos, IEnergyReceiver.class);
        ForgeDirection oppSide = ForgeDirection.getOrientation(super.Rotation);
        if (ipr != null && ipr.canConnectEnergy(oppSide)) {
            this.Flywheel -= ipr.receiveEnergy(oppSide, this.Flywheel * 10, false) / 10;
        }
    }

    @Override
    public void onTileTick() {}

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
            boolean act = super.Active;
            if (super.Active) {
                ++this.PumpTick;
                int sp = this.PumpTick;
                if (sp == this.PumpSpeed) {
                    this.deliverPower();
                }

                if (sp >= this.PumpSpeed * 2) {
                    this.PumpTick = 0;
                    if (this.PumpSpeed > 4) {
                        --this.PumpSpeed;
                    }

                    super.Active = false;
                }

                if (super.Powered && this.Flywheel < 512) {
                    double draw = Math.min(
                        (double) Math.min(512 - this.Flywheel, 32),
                        0.002 * this.cond.getEnergy(60.0)
                    );
                    this.cond.drawPower(1000.0 * draw);
                    this.Flywheel = (int) ((double) this.Flywheel + draw);
                }

                this.cond.drawPower(50.0);
            }

            if (this.cond.getVoltage() < 60.0) {
                if (super.Charged && this.cond.Flow == 0) {
                    super.Charged = false;
                    this.updateBlock();
                }
            } else {
                if (!super.Charged) {
                    super.Charged = true;
                    this.updateBlock();
                }

                if (super.Charged && super.Powered) {
                    super.Active = true;
                }

                if (super.Active != act) {
                    if (super.Active) {
                        this.PumpSpeed = 16;
                    }

                    this.updateBlock();
                }
            }
        } else if (super.Active) {
            ++this.PumpTick;
            if (this.PumpTick >= this.PumpSpeed * 2) {
                this.PumpTick = 0;
                if (this.PumpSpeed > 4) {
                    --this.PumpSpeed;
                }
            }
        } else {
            this.PumpTick = 0;
        }
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.cond.readFromNBT(tag);
        this.PumpTick = tag.getByte("ptk");
        this.PumpSpeed = tag.getByte("spd");
        this.Flywheel = tag.getInteger("flyw");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.cond.writeToNBT(tag);
        tag.setByte("ptk", this.PumpTick);
        tag.setByte("spd", this.PumpSpeed);
        tag.setInteger("flyw", this.Flywheel);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        super.readFromPacket(tag);
        this.PumpSpeed = tag.getByte("spd");
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        super.writeToPacket(tag);
        tag.setByte("spd", this.PumpSpeed);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection side) {
        return side.getOpposite() == ForgeDirection.getOrientation(super.Rotation);
    }
}
