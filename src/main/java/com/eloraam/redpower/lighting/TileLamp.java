package com.eloraam.redpower.lighting;

import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;

public class TileLamp extends TileExtended implements IFrameSupport {
   public boolean Powered = false;
   public boolean Inverted = false;
   public int Color = 0;

   private void updateLight() {
      super.worldObj.updateLightByType(EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
   public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
      this.onBlockNeighborChange(Blocks.air);
      this.Inverted = (ist.getItemDamage() & 16) > 0;
      this.Color = ist.getItemDamage() & 15;
      if (ent instanceof EntityPlayer) {
         super.Owner = ((EntityPlayer)ent).getGameProfile();
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      if (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63)) {
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
      ItemStack is = new ItemStack(this.getBlockType(), 1, (this.Inverted ? 16 : 0) + this.Color);
      ist.add(is);
   }

   @Override
   public void writeFramePacket(NBTTagCompound tag) {
      int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
      tag.setByte("ps", (byte)ps);
      tag.setByte("color", (byte)this.Color);
   }

   @Override
   public void readFramePacket(NBTTagCompound tag) {
      byte ps = tag.getByte("ps");
      this.Powered = (ps & 1) > 0;
      this.Inverted = (ps & 2) > 0;
      this.Color = tag.getByte("color");
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
      byte ps = data.getByte("ps");
      this.Powered = (ps & 1) > 0;
      this.Inverted = (ps & 2) > 0;
      this.Color = data.getByte("color");
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
      data.setByte("ps", (byte)ps);
      data.setByte("color", (byte)this.Color);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      byte ps = tag.getByte("ps");
      this.Powered = (ps & 1) > 0;
      this.Inverted = (ps & 2) > 0;
      this.Color = tag.getByte("color");
      this.updateBlock();
      this.updateLight();
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      int ps = (this.Powered ? 1 : 0) | (this.Inverted ? 2 : 0);
      tag.setByte("ps", (byte)ps);
      tag.setByte("color", (byte)this.Color);
   }

   public boolean shouldRenderInPass(int pass) {
      return true;
   }
}
