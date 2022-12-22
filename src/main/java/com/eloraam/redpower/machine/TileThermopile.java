package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileExtended;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileThermopile extends TileExtended implements IBluePowerConnectable {
   BluePowerConductor cond = new BluePowerConductor() {
      @Override
      public TileEntity getParent() {
         return TileThermopile.this;
      }

      @Override
      public double getInvCap() {
         return 4.0;
      }
   };
   public int tempHot = 0;
   public int tempCold = 0;
   public int ticks = 0;
   public int ConMask = -1;

   @Override
   public int getConnectableMask() {
      return 1073741823;
   }

   @Override
   public int getConnectClass(int side) {
      return 64;
   }

   @Override
   public int getCornerPowerMode() {
      return 0;
   }

   @Override
   public BluePowerConductor getBlueConductor(int side) {
      return this.cond;
   }

   @Override
   public int getExtendedID() {
      return 11;
   }

   public Block getBlockType() {
      return RedPowerMachine.blockMachine;
   }

   private void updateTemps() {
      int hot = 0;
      int cold = 0;

      for(int side = 0; side < 6; ++side) {
         WorldCoord wc = new WorldCoord(this);
         wc.step(side);
         Block bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
         if (super.worldObj.isAirBlock(wc.x, wc.y, wc.z)) {
            if (super.worldObj.provider.isHellWorld) {
               ++hot;
            } else {
               ++cold;
            }
         } else if (bid == Blocks.snow) {
            cold += 100;
         } else if (bid == Blocks.ice) {
            cold += 100;
         } else if (bid == Blocks.snow_layer) {
            cold += 50;
         } else if (bid == Blocks.torch) {
            hot += 5;
         } else if (bid == Blocks.lit_pumpkin) {
            hot += 3;
         } else if (bid == Blocks.flowing_water || bid == Blocks.water) {
            cold += 25;
         } else if (bid == Blocks.flowing_lava || bid == Blocks.lava) {
            hot += 100;
         } else if (bid == Blocks.fire) {
            hot += 25;
         }
      }

      if (this.tempHot >= 100 && this.tempCold >= 200) {
         for(int side = 0; side < 6; ++side) {
            WorldCoord wc = new WorldCoord(this);
            wc.step(side);
            Block bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
            if ((bid == Blocks.flowing_lava || bid == Blocks.lava) && super.worldObj.rand.nextInt(300) == 0) {
               int md = super.worldObj.getBlockMetadata(wc.x, wc.y, wc.z);
               super.worldObj.setBlock(wc.x, wc.y, wc.z, (Block)(md == 0 ? Blocks.obsidian : RedPowerWorld.blockStone), md > 0 ? 1 : 0, 3);
               break;
            }
         }
      }

      if (this.tempHot >= 100) {
         for(int side = 0; side < 6; ++side) {
            if (super.worldObj.rand.nextInt(300) == 0) {
               WorldCoord wc = new WorldCoord(this);
               wc.step(side);
               Block bid = super.worldObj.getBlock(wc.x, wc.y, wc.z);
               if (bid == Blocks.snow_layer) {
                  super.worldObj.setBlockToAir(wc.x, wc.y, wc.z);
                  break;
               }

               if (bid == Blocks.ice || bid == Blocks.snow) {
                  super.worldObj.setBlock(wc.x, wc.y, wc.z, (Block)(super.worldObj.provider.isHellWorld ? Blocks.air : Blocks.flowing_water), 0, 3);
                  break;
               }
            }
         }
      }

      this.tempHot = hot;
      this.tempCold = cold;
   }

   @Override
   public void updateEntity() {
      super.updateEntity();
      if (!super.worldObj.isRemote) {
         if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord);
            this.cond.recache(this.ConMask, 0);
         }

         this.cond.iterate();
         this.markDirty();
         if (this.cond.getVoltage() <= 100.0) {
            ++this.ticks;
            if (this.ticks > 20) {
               this.ticks = 0;
               this.updateTemps();
            }

            int diff = Math.min(this.tempHot, this.tempCold);
            this.cond.applyDirect(0.005 * (double)diff);
         }
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      this.ConMask = -1;
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.cond.readFromNBT(data);
      this.tempHot = data.getShort("hot");
      this.tempCold = data.getShort("cold");
      this.ticks = data.getByte("ticks");
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      this.cond.writeToNBT(data);
      data.setShort("hot", (short)this.tempHot);
      data.setShort("cold", (short)this.tempCold);
      data.setByte("ticks", (byte)this.ticks);
   }
}
