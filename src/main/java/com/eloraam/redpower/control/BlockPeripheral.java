package com.eloraam.redpower.control;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CreativeExtraTabs;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPeripheral extends BlockExtended {
    public BlockPeripheral() {
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

    public boolean
    isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
        return true;
    }

    @Override
    public int damageDropped(int i) {
        return i;
    }
}
