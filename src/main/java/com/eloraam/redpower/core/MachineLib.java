package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;

public class MachineLib {
   public static IInventory getInventory(World world, WorldCoord wc) {
      IInventory inv = CoreLib.getTileEntity(world, wc, IInventory.class);
      if (!(inv instanceof TileEntityChest)) {
         return inv;
      } else {
         TileEntityChest tec = CoreLib.getTileEntity(world, wc.x - 1, wc.y, wc.z, TileEntityChest.class);
         if (tec != null) {
            return new InventoryLargeChest("Large chest", tec, inv);
         } else {
            tec = CoreLib.getTileEntity(world, wc.x + 1, wc.y, wc.z, TileEntityChest.class);
            if (tec != null) {
               return new InventoryLargeChest("Large chest", inv, tec);
            } else {
               tec = CoreLib.getTileEntity(world, wc.x, wc.y, wc.z - 1, TileEntityChest.class);
               if (tec != null) {
                  return new InventoryLargeChest("Large chest", tec, inv);
               } else {
                  tec = CoreLib.getTileEntity(world, wc.x, wc.y, wc.z + 1, TileEntityChest.class);
                  return (IInventory)(tec != null ? new InventoryLargeChest("Large chest", inv, tec) : inv);
               }
            }
         }
      }
   }

   public static IInventory getSideInventory(World world, WorldCoord wc, int side, boolean push) {
      IInventory inv = getInventory(world, wc);
      if (inv == null) {
         return null;
      } else if (inv instanceof ISidedInventory) {
         ISidedInventory isi = (ISidedInventory)inv;
         int[] slots = isi.getAccessibleSlotsFromSide(side);
         return new MachineLib.SubInventory(inv, slots);
      } else {
         return inv;
      }
   }

   public static boolean addToInventoryCore(World world, ItemStack ist, WorldCoord wc, int side, boolean act) {
      IInventory inv = getInventory(world, wc);
      if (inv == null) {
         return false;
      } else {
         int[] slots;
         if (inv instanceof ISidedInventory) {
            ISidedInventory isi = (ISidedInventory)inv;
            slots = isi.getAccessibleSlotsFromSide(side);
            if (slots == null || slots.length == 0) {
               return false;
            }

            int[] var8 = slots;
            int var9 = slots.length;
            int var10 = 0;

            while(true) {
               if (var10 >= var9) {
                  return false;
               }

               int n = var8[var10];
               if (isi.canInsertItem(n, ist, side)) {
                  break;
               }

               ++var10;
            }
         } else {
            slots = IntStream.range(0, inv.getSizeInventory()).toArray();
         }

         return addToInventoryCore(inv, ist, slots, act);
      }
   }

   public static boolean addToInventoryCore(IInventory inv, ItemStack ist, int[] slots, boolean act) {
      for(int n : slots) {
         ItemStack invst = inv.getStackInSlot(n);
         if (invst != null) {
            if (ist.isItemEqual(invst) && ItemStack.areItemStackTagsEqual(ist, invst)) {
               int dfs = Math.min(invst.getMaxStackSize(), inv.getInventoryStackLimit());
               dfs -= invst.stackSize;
               if (dfs > 0) {
                  int si = Math.min(dfs, ist.stackSize);
                  if (!act) {
                     return true;
                  }

                  invst.stackSize += si;
                  inv.setInventorySlotContents(n, invst);
                  ist.stackSize -= si;
                  if (ist.stackSize == 0) {
                     return true;
                  }
               }
            }
         } else if (!act) {
            return true;
         }
      }

      if (!act) {
         return false;
      } else {
         for(int n : slots) {
            ItemStack invst = inv.getStackInSlot(n);
            if (invst == null) {
               if (inv.getInventoryStackLimit() >= ist.stackSize) {
                  inv.setInventorySlotContents(n, ist);
                  return true;
               }

               inv.setInventorySlotContents(n, ist.splitStack(inv.getInventoryStackLimit()));
            }
         }

         return false;
      }
   }

