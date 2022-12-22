package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CraftLib;
import java.util.stream.IntStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.EnumSkyBlock;

public class TileAlloyFurnace extends TileAppliance implements IInventory, ISidedInventory {
   private ItemStack[] contents = new ItemStack[11];
   int totalburn = 0;
   int burntime = 0;
   int cooktime = 0;

   private void updateLight() {
      super.worldObj.updateLightByType(EnumSkyBlock.Block, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
   public int getExtendedID() {
      return 0;
   }

   @Override
   public void updateEntity() {
      super.updateEntity();
      boolean btu = false;
      if (this.burntime > 0) {
         --this.burntime;
         if (this.burntime == 0) {
            btu = true;
            super.Active = false;
         }
      }

      if (!super.worldObj.isRemote) {
         boolean cs = this.canSmelt();
         if (this.burntime == 0 && cs && this.contents[9] != null) {
            this.burntime = this.totalburn = CoreLib.getBurnTime(this.contents[9]);
            if (this.burntime > 0) {
               super.Active = true;
               if (this.contents[9].getItem().getContainerItem() != null) {
                  this.contents[9] = new ItemStack(this.contents[9].getItem().getContainerItem());
               } else {
                  --this.contents[9].stackSize;
               }

               if (this.contents[9].stackSize == 0) {
                  this.contents[9] = null;
               }

               if (!btu) {
                  this.updateBlock();
                  this.updateLight();
               }
            }
         }

         if (this.burntime > 0 && cs) {
            ++this.cooktime;
            if (this.cooktime == 200) {
               this.cooktime = 0;
               this.smeltItem();
               this.markDirty();
            }
         } else {
            this.cooktime = 0;
         }

         if (btu) {
            this.updateBlock();
            this.updateLight();
         }
      }

   }

   private boolean canSmelt() {
      ItemStack ist = CraftLib.getAlloyResult(this.contents, 0, 9, false);
      if (ist == null) {
         return false;
      } else if (this.contents[10] == null) {
         return true;
      } else if (!this.contents[10].isItemEqual(ist)) {
         return false;
      } else {
         int st = this.contents[10].stackSize + ist.stackSize;
         return st <= this.getInventoryStackLimit() && st <= ist.getMaxStackSize();
      }
   }

   private void smeltItem() {
      if (this.canSmelt()) {
         ItemStack ist = CraftLib.getAlloyResult(this.contents, 0, 9, true);
         if (this.contents[10] == null) {
            this.contents[10] = ist.copy();
         } else {
            this.contents[10].stackSize += ist.stackSize;
         }
      }

   }

   int getCookScaled(int i) {
      return this.cooktime * i / 200;
   }

   int getBurnScaled(int i) {
      return this.totalburn == 0 ? 0 : this.burntime * i / this.totalburn;
   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!super.worldObj.isRemote) {
            player.openGui(RedPowerBase.instance, 1, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   @Override
   public void onBlockRemoval() {
      for(int i = 0; i < 11; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   public int getSizeInventory() {
      return 11;
   }

   public ItemStack getStackInSlot(int slotId) {
      return this.contents[slotId];
   }

   public ItemStack decrStackSize(int slotId, int amount) {
      if (this.contents[slotId] == null) {
         return null;
      } else if (this.contents[slotId].stackSize <= amount) {
         ItemStack tr = this.contents[slotId];
         this.contents[slotId] = null;
         this.markDirty();
         return tr;
      } else {
         ItemStack tr = this.contents[slotId].splitStack(amount);
         if (this.contents[slotId].stackSize == 0) {
            this.contents[slotId] = null;
         }

         this.markDirty();
         return tr;
      }
   }

   public ItemStack getStackInSlotOnClosing(int slotId) {
      if (this.contents[slotId] == null) {
         return null;
      } else {
         ItemStack ist = this.contents[slotId];
         this.contents[slotId] = null;
         return ist;
      }
   }

   public void setInventorySlotContents(int slotId, ItemStack ist) {
      this.contents[slotId] = ist;
      if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
         ist.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public String getInventoryName() {
      return "tile.rpafurnace.name";
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
      NBTTagList items = data.getTagList("Items", 10);
      this.contents = new ItemStack[this.getSizeInventory()];

      for(int i = 0; i < items.tagCount(); ++i) {
         NBTTagCompound item = items.getCompoundTagAt(i);
         int j = item.getByte("Slot") & 255;
         if (j >= 0 && j < this.contents.length) {
            this.contents[j] = ItemStack.loadItemStackFromNBT(item);
         }
      }

      this.totalburn = data.getShort("TotalBurn");
      this.burntime = data.getShort("BurnTime");
      this.cooktime = data.getShort("CookTime");
      super.readFromNBT(data);
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
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
      data.setShort("TotalBurn", (short)this.totalburn);
      data.setShort("BurnTime", (short)this.burntime);
      data.setShort("CookTime", (short)this.cooktime);
      super.writeToNBT(data);
   }

   public int[] getAccessibleSlotsFromSide(int side) {
      switch(side) {
         case 0:
            return new int[]{10};
         case 1:
            return IntStream.range(0, 9).toArray();
         default:
            return side != (super.Rotation ^ 1) ? new int[]{9} : new int[0];
      }
   }

   public boolean canInsertItem(int slotID, ItemStack stack, int side) {
      switch(side) {
         case 1:
            return slotID >= 0 && slotID < 9;
         default:
            return side != (super.Rotation ^ 1) ? TileEntityFurnace.isItemFuel(stack) : false;
      }
   }

   public boolean canExtractItem(int slotID, ItemStack stack, int side) {
      return slotID == 10 && side == 0;
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public boolean isItemValidForSlot(int slotID, ItemStack stack) {
      return TileEntityFurnace.isItemFuel(stack) && slotID == 9 || slotID >= 0 && slotID < 9;
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      super.readFromPacket(tag);
      this.updateLight();
   }
}
