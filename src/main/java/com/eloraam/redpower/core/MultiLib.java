package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerBase;
import java.util.List;
import net.minecraft.world.World;

public class MultiLib {
   public static boolean isClear(World world, WorldCoord parent, List<WorldCoord> coords) {
      for(WorldCoord wc : coords) {
         if (!RedPowerBase.blockMultiblock.canPlaceBlockAt(world, wc.x, wc.y, wc.z)) {
            TileMultiblock tmb = CoreLib.getTileEntity(world, wc, TileMultiblock.class);
            if (tmb == null) {
               return false;
            }

            if (tmb.relayX != parent.x || tmb.relayY != parent.y || tmb.relayZ != parent.z) {
               return false;
            }
         }
      }

      return true;
   }

   public static void addRelays(World world, WorldCoord parent, int md, List<WorldCoord> coords) {
      int num = 0;

      for(WorldCoord wc : coords) {
         world.setBlock(wc.x, wc.y, wc.z, RedPowerBase.blockMultiblock, md, 3);
         TileMultiblock tmb = CoreLib.getTileEntity(world, wc, TileMultiblock.class);
         if (tmb != null) {
            tmb.relayX = parent.x;
            tmb.relayY = parent.y;
            tmb.relayZ = parent.z;
            tmb.relayNum = num++;
         }
      }

   }

   public static void removeRelays(World world, WorldCoord parent, List<WorldCoord> coords) {
      for(WorldCoord wc : coords) {
         TileMultiblock tmb = CoreLib.getTileEntity(world, wc, TileMultiblock.class);
         if (tmb != null && tmb.relayX == parent.x && tmb.relayY == parent.y && tmb.relayZ == parent.z) {
            world.setBlockToAir(wc.x, wc.y, wc.z);
         }
      }

   }
}
