package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMachine extends BlockExtended {
   public BlockMachine() {
      super(Material.rock);
      this.setHardness(2.0F);
      this.setCreativeTab(CreativeExtraTabs.tabMachine);
   }

   @Override
   public boolean isOpaqueCube() {
      return true;
   }

   public boolean isNormalCube() {
      return true;
   }

   @Override
   public boolean renderAsNormalBlock() {
      return true;
   }

   public boolean isBlockNormalCube() {
      return false;
   }

   public boolean isSideSolid(IBlockAccess iba, int i, int j, int k, ForgeDirection side) {
      return true;
   }

   @Override
   public int damageDropped(int i) {
      return i;
   }

   public boolean canProvidePower() {
      return true;
   }

   @Override
   public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int side) {
      TileMachine tm = CoreLib.getTileEntity(iba, x, y, z, TileMachine.class);
      return tm != null && tm.isPoweringTo(side) ? 1 : 0;
   }

   public boolean isFireSource(World world, int x, int y, int z, ForgeDirection face) {
      int md = world.getBlockMetadata(x, y, z);
      if (md != 12) {
         return false;
      } else {
         TileIgniter tig = CoreLib.getTileEntity(world, x, y, z, TileIgniter.class);
         return tig != null && tig.isOnFire(face);
      }
   }
}
