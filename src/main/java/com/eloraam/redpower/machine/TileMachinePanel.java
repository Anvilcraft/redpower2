package com.eloraam.redpower.machine;

import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRotatable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileMultipart;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;

public class TileMachinePanel extends TileMultipart implements IRotatable, IFrameSupport {
    public int Rotation = 0;
    public boolean Active = false;
    public boolean Powered = false;
    public boolean Delay = false;
    public boolean Charged = false;

    public int getLightValue() {
        return 0;
    }

    void updateLight() {
        super.worldObj.updateLightByType(
            EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord
        );
    }

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
        this.Rotation
            = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
        RedPowerLib.updateIndirectNeighbors(
            super.worldObj, super.xCoord, super.yCoord, super.zCoord, super.blockType
        );
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    public Block getBlockType() {
        return RedPowerMachine.blockMachinePanel;
    }

    @Override
    public void addHarvestContents(List<ItemStack> ist) {
        ist.add(new ItemStack(this.getBlockType(), 1, this.getExtendedID()));
    }

    @Override
    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
        this.breakBlock(willHarvest);
    }

    @Override
    public float getPartStrength(EntityPlayer player, int part) {
        BlockMachinePanel bl = RedPowerMachine.blockMachinePanel;
        return player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F);
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void setPartBounds(BlockMultipart block, int part) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
        return sec ? 0 : 3;
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
        tag.setByte("rot", (byte) this.Rotation);
        int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0)
            | (this.Charged ? 8 : 0);
        tag.setByte("ps", (byte) ps);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        int ps = tag.getByte("ps");
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
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        byte k = data.getByte("ps");
        this.Rotation = data.getByte("rot");
        this.Active = (k & 1) > 0;
        this.Powered = (k & 2) > 0;
        this.Delay = (k & 4) > 0;
        this.Charged = (k & 8) > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0)
            | (this.Charged ? 8 : 0);
        data.setByte("ps", (byte) ps);
        data.setByte("rot", (byte) this.Rotation);
    }

    @Override
    protected void readFromPacket(NBTTagCompound data) {
        this.Rotation = data.getByte("rot");
        int ps = data.getByte("ps");
        this.Active = (ps & 1) > 0;
        this.Powered = (ps & 2) > 0;
        this.Delay = (ps & 4) > 0;
        this.Charged = (ps & 8) > 0;
        this.updateLight();
    }

    @Override
    protected void writeToPacket(NBTTagCompound data) {
        data.setByte("rot", (byte) this.Rotation);
        int ps = (this.Active ? 1 : 0) | (this.Powered ? 2 : 0) | (this.Delay ? 4 : 0)
            | (this.Charged ? 8 : 0);
        data.setByte("ps", (byte) ps);
    }
}
