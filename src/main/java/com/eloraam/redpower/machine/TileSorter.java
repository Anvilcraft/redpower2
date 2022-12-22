package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileSorter extends TileTranspose implements IInventory, ISidedInventory, IBluePowerConnectable {
   BluePowerEndpoint cond = new BluePowerEndpoint() {
      @Override
      public TileEntity getParent() {
         return TileSorter.this;
      }
   };
   public int ConMask = -1;
   private ItemStack[] contents = new ItemStack[40];
   public byte[] colors = new byte[8];
   public byte mode = 0;
   public byte automode = 0;
   public byte defcolor = 0;
   public byte draining = -1;
   public byte column = 0;
   public int pulses = 0;
   private MachineLib.FilterMap filterMap = null;
   private TubeBuffer[] channelBuffers = new TubeBuffer[8];

   public TileSorter() {
      for(int i = 0; i < 8; ++i) {
         this.channelBuffers[i] = new TubeBuffer();
      }

   }

   private void regenFilterMap() {
      this.filterMap = MachineLib.makeFilterMap(this.contents);
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

   public int[] getAccessibleSlotsFromSide(int side) {
      return new int[0];
   }

   @Override
   public void updateEntity() {
      super.updateEntity();
      if (!super.worldObj.isRemote) {
         if (!super.Powered) {
            super.Delay = false;
         }

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
            }
         } else if (!super.Charged) {
            super.Charged = true;
            this.updateBlock();
         }

         if ((this.automode == 1 || this.automode == 2 && this.pulses > 0) && !this.isTickScheduled()) {
            this.scheduleTick(10);
         }
      }

   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!super.worldObj.isRemote) {
            player.openGui(RedPowerMachine.instance, 5, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         }

         return true;
      }
   }

   @Override
   public int getExtendedID() {
      return 5;
   }

   @Override
   public void onBlockRemoval() {
      super.onBlockRemoval();

      for(int i = 0; i < 8; ++i) {
         this.channelBuffers[i].onRemove(this);
      }

      for(int i = 0; i < 40; ++i) {
         ItemStack ist = this.contents[i];
         if (ist != null && ist.stackSize > 0) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
         }
      }

   }

   @Override
   public void onBlockNeighborChange(Block block) {
      this.ConMask = -1;
      if (this.automode == 0) {
         super.onBlockNeighborChange(block);
      }

      if (this.automode == 2) {
         if (!RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63)) {
            super.Powered = false;
            this.markDirty();
            return;
         }

         if (super.Powered) {
            return;
         }

         super.Powered = true;
         this.markDirty();
         if (super.Delay) {
            return;
         }

         super.Delay = true;
         ++this.pulses;
      }

   }

   protected int getColumnMatch(ItemStack ist) {
      if (this.filterMap == null) {
         this.regenFilterMap();
      }

      if (this.filterMap.size() == 0) {
         return -2;
      } else {
         int i = this.filterMap.firstMatch(ist);
         return i < 0 ? i : i & 7;
      }
   }

   protected void fireMatch() {
      super.Active = true;
      this.updateBlock();
      this.scheduleTick(5);
   }

   protected boolean tryDrainBuffer(TubeBuffer buf) {
      if (buf.isEmpty()) {
         return false;
      } else {
         while(!buf.isEmpty()) {
            TubeItem ti = buf.getLast();
            if (this.stuffCart(ti.item)) {
               buf.pop();
            } else {
               if (!this.handleItem(ti)) {
                  buf.plugged = true;
                  return true;
               }

               buf.pop();
               if (buf.plugged) {
                  return true;
               }
            }
         }

         return true;
      }
   }

   protected boolean tryDrainBuffer() {
      for(int i = 0; i < 9; ++i) {
         ++this.draining;
         TubeBuffer buf;
         if (this.draining > 7) {
            this.draining = -1;
            buf = super.buffer;
         } else {
            buf = this.channelBuffers[this.draining];
         }

         if (this.tryDrainBuffer(buf)) {
            return false;
         }
      }

      return true;
   }

   protected boolean isBufferEmpty() {
      if (!super.buffer.isEmpty()) {
         return false;
      } else {
         for(int i = 0; i < 8; ++i) {
            if (!this.channelBuffers[i].isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public void drainBuffer() {
      this.tryDrainBuffer();
   }

   private boolean autoTick() {
      if (super.Active) {
         return false;
      } else if (this.automode == 2 && this.pulses == 0) {
         return false;
      } else {
         WorldCoord wc = new WorldCoord(this);
         wc.step(super.Rotation ^ 1);
         if (this.handleExtract(wc)) {
            super.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
         } else {
            this.scheduleTick(10);
         }

         return true;
      }
   }

   @Override
   public void onTileTick() {
      if (!super.worldObj.isRemote) {
         if (this.automode == 1 && super.Powered) {
            super.Powered = false;
            this.updateBlock();
         }

         if ((this.automode <= 0 || !this.autoTick()) && super.Active) {
            if (!this.tryDrainBuffer()) {
               if (this.isBufferEmpty()) {
                  this.scheduleTick(5);
               } else {
                  this.scheduleTick(10);
               }
            } else {
               if (!super.Powered || this.automode == 2) {
                  super.Active = false;
                  this.updateBlock();
               }

               if (this.automode == 1 || this.automode == 2 && this.pulses > 0) {
                  this.scheduleTick(5);
               }
            }
         }
      }

   }

   @Override
   public boolean tubeItemEnter(int side, int state, TubeItem item) {
      if (side == super.Rotation && state == 2) {
         int cm = this.getColumnMatch(item.item);
         TubeBuffer buf = super.buffer;
         if (cm >= 0 && this.mode > 1) {
            buf = this.channelBuffers[cm];
         }

         buf.addBounce(item);
         this.fireMatch();
         return true;
      } else if (side != (super.Rotation ^ 1) || state != 1) {
         return false;
      } else if (item.priority > 0) {
         return false;
      } else if (this.automode == 0 && super.Powered) {
         return false;
      } else if (this.cond.getVoltage() < 60.0) {
         return false;
      } else {
         int cm = this.getColumnMatch(item.item);
         TubeBuffer buf = super.buffer;
         if (cm >= 0 && this.mode > 1) {
            buf = this.channelBuffers[cm];
         }

         if (!buf.isEmpty()) {
            return false;
         } else if (cm >= 0) {
            this.cond.drawPower((double)(25 * item.item.stackSize));
            buf.addNewColor(item.item, this.colors[cm]);
            this.fireMatch();
            this.tryDrainBuffer(buf);
            return true;
         } else if (this.mode == 4 || this.mode == 6) {
            this.cond.drawPower((double)(25 * item.item.stackSize));
            buf.addNewColor(item.item, this.defcolor);
            this.fireMatch();
            this.tryDrainBuffer(buf);
            return true;
         } else if (cm == -2) {
            this.cond.drawPower((double)(25 * item.item.stackSize));
            buf.addNewColor(item.item, 0);
            this.fireMatch();
            this.tryDrainBuffer(buf);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
      if (side == super.Rotation && state == 2) {
         return true;
      } else if (side != (super.Rotation ^ 1) || state != 1) {
         return false;
      } else if (item.priority > 0) {
         return false;
      } else if (this.automode == 0 && super.Powered) {
         return false;
      } else if (this.cond.getVoltage() < 60.0) {
         return false;
      } else {
         int cm = this.getColumnMatch(item.item);
         TubeBuffer buf = super.buffer;
         if (cm >= 0 && this.mode > 1) {
            buf = this.channelBuffers[cm];
         }

         return buf.isEmpty() && (cm >= 0 || this.mode == 4 || this.mode == 6 || cm == -2);
      }
   }

   @Override
   protected void addToBuffer(ItemStack ist) {
      int cm = this.getColumnMatch(ist);
      TubeBuffer buf = super.buffer;
      if (cm >= 0 && this.mode > 1) {
         buf = this.channelBuffers[cm];
      }

      if (cm < 0) {
         if (this.mode != 4 && this.mode != 6) {
            buf.addNewColor(ist, 0);
         } else {
            buf.addNewColor(ist, this.defcolor);
         }
      } else {
         buf.addNewColor(ist, this.colors[cm]);
      }

   }

   private void stepColumn() {
      for(int i = 0; i < 8; ++i) {
         ++this.column;
         if (this.column > 7) {
            if (this.pulses > 0) {
               --this.pulses;
            }

            this.column = 0;
         }

         for(int a = 0; a < 5; ++a) {
            ItemStack ct = this.contents[a * 8 + this.column];
            if (ct != null && ct.stackSize != 0) {
               return;
            }
         }
      }

      this.column = 0;
   }

   private void checkColumn() {
      for(int a = 0; a < 5; ++a) {
         ItemStack ct = this.contents[a * 8 + this.column];
         if (ct != null && ct.stackSize != 0) {
            return;
         }
      }

      this.stepColumn();
      this.markDirty();
   }

   @Override
   protected boolean handleExtract(IInventory inv, int[] slots) {
      if (this.cond.getVoltage() < 60.0) {
         return false;
      } else {
         if (this.filterMap == null) {
            this.regenFilterMap();
         }

         if (this.filterMap.size() == 0) {
            ItemStack var8 = MachineLib.collectOneStack(inv, slots, null);
            if (var8 == null) {
               return false;
            } else {
               if (this.mode != 4 && this.mode != 6) {
                  super.buffer.addNew(var8);
               } else {
                  super.buffer.addNewColor(var8, this.defcolor);
               }

               this.cond.drawPower((double)(25 * var8.stackSize));
               this.drainBuffer();
               return true;
            }
         } else {
            int sm;
            ItemStack coll;
            switch(this.mode) {
               case 0:
                  this.checkColumn();
                  sm = MachineLib.matchAnyStackCol(this.filterMap, inv, slots, this.column);
                  if (sm < 0) {
                     return false;
                  }

                  coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
                  super.buffer.addNewColor(coll, this.colors[sm & 7]);
                  this.cond.drawPower((double)(25 * coll.stackSize));
                  this.stepColumn();
                  this.drainBuffer();
                  return true;
               case 1:
                  this.checkColumn();
                  if (!MachineLib.matchAllCol(this.filterMap, inv, slots, this.column)) {
                     return false;
                  }

                  for(int n = 0; n < 5; ++n) {
                     ItemStack match = this.contents[n * 8 + this.column];
                     if (match != null && match.stackSize != 0) {
                        coll = MachineLib.collectOneStack(inv, slots, match);
                        super.buffer.addNewColor(coll, this.colors[this.column]);
                        this.cond.drawPower((double)(25 * coll.stackSize));
                     }
                  }

                  this.stepColumn();
                  this.drainBuffer();
                  return true;
               case 2:
                  sm = 0;

                  while(sm < 8 && !MachineLib.matchAllCol(this.filterMap, inv, slots, sm)) {
                     ++sm;
                  }

                  if (sm == 8) {
                     return false;
                  } else {
                     for(int n = 0; n < 5; ++n) {
                        ItemStack match = this.contents[n * 8 + sm];
                        if (match != null && match.stackSize != 0) {
                           coll = MachineLib.collectOneStack(inv, slots, match);
                           this.channelBuffers[sm].addNewColor(coll, this.colors[sm]);
                           this.cond.drawPower((double)(25 * coll.stackSize));
                        }
                     }

                     if (this.pulses > 0) {
                        --this.pulses;
                     }

                     this.drainBuffer();
                     return true;
                  }
               case 3:
                  sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
                  if (sm < 0) {
                     return false;
                  }

                  coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
                  this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7]);
                  this.cond.drawPower((double)(25 * coll.stackSize));
                  if (this.pulses > 0) {
                     --this.pulses;
                  }

                  this.drainBuffer();
                  return true;
               case 4:
                  sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
                  if (sm < 0) {
                     coll = MachineLib.collectOneStack(inv, slots, null);
                     if (coll == null) {
                        return false;
                     }

                     super.buffer.addNewColor(coll, this.defcolor);
                  } else {
                     coll = MachineLib.collectOneStack(inv, slots, this.contents[sm]);
                     this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7]);
                  }

                  this.cond.drawPower((double)(25 * coll.stackSize));
                  if (this.pulses > 0) {
                     --this.pulses;
                  }

                  this.drainBuffer();
                  return true;
               case 5:
                  sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
                  if (sm < 0) {
                     return false;
                  }

                  coll = MachineLib.collectOneStackFuzzy(inv, slots, this.contents[sm]);
                  this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7]);
                  this.cond.drawPower((double)(25 * coll.stackSize));
                  if (this.pulses > 0) {
                     --this.pulses;
                  }

                  this.drainBuffer();
                  return true;
               case 6:
                  sm = MachineLib.matchAnyStack(this.filterMap, inv, slots);
                  if (sm < 0) {
                     coll = MachineLib.collectOneStack(inv, slots, null);
                     if (coll == null) {
                        return false;
                     }

                     super.buffer.addNewColor(coll, this.defcolor);
                  } else {
                     coll = MachineLib.collectOneStackFuzzy(inv, slots, this.contents[sm]);
                     this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7]);
                  }

                  this.cond.drawPower((double)(25 * coll.stackSize));
                  if (this.pulses > 0) {
                     --this.pulses;
                  }

                  this.drainBuffer();
                  return true;
               default:
                  return false;
            }
         }
      }
   }

   @Override
   protected boolean suckFilter(ItemStack ist) {
      if (this.cond.getVoltage() < 60.0) {
         return false;
      } else {
         if (this.filterMap == null) {
            this.regenFilterMap();
         }

         int cm = this.getColumnMatch(ist);
         TubeBuffer buf = super.buffer;
         if (cm >= 0 && this.mode > 1) {
            buf = this.channelBuffers[cm];
         }

         if (buf.plugged) {
            return false;
         } else if (cm < 0) {
            if (this.mode != 4 && this.mode != 6 && cm != -2) {
               return false;
            } else {
               this.cond.drawPower((double)(25 * ist.stackSize));
               return true;
            }
         } else {
            this.cond.drawPower((double)(25 * ist.stackSize));
            return true;
         }
      }
   }

   public int getSizeInventory() {
      return 40;
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
      return "tile.rpsorter.name";
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
      this.filterMap = null;
      super.markDirty();
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      NBTTagList items = data.getTagList("Items", 10);
      this.contents = new ItemStack[this.getSizeInventory()];

      for(int cols = 0; cols < items.tagCount(); ++cols) {
         NBTTagCompound bufs = items.getCompoundTagAt(cols);
         int i = bufs.getByte("Slot") & 255;
         if (i >= 0 && i < this.contents.length) {
            this.contents[i] = ItemStack.loadItemStackFromNBT(bufs);
         }
      }

      this.column = data.getByte("coln");
      byte[] cols = data.getByteArray("cols");
      if (cols.length >= 8) {
         System.arraycopy(cols, 0, this.colors, 0, 8);
      }

      this.mode = data.getByte("mode");
      this.automode = data.getByte("amode");
      this.draining = data.getByte("drain");
      if (this.mode == 4 || this.mode == 6) {
         this.defcolor = data.getByte("defc");
      }

      this.pulses = data.getInteger("pulses");
      this.cond.readFromNBT(data);
      NBTTagList buffers = data.getTagList("buffers", 10);

      for(int i = 0; i < buffers.tagCount(); ++i) {
         NBTTagCompound buf = buffers.getCompoundTagAt(i);
         this.channelBuffers[i].readFromNBT(buf);
      }

   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      NBTTagList items = new NBTTagList();

      for(int bufs = 0; bufs < this.contents.length; ++bufs) {
         if (this.contents[bufs] != null) {
            NBTTagCompound i = new NBTTagCompound();
            i.setByte("Slot", (byte)bufs);
            this.contents[bufs].writeToNBT(i);
            items.appendTag(i);
         }
      }

      data.setByte("coln", this.column);
      data.setTag("Items", items);
      data.setByteArray("cols", this.colors);
      data.setByte("mode", this.mode);
      data.setByte("amode", this.automode);
      data.setByte("drain", this.draining);
      data.setInteger("pulses", this.pulses);
      if (this.mode == 4 || this.mode == 6) {
         data.setByte("defc", this.defcolor);
      }

      this.cond.writeToNBT(data);
      NBTTagList buffers = new NBTTagList();

      for(int i = 0; i < 8; ++i) {
         NBTTagCompound buf = new NBTTagCompound();
         this.channelBuffers[i].writeToNBT(buf);
         buffers.appendTag(buf);
      }

      data.setTag("buffers", buffers);
   }

   public boolean canInsertItem(int slotID, ItemStack itemStack, int side) {
      return false;
   }

   public boolean canExtractItem(int slotID, ItemStack itemStack, int side) {
      return false;
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
      return true;
   }
}
