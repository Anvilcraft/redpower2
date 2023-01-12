package com.eloraam.redpower.core;

import java.util.ArrayList;

import com.eloraam.redpower.RedPowerCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMultiblock extends BlockContainer {
    public BlockMultiblock() {
        super(CoreLib.materialRedpower);
    }

    public void registerBlockIcons(IIconRegister reg) {}

    public int getRenderType() {
        return RedPowerCore.nullBlockModel;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public ArrayList<ItemStack>
    getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList();
    }

    public TileEntity createNewTileEntity(World worldObj, int metadata) {
        return null;
    }

    public TileEntity createTileEntity(World worldObj, int metadata) {
        switch (metadata) {
            case 0:
                return new TileMultiblock();
            default:
                return null;
        }
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int md) {
        TileMultiblock tmb = CoreLib.getTileEntity(world, x, y, z, TileMultiblock.class);
        if (tmb != null) {
            IMultiblock imb = CoreLib.getTileEntity(
                world, tmb.relayX, tmb.relayY, tmb.relayZ, IMultiblock.class
            );
            if (imb != null) {
                imb.onMultiRemoval(tmb.relayNum);
            }
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
        TileMultiblock tmb = CoreLib.getTileEntity(iba, x, y, z, TileMultiblock.class);
        if (tmb == null) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            IMultiblock imb = CoreLib.getTileEntity(
                iba, tmb.relayX, tmb.relayY, tmb.relayZ, IMultiblock.class
            );
            if (imb != null) {
                AxisAlignedBB aabb = imb.getMultiBounds(tmb.relayNum);
                int xa = tmb.relayX - x;
                int ya = tmb.relayY - y;
                int za = tmb.relayZ - z;
                this.setBlockBounds(
                    (float) aabb.minX + (float) xa,
                    (float) aabb.minY + (float) ya,
                    (float) aabb.minZ + (float) za,
                    (float) aabb.maxX + (float) xa,
                    (float) aabb.maxY + (float) ya,
                    (float) aabb.maxZ + (float) za
                );
            }
        }
    }

    public AxisAlignedBB
    getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public AxisAlignedBB
    getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    public float getPlayerRelativeBlockHardness(
        EntityPlayer player, World world, int x, int y, int z
    ) {
        TileMultiblock tmb = CoreLib.getTileEntity(world, x, y, z, TileMultiblock.class);
        if (tmb == null) {
            return 0.0F;
        } else {
            IMultiblock imb = CoreLib.getTileEntity(
                world, tmb.relayX, tmb.relayY, tmb.relayZ, IMultiblock.class
            );
            return imb == null ? 0.0F : imb.getMultiBlockStrength(tmb.relayNum, player);
        }
    }
}
