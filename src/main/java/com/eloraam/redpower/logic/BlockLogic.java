package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.BlockCoverable;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.world.IBlockAccess;

public class BlockLogic extends BlockCoverable {
    public BlockLogic() {
        super(CoreLib.materialRedpower);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setHardness(0.1F);
        this.setLightLevel(0.625F);
        this.setCreativeTab(CreativeExtraTabs.tabWires);
    }

    public boolean canConnectRedstone(IBlockAccess iba, int x, int y, int z, int side) {
        if (side < 0) {
            return false;
        } else {
            IRedPowerConnectable irp
                = CoreLib.getTileEntity(iba, x, y, z, IRedPowerConnectable.class);
            if (irp == null) {
                return false;
            } else {
                int s = RedPowerLib.mapLocalToRot(irp.getConnectableMask(), 2);
                return (s & 1 << side) > 0;
            }
        }
    }

    public int getLightValue(IBlockAccess iba, int x, int y, int z) {
        TileLogic tl = CoreLib.getTileEntity(iba, x, y, z, TileLogic.class);
        return tl == null ? super.getLightValue(iba, x, y, z) : tl.getLightValue();
    }

    public boolean canProvidePower() {
        return true;
    }
}
