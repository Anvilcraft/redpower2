package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeHooks;

public abstract class TileCoverable extends TileMultipart implements ICoverable, IMultipart {
   @Override
   public abstract boolean canAddCover(int var1, int var2);

   @Override
   public abstract boolean tryAddCover(int var1, int var2);

   @Override
   public abstract int tryRemoveCover(int var1);

   @Override
   public abstract int getCover(int var1);

   @Override
   public abstract int getCoverMask();

   @Override
   public boolean isSideSolid(int side) {
      int cm = this.getCoverMask();
      return (cm & 1 << side) > 0;
   }

   @Override
   public boolean isSideNormal(int side) {
      int cm = this.getCoverMask();
      if ((cm & 1 << side) == 0) {
         return false;
      } else {
         int c = this.getCover(side);
         int n = c >> 8;
         return !CoverLib.isTransparent(c & 0xFF) && (n < 3 || n >= 6 && n <= 9);
      }
   }

   public void addCoverableHarvestContents(List<ItemStack> drops) {
      if (CoverLib.blockCoverPlate != null) {
         for(int i = 0; i < 29; ++i) {
            int j = this.getCover(i);
            if (j >= 0) {
               drops.add(CoverLib.convertCoverPlate(i, j));
            }
         }
      }

   }

   @Override
   public void addHarvestContents(List<ItemStack> ist) {
      this.addCoverableHarvestContents(ist);
   }

   @Override
   public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
      int i = this.tryRemoveCover(part);
      if (i >= 0) {
         if (willHarvest) {
            this.dropCover(part, i);
         }

         if (this.blockEmpty()) {
            this.deleteBlock();
         }
      }

      this.updateBlock();
   }

   @Override
   public float getPartStrength(EntityPlayer player, int part) {
      int i = this.getCover(part);
      if (i < 0) {
         return 0.0F;
      } else {
         i &= 255;
         float hv = CoverLib.getMiningHardness(i);
         if (hv < 0.0F) {
            return 0.0F;
         } else {
            ItemStack ist = CoverLib.getItemStack(i);
            Block bl = Block.getBlockFromItem(ist.getItem());
            int md = ist.getItemDamage();
            return !ForgeHooks.canHarvestBlock(bl, player, md) ? 1.0F / hv / 100.0F : player.getBreakSpeed(bl, false, md) / hv / 30.0F;
         }
      }
   }

   @Override
   public void setPartBounds(BlockMultipart block, int part) {
      int i = this.getCover(part);
      float th = CoverLib.getThickness(part, i);
      switch(part) {
         case 0:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, th, 1.0F);
            break;
         case 1:
            block.setBlockBounds(0.0F, 1.0F - th, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case 2:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, th);
            break;
         case 3:
            block.setBlockBounds(0.0F, 0.0F, 1.0F - th, 1.0F, 1.0F, 1.0F);
            break;
         case 4:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, th, 1.0F, 1.0F);
            break;
         case 5:
            block.setBlockBounds(1.0F - th, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case 6:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, th, th, th);
            break;
         case 7:
            block.setBlockBounds(0.0F, 0.0F, 1.0F - th, th, th, 1.0F);
            break;
         case 8:
            block.setBlockBounds(1.0F - th, 0.0F, 0.0F, 1.0F, th, th);
            break;
         case 9:
            block.setBlockBounds(1.0F - th, 0.0F, 1.0F - th, 1.0F, th, 1.0F);
            break;
         case 10:
            block.setBlockBounds(0.0F, 1.0F - th, 0.0F, th, 1.0F, th);
            break;
         case 11:
            block.setBlockBounds(0.0F, 1.0F - th, 1.0F - th, th, 1.0F, 1.0F);
            break;
         case 12:
            block.setBlockBounds(1.0F - th, 1.0F - th, 0.0F, 1.0F, 1.0F, th);
            break;
         case 13:
            block.setBlockBounds(1.0F - th, 1.0F - th, 1.0F - th, 1.0F, 1.0F, 1.0F);
            break;
         case 14:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, th, th);
            break;
         case 15:
            block.setBlockBounds(0.0F, 0.0F, 1.0F - th, 1.0F, th, 1.0F);
            break;
         case 16:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, th, th, 1.0F);
            break;
         case 17:
            block.setBlockBounds(1.0F - th, 0.0F, 0.0F, 1.0F, th, 1.0F);
            break;
         case 18:
            block.setBlockBounds(0.0F, 0.0F, 0.0F, th, 1.0F, th);
            break;
         case 19:
            block.setBlockBounds(0.0F, 0.0F, 1.0F - th, th, 1.0F, 1.0F);
            break;
         case 20:
            block.setBlockBounds(1.0F - th, 0.0F, 0.0F, 1.0F, 1.0F, th);
            break;
         case 21:
            block.setBlockBounds(1.0F - th, 0.0F, 1.0F - th, 1.0F, 1.0F, 1.0F);
            break;
         case 22:
            block.setBlockBounds(0.0F, 1.0F - th, 0.0F, 1.0F, 1.0F, th);
            break;
         case 23:
            block.setBlockBounds(0.0F, 1.0F - th, 1.0F - th, 1.0F, 1.0F, 1.0F);
            break;
         case 24:
            block.setBlockBounds(0.0F, 1.0F - th, 0.0F, th, 1.0F, 1.0F);
            break;
         case 25:
            block.setBlockBounds(1.0F - th, 1.0F - th, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case 26:
            block.setBlockBounds(0.5F - th, 0.0F, 0.5F - th, 0.5F + th, 1.0F, 0.5F + th);
            break;
         case 27:
            block.setBlockBounds(0.5F - th, 0.5F - th, 0.0F, 0.5F + th, 0.5F + th, 1.0F);
            break;
         case 28:
            block.setBlockBounds(0.0F, 0.5F - th, 0.5F - th, 1.0F, 0.5F + th, 0.5F + th);
      }

   }

   @Override
   public int getSolidPartsMask() {
      return this.getCoverMask();
   }

   @Override
   public int getPartsMask() {
      return this.getCoverMask();
   }

   public void dropCover(int side, int cov) {
      ItemStack ist = CoverLib.convertCoverPlate(side, cov);
      if (ist != null) {
         CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, ist);
      }

   }

   public ItemStack getCover(int part, int side) {
      int i = this.getCover(part);
      if (i >= 0) {
         return CoverLib.convertCoverPlate(side, i);
      } else {
         List<ItemStack> ist = new ArrayList();
         this.addHarvestContents(ist);
         return ist.size() >= 1 ? (ItemStack)ist.get(0) : null;
      }
   }

   public ItemStack getPickBlock(MovingObjectPosition target, EntityPlayer player) {
      int i = this.getCover(target.subHit);
      return i > 0 ? CoverLib.convertCoverPlate(target.sideHit, i) : this.getBasePickStack();
   }

   protected ItemStack getBasePickStack() {
      return null;
   }

   public float getExplosionResistance(int part, int side, Entity exploder) {
      int i = this.getCover(part);
      if (i < 0) {
         return -1.0F;
      } else {
         i &= 255;
         ItemStack ist = CoverLib.getItemStack(i);
         return Block.getBlockFromItem(ist.getItem()).getExplosionResistance(exploder);
      }
   }
}
