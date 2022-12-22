package com.eloraam.redpower.base;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockAppliance extends BlockExtended {
   public BlockAppliance() {
      super(Material.rock);
      this.setHardness(2.0F);
      this.setCreativeTab(CreativeExtraTabs.tabMachine);
   }

   public int getLightValue(IBlockAccess iba, int i, int j, int k) {
      TileAppliance taf = CoreLib.getTileEntity(iba, i, j, k, TileAppliance.class);
      return taf == null ? super.getLightValue(iba, i, j, k) : taf.getLightValue();
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

   @Override
   public int damageDropped(int meta) {
      return meta;
   }
}
