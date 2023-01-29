package com.eloraam.redpower.lighting;

import java.util.List;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;

public class TileShapedLamp extends TileExtended implements IFrameSupport, IConnectable {
    public int Rotation = 0;
    public boolean Powered = false;
    public boolean Inverted = false;
    public int Style = 0;
    public int Color = 0;

    private void updateLight() {
        super.worldObj.updateLightByType(
            EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord
        );
    }

    @Override
    public int getConnectableMask() {
        return 16777216 << this.Rotation | 15 << (this.Rotation << 2);
    }

    @Override
    public int getConnectClass(int side) {
        return 1;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        this.Rotation = side ^ 1;
        this.onBlockNeighborChange(Blocks.air);
        this.Inverted = (ist.getItemDamage() & 16) > 0;
        this.Color = ist.getItemDamage() & 15;
        this.Style = (ist.getItemDamage() & 1023) >> 5;
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    public Block getBlockType() {
        return RedPowerLighting.blockShapedLamp;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        int mask = this.getConnectableMask();
        if (RedPowerLib.isPowered(
                super.worldObj,
                super.xCoord,
                super.yCoord,
                super.zCoord,
                mask & 16777215,
                mask >> 24
            )) {
            if (this.Powered) {
                return;
            }

            this.Powered = true;
            this.updateLight();
            this.updateBlock();
        } else {
            if (!this.Powered) {
                return;
            }

            this.Powered = false;
            this.updateLight();
            this.updateBlock();
        }
    }

    public int getLightValue() {
        return this.Powered != this.Inverted ? 15 : 0;
    }

    @Override
    public void addHarvestContents(List<ItemStack> ist) {
        ItemStack is = new ItemStack(
            this.getBlockType(),
            1,
            (this.Style << 5) + (this.Inverted ? 16 : 0) + this.Color
        );
        ist.add(is);
    }

    @Override
    public void writeFramePacket(NBTTagCompound tag) {
        int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
        tag.setByte("ps", (byte) ps);
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("color", (byte) this.Color);
        tag.setByte("style", (byte) this.Style);
    }

    @Override
    public void readFramePacket(NBTTagCompound tag) {
        byte ps = tag.getByte("ps");
        this.Rotation = tag.getByte("rot");
        this.Powered = (ps & 1) > 0;
        this.Inverted = (ps & 2) > 0;
        this.Color = tag.getByte("color");
        this.Style = tag.getByte("style");
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
        byte ps = data.getByte("ps");
        this.Rotation = data.getByte("rot");
        this.Powered = (ps & 1) > 0;
        this.Inverted = (ps & 2) > 0;
        this.Color = data.getByte("color");
        this.Style = data.getByte("style");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
        data.setByte("ps", (byte) ps);
        data.setByte("rot", (byte) this.Rotation);
        data.setByte("color", (byte) this.Color);
        data.setByte("style", (byte) this.Style);
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        byte ps = tag.getByte("ps");
        this.Rotation = tag.getByte("rot");
        this.Powered = (ps & 1) > 0;
        this.Inverted = (ps & 2) > 0;
        this.Color = tag.getByte("color");
        this.Style = tag.getByte("style");
        this.updateBlock();
        this.updateLight();
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
        tag.setByte("ps", (byte) ps);
        tag.setByte("rot", (byte) this.Rotation);
        tag.setByte("color", (byte) this.Color);
        tag.setByte("style", (byte) this.Style);
    }

    public boolean shouldRenderInPass(int pass) {
        return true;
    }
}
