package com.eloraam.redpower.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public interface IFrameSupport {
    void writeFramePacket(NBTTagCompound var1);

    void readFramePacket(NBTTagCompound var1);

    void onFrameRefresh(IBlockAccess var1);

    void onFramePickup(IBlockAccess var1);

    void onFrameDrop();
}
