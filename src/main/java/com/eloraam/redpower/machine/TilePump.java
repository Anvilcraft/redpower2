package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.FluidBuffer;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IPipeConnectable;
import com.eloraam.redpower.core.PipeLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TilePump
    extends TileMachinePanel implements IPipeConnectable, IBluePowerConnectable {
    private TilePump.PumpBuffer inbuf = new TilePump.PumpBuffer();
    private TilePump.PumpBuffer outbuf = new TilePump.PumpBuffer();
    BluePowerEndpoint cond = new BluePowerEndpoint() {
        @Override
        public TileEntity getParent() {
            return TilePump.this;
        }
    };
    public int ConMask = -1;
    public byte PumpTick = 0;

    @Override
    public int getPipeConnectableSides() {
        return 12 << (((super.Rotation ^ 1) & 1) << 1);
    }

    @Override
    public int getPipeFlangeSides() {
        return 12 << (((super.Rotation ^ 1) & 1) << 1);
    }

    @Override
    public int getPipePressure(int side) {
        int rt = CoreLib.rotToSide(super.Rotation);
        return !super.Active
            ? 0
            : (side == rt ? 1000 : (side == ((rt ^ 1) & 0xFF) ? -1000 : 0));
    }

    @Override
    public FluidBuffer getPipeBuffer(int side) {
        int rt = CoreLib.rotToSide(super.Rotation);
        return side == rt ? this.outbuf : (side == ((rt ^ 1) & 0xFF) ? this.inbuf : null);
    }

    @Override
    public int getConnectableMask() {
        return 3 << ((super.Rotation & 1) << 1) | 17895680;
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
    public int getExtendedID() {
        return 1;
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        super.Rotation
            = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 2.5) & 3;
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        this.ConMask = -1;
        if (RedPowerLib.isPowered(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63
            )) {
            if (!super.Powered) {
                super.Powered = true;
                this.markDirty();
            }
        } else {
            super.Powered = false;
            this.markDirty();
        }
    }

    private void pumpFluid() {
        if (this.inbuf.Type != null) {
            int lv = Math.min(
                this.inbuf.getLevel(), this.outbuf.getMaxLevel() - this.outbuf.getLevel()
            );
            lv = Math.min(lv, this.inbuf.getLevel() + this.inbuf.Delta);
            if (lv > 0
                && (this.inbuf.Type == this.outbuf.Type || this.outbuf.Type == null)) {
                this.outbuf.addLevel(this.inbuf.Type, lv);
                this.inbuf.addLevel(this.inbuf.Type, -lv);
            }
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (super.worldObj.isRemote) {
            if (super.Active) {
                ++this.PumpTick;
                if (this.PumpTick >= 16) {
                    this.PumpTick = 0;
                }
            }
        } else {
            if (this.ConMask < 0) {
                this.ConMask = RedPowerLib.getConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                this.cond.recache(this.ConMask, 0);
            }

            this.cond.iterate();
            this.markDirty();
            int rt = CoreLib.rotToSide(super.Rotation);
            PipeLib.movePipeLiquid(
                super.worldObj, this, new WorldCoord(this), 3 << (rt & -2)
            );
            boolean act = super.Active;
            if (super.Active) {
                ++this.PumpTick;
                if (this.PumpTick == 8) {
                    this.cond.drawPower(10000.0);
                    this.pumpFluid();
                }

                if (this.PumpTick >= 16) {
                    this.PumpTick = 0;
                    super.Active = false;
                }

                this.cond.drawPower(200.0);
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
                    this.updateBlock();
                }
            }
        }
    }

    @Override
    public void onTileTick() {
        if (!super.worldObj.isRemote && !super.Powered) {
            super.Active = false;
            this.updateBlock();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.cond.readFromNBT(data);
        this.inbuf.readFromNBT(data, "inb");
        this.outbuf.readFromNBT(data, "outb");
        this.PumpTick = data.getByte("ptk");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        this.cond.writeToNBT(data);
        this.inbuf.writeToNBT(data, "inb");
        this.outbuf.writeToNBT(data, "outb");
        data.setByte("ptk", this.PumpTick);
    }

    private class PumpBuffer extends FluidBuffer {
        private PumpBuffer() {}

        @Override
        public TileEntity getParent() {
            return TilePump.this;
        }

        @Override
        public void onChange() {
            TilePump.this.markDirty();
        }

        @Override
        public int getMaxLevel() {
            return 1000;
        }
    }
}
