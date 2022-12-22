package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class RedPowerLib {
   private static Set<RedPowerLib.PowerClassCompat> powerClassMapping = new HashSet();
   private static Set<ChunkCoordinates> blockUpdates = new HashSet();
   private static Deque<ChunkCoordinates> powerSearch = new LinkedList();
   private static Set<ChunkCoordinates> powerSearchTest = new HashSet();
   private static boolean searching = false;

   public static void notifyBlock(World world, int x, int y, int z, Block block) {
      if (block != null) {
         world.getBlock(x, y, z).onNeighborBlockChange(world, x, y, z, block);
      }

   }

   public static void updateIndirectNeighbors(World w, int x, int y, int z, Block block) {
      if (!w.isRemote) {
         for(int a = -3; a <= 3; ++a) {
            for(int b = -3; b <= 3; ++b) {
               for(int c = -3; c <= 3; ++c) {
                  int md = a < 0 ? -a : a;
                  md += b < 0 ? -b : b;
                  md += c < 0 ? -c : c;
                  if (md <= 3) {
                     notifyBlock(w, x + a, y + b, z + c, block);
                  }
               }
            }
         }
      }

   }

   public static boolean isBlockRedstone(IBlockAccess iba, int x, int y, int z, int side) {
      switch(side) {
         case 0:
            --y;
            break;
         case 1:
            ++y;
            break;
         case 2:
            --z;
            break;
         case 3:
            ++z;
            break;
         case 4:
            --x;
            break;
         case 5:
            ++x;
      }

      return iba.getBlock(x, y, z) instanceof BlockRedstoneWire;
   }

   public static boolean isSideNormal(IBlockAccess iba, int x, int y, int z, int side) {
      switch(side) {
         case 0:
            --y;
            break;
         case 1:
            ++y;
            break;
         case 2:
            --z;
            break;
         case 3:
            ++z;
            break;
         case 4:
            --x;
            break;
         case 5:
            ++x;
      }

      side ^= 1;
      if (iba.getBlock(x, y, z).isNormalCube()) {
         return true;
      } else {
         iba.getBlock(x, y, z);
         IMultipart im = CoreLib.getTileEntity(iba, x, y, z, IMultipart.class);
         return im != null && im.isSideNormal(side);
      }
   }

   public static boolean canSupportWire(IBlockAccess iba, int i, int j, int k, int side) {
      switch(side) {
         case 0:
            --j;
            break;
         case 1:
            ++j;
            break;
         case 2:
            --k;
            break;
         case 3:
            ++k;
            break;
         case 4:
            --i;
            break;
         case 5:
            ++i;
      }

      side ^= 1;
      if (iba instanceof World) {
         World bid = (World)iba;
         if (!bid.blockExists(i, j, k)) {
            return true;
         }

         if (bid.getBlock(i, j, k).isSideSolid(bid, i, j, k, ForgeDirection.getOrientation(side))) {
            return true;
         }
      }

      if (iba.getBlock(i, j, k).isNormalCube()) {
         return true;
      } else {
         Block block = iba.getBlock(i, j, k);
         if (block == Blocks.piston_extension) {
            return true;
         } else if (block != Blocks.sticky_piston && block != Blocks.piston) {
            IMultipart mpart = CoreLib.getTileEntity(iba, i, j, k, IMultipart.class);
            return mpart != null && mpart.isSideNormal(side);
         } else {
            int im = iba.getBlockMetadata(i, j, k) & 7;
            return i != im && im != 7;
         }
      }
   }

   public static boolean isStrongPoweringTo(IBlockAccess iba, int x, int y, int z, int side) {
      Block block = iba.getBlock(x, y, z);
      if (iba.isAirBlock(x, y, z)) {
         return false;
      } else if (searching && block == Blocks.redstone_wire) {
         return false;
      } else if (!(iba instanceof World)) {
         return false;
      } else {
         World world = (World)iba;
         return block.isProvidingStrongPower(world, x, y, z, side) > 0;
      }
   }

   public static boolean isStrongPowered(IBlockAccess iba, int x, int y, int z, int side) {
      return side != 1 && isStrongPoweringTo(iba, x, y - 1, z, 0)
         || side != 0 && isStrongPoweringTo(iba, x, y + 1, z, 1)
         || side != 3 && isStrongPoweringTo(iba, x, y, z - 1, 2)
         || side != 2 && isStrongPoweringTo(iba, x, y, z + 1, 3)
         || side != 5 && isStrongPoweringTo(iba, x - 1, y, z, 4)
         || side != 4 && isStrongPoweringTo(iba, x + 1, y, z, 5);
   }

   public static boolean isWeakPoweringTo(IBlockAccess iba, int x, int y, int z, int side) {
      Block block = iba.getBlock(x, y, z);
      return block != Blocks.air
         && (!searching || block != Blocks.redstone_wire)
         && (block.isProvidingWeakPower(iba, x, y, z, side) > 0 || side > 1 && block == Blocks.redstone_wire && block.isProvidingWeakPower(iba, x, y, z, 1) > 0);
   }

   public static boolean isPoweringTo(IBlockAccess iba, int x, int y, int z, int side) {
      Block block = iba.getBlock(x, y, z);
      if (block == Blocks.air) {
         return false;
      } else if (block.isProvidingWeakPower(iba, x, y, z, side) > 0) {
         return true;
      } else if (block.isNormalCube() && isStrongPowered(iba, x, y, z, side)) {
         return true;
      } else {
         return side > 1 && block == Blocks.redstone_wire && !searching && block.isProvidingWeakPower(iba, x, y, z, 1) > 0;
      }
   }

   public static boolean isPowered(IBlockAccess iba, int x, int y, int z, int cons, int inside) {
      return (cons & 17895680) > 0 && isWeakPoweringTo(iba, x, y - 1, z, 0)
         || (cons & 35791360) > 0 && isWeakPoweringTo(iba, x, y + 1, z, 1)
         || (cons & 71565329) > 0 && isWeakPoweringTo(iba, x, y, z - 1, 2)
         || (cons & 143130658) > 0 && isWeakPoweringTo(iba, x, y, z + 1, 3)
         || (cons & 268452932) > 0 && isWeakPoweringTo(iba, x - 1, y, z, 4)
         || (cons & 536905864) > 0 && isWeakPoweringTo(iba, x + 1, y, z, 5)
         || (inside & 1) > 0 && isPoweringTo(iba, x, y - 1, z, 0)
         || (inside & 2) > 0 && isPoweringTo(iba, x, y + 1, z, 1)
         || (inside & 4) > 0 && isPoweringTo(iba, x, y, z - 1, 2)
         || (inside & 8) > 0 && isPoweringTo(iba, x, y, z + 1, 3)
         || (inside & 16) > 0 && isPoweringTo(iba, x - 1, y, z, 4)
         || (inside & 32) > 0 && isPoweringTo(iba, x + 1, y, z, 5);
   }

   private static int getSidePowerMask(IBlockAccess iba, int x, int y, int z, int ch, int side) {
      IRedPowerConnectable irp = CoreLib.getTileEntity(iba, x, y, z, IRedPowerConnectable.class);
      int mask = getConDirMask(side);
      if (irp != null) {
         int m = irp.getPoweringMask(ch);
         m = (m & 1431655765) << 1 | (m & 715827882) >> 1;
         return m & mask;
      } else if (ch != 0) {
         return 0;
      } else {
         return isWeakPoweringTo(iba, x, y, z, side) ? mask & 16777215 : (isPoweringTo(iba, x, y, z, side) ? mask : 0);
      }
   }

   public static int getPowerState(IBlockAccess iba, int i, int j, int k, int cons, int ch) {
      int trs = 0;
      if ((cons & 17895680) > 0) {
         trs |= getSidePowerMask(iba, i, j - 1, k, ch, 0);
      }

      if ((cons & 35791360) > 0) {
         trs |= getSidePowerMask(iba, i, j + 1, k, ch, 1);
      }

      if ((cons & 71565329) > 0) {
         trs |= getSidePowerMask(iba, i, j, k - 1, ch, 2);
      }

      if ((cons & 143130658) > 0) {
         trs |= getSidePowerMask(iba, i, j, k + 1, ch, 3);
      }

      if ((cons & 268452932) > 0) {
         trs |= getSidePowerMask(iba, i - 1, j, k, ch, 4);
      }

      if ((cons & 536905864) > 0) {
         trs |= getSidePowerMask(iba, i + 1, j, k, ch, 5);
      }

      return trs & cons;
   }

   public static int getRotPowerState(IBlockAccess iba, int i, int j, int k, int rcon, int rot, int ch) {
      int c1 = mapRotToCon(rcon, rot);
      int ps = getPowerState(iba, i, j, k, c1, ch);
      return mapConToRot(ps, rot);
   }

   public static int getConDirMask(int dir) {
      switch(dir) {
         case 0:
            return 17895680;
         case 1:
            return 35791360;
         case 2:
            return 71565329;
         case 3:
            return 143130658;
         case 4:
            return 268452932;
         default:
            return 536905864;
      }
   }

   public static int mapConToLocal(int cons, int face) {
      cons >>= face * 4;
      cons &= 15;
      switch(face) {
         case 0:
            return cons;
         case 1:
            return cons ^ ((cons ^ cons >> 1) & 1) * 3;
         case 2:
         default:
            return cons ^ ((cons ^ cons >> 2) & 3) * 5;
         case 3:
         case 4:
            cons ^= ((cons ^ cons >> 2) & 3) * 5;
            return cons ^ ((cons ^ cons >> 1) & 1) * 3;
      }
   }

   public static int mapLocalToCon(int loc, int face) {
      switch(face) {
         case 0:
            break;
         case 1:
            loc ^= ((loc ^ loc >> 1) & 1) * 3;
            break;
         case 2:
         default:
            loc ^= ((loc ^ loc >> 2) & 3) * 5;
            break;
         case 3:
         case 4:
            loc ^= ((loc ^ loc >> 1) & 1) * 3;
            loc ^= ((loc ^ loc >> 2) & 3) * 5;
      }

      return loc << face * 4;
   }

   public static int mapRotToLocal(int rm, int rot) {
      rm = rm << rot | rm >> 4 - rot;
      rm &= 15;
      return rm & 8 | (rm & 3) << 1 | rm >> 2 & 1;
   }

   public static int mapLocalToRot(int rm, int rot) {
      rm = rm & 8 | (rm & 6) >> 1 | rm << 2 & 4;
      rm = rm << 4 - rot | rm >> rot;
      return rm & 15;
   }

   public static int mapConToRot(int con, int rot) {
      return mapLocalToRot(mapConToLocal(con, rot >> 2), rot & 3);
   }

   public static int mapRotToCon(int con, int rot) {
      return mapLocalToCon(mapRotToLocal(con, rot & 3), rot >> 2);
   }

   public static int getDirToRedstone(int rsd) {
      switch(rsd) {
         case 2:
            return 0;
         case 3:
            return 2;
         case 4:
            return 3;
         case 5:
            return 1;
         default:
            return 0;
      }
   }

   public static int getConSides(IBlockAccess iba, int i, int j, int k, int side, int pcl) {
      Block block = iba.getBlock(i, j, k);
      if (iba.isAirBlock(i, j, k)) {
         return 0;
      } else {
         IConnectable rpa = CoreLib.getTileEntity(iba, i, j, k, IConnectable.class);
         if (rpa != null) {
            int md = rpa.getConnectClass(side);
            return isCompatible(md, pcl) ? rpa.getConnectableMask() : 0;
         } else if (!isCompatible(0, pcl)) {
            return 0;
         } else if (block == Blocks.piston || block == Blocks.sticky_piston) {
            int md = iba.getBlockMetadata(i, j, k) & 7;
            return md == 7 ? 0 : 1073741823 ^ getConDirMask(md);
         } else if (block == Blocks.piston_extension) {
            TileEntity md2 = iba.getTileEntity(i, j, k);
            if (!(md2 instanceof TileEntityPiston)) {
               return 0;
            } else {
               TileEntityPiston tep = (TileEntityPiston)md2;
               Block sid = tep.getStoredBlockID();
               if (sid != Blocks.piston && sid != Blocks.sticky_piston) {
                  return 0;
               } else {
                  int md1 = tep.getBlockMetadata() & 7;
                  return md1 == 7 ? 0 : 1073741823 ^ getConDirMask(md1);
               }
            }
         } else if (block == Blocks.dispenser || block instanceof BlockButton || block == Blocks.lever) {
            return 1073741823;
         } else if (block == Blocks.redstone_torch || block == Blocks.unlit_redstone_torch) {
            return 1073741823;
         } else if (block != Blocks.unpowered_repeater && block != Blocks.powered_repeater) {
            return block.canConnectRedstone(iba, i, j, k, getDirToRedstone(side)) ? getConDirMask(side) : 0;
         } else {
            int md = iba.getBlockMetadata(i, j, k) & 1;
            return md > 0 ? 12 : 3;
         }
      }
   }

   private static int getES1(IBlockAccess iba, int i, int j, int k, int side, int pcl, int cc) {
      if (iba.isAirBlock(i, j, k)) {
         return 0;
      } else {
         IConnectable rpa = CoreLib.getTileEntity(iba, i, j, k, IConnectable.class);
         if (rpa != null) {
            int cc2 = rpa.getCornerPowerMode();
            if (cc == 0 || cc2 == 0) {
               return 0;
            } else if (cc == 2 && cc2 == 2) {
               return 0;
            } else if (cc == 3 && cc2 == 1) {
               return 0;
            } else {
               int pc = rpa.getConnectClass(side);
               return isCompatible(pc, pcl) ? rpa.getConnectableMask() : 0;
            }
         } else {
            return 0;
         }
      }
   }

   public static int getExtConSides(IBlockAccess iba, IConnectable irp, int i, int j, int k, int dir, int cc) {
      int cons = irp.getConnectableMask();
      cons &= getConDirMask(dir) & 16777215;
      if (cons == 0) {
         return 0;
      } else {
         Block block = iba.getBlock(i, j, k);
         if (CoverLib.blockCoverPlate != null && block == CoverLib.blockCoverPlate) {
            if (iba.getBlockMetadata(i, j, k) != 0) {
               return 0;
            }

            ICoverable pcl = CoreLib.getTileEntity(iba, i, j, k, ICoverable.class);
            if (pcl == null) {
               return 0;
            }

            int isv = pcl.getCoverMask();
            if ((isv & 1 << (dir ^ 1)) > 0) {
               return 0;
            }

            isv |= isv << 12;
            isv |= isv << 6;
            isv &= 197379;
            isv |= isv << 3;
            isv &= 1118481;
            isv |= isv << 2;
            isv |= isv << 1;
            cons &= ~isv;
         } else if (!iba.isAirBlock(i, j, k) && block != Blocks.flowing_water && block != Blocks.water) {
            return 0;
         }

         int pcl1 = irp.getConnectClass(dir);
         int isv = 0;
         if ((cons & 15) > 0) {
            isv |= getES1(iba, i, j - 1, k, 1, pcl1, cc) & 2236928;
         }

         if ((cons & 240) > 0) {
            isv |= getES1(iba, i, j + 1, k, 0, pcl1, cc) & 1118464;
         }

         if ((cons & 3840) > 0) {
            isv |= getES1(iba, i, j, k - 1, 3, pcl1, cc) & 8912930;
         }

         if ((cons & 61440) > 0) {
            isv |= getES1(iba, i, j, k + 1, 2, pcl1, cc) & 4456465;
         }

         if ((cons & 983040) > 0) {
            isv |= getES1(iba, i - 1, j, k, 5, pcl1, cc) & 34952;
         }

         if ((cons & 15728640) > 0) {
            isv |= getES1(iba, i + 1, j, k, 4, pcl1, cc) & 17476;
         }

         isv >>= (dir ^ 1) << 2;
         isv = (isv & 10) >> 1 | (isv & 5) << 1;
         isv |= isv << 6;
         isv |= isv << 3;
         isv &= 4369;
         isv <<= dir & 1;
         switch(dir) {
            case 0:
            case 1:
               return isv << 8;
            case 2:
            case 3:
               return isv << 10 & 0xFF0000 | isv & 0xFF;
            default:
               return isv << 2;
         }
      }
   }

   public static int getConnections(IBlockAccess iba, IConnectable irp, int x, int y, int z) {
      int cons = irp.getConnectableMask();
      int cs = 0;
      if ((cons & 17895680) > 0) {
         int pcl = irp.getConnectClass(0);
         cs |= getConSides(iba, x, y - 1, z, 1, pcl) & 35791360;
      }

      if ((cons & 35791360) > 0) {
         int pcl = irp.getConnectClass(1);
         cs |= getConSides(iba, x, y + 1, z, 0, pcl) & 17895680;
      }

      if ((cons & 71565329) > 0) {
         int pcl = irp.getConnectClass(2);
         cs |= getConSides(iba, x, y, z - 1, 3, pcl) & 143130658;
      }

      if ((cons & 143130658) > 0) {
         int pcl = irp.getConnectClass(3);
         cs |= getConSides(iba, x, y, z + 1, 2, pcl) & 71565329;
      }

      if ((cons & 268452932) > 0) {
         int pcl = irp.getConnectClass(4);
         cs |= getConSides(iba, x - 1, y, z, 5, pcl) & 536905864;
      }

      if ((cons & 536905864) > 0) {
         int pcl = irp.getConnectClass(5);
         cs |= getConSides(iba, x + 1, y, z, 4, pcl) & 268452932;
      }

      cs = cs << 1 & 715827882 | cs >> 1 & 357913941;
      return cs & cons;
   }

   public static int getExtConnections(IBlockAccess iba, IConnectable irp, int i, int j, int k) {
      byte cs = 0;
      int cc = irp.getCornerPowerMode();
      int cs1 = cs | getExtConSides(iba, irp, i, j - 1, k, 0, cc);
      cs1 |= getExtConSides(iba, irp, i, j + 1, k, 1, cc);
      cs1 |= getExtConSides(iba, irp, i, j, k - 1, 2, cc);
      cs1 |= getExtConSides(iba, irp, i, j, k + 1, 3, cc);
      cs1 |= getExtConSides(iba, irp, i - 1, j, k, 4, cc);
      return cs1 | getExtConSides(iba, irp, i + 1, j, k, 5, cc);
   }

   public static int getExtConnectionExtras(IBlockAccess iba, IConnectable irp, int i, int j, int k) {
      byte cs = 0;
      int cs1 = cs | getExtConSides(iba, irp, i, j - 1, k, 0, 3);
      cs1 |= getExtConSides(iba, irp, i, j + 1, k, 1, 3);
      cs1 |= getExtConSides(iba, irp, i, j, k - 1, 2, 3);
      cs1 |= getExtConSides(iba, irp, i, j, k + 1, 3, 3);
      cs1 |= getExtConSides(iba, irp, i - 1, j, k, 4, 3);
      return cs1 | getExtConSides(iba, irp, i + 1, j, k, 5, 3);
   }

   public static int getTileCurrentStrength(World world, int i, int j, int k, int cons, int ch) {
      IRedPowerConnectable irp = CoreLib.getTileEntity(world, i, j, k, IRedPowerConnectable.class);
      if (irp == null) {
         return -1;
      } else if (irp instanceof IRedPowerWiring) {
         IRedPowerWiring irw = (IRedPowerWiring)irp;
         return irw.getCurrentStrength(cons, ch);
      } else {
         return (irp.getPoweringMask(ch) & cons) > 0 ? 255 : -1;
      }
   }

   public static int getTileOrRedstoneCurrentStrength(World world, int i, int j, int k, int cons, int ch) {
      Block block = world.getBlock(i, j, k);
      if (world.isAirBlock(i, j, k)) {
         return -1;
      } else if (block == Blocks.redstone_wire) {
         int irp1 = world.getBlockMetadata(i, j, k);
         return irp1 > 0 ? irp1 : -1;
      } else {
         IRedPowerConnectable irp = CoreLib.getTileEntity(world, i, j, k, IRedPowerConnectable.class);
         if (irp == null) {
            return -1;
         } else if (irp instanceof IRedPowerWiring) {
            IRedPowerWiring irw = (IRedPowerWiring)irp;
            return irw.getCurrentStrength(cons, ch);
         } else {
            return (irp.getPoweringMask(ch) & cons) > 0 ? 255 : -1;
         }
      }
   }

   private static int getIndCur(World world, int i, int j, int k, int d1, int d2, int ch) {
      int d3;
      switch(d1) {
         case 0:
            --j;
            d3 = d2 + 2;
            break;
         case 1:
            ++j;
            d3 = d2 + 2;
            break;
         case 2:
            --k;
            d3 = d2 + (d2 & 2);
            break;
         case 3:
            ++k;
            d3 = d2 + (d2 & 2);
            break;
         case 4:
            --i;
            d3 = d2;
            break;
         default:
            ++i;
            d3 = d2;
      }

      int d4;
      switch(d3) {
         case 0:
            --j;
            d4 = d1 - 2;
            break;
         case 1:
            ++j;
            d4 = d1 - 2;
            break;
         case 2:
            --k;
            d4 = d1 & 1 | (d1 & 4) >> 1;
            break;
         case 3:
            ++k;
            d4 = d1 & 1 | (d1 & 4) >> 1;
            break;
         case 4:
            --i;
            d4 = d1;
            break;
         default:
            ++i;
            d4 = d1;
      }

      return getTileCurrentStrength(world, i, j, k, 1 << (d4 ^ 1) << ((d3 ^ 1) << 2), ch);
   }

   public static int getMaxCurrentStrength(World world, int i, int j, int k, int cons, int indcon, int ch) {
      int mcs = -1;
      int ocon = cons << 1 & 715827882 | cons >> 1 & 357913941;
      if ((cons & 17895680) > 0) {
         mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j - 1, k, ocon & 35791360, ch));
      }

      if ((cons & 35791360) > 0) {
         mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j + 1, k, ocon & 17895680, ch));
      }

      if ((cons & 71565329) > 0) {
         mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j, k - 1, ocon & 143130658, ch));
      }

      if ((cons & 143130658) > 0) {
         mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i, j, k + 1, ocon & 71565329, ch));
      }

      if ((cons & 268452932) > 0) {
         mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i - 1, j, k, ocon & 536905864, ch));
      }

      if ((cons & 536905864) > 0) {
         mcs = Math.max(mcs, getTileOrRedstoneCurrentStrength(world, i + 1, j, k, ocon & 268452932, ch));
      }

      for(int a = 0; a < 6; ++a) {
         for(int b = 0; b < 4; ++b) {
            if ((indcon & 1 << a * 4 + b) > 0) {
               mcs = Math.max(mcs, getIndCur(world, i, j, k, a, b, ch));
            }
         }
      }

      return mcs;
   }

   public static void addUpdateBlock(int i, int j, int k) {
      for(int a = -3; a <= 3; ++a) {
         for(int b = -3; b <= 3; ++b) {
            for(int c = -3; c <= 3; ++c) {
               int md = a < 0 ? -a : a;
               md += b < 0 ? -b : b;
               md += c < 0 ? -c : c;
               if (md <= 3) {
                  blockUpdates.add(new ChunkCoordinates(i + a, j + b, k + c));
               }
            }
         }
      }

   }

   public static void addStartSearchBlock(int x, int y, int z) {
      ChunkCoordinates sb = new ChunkCoordinates(x, y, z);
      if (!powerSearchTest.contains(sb)) {
         powerSearch.addLast(sb);
         powerSearchTest.add(sb);
      }

   }

   public static void addSearchBlock(int x, int y, int z) {
      addStartSearchBlock(x, y, z);
      blockUpdates.add(new ChunkCoordinates(x, y, z));
   }

   private static void addIndBl(int x, int y, int z, int d1, int d2) {
      int d3;
      switch(d1) {
         case 0:
            --y;
            d3 = d2 + 2;
            break;
         case 1:
            ++y;
            d3 = d2 + 2;
            break;
         case 2:
            --z;
            d3 = d2 + (d2 & 2);
            break;
         case 3:
            ++z;
            d3 = d2 + (d2 & 2);
            break;
         case 4:
            --x;
            d3 = d2;
            break;
         default:
            ++x;
            d3 = d2;
      }

      switch(d3) {
         case 0:
            --y;
            break;
         case 1:
            ++y;
            break;
         case 2:
            --z;
            break;
         case 3:
            ++z;
            break;
         case 4:
            --x;
            break;
         case 5:
            ++x;
      }

      addSearchBlock(x, y, z);
   }

   public static void addSearchBlocks(int i, int j, int k, int cons, int indcon) {
      int ocon = cons << 1 & 11184810 | cons >> 1 & 5592405;
      if ((cons & 17895680) > 0) {
         addSearchBlock(i, j - 1, k);
      }

      if ((cons & 35791360) > 0) {
         addSearchBlock(i, j + 1, k);
      }

      if ((cons & 71565329) > 0) {
         addSearchBlock(i, j, k - 1);
      }

      if ((cons & 143130658) > 0) {
         addSearchBlock(i, j, k + 1);
      }

      if ((cons & 268452932) > 0) {
         addSearchBlock(i - 1, j, k);
      }

      if ((cons & 536905864) > 0) {
         addSearchBlock(i + 1, j, k);
      }

      for(int a = 0; a < 6; ++a) {
         for(int b = 0; b < 4; ++b) {
            if ((indcon & 1 << a * 4 + b) > 0) {
               addIndBl(i, j, k, a, b);
            }
         }
      }

   }

   public static void updateCurrent(World world, int x, int y, int z) {
      addStartSearchBlock(x, y, z);
      if (!searching) {
         searching = true;

         while(powerSearch.size() > 0) {
            ChunkCoordinates c = (ChunkCoordinates)powerSearch.removeFirst();
            powerSearchTest.remove(c);
            IRedPowerWiring sp = CoreLib.getTileEntity(world, c.posX, c.posY, c.posZ, IRedPowerWiring.class);
            if (sp != null) {
               sp.updateCurrentStrength();
            }
         }

         searching = false;
         List<ChunkCoordinates> coords = new ArrayList(blockUpdates);
         blockUpdates.clear();

         for(ChunkCoordinates c : coords) {
            notifyBlock(world, c.posX, c.posY, c.posZ, Blocks.redstone_wire);
            world.markBlockForUpdate(c.posX, c.posY, c.posZ);
         }
      }

   }

   public static int updateBlockCurrentStrength(World world, IRedPowerWiring irp, int x, int y, int z, int conm, int chm) {
      int cons = irp.getConnectionMask() & conm;
      int indcon = irp.getExtConnectionMask() & conm;
      int mx = -1;
      int ps = 0;
      int cs = 0;

      int ch;
      for(int chm2 = chm; chm2 > 0; ps = Math.max(ps, irp.scanPoweringStrength(cons | indcon, ch))) {
         ch = Integer.numberOfTrailingZeros(chm2);
         chm2 &= ~(1 << ch);
         cs = Math.max(cs, irp.getCurrentStrength(conm, ch));
         mx = Math.max(mx, getMaxCurrentStrength(world, x, y, z, cons, indcon, ch));
      }

      if (ps > cs || mx != cs + 1 && (cs != 0 || mx != 0)) {
         if (ps == cs && mx <= cs) {
            return cs;
         } else {
            cs = Math.max(ps, cs);
            if (cs >= mx) {
               if (cs > ps) {
                  cs = 0;
               }
            } else {
               cs = Math.max(0, mx - 1);
            }

            if ((chm & 1) > 0) {
               addUpdateBlock(x, y, z);
            }

            addSearchBlocks(x, y, z, cons, indcon);
            return cs;
         }
      } else {
         return cs;
      }
   }

   public static boolean isSearching() {
      return searching;
   }

   public static void addCompatibleMapping(int a, int b) {
      powerClassMapping.add(new RedPowerLib.PowerClassCompat(a, b));
      powerClassMapping.add(new RedPowerLib.PowerClassCompat(b, a));
   }

   public static boolean isCompatible(int a, int b) {
      return a == b || powerClassMapping.contains(new RedPowerLib.PowerClassCompat(a, b));
   }

   public static class PowerClassCompat {
      private final int a;
      private final int b;

      public PowerClassCompat(int a, int b) {
         this.a = a;
         this.b = b;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            RedPowerLib.PowerClassCompat that = (RedPowerLib.PowerClassCompat)o;
            return this.a == that.a && this.b == that.b;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.a;
         return 31 * result + this.b;
      }
   }
}
