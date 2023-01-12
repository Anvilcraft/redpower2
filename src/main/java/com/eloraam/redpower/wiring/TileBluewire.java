package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileBluewire extends TileWiring implements IBluePowerConnectable {
    BluePowerConductor cond = new BluePowerConductor() {
        @Override
        public TileEntity getParent() {
            return TileBluewire.this;
        }

        @Override
        public double getInvCap() {
            switch (TileBluewire.this.Metadata) {
                case 0:
                    return 8.0;
                default:
                    return 800.0;
            }
        }

        @Override
        public double getResistance() {
            switch (TileBluewire.this.Metadata) {
                case 0:
                    return 0.01;
                default:
                    return 1.0;
            }
        }

        @Override
        public double getIndScale() {
            switch (TileBluewire.this.Metadata) {
                case 0:
                    return 0.07;
                default:
                    return 7.0E-4;
            }
        }

        @Override
        public double getCondParallel() {
            switch (TileBluewire.this.Metadata) {
                case 0:
                    return 0.5;
                default:
                    return 0.005;
            }
        }
    };

    @Override
    public float getWireHeight() {
        switch (super.Metadata) {
            case 0:
                return 0.188F;
            case 1:
                return 0.25F;
            case 2:
                return 0.3125F;
            default:
                return 0.25F;
        }
    }

    @Override
    public int getExtendedID() {
        return 5;
    }

    @Override
    public int getConnectClass(int side) {
        switch (super.Metadata) {
            case 0:
                return 64;
            case 1:
                return 68;
            default:
                return 69;
        }
    }

    @Override
    public BluePowerConductor getBlueConductor(int side) {
        return this.cond;
    }

    @Override
    public int getConnectionMask() {
        if (super.ConMask >= 0) {
            return super.ConMask;
        } else {
            super.ConMask = RedPowerLib.getConnections(
                super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
            );
            if (super.EConMask < 0) {
                super.EConMask = RedPowerLib.getExtConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                super.EConEMask = RedPowerLib.getExtConnectionExtras(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
            }

            if (!super.worldObj.isRemote) {
                this.cond.recache(super.ConMask, super.EConMask);
            }

            return super.ConMask;
        }
    }

    @Override
    public int getExtConnectionMask() {
        if (super.EConMask >= 0) {
            return super.EConMask;
        } else {
            super.EConMask = RedPowerLib.getExtConnections(
                super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
            );
            super.EConEMask = RedPowerLib.getExtConnectionExtras(
                super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
            );
            if (super.ConMask < 0) {
                super.ConMask = RedPowerLib.getConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
            }

            if (!super.worldObj.isRemote) {
                this.cond.recache(super.ConMask, super.EConMask);
            }

            return super.EConMask;
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (!super.worldObj.isRemote) {
            if (super.ConMask < 0 || super.EConMask < 0) {
                if (super.ConMask < 0) {
                    super.ConMask = RedPowerLib.getConnections(
                        super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                    );
                }

                if (super.EConMask < 0) {
                    super.EConMask = RedPowerLib.getExtConnections(
                        super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                    );
                    super.EConEMask = RedPowerLib.getExtConnectionExtras(
                        super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                    );
                }

                this.cond.recache(super.ConMask, super.EConMask);
            }

            this.cond.iterate();
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.cond.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        this.cond.writeToNBT(data);
    }
}
