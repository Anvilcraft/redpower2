package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCustomLeaves extends BlockLeaves {
   private String opaque;
   private IIcon opaqueIcon;
   private String transparent;
   private IIcon transparentIcon;

   public BlockCustomLeaves(String opaque, String transparent) {
      this.opaque = opaque;
      this.transparent = transparent;
      this.setTickRandomly(true);
      this.setHardness(0.2F);
      this.setStepSound(Block.soundTypeGrass);
      this.setLightOpacity(1);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister registerer) {
      this.opaqueIcon = registerer.registerIcon(this.opaque);
      this.transparentIcon = registerer.registerIcon(this.transparent);
   }

   public boolean isOpaqueCube() {
      super.field_150121_P = !Blocks.leaves.isOpaqueCube();
      return !super.field_150121_P;
   }

   public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
      super.field_150121_P = !Blocks.leaves.isOpaqueCube();
      return super.shouldSideBeRendered(iblockaccess, i, j, k, l);
   }

   public IIcon getIcon(int i, int j) {
      super.field_150121_P = !Blocks.leaves.isOpaqueCube();
      return super.field_150121_P ? this.transparentIcon : this.opaqueIcon;
   }

   public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
      if (world.getBlock(x, y, z) != this) {
         updateLeaves(world, x, y, z, 1);
      }

   }

   public static void updateLeaves(World world, int x, int y, int z, int radius) {
      if (world.checkChunksExist(x - radius - 1, y - radius - 1, z - radius - 1, x + radius + 1, y + radius + 1, z + radius + 1)) {
         for(int dx = -radius; dx <= radius; ++dx) {
            for(int dy = -radius; dy <= radius; ++dy) {
               for(int dz = -radius; dz <= radius; ++dz) {
                  if (world.getBlock(x + dx, y + dy, z + dz) == RedPowerWorld.blockLeaves) {
                     int md = world.getBlockMetadata(x + dx, y + dy, z + dz);
                     world.setBlock(x + dx, y + dy, z + dz, world.getBlock(x + dx, y + dy, z + dz), md | 8, 3);
                  }
               }
            }
         }
      }

   }

   public void updateTick(World world, int x, int y, int z, Random random) {
      if (!world.isRemote) {
         int md = world.getBlockMetadata(x, y, z);
         if ((md & 8) != 0 && (md & 4) <= 0) {
            HashMap<WorldCoord, Integer> wch = new HashMap();
            LinkedList<WorldCoord> fifo = new LinkedList();
            WorldCoord wc = new WorldCoord(x, y, z);
            WorldCoord wcp = wc.copy();
            fifo.addLast(wc);
            wch.put(wc, 4);

            while(fifo.size() > 0) {
               wc = (WorldCoord)fifo.removeFirst();
               Integer stp = (Integer)wch.get(wc);
               if (stp != null) {
                  for(int n = 0; n < 6; ++n) {
                     wcp.set(wc);
                     wcp.step(n);
                     if (!wch.containsKey(wcp)) {
                        Block block = world.getBlock(wcp.x, wcp.y, wcp.z);
                        if (block == RedPowerWorld.blockLogs) {
                           world.setBlock(x, y, z, RedPowerWorld.blockLeaves, md & -9, 3);
                           return;
                        }

                        if (stp != 0 && block == this) {
                           wch.put(wcp, stp - 1);
                           fifo.addLast(wcp);
                        }
                     }
                  }
               }
            }

            this.dropBlockAsItem(world, x, y, z, md, 0);
            world.setBlockToAir(x, y, z);
         }
      }

   }

   public Item getItemDropped(int i, Random random, int j) {
      return Item.getItemFromBlock(RedPowerWorld.blockPlants);
   }

   public int quantityDropped(int i, int fortune, Random random) {
      return random.nextInt(20) != 0 ? 0 : 1;
   }

   public int damageDropped(int i) {
      return 1;
   }

   public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
      return true;
   }

   public String[] func_150125_e() {
      return new String[]{this.getUnlocalizedName()};
   }
}
