package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileMultipart;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileIOExpander extends TileMultipart implements IRedbusConnectable, IRedPowerConnectable, IFrameSupport {
   public int Rotation = 0;
   public int WBuf = 0;
   public int WBufNew = 0;
   public int RBuf = 0;
   private int rbaddr = 3;

   @Override
   public int rbGetAddr() {
      return this.rbaddr;
   }

   @Override
   public void rbSetAddr(int addr) {
      this.rbaddr = addr;
   }

   @Override
   public int rbRead(int reg) {
      switch(reg) {
         case 0:
            return this.RBuf & 0xFF;
         case 1:
            return this.RBuf >> 8;
         case 2:
            return this.WBufNew & 0xFF;
         case 3:
            return this.WBufNew >> 8;
         default:
            return 0;
      }
   }

   @Override
   public void rbWrite(int reg, int dat) {
      this.markDirty();
      switch(reg) {
         case 0:
         case 2:
            this.WBufNew = this.WBufNew & 0xFF00 | dat;
            this.scheduleTick(2);
            break;
         case 1:
         case 3:
            this.WBufNew = this.WBufNew & 0xFF | dat << 8;
            this.scheduleTick(2);
      }

   }

   @Override
   public int getConnectableMask() {
      return 15;
   }

   @Override
   public int getConnectClass(int side) {
      return side == CoreLib.rotToSide(this.Rotation) ? 18 : 66;
   }

   @Override
   public int getCornerPowerMode() {
      return 0;
   }

   @Override
   public int getPoweringMask(int ch) {
      return ch == 0 ? 0 : ((this.WBuf & 1 << ch - 1) > 0 ? RedPowerLib.mapRotToCon(8, this.Rotation) : 0);
   }

   @Override
   public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
      this.Rotation = (int)Math.floor((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5) + 1 & 3;
      if (ent instanceof EntityPlayer) {
         super.Owner = ((EntityPlayer)ent).getGameProfile();
      }

   }

   @Override
   public void onTileTick() {
      if (this.WBuf != this.WBufNew) {
         this.WBuf = this.WBufNew;
         this.onBlockNeighborChange(Blocks.air);
         this.updateBlockChange();
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      boolean ch = false;

      for(int n = 0; n < 16; ++n) {
         int ps = RedPowerLib.getRotPowerState(super.worldObj, super.xCoord, super.yCoord, super.zCoord, 8, this.Rotation, n + 1);
         if (ps == 0) {
            if ((this.RBuf & 1 << n) > 0) {
               this.RBuf &= ~(1 << n);
               ch = true;
            }
         } else if ((this.RBuf & 1 << n) == 0) {
            this.RBuf |= 1 << n;
            ch = true;
         }
      }

      if (ch) {
         this.updateBlock();
      }

   }

   public Block getBlockType() {
      return RedPowerControl.blockFlatPeripheral;
   }

   @Override
   public int getExtendedID() {
      return 0;
   }

   @Override
   public void addHarvestContents(List<ItemStack> ist) {
      ist.add(new ItemStack(this.getBlockType(), 1, 0));
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
      block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
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
      tag.setShort("wb", (short)this.WBuf);
   }

   @Override
   public void readFramePacket(NBTTagCompound tag) {
      this.Rotation = tag.getByte("rot");
      this.WBuf = tag.getShort("wb");
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
      this.WBuf = data.getShort("wb");
      this.WBufNew = data.getShort("wbn");
      this.RBuf = data.getShort("rb");
      this.rbaddr = data.getByte("rbaddr") & 255;
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      data.setByte("rot", (byte)this.Rotation);
      data.setShort("wb", (short)this.WBuf);
      data.setShort("wbn", (short)this.WBufNew);
      data.setShort("rb", (short)this.RBuf);
      data.setByte("rbaddr", (byte)this.rbaddr);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      this.Rotation = tag.getByte("rot");
      this.WBuf = tag.getShort("wb");
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      tag.setByte("rot", (byte)this.Rotation);
      tag.setShort("wb", (short)this.WBuf);
   }
}