   public static boolean addToInventory(World world, ItemStack ist, WorldCoord wc, int side) {
      return addToInventoryCore(world, ist, wc, side, true);
   }

   public static boolean canAddToInventory(World world, ItemStack ist, WorldCoord wc, int side) {
      return addToInventoryCore(world, ist, wc, side, false);
   }

   public static void ejectItem(World world, WorldCoord wc, ItemStack ist, int dir) {
      wc = wc.copy();
      wc.step(dir);
      EntityItem item = new EntityItem(world, (double)wc.x + 0.5, (double)wc.y + 0.5, (double)wc.z + 0.5, ist);
      item.motionX = 0.0;
      item.motionY = 0.0;
      item.motionZ = 0.0;
      switch(dir) {
         case 0:
            item.motionY = -0.3;
            break;
         case 1:
            item.motionY = 0.3;
            break;
         case 2:
            item.motionZ = -0.3;
            break;
         case 3:
            item.motionZ = 0.3;
            break;
         case 4:
            item.motionX = -0.3;
            break;
         default:
            item.motionX = 0.3;
      }

      item.delayBeforeCanPickup = 10;
      world.spawnEntityInWorld(item);
   }

   public static boolean handleItem(World world, ItemStack ist, WorldCoord wc, int side) {
      WorldCoord dest = wc.copy();
      dest.step(side);
      if (ist.stackSize == 0) {
         return true;
      } else if (TubeLib.addToTubeRoute(world, ist, wc, dest, side ^ 1)) {
         return true;
      } else if (addToInventory(world, ist, dest, (side ^ 1) & 63)) {
         return true;
      } else {
         TileEntity te = CoreLib.getTileEntity(world, dest, TileEntity.class);
         if (!(te instanceof IInventory) && !(te instanceof ITubeConnectable)) {
            if (world.getBlock(dest.x, dest.y, dest.z).isSideSolid(world, dest.x, dest.y, dest.z, ForgeDirection.getOrientation(side ^ 1))) {
               return false;
            } else {
               ejectItem(world, wc, ist, side);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean handleItem(World world, TubeItem ti, WorldCoord wc, int side) {
      WorldCoord dest = wc.copy();
      dest.step(side);
      if (ti.item.stackSize == 0) {
         return true;
      } else if (TubeLib.addToTubeRoute(world, ti, wc, dest, side ^ 1)) {
         return true;
      } else if (addToInventory(world, ti.item, dest, (side ^ 1) & 63)) {
         return true;
      } else {
         TileEntity te = CoreLib.getTileEntity(world, dest, TileEntity.class);
         if (!(te instanceof IInventory) && !(te instanceof ITubeConnectable)) {
            if (world.getBlock(dest.x, dest.y, dest.z).isSideSolid(world, dest.x, dest.y, dest.z, ForgeDirection.getOrientation(side ^ 1))) {
               return false;
            } else {
               ejectItem(world, wc, ti.item, side);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static int compareItem(ItemStack a, ItemStack b) {
      if (Item.getIdFromItem(a.getItem()) != Item.getIdFromItem(b.getItem())) {
         return Item.getIdFromItem(a.getItem()) - Item.getIdFromItem(b.getItem());
      } else if (a.getItemDamage() == b.getItemDamage()) {
         return 0;
      } else if (a.getItem().getHasSubtypes()) {
         return a.getItemDamage() - b.getItemDamage();
      } else {
         int d1 = a.getItemDamage() <= 1 ? -1 : (a.getItemDamage() == a.getMaxDamage() - 1 ? 1 : 0);
         int d2 = b.getItemDamage() <= 1 ? -1 : (b.getItemDamage() == b.getMaxDamage() - 1 ? 1 : 0);
         return d1 - d2;
      }
   }

   public static MachineLib.FilterMap makeFilterMap(ItemStack[] ist) {
      return new MachineLib.FilterMap(ist);
   }

   public static MachineLib.FilterMap makeFilterMap(ItemStack[] ist, int st, int ln) {
      ItemStack[] it = new ItemStack[ln];
      System.arraycopy(ist, st, it, 0, ln);
      return new MachineLib.FilterMap(it);
   }

   public static int[] genMatchCounts(MachineLib.FilterMap map) {
      int[] tr = new int[map.filter.length];

      for(int n = 0; n < map.filter.length; ++n) {
         ItemStack ist = map.filter[n];
         if (ist != null && ist.stackSize != 0) {
            List<Integer> arl = (List)map.map.get(ist);
            if (arl != null && arl.get(0) == n) {
               for(int m : arl) {
                  tr[n] += map.filter[m].stackSize;
               }
            }
         }
      }

      return tr;
   }

   public static int decMatchCount(MachineLib.FilterMap map, int[] mc, ItemStack ist) {
      List<Integer> arl = (List)map.map.get(ist);
      if (arl == null) {
         return 0;
      } else {
         int n = arl.get(0);
         int tr = Math.min(mc[n], ist.stackSize);
         mc[n] -= tr;
         return tr;
      }
   }

   public static int getMatchCount(MachineLib.FilterMap map, int[] mc, ItemStack ist) {
      List<Integer> arl = (List)map.map.get(ist);
      if (arl == null) {
         return 0;
      } else {
         int n = arl.get(0);
         return Math.min(mc[n], ist.stackSize);
      }
   }

   public static boolean isMatchEmpty(int[] mc) {
      for(int i : mc) {
         if (i > 0) {
            return false;
         }
      }

      return true;
   }

   public static void decMatchCounts(MachineLib.FilterMap map, int[] mc, IInventory inv, int[] slots) {
      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            decMatchCount(map, mc, ist);
         }
      }

   }

   public static boolean matchOneStack(MachineLib.FilterMap map, IInventory inv, int[] slots, int pos) {
      ItemStack match = map.filter[pos];
      int fc = match == null ? 1 : match.stackSize;

      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            if (match == null) {
               return true;
            }

            if (compareItem(match, ist) == 0) {
               int m = Math.min(ist.stackSize, fc);
               fc -= m;
               if (fc <= 0) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static int matchAnyStack(MachineLib.FilterMap map, IInventory inv, int[] slots) {
      int[] mc = new int[map.filter.length];

      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            List<Integer> arl = (List)map.map.get(ist);
            if (arl != null) {
               for(int m : arl) {
                  mc[m] += ist.stackSize;
                  if (mc[m] >= map.filter[m].stackSize) {
                     return m;
                  }
               }
            }
         }
      }

      return -1;
   }

   public static int matchAnyStackCol(MachineLib.FilterMap map, IInventory inv, int[] slots, int col) {
      int[] mc = new int[5];

      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            List<Integer> arl = (List)map.map.get(ist);
            if (arl != null) {
               for(Integer m : arl) {
                  if ((m & 7) == col) {
                     int s = m >> 3;
                     mc[s] += ist.stackSize;
                     if (mc[s] >= map.filter[m].stackSize) {
                        return m;
                     }
                  }
               }
            }
         }
      }

      return -1;
   }

   public static boolean matchAllCol(MachineLib.FilterMap map, IInventory inv, int[] slots, int col) {
      int[] mc = new int[5];

      for(int any : slots) {
         ItemStack n = inv.getStackInSlot(any);
         if (n != null && n.stackSize != 0) {
            ArrayList<Integer> ct = (ArrayList)map.map.get(n);
            if (ct != null) {
               int ss = n.stackSize;

               for(Integer m : ct) {
                  if ((m & 7) == col) {
                     int c = m >> 3;
                     int s1 = Math.min(ss, map.filter[m].stackSize - mc[c]);
                     mc[c] += s1;
                     ss -= s1;
                     if (ss == 0) {
                        break;
                     }
                  }
               }
            }
         }
      }

      boolean match = false;

      for(int i = 0; i < 5; ++i) {
         ItemStack stack = map.filter[i * 8 + col];
         if (stack != null && stack.stackSize != 0) {
            match = true;
            if (stack.stackSize > mc[i]) {
               return false;
            }
         }
      }

      return match;
   }

   public static boolean emptyInventory(IInventory inv, int[] slots) {
      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            return false;
         }
      }

      return true;
   }

   public static ItemStack collectOneStack(IInventory inv, int[] slots, ItemStack match) {
      ItemStack tr = null;
      int fc = match == null ? 1 : match.stackSize;

      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            if (match == null) {
               inv.setInventorySlotContents(n, null);
               return ist;
            }

            if (compareItem(match, ist) == 0) {
               int m = Math.min(ist.stackSize, fc);
               if (tr == null) {
                  tr = inv.decrStackSize(n, m);
               } else {
                  inv.decrStackSize(n, m);
                  tr.stackSize += m;
               }

               fc -= m;
               if (fc <= 0) {
                  break;
               }
            }
         }
      }

      return tr;
   }

