package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.TileExtended;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileAppliance extends TileExtended implements IFrameSupport {
    public int Rotation = 0;
    public boolean Active = false;

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        this.Rotation
            = (int) Math.floor((double) (ent.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    public Block getBlockType() {
        return RedPowerBase.blockAppliance;
    }

    public int getLightValue() {
        return this.Active ? 13 : 0;
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("ps", (byte) (this.Active ? 1 : 0));
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        this.Active = tag.getByte("ps") > 0;
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
        this.Active = data.getByte("ps") > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("ps", (byte) (this.Active ? 1 : 0));
        data.setByte("rot", (byte) this.Rotation);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        this.Rotation = tag.getByte("rot");
        this.Active = tag.getByte("ps") > 0;
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("ps", (byte) (this.Active ? 1 : 0));
    }
}
