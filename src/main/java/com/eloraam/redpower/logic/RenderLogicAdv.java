package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.RenderModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderLogicAdv extends RenderLogic {
   private RenderModel modelXcvr = RenderModel.loadModel("rplogic:models/busxcvr.obj");
   private ResourceLocation modelRes = new ResourceLocation("rplogic", "models/arraytex.png");

   public RenderLogicAdv(Block block) {
      super(block);
   }

   @Override
   protected int getTorchState(TileLogic tileLogic) {
      return 0;
   }

   @Override
   protected int getInvTorchState(int metadata) {
      return 0;
   }

   @Override
   protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tileLogic) {
      return null;
   }

   @Override
   protected RenderLogic.TorchPos[] getInvTorchVectors(int metadata) {
      return null;
   }

   @Override
   protected void renderWorldPart(IBlockAccess iba, TileLogic tileLogic, double x, double y, double z, float partialTicks) {
      int md = tileLogic.getExtendedMetadata();
      TileLogicAdv tls = (TileLogicAdv)tileLogic;
      Tessellator tess = Tessellator.instance;
      tess.draw();
      switch(md) {
         case 0:
            TileLogicAdv.LogicAdvXcvr lsc = tls.getLogicStorage(TileLogicAdv.LogicAdvXcvr.class);
            tess.startDrawingQuads();
            super.context.bindTexture(this.modelRes);
            super.context.bindModelOffset(this.modelXcvr, 0.5, 0.5, 0.5);
            super.context.setTint(1.0F, 1.0F, 1.0F);
            boolean b = (3552867 >> tileLogic.Rotation & 1) == 0;
            super.context.renderModelGroup(1, 1 + (b ? 1 : 0) + (tileLogic.Deadmap == 0 ? 2 : 0));
            super.context.renderModelGroup(2, 1 + ((tileLogic.PowerState & 1) > 0 ? 1 : 0) + ((tileLogic.PowerState & 4) > 0 ? 2 : 0));

            for(int i = 0; i < 4; ++i) {
               if (tileLogic.Deadmap == 0) {
                  super.context.renderModelGroup(3 + i, 1 + (lsc.State2 >> 4 * i & 15));
                  super.context.renderModelGroup(7 + i, 1 + (lsc.State1 >> 4 * i & 15));
               } else {
                  super.context.renderModelGroup(3 + i, 1 + (lsc.State1 >> 4 * i & 15));
                  super.context.renderModelGroup(7 + i, 1 + (lsc.State2 >> 4 * i & 15));
               }
            }

            tess.draw();
         default:
            tess.startDrawingQuads();
      }
   }

   @Override
   protected void renderInvPart(int metadata) {
      switch(metadata) {
         case 1024:
            super.context.bindTexture(this.modelRes);
            Tessellator tess = Tessellator.instance;
            tess.startDrawingQuads();
            super.context.useNormal = true;
            super.context.bindModelOffset(this.modelXcvr, 0.5, 0.5, 0.5);
            super.context.setTint(1.0F, 1.0F, 1.0F);
            super.context.renderModelGroup(1, 1);
            super.context.renderModelGroup(2, 1);

            for(int i = 0; i < 8; ++i) {
               super.context.renderModelGroup(3 + i, 1);
            }

            super.context.useNormal = false;
            tess.draw();
      }
   }
}
