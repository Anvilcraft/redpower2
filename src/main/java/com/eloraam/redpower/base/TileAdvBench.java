package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;
import java.util.stream.IntStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileAdvBench extends TileAppliance implements ISidedInventory {
   private ItemStack[] contents = new ItemStack[28];

   @Override
   public int getExtendedID() {
      return 3;
   }

   public boolean canUpdate() {
      return true;
   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!super.worldObj.isRemote) {
            player.openGui(RedPowerBase.instance, 2, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   @Override
   public void onBlockRemoval() {
      for(int i = 0; i < 27; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   public int getSizeInventory() {
      return 28;
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
      return "tile.rpabench.name";
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      TileEntity tile = super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord);
      return tile == this
         && tile != null
         && !tile.isInvalid()
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

      for(int i = 0; i < items.tagCount(); ++i) {
         NBTTagCompound item = items.getCompoundTagAt(i);
         int j = item.getByte("Slot") & 255;
         if (j >= 0 && j < this.contents.length) {
            this.contents[j] = ItemStack.loadItemStackFromNBT(item);
         }
      }

      this.markDirty();
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
   }

   public int[] getAccessibleSlotsFromSide(int side) {
      switch(side) {
         case 1:
            return IntStream.range(0, 9).toArray();
         default:
            return side != (super.Rotation ^ 1) ? IntStream.range(10, 28).toArray() : new int[0];
      }
   }

   public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
      return side != (super.Rotation ^ 1) && slotID >= 0 && slotID < 28 && slotID != 9;
   }

   public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
      return side == 0 && slotID >= 10 && slotID < 28;
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public boolean isItemValidForSlot(int slotID, ItemStack stack) {
      return slotID != 9 || stack.getItem() == RedPowerBase.itemPlanBlank || stack.getItem() == RedPowerBase.itemPlanFull;
   }
}
