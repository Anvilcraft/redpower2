package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.CoreLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLamp extends BlockExtended {
    public BlockLamp() {
        super(CoreLib.materialRedpower);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setHardness(0.5F);
        this.setCreativeTab(RedPowerLighting.tabLamp);
    }

    public boolean canRenderInPass(int pass) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean
    isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    public int getRenderBlockPass() {
        return 1;
    }

    public int getLightValue(IBlockAccess iba, int x, int y, int z) {
        TileLamp lamp = CoreLib.getTileEntity(iba, x, y, z, TileLamp.class);
        return lamp == null ? 0 : lamp.getLightValue();
    }

    public ItemStack getPickBlock(
        MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player
    ) {
        TileLamp lamp = CoreLib.getTileEntity(world, x, y, z, TileLamp.class);
        return lamp != null
            ? new ItemStack(this, 1, (lamp.Inverted ? 16 : 0) + lamp.Color)
            : null;
    }
}
