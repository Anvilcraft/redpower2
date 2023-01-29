package com.eloraam.redpower.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCustomLog extends BlockLog {
    private String side;
    private IIcon sideIcon;
    private String top;
    private IIcon topIcon;

    public BlockCustomLog(String side, String top) {
        this.side = side;
        this.top = top;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.sideIcon = register.registerIcon(this.side);
        this.topIcon = register.registerIcon(this.top);
    }

    public static int func_150165_c(int p_150165_0_) {
        return p_150165_0_;
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getSideIcon(int damage) {
        return this.sideIcon;
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getTopIcon(int damage) {
        return this.topIcon;
    }

    public int damageDropped(int i) {
        return i == 1 ? 0 : i;
    }

    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
        BlockCustomLeaves.updateLeaves(world, i, j, k, 4);
    }
}
