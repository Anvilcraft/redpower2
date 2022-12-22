package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileLogicStorage extends TileLogic {
   TileLogicStorage.LogicStorageModule storage = null;

   @Override
   public int getExtendedID() {
      return 3;
   }

   @Override
   public void initSubType(int st) {
      super.initSubType(st);
      this.initStorage();
   }

   public TileLogicStorage.LogicStorageModule getLogicStorage(Class cl) {
      if (!cl.isInstance(this.storage)) {
         this.initStorage();
      }

      return this.storage;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return (this.isInvalid() || super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord) == this)
         && player.getDistanceSq((double)super.xCoord + 0.5, (double)super.yCoord + 0.5, (double)super.zCoord + 0.5) <= 64.0;
   }

   @Override
   public int getPartMaxRotation(int part, boolean sec) {
      if (sec) {
         switch(super.SubId) {
            case 0:
               return 1;
         }
      }

      return super.getPartMaxRotation(part, sec);
   }

   @Override
   public int getPartRotation(int part, boolean sec) {
      if (sec) {
         switch(super.SubId) {
            case 0:
               return super.Deadmap;
         }
      }

      return super.getPartRotation(part, sec);
   }

   @Override
   public void setPartRotation(int part, boolean sec, int rot) {
      if (sec) {
         switch(super.SubId) {
            case 0:
               super.Deadmap = rot;
               this.updateBlockChange();
               return;
         }
      }

      super.setPartRotation(part, sec, rot);
   }

   void initStorage() {
      if (this.storage == null || this.storage.getSubType() != super.SubId) {
         switch(super.SubId) {
            case 0:
               this.storage = new TileLogicStorage.LogicStorageCounter();
               break;
            default:
               this.storage = null;
         }
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      if (!this.tryDropBlock()) {
         this.initStorage();
         switch(super.SubId) {
            case 0:
               if (this.isTickRunnable()) {
                  return;
               }

               this.storage.updatePowerState();
         }
      }

   }

   @Override
   public void onTileTick() {
      this.initStorage();
      this.storage.tileTick();
   }

   @Override
   public int getPoweringMask(int ch) {
      this.initStorage();
      return this.storage.getPoweringMask(ch);
   }

   @Override
   public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
      if (part == super.Rotation >> 2 && !player.isSneaking()) {
         if (!super.worldObj.isRemote) {
            switch(super.SubId) {
               case 0:
                  player.openGui(RedPowerLogic.instance, 1, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.initStorage();
      this.storage.readFromNBT(data);
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      this.storage.writeToNBT(data);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      this.initStorage();
      this.storage.readFromPacket(tag);
      super.readFromPacket(tag);
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      this.storage.writeToPacket(tag);
      super.writeToPacket(tag);
   }

   public class LogicStorageCounter extends TileLogicStorage.LogicStorageModule {
      public int Count = 0;
      public int CountMax = 10;
      public int Inc = 1;
      public int Dec = 1;

      @Override
      public void updatePowerState() {
         int ps = RedPowerLib.getRotPowerState(
            TileLogicStorage.this.worldObj,
            TileLogicStorage.this.xCoord,
            TileLogicStorage.this.yCoord,
            TileLogicStorage.this.zCoord,
            5,
            TileLogicStorage.super.Rotation,
            0
         );
         if (ps != TileLogicStorage.this.PowerState) {
            if ((ps & ~TileLogicStorage.this.PowerState & 1) > 0) {
               TileLogicStorage.this.Active = true;
            }

            if ((ps & ~TileLogicStorage.this.PowerState & 4) > 0) {
               TileLogicStorage.this.Disabled = true;
            }

            TileLogicStorage.this.PowerState = ps;
            TileLogicStorage.this.updateBlock();
            if (TileLogicStorage.this.Active || TileLogicStorage.this.Disabled) {
               TileLogicStorage.this.scheduleTick(2);
            }
         }

      }

      @Override
      public void tileTick() {
         int co = this.Count;
         if (TileLogicStorage.this.Deadmap > 0) {
            if (TileLogicStorage.this.Active) {
               this.Count -= this.Dec;
               TileLogicStorage.this.Active = false;
            }

            if (TileLogicStorage.this.Disabled) {
               this.Count += this.Inc;
               TileLogicStorage.this.Disabled = false;
            }
         } else {
            if (TileLogicStorage.this.Active) {
               this.Count += this.Inc;
               TileLogicStorage.this.Active = false;
            }

            if (TileLogicStorage.this.Disabled) {
               this.Count -= this.Dec;
               TileLogicStorage.this.Disabled = false;
            }
         }

         if (this.Count < 0) {
            this.Count = 0;
         }

         if (this.Count > this.CountMax) {
            this.Count = this.CountMax;
         }

         if (co != this.Count) {
            TileLogicStorage.this.updateBlockChange();
            TileLogicStorage.this.playSound("random.click", 0.3F, 0.5F, false);
         }

         this.updatePowerState();
      }

      @Override
      public int getSubType() {
         return 0;
      }

      @Override
      public int getPoweringMask(int ch) {
         int ps = 0;
         if (ch != 0) {
            return 0;
         } else {
            if (this.Count == 0) {
               ps |= 2;
            }

            if (this.Count == this.CountMax) {
               ps |= 8;
            }

            return RedPowerLib.mapRotToCon(ps, TileLogicStorage.super.Rotation);
         }
      }

      @Override
      public void readFromNBT(NBTTagCompound tag) {
         this.Count = tag.getInteger("cnt");
         this.CountMax = tag.getInteger("max");
         this.Inc = tag.getInteger("inc");
         this.Dec = tag.getInteger("dec");
      }

      @Override
      public void writeToNBT(NBTTagCompound tag) {
         tag.setInteger("cnt", this.Count);
         tag.setInteger("max", this.CountMax);
         tag.setInteger("inc", this.Inc);
         tag.setInteger("dec", this.Dec);
      }

      @Override
      public void readFromPacket(NBTTagCompound tag) {
         this.Count = tag.getInteger("cnt");
         this.CountMax = tag.getInteger("max");
      }

      @Override
      public void writeToPacket(NBTTagCompound tag) {
         tag.setInteger("cnt", this.Count);
         tag.setInteger("max", this.CountMax);
      }
   }

   public abstract class LogicStorageModule {
      public abstract void updatePowerState();

      public abstract void tileTick();

      public abstract int getSubType();

      public abstract int getPoweringMask(int var1);

      public abstract void readFromNBT(NBTTagCompound var1);

      public abstract void writeToNBT(NBTTagCompound var1);

      public void readFromPacket(NBTTagCompound tag) {
      }

      public void writeToPacket(NBTTagCompound tag) {
      }
   }
}
