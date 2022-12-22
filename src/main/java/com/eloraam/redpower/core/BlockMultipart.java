package com.eloraam.redpower.core;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockMultipart extends BlockExtended {
   public BlockMultipart(Material material) {
      super(material);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
      TileMultipart tl = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
      if (tl == null) {
         world.setBlockToAir(x, y, z);
      } else {
         tl.onBlockNeighborChange(block);
      }

   }

   @Override
   public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
      if (!world.isRemote) {
         MovingObjectPosition mop = CoreLib.retraceBlock(world, player, x, y, z);
         if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
            TileMultipart tl = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
            if (tl != null) {
               tl.onHarvestPart(player, mop.subHit, willHarvest);
            }
         }
      }

      return false;
   }

   public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
      return false;
   }

   @Override
   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xp, float yp, float zp) {
      MovingObjectPosition pos = CoreLib.retraceBlock(world, player, x, y, z);
      if (pos == null) {
         return false;
      } else if (pos.typeOfHit != MovingObjectType.BLOCK) {
         return false;
      } else {
         TileMultipart tl = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
         return tl != null && tl.onPartActivateSide(player, pos.subHit, pos.sideHit);
      }
   }

   public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
      MovingObjectPosition pos = CoreLib.retraceBlock(world, player, x, y, z);
      if (pos == null) {
         return 0.0F;
      } else if (pos.typeOfHit != MovingObjectType.BLOCK) {
         return 0.0F;
      } else {
         TileMultipart tl = CoreLib.getTileEntity(player.worldObj, x, y, z, TileMultipart.class);
         return tl == null ? 0.0F : tl.getPartStrength(player, pos.subHit);
      }
   }

   public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
      TileMultipart tl = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
      if (tl != null) {
         tl.breakBlock();
      }

   }

   public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity ent) {
      TileMultipart tl = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
      if (tl != null) {
         int pm = tl.getSolidPartsMask();

         while(pm > 0) {
            int pt = Integer.numberOfTrailingZeros(pm);
            pm &= ~(1 << pt);
            tl.setPartBounds(this, pt);
            super.addCollisionBoxesToList(world, x, y, z, box, list, ent);
         }
      }

   }

   @Override
   public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
   }

   public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
      TileMultipart multipart = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
      if (multipart == null) {
         return null;
      } else {
         int pm = multipart.getPartsMask();
         MovingObjectPosition result = null;
         int cpt = -1;
         double distance = 0.0;

         while(pm > 0) {
            int pt = Integer.numberOfTrailingZeros(pm);
            pm &= ~(1 << pt);
            multipart.setPartBounds(this, pt);
            MovingObjectPosition mop = super.collisionRayTrace(world, x, y, z, start, end);
            if (mop != null) {
               double max = mop.hitVec.squareDistanceTo(start);
               if (result == null || max < distance) {
                  distance = max;
                  result = mop;
                  cpt = pt;
               }
            }
         }

         if (result == null) {
            return null;
         } else {
            multipart.setPartBounds(this, cpt);
            result.subHit = cpt;
            return result;
         }
      }
   }

   protected MovingObjectPosition traceCurrentBlock(World world, int x, int y, int z, Vec3 src, Vec3 dest) {
      return super.collisionRayTrace(world, x, y, z, src, dest);
   }

   public void setPartBounds(World world, int x, int y, int z, int part) {
      TileMultipart tl = CoreLib.getTileEntity(world, x, y, z, TileMultipart.class);
      if (tl == null) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         tl.setPartBounds(this, part);
      }

   }

   public void computeCollidingBoxes(World world, int x, int y, int z, AxisAlignedBB box, List list, TileMultipart tl) {
      int pm = tl.getSolidPartsMask();

      while(pm > 0) {
         int pt = Integer.numberOfTrailingZeros(pm);
         pm &= ~(1 << pt);
         tl.setPartBounds(this, pt);
         super.addCollisionBoxesToList(world, x, y, z, box, list, null);
      }

   }
}
