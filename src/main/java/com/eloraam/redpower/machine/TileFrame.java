package com.eloraam.redpower.machine;

import java.util.Arrays;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.TileCoverable;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileFrame extends TileCoverable implements IFrameLink, IFrameSupport {
    public int CoverSides = 0;
    public int StickySides = 63;
    public short[] Covers = new short[6];

    @Override
    public boolean isFrameMoving() {
        return false;
    }

    @Override
    public boolean canFrameConnectIn(int dir) {
        return (this.StickySides & 1 << dir) > 0;
    }

    @Override
    public boolean canFrameConnectOut(int dir) {
        return (this.StickySides & 1 << dir) > 0;
    }

    @Override
    public WorldCoord getFrameLinkset() {
        return null;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void onBlockNeighborChange(Block block) {}

    public Block getBlockType() {
        return RedPowerMachine.blockFrame;
    }

    @Override
    public int getPartsMask() {
        return this.CoverSides | 536870912;
    }

    @Override
    public int getSolidPartsMask() {
        return this.CoverSides | 536870912;
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
        if (part == 29) {
            if (willHarvest) {
                CoreLib.dropItem(
                    super.worldObj,
                    super.xCoord,
                    super.yCoord,
                    super.zCoord,
                    new ItemStack(RedPowerMachine.blockFrame, 1)
                );
            }

            if (this.CoverSides > 0) {
                this.replaceWithCovers();
                this.updateBlockChange();
            } else {
                this.deleteBlock();
            }
        } else {
            super.onHarvestPart(player, part, willHarvest);
        }
    }

    @Override
    public void addHarvestContents(List<ItemStack> ist) {
        super.addHarvestContents(ist);
        ist.add(new ItemStack(RedPowerMachine.blockFrame, 1));
    }

    @Override
    public float getPartStrength(EntityPlayer player, int part) {
        BlockMachine bl = RedPowerMachine.blockMachine;
        return part == 29
            ? player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F)
            : super.getPartStrength(player, part);
    }

    @Override
    public void setPartBounds(BlockMultipart block, int part) {
        if (part == 29) {
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            super.setPartBounds(block, part);
        }
    }

    @Override
    public boolean canAddCover(int side, int cover) {
        if (side > 5) {
            return false;
        } else {
            int n = cover >> 8;
            return (n == 0 || n == 1 || n == 3 || n == 4)
                && (this.CoverSides & 1 << side) <= 0;
        }
    }

    private void rebuildSticky() {
        int ss = 0;

        for (int i = 0; i < 6; ++i) {
            int m = 1 << i;
            if ((this.CoverSides & m) == 0) {
                ss |= m;
            } else {
                int n = this.Covers[i] >> 8;
                if (n == 1 || n == 4) {
                    ss |= m;
                }
            }
        }

        this.StickySides = ss;
    }

    @Override
    public boolean tryAddCover(int side, int cover) {
        if (!this.canAddCover(side, cover)) {
            return false;
        } else {
            this.CoverSides |= 1 << side;
            this.Covers[side] = (short) cover;
            this.rebuildSticky();
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
            this.rebuildSticky();
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

    public void replaceWithCovers() {
        short[] covs = Arrays.copyOf(this.Covers, 29);
        GameProfile owner = super.Owner;
        CoverLib.replaceWithCovers(
            super.worldObj,
            super.xCoord,
            super.yCoord,
            super.zCoord,
            this.CoverSides,
            covs
        );
        TileExtended te = CoreLib.getTileEntity(
            super.worldObj, super.xCoord, super.yCoord, super.zCoord, TileExtended.class
        );
        if (te != null) {
            te.Owner = owner;
        }
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setInteger("cvm", this.CoverSides);
        byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
        int dp = 0;

        for (int i = 0; i < 6; ++i) {
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
        int cs2 = tag.getInteger("cvm") & 63;
        this.CoverSides |= cs2;
        byte[] cov = tag.getByteArray("cvs");
        if (cov != null && cs2 > 0) {
            int sp = 0;

            for (int i = 0; i < 6; ++i) {
                if ((cs2 & 1 << i) != 0) {
                    this.Covers[i]
                        = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
                    sp += 2;
                }
            }
        }

        this.markForUpdate();
    }

    @Override
    public void onFramePickup(IBlockAccess iba) {}

    @Override
    public void onFrameRefresh(IBlockAccess iba) {}

    @Override
    public void onFrameDrop() {}

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        int cs2 = data.getInteger("cvm") & 63;
        this.CoverSides |= cs2;
        byte[] cov = data.getByteArray("cvs");
        if (cov != null && cs2 > 0) {
            int sp = 0;

            for (int i = 0; i < 6; ++i) {
                if ((cs2 & 1 << i) != 0) {
                    this.Covers[i]
                        = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
                    sp += 2;
                }
            }
        }

        this.rebuildSticky();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("cvm", this.CoverSides);
        byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
        int dp = 0;

        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) != 0) {
                cov[dp] = (byte) (this.Covers[i] & 255);
                cov[dp + 1] = (byte) (this.Covers[i] >> 8);
                dp += 2;
            }
        }

        data.setByteArray("cvs", cov);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        int cs2 = tag.getInteger("cvm") & 63;
        this.CoverSides = cs2;
        byte[] cov = tag.getByteArray("cvs");
        if (cov != null && cs2 > 0) {
            int sp = 0;

            for (int i = 0; i < 6; ++i) {
                if ((cs2 & 1 << i) != 0) {
                    this.Covers[i]
                        = (short) ((cov[sp] & 255) + ((cov[sp + 1] & 255) << 8));
                    sp += 2;
                }
            }
        }

        this.rebuildSticky();
        this.markForUpdate();
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setInteger("cvm", this.CoverSides);
        byte[] cov = new byte[Integer.bitCount(this.CoverSides) * 2];
        int dp = 0;

        for (int i = 0; i < 6; ++i) {
            if ((this.CoverSides & 1 << i) != 0) {
                cov[dp] = (byte) (this.Covers[i] & 255);
                cov[dp + 1] = (byte) (this.Covers[i] >> 8);
                dp += 2;
            }
        }

        tag.setByteArray("cvs", cov);
    }

    @Override
    protected ItemStack getBasePickStack() {
        return new ItemStack(RedPowerMachine.blockFrame, 1);
    }
}
