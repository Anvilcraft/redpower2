package com.eloraam.redpower.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockCustomFlower extends BlockFlower implements IGrowable {
   public String[] names = new String[2];
   public IIcon[] icons = new IIcon[2];

   public BlockCustomFlower(String... names) {
      super(0);
      this.names = names;
      this.setHardness(0.0F);
      this.setStepSound(Block.soundTypeGrass);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister registerer) {
      for(int i = 0; i < 2; ++i) {
         this.icons[i] = registerer.registerIcon(this.names[i]);
      }

   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(int side, int meta) {
      return this.icons[meta % this.icons.length];
   }

   public void updateTick(World world, int x, int y, int z, Random random) {
      int md = world.getBlockMetadata(x, y, z);
      if (!world.isRemote && (md == 1 || md == 2) && world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(300) == 0) {
         if (md == 1) {
            Chunk chunk = new Chunk(world, x >> 4, z >> 4);
            chunk.setBlockMetadata(x, y, z, 2);
         } else {
            this.growTree(world, x, y, z);
         }
      }

   }

   public boolean growTree(World world, int x, int y, int z) {
      world.setBlockToAir(x, y, z);
      if (!TerrainGen.saplingGrowTree(world, world.rand, x, y, z)) {
         return false;
      } else {
         WorldGenRubberTree wg = new WorldGenRubberTree();
         if (!wg.generate(world, world.rand, x, y, z)) {
            world.setBlock(x, y, z, this, 1, 3);
            return false;
         } else {
            return true;
         }
      }
   }

   public int damageDropped(int i) {
      return i == 2 ? 1 : i;
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tab, List items) {
      items.add(new ItemStack(this, 1, 0));
      items.add(new ItemStack(this, 1, 1));
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldObj, int x, int y, int z) {
      int meta = worldObj.getBlockMetadata(x, y, z);
      return meta == 0 ? super.getCollisionBoundingBoxFromPool(worldObj, x, y, z) : Blocks.sapling.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
   }

   public boolean func_149851_a(World world, int x, int y, int z, boolean isWorldRemote) {
      return world.getBlockMetadata(x, y, z) > 0;
   }

   public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
      return (double)world.rand.nextFloat() < 0.45;
   }

   public void func_149853_b(World world, Random rand, int x, int y, int z) {
      this.growTree(world, x, y, z);
   }
}
