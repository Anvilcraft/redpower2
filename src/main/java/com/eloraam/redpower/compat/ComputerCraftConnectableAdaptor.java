package com.eloraam.redpower.compat;

import com.eloraam.redpower.core.IRedPowerConnectableAdaptor;

import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import net.minecraft.tileentity.TileEntity;

public class ComputerCraftConnectableAdaptor implements IRedPowerConnectableAdaptor {

    @Override
    public boolean canHandle(TileEntity tile) {
        return tile instanceof TileComputerBase;
    }

    @Override
    public int getPoweringMask(int ch, TileEntity tile) {
        TileComputerBase computer = (TileComputerBase) tile;
        int direction = computer.getDirection();
        return ComputercraftInterop.getComputerPoweringMask(ch, computer, direction);
    }

    @Override
    public int getConnectableMask(TileEntity tile) {
        return -1;
    }

    @Override
    public int getConnectClass(int var1, TileEntity tile) {
        return ComputercraftInterop.getComputerConnectClass();
    }

    @Override
    public int getCornerPowerMode(TileEntity tile) {
        return 0;
    }
    
}
