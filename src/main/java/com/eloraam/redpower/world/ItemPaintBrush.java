package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IPaintable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemPaintBrush extends Item {
    private int color;

    public ItemPaintBrush(int col) {
        this.color = col;
        this.setMaxStackSize(1);
        this.setMaxDamage(15);
        this.setNoRepair();
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setTextureName("rpworld:paintBrush/" + col);
    }

    private boolean itemUseShared(
        ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side
    ) {
        IPaintable ip = CoreLib.getTileEntity(world, x, y, z, IPaintable.class);
        if (ip == null) {
            return false;
        } else {
            MovingObjectPosition mop = CoreLib.retraceBlock(world, player, x, y, z);
            if (mop == null) {
                return false;
            } else if (!ip.tryPaint(mop.subHit, mop.sideHit, this.color + 1)) {
                return false;
            } else {
                ist.damageItem(1, player);
                if (ist.stackSize == 0) {
                    player.inventory.setItemStack(new ItemStack(RedPowerWorld.itemBrushDry
                    ));
                }

                return true;
            }
        }
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
        return !world.isRemote && !player.isSneaking()
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
}
