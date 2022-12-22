package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class CoverRenderer {
   private float cx1;
   private float cx2;
   private float cy1;
   private float cy2;
   private float cz1;
   private float cz2;
   private float[] x1 = new float[4];
   private float[] x2 = new float[4];
   private float[] y1 = new float[4];
   private float[] y2 = new float[4];
   private float[] z1 = new float[4];
   private float[] z2 = new float[4];
   private short[] covs;
   private int covmask;
   private int covmaskt;
   private int covmaskh;
   private int covmasko;
   public static IIcon[][] coverIcons = new IIcon[256][];
   protected RenderContext context;

   public CoverRenderer(RenderContext ctx) {
      this.context = ctx;
   }

   public void start() {
      this.cx1 = 0.0F;
      this.cx2 = 1.0F;
      this.cy1 = 0.0F;
      this.cy2 = 1.0F;
      this.cz1 = 0.0F;
      this.cz2 = 1.0F;
   }

   public void startShrink(float sh) {
      this.cx1 = sh;
      this.cx2 = 1.0F - sh;
      this.cy1 = sh;
      this.cy2 = 1.0F - sh;
      this.cz1 = sh;
      this.cz2 = 1.0F - sh;
   }

   public void sizeHollow(int part, int s) {
      switch(part) {
         case 0:
         case 1:
            if (s == 0) {
               this.context.boxSize2.x = 0.25;
            }

            if (s == 1) {
               this.context.boxSize1.x = 0.75;
            }

            if (s > 1) {
               this.context.boxSize1.x = 0.25;
               this.context.boxSize2.x = 0.75;
            }

            if (s == 2) {
               this.context.boxSize2.z = 0.25;
            }

            if (s == 3) {
               this.context.boxSize1.z = 0.75;
            }
            break;
         case 2:
         case 3:
            if (s == 0) {
               this.context.boxSize2.x = 0.25;
            }

            if (s == 1) {
               this.context.boxSize1.x = 0.75;
            }

            if (s > 1) {
               this.context.boxSize1.x = 0.25;
               this.context.boxSize2.x = 0.75;
            }

            if (s == 2) {
               this.context.boxSize2.y = 0.25;
            }

            if (s == 3) {
               this.context.boxSize1.y = 0.75;
            }
            break;
         default:
            if (s == 0) {
               this.context.boxSize2.z = 0.25;
            }

            if (s == 1) {
               this.context.boxSize1.z = 0.75;
            }

            if (s > 1) {
               this.context.boxSize1.z = 0.25;
               this.context.boxSize2.z = 0.75;
            }

            if (s == 2) {
               this.context.boxSize2.y = 0.25;
            }

            if (s == 3) {
               this.context.boxSize1.y = 0.75;
            }
      }

   }

   public int innerFace(int part, int s) {
      int m;
      switch(part) {
         case 0:
         case 1:
            m = 67637280;
            break;
         case 2:
         case 3:
            m = 16912416;
            break;
         default:
            m = 16909320;
      }

      return m >> s * 8;
   }

   public boolean sizeColumnSpoke(int part, boolean n1, float f) {
      part = part - 26 + (n1 ? 3 : 0);
      switch(part) {
         case 0:
            this.context.boxSize2.y = 0.5 - (double)f;
            return 0.5 - (double)f > (double)this.cy1;
         case 1:
            this.context.boxSize2.z = 0.5 - (double)f;
            return 0.5 - (double)f > (double)this.cz1;
         case 2:
            this.context.boxSize2.x = 0.5 - (double)f;
            return 0.5 - (double)f > (double)this.cx1;
         case 3:
            this.context.boxSize2.y = (double)this.cy2;
            this.context.boxSize1.y = 0.5 + (double)f;
            return 0.5 + (double)f < (double)this.cy2;
         case 4:
            this.context.boxSize2.z = (double)this.cz2;
            this.context.boxSize1.z = 0.5 + (double)f;
            return 0.5 + (double)f < (double)this.cz2;
         case 5:
            this.context.boxSize2.x = (double)this.cx2;
            this.context.boxSize1.x = 0.5 + (double)f;
            return 0.5 + (double)f < (double)this.cx2;
         default:
            return false;
      }
   }

   public void setSize(int part, float th) {
      switch(part) {
         case 0:
            this.context.setSize((double)this.cx1, 0.0, (double)this.cz1, (double)this.cx2, (double)th, (double)this.cz2);
            this.cy1 = th;
            break;
         case 1:
            this.context.setSize((double)this.cx1, (double)(1.0F - th), (double)this.cz1, (double)this.cx2, 1.0, (double)this.cz2);
            this.cy2 = 1.0F - th;
            break;
         case 2:
            this.context.setSize((double)this.cx1, (double)this.cy1, 0.0, (double)this.cx2, (double)this.cy2, (double)th);
            this.cz1 = th;
            break;
         case 3:
            this.context.setSize((double)this.cx1, (double)this.cy1, (double)(1.0F - th), (double)this.cx2, (double)this.cy2, 1.0);
            this.cz2 = 1.0F - th;
            break;
         case 4:
            this.context.setSize(0.0, (double)this.cy1, (double)this.cz1, (double)th, (double)this.cy2, (double)this.cz2);
            this.cx1 = th;
            break;
         case 5:
            this.context.setSize((double)(1.0F - th), (double)this.cy1, (double)this.cz1, 1.0, (double)this.cy2, (double)this.cz2);
            this.cx2 = 1.0F - th;
            break;
         case 6:
            this.context.setSize((double)this.cx1, (double)this.cy1, (double)this.cz1, (double)th, (double)th, (double)th);
            this.x1[0] = th;
            this.y1[0] = th;
            this.z1[0] = th;
            break;
         case 7:
            this.context.setSize((double)this.cx1, (double)this.cy1, (double)(1.0F - th), (double)th, (double)th, (double)this.cz2);
            this.x1[1] = th;
            this.y1[1] = th;
            this.z2[0] = 1.0F - th;
            break;
         case 8:
            this.context.setSize((double)(1.0F - th), (double)this.cy1, (double)this.cz1, (double)this.cx2, (double)th, (double)th);
            this.x2[0] = 1.0F - th;
            this.y1[2] = th;
            this.z1[1] = th;
            break;
         case 9:
            this.context.setSize((double)(1.0F - th), (double)this.cy1, (double)(1.0F - th), (double)this.cx2, (double)th, (double)this.cz2);
            this.x2[1] = 1.0F - th;
            this.y1[3] = th;
            this.z2[1] = 1.0F - th;
            break;
         case 10:
            this.context.setSize((double)this.cx1, (double)(1.0F - th), (double)this.cz1, (double)th, (double)this.cy2, (double)th);
            this.x1[2] = th;
            this.y2[0] = 1.0F - th;
            this.z1[2] = th;
            break;
         case 11:
            this.context.setSize((double)this.cx1, (double)(1.0F - th), (double)(1.0F - th), (double)th, (double)this.cy2, (double)this.cz2);
            this.x1[3] = th;
            this.y2[1] = 1.0F - th;
            this.z2[2] = 1.0F - th;
            break;
         case 12:
            this.context.setSize((double)(1.0F - th), (double)(1.0F - th), (double)this.cz1, (double)this.cx2, (double)this.cy2, (double)th);
            this.x2[2] = 1.0F - th;
            this.y2[2] = 1.0F - th;
            this.z1[3] = th;
            break;
         case 13:
            this.context.setSize((double)(1.0F - th), (double)(1.0F - th), (double)(1.0F - th), (double)this.cx2, (double)this.cy2, (double)this.cz2);
            this.x2[3] = 1.0F - th;
            this.y2[3] = 1.0F - th;
            this.z2[3] = 1.0F - th;
            break;
         case 14:
            this.context.setSize((double)this.x1[0], (double)this.cy1, (double)this.cz1, (double)this.x2[0], (double)th, (double)th);
            this.z1[0] = Math.max(this.z1[0], th);
            this.z1[1] = Math.max(this.z1[1], th);
            this.y1[0] = Math.max(this.y1[0], th);
            this.y1[2] = Math.max(this.y1[2], th);
            break;
         case 15:
            this.context.setSize((double)this.x1[1], (double)this.cy1, (double)(1.0F - th), (double)this.x2[1], (double)th, (double)this.cz2);
            this.z2[0] = Math.min(this.z2[0], 1.0F - th);
            this.z2[1] = Math.min(this.z2[1], 1.0F - th);
            this.y1[1] = Math.max(this.y1[1], th);
            this.y1[3] = Math.max(this.y1[3], th);
            break;
         case 16:
            this.context.setSize((double)this.cx1, (double)this.cy1, (double)this.z1[0], (double)th, (double)th, (double)this.z2[0]);
            this.x1[0] = Math.max(this.x1[0], th);
            this.x1[1] = Math.max(this.x1[1], th);
            this.y1[0] = Math.max(this.y1[0], th);
            this.y1[1] = Math.max(this.y1[1], th);
            break;
         case 17:
            this.context.setSize((double)(1.0F - th), (double)this.cy1, (double)this.z1[1], (double)this.cx2, (double)th, (double)this.z2[1]);
            this.x2[0] = Math.min(this.x2[0], 1.0F - th);
            this.x2[1] = Math.min(this.x2[1], 1.0F - th);
            this.y1[2] = Math.max(this.y1[2], th);
            this.y1[3] = Math.max(this.y1[3], th);
            break;
         case 18:
            this.context.setSize((double)this.cx1, (double)this.y1[0], (double)this.cz1, (double)th, (double)this.y2[0], (double)th);
            this.x1[0] = Math.max(this.x1[0], th);
            this.x1[2] = Math.max(this.x1[2], th);
            this.z1[0] = Math.max(this.z1[0], th);
            this.z1[2] = Math.max(this.z1[2], th);
            break;
         case 19:
            this.context.setSize((double)this.cx1, (double)this.y1[1], (double)(1.0F - th), (double)th, (double)this.y2[1], (double)this.cz2);
            this.x1[1] = Math.max(this.x1[1], th);
            this.x1[3] = Math.max(this.x1[3], th);
            this.z2[0] = Math.min(this.z2[0], 1.0F - th);
            this.z2[2] = Math.min(this.z2[2], 1.0F - th);
            break;
         case 20:
            this.context.setSize((double)(1.0F - th), (double)this.y1[2], (double)this.cz1, (double)this.cx2, (double)this.y2[2], (double)th);
            this.x2[0] = Math.min(this.x2[0], 1.0F - th);
            this.x2[2] = Math.min(this.x2[2], 1.0F - th);
            this.z1[1] = Math.max(this.z1[1], th);
            this.z1[3] = Math.max(this.z1[3], th);
            break;
         case 21:
            this.context.setSize((double)(1.0F - th), (double)this.y1[3], (double)(1.0F - th), (double)this.cx2, (double)this.y2[3], (double)this.cz2);
            this.x2[1] = Math.min(this.x2[1], 1.0F - th);
            this.x2[3] = Math.min(this.x2[3], 1.0F - th);
            this.z2[1] = Math.min(this.z2[1], 1.0F - th);
            this.z2[3] = Math.min(this.z2[3], 1.0F - th);
            break;
         case 22:
            this.context.setSize((double)this.x1[2], (double)(1.0F - th), (double)this.cz1, (double)this.x2[2], (double)this.cy2, (double)th);
            this.z1[2] = Math.max(this.z1[2], th);
            this.z1[3] = Math.max(this.z1[3], th);
            this.y2[0] = Math.min(this.y2[0], 1.0F - th);
            this.y2[2] = Math.min(this.y2[2], 1.0F - th);
            break;
         case 23:
            this.context.setSize((double)this.x1[3], (double)(1.0F - th), (double)(1.0F - th), (double)this.x2[3], (double)this.cy2, (double)this.cz2);
            this.z2[2] = Math.max(this.z2[2], 1.0F - th);
            this.z2[3] = Math.max(this.z2[3], 1.0F - th);
            this.y2[1] = Math.min(this.y2[1], 1.0F - th);
            this.y2[3] = Math.min(this.y2[3], 1.0F - th);
            break;
         case 24:
            this.context.setSize((double)this.cx1, (double)(1.0F - th), (double)this.z1[2], (double)th, (double)this.cy2, (double)this.z2[2]);
            this.x1[2] = Math.max(this.x1[2], th);
            this.x1[3] = Math.max(this.x1[3], th);
            this.y2[0] = Math.min(this.y2[0], 1.0F - th);
            this.y2[1] = Math.min(this.y2[1], 1.0F - th);
            break;
         case 25:
            this.context.setSize((double)(1.0F - th), (double)(1.0F - th), (double)this.z1[3], (double)this.cx2, (double)this.cy2, (double)this.z2[3]);
            this.x2[2] = Math.min(this.x2[2], 1.0F - th);
            this.x2[3] = Math.min(this.x2[3], 1.0F - th);
            this.y2[2] = Math.min(this.y2[2], 1.0F - th);
            this.y2[3] = Math.min(this.y2[3], 1.0F - th);
            break;
         case 26:
            this.context.setSize(0.5 - (double)th, (double)this.cy1, 0.5 - (double)th, 0.5 + (double)th, (double)this.cy2, 0.5 + (double)th);
            break;
         case 27:
            this.context.setSize(0.5 - (double)th, 0.5 - (double)th, (double)this.cz1, 0.5 + (double)th, 0.5 + (double)th, (double)this.cz2);
            break;
         case 28:
            this.context.setSize((double)this.cx1, 0.5 - (double)th, 0.5 - (double)th, (double)this.cx2, 0.5 + (double)th, 0.5 + (double)th);
      }

   }

   void setupCorners() {
      for(int i = 0; i < 4; ++i) {
         this.x1[i] = this.cx1;
         this.y1[i] = this.cy1;
         this.z1[i] = this.cz1;
         this.x2[i] = this.cx2;
         this.y2[i] = this.cy2;
         this.z2[i] = this.cz2;
      }

   }

   public void initMasks(int uc, short[] cv) {
      this.covmask = uc;
      this.covs = cv;
      this.covmaskt = 0;
      this.covmaskh = 0;
      this.covmasko = 0;

      for(int i = 0; i < 6; ++i) {
         if ((uc & 1 << i) != 0) {
            if (CoverLib.isTransparent(this.covs[i] & 255)) {
               this.covmaskt |= 1 << i;
            }

            if (this.covs[i] >> 8 > 2) {
               this.covmaskh |= 1 << i;
            }
         }
      }

      this.covmasko = this.covmask & ~this.covmaskt & ~this.covmaskh;
   }

   public void render(int uc, short[] cv) {
      this.initMasks(uc, cv);
      this.start();
      this.renderShell();
      if ((uc & -64) != 0) {
         this.renderOthers();
      }

   }

   public void renderShrink(int uc, short[] cv, float sh) {
      this.initMasks(uc, cv);
      this.startShrink(sh);
      this.renderShell();
      if ((uc & -64) != 0) {
         this.renderOthers();
      }

   }

   public void setIcon(int cn) {
      this.context.setIcon(coverIcons[cn]);
   }

   public void setIcon(int c1, int c2, int c3, int c4, int c5, int c6) {
      this.context.setIcon(coverIcons[c1][0], coverIcons[c2][1], coverIcons[c3][2], coverIcons[c4][3], coverIcons[c5][4], coverIcons[c6][5]);
   }

   public void renderShell() {
      this.context.setOrientation(0, 0);
      this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      if (this.covmasko > 0) {
         this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         this.setIcon(this.covs[0] & 255, this.covs[1] & 255, this.covs[2] & 255, this.covs[3] & 255, this.covs[4] & 255, this.covs[5] & 255);
         this.context.setTexFlags(55);
         this.context.calcBoundsGlobal();
         this.context.renderGlobFaces(this.covmasko);
      }

      int rsf = (this.covmasko | this.covmaskh) & ~this.covmaskt;
      if (rsf > 0) {
         for(int i = 0; i < 6; ++i) {
            if ((rsf & 1 << i) != 0) {
               this.setIcon(this.covs[i] & 255);
               int cn = this.covs[i] >> 8;
               int vf = 1 << (i ^ 1) | 63 ^ this.covmasko;
               if (cn >= 3 && cn <= 5 || cn >= 10 && cn <= 13) {
                  for(int j = 0; j < 4; ++j) {
                     this.setSize(i, CoverLib.getThickness(i, this.covs[i]));
                     this.sizeHollow(i, j);
                     this.context.calcBoundsGlobal();
                     this.context.renderGlobFaces(vf | this.innerFace(i, j));
                  }
               } else {
                  this.setSize(i, CoverLib.getThickness(i, this.covs[i]));
                  this.context.calcBoundsGlobal();
                  this.context.renderGlobFaces(vf);
               }
            }
         }
      }

      if (this.covmaskt > 0) {
         for(int i = 0; i < 6; ++i) {
            if ((this.covmaskt & 1 << i) != 0) {
               this.setIcon(this.covs[i] & 255);
               int cn = this.covs[i] >> 8;
               int vf = 1 << (i ^ 1) | 63 ^ this.covmasko;
               if (cn >= 3 && cn <= 5 || cn >= 10 && cn <= 13) {
                  for(int j = 0; j < 4; ++j) {
                     this.setSize(i, CoverLib.getThickness(i, this.covs[i]));
                     this.sizeHollow(i, j);
                     this.context.calcBoundsGlobal();
                     this.context.renderGlobFaces(vf | this.innerFace(i, j));
                  }
               } else {
                  this.setSize(i, CoverLib.getThickness(i, this.covs[i]));
                  this.context.calcBoundsGlobal();
                  this.context.renderGlobFaces(vf);
               }
            }
         }
      }

   }

   public void renderOthers() {
      float cth = 0.0F;
      int colc = 0;
      int coln = 0;

      for(int j = 26; j < 29; ++j) {
         if ((this.covmasko & 1 << j) != 0) {
            ++colc;
            float i = CoverLib.getThickness(j, this.covs[j]);
            if (i > cth) {
               coln = j;
               cth = i;
            }
         }
      }

      if (colc > 1) {
         this.setIcon(this.covs[coln] & 255);
         this.context.setSize(0.5 - (double)cth, 0.5 - (double)cth, 0.5 - (double)cth, 0.5 + (double)cth, 0.5 + (double)cth, 0.5 + (double)cth);
         this.context.calcBoundsGlobal();
         this.context.renderGlobFaces(63);

         for(int var61 = 26; var61 < 29; ++var61) {
            if ((this.covmasko & 1 << var61) != 0) {
               this.setIcon(this.covs[var61] & 255);
               this.setSize(var61, CoverLib.getThickness(var61, this.covs[var61]));
               if (this.sizeColumnSpoke(var61, false, cth)) {
                  this.context.calcBoundsGlobal();
                  this.context.renderGlobFaces(63);
               }

               if (this.sizeColumnSpoke(var61, true, cth)) {
                  this.context.calcBoundsGlobal();
                  this.context.renderGlobFaces(63);
               }
            }
         }
      } else if (colc == 1) {
         this.setIcon(this.covs[coln] & 255);
         this.setSize(coln, CoverLib.getThickness(coln, this.covs[coln]));
         this.context.calcBoundsGlobal();
         this.context.renderGlobFaces(63 ^ 3 << coln - 25 & this.covmasko);
      }

      this.setupCorners();

      for(int var7 = 6; var7 < 14; ++var7) {
         if ((this.covmasko & 1 << var7) != 0) {
            this.setSize(var7, CoverLib.getThickness(var7, this.covs[var7]));
            this.context.calcBoundsGlobal();
            this.setIcon(this.covs[var7] & 255);
            this.context.renderGlobFaces(63);
         }
      }

      for(int var8 = 6; var8 >= 0; --var8) {
         for(int var6 = 14; var6 < 26; ++var6) {
            if ((this.covmasko & 1 << var6) != 0 && this.covs[var6] >> 8 == var8) {
               this.setSize(var6, CoverLib.getThickness(var6, this.covs[var6]));
               this.context.calcBoundsGlobal();
               this.setIcon(this.covs[var6] & 255);
               this.context.renderGlobFaces(63);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void reInitIcons() {
      for(int i = 0; i < CoverLib.materials.length; ++i) {
         ItemStack is = CoverLib.materials[i];
         if (is != null) {
            Block b = Block.getBlockFromItem(is.getItem());
            if (b != null) {
               for(int side = 0; side < 6; ++side) {
                  coverIcons[i][side] = b.getIcon(side, is.getItemDamage());
               }
            }
         }
      }

   }
}
