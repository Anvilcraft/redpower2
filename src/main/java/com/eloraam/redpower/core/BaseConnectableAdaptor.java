package com.eloraam.redpower.core;

import net.minecraft.tileentity.TileEntity;

public class BaseConnectableAdaptor implements IRedPowerConnectableAdaptor {

    @Override
    public int getPoweringMask(int var1, TileEntity tile) {
        return ((IRedPowerConnectable)tile).getPoweringMask(var1);
    }

    @Override
    public int getConnectableMask(TileEntity tile) {
        return ((IConnectable)tile).getConnectableMask();
    }

    @Override
    public int getConnectClass(int var1, TileEntity tile) {
        return ((IConnectable)tile).getConnectClass(var1);
    }

    @Override
    public int getCornerPowerMode(TileEntity tile) {
        return ((IConnectable)tile).getCornerPowerMode();
    }

    @Override
    public boolean canHandle(TileEntity tile) {
        return tile instanceof IRedPowerConnectable;
    }
    
}
