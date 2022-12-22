package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IChargeable;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileChargingBench extends TileAppliance implements IInventory, IBluePowerConnectable {
   BluePowerEndpoint cond = new BluePowerEndpoint() {
      @Override
      public TileEntity getParent() {
         return TileChargingBench.this;
      }
   };
   public boolean Powered = false;
   public int Storage = 0;
   private ItemStack[] contents = new ItemStack[16];
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
   public int getLightValue() {
      return 0;
   }

   @Override
   public int getExtendedID() {
      return 5;
   }

   public int getMaxStorage() {
      return 3000;
   }

   public int getStorageForRender() {
      return this.Storage * 4 / this.getMaxStorage();
   }

   public int getChargeScaled(int i) {
      return Math.min(i, i * this.cond.Charge / 1000);
   }

   public int getStorageScaled(int i) {
      return Math.min(i, i * this.Storage / this.getMaxStorage());
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
         if (this.cond.Flow == 0) {
            if (this.Powered) {
               this.Powered = false;
               this.updateBlock();
            }
         } else if (!this.Powered) {
            this.Powered = true;
            this.updateBlock();
         }

         int rs = this.getStorageForRender();
         if (this.cond.Charge > 600 && this.Storage < this.getMaxStorage()) {
            int lastact = Math.min((this.cond.Charge - 600) / 40, 5);
            lastact = Math.min(lastact, this.getMaxStorage() - this.Storage);
            this.cond.drawPower((double)(lastact * 1000));
            this.Storage += lastact;
         }

         boolean var5 = super.Active;
         super.Active = false;
         if (this.Storage > 0) {
            for(int i = 0; i < 16; ++i) {
               if (this.contents[i] != null && this.contents[i].getItem() instanceof IChargeable && this.contents[i].getItemDamage() > 1) {
                  int d = Math.min(this.contents[i].getItemDamage() - 1, this.Storage);
                  d = Math.min(d, 25);
                  this.contents[i].setItemDamage(this.contents[i].getItemDamage() - d);
                  this.Storage -= d;
                  this.markDirty();
                  super.Active = true;
               }
            }
         }

         if (rs != this.getStorageForRender() || var5 != super.Active) {
            this.updateBlock();
         }
      }

   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!super.worldObj.isRemote) {
            player.openGui(RedPowerMachine.instance, 14, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   @Override
   public void onBlockRemoval() {
      for(int i = 0; i < 2; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      this.ConMask = -1;
   }

   public int getSizeInventory() {
      return 16;
   }

   public ItemStack getStackInSlot(int i) {
      return this.contents[i];
   }

   public ItemStack decrStackSize(int i, int j) {
      if (this.contents[i] == null) {
         return null;
      } else if (this.contents[i].stackSize <= j) {
         ItemStack tr = this.contents[i];
         this.contents[i] = null;
         this.markDirty();
         return tr;
      } else {
         ItemStack tr = this.contents[i].splitStack(j);
         if (this.contents[i].stackSize == 0) {
            this.contents[i] = null;
         }

         this.markDirty();
         return tr;
      }
   }

   public ItemStack getStackInSlotOnClosing(int i) {
      if (this.contents[i] == null) {
         return null;
      } else {
         ItemStack ist = this.contents[i];
         this.contents[i] = null;
         return ist;
      }
   }

   public void setInventorySlotContents(int i, ItemStack ist) {
      this.contents[i] = ist;
      if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
         ist.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public String getInventoryName() {
      return "tile.rpcharge.name";
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return !this.isInvalid()
         && super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord) == this
         && player.getDistanceSq((double)super.xCoord + 0.5, (double)super.yCoord + 0.5, (double)super.zCoord + 0.5) <= 64.0;
   }

   public void closeInventory() {
   }

   public void openInventory() {
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      NBTTagList items = data.getTagList("Items", 10);
      this.contents = new ItemStack[this.getSizeInventory()];

      for(int k = 0; k < items.tagCount(); ++k) {
         NBTTagCompound item = items.getCompoundTagAt(k);
         int j = item.getByte("Slot") & 255;
         if (j >= 0 && j < this.contents.length) {
            this.contents[j] = ItemStack.loadItemStackFromNBT(item);
         }
      }

      this.cond.readFromNBT(data);
      this.Storage = data.getShort("stor");
      byte var6 = data.getByte("ps");
      this.Powered = (var6 & 1) > 0;
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      NBTTagList items = new NBTTagList();

      for(int i = 0; i < this.contents.length; ++i) {
         if (this.contents[i] != null) {
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte)i);
            this.contents[i].writeToNBT(item);
            items.appendTag(item);
         }
      }

      data.setTag("Items", items);
      this.cond.writeToNBT(data);
      data.setShort("stor", (short)this.Storage);
      int ps = this.Powered ? 1 : 0;
      data.setByte("ps2", (byte)ps);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      super.Rotation = tag.getByte("rot");
      int ps = tag.getByte("ps");
      super.Active = (ps & 1) > 0;
      this.Powered = (ps & 2) > 0;
      this.Storage = tag.getInteger("stor");
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      tag.setByte("rot", (byte)super.Rotation);
      tag.setByte("ps", (byte)((super.Active ? 1 : 0) | (this.Powered ? 2 : 0)));
      tag.setInteger("stor", this.Storage);
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
      return itemStack.getItem() instanceof IChargeable;
   }
}
