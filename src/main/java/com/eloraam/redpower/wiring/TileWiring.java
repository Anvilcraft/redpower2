package com.eloraam.redpower.wiring;

import java.util.Arrays;
import java.util.List;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.BlockMicro;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCovered;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public abstract class TileWiring extends TileCovered implements IWiring {
    public int ConSides = 0;
    public int Metadata = 0;
    public short CenterPost = 0;
    public int ConMask = -1;
    public int EConMask = -1;
    public int EConEMask = -1;
    public int ConaMask = -1;

    public float getWireHeight() {
        return 0.125F;
    }

    public void uncache0() {
        this.EConMask = -1;
        this.EConEMask = -1;
        this.ConMask = -1;
    }

    public void uncache() {
        if (this.ConaMask >= 0 || this.EConMask >= 0 || this.ConMask >= 0) {
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
        }

        this.ConaMask = -1;
        this.EConMask = -1;
        this.EConEMask = -1;
        this.ConMask = -1;
    }

    private static int stripBlockConMask(int side) {
        switch (side) {
            case 0:
                return 257;
            case 1:
                return 4098;
            case 2:
                return 65540;
            case 3:
                return 1048584;
            case 4:
                return 263168;
            case 5:
                return 540672;
            case 6:
                return 4196352;
            case 7:
                return 8421376;
            case 8:
                return 528;
            case 9:
                return 8224;
            case 10:
                return 131136;
            default:
                return 2097280;
        }
    }

    @Override
    public int getConnectableMask() {
        if (this.ConaMask >= 0) {
            return this.ConaMask;
        } else {
            int tr = 0;
            if ((this.ConSides & 1) > 0) {
                tr |= 15;
            }

            if ((this.ConSides & 2) > 0) {
                tr |= 240;
            }

            if ((this.ConSides & 4) > 0) {
                tr |= 3840;
            }

            if ((this.ConSides & 8) > 0) {
                tr |= 61440;
            }

            if ((this.ConSides & 16) > 0) {
                tr |= 983040;
            }

            if ((this.ConSides & 32) > 0) {
                tr |= 15728640;
            }

            if ((super.CoverSides & 1) > 0) {
                tr &= -1118465;
            }

            if ((super.CoverSides & 2) > 0) {
                tr &= -2236929;
            }

            if ((super.CoverSides & 4) > 0) {
                tr &= -4456466;
            }

            if ((super.CoverSides & 8) > 0) {
                tr &= -8912931;
            }

            if ((super.CoverSides & 16) > 0) {
                tr &= -17477;
            }

            if ((super.CoverSides & 32) > 0) {
                tr &= -34953;
            }

            for (int i = 0; i < 12; ++i) {
                if ((super.CoverSides & 16384 << i) > 0) {
                    tr &= ~stripBlockConMask(i);
                }
            }

            if ((this.ConSides & 64) > 0) {
                tr |= 1056964608;

                for (int var4 = 0; var4 < 6; ++var4) {
                    if ((super.CoverSides & 1 << var4) > 0) {
                        int j = super.Covers[var4] >> 8;
                        if (j < 3) {
                            tr &= ~(1 << var4 + 24);
                        }

                        if (j == 5) {
                            tr &= 3 << (var4 & -2) + 24;
                        }
                    }
                }
            }

            this.ConaMask = tr;
            return tr;
        }
    }

    @Override
    public int getConnectionMask() {
        if (this.ConMask >= 0) {
            return this.ConMask;
        } else {
            this.ConMask = RedPowerLib.getConnections(
                super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
            );
            return this.ConMask;
        }
    }

    @Override
    public int getExtConnectionMask() {
        if (this.EConMask >= 0) {
            return this.EConMask;
        } else {
            this.EConMask = RedPowerLib.getExtConnections(
                super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
            );
            this.EConEMask = RedPowerLib.getExtConnectionExtras(
                super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
            );
            return this.EConMask;
        }
    }

    @Override
    public int getCornerPowerMode() {
        return 1;
    }

    @Override
    public void onFrameRefresh(IBlockAccess iba) {
        if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections(
                iba, this, super.xCoord, super.yCoord, super.zCoord
            );
        }

        if (this.EConMask < 0) {
            this.EConMask = RedPowerLib.getExtConnections(
                iba, this, super.xCoord, super.yCoord, super.zCoord
            );
            this.EConEMask = RedPowerLib.getExtConnectionExtras(
                iba, this, super.xCoord, super.yCoord, super.zCoord
            );
        }
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        if (this.EConMask >= 0 || this.ConMask >= 0) {
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
        }

        this.ConMask = -1;
        this.EConMask = -1;
        this.EConEMask = -1;
        this.refreshBlockSupport();
        RedPowerLib.updateCurrent(
            super.worldObj, super.xCoord, super.yCoord, super.zCoord
        );
        this.updateBlock();
    }

    @Override
    public int getExtendedMetadata() {
        return this.Metadata;
    }

    @Override
    public void setExtendedMetadata(int md) {
        this.Metadata = md;
    }

    @Override
    public boolean canAddCover(int side, int cover) {
        if (side < 6 && (this.ConSides & 1 << side) > 0) {
            return false;
        } else if ((super.CoverSides & 1 << side) > 0) {
            return false;
        } else {
            short[] test = Arrays.copyOf(super.Covers, 29);
            test[side] = (short) cover;
            return CoverLib.checkPlacement(
                super.CoverSides | 1 << side,
                test,
                this.ConSides,
                (this.ConSides & 64) > 0
            );
        }
    }

    @Override
    public boolean tryAddCover(int side, int cover) {
        if (!this.canAddCover(side, cover)) {
            return false;
        } else {
            super.CoverSides |= 1 << side;
            super.Covers[side] = (short) cover;
            this.uncache();
            this.updateBlockChange();
            return true;
        }
    }

    @Override
    public int tryRemoveCover(int side) {
        int tr = super.tryRemoveCover(side);
        if (tr < 0) {
            return -1;
        } else {
            this.uncache();
            this.updateBlockChange();
            return tr;
        }
    }

    @Override
    public boolean blockEmpty() {
        return super.CoverSides == 0 && this.ConSides == 0;
    }

    @Override
    public void addHarvestContents(List<ItemStack> ist) {
        super.addHarvestContents(ist);

        for (int s = 0; s < 6; ++s) {
            if ((this.ConSides & 1 << s) != 0) {
                ist.add(new ItemStack(
                    RedPowerBase.blockMicro, 1, this.getExtendedID() * 256 + this.Metadata
                ));
            }
        }

        if ((this.ConSides & 64) > 0) {
            int td = 16384 + this.CenterPost;
            if (this.getExtendedID() == 3) {
                td += 256;
            }

            if (this.getExtendedID() == 5) {
                td += 512;
            }

            ist.add(new ItemStack(RedPowerBase.blockMicro, 1, td));
        }
    }

    @Override
    public int getPartsMask() {
        return super.CoverSides | this.ConSides & 63 | (this.ConSides & 64) << 23;
    }

    @Override
    public int getSolidPartsMask() {
        return super.CoverSides | (this.ConSides & 64) << 23;
    }

    public boolean refreshBlockSupport() {
        boolean all = false;
        int s = this.ConSides & 63;
        if (s == 3 || s == 12 || s == 48) {
            all = true;
        }

        for (int var3 = 0; var3 < 6; ++var3) {
            if ((this.ConSides & 1 << var3) != 0
                && (all
                    || !RedPowerLib.canSupportWire(
                        super.worldObj, super.xCoord, super.yCoord, super.zCoord, var3
                    ))) {
                this.uncache();
                CoreLib.markBlockDirty(
                    super.worldObj, super.xCoord, super.yCoord, super.zCoord
                );
                CoreLib.dropItem(
                    super.worldObj,
                    super.xCoord,
                    super.yCoord,
                    super.zCoord,
                    new ItemStack(
                        RedPowerBase.blockMicro,
                        1,
                        this.getExtendedID() * 256 + this.Metadata
                    )
                );
                this.ConSides &= ~(1 << var3);
            }
        }

        if (this.ConSides == 0) {
            if (super.CoverSides > 0) {
                this.replaceWithCovers();
            } else {
                this.deleteBlock();
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
        if (part == 29 && (this.ConSides & 64) > 0) {
            int td = 16384 + this.CenterPost;
            if (this.getExtendedID() == 3) {
                td += 256;
            }

            if (this.getExtendedID() == 5) {
                td += 512;
            }

            if (willHarvest) {
                CoreLib.dropItem(
                    super.worldObj,
                    super.xCoord,
                    super.yCoord,
                    super.zCoord,
                    new ItemStack(RedPowerBase.blockMicro, 1, td)
                );
            }

            this.ConSides &= 63;
        } else {
            if ((this.ConSides & 1 << part) <= 0) {
                super.onHarvestPart(player, part, willHarvest);
                return;
            }

            if (willHarvest) {
                CoreLib.dropItem(
                    super.worldObj,
                    super.xCoord,
                    super.yCoord,
                    super.zCoord,
                    new ItemStack(
                        RedPowerBase.blockMicro,
                        1,
                        this.getExtendedID() * 256 + this.Metadata
                    )
                );
            }

            this.ConSides &= ~(1 << part);
        }

        this.uncache();
        if (this.ConSides == 0) {
            if (super.CoverSides > 0) {
                this.replaceWithCovers();
            } else {
                this.deleteBlock();
            }
        }

        CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
        RedPowerLib.updateIndirectNeighbors(
            super.worldObj,
            super.xCoord,
            super.yCoord,
            super.zCoord,
            RedPowerBase.blockMicro
        );
    }

    @Override
    public float getPartStrength(EntityPlayer player, int part) {
        BlockMicro bl = RedPowerBase.blockMicro;
        return part == 29 && (this.ConSides & 64) > 0
            ? player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F)
            : ((this.ConSides & 1 << part) > 0
                   ? player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F)
                   : super.getPartStrength(player, part));
    }

    @Override
    public void setPartBounds(BlockMultipart block, int part) {
        if (part == 29) {
            if ((this.ConSides & 64) == 0) {
                super.setPartBounds(block, part);
                return;
            }
        } else if ((this.ConSides & 1 << part) == 0) {
            super.setPartBounds(block, part);
            return;
        }

        float wh = this.getWireHeight();
        switch (part) {
            case 0:
                block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, wh, 1.0F);
                break;
            case 1:
                block.setBlockBounds(0.0F, 1.0F - wh, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, wh);
                break;
            case 3:
                block.setBlockBounds(0.0F, 0.0F, 1.0F - wh, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                block.setBlockBounds(0.0F, 0.0F, 0.0F, wh, 1.0F, 1.0F);
                break;
            case 5:
                block.setBlockBounds(1.0F - wh, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 29:
                block.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.ConSides = data.getByte("cons") & 255;
        this.Metadata = data.getByte("md") & 255;
        this.CenterPost = (short) (data.getByte("post") & 255);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("cons", (byte) this.ConSides);
        data.setByte("md", (byte) this.Metadata);
        data.setShort("post", this.CenterPost);
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setInteger("md", this.Metadata);
        tag.setInteger("cons", this.ConSides);
        if ((this.ConSides & 64) > 0) {
            tag.setShort("post", this.CenterPost);
        }

        super.writeFramePacket(tag);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.Metadata = tag.getInteger("md");
        this.ConSides = tag.getInteger("cons");
        if ((this.ConSides & 64) > 0) {
            this.CenterPost = tag.getShort("post");
        }

        this.ConaMask = -1;
        this.EConMask = -1;
        this.EConEMask = -1;
        this.ConMask = -1;
        super.readFramePacket(tag);
    }

    @Override
    protected void readFromPacket(NBTTagCompound data) {
        this.Metadata = data.getInteger("md");
        this.ConSides = data.getInteger("cons");
        if ((this.ConSides & 64) > 0) {
            this.CenterPost = data.getShort("post");
        }

        this.ConaMask = -1;
        this.EConMask = -1;
        this.EConEMask = -1;
        this.ConMask = -1;
        super.readFromPacket(data);
    }

    @Override
    protected void writeToPacket(NBTTagCompound data) {
        data.setInteger("md", this.Metadata);
        data.setInteger("cons", this.ConSides);
        if ((this.ConSides & 64) > 0) {
            data.setShort("post", this.CenterPost);
        }

        super.writeToPacket(data);
    }

    @Override
    protected ItemStack getBasePickStack() {
        if ((this.ConSides & 64) > 0) {
            int td = 16384 + this.CenterPost;
            if (this.getExtendedID() == 3) {
                td += 256;
            }

            if (this.getExtendedID() == 5) {
                td += 512;
            }

            return new ItemStack(RedPowerBase.blockMicro, 1, td);
        } else {
            return new ItemStack(
                RedPowerBase.blockMicro, 1, this.getExtendedID() * 256 + this.Metadata
            );
        }
    }
}
