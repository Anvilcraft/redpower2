package com.eloraam.redpower.core;

import net.minecraft.tileentity.TileEntity;

public interface IRedPowerConnectableAdaptor {

    boolean canHandle(TileEntity tile);

    int getPoweringMask(int var1, TileEntity tile);

    int getConnectableMask(TileEntity tile);

    int getConnectClass(int var1, TileEntity tile);

    int getCornerPowerMode(TileEntity tile);
    
}
