package com.eloraam.redpower.core;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class RenderContext {
   public static final int[][] texRotTable = new int[][]{
      {0, 1, 2, 3, 4, 5, 0, 112347, 0},
      {0, 1, 4, 5, 3, 2, 45, 112320, 27},
      {0, 1, 3, 2, 5, 4, 27, 112347, 0},
      {0, 1, 5, 4, 2, 3, 54, 112320, 27},
      {1, 0, 2, 3, 5, 4, 112347, 112347, 0},
      {1, 0, 4, 5, 2, 3, 112374, 112320, 27},
      {1, 0, 3, 2, 4, 5, 112320, 112347, 0},
      {1, 0, 5, 4, 3, 2, 112365, 112320, 27},
      {4, 5, 0, 1, 2, 3, 217134, 1728, 110619},
      {3, 2, 0, 1, 4, 5, 220014, 0, 112347},
      {5, 4, 0, 1, 3, 2, 218862, 1728, 110619},
      {2, 3, 0, 1, 5, 4, 220590, 0, 112347},
      {4, 5, 1, 0, 3, 2, 188469, 1728, 110619},
      {3, 2, 1, 0, 5, 4, 191349, 0, 112347},
      {5, 4, 1, 0, 2, 3, 190197, 1728, 110619},
      {2, 3, 1, 0, 4, 5, 191925, 0, 112347},
      {4, 5, 3, 2, 0, 1, 2944, 110619, 1728},
      {3, 2, 5, 4, 0, 1, 187264, 27, 112320},
      {5, 4, 2, 3, 0, 1, 113536, 110619, 1728},
      {2, 3, 4, 5, 0, 1, 224128, 27, 112320},
      {4, 5, 2, 3, 1, 0, 3419, 110619, 1728},
      {3, 2, 4, 5, 1, 0, 187739, 27, 112320},
      {5, 4, 3, 2, 1, 0, 114011, 110619, 1728},
      {2, 3, 5, 4, 1, 0, 224603, 27, 112320}
   };
   public Matrix3 basis = new Matrix3();
   public Vector3 localOffset = new Vector3();
   public Vector3 globalOrigin = new Vector3();
   public Vector3 boxSize1 = new Vector3();
   public Vector3 boxSize2 = new Vector3();
   public RenderModel boundModel = null;
   public Vector3[] vertices;
   private Vector3[] verticesBox = new Vector3[8];
   public TexVertex[][] corners;
   private TexVertex[][] cornersBox = new TexVertex[6][4];
   private IIcon[] texIndex;
   private IIcon[] texIndexBox = new IIcon[6];
   private IIcon[][] texIndexList;
   public boolean lockTexture = false;
   public boolean exactTextureCoordinates = false;
   private int texFlags = 0;
   public boolean useNormal = false;
   public boolean forceFlat = false;
   private float tintR = 1.0F;
   private float tintG = 1.0F;
   private float tintB = 1.0F;
   private float tintA = 1.0F;
   public float[] lightLocal;
   private float[] lightLocalBox = new float[6];
   public int[] brightLocal;
   private int[] brightLocalBox = new int[6];
   private int[][][] lightGlobal = new int[3][3][3];
   private float[][][] aoGlobal = new float[3][3][3];
   private float[] lightFlat = new float[6];
   private int globTrans;

   public void setDefaults() {
      this.localOffset.set(0.0, 0.0, 0.0);
      this.setOrientation(0, 0);
      this.texFlags = 0;
      this.tintR = 1.0F;
      this.tintG = 1.0F;
      this.tintB = 1.0F;
      this.tintA = 1.0F;
      this.setLocalLights(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
      this.setBrightness(15728880);
   }

   public void bindTexture(ResourceLocation texture) {
      Minecraft.getMinecraft().renderEngine.bindTexture(texture);
   }

   public void bindBlockTexture() {
      this.bindTexture(TextureMap.locationBlocksTexture);
   }

   public void setPos(double x, double y, double z) {
      this.globalOrigin.set(x, y, z);
   }

   public void setPos(Vector3 v) {
      this.globalOrigin.set(v);
   }

   public void setRelPos(double x, double y, double z) {
      this.localOffset.set(x, y, z);
   }

   public void setRelPos(Vector3 v) {
      this.localOffset.set(v);
   }

   public void setOrientation(int down, int rot) {
      MathLib.orientMatrix(this.basis, down, rot);
   }

   public void setSize(double tx, double ty, double tz, double bx, double by, double bz) {
      this.boxSize1.set(tx, ty, tz);
      this.boxSize2.set(bx, by, bz);
   }

   public void setTexFlags(int fl) {
      this.texFlags = fl;
   }

   public void setTexRotation(RenderBlocks renderer, int rotation, boolean sides) {
      switch(rotation) {
         case 0:
            if (sides) {
               renderer.uvRotateEast = 3;
               renderer.uvRotateWest = 3;
               renderer.uvRotateSouth = 3;
               renderer.uvRotateNorth = 3;
            }
         case 1:
         default:
            break;
         case 2:
            if (sides) {
               renderer.uvRotateSouth = 1;
               renderer.uvRotateNorth = 2;
            }
            break;
         case 3:
            if (sides) {
               renderer.uvRotateSouth = 2;
               renderer.uvRotateNorth = 1;
            }

            renderer.uvRotateTop = 3;
            renderer.uvRotateBottom = 3;
            break;
         case 4:
            if (sides) {
               renderer.uvRotateEast = 1;
               renderer.uvRotateWest = 2;
            }

            renderer.uvRotateTop = 2;
            renderer.uvRotateBottom = 1;
            break;
         case 5:
            if (sides) {
               renderer.uvRotateEast = 2;
               renderer.uvRotateWest = 1;
            }

            renderer.uvRotateTop = 1;
            renderer.uvRotateBottom = 2;
      }

   }

   public void resetTexRotation(RenderBlocks renderer) {
      renderer.uvRotateEast = 0;
      renderer.uvRotateWest = 0;
      renderer.uvRotateSouth = 0;
      renderer.uvRotateNorth = 0;
      renderer.uvRotateTop = 0;
      renderer.uvRotateBottom = 0;
   }

   public void setIcon(IIcon bottom, IIcon top, IIcon north, IIcon south, IIcon west, IIcon east) {
      if (!this.lockTexture) {
         this.texIndex = this.texIndexBox;
         this.texIndex[0] = bottom;
         this.texIndex[1] = top;
         this.texIndex[2] = north;
         this.texIndex[3] = south;
         this.texIndex[4] = west;
         this.texIndex[5] = east;
      }

   }

   public void setIcon(IIcon universal) {
      if (!this.lockTexture) {
         this.texIndex = this.texIndexBox;
         this.texIndex[0] = universal;
         this.texIndex[1] = universal;
         this.texIndex[2] = universal;
         this.texIndex[3] = universal;
         this.texIndex[4] = universal;
         this.texIndex[5] = universal;
      }

   }

   public void setIcon(IIcon[] a) {
      if (!this.lockTexture) {
         this.texIndex = a;
      }

   }

   public void setIcon(IIcon[][] a) {
      if (!this.lockTexture) {
         this.texIndexList = a;
         this.texIndex = a[0];
      }

   }

   public void setIconIndex(int n) {
      if (this.texIndexList != null) {
         this.texIndex = this.texIndexList[n];
      }

   }

   public void setIconNum(int num, IIcon tex) {
      this.texIndex[num] = tex;
   }

   public void setTint(float r, float g, float b) {
      this.tintR = r;
      this.tintG = g;
      this.tintB = b;
   }

   public void setTintHex(int tc) {
      this.tintR = (float)(tc >> 16) / 255.0F;
      this.tintG = (float)(tc >> 8 & 0xFF) / 255.0F;
      this.tintB = (float)(tc & 0xFF) / 255.0F;
   }

   public void setAlpha(float a) {
      this.tintA = a;
   }

   public void setLocalLights(float a, float b, float c, float d, float e, float f) {
      this.lightLocal = this.lightLocalBox;
      this.lightLocal[0] = a;
      this.lightLocal[1] = b;
      this.lightLocal[2] = c;
      this.lightLocal[3] = d;
      this.lightLocal[4] = e;
      this.lightLocal[5] = f;
   }

   public void setLocalLights(float a) {
      this.lightLocal = this.lightLocalBox;

      for(int i = 0; i < 6; ++i) {
         this.lightLocal[i] = a;
      }

   }

   public void setBrightness(int a) {
      this.brightLocal = this.brightLocalBox;

      for(int i = 0; i < 6; ++i) {
         this.brightLocal[i] = a;
      }

   }

   public void startWorldRender(RenderBlocks rbl) {
   }

   public boolean endWorldRender() {
      return false;
   }

   public void setupBox() {
      this.vertices = this.verticesBox;
      this.vertices[0].set(this.boxSize2.x, this.boxSize2.y, this.boxSize1.z);
      this.vertices[1].set(this.boxSize1.x, this.boxSize2.y, this.boxSize1.z);
      this.vertices[2].set(this.boxSize1.x, this.boxSize2.y, this.boxSize2.z);
      this.vertices[3].set(this.boxSize2.x, this.boxSize2.y, this.boxSize2.z);
      this.vertices[4].set(this.boxSize2.x, this.boxSize1.y, this.boxSize1.z);
      this.vertices[5].set(this.boxSize1.x, this.boxSize1.y, this.boxSize1.z);
      this.vertices[6].set(this.boxSize1.x, this.boxSize1.y, this.boxSize2.z);
      this.vertices[7].set(this.boxSize2.x, this.boxSize1.y, this.boxSize2.z);
   }

   public void transformRotate() {
      for(Vector3 vec : this.vertices) {
         vec.add(this.localOffset.x - 0.5, this.localOffset.y - 0.5, this.localOffset.z - 0.5);
         this.basis.rotate(vec);
         vec.add(this.globalOrigin.x + 0.5, this.globalOrigin.y + 0.5, this.globalOrigin.z + 0.5);
      }

   }

   public void transform() {
      for(Vector3 vec : this.vertices) {
         vec.add(this.localOffset);
         vec.add(this.globalOrigin);
      }

   }

   public void setSideUV(int side, double uMin, double uMax, double vMin, double vMax) {
      if (!this.exactTextureCoordinates) {
         uMin += 0.001;
         vMin += 0.001;
         uMax -= 0.001;
         vMax -= 0.001;
      }

      int txl = this.texFlags >> side * 3;
      if ((txl & 1) > 0) {
         uMin = 1.0 - uMin;
         uMax = 1.0 - uMax;
      }

      if ((txl & 2) > 0) {
         vMin = 1.0 - vMin;
         vMax = 1.0 - vMax;
      }

      IIcon icon = this.texIndex[side];
      if (icon != null) {
         if ((txl & 4) > 0) {
            double uStart = (double)icon.getInterpolatedV(uMin * 16.0);
            double uEnd = (double)icon.getInterpolatedV(uMax * 16.0);
            double vStart = (double)icon.getInterpolatedU(vMin * 16.0);
            double vEnd = (double)icon.getInterpolatedU(vMax * 16.0);
            this.corners[side][0].setUV(vStart, uStart);
            this.corners[side][1].setUV(vEnd, uStart);
            this.corners[side][2].setUV(vEnd, uEnd);
            this.corners[side][3].setUV(vStart, uEnd);
         } else {
            double uStart = (double)icon.getInterpolatedU(uMin * 16.0);
            double uEnd = (double)icon.getInterpolatedU(uMax * 16.0);
            double vStart = (double)icon.getInterpolatedV(vMin * 16.0);
            double vEnd = (double)icon.getInterpolatedV(vMax * 16.0);
            this.corners[side][0].setUV(uStart, vStart);
            this.corners[side][1].setUV(uStart, vEnd);
            this.corners[side][2].setUV(uEnd, vEnd);
            this.corners[side][3].setUV(uEnd, vStart);
         }
      }

   }

   public void doMappingBox(int sides) {
      this.corners = this.cornersBox;
      if ((sides & 3) > 0) {
         double vMin = 1.0 - this.boxSize2.x;
         double vMax = 1.0 - this.boxSize1.x;
         if ((sides & 1) > 0) {
            double uMin = 1.0 - this.boxSize2.z;
            double uMax = 1.0 - this.boxSize1.z;
            this.setSideUV(0, uMin, uMax, vMin, vMax);
         }

         if ((sides & 2) > 0) {
            double uMin = this.boxSize1.z;
            double uMax = this.boxSize2.z;
            this.setSideUV(1, uMin, uMax, vMin, vMax);
         }
      }

      if ((sides & 60) != 0) {
         double vMin = 1.0 - this.boxSize2.y;
         double vMax = 1.0 - this.boxSize1.y;
         if ((sides & 4) > 0) {
            double uMin = 1.0 - this.boxSize2.x;
            double uMax = 1.0 - this.boxSize1.x;
            this.setSideUV(2, uMin, uMax, vMin, vMax);
         }

         if ((sides & 8) > 0) {
            double uMin = this.boxSize1.x;
            double uMax = this.boxSize2.x;
            this.setSideUV(3, uMin, uMax, vMin, vMax);
         }

         if ((sides & 16) > 0) {
            double uMin = this.boxSize1.z;
            double uMax = this.boxSize2.z;
            this.setSideUV(4, uMin, uMax, vMin, vMax);
         }

         if ((sides & 32) > 0) {
            double uMin = 1.0 - this.boxSize2.z;
            double uMax = 1.0 - this.boxSize1.z;
            this.setSideUV(5, uMin, uMax, vMin, vMax);
         }
      }

   }

   public void calcBoundsGlobal() {
      this.setupBox();
      this.transform();
   }

   public void calcBounds() {
      this.setupBox();
      this.transformRotate();
   }

   private void swapTex(int a, int b) {
      IIcon tex = this.texIndexBox[a];
      this.texIndexBox[a] = this.texIndexBox[b];
      this.texIndexBox[b] = tex;
   }

   public void orientTextures(int down) {
      switch(down) {
         case 0:
         default:
            break;
         case 1:
            this.swapTex(0, 1);
            this.swapTex(4, 5);
            this.texFlags = 112347;
            break;
         case 2:
            this.swapTex(0, 2);
            this.swapTex(1, 3);
            this.swapTex(0, 4);
            this.swapTex(1, 5);
            this.texFlags = 217134;
            break;
         case 3:
            this.swapTex(0, 3);
            this.swapTex(1, 2);
            this.swapTex(0, 4);
            this.swapTex(1, 5);
            this.texFlags = 188469;
            break;
         case 4:
            this.swapTex(0, 4);
            this.swapTex(1, 5);
            this.swapTex(2, 3);
            this.texFlags = 2944;
            break;
         case 5:
            this.swapTex(0, 5);
            this.swapTex(1, 4);
            this.swapTex(0, 1);
            this.texFlags = 3419;
      }

   }

   public void orientTextureRot(int down, int rot) {
      int r = rot > 1 ? (rot == 2 ? 3 : 6) : (rot == 0 ? 0 : 5);
      r |= r << 3;
      switch(down) {
         case 0:
            this.texFlags = r;
            break;
         case 1:
            this.swapTex(0, 1);
            this.swapTex(4, 5);
            this.texFlags = 112347 ^ r;
            break;
         case 2:
            this.swapTex(0, 2);
            this.swapTex(1, 3);
            this.swapTex(0, 4);
            this.swapTex(1, 5);
            this.texFlags = 217134 ^ r << 6;
            break;
         case 3:
            this.swapTex(0, 3);
            this.swapTex(1, 2);
            this.swapTex(0, 4);
            this.swapTex(1, 5);
            this.texFlags = 188469 ^ r << 6;
            break;
         case 4:
            this.swapTex(0, 4);
            this.swapTex(1, 5);
            this.swapTex(2, 3);
            this.texFlags = 2944 ^ r << 12;
            break;
         case 5:
            this.swapTex(0, 5);
            this.swapTex(1, 4);
            this.swapTex(0, 1);
            this.texFlags = 3419 ^ r << 12;
      }

   }

   private void swapTexFl(int a, int b) {
      IIcon t = this.texIndexBox[a];
      this.texIndexBox[a] = this.texIndexBox[b];
      this.texIndexBox[b] = t;
      a *= 3;
      b *= 3;
      int f1 = this.texFlags >> a & 7;
      int f2 = this.texFlags >> b & 7;
      this.texFlags &= ~(7 << a | 7 << b);
      this.texFlags |= f1 << b | f2 << a;
   }

   public void rotateTextures(int rot) {
      int r = rot > 1 ? (rot == 2 ? 3 : 6) : (rot == 0 ? 0 : 5);
      r |= r << 3;
      this.texFlags ^= r;
      switch(rot) {
         case 1:
            this.swapTexFl(2, 4);
            this.swapTexFl(3, 4);
            this.swapTexFl(3, 5);
            break;
         case 2:
            this.swapTexFl(2, 3);
            this.swapTexFl(4, 5);
            break;
         case 3:
            this.swapTexFl(2, 5);
            this.swapTexFl(3, 5);
            this.swapTexFl(3, 4);
      }

   }

   public void orientTextureFl(int down) {
      switch(down) {
         case 0:
         default:
            break;
         case 1:
            this.swapTexFl(0, 1);
            this.swapTexFl(4, 5);
            this.texFlags ^= 112347;
            break;
         case 2:
            this.swapTexFl(0, 2);
            this.swapTexFl(1, 3);
            this.swapTexFl(0, 4);
            this.swapTexFl(1, 5);
            this.texFlags ^= 217134;
            break;
         case 3:
            this.swapTexFl(0, 3);
            this.swapTexFl(1, 2);
            this.swapTexFl(0, 4);
            this.swapTexFl(1, 5);
            this.texFlags ^= 188469;
            break;
         case 4:
            this.swapTexFl(0, 4);
            this.swapTexFl(1, 5);
            this.swapTexFl(2, 3);
            this.texFlags ^= 2944;
            break;
         case 5:
            this.swapTexFl(0, 5);
            this.swapTexFl(1, 4);
            this.swapTexFl(0, 1);
            this.texFlags ^= 3419;
      }

   }

   public void orientTextureNew(int rv) {
      IIcon[] texSrc = new IIcon[6];
      System.arraycopy(this.texIndexBox, 0, texSrc, 0, 6);
      int[] rot = texRotTable[rv];
      int tfo = 0;

      for(int i = 0; i < 6; ++i) {
         this.texIndexBox[i] = texSrc[rot[i]];
         tfo |= (this.texFlags >> rot[i] * 3 & 7) << i * 3;
      }

      int t2 = (tfo & 37449) << 1 | (tfo & 74898) >> 1;
      this.texFlags = rot[6] ^ tfo & rot[7] ^ t2 & rot[8];
   }

   public void flipTextures() {
      this.swapTex(0, 1);
      this.swapTex(2, 3);
      this.swapTex(4, 5);
   }

   public void renderBox(int sides, double x1, double y1, double z1, double x2, double y2, double z2) {
      this.setSize(x1, y1, z1, x2, y2, z2);
      this.setupBox();
      this.transformRotate();
      this.renderFaces(sides);
   }

   public void doubleBox(int sides, double x1, double y1, double z1, double x2, double y2, double z2, double ino) {
      int s2 = sides << 1 & 42 | sides >> 1 & 21;
      this.renderBox(sides, x1, y1, z1, x2, y2, z2);
      this.flipTextures();
      this.renderBox(s2, x2 - ino, y2 - ino, z2 - ino, x1 + ino, y1 + ino, z1 + ino);
   }

   public void doLightLocal(int sides) {
      for(int i = 0; i < this.corners.length; ++i) {
         if ((sides & 1 << i) != 0) {
            TexVertex c = this.corners[i][0];
            c.r = this.lightLocal[i] * this.tintR;
            c.g = this.lightLocal[i] * this.tintG;
            c.b = this.lightLocal[i] * this.tintB;
            c.brtex = this.brightLocal[i];
         }
      }

   }

   public void readGlobalLights(IBlockAccess iba, int i, int j, int k) {
      Block block = iba.getBlock(i, j, k);
      if (Minecraft.isAmbientOcclusionEnabled() && !this.forceFlat) {
         for(int a = 0; a < 3; ++a) {
            for(int b = 0; b < 3; ++b) {
               for(int c = 0; c < 3; ++c) {
                  this.aoGlobal[a][b][c] = iba.getBlock(i + a - 1, j + b - 1, k + c - 1).getAmbientOcclusionLightValue();
                  this.lightGlobal[a][b][c] = block.getMixedBrightnessForBlock(iba, i + a - 1, j + b - 1, k + c - 1);
               }
            }
         }

         int t = 0;
         if (iba.getBlock(i, j - 1, k - 1).getCanBlockGrass()) {
            t |= 1;
         }

         if (iba.getBlock(i, j - 1, k + 1).getCanBlockGrass()) {
            t |= 2;
         }

         if (iba.getBlock(i - 1, j - 1, k).getCanBlockGrass()) {
            t |= 4;
         }

         if (iba.getBlock(i + 1, j - 1, k).getCanBlockGrass()) {
            t |= 8;
         }

         if (iba.getBlock(i - 1, j, k - 1).getCanBlockGrass()) {
            t |= 16;
         }

         if (iba.getBlock(i - 1, j, k + 1).getCanBlockGrass()) {
            t |= 32;
         }

         if (iba.getBlock(i + 1, j, k - 1).getCanBlockGrass()) {
            t |= 64;
         }

         if (iba.getBlock(i + 1, j, k + 1).getCanBlockGrass()) {
            t |= 128;
         }

         if (iba.getBlock(i, j + 1, k - 1).getCanBlockGrass()) {
            t |= 256;
         }

         if (iba.getBlock(i, j + 1, k + 1).getCanBlockGrass()) {
            t |= 512;
         }

         if (iba.getBlock(i - 1, j + 1, k).getCanBlockGrass()) {
            t |= 1024;
         }

         if (iba.getBlock(i + 1, j + 1, k).getCanBlockGrass()) {
            t |= 2048;
         }

         this.globTrans = t;
      } else {
         this.lightFlat[0] = (float)block.getMixedBrightnessForBlock(iba, i, j - 1, k);
         this.lightFlat[1] = (float)block.getMixedBrightnessForBlock(iba, i, j + 1, k);
         this.lightFlat[2] = (float)block.getMixedBrightnessForBlock(iba, i, j, k - 1);
         this.lightFlat[3] = (float)block.getMixedBrightnessForBlock(iba, i, j, k + 1);
         this.lightFlat[4] = (float)block.getMixedBrightnessForBlock(iba, i - 1, j, k);
         this.lightFlat[5] = (float)block.getMixedBrightnessForBlock(iba, i + 1, j, k);
      }

   }

   public static int blendLight(int i, int j, int k, int l) {
      if (j == 0) {
         j = i;
      }

      if (k == 0) {
         k = i;
      }

      if (l == 0) {
         l = i;
      }

      return i + j + k + l >> 2 & 16711935;
   }

   private void lightSmoothFace(int fn) {
      int ff = 0;
      if (this.boxSize1.y > 0.0) {
         ff |= 1;
      }

      if (this.boxSize2.y < 1.0) {
         ff |= 2;
      }

      if (this.boxSize1.z > 0.0) {
         ff |= 4;
      }

      if (this.boxSize2.z < 1.0) {
         ff |= 8;
      }

      if (this.boxSize1.x > 0.0) {
         ff |= 16;
      }

      if (this.boxSize2.x < 1.0) {
         ff |= 32;
      }

      float gf2;
      float gf3;
      float gf4;
      float gf1 = gf2 = gf3 = gf4 = this.aoGlobal[1][1][1];
      int gl2;
      int gl3;
      int gl4;
      int gl1 = gl2 = gl3 = gl4 = this.lightGlobal[1][1][1];
      switch(fn) {
         case 0:
            if ((ff & 61) <= 0) {
               float ao2;
               float ao1 = ao2 = this.aoGlobal[0][0][1];
               float ao4;
               float ao3 = ao4 = this.aoGlobal[2][0][1];
               int lv2;
               int lv1 = lv2 = this.lightGlobal[0][0][1];
               int lv4;
               int lv3 = lv4 = this.lightGlobal[2][0][1];
               if ((this.globTrans & 5) > 0) {
                  ao1 = this.aoGlobal[0][0][0];
                  lv1 = this.lightGlobal[0][0][0];
               }

               if ((this.globTrans & 6) > 0) {
                  ao2 = this.aoGlobal[0][0][2];
                  lv2 = this.lightGlobal[0][0][2];
               }

               if ((this.globTrans & 9) > 0) {
                  ao3 = this.aoGlobal[2][0][0];
                  lv3 = this.lightGlobal[2][0][0];
               }

               if ((this.globTrans & 10) > 0) {
                  ao4 = this.aoGlobal[2][0][2];
                  lv4 = this.lightGlobal[2][0][2];
               }

               gf3 = 0.25F * (this.aoGlobal[1][0][1] + this.aoGlobal[1][0][0] + this.aoGlobal[0][0][1] + ao1);
               gf4 = 0.25F * (this.aoGlobal[1][0][1] + this.aoGlobal[1][0][0] + this.aoGlobal[2][0][1] + ao3);
               gf2 = 0.25F * (this.aoGlobal[1][0][1] + this.aoGlobal[1][0][2] + this.aoGlobal[0][0][1] + ao2);
               gf1 = 0.25F * (this.aoGlobal[1][0][1] + this.aoGlobal[1][0][2] + this.aoGlobal[2][0][1] + ao4);
               gl3 = blendLight(this.lightGlobal[1][0][1], this.lightGlobal[1][0][0], this.lightGlobal[0][0][1], lv1);
               gl4 = blendLight(this.lightGlobal[1][0][1], this.lightGlobal[1][0][0], this.lightGlobal[2][0][1], lv3);
               gl2 = blendLight(this.lightGlobal[1][0][1], this.lightGlobal[1][0][2], this.lightGlobal[0][0][1], lv2);
               gl1 = blendLight(this.lightGlobal[1][0][1], this.lightGlobal[1][0][2], this.lightGlobal[2][0][1], lv4);
            }
            break;
         case 1:
            if ((ff & 62) <= 0) {
               float ao2;
               float ao1 = ao2 = this.aoGlobal[0][2][1];
               float ao4;
               float ao3 = ao4 = this.aoGlobal[2][2][1];
               int lv2;
               int lv1 = lv2 = this.lightGlobal[0][2][1];
               int lv4;
               int lv3 = lv4 = this.lightGlobal[2][2][1];
               if ((this.globTrans & 1280) > 0) {
                  ao1 = this.aoGlobal[0][2][0];
                  lv1 = this.lightGlobal[0][2][0];
               }

               if ((this.globTrans & 1536) > 0) {
                  ao2 = this.aoGlobal[0][2][2];
                  lv2 = this.lightGlobal[0][2][2];
               }

               if ((this.globTrans & 2304) > 0) {
                  ao3 = this.aoGlobal[2][2][0];
                  lv3 = this.lightGlobal[2][2][0];
               }

               if ((this.globTrans & 2560) > 0) {
                  ao4 = this.aoGlobal[2][2][2];
                  lv4 = this.lightGlobal[2][2][2];
               }

               gf2 = 0.25F * (this.aoGlobal[1][2][1] + this.aoGlobal[1][2][0] + this.aoGlobal[0][2][1] + ao1);
               gf1 = 0.25F * (this.aoGlobal[1][2][1] + this.aoGlobal[1][2][0] + this.aoGlobal[2][2][1] + ao3);
               gf3 = 0.25F * (this.aoGlobal[1][2][1] + this.aoGlobal[1][2][2] + this.aoGlobal[0][2][1] + ao2);
               gf4 = 0.25F * (this.aoGlobal[1][2][1] + this.aoGlobal[1][2][2] + this.aoGlobal[2][2][1] + ao4);
               gl2 = blendLight(this.lightGlobal[1][2][1], this.lightGlobal[1][2][0], this.lightGlobal[0][2][1], lv1);
               gl1 = blendLight(this.lightGlobal[1][2][1], this.lightGlobal[1][2][0], this.lightGlobal[2][2][1], lv3);
               gl3 = blendLight(this.lightGlobal[1][2][1], this.lightGlobal[1][2][2], this.lightGlobal[0][2][1], lv2);
               gl4 = blendLight(this.lightGlobal[1][2][1], this.lightGlobal[1][2][2], this.lightGlobal[2][2][1], lv4);
            }
            break;
         case 2:
            if ((ff & 55) <= 0) {
               float ao2;
               float ao1 = ao2 = this.aoGlobal[0][1][0];
               float ao4;
               float ao3 = ao4 = this.aoGlobal[2][1][0];
               int lv2;
               int lv1 = lv2 = this.lightGlobal[0][1][0];
               int lv4;
               int lv3 = lv4 = this.lightGlobal[2][1][0];
               if ((this.globTrans & 17) > 0) {
                  ao1 = this.aoGlobal[0][0][0];
                  lv1 = this.lightGlobal[0][0][0];
               }

               if ((this.globTrans & 272) > 0) {
                  ao2 = this.aoGlobal[0][2][0];
                  lv2 = this.lightGlobal[0][2][0];
               }

               if ((this.globTrans & 65) > 0) {
                  ao3 = this.aoGlobal[2][0][0];
                  lv3 = this.lightGlobal[2][0][0];
               }

               if ((this.globTrans & 320) > 0) {
                  ao4 = this.aoGlobal[2][2][0];
                  lv4 = this.lightGlobal[2][2][0];
               }

               gf3 = 0.25F * (this.aoGlobal[1][1][0] + this.aoGlobal[1][0][0] + this.aoGlobal[0][1][0] + ao1);
               gf4 = 0.25F * (this.aoGlobal[1][1][0] + this.aoGlobal[1][2][0] + this.aoGlobal[0][1][0] + ao2);
               gf2 = 0.25F * (this.aoGlobal[1][1][0] + this.aoGlobal[1][0][0] + this.aoGlobal[2][1][0] + ao3);
               gf1 = 0.25F * (this.aoGlobal[1][1][0] + this.aoGlobal[1][2][0] + this.aoGlobal[2][1][0] + ao4);
               gl3 = blendLight(this.lightGlobal[1][1][0], this.lightGlobal[1][0][0], this.lightGlobal[0][1][0], lv1);
               gl4 = blendLight(this.lightGlobal[1][1][0], this.lightGlobal[1][2][0], this.lightGlobal[0][1][0], lv2);
               gl2 = blendLight(this.lightGlobal[1][1][0], this.lightGlobal[1][0][0], this.lightGlobal[2][1][0], lv3);
               gl1 = blendLight(this.lightGlobal[1][1][0], this.lightGlobal[1][2][0], this.lightGlobal[2][1][0], lv4);
            }
            break;
         case 3:
            if ((ff & 59) <= 0) {
               float ao2;
               float ao1 = ao2 = this.aoGlobal[0][1][2];
               float ao4;
               float ao3 = ao4 = this.aoGlobal[2][1][2];
               int lv2;
               int lv1 = lv2 = this.lightGlobal[0][1][2];
               int lv4;
               int lv3 = lv4 = this.lightGlobal[2][1][2];
               if ((this.globTrans & 34) > 0) {
                  ao1 = this.aoGlobal[0][0][2];
                  lv1 = this.lightGlobal[0][0][2];
               }

               if ((this.globTrans & 544) > 0) {
                  ao2 = this.aoGlobal[0][2][2];
                  lv2 = this.lightGlobal[0][2][2];
               }

               if ((this.globTrans & 130) > 0) {
                  ao3 = this.aoGlobal[2][0][2];
                  lv3 = this.lightGlobal[2][0][2];
               }

               if ((this.globTrans & 640) > 0) {
                  ao4 = this.aoGlobal[2][2][2];
                  lv4 = this.lightGlobal[2][2][2];
               }

               gf2 = 0.25F * (this.aoGlobal[1][1][2] + this.aoGlobal[1][0][2] + this.aoGlobal[0][1][2] + ao1);
               gf1 = 0.25F * (this.aoGlobal[1][1][2] + this.aoGlobal[1][2][2] + this.aoGlobal[0][1][2] + ao3);
               gf3 = 0.25F * (this.aoGlobal[1][1][2] + this.aoGlobal[1][0][2] + this.aoGlobal[2][1][2] + ao2);
               gf4 = 0.25F * (this.aoGlobal[1][1][2] + this.aoGlobal[1][2][2] + this.aoGlobal[2][1][2] + ao4);
               gl2 = blendLight(this.lightGlobal[1][1][2], this.lightGlobal[1][0][2], this.lightGlobal[0][1][2], lv1);
               gl1 = blendLight(this.lightGlobal[1][1][2], this.lightGlobal[1][2][2], this.lightGlobal[0][1][2], lv2);
               gl3 = blendLight(this.lightGlobal[1][1][2], this.lightGlobal[1][0][2], this.lightGlobal[2][1][2], lv3);
               gl4 = blendLight(this.lightGlobal[1][1][2], this.lightGlobal[1][2][2], this.lightGlobal[2][1][2], lv4);
            }
            break;
         case 4:
            if ((ff & 31) <= 0) {
               float ao2;
               float ao1 = ao2 = this.aoGlobal[0][1][0];
               float ao4;
               float ao3 = ao4 = this.aoGlobal[0][1][2];
               int lv2;
               int lv1 = lv2 = this.lightGlobal[0][1][0];
               int lv4;
               int lv3 = lv4 = this.lightGlobal[0][1][2];
               if ((this.globTrans & 20) > 0) {
                  ao1 = this.aoGlobal[0][0][0];
                  lv1 = this.lightGlobal[0][0][0];
               }

               if ((this.globTrans & 1040) > 0) {
                  ao2 = this.aoGlobal[0][2][0];
                  lv2 = this.lightGlobal[0][2][0];
               }

               if ((this.globTrans & 36) > 0) {
                  ao3 = this.aoGlobal[0][0][2];
                  lv3 = this.lightGlobal[0][0][2];
               }

               if ((this.globTrans & 1056) > 0) {
                  ao4 = this.aoGlobal[0][2][2];
                  lv4 = this.lightGlobal[0][2][2];
               }

               gf2 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][0][1] + this.aoGlobal[0][1][0] + ao1);
               gf1 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][2][1] + this.aoGlobal[0][1][0] + ao2);
               gf3 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][0][1] + this.aoGlobal[0][1][2] + ao3);
               gf4 = 0.25F * (this.aoGlobal[0][1][1] + this.aoGlobal[0][2][1] + this.aoGlobal[0][1][2] + ao4);
               gl2 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][0][1], this.lightGlobal[0][1][0], lv1);
               gl1 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][2][1], this.lightGlobal[0][1][0], lv2);
               gl3 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][0][1], this.lightGlobal[0][1][2], lv3);
               gl4 = blendLight(this.lightGlobal[0][1][1], this.lightGlobal[0][2][1], this.lightGlobal[0][1][2], lv4);
            }
            break;
         default:
            if ((ff & 47) <= 0) {
               float ao2;
               float ao1 = ao2 = this.aoGlobal[2][1][0];
               float ao4;
               float ao3 = ao4 = this.aoGlobal[2][1][2];
               int lv2;
               int lv1 = lv2 = this.lightGlobal[2][1][0];
               int lv4;
               int lv3 = lv4 = this.lightGlobal[2][1][2];
               if ((this.globTrans & 72) > 0) {
                  ao1 = this.aoGlobal[2][0][0];
                  lv1 = this.lightGlobal[2][0][0];
               }

               if ((this.globTrans & 2112) > 0) {
                  ao2 = this.aoGlobal[2][2][0];
                  lv2 = this.lightGlobal[2][2][0];
               }

               if ((this.globTrans & 136) > 0) {
                  ao3 = this.aoGlobal[2][0][2];
                  lv3 = this.lightGlobal[2][0][2];
               }

               if ((this.globTrans & 2176) > 0) {
                  ao4 = this.aoGlobal[2][2][2];
                  lv4 = this.lightGlobal[2][2][2];
               }

               gf3 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][0][1] + this.aoGlobal[2][1][0] + ao1);
               gf4 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][2][1] + this.aoGlobal[2][1][0] + ao2);
               gf2 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][0][1] + this.aoGlobal[2][1][2] + ao3);
               gf1 = 0.25F * (this.aoGlobal[2][1][1] + this.aoGlobal[2][2][1] + this.aoGlobal[2][1][2] + ao4);
               gl3 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][0][1], this.lightGlobal[2][1][0], lv1);
               gl4 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][2][1], this.lightGlobal[2][1][0], lv2);
               gl2 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][0][1], this.lightGlobal[2][1][2], lv3);
               gl1 = blendLight(this.lightGlobal[2][1][1], this.lightGlobal[2][2][1], this.lightGlobal[2][1][2], lv4);
            }
      }

      TexVertex c = this.corners[fn][0];
      float fc = this.lightLocal[fn] * gf1;
      c.r = fc * this.tintR;
      c.g = fc * this.tintG;
      c.b = fc * this.tintB;
      c.brtex = gl1;
      c = this.corners[fn][1];
      fc = this.lightLocal[fn] * gf2;
      c.r = fc * this.tintR;
      c.g = fc * this.tintG;
      c.b = fc * this.tintB;
      c.brtex = gl2;
      c = this.corners[fn][2];
      fc = this.lightLocal[fn] * gf3;
      c.r = fc * this.tintR;
      c.g = fc * this.tintG;
      c.b = fc * this.tintB;
      c.brtex = gl3;
      c = this.corners[fn][3];
      fc = this.lightLocal[fn] * gf4;
      c.r = fc * this.tintR;
      c.g = fc * this.tintG;
      c.b = fc * this.tintB;
      c.brtex = gl4;
   }

   public void doLightSmooth(int sides) {
      for(int i = 0; i < 6; ++i) {
         if ((sides & 1 << i) != 0) {
            this.lightSmoothFace(i);
         }
      }

   }

   private void doLightFlat(int sides) {
      for(int i = 0; i < this.corners.length; ++i) {
         if ((sides & 1 << i) != 0) {
            TexVertex c = this.corners[i][0];
            c.r = this.lightFlat[i] * this.lightLocal[i] * this.tintR;
            c.g = this.lightFlat[i] * this.lightLocal[i] * this.tintG;
            c.b = this.lightFlat[i] * this.lightLocal[i] * this.tintB;
            c.brtex = this.brightLocal[i];
         }
      }

   }

   public void renderFlat(int sides) {
      Tessellator tess = Tessellator.instance;

      for(int i = 0; i < this.corners.length; ++i) {
         if ((sides & 1 << i) != 0) {
            TexVertex c = this.corners[i][0];
            tess.setColorOpaque_F(c.r, c.g, c.b);
            if (this.useNormal) {
               Vector3 v = this.vertices[c.vtx];
               c = this.corners[i][1];
               Vector3 v1 = new Vector3(this.vertices[c.vtx]);
               c = this.corners[i][2];
               Vector3 v2 = new Vector3(this.vertices[c.vtx]);
               v1.subtract(v);
               v2.subtract(v);
               v1.crossProduct(v2);
               v1.normalize();
               tess.setNormal((float)v1.x, (float)v1.y, (float)v1.z);
            } else {
               tess.setBrightness(c.brtex);
            }

            for(int j = 0; j < 4; ++j) {
               c = this.corners[i][j];
               Vector3 v = this.vertices[c.vtx];
               tess.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
            }
         }
      }

   }

   public void renderRangeFlat(int st, int ed) {
      Tessellator tess = Tessellator.instance;

      for(int i = st; i < ed; ++i) {
         TexVertex c = this.corners[i][0];
         tess.setColorRGBA_F(c.r * this.tintR, c.g * this.tintG, c.b * this.tintB, this.tintA);
         if (this.useNormal) {
            Vector3 v = this.vertices[c.vtx];
            c = this.corners[i][1];
            Vector3 var8 = new Vector3(this.vertices[c.vtx]);
            c = this.corners[i][2];
            Vector3 var9 = new Vector3(this.vertices[c.vtx]);
            var8.subtract(v);
            var9.subtract(v);
            var8.crossProduct(var9);
            var8.normalize();
            tess.setNormal((float)var8.x, (float)var8.y, (float)var8.z);
         } else {
            tess.setBrightness(c.brtex);
         }

         for(int j = 0; j < 4; ++j) {
            c = this.corners[i][j];
            Vector3 v = this.vertices[c.vtx];
            tess.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
         }
      }

   }

   public void renderAlpha(int sides, float alpha) {
      Tessellator tess = Tessellator.instance;

      for(int i = 0; i < this.corners.length; ++i) {
         if ((sides & 1 << i) != 0) {
            TexVertex c = this.corners[i][0];
            tess.setColorRGBA_F(c.r, c.g, c.b, alpha);
            if (!this.useNormal) {
               tess.setBrightness(c.brtex);
            }

            for(int j = 0; j < 4; ++j) {
               c = this.corners[i][j];
               Vector3 v = this.vertices[c.vtx];
               tess.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
            }
         }
      }

   }

   public void renderSmooth(int sides) {
      Tessellator tess = Tessellator.instance;

      for(int i = 0; i < this.corners.length; ++i) {
         if ((sides & 1 << i) != 0) {
            for(int j = 0; j < 4; ++j) {
               TexVertex c = this.corners[i][j];
               tess.setColorOpaque_F(c.r, c.g, c.b);
               if (!this.useNormal) {
                  tess.setBrightness(c.brtex);
               }

               Vector3 v = this.vertices[c.vtx];
               tess.addVertexWithUV(v.x, v.y, v.z, c.u, c.v);
            }
         }
      }

   }

   public void renderFaces(int faces) {
      this.doMappingBox(faces);
      this.doLightLocal(faces);
      this.renderFlat(faces);
   }

   public void renderGlobFaces(int faces) {
      this.doMappingBox(faces);
      this.doLightLocal(faces);
      if (Minecraft.isAmbientOcclusionEnabled() && !this.forceFlat) {
         this.doLightSmooth(faces);
         this.renderSmooth(faces);
      } else {
         this.doLightFlat(faces);
         this.renderFlat(faces);
      }

   }

   public void drawPoints(int... points) {
      Tessellator tess = Tessellator.instance;

      for(int p : points) {
         Vector3 vec = this.vertices[p];
         tess.addVertex(vec.x, vec.y, vec.z);
      }

   }

   public void bindModel(RenderModel model) {
      this.vertices = new Vector3[model.vertices.length];

      for(int i = 0; i < this.vertices.length; ++i) {
         Vector3 v = new Vector3(model.vertices[i]);
         this.basis.rotate(v);
         v.add(this.globalOrigin);
         this.vertices[i] = v;
      }

      this.corners = model.texs;
      this.boundModel = model;
   }

   public void bindModelOffset(RenderModel model, double ofx, double ofy, double ofz) {
      this.vertices = new Vector3[model.vertices.length];

      for(int i = 0; i < this.vertices.length; ++i) {
         Vector3 v = new Vector3(model.vertices[i]);
         v.add(this.localOffset.x - ofx, this.localOffset.y - ofy, this.localOffset.z - ofz);
         this.basis.rotate(v);
         v.add(ofx, ofy, ofz);
         v.add(this.globalOrigin);
         this.vertices[i] = v;
      }

      this.corners = model.texs;
      this.boundModel = model;
   }

   public void renderModelGroup(int gr, int sgr) {
      for(TexVertex[] corner : this.corners) {
         TexVertex c = corner[0];
         c.brtex = this.brightLocal[0];
      }

      this.renderRangeFlat(this.boundModel.groups[gr][sgr][0], this.boundModel.groups[gr][sgr][1]);
   }

   public void renderModel(RenderModel model) {
      this.bindModel(model);

      for(int i = 0; i < this.corners.length; ++i) {
         TexVertex c = this.corners[i][0];
         c.brtex = this.brightLocal[0];
      }

      this.renderRangeFlat(0, this.corners.length);
   }

   public RenderContext() {
      for(int i = 0; i < 8; ++i) {
         this.verticesBox[i] = new Vector3();
      }

      int[][] vtxl = new int[][]{{7, 6, 5, 4}, {0, 1, 2, 3}, {0, 4, 5, 1}, {2, 6, 7, 3}, {1, 5, 6, 2}, {3, 7, 4, 0}};

      for(int i = 0; i < 6; ++i) {
         for(int j = 0; j < 4; ++j) {
            this.cornersBox[i][j] = new TexVertex();
            this.cornersBox[i][j].vtx = vtxl[i][j];
         }
      }

      this.setDefaults();
   }
}
