package com.eloraam.redpower.logic;

import java.util.List;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCoverable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileLogic
    extends TileCoverable implements IRedPowerConnectable, IRotatable, IFrameSupport {
    public int SubId = 0;
    public int Rotation = 0;
    public boolean Powered = false;
    public boolean Disabled = false;
    public boolean Active = false;
    public int PowerState = 0;
    public int Deadmap = 0;
    public int Cover = 255;

    @Override
    public int getPartMaxRotation(int part, boolean sec) {
        return sec ? 0 : (part != this.Rotation >> 2 ? 0 : 3);
    }

    @Override
    public int getPartRotation(int part, boolean sec) {
        return sec ? 0 : (part != this.Rotation >> 2 ? 0 : this.Rotation & 3);
    }

    @Override
    public void setPartRotation(int part, boolean sec, int rot) {
        if (!sec && part == this.Rotation >> 2) {
            this.Rotation = rot & 3 | this.Rotation & -4;
            this.updateBlockChange();
        }
    }

    @Override
    public int getConnectableMask() {
        return 15 << (this.Rotation & -4);
    }

    @Override
    public int getConnectClass(int side) {
        return 0;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public int getPoweringMask(int ch) {
        return ch != 0 ? 0
                       : (this.Powered ? RedPowerLib.mapRotToCon(8, this.Rotation) : 0);
    }

    @Override
    public boolean canAddCover(int side, int cover) {
        return this.Cover == 255 && (side ^ 1) == this.Rotation >> 2 && cover <= 254;
    }

    @Override
    public boolean tryAddCover(int side, int cover) {
        if (!this.canAddCover(side, cover)) {
            return false;
        } else {
            this.Cover = cover;
            this.updateBlock();
            return true;
        }
    }

    @Override
    public int tryRemoveCover(int side) {
        if (this.Cover == 255) {
            return -1;
        } else if ((side ^ 1) != this.Rotation >> 2) {
            return -1;
        } else {
            int tr = this.Cover;
            this.Cover = 255;
            this.updateBlock();
            return tr;
        }
    }

    @Override
    public int getCover(int side) {
        return this.Cover == 255 ? -1
                                 : ((side ^ 1) != this.Rotation >> 2 ? -1 : this.Cover);
    }

    @Override
    public int getCoverMask() {
        return this.Cover == 255 ? 0 : 1 << (this.Rotation >> 2 ^ 1);
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void addHarvestContents(List<ItemStack> drops) {
        super.addHarvestContents(drops);
        drops.add(
            new ItemStack(this.getBlockType(), 1, this.getExtendedID() * 256 + this.SubId)
        );
    }

    private void replaceWithCovers(boolean shouldDrop) {
        if (this.Cover != 255) {
            short[] covers = new short[26];
            covers[this.Rotation >> 2 ^ 1] = (short) this.Cover;
            CoverLib.replaceWithCovers(
                super.worldObj,
                super.xCoord,
                super.yCoord,
                super.zCoord,
                1 << (this.Rotation >> 2 ^ 1),
                covers
            );
            if (shouldDrop) {
                CoreLib.dropItem(
                    super.worldObj,
                    super.xCoord,
                    super.yCoord,
                    super.zCoord,
                    new ItemStack(
                        this.getBlockType(), 1, this.getExtendedID() * 256 + this.SubId
                    )
                );
            }

            this.markForUpdate();
        } else {
            this.breakBlock(shouldDrop);
            RedPowerLib.updateIndirectNeighbors(
                super.worldObj,
                super.xCoord,
                super.yCoord,
                super.zCoord,
                this.getBlockType()
            );
        }
    }

    public boolean tryDropBlock() {
        if (RedPowerLib.canSupportWire(
                super.worldObj,
                super.xCoord,
                super.yCoord,
                super.zCoord,
                this.Rotation >> 2
            )) {
            return false;
        } else {
            this.replaceWithCovers(true);
            return true;
        }
    }

    @Override
    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
        if (part == this.Rotation >> 2) {
            this.replaceWithCovers(willHarvest);
        } else {
            super.onHarvestPart(player, part, willHarvest);
        }
    }

    @Override
    public float getPartStrength(EntityPlayer player, int part) {
        BlockLogic bl = RedPowerLogic.blockLogic;
        return part == this.Rotation >> 2
            ? player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F)
            : super.getPartStrength(player, part);
    }

    @Override
    public void setPartBounds(BlockMultipart block, int part) {
        if (part != this.Rotation >> 2) {
            super.setPartBounds(block, part);
        } else {
            switch (part) {
                case 0:
                    block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
                    break;
                case 1:
                    block.setBlockBounds(0.0F, 0.875F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 2:
                    block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
                    break;
                case 3:
                    block.setBlockBounds(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F);
                    break;
                case 4:
                    block.setBlockBounds(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
                    break;
                case 5:
                    block.setBlockBounds(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public int getPartsMask() {
        int pm = 1 << (this.Rotation >> 2);
        if (this.Cover != 255) {
            pm |= 1 << (this.Rotation >> 2 ^ 1);
        }

        return pm;
    }

    @Override
    public int getSolidPartsMask() {
        return this.getPartsMask();
    }

    @Override
    public boolean isBlockStrongPoweringTo(int l) {
        return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(l ^ 1)) > 0;
    }

    @Override
    public boolean isBlockWeakPoweringTo(int l) {
        return (this.getPoweringMask(0) & RedPowerLib.getConDirMask(l ^ 1)) > 0;
    }

    public Block getBlockType() {
        return RedPowerLogic.blockLogic;
    }

    @Override
    public int getExtendedMetadata() {
        return this.SubId;
    }

    @Override
    public void setExtendedMetadata(int md) {
        this.SubId = md;
    }

    public void playSound(String name, float f, float f2, boolean always) {
        if (always || RedPowerLogic.soundsEnabled) {
            super.worldObj.playSoundEffect(
                (double) ((float) super.xCoord + 0.5F),
                (double) ((float) super.yCoord + 0.5F),
                (double) ((float) super.zCoord + 0.5F),
                name,
                f,
                f2
            );
        }
    }

    public void initSubType(int st) {
        this.SubId = st;
        if (!super.worldObj.isRemote && this.getLightValue() != 9) {
            CoreLib.updateAllLightTypes(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord
            );
        }
    }

    public int getLightValue() {
        return 9;
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setByte("sid", (byte) this.SubId);
        tag.setByte("rot", (byte) this.Rotation);
        int ps = this.PowerState | (this.Powered ? 16 : 0) | (this.Disabled ? 32 : 0)
            | (this.Active ? 64 : 0) | (this.Deadmap > 0 ? 128 : 0);
        tag.setByte("ps", (byte) ps);
        if (this.Deadmap > 0) {
            tag.setByte("dm", (byte) this.Deadmap);
        }

        tag.setShort("cov", (short) this.Cover);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.SubId = tag.getByte("sid");
        this.Rotation = tag.getByte("rot");
        int ps = tag.getByte("ps");
        if (super.worldObj.isRemote) {
            this.PowerState = ps & 15;
            this.Powered = (ps & 16) > 0;
            this.Disabled = (ps & 32) > 0;
            this.Active = (ps & 64) > 0;
        }

        if ((ps & 128) > 0) {
            this.Deadmap = tag.getByte("dm");
        } else {
            this.Deadmap = 0;
        }

        this.Cover = tag.getShort("cov");
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
        this.SubId = data.getByte("sid") & 255;
        this.Rotation = data.getByte("rot") & 255;
        int ps = data.getByte("ps") & 255;
        this.Deadmap = data.getByte("dm") & 255;
        this.Cover = data.getByte("cov") & 255;
        this.PowerState = ps & 15;
        this.Powered = (ps & 16) > 0;
        this.Disabled = (ps & 32) > 0;
        this.Active = (ps & 64) > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("sid", (byte) this.SubId);
        data.setByte("rot", (byte) this.Rotation);
        int ps = this.PowerState | (this.Powered ? 16 : 0) | (this.Disabled ? 32 : 0)
            | (this.Active ? 64 : 0);
        data.setByte("ps", (byte) ps);
        data.setByte("dm", (byte) this.Deadmap);
        data.setByte("cov", (byte) this.Cover);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        this.SubId = tag.getByte("sid");
        this.Rotation = tag.getByte("rot");
        int ps = tag.getByte("ps");
        if (super.worldObj.isRemote) {
            this.PowerState = ps & 15;
            this.Powered = (ps & 16) > 0;
            this.Disabled = (ps & 32) > 0;
            this.Active = (ps & 64) > 0;
        }

        if ((ps & 128) > 0) {
            this.Deadmap = tag.getByte("dm");
        } else {
            this.Deadmap = 0;
        }

        this.Cover = tag.getShort("cov");
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setByte("sid", (byte) this.SubId);
        tag.setByte("rot", (byte) this.Rotation);
        int ps = this.PowerState | (this.Powered ? 16 : 0) | (this.Disabled ? 32 : 0)
            | (this.Active ? 64 : 0) | (this.Deadmap > 0 ? 128 : 0);
        tag.setByte("ps", (byte) ps);
        if (this.Deadmap > 0) {
            tag.setByte("dm", (byte) this.Deadmap);
        }

        tag.setShort("cov", (short) this.Cover);
    }

    @Override
    protected ItemStack getBasePickStack() {
        return new ItemStack(
            this.getBlockType(), 1, this.getExtendedID() * 256 + this.SubId
        );
    }
}
