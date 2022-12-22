package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import java.util.stream.IntStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileRegulator extends TileMachine implements ITubeConnectable, ISidedInventory {
   private TubeBuffer buffer = new TubeBuffer();
   public byte mode = 0;
   protected ItemStack[] contents = new ItemStack[27];
   protected MachineLib.FilterMap inputMap = null;
   protected MachineLib.FilterMap outputMap = null;
   public int color = 0;

   private void regenFilterMap() {
      this.inputMap = MachineLib.makeFilterMap(this.contents, 0, 9);
      this.outputMap = MachineLib.makeFilterMap(this.contents, 18, 9);
   }

   @Override
   public int getTubeConnectableSides() {
      return 3 << (super.Rotation & -2);
   }

   @Override
   public int getTubeConClass() {
      return 0;
   }

   @Override
   public boolean canRouteItems() {
      return false;
   }

   @Override
   public boolean tubeItemEnter(int side, int state, TubeItem item) {
      if (side == super.Rotation && state == 2) {
         this.buffer.addBounce(item);
         super.Active = true;
         this.updateBlock();
         this.scheduleTick(5);
         return true;
      } else if (side == (super.Rotation ^ 1) && state == 1) {
         int ic = this.inCount(item.item);
         if (ic == 0) {
            return false;
         } else {
            boolean tr = true;
            ItemStack ist = item.item;
            if (ic < ist.stackSize) {
               tr = false;
               ist = ist.splitStack(ic);
            }

            if (MachineLib.addToInventoryCore(this, ist, IntStream.range(0, 9).toArray(), true)) {
               this.markDirty();
               this.scheduleTick(2);
               this.markDirty();
               return tr;
            } else {
               this.markDirty();
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
      return side == super.Rotation && state == 2
         || side == (super.Rotation ^ 1)
            && state == 1
            && this.inCount(item.item) != 0
            && MachineLib.addToInventoryCore(this, item.item, IntStream.range(0, 9).toArray(), false);
   }

   @Override
   public int tubeWeight(int side, int state) {
      return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
   }

   public int[] getAccessibleSlotsFromSide(int side) {
      return side != super.Rotation && side != (super.Rotation ^ 1) ? new int[]{9} : new int[0];
   }

   public void drainBuffer() {
      while(!this.buffer.isEmpty()) {
         TubeItem ti = this.buffer.getLast();
         if (!this.handleItem(ti)) {
            this.buffer.plugged = true;
            return;
         }

         this.buffer.pop();
         if (this.buffer.plugged) {
            return;
         }
      }

   }

   @Override
   public void updateEntity() {
      super.updateEntity();
      if (!super.worldObj.isRemote && !this.isTickScheduled()) {
         this.scheduleTick(10);
      }

   }

   @Override
   public boolean isPoweringTo(int side) {
      return side != (super.Rotation ^ 1) && super.Powered;
   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!super.worldObj.isRemote) {
            player.openGui(RedPowerMachine.instance, 9, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   @Override
   public int getExtendedID() {
      return 10;
   }

   @Override
   public void onBlockRemoval() {
      this.buffer.onRemove(this);

      for(int i = 0; i < 27; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   private int[] scanInput() {
      if (this.inputMap == null) {
         this.regenFilterMap();
      }

      if (this.inputMap.size() == 0) {
         return null;
      } else {
         int[] mc = MachineLib.genMatchCounts(this.inputMap);
         MachineLib.decMatchCounts(this.inputMap, mc, this, IntStream.range(0, 9).toArray());
         return mc;
      }
   }

   private int inCount(ItemStack ist) {
      if (this.inputMap == null) {
         this.regenFilterMap();
      }

      if (this.inputMap.size() == 0) {
         return 0;
      } else if (!this.inputMap.containsKey(ist)) {
         return 0;
      } else {
         int[] mc = MachineLib.genMatchCounts(this.inputMap);
         MachineLib.decMatchCounts(this.inputMap, mc, this, IntStream.range(0, 9).toArray());
         return MachineLib.decMatchCount(this.inputMap, mc, ist);
      }
   }

   private int[] scanOutput() {
      WorldCoord wc = new WorldCoord(this);
      wc.step(super.Rotation);
      IInventory inv = MachineLib.getInventory(super.worldObj, wc);
      if (inv == null) {
         return null;
      } else {
         int[] slots;
         if (inv instanceof ISidedInventory) {
            ISidedInventory mc = (ISidedInventory)inv;
            slots = mc.getAccessibleSlotsFromSide((super.Rotation ^ 1) & 0xFF);
         } else {
            slots = IntStream.range(0, inv.getSizeInventory()).toArray();
         }

         if (this.outputMap == null) {
            this.regenFilterMap();
         }

         if (this.outputMap.size() == 0) {
            return null;
         } else {
            int[] mc1 = MachineLib.genMatchCounts(this.outputMap);
            MachineLib.decMatchCounts(this.outputMap, mc1, inv, slots);
            return mc1;
         }
      }
   }

   private void handleTransfer(int[] omc) {
      if (this.mode != 0 && omc != null) {
         boolean var7 = false;

         for(int var8 = 0; var8 < 9; ++var8) {
            while(omc[var8] > 0) {
               ItemStack ist = this.contents[18 + var8].copy();
               int ss = Math.min(ist.stackSize, omc[var8]);
               omc[var8] -= ss;
               ist.stackSize = ss;
               ItemStack is2 = MachineLib.collectOneStack(this, IntStream.range(0, 9).toArray(), ist);
               if (is2 != null) {
                  this.buffer.addNewColor(is2, this.color);
                  var7 = true;
               }
            }
         }

         if (!var7) {
            return;
         }
      } else {
         for(int ch = 0; ch < 9; ++ch) {
            ItemStack i = this.contents[9 + ch];
            if (i != null && i.stackSize != 0) {
               this.buffer.addNewColor(i, this.color);
               this.contents[9 + ch] = null;
            }
         }
      }

      this.markDirty();
      super.Powered = true;
      super.Active = true;
      this.updateBlockChange();
      this.drainBuffer();
      if (!this.buffer.isEmpty()) {
         this.scheduleTick(10);
      } else {
         this.scheduleTick(5);
      }

   }

   @Override
   public void onTileTick() {
      if (!super.worldObj.isRemote) {
         if (super.Active) {
            if (!this.buffer.isEmpty()) {
               super.Powered = true;
               this.drainBuffer();
               this.updateBlockChange();
               if (!this.buffer.isEmpty()) {
                  this.scheduleTick(10);
               }

               return;
            }

            super.Active = false;
            this.updateBlock();
         }

         if (super.Powered) {
            int[] omc = this.scanOutput();
            if (omc == null) {
               super.Powered = false;
               this.updateBlockChange();
            } else if (!MachineLib.isMatchEmpty(omc)) {
               int[] imc = this.scanInput();
               if (imc != null && MachineLib.isMatchEmpty(imc)) {
                  this.handleTransfer(omc);
               } else {
                  super.Powered = false;
                  this.updateBlockChange();
               }
            }
         } else {
            int[] omc = this.scanOutput();
            if (omc != null && MachineLib.isMatchEmpty(omc)) {
               super.Powered = true;
               this.updateBlockChange();
            } else {
               int[] imc = this.scanInput();
               if (imc != null && MachineLib.isMatchEmpty(imc)) {
                  this.handleTransfer(omc);
               } else if (omc != null && this.mode == 1) {
                  this.handleTransfer(omc);
               }
            }
         }
      }

   }

   public int getSizeInventory() {
      return 27;
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
      return "tile.rpregulate.name";
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return !this.isInvalid()
         && super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord) == this
         && player.getDistanceSq((double)super.xCoord + 0.5, (double)super.yCoord + 0.5, (double)super.zCoord + 0.5) <= 64.0;
   }

   public void markDirty() {
      this.inputMap = null;
      this.outputMap = null;
      super.markDirty();
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

      this.buffer.readFromNBT(data);
      this.mode = data.getByte("mode");
      this.color = data.getByte("col");
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
      this.buffer.writeToNBT(data);
      data.setByte("mode", this.mode);
      data.setByte("col", (byte)this.color);
   }

   public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
      return side != super.Rotation && side != (super.Rotation ^ 1);
   }

   public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
      return side != super.Rotation && side != (super.Rotation ^ 1);
   }

   public boolean hasCustomInventoryName() {
      return true;
   }

   public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
      return true;
   }
}