   public static ItemStack collectOneStackFuzzy(IInventory inv, int[] slots, ItemStack match) {
      ItemStack tr = null;
      int fc = match == null ? 1 : match.getMaxStackSize();

      for(int n : slots) {
         ItemStack ist = inv.getStackInSlot(n);
         if (ist != null && ist.stackSize != 0) {
            if (match == null) {
               inv.setInventorySlotContents(n, null);
               return ist;
            }

            if (compareItem(match, ist) == 0) {
               int m = Math.min(ist.stackSize, fc);
               if (tr == null) {
                  tr = inv.decrStackSize(n, m);
               } else {
                  inv.decrStackSize(n, m);
                  tr.stackSize += m;
               }

               fc -= m;
               if (fc <= 0) {
                  break;
               }
            }
         }
      }

      return tr;
   }

   public static class FilterMap {
      protected TreeMap<ItemStack, ArrayList<Integer>> map;
      protected ItemStack[] filter;

      public FilterMap(ItemStack[] filt) {
         this.filter = filt;
         this.map = new TreeMap<>(MachineLib::compareItem);

         for(int i = 0; i < filt.length; ++i) {
            if (filt[i] != null && filt[i].stackSize != 0) {
               ((ArrayList)this.map.computeIfAbsent(filt[i], k -> new ArrayList())).add(i);
            }
         }

      }

