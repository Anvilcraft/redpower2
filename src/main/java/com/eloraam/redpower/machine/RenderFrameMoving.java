package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFrameMoving extends RenderCustomBlock {
   private RenderBlocks rblocks;
   private RenderContext context = new RenderContext();

   public RenderFrameMoving(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileFrameMoving frame = (TileFrameMoving)tile;
      World world = frame.getWorldObj();
      Tessellator tess = Tessellator.instance;
      if (!tile.isInvalid()) {
         Block block = frame.movingBlock;
         if (block != null) {
            this.context.bindBlockTexture();
            int lv = world.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
            tess.setBrightness(lv);
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glEnable(2884);
            if (Minecraft.isAmbientOcclusionEnabled()) {
               GL11.glShadeModel(7425);
            } else {
               GL11.glShadeModel(7424);
            }

            IBlockAccess wba = this.rblocks.blockAccess;
            this.rblocks.blockAccess = frame.getFrameBlockAccess();
            TileMotor tm = CoreLib.getTileEntity(frame.getWorldObj(), frame.motorX, frame.motorY, frame.motorZ, TileMotor.class);
            GL11.glPushMatrix();
            if (tm != null) {
               WorldCoord wc = new WorldCoord(0, 0, 0);
               wc.step(tm.MoveDir);
               float ms = tm.getMoveScaled();
               GL11.glTranslatef((float)wc.x * ms, (float)wc.y * ms, (float)wc.z * ms);
            }

            tess.setTranslation(x - (double)frame.xCoord, y - (double)frame.yCoord, z - (double)frame.zCoord);
            tess.setColorOpaque(1, 1, 1);
            if (frame.movingCrate) {
               this.context.setDefaults();
               this.context.setBrightness(lv);
               this.context.setPos((double)frame.xCoord, (double)frame.yCoord, (double)frame.zCoord);
               this.context.setIcon(RedPowerMachine.crate);
               tess.startDrawingQuads();
               this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
               tess.draw();
            } else {
               label47: {
                  frame.doRefresh(frame.getFrameBlockAccess());
                  if (frame.movingTileEntity != null) {
                     TileEntitySpecialRenderer tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(frame.movingTileEntity);
                     if (tesr != null) {
                        try {
                           double tileX = (double)frame.xCoord;
                           double tileY = (double)frame.yCoord;
                           double tileZ = (double)frame.zCoord;
                           tesr.renderTileEntityAt(frame.movingTileEntity, tileX, tileY, tileZ, partialTicks);
                        } catch (Exception var24) {
                           try {
                              tess.draw();
                           } catch (Exception var23) {
                           }
                        }
                        break label47;
                     }
                  }

                  tess.startDrawingQuads();
                  this.rblocks.renderAllFaces = true;
                  this.rblocks.renderBlockByRenderType(block, frame.xCoord, frame.yCoord, frame.zCoord);
                  this.rblocks.renderAllFaces = false;
                  tess.draw();
               }
            }

            tess.setTranslation(0.0, 0.0, 0.0);
            GL11.glPopMatrix();
            this.rblocks.blockAccess = wba;
            RenderHelper.enableStandardItemLighting();
         }
      }

   }

   public void func_147496_a(World world) {
      this.rblocks = new RenderBlocks(world);
   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
   }
}
