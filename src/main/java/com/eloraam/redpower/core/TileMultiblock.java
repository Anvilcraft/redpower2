package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerBase;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileMultiblock extends TileEntity {
   public int relayX;
   public int relayY;
   public int relayZ;
   public int relayNum;

   public boolean canUpdate() {
      return true;
   }

   public Block getBlockType() {
      return RedPowerBase.blockMultiblock;
   }

   public void markDirty() {
      super.markDirty();
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.relayX = tag.getInteger("rlx");
      this.relayY = tag.getInteger("rly");
      this.relayZ = tag.getInteger("rlz");
      this.relayNum = tag.getInteger("rln");
   }

   public void writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      tag.setInteger("rlx", this.relayX);
      tag.setInteger("rly", this.relayY);
      tag.setInteger("rlz", this.relayZ);
      tag.setInteger("rln", this.relayNum);
   }

   protected void readFromPacket(NBTTagCompound tag) {
      this.relayX = tag.getInteger("rlx");
      this.relayY = tag.getInteger("rly");
      this.relayZ = tag.getInteger("rlz");
      this.relayNum = tag.getInteger("rln");
   }

   protected void writeToPacket(NBTTagCompound tag) {
      tag.setInteger("rlx", this.relayX);
      tag.setInteger("rly", this.relayY);
      tag.setInteger("rlz", this.relayZ);
      tag.setInteger("rln", this.relayNum);
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound syncData = new NBTTagCompound();
      this.writeToPacket(syncData);
      return new S35PacketUpdateTileEntity(super.xCoord, super.yCoord, super.zCoord, 1, syncData);
   }

   public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet) {
      this.readFromPacket(packet.func_148857_g());
   }
}
