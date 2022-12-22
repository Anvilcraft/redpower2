package com.eloraam.redpower.compat;

import com.eloraam.redpower.core.Matrix3;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public class RenderBlueEngine extends RenderCustomBlock {
   private static ResourceLocation res = new ResourceLocation("rpcompat", "models/compat1.png");
   protected RenderModel modelBase = RenderModel.loadModel("rpcompat:models/btengine1.obj").scale(0.0625);
   protected RenderModel modelSlide = RenderModel.loadModel("rpcompat:models/btengine2.obj").scale(0.0625);
   protected RenderModel modelGear = RenderModel.loadModel("rpcompat:models/btengine3.obj").scale(0.0625);
   protected RenderContext context = new RenderContext();

   public RenderBlueEngine(Block bl) {
      super(bl);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileBlueEngine tb = (TileBlueEngine)tile;
      if (tb != null) {
         Tessellator tess = Tessellator.instance;
         this.context.setDefaults();
         this.context.setPos(x, y, z);
         this.context.setOrientation(tb.Rotation, 0);
         this.context.readGlobalLights(tb.getWorldObj(), tb.xCoord, tb.yCoord, tb.zCoord);
         this.context.setBrightness(super.getMixedBrightness(tb));
         this.context.bindTexture(res);
         this.context.bindModelOffset(this.modelBase, 0.5, 0.5, 0.5);
         tess.startDrawingQuads();
         this.context.renderModelGroup(0, 0);
         this.context.renderModelGroup(1, tb.Charged ? (tb.Active ? 3 : 2) : 1);
         tess.draw();
         int lv = tb.getWorldObj().getLightBrightnessForSkyBlocks(tb.xCoord, tb.yCoord, tb.zCoord, 0);
         tess.startDrawingQuads();
         tess.setBrightness(lv);
         if (tb.Active) {
            partialTicks += (float)tb.PumpTick;
            if (tb.PumpSpeed > 0) {
               partialTicks /= (float)tb.PumpSpeed;
            }
         } else {
            partialTicks = 0.0F;
         }

         this.context.useNormal = true;
         this.context.setPos(x, y, z);
         this.context.setOrientation(tb.Rotation, 0);
         this.context.setRelPos(0.0, 0.1875 * (0.5 - 0.5 * Math.cos(Math.PI * (double)partialTicks)), 0.0);
         this.context.bindModelOffset(this.modelSlide, 0.5, 0.5, 0.5);
         this.context.renderModelGroup(0, 0);
         this.context.basis = Matrix3.getRotY(Math.PI / 2 * (double)partialTicks).multiply(this.context.basis);
         this.context.setRelPos(0.5, 0.34375, 0.5);
         this.context.bindModelOffset(this.modelGear, 0.5, 0.5, 0.5);
         this.context.renderModelGroup(0, 0);
         tess.draw();
      }

   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      super.block.setBlockBoundsForItemRender();
      this.context.setDefaults();
      if (type == ItemRenderType.INVENTORY) {
         this.context.setPos(-0.5, -0.5, -0.5);
      } else {
         this.context.setPos(0.0, 0.0, 0.0);
      }

      this.context.bindTexture(res);
      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      this.context.useNormal = true;
      this.context.bindModelOffset(this.modelBase, 0.5, 0.5, 0.5);
      this.context.renderModelGroup(0, 0);
      this.context.renderModelGroup(1, 1);
      this.context.bindModelOffset(this.modelSlide, 0.5, 0.5, 0.5);
      this.context.renderModelGroup(0, 0);
      this.context.setPos(0.0, -0.15625, 0.0);
      this.context.bindModel(this.modelGear);
      this.context.renderModelGroup(0, 0);
      this.context.useNormal = false;
      tess.draw();
   }
}
