package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public abstract class TileExtended extends TileEntity {
    protected long timeSched = -1L;
    public GameProfile Owner = CoreLib.REDPOWER_PROFILE;

    public void onBlockNeighborChange(Block block) {}

    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        this.updateBlock();
    }

    public void onBlockRemoval() {}

    public boolean isBlockStrongPoweringTo(int side) {
        return false;
    }

    public boolean isBlockWeakPoweringTo(int side) {
        return this.isBlockStrongPoweringTo(side);
    }

    public boolean onBlockActivated(EntityPlayer player) {
        return false;
    }

    public void onEntityCollidedWithBlock(Entity ent) {}

    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    public void onTileTick() {}

    public int getExtendedID() {
        return 0;
    }

    public int getExtendedMetadata() {
        return 0;
    }

    public void setExtendedMetadata(int md) {}

    public void addHarvestContents(List<ItemStack> ist) {
        ist.add(new ItemStack(this.getBlockType(), 1, this.getExtendedID()));
    }

    public void scheduleTick(int time) {
        long tn = super.worldObj.getTotalWorldTime() + (long) time;
        if (this.timeSched <= 0L || this.timeSched >= tn) {
            this.timeSched = tn;
            this.updateBlock();
        }
    }

    public boolean isTickRunnable() {
        return this.timeSched >= 0L && this.timeSched <= super.worldObj.getTotalWorldTime();
    }

    public boolean isTickScheduled() {
        return this.timeSched >= 0L;
    }

    public void updateBlockChange() {
        RedPowerLib.updateIndirectNeighbors(
            super.worldObj, super.xCoord, super.yCoord, super.zCoord, this.getBlockType()
        );
        this.updateBlock();
    }

    public void updateBlock() {
        this.markDirty();
        this.markForUpdate();
    }

    public void markForUpdate() {
        super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
    }

    public void breakBlock() {
        this.breakBlock(true);
    }

    public void breakBlock(boolean shouldDrop) {
        if (shouldDrop) {
            List<ItemStack> il = new ArrayList();
            this.addHarvestContents(il);

            for (ItemStack it : il) {
                CoreLib.dropItem(
                    super.worldObj, super.xCoord, super.yCoord, super.zCoord, it
                );
            }
        }

        super.worldObj.setBlockToAir(super.xCoord, super.yCoord, super.zCoord);
    }

    @Override
    public void updateEntity() {
        if (!super.worldObj.isRemote && this.timeSched >= 0L) {
            long wtime = super.worldObj.getTotalWorldTime();
            if (this.timeSched > wtime + 1200L) {
                this.timeSched = wtime + 1200L;
            } else if (this.timeSched <= wtime) {
                this.timeSched = -1L;
                this.onTileTick();
                this.markDirty();
            }
        }
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.timeSched = data.getLong("sched");
        if (data.hasKey("Owner")) {
            this.Owner = NBTUtil.func_152459_a(data.getCompoundTag("Owner"));
        } else {
            this.Owner = CoreLib.REDPOWER_PROFILE;
        }
    }

    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setLong("sched", this.timeSched);
        NBTTagCompound owner = new NBTTagCompound();
        NBTUtil.func_152460_a(owner, this.Owner);
        data.setTag("Owner", owner);
    }

    public Packet getDescriptionPacket() {
        NBTTagCompound syncData = new NBTTagCompound();
        this.writeToPacket(syncData);
        return new S35PacketUpdateTileEntity(
            super.xCoord, super.yCoord, super.zCoord, 1, syncData
        );
    }

    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(
            (double) super.xCoord,
            (double) super.yCoord,
            (double) super.zCoord,
            (double) super.xCoord + 1.0,
            (double) super.yCoord + 1.0,
            (double) super.zCoord + 1.0
        );
    }

    public void
    onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet) {
        this.readFromPacket(packet.func_148857_g());
        this.updateBlock();
    }

    protected void writeToPacket(NBTTagCompound tag) {}

    protected void readFromPacket(NBTTagCompound tag) {}

    public double getMaxRenderDistanceSquared() {
        return 65535.0;
    }
}
