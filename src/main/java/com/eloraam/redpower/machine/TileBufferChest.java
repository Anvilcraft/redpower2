package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.base.TileAppliance;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRotatable;
import java.util.stream.IntStream;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.ArrayUtils;

public class TileBufferChest extends TileAppliance implements IInventory, ISidedInventory, IRotatable {
   private ItemStack[] contents = new ItemStack[20];

   @Override
   public int getExtendedID() {
      return 2;
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
            player.openGui(RedPowerMachine.instance, 4, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   public int getFacing(EntityLivingBase ent) {
      int yawrx = (int)Math.floor((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
      if (Math.abs(ent.posX - (double)super.xCoord) < 2.0 && Math.abs(ent.posZ - (double)super.zCoord) < 2.0) {
         double p = ent.posY + 1.82 - (double)ent.yOffset - (double)super.yCoord;
         if (p > 2.0) {
            return 0;
         }

         if (p < 0.0) {
            return 1;
         }
      }

      switch(yawrx) {
         case 0:
            return 3;
         case 1:
            return 4;
         case 2:
            return 2;
         default:
            return 5;
      }
   }

   @Override
   public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
      super.Rotation = this.getFacing(ent);
      if (ent instanceof EntityPlayer) {
         super.Owner = ((EntityPlayer)ent).getGameProfile();
      }

   }

   @Override
   public void onBlockRemoval() {
      for(int i = 0; i < 20; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   @Override
   public int getPartMaxRotation(int part, boolean sec) {
      return sec ? 0 : 5;
   }

   @Override
   public int getPartRotation(int part, boolean sec) {
      return sec ? 0 : super.Rotation;
   }

   @Override
   public void setPartRotation(int part, boolean sec, int rot) {
      if (!sec) {
         super.Rotation = rot;
         this.updateBlockChange();
      }

   }

   public int getSizeInventory() {
      return 20;
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
      return "tile.rpbuffer.name";
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

      for(int i = 0; i < items.tagCount(); ++i) {
         NBTTagCompound item = items.getCompoundTagAt(i);
         int j = item.getByte("Slot") & 255;
         if (j >= 0 && j < this.contents.length) {
            this.contents[j] = ItemStack.loadItemStackFromNBT(item);
         }
      }

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
      boolean isFront = side == (super.Rotation ^ 1);
      int start = isFront ? 0 : 4 * ((5 + side - (super.Rotation ^ 1)) % 6);
      int end = isFront ? 20 : start + 4;
      return IntStream.range(start, end).toArray();
   }

   public boolean canInsertItem(int slotID, ItemStack stack, int side) {
      int[] slots = this.getAccessibleSlotsFromSide(side);
      return ArrayUtils.contains(slots, slotID);
   }

   public boolean canExtractItem(int slotID, ItemStack stack, int side) {
      int[] slots = this.getAccessibleSlotsFromSide(side);
      return ArrayUtils.contains(slots, slotID);
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public boolean isItemValidForSlot(int slotID, ItemStack stack) {
      return true;
   }
}
