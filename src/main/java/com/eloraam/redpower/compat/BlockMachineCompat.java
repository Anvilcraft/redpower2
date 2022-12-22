package com.eloraam.redpower.compat;

import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CreativeExtraTabs;
import net.minecraft.block.material.Material;

public class BlockMachineCompat extends BlockMultipart {
   public BlockMachineCompat() {
      super(Material.rock);
      this.setHardness(2.0F);
      this.setCreativeTab(CreativeExtraTabs.tabMachine);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isNormalCube() {
      return false;
   }

   @Override
   public boolean renderAsNormalBlock() {
      return false;
   }

   @Override
   public int damageDropped(int meta) {
      return meta;
   }
}
