package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderLogicArray extends RenderLogic {
   private RenderModel model = RenderModel.loadModel("rplogic:models/arraycells.obj");
   private ResourceLocation modelRes = new ResourceLocation("rplogic", "models/arraytex.png");
   private static RenderLogic.TorchPos[] torchMapInvert = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.0, -0.25, 0.0, 0.7)};
   private static RenderLogic.TorchPos[] torchMapNonInv = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.0, -0.25, 0.0, 0.7), new RenderLogic.TorchPos(-0.188, -0.25, 0.219, 0.7)
   };

   public RenderLogicArray(Block block) {
      super(block);
   }

   @Override
   protected int getTorchState(TileLogic tileLogic) {
      int md = tileLogic.getExtendedMetadata();
      switch(md) {
         case 1:
            return tileLogic.Powered ? 1 : 0;
         case 2:
            return tileLogic.Powered ? 1 : 2;
         default:
            return 0;
      }
   }

   @Override
   protected int getInvTorchState(int metadata) {
      return metadata == 514 ? 2 : 0;
   }

   @Override
   protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tileLogic) {
      int md = tileLogic.getExtendedMetadata();
      switch(md) {
         case 1:
            return torchMapInvert;
         case 2:
            return torchMapNonInv;
         default:
            return null;
      }
   }

   @Override
   protected RenderLogic.TorchPos[] getInvTorchVectors(int metadata) {
      switch(metadata) {
         case 513:
            return torchMapInvert;
         case 514:
            return torchMapNonInv;
         default:
            return null;
      }
   }

   public static int getFacingDir(int rot, int rel) {
      short n;
      switch(rot >> 2) {
         case 0:
            n = 13604;
            break;
         case 1:
            n = 13349;
            break;
         case 2:
            n = 20800;
            break;
         case 3:
            n = 16720;
            break;
         case 4:
            n = 8496;
            break;
         default:
            n = 12576;
      }

      int n1 = n >> ((rot + rel & 3) << 2);
      return n1 & 7;
   }

   private boolean isArrayTopwire(IBlockAccess iba, WorldCoord wc, int mask, int dir) {
      wc = wc.coordStep(dir);
      TileLogicArray logicArray = CoreLib.getTileEntity(iba, wc, TileLogicArray.class);
      if (logicArray == null) {
         return false;
      } else {
         int m = logicArray.getTopwireMask();
         m &= RedPowerLib.getConDirMask(dir);
         m = (m & 1431655765) << 1 | (m & 715827882) >> 1;
         m &= mask;
         return m > 0;
      }
   }

   @Override
   protected void renderWorldPart(IBlockAccess iba, TileLogic tileLogic, double x, double y, double z, float partialTicks) {
      TileLogicArray logicArray = (TileLogicArray)tileLogic;
      Tessellator tess = Tessellator.instance;
      int md = tileLogic.getExtendedMetadata();
      super.context.bindTexture(this.modelRes);
      tess.draw();
      tess.startDrawingQuads();
      super.context.bindModelOffset(this.model, 0.5, 0.5, 0.5);
      super.context.setTint(1.0F, 1.0F, 1.0F);
      super.context.renderModelGroup(0, 0);
      switch(md) {
         case 0:
            super.context.renderModelGroup(1, 1);
            super.context.setTint(0.3F + 0.7F * ((float)logicArray.PowerVal1 / 255.0F), 0.0F, 0.0F);
            super.context.renderModelGroup(2, 1);
            super.context.setTint(0.3F + 0.7F * ((float)logicArray.PowerVal2 / 255.0F), 0.0F, 0.0F);
            super.context.renderModelGroup(3, 1);
            break;
         case 1:
            super.context.renderModelGroup(1, 2 + (logicArray.PowerVal1 > 0 ? 1 : 0));
            super.context.renderModelGroup(5, 0);
            super.context.setTint(0.3F + 0.7F * ((float)logicArray.PowerVal1 / 255.0F), 0.0F, 0.0F);
            super.context.renderModelGroup(2, 2);
            super.context.setTint(0.3F + 0.7F * ((float)logicArray.PowerVal2 / 255.0F), 0.0F, 0.0F);
            super.context.renderModelGroup(3, 2);
            break;
         case 2:
            super.context.renderModelGroup(1, 4 + (logicArray.PowerVal1 > 0 ? 1 : 0) + (logicArray.Powered ? 0 : 2));
            super.context.renderModelGroup(5, 0);
            super.context.setTint(0.3F + 0.7F * ((float)logicArray.PowerVal1 / 255.0F), 0.0F, 0.0F);
            super.context.renderModelGroup(2, 2);
            super.context.setTint(0.3F + 0.7F * ((float)logicArray.PowerVal2 / 255.0F), 0.0F, 0.0F);
            super.context.renderModelGroup(3, 2);
      }

      int fd = getFacingDir(logicArray.Rotation, 1);
      int fm = logicArray.getTopwireMask();
      WorldCoord wc = new WorldCoord(tileLogic);
      super.context.renderModelGroup(4, (this.isArrayTopwire(iba, wc, fm, fd) ? 0 : 1) + (this.isArrayTopwire(iba, wc, fm, fd ^ 1) ? 0 : 2));
      tess.draw();
      tess.startDrawingQuads();
   }

   @Override
   protected void renderInvPart(int metadata) {
      Tessellator tess = Tessellator.instance;
      super.context.bindTexture(this.modelRes);
      tess.startDrawingQuads();
      super.context.useNormal = true;
      super.context.bindModelOffset(this.model, 0.5, 0.5, 0.5);
      super.context.setTint(1.0F, 1.0F, 1.0F);
      super.context.renderModelGroup(0, 0);
      switch(metadata) {
         case 512:
            super.context.renderModelGroup(1, 1);
            super.context.setTint(0.3F, 0.0F, 0.0F);
            super.context.renderModelGroup(2, 1);
            super.context.renderModelGroup(3, 1);
            super.context.renderModelGroup(4, 3);
            break;
         case 513:
            super.context.renderModelGroup(1, 2);
            super.context.renderModelGroup(5, 0);
            super.context.setTint(0.3F, 0.0F, 0.0F);
            super.context.renderModelGroup(2, 2);
            super.context.renderModelGroup(3, 2);
            super.context.renderModelGroup(4, 3);
            break;
         case 514:
            super.context.renderModelGroup(1, 6);
            super.context.renderModelGroup(5, 0);
            super.context.setTint(0.3F, 0.0F, 0.0F);
            super.context.renderModelGroup(2, 2);
            super.context.renderModelGroup(3, 2);
            super.context.renderModelGroup(4, 3);
      }

      super.context.useNormal = false;
      tess.draw();
   }
}
