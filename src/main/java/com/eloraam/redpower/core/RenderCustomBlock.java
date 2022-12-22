package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

@SideOnly(Side.CLIENT)
public abstract class RenderCustomBlock extends TileEntitySpecialRenderer implements IItemRenderer {
   protected Block block;

   public RenderCustomBlock(Block block) {
      this.block = block;
   }

   protected int getMixedBrightness(TileEntity tile) {
      return tile.getBlockType().getMixedBrightnessForBlock(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
   }

   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
   }

   public boolean renderHit(EffectRenderer effectRenderer, World world, MovingObjectPosition target, int x, int y, int z, int side, int meta) {
      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      TileEntity tile = world.getTileEntity(x, y, z);
      if (tile != null && tile instanceof TileCoverable) {
         TileCoverable coverable = (TileCoverable)tile;
         Block block = coverable.getBlockType();
         int cvr = coverable.getCover(target.subHit);
         if (cvr >= 0) {
            Block bl = CoverLib.getBlock(cvr & 0xFF);
            int m = CoverLib.getMeta(cvr & 0xFF);
            if (bl != null && bl != Blocks.air) {
               float f = 0.1F;
               double dx = (double)x
                  + world.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double)(f * 2.0F))
                  + (double)f
                  + block.getBlockBoundsMinX();
               double dy = (double)y
                  + world.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double)(f * 2.0F))
                  + (double)f
                  + block.getBlockBoundsMinY();
               double dz = (double)z
                  + world.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double)(f * 2.0F))
                  + (double)f
                  + block.getBlockBoundsMinZ();
               switch(side) {
                  case 0:
                     dy = (double)y + block.getBlockBoundsMinY() - (double)f;
                     break;
                  case 1:
                     dy = (double)y + block.getBlockBoundsMaxY() + (double)f;
                     break;
                  case 2:
                     dz = (double)z + block.getBlockBoundsMinZ() - (double)f;
                     break;
                  case 3:
                     dz = (double)z + block.getBlockBoundsMaxZ() + (double)f;
                     break;
                  case 4:
                     dx = (double)x + block.getBlockBoundsMinX() - (double)f;
                     break;
                  case 5:
                     dx = (double)x + block.getBlockBoundsMaxX() + (double)f;
               }

               effectRenderer.addEffect(
                  new EntityDiggingFX(world, dx, dy, dz, 0.0, 0.0, 0.0, bl, m, target.sideHit).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F)
               );
            }

            return true;
         }
      }

      if (tile != null) {
         float f = 0.1F;
         double dx = (double)x
            + world.rand.nextDouble() * (this.block.getBlockBoundsMaxX() - this.block.getBlockBoundsMinX() - (double)(f * 2.0F))
            + (double)f
            + this.block.getBlockBoundsMinX();
         double dy = (double)y
            + world.rand.nextDouble() * (this.block.getBlockBoundsMaxY() - this.block.getBlockBoundsMinY() - (double)(f * 2.0F))
            + (double)f
            + this.block.getBlockBoundsMinY();
         double dz = (double)z
            + world.rand.nextDouble() * (this.block.getBlockBoundsMaxZ() - this.block.getBlockBoundsMinZ() - (double)(f * 2.0F))
            + (double)f
            + this.block.getBlockBoundsMinZ();
         switch(side) {
            case 0:
               dy = (double)y + this.block.getBlockBoundsMinY() - (double)f;
               break;
            case 1:
               dy = (double)y + this.block.getBlockBoundsMaxY() + (double)f;
               break;
            case 2:
               dz = (double)z + this.block.getBlockBoundsMinZ() - (double)f;
               break;
            case 3:
               dz = (double)z + this.block.getBlockBoundsMaxZ() + (double)f;
               break;
            case 4:
               dx = (double)x + this.block.getBlockBoundsMinX() - (double)f;
               break;
            case 5:
               dx = (double)x + this.block.getBlockBoundsMaxX() + (double)f;
         }

         int color = this.getParticleColorForSide(world, x, y, z, tile, side, meta);
         IIcon icon = this.getParticleIconForSide(world, x, y, z, tile, side, meta);
         if (icon != null) {
            effectRenderer.addEffect(
               new EntityCustomDiggingFX(world, dx, dy, dz, 0.0, 0.0, 0.0, icon, color).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F)
            );
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean renderDestroy(EffectRenderer effectRenderer, World world, int x, int y, int z, int meta) {
      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      TileEntity tile = world.getTileEntity(x, y, z);
      MovingObjectPosition target = Minecraft.getMinecraft().thePlayer.rayTrace(5.0, 1.0F);
      if (tile != null && tile instanceof TileCoverable && target != null && target.blockX == x && target.blockY == y && target.blockZ == z) {
         TileCoverable coverable = (TileCoverable)tile;
         int cvr = coverable.getCover(target.subHit);
         if (cvr >= 0) {
            Block bl = CoverLib.getBlock(cvr & 0xFF);
            int m = CoverLib.getMeta(cvr & 0xFF);
            if (bl != null && bl != Blocks.air) {
               byte offset = 4;

               for(int xoff = 0; xoff < offset; ++xoff) {
                  for(int yoff = 0; yoff < offset; ++yoff) {
                     for(int zoff = 0; zoff < offset; ++zoff) {
                        double xc = (double)x + ((double)xoff + 0.5) / (double)offset;
                        double yc = (double)y + ((double)yoff + 0.5) / (double)offset;
                        double zc = (double)z + ((double)zoff + 0.5) / (double)offset;
                        effectRenderer.addEffect(
                           new EntityDiggingFX(world, xc, yc, zc, xc - (double)x - 0.5, yc - (double)y - 0.5, zc - (double)z - 0.5, bl, m, target.sideHit)
                        );
                     }
                  }
               }
            }

            return true;
         }
      }

      if (tile == null) {
         return false;
      } else {
         byte offset = 4;

         for(int xoff = 0; xoff < offset; ++xoff) {
            for(int yoff = 0; yoff < offset; ++yoff) {
               for(int zoff = 0; zoff < offset; ++zoff) {
                  double xc = (double)x + ((double)xoff + 0.5) / (double)offset;
                  double yc = (double)y + ((double)yoff + 0.5) / (double)offset;
                  double zc = (double)z + ((double)zoff + 0.5) / (double)offset;
                  int side = world.rand.nextInt(6);
                  int color = this.getParticleColorForSide(world, x, y, z, tile, side, meta);
                  IIcon icon = this.getParticleIconForSide(world, x, y, z, tile, side, meta);
                  if (icon != null) {
                     effectRenderer.addEffect(
                        new EntityCustomDiggingFX(world, xc, yc, zc, xc - (double)x - 0.5, yc - (double)y - 0.5, zc - (double)z - 0.5, icon, color)
                     );
                  }
               }
            }
         }

         return true;
      }
   }

   protected IIcon getParticleIconForSide(World world, int x, int y, int z, TileEntity tile, int side, int meta) {
      return null;
   }

   protected int getParticleColorForSide(World world, int x, int y, int z, TileEntity tile, int side, int meta) {
      return 16777215;
   }

   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return true;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return true;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
   }
}
