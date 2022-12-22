package com.eloraam.redpower.wiring;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.nbt.NBTTagCompound;

public class TileInsulatedWire extends TileWiring implements IRedPowerWiring {
   public short PowerState = 0;

   @Override
   public float getWireHeight() {
      return 0.188F;
   }

   @Override
   public int getExtendedID() {
      return 2;
   }

   @Override
   public boolean isBlockWeakPoweringTo(int side) {
      if (RedPowerLib.isSearching()) {
         return false;
      } else {
         int dir = RedPowerLib.getConDirMask(side ^ 1);
         dir &= this.getConnectableMask();
         if (dir != 0) {
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
   }

   @Override
   public int getConnectClass(int side) {
      return 2 + super.Metadata;
   }

   @Override
   public int scanPoweringStrength(int cons, int ch) {
      return RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, cons, 0) ? 255 : 0;
   }

   @Override
   public int getCurrentStrength(int cons, int ch) {
      return ch != 0 && ch != super.Metadata + 1 ? -1 : ((cons & this.getConnectableMask()) == 0 ? -1 : this.PowerState);
   }

   @Override
   public void updateCurrentStrength() {
      this.PowerState = (short)RedPowerLib.updateBlockCurrentStrength(
         super.worldObj, this, super.xCoord, super.yCoord, super.zCoord, 16777215, 1 | 2 << super.Metadata
      );
      CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
   public int getPoweringMask(int ch) {
      return this.PowerState == 0 ? 0 : (ch != 0 && ch != super.Metadata + 1 ? 0 : this.getConnectableMask());
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
      super.readFromPacket(data);
   }

   @Override
   protected void writeToPacket(NBTTagCompound data) {
      data.setByte("pwr", (byte)this.PowerState);
      super.writeToPacket(data);
   }
}
