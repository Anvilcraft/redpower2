package com.eloraam.redpower.core;

import java.util.Arrays;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileCovered extends TileCoverable implements IFrameSupport {
    public int CoverSides = 0;
    public short[] Covers = new short[29];

    public void replaceWithCovers() {
        GameProfile owner = super.Owner;
        CoverLib.replaceWithCovers(
            super.worldObj,
            super.xCoord,
            super.yCoord,
            super.zCoord,
            this.CoverSides,
            this.Covers
        );
        TileExtended te = CoreLib.getTileEntity(
            super.worldObj, super.xCoord, super.yCoord, super.zCoord, TileExtended.class
        );
        if (te != null) {
            te.Owner = owner;
        }
    }

    public boolean canUpdate() {
        return true;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        if (this.CoverSides == 0) {
            this.deleteBlock();
        }

        this.markDirty();
    }

    public Block getBlockType() {
        return CoverLib.blockCoverPlate;
    }

    @Override
    public boolean canAddCover(int side, int cover) {
        if ((this.CoverSides & 1 << side) > 0) {
            return false;
        } else {
            short[] test = Arrays.copyOf(this.Covers, 29);
            test[side] = (short) cover;
            return CoverLib.checkPlacement(this.CoverSides | 1 << side, test, 0, false);
        }
    }

    @Override
    public boolean tryAddCover(int side, int cover) {
        if (!this.canAddCover(side, cover)) {
            return false;
        } else {
            this.CoverSides |= 1 << side;
            this.Covers[side] = (short) cover;
            this.updateBlockChange();
            return true;
        }
    }

    @Override
    public int tryRemoveCover(int side) {
        if ((this.CoverSides & 1 << side) == 0) {
            return -1;
        } else {
            this.CoverSides &= ~(1 << side);
            short tr = this.Covers[side];
            this.Covers[side] = 0;
            this.updateBlockChange();
            return tr;
        }
    }

    @Override
    public int getCover(int side) {
        return (this.CoverSides & 1 << side) == 0 ? -1 : this.Covers[side];
    }

    @Override
    public int getCoverMask() {
        return this.CoverSides;
    }

    @Override
    public boolean blockEmpty() {
        return this.CoverSides == 0;
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
        int mask = data.getInteger("cvm") & 536870911;
        this.CoverSides |= mask;
        byte[] cov = data.getByteArray("cvs");
        if (cov != null && mask > 0) {
            int sp = 0;

            for (int i = 0; i < 29; ++i) {
                if ((mask & 1 << i) != 0) {
                    this.Covers[i]
                        = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
                    sp += 2;
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("cvm", this.CoverSides);
        byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
        int dp = 0;

        for (int i = 0; i < 29; ++i) {
            if ((this.CoverSides & 1 << i) != 0) {
                cov[dp] = (byte) (this.Covers[i] & 255);
                cov[dp + 1] = (byte) (this.Covers[i] >> 8);
                dp += 2;
            }
        }

        data.setByteArray("cvs", cov);
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setInteger("cvm", this.CoverSides);
        byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
        int dp = 0;

        for (int i = 0; i < 29; ++i) {
            if ((this.CoverSides & 1 << i) != 0) {
                cov[dp] = (byte) (this.Covers[i] & 255);
                cov[dp + 1] = (byte) (this.Covers[i] >> 8);
                dp += 2;
            }
        }

        tag.setByteArray("cvs", cov);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        int mask = tag.getInteger("cvm");
        this.CoverSides |= mask;
        byte[] cov = tag.getByteArray("cvs");
        if (cov != null && mask > 0) {
            int sp = 0;

            for (int i = 0; i < 29; ++i) {
                if ((mask & 1 << i) != 0) {
                    this.Covers[i]
                        = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
                    sp += 2;
                }
            }
        }
    }

    @Override
    protected void readFromPacket(NBTTagCompound data) {
        int mask = data.getInteger("cvm") & 536870911;
        this.CoverSides = mask;
        byte[] cov = data.getByteArray("cvs");
        if (cov != null && mask > 0) {
            int sp = 0;

            for (int i = 0; i < 29; ++i) {
                if ((mask & 1 << i) > 0) {
                    this.Covers[i]
                        = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
                    sp += 2;
                }
            }
        }
    }

    @Override
    protected void writeToPacket(NBTTagCompound data) {
        data.setInteger("cvm", this.CoverSides);
        byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
        int dp = 0;

        for (int i = 0; i < 29; ++i) {
            if ((this.CoverSides & 1 << i) > 0) {
                cov[dp] = (byte) (this.Covers[i] & 255);
                cov[dp + 1] = (byte) (this.Covers[i] >> 8);
                dp += 2;
            }
        }

        data.setByteArray("cvs", cov);
    }
}
