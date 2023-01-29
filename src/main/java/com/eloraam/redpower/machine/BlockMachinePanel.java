package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockMachinePanel extends BlockMultipart {
    public BlockMachinePanel() {
        super(Material.rock);
        this.setHardness(2.0F);
        this.setCreativeTab(CreativeExtraTabs.tabMachine);
    }

    public int getLightValue(IBlockAccess iba, int i, int j, int k) {
        TileMachinePanel tmp
            = CoreLib.getTileEntity(iba, i, j, k, TileMachinePanel.class);
        return tmp == null ? 0 : tmp.getLightValue();
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
    public int damageDropped(int i) {
        return i;
    }
}