      public int size() {
         return this.map.size();
      }

      public boolean containsKey(ItemStack ist) {
         return this.map.containsKey(ist);
      }

      public int firstMatch(ItemStack ist) {
         ArrayList<Integer> arl = (ArrayList)this.map.get(ist);
         return arl == null ? -1 : arl.get(0);
      }
   }

   public static class SubInventory implements IInventory {
      IInventory parent;
      int[] slots;

      SubInventory(IInventory par, int[] sl) {
         this.parent = par;
         this.slots = sl;
      }

      public int getSizeInventory() {
         return this.slots.length;
      }

      public ItemStack getStackInSlot(int idx) {
         return ArrayUtils.contains(this.slots, idx) ? this.parent.getStackInSlot(idx) : null;
      }

      public ItemStack decrStackSize(int idx, int num) {
         return ArrayUtils.contains(this.slots, idx) ? this.parent.decrStackSize(idx, num) : null;
      }

      public ItemStack getStackInSlotOnClosing(int idx) {
         return ArrayUtils.contains(this.slots, idx) ? this.parent.getStackInSlotOnClosing(idx) : null;
      }

      public void setInventorySlotContents(int idx, ItemStack ist) {
         if (ArrayUtils.contains(this.slots, idx)) {
            this.parent.setInventorySlotContents(idx, ist);
         }

      }

      public String getInventoryName() {
         return this.parent.getInventoryName();
      }

      public int getInventoryStackLimit() {
         return this.parent.getInventoryStackLimit();
      }

      public void markDirty() {
         this.parent.markDirty();
      }

      public boolean isUseableByPlayer(EntityPlayer player) {
         return this.parent.isUseableByPlayer(player);
      }

      public void openInventory() {
      }

      public void closeInventory() {
      }

      public boolean hasCustomInventoryName() {
         return this.parent.hasCustomInventoryName();
      }

      public boolean isItemValidForSlot(int slotID, ItemStack stack) {
         return this.parent.isItemValidForSlot(slotID, stack);
      }
   }
}
