package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTube extends RenderCovers {
   protected int[] paintColors = new int[]{
      16777215, 16744448, 16711935, 7110911, 16776960, 65280, 16737408, 5460819, 9671571, 65535, 8388863, 255, 5187328, 32768, 16711680, 2039583
   };
   protected EntityItem item = new EntityItem((World)null);

   public RenderTube(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileTube tube = (TileTube)tile;
      World world = tube.getWorldObj();
      int metadata = tube.getBlockMetadata();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      int lv = world.getLightBrightnessForSkyBlocks(tube.xCoord, tube.yCoord, tube.zCoord, 0);
      tess.setBrightness(lv);
      tess.startDrawingQuads();
      super.context.bindBlockTexture();
      super.context.exactTextureCoordinates = true;
      super.context.setTexFlags(55);
      super.context.setTint(1.0F, 1.0F, 1.0F);
      super.context.setPos(x, y, z);
      if (tube.CoverSides > 0) {
         super.context.readGlobalLights(world, tube.xCoord, tube.yCoord, tube.zCoord);
         this.renderCovers(tube.CoverSides, tube.Covers);
      }

      int cons = TubeLib.getConnections(world, tube.xCoord, tube.yCoord, tube.zCoord);
      super.context.setBrightness(this.getMixedBrightness(tube));
      super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      super.context.setPos(x, y, z);
      switch(metadata) {
         case 10:
            this.renderCenterBlock(cons, RedPowerMachine.restrictTubeSide, RedPowerMachine.restrictTubeFace);
            break;
         case 11:
            if (this.renderMagFins(cons, metadata)) {
               this.renderCenterBlock(cons, RedPowerMachine.magTubeSide, RedPowerMachine.magTubeFace);
            } else {
               this.renderCenterBlock(cons, RedPowerMachine.magTubeSideNR, RedPowerMachine.magTubeFaceNR);
            }
            break;
         default:
            this.renderCenterBlock(cons, RedPowerMachine.baseTubeSide, RedPowerMachine.baseTubeFace);
      }

      if (tube.paintColor > 0) {
         int tc = this.paintColors[tube.paintColor - 1];
         super.context.setTint((float)(tc >> 16) / 255.0F, (float)(tc >> 8 & 0xFF) / 255.0F, (float)(tc & 0xFF) / 255.0F);
         if (metadata == 10) {
            this.renderBlockPaint(cons, RedPowerMachine.restrictTubeFaceColor, RedPowerMachine.restrictTubeSideColor, metadata);
         } else {
            this.renderBlockPaint(cons, RedPowerMachine.baseTubeFaceColor, RedPowerMachine.baseTubeSideColor, metadata);
         }
      }

      tess.draw();
      this.item.worldObj = world;
      this.item.setPosition((double)tube.xCoord + 0.5, (double)tube.yCoord + 0.5, (double)tube.zCoord + 0.5);
      RenderItem renderitem = (RenderItem)RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
      this.item.age = 0;
      this.item.hoverStart = 0.0F;
      WorldCoord offset = new WorldCoord(0, 0, 0);
      TubeFlow flow = tube.getTubeFlow();

      for(TubeItem item : flow.contents) {
         this.item.setEntityItemStack(item.item);
         offset.x = 0;
         offset.y = 0;
         offset.z = 0;
         offset.step(item.side);
         double d = (double)item.progress / 128.0 * 0.5;
         if (!item.scheduled) {
            d = 0.5 - d;
         }

         double yo = 0.0;
         if (Item.getIdFromItem(item.item.getItem()) >= 256) {
            yo += 0.1;
         }

         renderitem.doRender(
            this.item,
            x + 0.5 + (double)offset.x * d,
            y + 0.5 - (double)this.item.yOffset - yo + (double)offset.y * d,
            z + 0.5 + (double)offset.z * d,
            0.0F,
            0.0F
         );
         if (item.color > 0) {
            super.context.bindBlockTexture();
            tess.startDrawingQuads();
            super.context.useNormal = true;
            super.context.setDefaults();
            super.context.setBrightness(lv);
            super.context.setPos(x + (double)offset.x * d, y + (double)offset.y * d, z + (double)offset.z * d);
            super.context.setTintHex(this.paintColors[item.color - 1]);
            super.context.setIcon(RedPowerMachine.tubeItemOverlay);
            super.context.renderBox(63, 0.26F, 0.26F, 0.26F, 0.74F, 0.74F, 0.74F);
            tess.draw();
         }
      }

      GL11.glEnable(2896);
   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      int meta = item.getItemDamage();
      super.block.setBlockBoundsForItemRender();
      super.context.setDefaults();
      if (type == ItemRenderType.INVENTORY) {
         super.context.setPos(-0.5, -0.5, -0.5);
      } else {
         super.context.setPos(0.0, 0.0, 0.0);
      }

      super.context.useNormal = true;
      super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      super.context.useNormal = true;
      if (meta >> 8 == 10) {
         super.context
            .setIcon(
               RedPowerMachine.baseTubeFace,
               RedPowerMachine.baseTubeFace,
               RedPowerMachine.restrictTubeSide,
               RedPowerMachine.restrictTubeSide,
               RedPowerMachine.restrictTubeSide,
               RedPowerMachine.restrictTubeSide
            );
      } else if (meta >> 8 == 11) {
         this.renderMagFins(3, meta);
         super.context
            .setIcon(
               RedPowerMachine.magTubeFaceNR,
               RedPowerMachine.magTubeFaceNR,
               RedPowerMachine.magTubeSideNR,
               RedPowerMachine.magTubeSideNR,
               RedPowerMachine.magTubeSideNR,
               RedPowerMachine.magTubeSideNR
            );
      } else {
         super.context
            .setIcon(
               RedPowerMachine.baseTubeFace,
               RedPowerMachine.baseTubeFace,
               RedPowerMachine.baseTubeSide,
               RedPowerMachine.baseTubeSide,
               RedPowerMachine.baseTubeSide,
               RedPowerMachine.baseTubeSide
            );
      }

      super.context.renderBox(63, 0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
      super.context.renderBox(63, 0.74F, 0.99F, 0.74F, 0.26F, 0.01F, 0.26F);
      tess.draw();
      super.context.useNormal = false;
   }

   private void doubleBox(int sides, float x1, float y1, float z1, float x2, float y2, float z2) {
      int s2 = sides << 1 & 42 | sides >> 1 & 21;
      super.context.renderBox(sides, (double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
      super.context.renderBox(s2, (double)x2, (double)y2, (double)z2, (double)x1, (double)y1, (double)z1);
   }

   public boolean renderMagFins(int cons, int md) {
      if (cons == 3) {
         super.context.setTexFlags(0);
         super.context
            .setIcon(
               RedPowerMachine.magTubeFace,
               RedPowerMachine.magTubeFace,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing
            );
         super.context.renderBox(63, 0.125, 0.125, 0.125, 0.875, 0.375, 0.875);
         super.context.renderBox(63, 0.125, 0.625, 0.125, 0.875, 0.875, 0.875);
         return true;
      } else if (cons == 12) {
         super.context.setTexFlags(147492);
         super.context
            .setIcon(
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeFace,
               RedPowerMachine.magTubeFace,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing
            );
         super.context.renderBox(63, 0.125, 0.125, 0.125, 0.875, 0.875, 0.375);
         super.context.renderBox(63, 0.125, 0.125, 0.625, 0.875, 0.875, 0.875);
         return true;
      } else if (cons == 48) {
         super.context.setTexFlags(2304);
         super.context
            .setIcon(
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeRing,
               RedPowerMachine.magTubeFace,
               RedPowerMachine.magTubeFace
            );
         super.context.renderBox(63, 0.125, 0.125, 0.125, 0.375, 0.875, 0.875);
         super.context.renderBox(63, 0.625, 0.125, 0.125, 0.875, 0.875, 0.875);
         return true;
      } else {
         return false;
      }
   }

   public void renderCenterBlock(int cons, IIcon side, IIcon end) {
      if (cons == 0) {
         super.context.setIcon(end);
         this.doubleBox(63, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
      } else if (cons == 3) {
         super.context.setTexFlags(1773);
         super.context.setIcon(end, end, side, side, side, side);
         this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
      } else if (cons == 12) {
         super.context.setTexFlags(184365);
         super.context.setIcon(side, side, end, end, side, side);
         this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
      } else if (cons == 48) {
         super.context.setTexFlags(187200);
         super.context.setIcon(side, side, side, side, end, end);
         this.doubleBox(15, 0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
      } else {
         super.context.setIcon(end);
         this.doubleBox(63 ^ cons, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
         if ((cons & 1) > 0) {
            super.context.setTexFlags(1773);
            super.context.setIcon(end, end, side, side, side, side);
            this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 0.25F, 0.75F);
         }

         if ((cons & 2) > 0) {
            super.context.setTexFlags(1773);
            super.context.setIcon(end, end, side, side, side, side);
            this.doubleBox(60, 0.25F, 0.75F, 0.25F, 0.75F, 1.0F, 0.75F);
         }

         if ((cons & 4) > 0) {
            super.context.setTexFlags(184365);
            super.context.setIcon(side, side, end, end, side, side);
            this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.25F);
         }

         if ((cons & 8) > 0) {
            super.context.setTexFlags(184365);
            super.context.setIcon(side, side, end, end, side, side);
            this.doubleBox(51, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F, 1.0F);
         }

         if ((cons & 16) > 0) {
            super.context.setTexFlags(187200);
            super.context.setIcon(side, side, side, side, end, end);
            this.doubleBox(15, 0.0F, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F);
         }

         if ((cons & 32) > 0) {
            super.context.setTexFlags(187200);
            super.context.setIcon(side, side, side, side, end, end);
            this.doubleBox(15, 0.75F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
         }
      }

   }

   public void renderBlockPaint(int cons, IIcon faceIcon, IIcon sideIcon, int meta) {
      if (cons != 0) {
         if (cons == 3) {
            super.context.setTexFlags(1773);
            super.context.setIcon(null, null, sideIcon, sideIcon, sideIcon, sideIcon);
            this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
         } else if (cons == 12) {
            super.context.setTexFlags(184365);
            super.context.setIcon(sideIcon, sideIcon, null, null, sideIcon, sideIcon);
            this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
         } else if (cons == 48) {
            super.context.setTexFlags(187200);
            super.context.setIcon(sideIcon, sideIcon, sideIcon, sideIcon, null, null);
            this.doubleBox(15, 0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
         } else {
            super.context.setIcon(faceIcon);
            this.doubleBox(63 ^ cons, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
            if ((cons & 1) > 0) {
               super.context.setTexFlags(1773);
               super.context.setIcon(faceIcon, faceIcon, sideIcon, sideIcon, sideIcon, sideIcon);
               this.doubleBox(60, 0.25F, 0.0F, 0.25F, 0.75F, 0.25F, 0.75F);
            }

            if ((cons & 2) > 0) {
               super.context.setTexFlags(1773);
               super.context.setIcon(faceIcon, faceIcon, sideIcon, sideIcon, sideIcon, sideIcon);
               this.doubleBox(60, 0.25F, 0.75F, 0.25F, 0.75F, 1.0F, 0.75F);
            }

            if ((cons & 4) > 0) {
               super.context.setTexFlags(184365);
               super.context.setIcon(sideIcon, sideIcon, faceIcon, faceIcon, sideIcon, sideIcon);
               this.doubleBox(51, 0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.25F);
            }

            if ((cons & 8) > 0) {
               super.context.setTexFlags(184365);
               super.context.setIcon(sideIcon, sideIcon, faceIcon, faceIcon, sideIcon, sideIcon);
               this.doubleBox(51, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F, 1.0F);
            }

            if ((cons & 16) > 0) {
               super.context.setTexFlags(187200);
               super.context.setIcon(sideIcon, sideIcon, sideIcon, sideIcon, faceIcon, faceIcon);
               this.doubleBox(15, 0.0F, 0.25F, 0.25F, 0.25F, 0.75F, 0.75F);
            }

            if ((cons & 32) > 0) {
               super.context.setTexFlags(187200);
               super.context.setIcon(sideIcon, sideIcon, sideIcon, sideIcon, faceIcon, faceIcon);
               this.doubleBox(15, 0.75F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            }
         }
      }

   }
}
