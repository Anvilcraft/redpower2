package com.eloraam.redpower.core;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IMultipart {
    boolean isSideSolid(int var1);

    boolean isSideNormal(int var1);

    List<ItemStack> harvestMultipart();
}
