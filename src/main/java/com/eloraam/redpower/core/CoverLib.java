package com.eloraam.redpower.core;

import cpw.mods.fml.common.FMLCommonHandler;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class CoverLib {
   public static final float selectBoxWidth = 0.25F;
   public static Block blockCoverPlate = null;
   public static ItemStack[] materials = new ItemStack[256];
   private static String[] names = new String[256];
   private static int[] hardness = new int[256];
   private static List<CoverLib.IMaterialHandler> materialHandlers = new ArrayList();
   private static boolean[] transparency = new boolean[256];
   private static float[] miningHardness = new float[256];
   private static Map<Item, List<Integer>> coverIndex = new LinkedHashMap();

   public static void addMaterialHandler(CoverLib.IMaterialHandler handler) {
      for(int i = 0; i < 256; ++i) {
         if (materials[i] != null) {
            handler.addMaterial(i);
         }
      }

      materialHandlers.add(handler);
   }

   public static Integer getMaterial(ItemStack ist) {
      List<Integer> cvr = (List)coverIndex.get(ist.getItem());
      int meta = ist.getItemDamage();
      if (cvr == null) {
         return null;
      } else {
         return meta >= cvr.size() ? null : (Integer)cvr.get(meta);
      }
   }

   public static void addMaterial(int n, int hard, Block bl, String name) {
      addMaterial(n, hard, false, bl, 0, name);
   }

   public static void addMaterial(int n, int hard, Block bl, int md, String name) {
      addMaterial(n, hard, false, bl, md, name);
   }

   public static void addMaterial(int n, int hard, boolean tpar, Block bl, String name) {
      addMaterial(n, hard, tpar, bl, 0, name);
   }

   public static void addMaterial(int n, int hard, boolean tpar, Block bl, int md, String name) {
      ItemStack ist = new ItemStack(bl, 1, md);
      if (FMLCommonHandler.instance().getSide().isClient()) {
         CoverRenderer.coverIcons[n] = new IIcon[6];

         for(int i = 0; i < 6; ++i) {
            CoverRenderer.coverIcons[n][i] = bl.getIcon(i, md);
         }
      }

      if (bl instanceof IBlockHardness) {
         miningHardness[n] = ((IBlockHardness)bl).getPrototypicalHardness(md);
      } else {
         miningHardness[n] = bl.getBlockHardness(null, 0, 0, 0);
      }

      materials[n] = ist;
      names[n] = name;
      hardness[n] = hard;
      transparency[n] = tpar;
      ((List)coverIndex.computeIfAbsent(ist.getItem(), ix -> new ArrayList())).add(md, n);

      for(CoverLib.IMaterialHandler imh : materialHandlers) {
         imh.addMaterial(n);
      }

   }

   public static int damageToCoverData(int dmg) {
      int hd = dmg >> 8;
      int cn = dmg & 0xFF;
      switch(hd) {
         case 0:
            cn |= 65536;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            break;
         case 16:
            cn |= 131328;
            break;
         case 17:
            cn |= 262656;
            break;
         case 18:
            cn |= 33619968;
            break;
         case 19:
            cn |= 33685760;
            break;
         case 20:
            cn |= 33817088;
            break;
         case 21:
            cn |= 16842752;
            break;
         case 22:
            cn |= 16908544;
            break;
         case 23:
            cn |= 17039872;
            break;
         case 24:
            cn |= 1114880;
            break;
         case 25:
            cn |= 1180672;
            break;
         case 26:
            cn |= 1312000;
            break;
         case 27:
            cn |= 198144;
            break;
         case 28:
            cn |= 329472;
            break;
         case 29:
            cn |= 395264;
            break;
         case 30:
            cn |= 461056;
            break;
         case 31:
            cn |= 1247744;
            break;
         case 32:
            cn |= 1379072;
            break;
         case 33:
            cn |= 1444864;
            break;
         case 34:
            cn |= 1510656;
            break;
         case 35:
            cn |= 33751808;
            break;
         case 36:
            cn |= 33883136;
            break;
         case 37:
            cn |= 33948928;
            break;
         case 38:
            cn |= 34014720;
            break;
         case 39:
            cn |= 16974592;
            break;
         case 40:
            cn |= 17105920;
            break;
         case 41:
            cn |= 17171712;
            break;
         case 42:
            cn |= 17237504;
            break;
         case 43:
            cn |= 50462720;
            break;
         case 44:
            cn |= 50594048;
            break;
         case 45:
            cn |= 50725376;
      }

      return cn;
   }

   public static int damageToCoverValue(int dmg) {
      return damageToCoverData(dmg) & 65535;
   }

   public static int coverValueToDamage(int side, int cov) {
      int hd = cov >> 8;
      int cn = cov & 0xFF;
      if (side < 6) {
         switch(hd) {
            case 1:
               cn |= 4096;
               break;
            case 2:
               cn |= 4352;
               break;
            case 3:
               cn |= 6144;
               break;
            case 4:
               cn |= 6400;
               break;
            case 5:
               cn |= 6656;
               break;
            case 6:
               cn |= 6912;
               break;
            case 7:
               cn |= 7168;
               break;
            case 8:
               cn |= 7424;
               break;
            case 9:
               cn |= 7680;
               break;
            case 10:
               cn |= 7936;
               break;
            case 11:
               cn |= 8192;
               break;
            case 12:
               cn |= 8448;
               break;
            case 13:
               cn |= 8704;
         }
      } else if (side < 14) {
         switch(hd) {
            case 0:
               cn |= 4608;
               break;
            case 1:
               cn |= 4864;
               break;
            case 2:
               cn |= 5120;
               break;
            case 3:
               cn |= 8960;
               break;
            case 4:
               cn |= 9216;
               break;
            case 5:
               cn |= 9472;
               break;
            case 6:
               cn |= 9728;
         }
      } else if (side < 26) {
         switch(hd) {
            case 0:
               cn |= 5376;
               break;
            case 1:
               cn |= 5632;
               break;
            case 2:
               cn |= 5888;
               break;
            case 3:
               cn |= 9984;
               break;
            case 4:
               cn |= 10240;
               break;
            case 5:
               cn |= 10496;
               break;
            case 6:
               cn |= 10752;
         }
      } else if (side < 29) {
         switch(hd) {
            case 0:
               cn |= 11008;
               break;
            case 1:
               cn |= 11264;
               break;
            case 2:
               cn |= 11520;
         }
      }

      return cn;
   }

   public static ItemStack convertCoverPlate(int side, int cov) {
      return blockCoverPlate == null ? null : new ItemStack(blockCoverPlate, 1, coverValueToDamage(side, cov));
   }

   public static int cornerToCoverMask(int cn) {
      switch(cn) {
         case 0:
            return 21;
         case 1:
            return 25;
         case 2:
            return 37;
         case 3:
            return 41;
         case 4:
            return 22;
         case 5:
            return 26;
         case 6:
            return 38;
         default:
            return 42;
      }
   }

   public static int coverToCornerMask(int cn) {
      switch(cn) {
         case 0:
            return 15;
         case 1:
            return 240;
         case 2:
            return 85;
         case 3:
            return 170;
         case 4:
            return 51;
         default:
            return 204;
      }
   }

   public static int coverToStripMask(int cn) {
      switch(cn) {
         case 0:
            return 15;
         case 1:
            return 3840;
         case 2:
            return 337;
         case 3:
            return 674;
         case 4:
            return 1076;
         default:
            return 2248;
      }
   }

   public static int stripToCornerMask(int sn) {
      switch(sn) {
         case 0:
            return 5;
         case 1:
            return 10;
         case 2:
            return 3;
         case 3:
            return 12;
         case 4:
            return 17;
         case 5:
            return 34;
         case 6:
            return 68;
         case 7:
            return 136;
         case 8:
            return 80;
         case 9:
            return 160;
         case 10:
            return 48;
         default:
            return 192;
      }
   }

   public static int stripToCoverMask(int sn) {
      switch(sn) {
         case 0:
            return 5;
         case 1:
            return 9;
         case 2:
            return 17;
         case 3:
            return 33;
         case 4:
            return 20;
         case 5:
            return 24;
         case 6:
            return 36;
         case 7:
            return 40;
         case 8:
            return 6;
         case 9:
            return 10;
         case 10:
            return 18;
         default:
            return 34;
      }
   }

   public static float getThickness(int side, int cov) {
      if (side < 6) {
         switch(cov >> 8) {
            case 0:
               return 0.125F;
            case 1:
               return 0.25F;
            case 2:
               return 0.5F;
            case 3:
               return 0.125F;
            case 4:
               return 0.25F;
            case 5:
               return 0.5F;
            case 6:
               return 0.375F;
            case 7:
               return 0.625F;
            case 8:
               return 0.75F;
            case 9:
               return 0.875F;
            case 10:
               return 0.375F;
            case 11:
               return 0.625F;
            case 12:
               return 0.75F;
            case 13:
               return 0.875F;
            default:
               return 1.0F;
         }
      } else {
         if (side >= 26 && side < 29) {
            switch(cov >> 8) {
               case 0:
                  return 0.125F;
               case 1:
                  return 0.25F;
               case 2:
                  return 0.375F;
            }
         }

         switch(cov >> 8) {
            case 0:
               return 0.125F;
            case 1:
               return 0.25F;
            case 2:
               return 0.5F;
            case 3:
               return 0.375F;
            case 4:
               return 0.625F;
            case 5:
               return 0.75F;
            case 6:
               return 0.875F;
            default:
               return 1.0F;
         }
      }
   }

   public static int getThicknessQuanta(int side, int cov) {
      if (side < 6) {
         switch(cov >> 8) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 4;
            case 3:
               return 1;
            case 4:
               return 2;
            case 5:
               return 4;
            case 6:
               return 3;
            case 7:
               return 5;
            case 8:
               return 6;
            case 9:
               return 7;
            case 10:
               return 3;
            case 11:
               return 5;
            case 12:
               return 6;
            case 13:
               return 7;
            default:
               return 0;
         }
      } else {
         if (side >= 26 && side < 29) {
            switch(cov >> 8) {
               case 0:
                  return 1;
               case 1:
                  return 2;
               case 2:
                  return 3;
            }
         }

         switch(cov >> 8) {
            case 0:
               return 1;
            case 1:
               return 2;
            case 2:
               return 4;
            case 3:
               return 3;
            case 4:
               return 5;
            case 5:
               return 6;
            case 6:
               return 7;
            default:
               return 0;
         }
      }
   }

   public static boolean checkPlacement(int covm, short[] covs, int cons, boolean jacket) {
      CoverLib.PlacementValidator pv = new CoverLib.PlacementValidator(covm, covs);
      return pv.checkPlacement(cons, jacket);
   }

   private static boolean canAddCover(World world, MovingObjectPosition mop, int item) {
      if (world.canPlaceEntityOnSide(blockCoverPlate, mop.blockX, mop.blockY, mop.blockZ, false, mop.sideHit, null, null)) {
         return true;
      } else {
         ICoverable icv = CoreLib.getTileEntity(world, mop.blockX, mop.blockY, mop.blockZ, ICoverable.class);
         return icv != null && icv.canAddCover(mop.subHit, item);
      }
   }

   public static int extractCoverSide(MovingObjectPosition src) {
      byte tr = 0;
      double rpx = src.hitVec.xCoord - (double)src.blockX - 0.5;
      double rpy = src.hitVec.yCoord - (double)src.blockY - 0.5;
      double rpz = src.hitVec.zCoord - (double)src.blockZ - 0.5;
      float sbw = 0.25F;
      switch(src.sideHit) {
         case 0:
         case 1:
            if (rpz > (double)(-sbw) && rpz < (double)sbw && rpx > (double)(-sbw) && rpx < (double)sbw) {
               return src.sideHit;
            } else if (rpz > rpx) {
               if (rpz > -rpx) {
                  return 3;
               }

               return 4;
            } else {
               if (rpz > -rpx) {
                  return 5;
               }

               return 2;
            }
         case 2:
         case 3:
            if (rpy > (double)(-sbw) && rpy < (double)sbw && rpx > (double)(-sbw) && rpx < (double)sbw) {
               return src.sideHit;
            } else if (rpy > rpx) {
               if (rpy > -rpx) {
                  return 1;
               }

               return 4;
            } else {
               if (rpy > -rpx) {
                  return 5;
               }

               return 0;
            }
         case 4:
         case 5:
            if (rpy > (double)(-sbw) && rpy < (double)sbw && rpz > (double)(-sbw) && rpz < (double)sbw) {
               return src.sideHit;
            } else if (rpy > rpz) {
               if (rpy > -rpz) {
                  return 1;
               }

               return 2;
            } else {
               if (rpy > -rpz) {
                  return 3;
               }

               return 0;
            }
         default:
            return tr;
      }
   }

   public static int extractCoverAxis(MovingObjectPosition src) {
      switch(src.sideHit) {
         case 0:
            return 0;
         case 1:
            return src.hitVec.yCoord - (double)src.blockY > 0.5 ? 1 : 0;
         case 2:
            return 0;
         case 3:
            return src.hitVec.zCoord - (double)src.blockZ > 0.5 ? 1 : 0;
         default:
            return src.hitVec.xCoord - (double)src.blockX > 0.5 ? 1 : 0;
      }
   }

   private static void stepDir(MovingObjectPosition mop) {
      switch(mop.sideHit) {
         case 0:
            --mop.blockY;
            break;
         case 1:
            ++mop.blockY;
            break;
         case 2:
            --mop.blockZ;
            break;
         case 3:
            ++mop.blockZ;
            break;
         case 4:
            --mop.blockX;
            break;
         default:
            ++mop.blockX;
      }

   }

   private static boolean isClickOutside(MovingObjectPosition mop) {
      if (mop.subHit < 0) {
         return true;
      } else if (mop.subHit < 6) {
         return mop.sideHit != (mop.subHit ^ 1);
      } else if (mop.subHit < 14) {
         int fc = mop.subHit - 6;
         fc = fc >> 2 | (fc & 3) << 1;
         return ((mop.sideHit ^ fc >> (mop.sideHit >> 1)) & 1) == 0;
      } else if (mop.subHit < 26) {
         int fc = mop.subHit - 14;
         fc = stripToCoverMask(fc);
         return (fc & 1 << (mop.sideHit ^ 1)) <= 0;
      } else {
         return mop.subHit < 29 || mop.subHit == 29;
      }
   }

   public static MovingObjectPosition getPlacement(World world, MovingObjectPosition src, int item) {
      MovingObjectPosition tr = new MovingObjectPosition(src.blockX, src.blockY, src.blockZ, src.sideHit, src.hitVec);
      int cval = damageToCoverValue(item);
      int dir;
      switch(item >> 8) {
         case 0:
         case 16:
         case 17:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            dir = extractCoverSide(src);
            if (dir != tr.sideHit) {
               tr.subHit = dir;
               if (!isClickOutside(src) && canAddCover(world, tr, cval)) {
                  return tr;
               } else {
                  stepDir(tr);
                  if (canAddCover(world, tr, cval)) {
                     return tr;
                  }

                  return null;
               }
            } else {
               if (!isClickOutside(src)) {
                  tr.subHit = dir ^ 1;
                  if (canAddCover(world, tr, cval)) {
                     return tr;
                  }
               }

               tr.subHit = dir;
               if (canAddCover(world, tr, cval)) {
                  return tr;
               } else if (!isClickOutside(src)) {
                  return null;
               } else {
                  stepDir(tr);
                  tr.subHit = dir ^ 1;
                  if (canAddCover(world, tr, cval)) {
                     return tr;
                  }

                  return null;
               }
            }
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            return null;
         case 18:
         case 19:
         case 20:
         case 35:
         case 36:
         case 37:
         case 38:
            double rpx = src.hitVec.xCoord - (double)src.blockX;
            double rpy = src.hitVec.yCoord - (double)src.blockY;
            double rpz = src.hitVec.zCoord - (double)src.blockZ;
            dir = 0;
            if (rpz > 0.5) {
               ++dir;
            }

            if (rpx > 0.5) {
               dir += 2;
            }

            if (rpy > 0.5) {
               dir += 4;
            }

            switch(src.sideHit) {
               case 0:
                  dir &= 3;
                  break;
               case 1:
                  dir |= 4;
                  break;
               case 2:
                  dir &= 6;
                  break;
               case 3:
                  dir |= 1;
                  break;
               case 4:
                  dir &= 5;
                  break;
               default:
                  dir |= 2;
            }

            int dir2;
            switch(src.sideHit) {
               case 0:
               case 1:
                  dir2 = dir ^ 4;
                  break;
               case 2:
               case 3:
                  dir2 = dir ^ 1;
                  break;
               default:
                  dir2 = dir ^ 2;
            }

            if (isClickOutside(src)) {
               tr.subHit = dir2 + 6;
               stepDir(tr);
               if (canAddCover(world, tr, cval)) {
                  return tr;
               }

               return null;
            } else {
               tr.subHit = dir2 + 6;
               if (canAddCover(world, tr, cval)) {
                  return tr;
               } else {
                  tr.subHit = dir + 6;
                  if (canAddCover(world, tr, cval)) {
                     return tr;
                  }

                  return null;
               }
            }
         case 21:
         case 22:
         case 23:
         case 39:
         case 40:
         case 41:
         case 42:
            dir = extractCoverSide(src);
            if (dir == tr.sideHit) {
               return null;
            } else {
               int csm = coverToStripMask(dir);
               if (!isClickOutside(src)) {
                  int csm2 = csm & coverToStripMask(tr.sideHit ^ 1);
                  tr.subHit = 14 + Integer.numberOfTrailingZeros(csm2);
                  if (canAddCover(world, tr, cval)) {
                     return tr;
                  } else {
                     csm2 = csm & coverToStripMask(tr.sideHit);
                     tr.subHit = 14 + Integer.numberOfTrailingZeros(csm2);
                     if (canAddCover(world, tr, cval)) {
                        return tr;
                     }

                     return null;
                  }
               } else {
                  stepDir(tr);
                  int csm2 = csm & coverToStripMask(tr.sideHit ^ 1);
                  tr.subHit = 14 + Integer.numberOfTrailingZeros(csm2);
                  if (canAddCover(world, tr, cval)) {
                     return tr;
                  }

                  return null;
               }
            }
         case 43:
         case 44:
         case 45:
            dir = extractCoverSide(src);
            if (dir != tr.sideHit && dir != (tr.sideHit ^ 1)) {
               return null;
            } else {
               if (isClickOutside(src)) {
                  stepDir(tr);
               }

               tr.subHit = (dir >> 1) + 26;
               return canAddCover(world, tr, cval) ? tr : null;
            }
      }
   }

   public static void replaceWithCovers(World world, int x, int y, int z, int sides, short[] covers) {
      if (blockCoverPlate != null && sides != 0) {
         world.setBlock(x, y, z, blockCoverPlate, 0, 3);
         TileCovered tc = CoreLib.getTileEntity(world, x, y, z, TileCovered.class);
         if (tc != null) {
            tc.CoverSides = sides;
            tc.Covers = covers;
            RedPowerLib.updateIndirectNeighbors(world, x, y, z, blockCoverPlate);
            tc.updateBlock();
         }
      }

   }

   public static boolean tryMakeCompatible(World world, WorldCoord wc, Block bid, int dmg) {
      TileCovered tc = CoreLib.getTileEntity(world, wc, TileCovered.class);
      if (tc == null) {
         return false;
      } else {
         int hb = dmg >> 8;
         int lb = dmg & 0xFF;
         int xid = tc.getExtendedID();
         if (xid == hb) {
            return tc.getExtendedMetadata() == lb;
         } else if (xid != 0) {
            return false;
         } else {
            short[] covs = tc.Covers;
            int cs = tc.CoverSides;
            if (!world.setBlock(wc.x, wc.y, wc.z, bid, hb, 3)) {
               return false;
            } else {
               tc = CoreLib.getTileEntity(world, wc, TileCovered.class);
               if (tc == null) {
                  return true;
               } else {
                  tc.Covers = covs;
                  tc.CoverSides = cs;
                  tc.setExtendedMetadata(lb);
                  return true;
               }
            }
         }
      }
   }

   public static ItemStack getItemStack(int n) {
      return materials[n];
   }

   public static Block getBlock(int n) {
      ItemStack ist = materials[n];
      return Block.getBlockFromItem(ist.getItem());
   }

   public static int getMeta(int n) {
      ItemStack ist = materials[n];
      return ist.getItemDamage();
   }

   public static String getName(int n) {
      return names[n];
   }

   public static int getHardness(int n) {
      return hardness[n];
   }

   public static float getMiningHardness(int n) {
      return miningHardness[n];
   }

   public static boolean isTransparent(int n) {
      return transparency[n];
   }

   public interface IMaterialHandler {
      void addMaterial(int var1);
   }

   private static class PlacementValidator {
      public int sidemask = 0;
      public int cornermask = 0;
      public int fillcornermask = 0;
      public int hollowcornermask = 0;
      public int thickfaces = 0;
      public int covm;
      public short[] covs;
      public int[] quanta = new int[29];

      public PlacementValidator(int cm, short[] cs) {
         this.covm = cm;
         this.covs = cs;
      }

      public boolean checkThickFace(int type) {
         for(int i = 0; i < 6; ++i) {
            if ((this.covm & 1 << i) != 0 && this.covs[i] >> 8 == type) {
               int t = CoverLib.coverToCornerMask(i);
               if ((this.fillcornermask & t) > 0) {
                  return false;
               }

               this.fillcornermask |= t;
               this.sidemask |= CoverLib.coverToStripMask(i);
            }
         }

         return true;
      }

      public boolean checkThickSide(int type) {
         for(int i = 0; i < 12; ++i) {
            if ((this.covm & 1 << i + 14) != 0 && this.covs[i + 14] >> 8 == type) {
               int t = CoverLib.stripToCornerMask(i);
               if ((this.fillcornermask & t) > 0) {
                  return false;
               }

               this.fillcornermask |= t;
               this.sidemask |= 1 << i;
            }
         }

         return true;
      }

      public boolean checkThickCorner(int type) {
         for(int i = 0; i < 8; ++i) {
            if ((this.covm & 1 << i + 6) != 0 && this.covs[i + 6] >> 8 == type) {
               int t = 1 << i;
               if ((this.fillcornermask & t) == t) {
                  return false;
               }

               this.fillcornermask |= t;
            }
         }

         return true;
      }

      public boolean checkFace(int type) {
         for(int i = 0; i < 6; ++i) {
            if ((this.covm & 1 << i) != 0 && this.covs[i] >> 8 == type) {
               int t = CoverLib.coverToCornerMask(i);
               if ((this.fillcornermask & t) == t) {
                  return false;
               }

               this.cornermask |= t;
               this.sidemask |= CoverLib.coverToStripMask(i);
            }
         }

         return true;
      }

      public boolean checkSide(int type) {
         for(int i = 0; i < 12; ++i) {
            if ((this.covm & 1 << i + 14) != 0 && this.covs[i + 14] >> 8 == type) {
               int t = CoverLib.stripToCornerMask(i);
               if ((this.fillcornermask & t) == t) {
                  return false;
               }

               if ((this.sidemask & 1 << i) > 0) {
                  return false;
               }

               this.cornermask |= t;
               this.sidemask |= 1 << i;
            }
         }

         return true;
      }

      public boolean checkCorner(int type) {
         for(int i = 0; i < 8; ++i) {
            if ((this.covm & 1 << i + 6) != 0 && this.covs[i + 6] >> 8 == type) {
               int t = 1 << i;
               if ((this.cornermask & t) == t) {
                  return false;
               }

               this.cornermask |= t;
            }
         }

         return true;
      }

      public boolean checkHollow(int type) {
         for(int i = 0; i < 6; ++i) {
            if ((this.covm & 1 << i) != 0 && this.covs[i] >> 8 == type) {
               int t = CoverLib.coverToCornerMask(i);
               if ((this.cornermask & t) > 0) {
                  return false;
               }

               this.cornermask |= t;
               this.hollowcornermask |= t;
               t = CoverLib.coverToStripMask(i);
               if ((this.sidemask & t) > 0) {
                  return false;
               }

               this.sidemask |= t;
            }
         }

         return true;
      }

      public boolean checkHollowCover(int type) {
         int ocm = 0;
         int osm = 0;

         for(int i = 0; i < 6; ++i) {
            if ((this.covm & 1 << i) != 0 && this.covs[i] >> 8 == type) {
               int t = CoverLib.coverToCornerMask(i);
               if ((this.cornermask & t) > 0) {
                  return false;
               }

               ocm |= t;
               t = CoverLib.coverToStripMask(i);
               if ((this.sidemask & t) > 0) {
                  return false;
               }

               osm |= t;
            }
         }

         this.cornermask |= ocm;
         this.sidemask |= osm;
         return true;
      }

      public void calcQuanta() {
         for(int i = 0; i < 29; ++i) {
            if ((this.covm & 1 << i) == 0) {
               this.quanta[i] = 0;
            } else {
               this.quanta[i] = CoverLib.getThicknessQuanta(i, this.covs[i]);
            }
         }

      }

      private boolean checkOverlap(int a, int b, int c, int d) {
         a = this.quanta[a];
         b = this.quanta[b];
         c = this.quanta[c];
         d = this.quanta[d];
         return a + b > 8 || a + c > 8 || a + d > 8 || b + c > 8 || b + d > 8 || c + d > 8;
      }

      public boolean checkImpingement() {
         for(int i = 0; i < 6; i += 2) {
            if (this.quanta[i] + this.quanta[i + 1] > 8) {
               return false;
            }
         }

         if (this.checkOverlap(14, 15, 22, 23)) {
            return false;
         } else if (this.checkOverlap(16, 17, 24, 25)) {
            return false;
         } else if (this.checkOverlap(18, 19, 20, 22)) {
            return false;
         } else if (this.checkOverlap(6, 7, 8, 9)) {
            return false;
         } else if (this.checkOverlap(10, 11, 12, 13)) {
            return false;
         } else if (this.checkOverlap(6, 8, 10, 12)) {
            return false;
         } else if (this.checkOverlap(7, 9, 11, 13)) {
            return false;
         } else if (this.checkOverlap(6, 7, 10, 11)) {
            return false;
         } else if (this.checkOverlap(8, 9, 12, 13)) {
            return false;
         } else {
            for(int var8 = 0; var8 < 6; ++var8) {
               int q1 = this.quanta[var8];
               if (q1 != 0) {
                  int j = CoverLib.coverToCornerMask(var8);
                  int q2 = CoverLib.coverToStripMask(var8);
                  int q21 = CoverLib.coverToStripMask(var8 ^ 1);

                  for(int j1 = 0; j1 < 8; ++j1) {
                     int q22 = this.quanta[6 + j1];
                     if ((j & 1 << j1) == 0) {
                        if (q1 + q22 > 8) {
                           return false;
                        }
                     } else if (q22 > 0 && q22 < q1) {
                        return false;
                     }
                  }

                  for(int var21 = 0; var21 < 12; ++var21) {
                     int q22 = this.quanta[14 + var21];
                     if ((q21 & 1 << var21) > 0) {
                        if (q1 + q22 > 8) {
                           return false;
                        }
                     } else if ((q2 & 1 << var21) > 0 && q22 > 0 && q22 < q1) {
                        return false;
                     }
                  }
               }
            }

            for(int var9 = 0; var9 < 12; ++var9) {
               int q1 = this.quanta[14 + var9];
               if (q1 != 0) {
                  int j = CoverLib.stripToCornerMask(var9);

                  for(int q2 = 0; q2 < 8; ++q2) {
                     int q21 = this.quanta[6 + q2];
                     if ((j & 1 << q2) == 0) {
                        if (q1 + q21 > 8) {
                           return false;
                        }
                     } else if (q21 > 0 && q21 < q1) {
                        return false;
                     }
                  }
               }
            }

            for(int var10 = 0; var10 < 3; ++var10) {
               int q1 = this.quanta[26 + var10];
               if (q1 != 0) {
                  for(int j = 0; j < 8; ++j) {
                     int q2 = this.quanta[6 + j];
                     if (q1 + q2 > 4) {
                        return false;
                     }
                  }

                  for(int var15 = 0; var15 < 12; ++var15) {
                     int q2 = this.quanta[14 + var15];
                     if (q1 + q2 > 4) {
                        return false;
                     }
                  }

                  for(int var16 = 0; var16 < 6; ++var16) {
                     if (var16 >> 1 != var10 && this.quanta[var16] + q1 > 4) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      }

      public boolean checkPlacement(int cons, boolean jacket) {
         this.calcQuanta();
         if (!this.checkImpingement()) {
            return false;
         } else if (!this.checkThickFace(9)) {
            return false;
         } else if (!this.checkThickSide(6)) {
            return false;
         } else if (!this.checkThickCorner(6)) {
            return false;
         } else if (!this.checkThickFace(8)) {
            return false;
         } else if (!this.checkThickSide(5)) {
            return false;
         } else if (!this.checkThickCorner(5)) {
            return false;
         } else if (!this.checkThickFace(7)) {
            return false;
         } else if (!this.checkThickSide(4)) {
            return false;
         } else if (!this.checkThickCorner(4)) {
            return false;
         } else if (this.cornermask > 0 && cons > 0) {
            return false;
         } else if (!this.checkThickFace(2)) {
            return false;
         } else if (!this.checkThickSide(2)) {
            return false;
         } else if (!this.checkThickCorner(2)) {
            return false;
         } else {
            this.cornermask = this.fillcornermask;
            if (!this.checkFace(6)) {
               return false;
            } else if (!this.checkSide(3)) {
               return false;
            } else if (!this.checkCorner(3)) {
               return false;
            } else {
               if ((this.covm & 469762048) > 0) {
                  if (jacket) {
                     return false;
                  }

                  if (cons > 0) {
                     return false;
                  }
               }

               for(int i = 0; i < 6; ++i) {
                  if ((cons & 1 << i) != 0 && (this.cornermask & CoverLib.coverToCornerMask(i)) > 0) {
                     return false;
                  }
               }

               if (!this.checkFace(1)) {
                  return false;
               } else if (!this.checkSide(1)) {
                  return false;
               } else if (!this.checkCorner(1)) {
                  return false;
               } else if (!jacket || this.cornermask <= 0 && this.sidemask <= 0) {
                  if (!this.checkHollow(13)) {
                     return false;
                  } else if (!this.checkHollow(12)) {
                     return false;
                  } else if (!this.checkHollow(11)) {
                     return false;
                  } else if (!this.checkHollow(10)) {
                     return false;
                  } else if (!this.checkHollow(5)) {
                     return false;
                  } else {
                     for(int var5 = 0; var5 < 6; ++var5) {
                        if ((cons & 1 << var5) != 0 && (this.hollowcornermask & CoverLib.coverToCornerMask(var5)) > 0) {
                           return false;
                        }
                     }

                     if (!this.checkHollow(4)) {
                        return false;
                     } else if (!this.checkHollowCover(3)) {
                        return false;
                     } else if (!this.checkFace(0)) {
                        return false;
                     } else if (!this.checkSide(0)) {
                        return false;
                     } else if (!this.checkCorner(0)) {
                        return false;
                     } else {
                        for(int var6 = 0; var6 < 12; ++var6) {
                           if ((this.covm & 1 << var6 + 14) != 0) {
                              int t = CoverLib.stripToCoverMask(var6);
                              if ((cons & t) == t) {
                                 return false;
                              }
                           }
                        }

                        return true;
                     }
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }
}
