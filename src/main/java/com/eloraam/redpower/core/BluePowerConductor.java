package com.eloraam.redpower.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BluePowerConductor {
   private static int[] dirmap = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 11, 14, 18, 23};
   int imask = 0;
   double[] currents;
   public double Vcap = 0.0;
   public double Icap = 0.0;
   public double Veff = 0.0;
   int lastTick = 0;
   public double It1 = 0.0;
   public double Itot = 0.0;

   public abstract TileEntity getParent();

   public abstract double getInvCap();

   public int getChargeScaled(int i) {
      return 0;
   }

   public int getFlowScaled(int i) {
      return 0;
   }

   public double getResistance() {
      return 0.01;
   }

   public double getIndScale() {
      return 0.07;
   }

   public double getCondParallel() {
      return 0.5;
   }

   public void recache(int conm, int econm) {
      int imo = 0;

      for(int c2 = 0; c2 < 3; ++c2) {
         if ((conm & RedPowerLib.getConDirMask(c2 * 2)) > 0) {
            imo |= 1 << c2;
         }
      }

      for(int var12 = 0; var12 < 12; ++var12) {
         if ((econm & 1 << dirmap[var12]) > 0) {
            imo |= 8 << var12;
         }
      }

      if (this.imask != imo) {
         double[] var11 = new double[Integer.bitCount(imo)];
         int s = 0;
         int d = 0;

         for(int a = 0; a < 15; ++a) {
            int m = 1 << a;
            double v = 0.0;
            if ((this.imask & m) > 0) {
               v = this.currents[s++];
            }

            if ((imo & m) > 0) {
               var11[d++] = v;
            }
         }

         this.currents = var11;
         this.imask = imo;
      }

   }

   protected void computeVoltage() {
      this.Itot = 0.5 * this.It1;
      this.It1 = 0.0;
      this.Vcap += 0.05 * this.Icap * this.getInvCap();
      this.Icap = 0.0;
   }

   public double getVoltage() {
      long lt = this.getParent().getWorldObj().getWorldTime();
      if ((lt & 65535L) == (long)this.lastTick) {
         return this.Vcap;
      } else {
         this.lastTick = (int)(lt & 65535L);
         this.computeVoltage();
         return this.Vcap;
      }
   }

   public void applyCurrent(double Iin) {
      this.getVoltage();
      this.Icap += Iin;
      this.It1 += Math.abs(Iin);
   }

   public void drawPower(double P) {
      double p1 = this.Vcap * this.Vcap - 0.1 * P * this.getInvCap();
      double t = p1 < 0.0 ? 0.0 : Math.sqrt(p1) - this.Vcap;
      this.applyDirect(20.0 * t / this.getInvCap());
   }

   public double getEnergy(double vthresh) {
      double d = this.getVoltage();
      double tr = 0.5 * (d * d - vthresh * vthresh) / this.getInvCap();
      return tr < 0.0 ? 0.0 : tr;
   }

   public void applyPower(double P) {
      double t = Math.sqrt(this.Vcap * this.Vcap + 0.1 * P * this.getInvCap()) - this.Vcap;
      this.applyDirect(20.0 * t / this.getInvCap());
   }

   public void applyDirect(double Iin) {
      this.applyCurrent(Iin);
   }

   public void iterate() {
      TileEntity parent = this.getParent();
      World world = parent.getWorldObj();
      this.getVoltage();
      int dm = this.imask;

      for(int s = 0; dm > 0; ++s) {
         int d = Integer.numberOfTrailingZeros(dm);
         dm &= ~(1 << d);
         WorldCoord wc = new WorldCoord(parent);
         int facing;
         if (d < 3) {
            facing = d * 2;
            wc.step(facing);
         } else {
            int ibc = dirmap[d - 3];
            wc.step(ibc >> 2);
            facing = WorldCoord.getIndStepDir(ibc >> 2, ibc & 3);
            wc.step(facing);
         }

         IBluePowerConnectable powerConnectable = CoreLib.getTileEntity(world, wc, IBluePowerConnectable.class);
         if (powerConnectable != null) {
            BluePowerConductor bpc = powerConnectable.getBlueConductor(facing ^ 1);
            double r = this.getResistance() + bpc.getResistance();
            double I = this.currents[s];
            double V = this.Vcap - bpc.getVoltage();
            this.currents[s] += (V - I * r) * this.getIndScale();
            I += V * this.getCondParallel();
            this.applyCurrent(-I);
            bpc.applyCurrent(I);
         }
      }

   }

   public void readFromNBT(NBTTagCompound tag) {
      this.imask = tag.getInteger("bpim");
      int l = Integer.bitCount(this.imask);
      this.currents = new double[l];
      NBTTagList clist = tag.getTagList("bpil", 6);
      if (clist.tagCount() == l) {
         for(int i = 0; i < l; ++i) {
            this.currents[i] = clist.func_150309_d(i);
         }

         this.Vcap = tag.getDouble("vcap");
         this.Icap = tag.getDouble("icap");
         this.Veff = tag.getDouble("veff");
         this.It1 = tag.getDouble("it1");
         this.Itot = tag.getDouble("itot");
         this.lastTick = tag.getInteger("ltk");
      }

   }

   public void writeToNBT(NBTTagCompound tag) {
      tag.setInteger("bpim", this.imask);
      int l = Integer.bitCount(this.imask);
      NBTTagList clist = new NBTTagList();

      for(int i = 0; i < l; ++i) {
         NBTTagDouble val = new NBTTagDouble(this.currents[i]);
         clist.appendTag(val);
      }

      tag.setTag("bpil", clist);
      tag.setDouble("vcap", this.Vcap);
      tag.setDouble("icap", this.Icap);
      tag.setDouble("veff", this.Veff);
      tag.setDouble("it1", this.It1);
      tag.setDouble("itot", this.Itot);
      tag.setInteger("ltk", this.lastTick);
   }
}
