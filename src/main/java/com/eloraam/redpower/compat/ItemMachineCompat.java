package com.eloraam.redpower.compat;

import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.ItemExtended;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemMachineCompat extends ItemExtended {
    public ItemMachineCompat(Block block) {
        super(block);
    }

    public boolean onItemUse(
        ItemStack stack,
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
        Block bid = world.getBlock(x, y, z);
        Block bl = Block.getBlockFromItem(this);
        if (bid == Blocks.snow) {
            side = 1;
        } else if (bid != Blocks.vine && bid != Blocks.tallgrass && bid != Blocks.deadbush) {
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
                default:
                    ++x;
            }
        }

        if (stack.stackSize == 0) {
            return false;
        } else if (!player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        } else if (y >= world.getHeight() - 1) {
            return false;
        } else if (!world.canPlaceEntityOnSide(bl, x, y, z, false, side, player, stack)) {
            return false;
        } else {
            if (world.setBlock(x, y, z, bl, this.getMetadata(stack.getItemDamage()), 3)) {
                if (world.getBlock(x, y, z) == bl) {
                    BlockExtended bex = (BlockExtended) bl;
                    bex.onBlockPlacedBy(world, x, y, z, side, player, stack);
                }

                world.playSoundEffect(
                    (double) ((float) x + 0.5F),
                    (double) ((float) y + 0.5F),
                    (double) ((float) z + 0.5F),
                    bl.stepSound.func_150496_b(),
                    (bl.stepSound.getVolume() + 1.0F) / 2.0F,
                    bl.stepSound.getPitch() * 0.8F
                );
                --stack.stackSize;
            }

            return true;
        }
    }
}
