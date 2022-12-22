package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockShapedLamp extends BlockExtended {
   public BlockShapedLamp() {
      super(CoreLib.materialRedpower);
      this.setHardness(1.0F);
      this.setCreativeTab(RedPowerLighting.tabLamp);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean renderAsNormalBlock() {
      return false;
   }

   public boolean isNormalCube() {
      return false;
   }

   public boolean canProvidePower() {
      return true;
   }

   public boolean canRenderInPass(int pass) {
      return true;
   }

   public int getRenderBlockPass() {
      return 1;
   }

   public int getLightValue(IBlockAccess iba, int x, int y, int z) {
      TileShapedLamp lamp = CoreLib.getTileEntity(iba, x, y, z, TileShapedLamp.class);
      return lamp == null ? 0 : lamp.getLightValue();
   }

   public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
      TileShapedLamp lamp = CoreLib.getTileEntity(iba, x, y, z, TileShapedLamp.class);
      if (lamp != null) {
         AxisAlignedBB bb;
         switch(lamp.Style) {
            case 0:
               bb = this.getRotatedBB(0.125F, 0.0F, 0.125F, 0.875F, 0.5F, 0.875F, lamp.Rotation);
               break;
            case 1:
               bb = this.getRotatedBB(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.75F, 0.8125F, lamp.Rotation);
               break;
            default:
               bb = this.getRotatedBB(0.125F, 0.0F, 0.125F, 0.875F, 0.5F, 0.875F, lamp.Rotation);
         }

         this.setBlockBounds((float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ);
      }

      super.setBlockBoundsBasedOnState(iba, x, y, z);
   }

   private AxisAlignedBB getRotatedBB(float x1, float y1, float z1, float x2, float y2, float z2, int rot) {
      switch(rot) {
         case 0:
            return AxisAlignedBB.getBoundingBox((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
         case 1:
            return AxisAlignedBB.getBoundingBox((double)x1, (double)(1.0F - y2), (double)z1, (double)x2, (double)(1.0F - y1), (double)z2);
         case 2:
            return AxisAlignedBB.getBoundingBox((double)x1, (double)z1, (double)y1, (double)x2, (double)z2, (double)y2);
         case 3:
            return AxisAlignedBB.getBoundingBox((double)x1, (double)(1.0F - z2), (double)(1.0F - y2), (double)x2, (double)(1.0F - z1), (double)(1.0F - y1));
         case 4:
            return AxisAlignedBB.getBoundingBox((double)y1, (double)x1, (double)z1, (double)y2, (double)x2, (double)z2);
         default:
            return AxisAlignedBB.getBoundingBox((double)(1.0F - y2), (double)(1.0F - x2), (double)z1, (double)(1.0F - y1), (double)(1.0F - x1), (double)z2);
      }
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
      TileShapedLamp lamp = CoreLib.getTileEntity(world, x, y, z, TileShapedLamp.class);
      return lamp != null ? new ItemStack(this, 1, (lamp.getExtendedID() << 10) + (lamp.Style << 5) + (lamp.Inverted ? 16 : 0) + lamp.Color) : null;
   }
}
