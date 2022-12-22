package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedPowerWiring;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.FakePlayer;

public class TileAssemble extends TileDeployBase implements ISidedInventory, IRedPowerWiring {
   private ItemStack[] contents = new ItemStack[34];
   public byte select = 0;
   public byte mode = 0;
   public int skipSlots = 65534;
   public int ConMask = -1;
   public int PowerState = 0;

   @Override
   public int getExtendedID() {
      return 13;
   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!super.worldObj.isRemote) {
            player.openGui(RedPowerMachine.instance, 11, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   @Override
   public void onBlockRemoval() {
      for(int i = 0; i < 34; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      this.ConMask = -1;
      if (this.mode == 0) {
         super.onBlockNeighborChange(block);
      }

      RedPowerLib.updateCurrent(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
   }

   @Override
   public int getConnectionMask() {
      if (this.ConMask >= 0) {
         return this.ConMask;
      } else {
         this.ConMask = RedPowerLib.getConnections(super.worldObj, this, super.xCoord, super.yCoord, super.zCoord);
         return this.ConMask;
      }
   }

   @Override
   public int getExtConnectionMask() {
      return 0;
   }

   @Override
   public int getPoweringMask(int ch) {
      return 0;
   }

   @Override
   public int scanPoweringStrength(int cons, int ch) {
      return 0;
   }

   @Override
   public int getCurrentStrength(int cons, int ch) {
      return -1;
   }

   @Override
   public void updateCurrentStrength() {
      if (this.mode == 1) {
         for(int slot = 0; slot < 16; ++slot) {
            short wc = (short)RedPowerLib.getMaxCurrentStrength(super.worldObj, super.xCoord, super.yCoord, super.zCoord, 1073741823, 0, slot + 1);
            if (wc > 0) {
               this.PowerState |= 1 << slot;
            } else {
               this.PowerState &= ~(1 << slot);
            }
         }

         CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         if (this.PowerState == 0) {
            if (super.Active) {
               this.scheduleTick(5);
            }
         } else if (!super.Active) {
            super.Active = true;
            this.updateBlock();
            int var41 = Integer.numberOfTrailingZeros(this.PowerState);
            if (this.contents[var41] != null) {
               WorldCoord var4 = new WorldCoord(this);
               var4.step(super.Rotation ^ 1);
               int ms = this.getMatchingStack(var41);
               if (ms >= 0) {
                  this.enableTowardsActive(var4, ms);
               }
            }
         }
      }

   }

   @Override
   public int getConnectClass(int side) {
      return this.mode == 0 ? 0 : 18;
   }

   protected void packInv(ItemStack[] bkup, int act, FakePlayer player) {
      for(int i = 0; i < 36; ++i) {
         bkup[i] = player.inventory.getStackInSlot(i);
         player.inventory.setInventorySlotContents(i, null);
      }

      for(int i = 0; i < 18; ++i) {
         if (act == i) {
            player.inventory.setInventorySlotContents(0, this.contents[16 + i]);
         } else {
            player.inventory.setInventorySlotContents(i + 9, this.contents[16 + i]);
         }
      }

   }

   protected void unpackInv(ItemStack[] bkup, int act, FakePlayer player) {
      for(int i = 0; i < 18; ++i) {
         if (act == i) {
            this.contents[16 + i] = player.inventory.getStackInSlot(0);
         } else {
            this.contents[16 + i] = player.inventory.getStackInSlot(i + 9);
         }
      }

      for(int i = 0; i < 36; ++i) {
         player.inventory.setInventorySlotContents(i, bkup[i]);
      }

   }

   protected int getMatchingStack(int stack) {
      for(int i = 0; i < 18; ++i) {
         ItemStack compareStack = this.contents[16 + i];
         if (this.contents[16 + i] != null && CoreLib.compareItemStack(compareStack, this.contents[stack]) == 0) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void enableTowards(WorldCoord wc) {
      if (this.contents[this.select] != null) {
         int i = this.getMatchingStack(this.select);
         if (i >= 0) {
            this.enableTowardsActive(wc, i);
         }
      }

      for(int i = 0; i < 16; ++i) {
         this.select = (byte)(this.select + 1 & 15);
         if ((this.skipSlots & 1 << this.select) == 0 || this.select == 0) {
            break;
         }
      }

   }

   protected void enableTowardsActive(WorldCoord wc, int act) {
      ItemStack[] bkup = new ItemStack[36];
      FakePlayer player = CoreLib.getRedpowerPlayer(super.worldObj, super.xCoord, super.yCoord, super.zCoord, super.Rotation, super.Owner);
      this.packInv(bkup, act, player);
      ItemStack ist = this.contents[16 + act];
      if (ist != null && ist.stackSize > 0 && this.tryUseItemStack(ist, wc.x, wc.y, wc.z, 0, player)) {
         if (player.isUsingItem()) {
            player.stopUsingItem();
         }

         this.unpackInv(bkup, act, player);
         if (this.contents[16 + act].stackSize == 0) {
            this.contents[16 + act] = null;
         }

         this.markDirty();
      } else {
         this.unpackInv(bkup, act, player);
      }

   }

   public int getSizeInventory() {
      return 34;
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

      if (ist != null && i < 16) {
         this.skipSlots &= ~(1 << i);
      }

      this.markDirty();
   }

   public String getInventoryName() {
      return "tile.rpassemble.name";
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

      this.mode = data.getByte("mode");
      this.select = data.getByte("sel");
      this.skipSlots = data.getShort("ssl");
      this.PowerState = data.getInteger("psex");
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
      data.setByte("mode", this.mode);
      data.setByte("sel", this.select);
      data.setShort("ssl", (short)this.skipSlots);
      data.setInteger("psex", this.PowerState);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      super.readFromPacket(tag);
      this.mode = tag.getByte("mode");
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      super.writeToPacket(tag);
      tag.setByte("mode", this.mode);
   }

   public int[] getAccessibleSlotsFromSide(int side) {
      return side != (super.Rotation ^ 1) ? IntStream.range(16, 24).toArray() : new int[0];
   }

   public boolean canInsertItem(int slotID, ItemStack stack, int side) {
      return side != (super.Rotation ^ 1) && slotID >= 16 && slotID < 24;
   }

   public boolean canExtractItem(int slotID, ItemStack stack, int side) {
      return side != (super.Rotation ^ 1) && slotID >= 16 && slotID < 24;
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public boolean isItemValidForSlot(int slotID, ItemStack stack) {
      return true;
   }
}
