package com.eloraam.redpower.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockCoverable extends BlockMultipart {
   public BlockCoverable(Material material) {
      super(material);
   }

   public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
      TileCoverable tc = CoreLib.getTileEntity(world, i, j, k, TileCoverable.class);
      return tc != null && tc.isSideNormal(side.ordinal());
   }

   public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double srcX, double srcY, double srcZ) {
      Vec3 org = Vec3.createVectorHelper(srcX, srcY, srcZ);
      Vec3 end = Vec3.createVectorHelper((double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
      Block bl = world.getBlock(x, y, z);
      if (bl == null) {
         return 0.0F;
      } else {
         MovingObjectPosition mop = bl.collisionRayTrace(world, x, y, z, org, end);
         if (mop == null) {
            return bl.getExplosionResistance(exploder);
         } else {
            TileCoverable tl = CoreLib.getTileEntity(world, x, y, z, TileCoverable.class);
            if (tl == null) {
               return bl.getExplosionResistance(exploder);
            } else {
               float er = tl.getExplosionResistance(mop.subHit, mop.sideHit, exploder);
               return er < 0.0F ? bl.getExplosionResistance(exploder) : er;
            }
         }
      }
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
      TileCoverable tile = CoreLib.getTileEntity(world, x, y, z, TileCoverable.class);
      return tile != null ? tile.getPickBlock(target, player) : null;
   }
}
