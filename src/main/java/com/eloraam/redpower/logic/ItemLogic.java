package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.RedPowerLib;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLogic extends ItemExtended {
    public ItemLogic(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public void placeNoise(World world, int x, int y, int z, Block block) {
        world.playSoundEffect(
            (double) ((float) x + 0.5F),
            (double) ((float) y + 0.5F),
            (double) ((float) z + 0.5F),
            "step.stone",
            (block.stepSound.getVolume() + 1.0F) / 2.0F,
            block.stepSound.getPitch() * 0.8F
        );
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
        int x,
        int y,
        int z,
        int side,
        float xp,
        float yp,
        float zp
    ) {
        return !world.isRemote && player.isSneaking()
            && this.itemUseShared(ist, player, world, x, y, z, side);
    }

    protected boolean tryPlace(
        ItemStack ist,
        EntityPlayer player,
        World world,
        int i,
        int j,
        int k,
        int l,
        int down,
        int rot
    ) {
        int md = ist.getItemDamage();
        Block bid = Block.getBlockFromItem(ist.getItem());
        if (!world.setBlock(i, j, k, bid, md >> 8, 3)) {
            return false;
        } else {
            TileLogic tl = CoreLib.getTileEntity(world, i, j, k, TileLogic.class);
            if (tl == null) {
                return false;
            } else {
                tl.Rotation = down << 2 | rot;
                tl.initSubType(md & 0xFF);
                return true;
            }
        }
    }

    protected boolean itemUseShared(
        ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side
    ) {
        switch (side) {
            case 0:
                --y;
                break;
            case 1:
                ++y;
                break;
            case 2:
                --z;
                break;
            case 3:
                ++z;
                break;
            case 4:
                --x;
                break;
            case 5:
                ++x;
        }

        Block bid = Block.getBlockFromItem(ist.getItem());
        if (!world.canPlaceEntityOnSide(
                world.getBlock(x, y, z), x, y, z, false, side, player, ist
            )) {
            return false;
        } else if (!RedPowerLib.isSideNormal(world, x, y, z, side ^ 1)) {
            return false;
        } else {
            int yaw
                = (int) Math.floor((double) (player.rotationYaw / 90.0F + 0.5F)) + 1 & 3;
            int pitch = (int) Math.floor((double) (player.rotationPitch / 90.0F + 0.5F));
            int down = side ^ 1;
            int rot;
            switch (down) {
                case 0:
                    rot = yaw;
                    break;
                case 1:
                    rot = yaw ^ (yaw & 1) << 1;
                    break;
                case 2:
                    rot = (yaw & 1) > 0 ? (pitch > 0 ? 2 : 0) : 1 - yaw & 3;
                    break;
                case 3:
                    rot = (yaw & 1) > 0 ? (pitch > 0 ? 2 : 0) : yaw - 1 & 3;
                    break;
                case 4:
                    rot = (yaw & 1) == 0 ? (pitch > 0 ? 2 : 0) : yaw - 2 & 3;
                    break;
                case 5:
                    rot = (yaw & 1) == 0 ? (pitch > 0 ? 2 : 0) : 2 - yaw & 3;
                    break;
                default:
                    rot = 0;
            }

            if (!this.tryPlace(ist, player, world, x, y, z, side, down, rot)) {
                return true;
            } else {
                this.placeNoise(world, x, y, z, bid);
                --ist.stackSize;
                world.markBlockForUpdate(x, y, z);
                return true;
            }
        }
    }
}
