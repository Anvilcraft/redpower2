package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.FrameLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

public class TileMotor extends TileExtended
    implements IBluePowerConnectable, IRotatable, IFrameLink, IFrameSupport {
    BluePowerEndpoint cond = new BluePowerEndpoint() {
        @Override
        public TileEntity getParent() {
            return TileMotor.this;
        }
    };
    public int Rotation = 0;
    public int MoveDir = 4;
    public int MovePos = -1;
    public boolean Powered = false;
    public boolean Active = false;
    public boolean Charged = false;
    public int LinkSize = -1;
    public int ConMask = -1;

    @Override
    public int getConnectableMask() {
        return 1073741823 ^ RedPowerLib.getConDirMask(this.Rotation >> 2 ^ 1);
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
    public WorldCoord getFrameLinkset() {
        return null;
    }

    @Override
    public BluePowerConductor getBlueConductor(int side) {
        return this.cond;
    }

    @Override
    public int getPartMaxRotation(int part, boolean sec) {
        return this.MovePos >= 0 ? 0 : (sec ? 5 : 3);
    }

    @Override
    public int getPartRotation(int part, boolean sec) {
        return sec ? this.Rotation >> 2 : this.Rotation & 3;
    }

    @Override
    public void setPartRotation(int part, boolean sec, int rot) {
        if (this.MovePos < 0) {
            if (sec) {
                this.Rotation = this.Rotation & 3 | rot << 2;
            } else {
                this.Rotation = this.Rotation & -4 | rot & 3;
            }

            this.updateBlockChange();
        }
    }

    @Override
    public boolean isFrameMoving() {
        return false;
    }

    @Override
    public boolean canFrameConnectIn(int dir) {
        return dir != (this.Rotation >> 2 ^ 1);
    }

    @Override
    public boolean canFrameConnectOut(int dir) {
        return dir == (this.Rotation >> 2 ^ 1);
    }

    @Override
    public int getExtendedID() {
        return 7;
    }

    public Block getBlockType() {
        return RedPowerMachine.blockMachine;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (this.MovePos >= 0 && this.MovePos < 16) {
            ++this.MovePos;
            this.markDirty();
        }

        if (!super.worldObj.isRemote) {
            if (this.MovePos >= 0) {
                this.cond.drawPower((double) (100 + 10 * this.LinkSize));
            }

            if (this.MovePos >= 16) {
                this.dropFrame(true);
                this.MovePos = -1;
                this.Active = false;
                this.updateBlock();
            }

            if (this.ConMask < 0) {
                this.ConMask = RedPowerLib.getConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                this.cond.recache(this.ConMask, 0);
            }

            this.cond.iterate();
            this.markDirty();
            if (this.MovePos < 0) {
                if (this.cond.getVoltage() < 60.0) {
                    if (this.Charged && this.cond.Flow == 0) {
                        this.Charged = false;
                        this.updateBlock();
                    }
                } else if (!this.Charged) {
                    this.Charged = true;
                    this.updateBlock();
                }
            }
        }
    }

    private int getDriveSide() {
        short n;
        switch (this.Rotation >> 2) {
            case 0:
                n = 13604;
                break;
            case 1:
                n = 13349;
                break;
            case 2:
                n = 20800;
                break;
            case 3:
                n = 16720;
                break;
            case 4:
                n = 8496;
                break;
            default:
                n = 12576;
        }

        int n1 = n >> ((this.Rotation & 3) << 2);
        return n1 & 7;
    }

    private void pickFrame() {
        this.MoveDir = this.getDriveSide();
        WorldCoord wc = new WorldCoord(this);
        FrameLib.FrameSolver fs = new FrameLib.FrameSolver(
            super.worldObj, wc.coordStep(this.Rotation >> 2 ^ 1), wc, this.MoveDir
        );
        if (fs.solveLimit(RedPowerMachine.FrameLinkSize) && fs.addMoved()) {
            this.LinkSize = fs.getFrameSet().size();
            this.MovePos = 0;
            this.Active = true;
            this.updateBlock();

            for (WorldCoord sp : fs.getClearSet()) {
                super.worldObj.setBlockToAir(sp.x, sp.y, sp.z);
            }

            for (WorldCoord sp : fs.getFrameSet()) {
                Block tfm = super.worldObj.getBlock(sp.x, sp.y, sp.z);
                int ifs = super.worldObj.getBlockMetadata(sp.x, sp.y, sp.z);
                TileEntity te = super.worldObj.getTileEntity(sp.x, sp.y, sp.z);
                if (te != null) {
                    super.worldObj.removeTileEntity(sp.x, sp.y, sp.z);
                }

                boolean ir = super.worldObj.isRemote;
                super.worldObj.isRemote = true;
                super.worldObj.setBlock(
                    sp.x, sp.y, sp.z, RedPowerMachine.blockFrame, 1, 2
                );
                super.worldObj.isRemote = ir;
                TileFrameMoving tfm1
                    = CoreLib.getTileEntity(super.worldObj, sp, TileFrameMoving.class);
                if (tfm1 != null) {
                    tfm1.setContents(
                        tfm, ifs, super.xCoord, super.yCoord, super.zCoord, te
                    );
                }
            }

            for (WorldCoord sp : fs.getFrameSet()) {
                super.worldObj.markBlockForUpdate(sp.x, sp.y, sp.z);
                CoreLib.markBlockDirty(super.worldObj, sp.x, sp.y, sp.z);
                TileFrameMoving tfm2
                    = CoreLib.getTileEntity(super.worldObj, sp, TileFrameMoving.class);
                if (tfm2 != null && tfm2.movingTileEntity instanceof IFrameSupport) {
                    IFrameSupport ifs1 = (IFrameSupport) tfm2.movingTileEntity;
                    ifs1.onFramePickup(tfm2.getFrameBlockAccess());
                }
            }
        }
    }

    private void dropFrame(boolean fw) {
        WorldCoord wc = new WorldCoord(this);
        FrameLib.FrameSolver fs = new FrameLib.FrameSolver(
            super.worldObj, wc.coordStep(this.Rotation >> 2 ^ 1), wc, -1
        );
        if (fs.solve()) {
            this.LinkSize = 0;
            fs.sort(this.MoveDir);
            List<BlockSnapshot> snapshots = new ArrayList();
            FakePlayer player = CoreLib.getRedpowerPlayer(
                super.worldObj,
                super.xCoord,
                super.yCoord,
                super.zCoord,
                this.Rotation >> 2,
                super.Owner
            );

            for (WorldCoord sp : fs.getFrameSet()) {
                TileFrameMoving ifs
                    = CoreLib.getTileEntity(super.worldObj, sp, TileFrameMoving.class);
                if (ifs != null) {
                    WorldCoord s2 = sp.copy();
                    if (fw) {
                        s2.step(this.MoveDir);
                    }

                    if (!CoreLib.hasEditPermission(player, s2.x, s2.y, s2.z)) {
                        return;
                    }

                    if (ifs.movingTileEntity != null) {
                        NBTTagCompound compound = new NBTTagCompound();
                        ifs.movingTileEntity.writeToNBT(compound);
                        snapshots.add(new BlockSnapshot(
                            super.worldObj,
                            s2.x,
                            s2.y,
                            s2.z,
                            ifs.movingBlock,
                            ifs.movingBlockMeta,
                            compound
                        ));
                    } else {
                        snapshots.add(new BlockSnapshot(
                            super.worldObj,
                            sp.x,
                            sp.y,
                            sp.z,
                            ifs.movingBlock,
                            ifs.movingBlockMeta
                        ));
                    }
                }
            }

            if (!snapshots.isEmpty()
                && !ForgeEventFactory
                        .onPlayerMultiBlockPlace(
                            player,
                            snapshots,
                            ForgeDirection.getOrientation(this.Rotation >> 2 ^ 1)
                        )
                        .isCanceled()) {
                for (WorldCoord sp : fs.getFrameSet()) {
                    TileFrameMoving ifs
                        = CoreLib
                              .getTileEntity(super.worldObj, sp, TileFrameMoving.class);
                    if (ifs != null) {
                        ifs.pushEntities(this);
                        WorldCoord s2 = sp.copy();
                        if (fw) {
                            s2.step(this.MoveDir);
                        }

                        if (ifs.movingBlock != Blocks.air) {
                            super.worldObj.setBlock(
                                s2.x, s2.y, s2.z, ifs.movingBlock, ifs.movingBlockMeta, 2
                            );
                            if (ifs.movingTileEntity != null) {
                                ifs.movingTileEntity.xCoord = s2.x;
                                ifs.movingTileEntity.yCoord = s2.y;
                                ifs.movingTileEntity.zCoord = s2.z;
                                ifs.movingTileEntity.validate();
                                super.worldObj.setTileEntity(
                                    s2.x, s2.y, s2.z, ifs.movingTileEntity
                                );
                            }
                        }

                        if (fw) {
                            super.worldObj.setBlockToAir(sp.x, sp.y, sp.z);
                        }
                    }
                }

                for (WorldCoord sp : fs.getFrameSet()) {
                    IFrameSupport frameSupport
                        = CoreLib.getTileEntity(super.worldObj, sp, IFrameSupport.class);
                    if (frameSupport != null) {
                        frameSupport.onFrameDrop();
                    }

                    super.worldObj.markBlockForUpdate(sp.x, sp.y, sp.z);
                    CoreLib.markBlockDirty(super.worldObj, sp.x, sp.y, sp.z);
                    RedPowerLib.updateIndirectNeighbors(
                        super.worldObj,
                        sp.x,
                        sp.y,
                        sp.z,
                        super.worldObj.getBlock(sp.x, sp.y, sp.z)
                    );
                }
            }
        }
    }

    float getMoveScaled() {
        return (float) this.MovePos / 16.0F;
    }

    @Override
    public void onBlockRemoval() {
        if (this.MovePos >= 0) {
            this.Active = false;
            this.dropFrame(false);
        }

        this.MovePos = -1;
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        this.ConMask = -1;
        if (RedPowerLib.isPowered(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63
            )) {
            if (this.Charged && !this.Powered && this.MovePos < 0) {
                this.Powered = true;
                this.updateBlockChange();
                if (this.Powered) {
                    this.pickFrame();
                }
            }
        } else if (this.Powered) {
            this.Powered = false;
            this.updateBlockChange();
        }
    }

    public int getFacing(EntityLivingBase ent) {
        int yawrx
            = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
        if (Math.abs(ent.posX - (double) super.xCoord) < 2.0
            && Math.abs(ent.posZ - (double) super.zCoord) < 2.0) {
            double p = ent.posY + 1.82 - (double) ent.yOffset - (double) super.yCoord;
            if (p > 2.0) {
                return 0 | yawrx;
            }

            if (p < 0.0) {
                return 4 | yawrx;
            }
        }

        switch (yawrx) {
            case 0:
                return 12;
            case 1:
                return 16;
            case 2:
                return 8;
            default:
                return 20;
        }
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        this.Rotation = this.getFacing(ent);
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("mdir", (byte) this.MoveDir);
        tag.setByte("mpos", (byte) (this.MovePos + 1));
        int ps = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0) | (this.Charged ? 4 : 0);
        tag.setByte("ps", (byte) ps);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        this.MoveDir = tag.getByte("mdir");
        this.MovePos = tag.getByte("mpos") - 1;
        int ps = tag.getByte("ps");
        this.Powered = (ps & 1) > 0;
        this.Active = (ps & 2) > 0;
        this.Charged = (ps & 4) > 0;
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
        this.MoveDir = data.getByte("mdir");
        this.MovePos = data.getByte("mpos");
        this.LinkSize = data.getInteger("links");
        this.cond.readFromNBT(data);
        byte k = data.getByte("ps");
        this.Powered = (k & 1) > 0;
        this.Active = (k & 2) > 0;
        this.Charged = (k & 4) > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("rot", (byte) this.Rotation);
        data.setByte("mdir", (byte) this.MoveDir);
        data.setByte("mpos", (byte) this.MovePos);
        data.setInteger("links", this.LinkSize);
        this.cond.writeToNBT(data);
        int ps = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0) | (this.Charged ? 4 : 0);
        data.setByte("ps", (byte) ps);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        this.MoveDir = tag.getByte("mdir");
        this.MovePos = tag.getByte("mpos") - 1;
        int ps = tag.getByte("ps");
        this.Powered = (ps & 1) > 0;
        this.Active = (ps & 2) > 0;
        this.Charged = (ps & 4) > 0;
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("mdir", (byte) this.MoveDir);
        tag.setByte("mpos", (byte) (this.MovePos + 1));
        int ps = (this.Powered ? 1 : 0) | (this.Active ? 2 : 0) | (this.Charged ? 4 : 0);
        tag.setByte("ps", (byte) ps);
    }
}
