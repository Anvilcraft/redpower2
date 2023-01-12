package com.eloraam.redpower.compat;

import java.util.List;

import com.eloraam.redpower.RedPowerCompat;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.TileMultipart;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileMachineCompat
    extends TileMultipart implements IRotatable, IFrameSupport {
    public int Rotation = 0;
    public boolean Active = false;
    public boolean Powered = false;
    public boolean Delay = false;
    public boolean Charged = false;

    public int getFacing(EntityLivingBase ent) {
        int yawrx
            = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
        if (Math.abs(ent.posX - (double) super.xCoord) < 2.0
            && Math.abs(ent.posZ - (double) super.zCoord) < 2.0) {
            double p = ent.posY + 1.82 - (double) ent.yOffset - (double) super.yCoord;
            if (p > 2.0) {
                return 0;
            }

            if (p < 0.0) {
                return 1;
            }
        }

        switch (yawrx) {
            case 0:
                return 3;
            case 1:
                return 4;
            case 2:
                return 2;
            default:
                return 5;
        }
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        this.Rotation = this.getFacing(ent);
    }

    public Block getBlockType() {
        return RedPowerCompat.blockMachineCompat;
    }

    @Override
    public void addHarvestContents(List<ItemStack> items) {
        items.add(new ItemStack(this.getBlockType(), 1, this.getExtendedID()));
    }

    @Override
    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
        this.breakBlock(willHarvest);
    }

    @Override
    public float getPartStrength(EntityPlayer player, int part) {
        BlockExtended bl = RedPowerCompat.blockMachineCompat;
        return player.getBreakSpeed(
                   bl,
                   false,
                   super.blockMetadata,
                   super.xCoord,
                   super.yCoord,
                   super.zCoord
               )
            / (bl.getHardness() * 30.0F);
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void setPartBounds(BlockMultipart bl, int part) {
        bl.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getSolidPartsMask() {
        return 1;
    }

    @Override
    public int getPartsMask() {
        return 1;
    }

    @Override
    public int getPartMaxRotation(int part, boolean sec) {
        return sec ? 0 : 5;
    }

    @Override
    public int getPartRotation(int part, boolean sec) {
        return sec ? 0 : this.Rotation;
    }

    @Override
    public void setPartRotation(int part, boolean sec, int rot) {
        if (!sec) {
            this.Rotation = rot;
            this.updateBlockChange();
        }
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0)
            | (this.Charged ? 8 : 0);
        tag.setByte("ps", (byte) ps);
        tag.setByte("rot", (byte) this.Rotation);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        int ps = tag.getByte("ps");
        this.Rotation = tag.getByte("rot");
        this.Active = (ps & 1) > 0;
        this.Powered = (ps & 2) > 0;
        this.Delay = (ps & 4) > 0;
        this.Charged = (ps & 8) > 0;
    }

    @Override
    public void onFrameRefresh(IBlockAccess iba) {}

    @Override
    public void onFramePickup(IBlockAccess iba) {}

    @Override
    public void onFrameDrop() {}

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        int ps = tag.getByte("ps");
        this.Rotation = tag.getByte("rot");
        this.Active = (ps & 1) > 0;
        this.Powered = (ps & 2) > 0;
        this.Delay = (ps & 4) > 0;
        this.Charged = (ps & 8) > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0)
            | (this.Charged ? 8 : 0);
        tag.setByte("ps", (byte) ps);
        tag.setByte("rot", (byte) this.Rotation);
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0)
            | (this.Charged ? 8 : 0);
        tag.setByte("ps", (byte) ps);
        tag.setByte("rot", (byte) this.Rotation);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        int ps = tag.getByte("ps");
        this.Rotation = tag.getByte("rot");
        this.Active = (ps & 1) > 0;
        this.Powered = (ps & 2) > 0;
        this.Delay = (ps & 4) > 0;
        this.Charged = (ps & 8) > 0;
    }
}
