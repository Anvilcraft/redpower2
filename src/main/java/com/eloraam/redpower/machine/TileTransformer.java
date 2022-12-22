package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileTransformer extends TileMachinePanel implements IBluePowerConnectable {
   BluePowerEndpoint cond = new BluePowerEndpoint() {
      @Override
      public TileEntity getParent() {
         return TileTransformer.this;
      }
   };
   BluePowerEndpoint cond2 = new BluePowerEndpoint() {
      @Override
      public TileEntity getParent() {
         return TileTransformer.this;
      }

      @Override
      public double getResistance() {
         return 1.0;
      }

      @Override
      public double getIndScale() {
         return 7.0E-4;
      }

      @Override
      public double getCondParallel() {
         return 0.005;
      }

      @Override
      public double getInvCap() {
         return 25.0;
      }

      @Override
      protected void computeVoltage() {
         super.Vcap = TileTransformer.this.cond.getVoltage() * 100.0;
         super.Itot = TileTransformer.this.cond.Itot * 0.01;
         super.It1 = 0.0;
         super.Icap = 0.0;
      }

      @Override
      public void applyCurrent(double Iin) {
         TileTransformer.this.cond.applyCurrent(Iin * 100.0);
      }
   };
   public int ConMask1 = -1;
   public int ConMask2 = -1;

   @Override
   public int getPartMaxRotation(int part, boolean sec) {
      return sec ? 0 : 3;
   }

   @Override
   public int getPartRotation(int part, boolean sec) {
      return sec ? 0 : super.Rotation & 3;
   }

   @Override
   public void setPartRotation(int part, boolean sec, int rot) {
      if (!sec) {
         super.Rotation = rot & 3 | super.Rotation & -4;
         this.updateBlockChange();
      }

   }

   @Override
   public int getConnectableMask() {
      return RedPowerLib.mapRotToCon(5, super.Rotation);
   }

   @Override
   public int getConnectClass(int side) {
      int s = RedPowerLib.mapRotToCon(1, super.Rotation);
      return (s & RedPowerLib.getConDirMask(side)) > 0 ? 64 : 68;
   }

   @Override
   public int getCornerPowerMode() {
      return 0;
   }

   @Override
   public BluePowerConductor getBlueConductor(int side) {
      return (RedPowerLib.mapRotToCon(1, super.Rotation) & RedPowerLib.getConDirMask(side)) > 0 ? this.cond : this.cond2;
   }

   @Override
   public int getExtendedID() {
      return 4;
   }

   @Override
   public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
      super.Rotation = (side ^ 1) << 2;
      int yaw = (int)Math.floor((double)(ent.rotationYaw / 90.0F + 0.5F));
      int pitch = (int)Math.floor((double)(ent.rotationPitch / 90.0F + 0.5F));
      yaw &= 3;
      int down = super.Rotation >> 2;
      int rot;
      switch(down) {
         case 0:
            rot = yaw;
            break;
         case 1:
            rot = yaw ^ (yaw & 1) << 1;
            break;
         case 2:
            rot = (yaw & 1) > 0 ? (pitch > 0 ? 2 : 0) : 1 - yaw & 3;
            break;
         case 3:
            rot = (yaw & 1) > 0 ? (pitch > 0 ? 2 : 0) : yaw - 1 & 3;
            break;
         case 4:
            rot = (yaw & 1) == 0 ? (pitch > 0 ? 2 : 0) : yaw - 2 & 3;
            break;
         case 5:
            rot = (yaw & 1) == 0 ? (pitch > 0 ? 2 : 0) : 2 - yaw & 3;
            break;
         default:
            rot = 0;
      }

      super.Rotation = down << 2 | rot;
      if (ent instanceof EntityPlayer) {
         super.Owner = ((EntityPlayer)ent).getGameProfile();
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      this.ConMask1 = -1;
      this.ConMask2 = -1;
   }

   @Override
   public void updateEntity() {
      super.updateEntity();
      if (!super.worldObj.isRemote) {
         if (this.ConMask1 < 0) {
            int cm1 = RedPowerLib.getConnections(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord);
            this.ConMask1 = cm1 & RedPowerLib.mapRotToCon(1, super.Rotation);
            this.ConMask2 = cm1 & RedPowerLib.mapRotToCon(4, super.Rotation);
            this.cond.recache(this.ConMask1, 0);
            this.cond2.recache(this.ConMask2, 0);
         }

         this.cond.iterate();
         this.cond2.iterate();
         this.markDirty();
      }

   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      NBTTagCompound c1 = data.getCompoundTag("c1");
      this.cond.readFromNBT(c1);
      NBTTagCompound c2 = data.getCompoundTag("c2");
      this.cond2.readFromNBT(c2);
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      NBTTagCompound c1 = new NBTTagCompound();
      this.cond.writeToNBT(c1);
      NBTTagCompound c2 = new NBTTagCompound();
      this.cond2.writeToNBT(c2);
      data.setTag("c1", c1);
      data.setTag("c2", c2);
   }
}
