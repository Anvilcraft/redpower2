package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class TileMultipart extends TileExtended implements IMultipart {
    @Override
    public boolean isSideSolid(int side) {
        return false;
    }

    @Override
    public boolean isSideNormal(int side) {
        return false;
    }

    @Override
    public List<ItemStack> harvestMultipart() {
        List<ItemStack> ist = new ArrayList();
        this.addHarvestContents(ist);
        this.deleteBlock();
        return ist;
    }

    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {}

    public boolean onPartActivateSide(EntityPlayer player, int part, int side) {
        return false;
    }

    public float getPartStrength(EntityPlayer player, int part) {
        return 0.0F;
    }

    public abstract boolean blockEmpty();

    public abstract void setPartBounds(BlockMultipart var1, int var2);

    public abstract int getSolidPartsMask();

    public abstract int getPartsMask();

    public void deleteBlock() {
        super.worldObj.setBlockToAir(super.xCoord, super.yCoord, super.zCoord);
    }
}
