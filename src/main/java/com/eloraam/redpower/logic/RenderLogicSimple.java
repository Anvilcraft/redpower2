package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.PowerLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderLogicSimple extends RenderLogic {
   private static RenderLogic.TorchPos[] torchMapLatch = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.3, -0.15, 0.0, 0.8), new RenderLogic.TorchPos(0.3, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapLatch2 = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.281, -0.15, -0.0938, 0.8), new RenderLogic.TorchPos(0.281, -0.15, 0.0938, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapLatch2b = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.281, -0.15, 0.0938, 0.8), new RenderLogic.TorchPos(0.281, -0.15, -0.0938, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapNor = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.094, -0.25, 0.031, 0.7)};
   private static RenderLogic.TorchPos[] torchMapOr = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.094, -0.25, 0.031, 0.7), new RenderLogic.TorchPos(0.28, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapNand = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.031, -0.25, 0.22, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, 0.0, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, -0.22, 0.7)
   };
   private static RenderLogic.TorchPos[] torchMapAnd = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.031, -0.25, 0.22, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, 0.0, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, -0.22, 0.7),
      new RenderLogic.TorchPos(0.28, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapXnor = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.031, -0.25, 0.22, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, -0.22, 0.7),
      new RenderLogic.TorchPos(-0.28, -0.25, 0.0, 0.7),
      new RenderLogic.TorchPos(0.28, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapXor = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.031, -0.25, 0.22, 0.7), new RenderLogic.TorchPos(-0.031, -0.25, -0.22, 0.7), new RenderLogic.TorchPos(-0.28, -0.25, 0.0, 0.7)
   };
   private static RenderLogic.TorchPos[] torchMapPulse = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.09, -0.25, -0.22, 0.7), new RenderLogic.TorchPos(-0.09, -0.25, 0.22, 0.7), new RenderLogic.TorchPos(0.28, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapToggle = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.28, -0.25, -0.22, 0.7), new RenderLogic.TorchPos(-0.28, -0.25, -0.22, 0.7)
   };
   private static RenderLogic.TorchPos[] torchMapNot = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(-0.031, -0.25, 0.031, 0.7)};
   private static RenderLogic.TorchPos[] torchMapBuffer = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.281, -0.15, 0.031, 0.8), new RenderLogic.TorchPos(-0.094, -0.25, 0.031, 0.7)
   };
   private static RenderLogic.TorchPos[] torchMapMux = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.031, -0.25, 0.22, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, -0.22, 0.7),
      new RenderLogic.TorchPos(-0.156, -0.25, 0.031, 0.7),
      new RenderLogic.TorchPos(0.28, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapMux2 = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.031, -0.25, 0.22, 0.7),
      new RenderLogic.TorchPos(-0.031, -0.25, -0.22, 0.7),
      new RenderLogic.TorchPos(-0.156, -0.25, -0.031, 0.7),
      new RenderLogic.TorchPos(0.28, -0.15, 0.0, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapRepS = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(0.313, -0.25, -0.125, 0.7), new RenderLogic.TorchPos(-0.25, -0.25, 0.25, 0.7)
   };
   private static RenderLogic.TorchPos[] torchMapSync = new RenderLogic.TorchPos[]{new RenderLogic.TorchPos(0.28, -0.25, 0.0, 0.7)};
   private static RenderLogic.TorchPos[] torchMapDLatch = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.28, -0.25, -0.219, 0.7),
      new RenderLogic.TorchPos(0.031, -0.25, -0.219, 0.7),
      new RenderLogic.TorchPos(0.031, -0.25, -0.031, 0.7),
      new RenderLogic.TorchPos(0.031, -0.15, 0.281, 0.8),
      new RenderLogic.TorchPos(0.281, -0.15, -0.094, 0.8)
   };
   private static RenderLogic.TorchPos[] torchMapDLatch2 = new RenderLogic.TorchPos[]{
      new RenderLogic.TorchPos(-0.28, -0.25, 0.219, 0.7),
      new RenderLogic.TorchPos(0.031, -0.25, 0.219, 0.7),
      new RenderLogic.TorchPos(0.031, -0.25, 0.031, 0.7),
      new RenderLogic.TorchPos(0.031, -0.15, -0.281, 0.8),
      new RenderLogic.TorchPos(0.281, -0.15, 0.094, 0.8)
   };
   private static final int[] texIdxNor = new int[]{272, 288, 296, 312, 304, 316, 320};
   private static final int[] texIdxOr = new int[]{376, 384, 388, 416, 392, 418, 420};
   private static final int[] texIdxNand = new int[]{336, 352, 360, 324, 368, 328, 332};
   private static final int[] texIdxAnd = new int[]{400, 408, 412, 422, 396, 424, 426};
   private static final int[] texIdxNot = new int[]{432, 448, 456, 472, 464, 476, 428};
   private static final int[] texIdxBuf = new int[]{496, 504, 508, 257};
   private static Quat[] leverPositions = new Quat[2];

   public RenderLogicSimple(Block block) {
      super(block);
   }

   @Override
   protected int getTorchState(TileLogic tileLogic) {
      int md = tileLogic.getExtendedMetadata();
      switch(md) {
         case 0:
            if (tileLogic.Deadmap > 1) {
               return ((tileLogic.PowerState & 2) > 0 ? 1 : 0) | ((tileLogic.PowerState & 8) > 0 ? 2 : 0);
            } else {
               if (!tileLogic.Disabled && !tileLogic.Active) {
                  if (tileLogic.Deadmap == 1) {
                     return tileLogic.Powered ? 1 : 2;
                  }

                  return tileLogic.Powered ? 2 : 1;
               }

               return 0;
            }
         case 1:
            return tileLogic.Powered ? 1 : 0;
         case 2: {
            int eps1 = tileLogic.PowerState & ~tileLogic.Deadmap;
            return (eps1 == 0 ? 1 : 0) | (tileLogic.Powered ? 2 : 0);
         }
         case 3: {
            int eps1 = tileLogic.PowerState | tileLogic.Deadmap;
            return eps1 & 7 ^ 7;
         }
         case 4: {
            int eps1 = tileLogic.PowerState | tileLogic.Deadmap;
            return eps1 & 7 ^ 7 | (tileLogic.Powered ? 8 : 0);
         }
         case 5:
         case 6:
            byte eps;
            switch(tileLogic.PowerState & 5) {
               case 0:
                  eps = 4;
                  break;
               case 1:
                  eps = 2;
                  break;
               case 2:
               case 3:
               default:
                  eps = 0;
                  break;
               case 4:
                  eps = 1;
            }

            if (md == 6) {
               return eps;
            }

            return eps | (tileLogic.Powered ? 8 : 0);
         case 7:
            return (!tileLogic.Powered && !tileLogic.Active ? 1 : 0)
               | (!tileLogic.Powered && !tileLogic.Active ? 0 : 2)
               | (tileLogic.Powered && !tileLogic.Active ? 4 : 0);
         case 8:
            return !tileLogic.Powered ? 1 : 2;
         case 9:
            return tileLogic.Powered ? 1 : 0;
         case 10:
            return (tileLogic.Powered ? 1 : 0) | tileLogic.PowerState & 2;
         case 11:
            if (tileLogic.Deadmap == 0) {
               return (tileLogic.Powered ? 8 : 0)
                  | ((tileLogic.PowerState & 3) == 0 ? 1 : 0)
                  | ((tileLogic.PowerState & 6) == 2 ? 2 : 0)
                  | ((tileLogic.PowerState & 2) == 0 ? 4 : 0);
            }

            return (tileLogic.Powered ? 8 : 0)
               | ((tileLogic.PowerState & 3) == 2 ? 1 : 0)
               | ((tileLogic.PowerState & 6) == 0 ? 2 : 0)
               | ((tileLogic.PowerState & 2) == 0 ? 4 : 0);
         case 12:
            return (tileLogic.Powered ? 1 : 0) | (tileLogic.PowerState == 0 ? 2 : 0);
         case 13:
            return tileLogic.Powered ? 1 : 0;
         case 14:
            return 0;
         case 15:
            if (tileLogic.Deadmap == 0) {
               switch(tileLogic.PowerState & 6) {
                  case 0:
                     return tileLogic.Powered ? 25 : 5;
                  case 1:
                  case 3:
                  default:
                     return tileLogic.Powered ? 24 : 0;
                  case 2:
                     return tileLogic.Powered ? 26 : 2;
                  case 4:
                     return tileLogic.Powered ? 25 : 5;
               }
            } else {
               switch(tileLogic.PowerState & 3) {
                  case 0:
                     return tileLogic.Powered ? 25 : 5;
                  case 1:
                     return tileLogic.Powered ? 25 : 5;
                  case 2:
                     return tileLogic.Powered ? 26 : 2;
                  default:
                     return tileLogic.Powered ? 24 : 0;
               }
            }
         default:
            return 0;
      }
   }

   @Override
   protected int getInvTorchState(int metadata) {
      switch(metadata) {
         case 256:
         case 257:
         case 258:
            return 1;
         case 259:
         case 260:
            return 7;
         case 261:
            return 12;
         case 262:
            return 4;
         case 263:
         case 264:
         case 265:
            return 1;
         case 266:
            return 2;
         case 267:
            return 12;
         case 268:
            return 1;
         case 269:
            return 0;
         case 270:
            return 0;
         case 271:
            return 5;
         default:
            return 0;
      }
   }

   @Override
   protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tileLogic) {
      int md = tileLogic.getExtendedMetadata();
      switch(md) {
         case 0:
            if (tileLogic.Deadmap == 2) {
               return torchMapLatch2;
            } else {
               if (tileLogic.Deadmap == 3) {
                  return torchMapLatch2b;
               }

               return torchMapLatch;
            }
         case 1:
            return torchMapNor;
         case 2:
            return torchMapOr;
         case 3:
            return torchMapNand;
         case 4:
            return torchMapAnd;
         case 5:
            return torchMapXnor;
         case 6:
            return torchMapXor;
         case 7:
            return torchMapPulse;
         case 8:
            return torchMapToggle;
         case 9:
            return torchMapNot;
         case 10:
            return torchMapBuffer;
         case 11:
            if (tileLogic.Deadmap == 0) {
               return torchMapMux;
            }

            return torchMapMux2;
         case 12:
            return new RenderLogic.TorchPos[]{
               new RenderLogic.TorchPos(0.313, -0.25, -0.125, 0.7), new RenderLogic.TorchPos(-0.25 + (double)tileLogic.Deadmap * 0.063, -0.25, 0.25, 0.7)
            };
         case 13:
            return torchMapSync;
         case 14:
            return null;
         case 15:
            if (tileLogic.Deadmap == 0) {
               return torchMapDLatch;
            }

            return torchMapDLatch2;
         default:
            return null;
      }
   }

   @Override
   protected RenderLogic.TorchPos[] getInvTorchVectors(int metadata) {
      switch(metadata) {
         case 256:
            return torchMapLatch;
         case 257:
            return torchMapNor;
         case 258:
            return torchMapOr;
         case 259:
            return torchMapNand;
         case 260:
            return torchMapAnd;
         case 261:
            return torchMapXnor;
         case 262:
            return torchMapXor;
         case 263:
            return torchMapPulse;
         case 264:
            return torchMapToggle;
         case 265:
            return torchMapNot;
         case 266:
            return torchMapBuffer;
         case 267:
            return torchMapMux;
         case 268:
            return torchMapRepS;
         case 269:
            return torchMapSync;
         case 270:
            return null;
         case 271:
            return torchMapDLatch;
         default:
            return null;
      }
   }

   @Override
   protected void renderWorldPart(IBlockAccess iba, TileLogic tileLogic, double x, double y, double z, float partialTicks) {
      int md = tileLogic.getExtendedMetadata();
      int tx;
      switch(md) {
         case 0:
            if (tileLogic.Deadmap < 2) {
               tx = ((tileLogic.PowerState & 1) > 0 ? 1 : 0) | ((tileLogic.PowerState & 4) > 0 ? 2 : 0);
               if (!tileLogic.Disabled || tileLogic.Active) {
                  tx |= tileLogic.Powered ? 2 : 1;
               }

               tx += 24 + (tileLogic.Deadmap == 1 ? 4 : 0);
            } else {
               tx = 96 + (tileLogic.Deadmap == 3 ? 16 : 0) + tileLogic.PowerState;
            }
            break;
         case 1:
            tx = texIdxNor[tileLogic.Deadmap] + PowerLib.cutBits(tileLogic.PowerState | (tileLogic.Powered ? 8 : 0), tileLogic.Deadmap);
            break;
         case 2:
            tx = texIdxOr[tileLogic.Deadmap] + PowerLib.cutBits(tileLogic.PowerState, tileLogic.Deadmap);
            break;
         case 3:
            tx = texIdxNand[tileLogic.Deadmap] + PowerLib.cutBits(tileLogic.PowerState | (tileLogic.Powered ? 8 : 0), tileLogic.Deadmap);
            break;
         case 4:
            tx = texIdxAnd[tileLogic.Deadmap] + PowerLib.cutBits(tileLogic.PowerState, tileLogic.Deadmap);
            break;
         case 5:
            tx = 128 + (tileLogic.PowerState & 1) + ((tileLogic.PowerState & 4) >> 1);
            break;
         case 6:
            tx = 132 + ((tileLogic.Powered ? 4 : 0) | (tileLogic.PowerState & 12) >> 1 | tileLogic.PowerState & 1);
            break;
         case 7:
            tx = 5;
            if (tileLogic.Powered && !tileLogic.Active) {
               tx = 6;
            } else if (!tileLogic.Powered && tileLogic.Active) {
               tx = 7;
            }
            break;
         case 8:
            tx = 140 + (tileLogic.PowerState & 1) + (tileLogic.PowerState >> 1 & 2);
            break;
         case 9:
            if (tileLogic.Deadmap == 0) {
               tx = 432 + (tileLogic.PowerState | (tileLogic.Powered ? 13 : 0));
            } else {
               int tmp = PowerLib.cutBits(tileLogic.Deadmap, 2);
               if (tileLogic.Powered) {
                  tx = 480 + (tmp - 1 << 1) + ((tileLogic.PowerState & 2) >> 1);
               } else {
                  tx = texIdxNot[tmp] + PowerLib.cutBits(tileLogic.PowerState, tileLogic.Deadmap);
               }
            }
            break;
         case 10:
            if (tileLogic.Deadmap == 0) {
               tx = 496 + (tileLogic.PowerState | (tileLogic.Powered ? 5 : 0));
            } else {
               int tmp = PowerLib.cutBits(tileLogic.Deadmap, 2);
               if (tileLogic.Powered) {
                  tx = 256 + (tmp << 1) + ((tileLogic.PowerState & 2) >> 1);
               } else {
                  tx = texIdxBuf[tmp] + PowerLib.cutBits(tileLogic.PowerState, tileLogic.Deadmap);
               }
            }
            break;
         case 11:
            tx = 144 + (tileLogic.Deadmap > 0 ? 8 : 0) + tileLogic.PowerState;
            break;
         case 12:
            tx = 492 + (tileLogic.PowerState >> 1) + (tileLogic.Powered ? 0 : 2);
            break;
         case 13:
            tx = 160 + tileLogic.PowerState + (tileLogic.Active ? 8 : 0) + (tileLogic.Disabled ? 16 : 0);
            break;
         case 14:
            tx = 192 + (tileLogic.PowerState | (tileLogic.Active ? 1 : 0) | (tileLogic.Powered ? 4 : 0) | (tileLogic.Disabled ? 8 : 0));
            break;
         case 15:
            if (tileLogic.Deadmap > 0) {
               tx = 216 + tileLogic.PowerState + (tileLogic.Powered ? 4 : 0);
            } else {
               tx = 208 + (tileLogic.PowerState >> 1) + (tileLogic.Powered ? 4 : 0);
            }
            break;
         case 16:
            tx = 513 + (!tileLogic.Powered && tileLogic.PowerState <= 0 ? 0 : 1);
            break;
         default:
            return;
      }

      this.renderWafer(tx);
      switch(md) {
         case 8:
            super.context.setTexFlags(44);
            super.context.setSize(0.25, 0.0, 0.555F, 0.75, 0.3F, 0.805F);
            super.context.setIcon(RedPowerLogic.cobblestone);
            super.context.calcBounds();
            super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
            super.context.renderFaces(62);
            Vector3 pos = new Vector3(0.0, -0.3, 0.18);
            Quat q = MathLib.orientQuat(tileLogic.Rotation >> 2, tileLogic.Rotation & 3);
            q.rotate(pos);
            pos.add(super.context.globalOrigin);
            q.rightMultiply(leverPositions[tileLogic.Powered ? 1 : 0]);
            RenderLib.renderSpecialLever(pos, q, RedPowerLogic.cobblestone, RedPowerLogic.lever);
         case 9:
         case 10:
         case 11:
         case 12:
         case 15:
         default:
            break;
         case 13:
            this.renderChip(-0.125, 0.0, -0.1875, tileLogic.Disabled ? 2 : 1);
            this.renderChip(-0.125, 0.0, 0.1875, tileLogic.Active ? 2 : 1);
            break;
         case 14:
            this.renderChip(-0.25, 0.0, -0.25, tileLogic.Disabled ? 9 : 8);
            this.renderChip(-0.25, 0.0, 0.25, tileLogic.Active ? 9 : 8);
            this.renderChip(0.125, 0.0, 0.0, tileLogic.Powered ? 9 : 8);
            break;
         case 16:
            super.context.setTexFlags(64);
            IIcon icon = RedPowerLogic.logicSensor[16 + tileLogic.Deadmap];
            super.context.setIcon(icon, icon, icon, icon, icon, icon);
            super.context.renderBox(62, 0.125, 0.0, 0.188F, 0.625, 0.188F, 0.813F);
      }

   }

   @Override
   protected void renderInvPart(int metadata) {
      switch(metadata) {
         case 256:
            this.renderInvWafer(25);
            break;
         case 257:
            this.renderInvWafer(280);
            break;
         case 258:
            this.renderInvWafer(384);
            break;
         case 259:
            this.renderInvWafer(344);
            break;
         case 260:
            this.renderInvWafer(400);
            break;
         case 261:
            this.renderInvWafer(128);
            break;
         case 262:
            this.renderInvWafer(132);
            break;
         case 263:
            this.renderInvWafer(5);
            break;
         case 264:
            this.renderInvWafer(140);
            break;
         case 265:
            this.renderInvWafer(440);
            break;
         case 266:
            this.renderInvWafer(496);
            break;
         case 267:
            this.renderInvWafer(144);
            break;
         case 268:
            this.renderInvWafer(493);
            break;
         case 269:
            this.renderInvWafer(160);
            break;
         case 270:
            this.renderInvWafer(192);
            break;
         case 271:
            this.renderInvWafer(208);
            break;
         case 272:
            this.renderInvWafer(51);
      }

      if (metadata == 264) {
         Tessellator tess = Tessellator.instance;
         tess.startDrawingQuads();
         super.context.useNormal = true;
         super.context.setTexFlags(44);
         super.context.setSize(0.25, 0.0, 0.555F, 0.75, 0.3F, 0.805F);
         super.context.setIcon(RedPowerLogic.cobblestone);
         super.context.calcBounds();
         super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
         super.context.renderFaces(62);
         super.context.useNormal = false;
         tess.draw();
         tess.startDrawingQuads();
         tess.setNormal(0.0F, 0.0F, 1.0F);
         Vector3 pos = new Vector3(0.0, -0.3, 0.18);
         Quat q = MathLib.orientQuat(0, 3);
         q.rotate(pos);
         pos.add(super.context.globalOrigin);
         q.rightMultiply(leverPositions[0]);
         RenderLib.renderSpecialLever(pos, q, RedPowerLogic.cobblestone, RedPowerLogic.lever);
         tess.draw();
      } else if (metadata == 269) {
         Tessellator tess = Tessellator.instance;
         tess.startDrawingQuads();
         super.context.useNormal = true;
         this.renderChip(-0.125, 0.0, -0.1875, 2);
         this.renderChip(-0.125, 0.0, 0.1875, 2);
         super.context.useNormal = false;
         tess.draw();
      } else if (metadata == 270) {
         Tessellator tess = Tessellator.instance;
         tess.startDrawingQuads();
         super.context.useNormal = true;
         this.renderChip(-0.25, 0.0, -0.25, 8);
         this.renderChip(-0.25, 0.0, 0.25, 8);
         this.renderChip(0.125, 0.0, 0.0, 8);
         super.context.useNormal = false;
         tess.draw();
      } else if (metadata == 272) {
         Tessellator tess = Tessellator.instance;
         tess.startDrawingQuads();
         super.context.useNormal = true;
         IIcon icon = RedPowerLogic.logicSensor[16];
         super.context.setIcon(icon, icon, icon, icon, icon, icon);
         super.context.setTexFlags(64);
         super.context.renderBox(62, 0.125, 0.0, 0.188F, 0.625, 0.188F, 0.813F);
         super.context.useNormal = false;
         tess.draw();
      }

   }

   static {
      leverPositions[0] = Quat.aroundAxis(1.0, 0.0, 0.0, 0.8639379797371932);
      leverPositions[1] = Quat.aroundAxis(1.0, 0.0, 0.0, -0.8639379797371932);
      leverPositions[0].multiply(MathLib.orientQuat(0, 3));
      leverPositions[1].multiply(MathLib.orientQuat(0, 3));
   }
}
