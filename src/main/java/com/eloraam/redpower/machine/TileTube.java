package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IPaintable;
import com.eloraam.redpower.core.ITubeFlow;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileTube extends TileCovered implements ITubeFlow, IPaintable {
   protected TubeFlow flow = new TubeFlow() {
      @Override
      public TileEntity getParent() {
         return TileTube.this;
      }

      @Override
      public boolean schedule(TubeItem item, TubeFlow.TubeScheduleContext context) {
         item.scheduled = true;
         item.progress = 0;
         int i = context.cons & ~(1 << item.side);
         if (i == 0) {
            return true;
         } else if (Integer.bitCount(i) == 1) {
            item.side = (byte)Integer.numberOfTrailingZeros(i);
            return true;
         } else if (!TileTube.this.worldObj.isRemote) {
            if (item.mode != 3) {
               item.mode = 1;
            }

            item.side = (byte)TubeLib.findRoute(context.world, context.wc, item, i, item.mode, TileTube.this.lastDir);
            if (item.side >= 0) {
               int m = i & ~((2 << TileTube.this.lastDir) - 1);
               if (m == 0) {
                  m = i;
               }

               if (m == 0) {
                  TileTube.this.lastDir = 0;
               } else {
                  TileTube.this.lastDir = (byte)Integer.numberOfTrailingZeros(m);
               }
            } else {
               if (item.mode == 1 && item.priority > 0) {
                  item.priority = 0;
                  item.side = (byte)TubeLib.findRoute(context.world, context.wc, item, context.cons, 1);
                  if (item.side >= 0) {
                     return true;
                  }
               }

               item.side = (byte)TubeLib.findRoute(context.world, context.wc, item, context.cons, 2);
               if (item.side >= 0) {
                  item.mode = 2;
                  return true;
               }

               if (item.mode == 3) {
                  item.side = (byte)TubeLib.findRoute(context.world, context.wc, item, context.cons, 1);
                  item.mode = 1;
               }

               if (item.side < 0) {
                  item.side = TileTube.this.lastDir;
                  int m = i & ~((2 << TileTube.this.lastDir) - 1);
                  if (m == 0) {
                     m = i;
                  }

                  if (m == 0) {
                     TileTube.this.lastDir = 0;
                  } else {
                     TileTube.this.lastDir = (byte)Integer.numberOfTrailingZeros(m);
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean handleItem(TubeItem item, TubeFlow.TubeScheduleContext context) {
         return MachineLib.addToInventory(TileTube.this.worldObj, item.item, context.dest, (item.side ^ 1) & 63);
      }
   };
   public byte lastDir = 0;
   public byte paintColor = 0;
   private boolean hasChanged = false;

   @Override
   public int getTubeConnectableSides() {
      int tr = 63;

      for(int i = 0; i < 6; ++i) {
         if ((super.CoverSides & 1 << i) > 0 && super.Covers[i] >> 8 < 3) {
            tr &= ~(1 << i);
         }
      }

      return tr;
   }

   @Override
   public int getTubeConClass() {
      return this.paintColor;
   }

   @Override
   public boolean canRouteItems() {
      return true;
   }

   @Override
   public boolean tubeItemEnter(int side, int state, TubeItem item) {
      if (state != 0) {
         return false;
      } else if (item.color != 0 && this.paintColor != 0 && item.color != this.paintColor) {
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
      return (item.color == 0 || this.paintColor == 0 || item.color == this.paintColor) && state == 0;
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
   public boolean tryPaint(int part, int side, int color) {
      if (part == 29) {
         if (this.paintColor == color) {
            return false;
         } else {
            this.paintColor = (byte)color;
            this.updateBlockChange();
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean canUpdate() {
      return true;
   }

   @Override
   public void updateEntity() {
      if (this.flow.update()) {
         this.hasChanged = true;
      }

      if (this.hasChanged) {
         if (!super.worldObj.isRemote) {
            this.markForUpdate();
         }

         this.markDirty();
      }

   }

   @Override
   public Block getBlockType() {
      return RedPowerBase.blockMicro;
   }

   @Override
   public int getExtendedID() {
      return 8;
   }

   @Override
   public void onBlockNeighborChange(Block block) {
   }

   @Override
   public int getPartsMask() {
      return super.CoverSides | 536870912;
   }

   @Override
   public int getSolidPartsMask() {
      return super.CoverSides | 536870912;
   }

   @Override
   public boolean blockEmpty() {
      return false;
   }

   @Override
   public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
      if (part == 29) {
         if (willHarvest) {
            CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8));
         }

         this.flow.onRemove();
         if (super.CoverSides > 0) {
            this.replaceWithCovers();
         } else {
            this.deleteBlock();
         }
      } else {
         super.onHarvestPart(player, part, willHarvest);
      }

   }

   @Override
   public void addHarvestContents(List<ItemStack> ist) {
      super.addHarvestContents(ist);
      ist.add(new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8));
   }

   @Override
   public float getPartStrength(EntityPlayer player, int part) {
      BlockMachine bl = RedPowerMachine.blockMachine;
      return part == 29 ? player.getBreakSpeed(bl, false, 0) / (bl.getHardness() * 30.0F) : super.getPartStrength(player, part);
   }

   @Override
   public void setPartBounds(BlockMultipart block, int part) {
      if (part == 29) {
         block.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
      } else {
         super.setPartBounds(block, part);
      }

   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.flow.readFromNBT(data);
      this.lastDir = data.getByte("lDir");
      this.paintColor = data.getByte("pCol");
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      this.flow.writeToNBT(data);
      data.setByte("lDir", this.lastDir);
      data.setByte("pCol", this.paintColor);
   }

   @Override
   protected void readFromPacket(NBTTagCompound data) {
      if (data.hasKey("flw")) {
         this.flow.contents.clear();
         int cs = data.getInteger("cs");

         for(int i = 0; i < cs; ++i) {
            this.flow.contents.add(TubeItem.newFromPacket((NBTTagCompound)data.getTag("cs" + i)));
         }
      } else {
         this.paintColor = data.getByte("pCol");
         super.readFromPacket(data);
      }

   }

   @Override
   protected void writeToPacket(NBTTagCompound data) {
      if (this.hasChanged) {
         this.hasChanged = false;
         data.setBoolean("flw", true);
         int cs = this.flow.contents.size();
         if (cs > 6) {
            cs = 6;
         }

         data.setInteger("cs", cs);

         for(int i = 0; i < cs; ++i) {
            TubeItem ti = (TubeItem)this.flow.contents.get(i);
            NBTTagCompound ftag = new NBTTagCompound();
            ti.writeToPacket(ftag);
            data.setTag("cs" + i, ftag);
         }
      } else {
         data.setByte("pCol", this.paintColor);
         super.writeToPacket(data);
      }

   }

   @Override
   protected ItemStack getBasePickStack() {
      return new ItemStack(RedPowerBase.blockMicro, 1, this.getExtendedID() << 8);
   }
}
