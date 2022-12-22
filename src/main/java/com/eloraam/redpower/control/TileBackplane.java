package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.TileMultipart;
import com.eloraam.redpower.core.WorldCoord;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class TileBackplane extends TileMultipart implements IFrameSupport {
   public int Rotation = 0;

   public int readBackplane(int addr) {
      return 255;
   }

   public void writeBackplane(int addr, int val) {
   }

   public Block getBlockType() {
      return RedPowerControl.blockBackplane;
   }

   @Override
   public int getExtendedID() {
      return 0;
   }

   @Override
   public void onBlockNeighborChange(Block block) {
      if (!super.worldObj
         .getBlock(super.xCoord, super.yCoord - 1, super.zCoord)
         .isSideSolid(super.worldObj, super.xCoord, super.yCoord - 1, super.zCoord, ForgeDirection.UP)) {
         this.breakBlock();
      } else {
         WorldCoord wc = new WorldCoord(this);
         wc.step(CoreLib.rotToSide(this.Rotation) ^ 1);
         Block bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
         int md = super.worldObj.getBlockMetadata(wc.x, wc.y, wc.z);
         if (bid != RedPowerControl.blockBackplane && (bid != RedPowerControl.blockPeripheral || md != 1)) {
            this.breakBlock();
         }
      }

   }

   @Override
   public void addHarvestContents(List<ItemStack> ist) {
      ist.add(new ItemStack(RedPowerControl.blockBackplane, 1, 0));
   }

   @Override
   public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
      this.breakBlock(willHarvest);
   }

   @Override
   public float getPartStrength(EntityPlayer player, int part) {
      return 0.1F;
   }

   @Override
   public boolean blockEmpty() {
      return false;
   }

   @Override
   public void setPartBounds(BlockMultipart block, int part) {
      block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
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
   public void writeFramePacket(NBTTagCompound tag) {
      tag.setByte("rot", (byte)this.Rotation);
   }

   @Override
   public void readFramePacket(NBTTagCompound tag) {
      this.Rotation = tag.getByte("rot");
   }

   @Override
   public void onFrameRefresh(IBlockAccess iba) {
   }

   @Override
   public void onFramePickup(IBlockAccess iba) {
   }

   @Override
   public void onFrameDrop() {
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.Rotation = data.getByte("rot");
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      data.setByte("rot", (byte)this.Rotation);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      this.Rotation = tag.getByte("rot");
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      tag.setByte("rot", (byte)this.Rotation);
   }
}
