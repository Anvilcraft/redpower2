package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderLogicPointer extends RenderLogic {
   private static RenderLogic.TorchPos[] torchMapSequencer = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.0, 0.125, 0.0, 1.0),
      new RenderLogic.TorchPos(0.0, -0.3, 0.3, 0.6),
      new RenderLogic.TorchPos(-0.3, -0.3, 0.0, 0.6),
      new RenderLogic.TorchPos(0.0, -0.3, -0.3, 0.6),
      new RenderLogic.TorchPos(0.3, -0.3, 0.0, 0.6)
   };
   private static RenderLogic.TorchPos[] torchMapTimer = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.0, 0.125, 0.0, 1.0), new RenderLogic.TorchPos(0.3, -0.3, 0.0, 0.6)
   };
   private static RenderLogic.TorchPos[] torchMapStateCell = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.0, 0.125, 0.25, 1.0), new RenderLogic.TorchPos(0.281, -0.3, 0.156, 0.6)
   };
   private static RenderLogic.TorchPos[] torchMapStateCell2 = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.0, 0.125, -0.25, 1.0), new RenderLogic.TorchPos(0.281, -0.3, -0.156, 0.6)
   };

   public RenderLogicPointer(Block block) {
      super(block);
   }

   @Override
   protected int getTorchState(TileLogic tileLogic) {
      int md = tileLogic.getExtendedMetadata();
      switch(md) {
         case 0:
            return (tileLogic.Disabled ? 0 : 1) | (tileLogic.Powered && !tileLogic.Disabled ? 2 : 0);
         case 1:
            return 1 | 2 << tileLogic.PowerState & 31;
         case 2:
            return (tileLogic.Active && !tileLogic.Powered && !tileLogic.Disabled ? 1 : 0) | (tileLogic.Active && tileLogic.Powered ? 2 : 0);
         default:
            return 0;
      }
   }

   @Override
   protected int getInvTorchState(int metadata) {
      switch(metadata) {
         case 0:
            return 1;
         case 1:
            return 5;
         case 2:
            return 0;
         default:
            return 0;
      }
   }

   @Override
   protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tileLogic) {
      int md = tileLogic.getExtendedMetadata();
      switch(md) {
         case 0:
            return torchMapTimer;
         case 1:
            return torchMapSequencer;
         case 2:
            if (tileLogic.Deadmap > 0) {
               return torchMapStateCell2;
            }

            return torchMapStateCell;
         default:
            return null;
      }
   }

   @Override
   protected RenderLogic.TorchPos[] getInvTorchVectors(int metadata) {
      switch(metadata) {
         case 0:
            return torchMapTimer;
         case 1:
            return torchMapSequencer;
         case 2:
            return torchMapStateCell;
         default:
            return null;
      }
   }

   @Override
   protected void renderWorldPart(IBlockAccess iba, TileLogic tileLogic, double x, double y, double z, float partialTicks) {
      TileLogicPointer logicPointer = (TileLogicPointer)tileLogic;
      int md = tileLogic.getExtendedMetadata();
      int tx;
      switch(md) {
         case 0:
            tx = 16 + (tileLogic.PowerState | (tileLogic.Powered ? 5 : 0));
            break;
         case 1:
            if (tileLogic.Deadmap == 1) {
               tx = 4;
            } else {
               tx = 3;
            }
            break;
         case 2:
            tx = 32
               + (
                  (tileLogic.Deadmap > 0 ? 32 : 0)
                     | tileLogic.PowerState
                     | (tileLogic.Active && tileLogic.Powered ? 8 : 0)
                     | (tileLogic.Active && !tileLogic.Powered && !tileLogic.Disabled ? 0 : 16)
                     | (tileLogic.Active && !tileLogic.Powered ? (tileLogic.Deadmap > 0 ? 1 : 4) : 0)
               );
            break;
         default:
            return;
      }

      this.renderWafer(tx);
      if (md == 2) {
         if (tileLogic.Deadmap > 0) {
            this.renderChip(-0.125, 0.0, 0.125, tileLogic.Active ? 2 : 1);
         } else {
            this.renderChip(-0.125, 0.0, -0.125, tileLogic.Active ? 2 : 1);
         }
      }

      float ptrdir = logicPointer.getPointerDirection(partialTicks) + 0.25F;
      Quat q = MathLib.orientQuat(logicPointer.Rotation >> 2, logicPointer.Rotation & 3);
      Vector3 v = logicPointer.getPointerOrigin();
      q.rotate(v);
      v.add(x + 0.5, y + 0.5, z + 0.5);
      q.rightMultiply(Quat.aroundAxis(0.0, 1.0, 0.0, -Math.PI * 2 * (double)ptrdir));
      RenderLib.renderPointer(v, q);
   }

   @Override
   protected void renderInvPart(int metadata) {
      switch(metadata) {
         case 0:
            super.context.setOrientation(0, 1);
            this.renderInvWafer(16);
            break;
         case 1:
            this.renderInvWafer(3);
            break;
         case 2:
            super.context.setOrientation(0, 1);
            this.renderInvWafer(48);
      }

      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      tess.setNormal(0.0F, 0.0F, 1.0F);
      switch(metadata) {
         case 2:
            RenderLib.renderPointer(new Vector3(-0.25, -0.1, 0.0), Quat.aroundAxis(0.0, 1.0, 0.0, 0.0));
            super.context.useNormal = true;
            this.renderChip(-0.125, 0.0, -0.125, 1);
            super.context.useNormal = false;
            break;
         default:
            RenderLib.renderPointer(new Vector3(0.0, -0.1, 0.0), Quat.aroundAxis(0.0, 1.0, 0.0, -Math.PI / 2));
      }

      tess.draw();
   }
}
