package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileRedstoneTube extends TileTube implements IRedPowerWiring {
   public short PowerState = 0;
   public int ConMask = -1;

   @Override
   public int getConnectableMask() {
      int tr = 63;

      for(int i = 0; i < 6; ++i) {
         if ((super.CoverSides & 1 << i) > 0 && super.Covers[i] >> 8 < 3) {
            tr &= ~(1 << i);
         }
      }

      return tr << 24;
   }

   @Override
   public int getConnectionMask() {
      if (this.ConMask >= 0) {
         return this.ConMask;
      } else {
         this.ConMask = RedPowerLib.getConnections(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord);
         return this.ConMask;
      }
   }

   @Override
   public int getExtConnectionMask() {
      return 0;
   }

   @Override
   public int getCornerPowerMode() {
      return 0;
   }

   @Override
   public void onFrameRefresh(IBlockAccess iba) {
      if (this.ConMask < 0) {
         this.ConMask = RedPowerLib.getConnections(iba, this, super.xCoord, super.yCoord, super.zCoord);
      }

   }

   @Override
   public int getConnectClass(int side) {
      return 1;
   }

   @Override
   public int getCurrentStrength(int cons, int ch) {
      return ch != 0 ? -1 : ((cons & this.getConnectableMask()) == 0 ? -1 : this.PowerState);
   }

   @Override
   public int scanPoweringStrength(int cons, int ch) {
      return ch != 0 ? 0 : (RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, cons, this.getConnectionMask()) ? 255 : 0);
   }

   @Override
   public void updateCurrentStrength() {
      this.PowerState = (short)RedPowerLib.updateBlockCurrentStrength(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord, 1073741823, 1);
      CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
   public int getPoweringMask(int ch) {
      return ch == 0 && this.PowerState != 0 ? this.getConnectableMask() : 0;
   }

   @Override
   public void onBlockNeighborChange(Block block) {
      super.onBlockNeighborChange(block);
      if (this.ConMask >= 0) {
         this.markForUpdate();
      }

      this.ConMask = -1;
      RedPowerLib.updateCurrent(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
   public int getExtendedID() {
      return 9;
   }

   @Override
   public boolean isBlockWeakPoweringTo(int side) {
      if (!RedPowerLib.isSearching() && (this.getConnectionMask() & 16777216 << (side ^ 1)) != 0) {
         if (RedPowerLib.isBlockRedstone(super.worldObj, super.xCoord, super.yCoord, super.zCoord, side ^ 1)) {
            if (this.PowerState > 15) {
               return true;
            }
         } else if (this.PowerState > 0) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean tryAddCover(int side, int cover) {
      if (!this.canAddCover(side, cover)) {
         return false;
      } else {
         super.CoverSides |= 1 << side;
         super.Covers[side] = (short)cover;
         this.ConMask = -1;
         this.updateBlockChange();
         return true;
      }
   }

   @Override
   public int tryRemoveCover(int side) {
      if ((super.CoverSides & 1 << side) == 0) {
         return -1;
      } else {
         super.CoverSides &= ~(1 << side);
         short tr = super.Covers[side];
         super.Covers[side] = 0;
         this.ConMask = -1;
         this.updateBlockChange();
         return tr;
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.PowerState = (short)(data.getByte("pwr") & 255);
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      data.setByte("pwr", (byte)this.PowerState);
   }

   @Override
   protected void readFromPacket(NBTTagCompound data) {
      this.PowerState = (short)(data.getByte("pwr") & 255);
      this.ConMask = -1;
      super.readFromPacket(data);
   }

   @Override
   protected void writeToPacket(NBTTagCompound data) {
      data.setByte("pwr", (byte)this.PowerState);
      super.writeToPacket(data);
   }
}
