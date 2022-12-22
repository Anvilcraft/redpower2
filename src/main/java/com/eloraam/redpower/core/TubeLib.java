package com.eloraam.redpower.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TubeLib {
   private static Set<List<Integer>> tubeClassMapping = new HashSet();

   public static void addCompatibleMapping(int a, int b) {
      tubeClassMapping.add(Arrays.asList(a, b));
      tubeClassMapping.add(Arrays.asList(b, a));
   }

   public static boolean isCompatible(int a, int b) {
      return a == b || tubeClassMapping.contains(Arrays.asList(a, b));
   }

   private static boolean isConSide(IBlockAccess iba, int x, int y, int z, int col, int side) {
      TileEntity te = iba.getTileEntity(x, y, z);
      if (te instanceof ITubeConnectable) {
         ITubeConnectable itc = (ITubeConnectable)te;
         if (!isCompatible(col, itc.getTubeConClass())) {
            return false;
         } else {
            int sides = itc.getTubeConnectableSides();
            return (sides & 1 << side) > 0;
         }
      } else {
         if (isCompatible(col, 0) && te instanceof IInventory) {
            if (!(te instanceof ISidedInventory)) {
               return true;
            }

            ISidedInventory isi = (ISidedInventory)te;
            if (isi.getSizeInventory() > 0) {
               int[] slots = isi.getAccessibleSlotsFromSide(side);
               return slots != null && slots.length > 0;
            }
         }

         return false;
      }
   }

   public static int getConnections(IBlockAccess iba, int x, int y, int z) {
      ITubeConnectable itc = CoreLib.getTileEntity(iba, x, y, z, ITubeConnectable.class);
      if (itc == null) {
         return 0;
      } else {
         int trs = 0;
         int col = itc.getTubeConClass();
         int sides = itc.getTubeConnectableSides();
         if ((sides & 1) > 0 && isConSide(iba, x, y - 1, z, col, 1)) {
            trs |= 1;
         }

         if ((sides & 2) > 0 && isConSide(iba, x, y + 1, z, col, 0)) {
            trs |= 2;
         }

         if ((sides & 4) > 0 && isConSide(iba, x, y, z - 1, col, 3)) {
            trs |= 4;
         }

         if ((sides & 8) > 0 && isConSide(iba, x, y, z + 1, col, 2)) {
            trs |= 8;
         }

         if ((sides & 16) > 0 && isConSide(iba, x - 1, y, z, col, 5)) {
            trs |= 16;
         }

         if ((sides & 32) > 0 && isConSide(iba, x + 1, y, z, col, 4)) {
            trs |= 32;
         }

         return trs;
      }
   }

   public static int findRoute(World world, WorldCoord wc, TubeItem te, int sides, int state) {
      TubeLib.OutRouteFinder rf = new TubeLib.OutRouteFinder(world, te, state);
      return rf.find(wc, sides);
   }

   public static int findRoute(World world, WorldCoord wc, TubeItem te, int sides, int state, int start) {
      TubeLib.OutRouteFinder rf = new TubeLib.OutRouteFinder(world, te, state);
      rf.startDir = start;
      return rf.find(wc, sides);
   }

   public static boolean addToTubeRoute(World world, ItemStack ist, WorldCoord src, WorldCoord wc, int side) {
      return addToTubeRoute(world, new TubeItem(0, ist), src, wc, side);
   }

   public static boolean addToTubeRoute(World world, TubeItem ti, WorldCoord src, WorldCoord wc, int side) {
      ITubeConnectable ite = CoreLib.getTileEntity(world, wc, ITubeConnectable.class);
      if (ite == null) {
         return false;
      } else {
         ti.mode = 1;
         int s = findRoute(world, src, ti, 1 << (side ^ 1), 1);
         return s >= 0 && ite.tubeItemEnter(side, 0, ti);
      }
   }

   static {
      addCompatibleMapping(0, 17);
      addCompatibleMapping(17, 18);

      for(int i = 0; i < 16; ++i) {
         addCompatibleMapping(0, 1 + i);
         addCompatibleMapping(17, 1 + i);
         addCompatibleMapping(17, 19 + i);
         addCompatibleMapping(18, 19 + i);
      }

   }

   public static class InRouteFinder extends TubeLib.RouteFinder {
      MachineLib.FilterMap filterMap;
      int subFilt = -1;

      public InRouteFinder(World world, MachineLib.FilterMap map) {
         super(world);
         this.filterMap = map;
      }

      @Override
      public void addPoint(WorldCoord wc, int st, int side, int weight) {
         IInventory inv = MachineLib.getInventory(super.worldObj, wc);
         if (inv == null) {
            super.addPoint(wc, st, side, weight);
         } else {
            int opside = (side ^ 1) & 63;
            int[] slots;
            if (inv instanceof ISidedInventory) {
               ISidedInventory sm = (ISidedInventory)inv;
               slots = sm.getAccessibleSlotsFromSide(opside);
            } else {
               slots = IntStream.range(0, inv.getSizeInventory()).toArray();
            }

            if (this.filterMap.size() == 0) {
               if (!MachineLib.emptyInventory(inv, slots)) {
                  TubeLib.WorldRoute sm2 = new TubeLib.WorldRoute(wc, 0, opside, weight);
                  sm2.solved = true;
                  super.scanpos.add(sm2);
               } else {
                  super.addPoint(wc, st, side, weight);
               }
            } else {
               int sm1 = -1;
               if (this.subFilt < 0) {
                  sm1 = MachineLib.matchAnyStack(this.filterMap, inv, slots);
               } else if (MachineLib.matchOneStack(this.filterMap, inv, slots, this.subFilt)) {
                  sm1 = this.subFilt;
               }

               if (sm1 < 0) {
                  super.addPoint(wc, st, side, weight);
               } else {
                  TubeLib.WorldRoute nr = new TubeLib.WorldRoute(wc, sm1, opside, weight);
                  nr.solved = true;
                  super.scanpos.add(nr);
               }
            }
         }

      }

      public void setSubFilt(int sf) {
         this.subFilt = sf;
      }

      public int getResultSide() {
         return super.result.side;
      }
   }

   private static class OutRouteFinder extends TubeLib.RouteFinder {
      int state;
      TubeItem tubeItem;

      public OutRouteFinder(World world, TubeItem ti, int st) {
         super(world);
         this.state = st;
         this.tubeItem = ti;
      }

      @Override
      public void addPoint(WorldCoord wc, int start, int side, int weight) {
         int opside = (side ^ 1) & 0xFF;
         if (this.state != 3 && this.tubeItem.priority == 0 && MachineLib.canAddToInventory(super.worldObj, this.tubeItem.item, wc, opside)) {
            TubeLib.WorldRoute route = new TubeLib.WorldRoute(wc, start, side, weight);
            route.solved = true;
            super.scanpos.add(route);
         } else {
            ITubeConnectable itc = CoreLib.getTileEntity(super.worldObj, wc, ITubeConnectable.class);
            if (itc != null) {
               if (itc.tubeItemCanEnter(opside, this.state, this.tubeItem)) {
                  TubeLib.WorldRoute route = new TubeLib.WorldRoute(wc, start, opside, weight + itc.tubeWeight(opside, this.state));
                  route.solved = true;
                  super.scanpos.add(route);
               } else if (itc.tubeItemCanEnter(opside, 0, this.tubeItem) && itc.canRouteItems() && !super.scanmap.contains(wc)) {
                  super.scanmap.add(wc);
                  super.scanpos.add(new TubeLib.WorldRoute(wc, start, opside, weight + itc.tubeWeight(opside, this.state)));
               }
            }
         }

      }
   }

   public static class RequestRouteFinder extends TubeLib.RouteFinder {
      TubeItem tubeItem;

      public RequestRouteFinder(World world, TubeItem item) {
         super(world);
         this.tubeItem = item;
      }

      @Override
      public void addPoint(WorldCoord wc, int st, int side, int weight) {
         ITubeRequest itr = CoreLib.getTileEntity(super.worldObj, wc, ITubeRequest.class);
         if (itr != null) {
            if (itr.requestTubeItem(this.tubeItem, false)) {
               TubeLib.WorldRoute itc1 = new TubeLib.WorldRoute(wc, 0, side, weight);
               itc1.solved = true;
               super.scanpos.add(itc1);
            }
         } else {
            ITubeConnectable itc = CoreLib.getTileEntity(super.worldObj, wc, ITubeConnectable.class);
            if (itc != null) {
               int side1 = (side ^ 1) & 0xFF;
               if (itc.tubeItemCanEnter(side1, 0, this.tubeItem) && itc.canRouteItems() && !super.scanmap.contains(wc)) {
                  super.scanmap.add(wc);
                  super.scanpos.add(new TubeLib.WorldRoute(wc, st, side1, weight + itc.tubeWeight(side1, 0)));
               }
            }
         }

      }
   }

   private static class RouteFinder {
      int startDir = 0;
      TubeLib.WorldRoute result;
      World worldObj;
      Set<WorldCoord> scanmap = new HashSet();
      PriorityQueue<TubeLib.WorldRoute> scanpos = new PriorityQueue();

      public RouteFinder(World world) {
         this.worldObj = world;
      }

      public void addPoint(WorldCoord wc, int start, int side, int weight) {
         ITubeConnectable itc = CoreLib.getTileEntity(this.worldObj, wc, ITubeConnectable.class);
         if (itc != null && itc.canRouteItems() && !this.scanmap.contains(wc)) {
            this.scanmap.add(wc);
            this.scanpos.add(new TubeLib.WorldRoute(wc, start, side ^ 1, weight));
         }

      }

      public int find(WorldCoord wc, int sides) {
         for(int wr = 0; wr < 6; ++wr) {
            if ((sides & 1 << wr) != 0) {
               WorldCoord cons = wc.copy();
               cons.step(wr);
               this.addPoint(cons, wr, wr, wr == this.startDir ? 0 : 1);
            }
         }

         while(this.scanpos.size() > 0) {
            TubeLib.WorldRoute route = (TubeLib.WorldRoute)this.scanpos.poll();
            if (route.solved) {
               this.result = route;
               return route.start;
            }

            int cons = TubeLib.getConnections(this.worldObj, route.wc.x, route.wc.y, route.wc.z);

            for(int side = 0; side < 6; ++side) {
               if (side != route.side && (cons & 1 << side) != 0) {
                  WorldCoord wcp = route.wc.copy();
                  wcp.step(side);
                  this.addPoint(wcp, route.start, side, route.weight + 2);
               }
            }
         }

         return -1;
      }

      public WorldCoord getResultPoint() {
         return this.result.wc;
      }
   }

   private static class WorldRoute implements Comparable<TubeLib.WorldRoute> {
      public WorldCoord wc;
      public int start;
      public int side;
      public int weight;
      public boolean solved = false;

      public WorldRoute(WorldCoord w, int st, int s, int wt) {
         this.wc = w;
         this.start = st;
         this.side = s;
         this.weight = wt;
      }

      public int compareTo(TubeLib.WorldRoute wr) {
         return this.weight - wr.weight;
      }
   }
}
