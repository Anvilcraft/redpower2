package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.WorldCoord;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockFrame extends BlockCoverable {
   public BlockFrame() {
      super(CoreLib.materialRedpower);
      this.setHardness(0.5F);
      this.setCreativeTab(CreativeExtraTabs.tabMachine);
   }

   @Override
   public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity ent) {
      TileFrameMoving tl = CoreLib.getTileEntity(world, x, y, z, TileFrameMoving.class);
      if (tl == null) {
         super.addCollisionBoxesToList(world, x, y, z, box, list, ent);
      } else {
         this.computeCollidingBoxes(world, x, y, z, box, list, tl);
         TileMotor tm = CoreLib.getTileEntity(world, tl.motorX, tl.motorY, tl.motorZ, TileMotor.class);
         if (tm != null) {
            WorldCoord wc = new WorldCoord(x, y, z);
            wc.step(tm.MoveDir ^ 1);
            tl = CoreLib.getTileEntity(world, wc, TileFrameMoving.class);
            if (tl != null) {
               this.computeCollidingBoxes(world, wc.x, wc.y, wc.z, box, list, tl);
            }
         }
      }

   }
}
