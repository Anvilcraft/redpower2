package com.eloraam.redpower.control;

import com.eloraam.redpower.wiring.TileWiring;
import net.minecraft.block.Block;

public class TileRibbon extends TileWiring {
   @Override
   public int getExtendedID() {
      return 12;
   }

   @Override
   public int getConnectClass(int side) {
      return 66;
   }

   @Override
   public void onBlockNeighborChange(Block block) {
      super.onBlockNeighborChange(block);
      this.getConnectionMask();
      this.getExtConnectionMask();
   }
}
