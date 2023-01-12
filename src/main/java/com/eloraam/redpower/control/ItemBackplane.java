package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBackplane extends ItemExtended {
    public ItemBackplane(Block block) {
        super(block);
    }

    public boolean onItemUse(
        ItemStack ist,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z,
        int side,
        float xp,
        float yp,
        float zp
    ) {
        return !player.isSneaking()
            && this.itemUseShared(ist, player, world, x, y, z, side);
    }

    public boolean onItemUseFirst(
        ItemStack ist,
        EntityPlayer player,
        World world,
        int y,
        int x,
        int z,
        int side,
        float xp,
        float yp,
        float zp
    ) {
        return !world.isRemote && player.isSneaking()
            && this.itemUseShared(ist, player, world, x, y, z, side);
    }

    protected boolean itemUseShared(
        ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side
    ) {
        Block bid = world.getBlock(x, y, z);
        int md = world.getBlockMetadata(x, y, z);
        int dmg = ist.getItemDamage();
        if (bid == Block.getBlockFromItem(ist.getItem()) && md == 0 && dmg != 0) {
            TileBackplane bp = CoreLib.getTileEntity(world, x, y, z, TileBackplane.class);
            if (bp == null) {
                return false;
            } else {
                int rx = bp.Rotation;
                if (!world.setBlock(x, y, z, bid, dmg, 3)) {
                    return false;
                } else {
                    bp = CoreLib.getTileEntity(world, x, y, z, TileBackplane.class);
                    if (bp != null) {
                        bp.Rotation = rx;
                    }

                    world.markBlockForUpdate(x, y, z);
                    CoreLib.placeNoise(
                        world, x, y, z, Block.getBlockFromItem(ist.getItem())
                    );
                    if (!player.capabilities.isCreativeMode) {
                        --ist.stackSize;
                    }

                    RedPowerLib.updateIndirectNeighbors(
                        world, x, y, z, Block.getBlockFromItem(ist.getItem())
                    );
                    return true;
                }
            }
        } else if (dmg != 0) {
            return false;
        } else {
            WorldCoord wc = new WorldCoord(x, y, z);
            wc.step(side);
            if (!world.canPlaceEntityOnSide(
                    Block.getBlockFromItem(ist.getItem()),
                    wc.x,
                    wc.y,
                    wc.z,
                    false,
                    1,
                    player,
                    ist
                )) {
                return false;
            } else if (!RedPowerLib.isSideNormal(world, wc.x, wc.y, wc.z, 0)) {
                return false;
            } else {
                int rx = -1;

            label84:
                for (int i = 0; i < 4; ++i) {
                    WorldCoord wc2 = wc.copy();
                    int dir = CoreLib.rotToSide(i) ^ 1;
                    wc2.step(dir);
                    TileCPU cpu = CoreLib.getTileEntity(world, wc2, TileCPU.class);
                    if (cpu != null && cpu.Rotation == i) {
                        rx = i;
                        break;
                    }

                    TileBackplane backplane
                        = CoreLib.getTileEntity(world, wc2, TileBackplane.class);
                    if (backplane != null && backplane.Rotation == i) {
                        for (int pb = 0; pb < 6; ++pb) {
                            wc2.step(dir);
                            if (world.getBlock(wc2.x, wc2.y, wc2.z)
                                    == RedPowerControl.blockPeripheral
                                && world.getBlockMetadata(wc2.x, wc2.y, wc2.z) == 1) {
                                rx = i;
                                break label84;
                            }
                        }
                    }
                }

                if (rx < 0) {
                    return false;
                } else if (!world.setBlock(
                               wc.x,
                               wc.y,
                               wc.z,
                               Block.getBlockFromItem(ist.getItem()),
                               dmg,
                               3
                           )) {
                    return true;
                } else {
                    TileBackplane bp
                        = CoreLib.getTileEntity(world, wc, TileBackplane.class);
                    bp.Rotation = rx;
                    CoreLib.placeNoise(
                        world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem())
                    );
                    if (!player.capabilities.isCreativeMode) {
                        --ist.stackSize;
                    }

                    world.markBlockForUpdate(wc.x, wc.y, wc.z);
                    RedPowerLib.updateIndirectNeighbors(
                        world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem())
                    );
                    return true;
                }
            }
        }
    }
}
