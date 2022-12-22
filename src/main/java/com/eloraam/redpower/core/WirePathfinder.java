package com.eloraam.redpower.core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class WirePathfinder {
   Set<WorldCoord> scanmap;
   LinkedList<WorldCoord> scanpos;

   public void init() {
      this.scanmap = new HashSet();
      this.scanpos = new LinkedList();
   }

   public void addSearchBlock(WorldCoord wc) {
      if (!this.scanmap.contains(wc)) {
         this.scanmap.add(wc);
         this.scanpos.addLast(wc);
      }

   }

   private void addIndBl(WorldCoord wc, int d1, int d2) {
      wc = wc.coordStep(d1);
      int d3;
      switch(d1) {
         case 0:
            d3 = d2 + 2;
            break;
         case 1:
            d3 = d2 + 2;
            break;
         case 2:
            d3 = d2 + (d2 & 2);
            break;
         case 3:
            d3 = d2 + (d2 & 2);
            break;
         case 4:
            d3 = d2;
            break;
         default:
            d3 = d2;
      }

      wc.step(d3);
      this.addSearchBlock(wc);
   }

   public void addSearchBlocks(WorldCoord wc, int cons, int indcon) {
      for(int side = 0; side < 6; ++side) {
         if ((cons & RedPowerLib.getConDirMask(side)) > 0) {
            this.addSearchBlock(wc.coordStep(side));
         }
      }

      for(int side = 0; side < 6; ++side) {
         for(int b = 0; b < 4; ++b) {
            if ((indcon & 1 << side * 4 + b) > 0) {
               this.addIndBl(wc, side, b);
            }
         }
      }

   }

   public boolean step(WorldCoord coord) {
      return false;
   }

   public boolean iterate() {
      if (this.scanpos.size() == 0) {
         return false;
      } else {
         WorldCoord wc = (WorldCoord)this.scanpos.removeFirst();
         return this.step(wc);
      }
   }
}
