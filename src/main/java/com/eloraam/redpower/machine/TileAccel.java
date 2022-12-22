package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.ITubeFlow;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileAccel extends TileMachinePanel implements IBluePowerConnectable, ITubeFlow {
   TubeFlow flow = new TubeFlow() {
      @Override
      public TileEntity getParent() {
         return TileAccel.this;
      }

      @Override
      public boolean schedule(TubeItem item, TubeFlow.TubeScheduleContext context) {
         item.scheduled = true;
         item.progress = 0;
         item.side = (byte)(item.side ^ 1);
         TileAccel.this.recache();
         item.power = 0;
         if ((
               item.side == TileAccel.super.Rotation && (TileAccel.this.conCache & 2) > 0
                  || item.side == (TileAccel.super.Rotation ^ 1) && (TileAccel.this.conCache & 8) > 0
            )
            && TileAccel.this.cond.getVoltage() >= 60.0) {
            TileAccel.this.cond.drawPower((double)(100 * item.item.stackSize));
            item.power = 255;
         }

         return true;
      }
   };
   BluePowerEndpoint cond = new BluePowerEndpoint() {
      @Override
      public TileEntity getParent() {
         return TileAccel.this;
      }
   };
   private boolean hasChanged = false;
   public int ConMask = -1;
   public int conCache = -1;

   @Override
   public int getTubeConnectableSides() {
      return 3 << (super.Rotation & 6);
   }

   @Override
   public int getTubeConClass() {
      return 17;
   }

   @Override
   public boolean canRouteItems() {
      return true;
   }

   @Override
   public boolean tubeItemEnter(int side, int state, TubeItem item) {
      if (state != 0) {
         return false;
      } else if (side != super.Rotation && side != (super.Rotation ^ 1)) {
         return false;
      } else {
         item.side = (byte)side;
         this.flow.add(item);
         this.hasChanged = true;
         this.markDirty();
         return true;
      }
   }

   @Override
   public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
      return state == 0;
   }

   @Override
   public int tubeWeight(int side, int state) {
      return 0;
   }

   @Override
   public void addTubeItem(TubeItem ti) {
      ti.side = (byte)(ti.side ^ 1);
      this.flow.add(ti);
      this.hasChanged = true;
      this.markDirty();
   }

   @Override
   public TubeFlow getTubeFlow() {
      return this.flow;
   }

   @Override
   public int getPartMaxRotation(int part, boolean sec) {
      return sec ? 0 : 5;
   }

   @Override
   public int getLightValue() {
      return super.Charged ? 6 : 0;
   }

   public void recache() {
      if (this.conCache < 0) {
         WorldCoord wc = new WorldCoord(this);
         ITubeConnectable fw = CoreLib.getTileEntity(super.worldObj, wc.coordStep(super.Rotation), ITubeConnectable.class);
         ITubeConnectable bw = CoreLib.getTileEntity(super.worldObj, wc.coordStep(super.Rotation ^ 1), ITubeConnectable.class);
         this.conCache = 0;
         if (fw != null) {
            int mcl = fw.getTubeConClass();
            if (mcl < 17) {
               this.conCache |= 1;
            } else if (mcl >= 17) {
               this.conCache |= 2;
            }
         }

         if (bw != null) {
            int mcl = bw.getTubeConClass();
            if (mcl < 17) {
               this.conCache |= 4;
            } else if (mcl >= 17) {
               this.conCache |= 8;
            }
         }
      }

   }

   @Override
   public int getConnectableMask() {
      return 1073741823;
   }

   @Override
   public int getConnectClass(int side) {
      return 65;
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
   public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
      this.flow.onRemove();
      this.breakBlock(willHarvest);
   }

   @Override
   public int getExtendedID() {
      return 2;
   }

   @Override
   public void updateEntity() {
      super.updateEntity();
      if (this.flow.update()) {
         this.hasChanged = true;
      }

      if (this.hasChanged) {
         if (!super.worldObj.isRemote) {
            this.markForUpdate();
         }

         this.markDirty();
      }

      if (!super.worldObj.isRemote) {
         if (this.ConMask < 0) {
            this.ConMask = RedPowerLib.getConnections(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord);
            this.cond.recache(this.ConMask, 0);
         }

         this.cond.iterate();
         this.markDirty();
         if (this.cond.Flow == 0) {
            if (super.Charged) {
               super.Charged = false;
               this.updateBlock();
               this.updateLight();
            }
         } else if (!super.Charged) {
            super.Charged = true;
            this.updateBlock();
            this.updateLight();
         }
      }

   }

   @Override
   public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
      super.Rotation = this.getFacing(ent);
      RedPowerLib.updateIndirectNeighbors(super.worldObj, super.xCoord, super.yCoord, super.zCoord, super.blockType);
      if (ent instanceof EntityPlayer) {
         super.Owner = ((EntityPlayer)ent).getGameProfile();
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      this.ConMask = -1;
      this.conCache = -1;
      this.updateBlock();
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.cond.readFromNBT(data);
      this.flow.readFromNBT(data);
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      this.cond.writeToNBT(data);
      this.flow.writeToNBT(data);
   }

   @Override
   protected void writeToPacket(NBTTagCompound data) {
      int cs = this.flow.contents.size();
      if (cs > 6) {
         cs = 6;
      }

      data.setInteger("cs", cs);
      Iterator<TubeItem> tii = this.flow.contents.iterator();

      for(int i = 0; i < cs; ++i) {
         TubeItem ti = (TubeItem)tii.next();
         NBTTagCompound itag = new NBTTagCompound();
         ti.writeToPacket(itag);
         data.setTag("cs" + i, itag);
      }

      if (this.hasChanged) {
         this.hasChanged = false;
         data.setBoolean("data", true);
         super.writeToPacket(data);
      }

   }

   @Override
   protected void readFromPacket(NBTTagCompound data) {
      this.flow.contents.clear();
      int cs = data.getInteger("cs");

      for(int i = 0; i < cs; ++i) {
         this.flow.contents.add(TubeItem.newFromPacket((NBTTagCompound)data.getTag("cs" + i)));
      }

      if (data.hasKey("data")) {
         super.readFromPacket(data);
      }

   }
}
