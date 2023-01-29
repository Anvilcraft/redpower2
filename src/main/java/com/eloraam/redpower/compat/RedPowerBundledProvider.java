package com.eloraam.redpower.compat;

import com.eloraam.redpower.core.IRedPowerConnectable;
import com.eloraam.redpower.core.RedPowerLib;

import dan200.computercraft.api.redstone.IBundledRedstoneProvider;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class RedPowerBundledProvider implements IBundledRedstoneProvider {

    @Override
    public int getBundledRedstoneOutput(World world, int x, int y, int z, int side) {
        if (world.getTileEntity(x, y, z) instanceof IRedPowerConnectable) {
            int offsetX = x + Facing.offsetsXForSide[side];
            int offsetY = y + Facing.offsetsYForSide[side];
            int offsetZ = z + Facing.offsetsZForSide[side];
            int offsetSide = Facing.oppositeSide[side];
            int cons = RedPowerLib.getConDirMask(offsetSide);
            int combination = 0;
            for (int c = 0; c < 16; ++c) {
                if (RedPowerLib.getPowerState(world, offsetX, offsetY, offsetZ, cons, c + 1) <= 0) continue;
                combination |= 1 << c;
            }
            return combination;
        }
        return -1;
    }
    
}
