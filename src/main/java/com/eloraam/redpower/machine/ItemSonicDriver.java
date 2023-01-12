package com.eloraam.redpower.machine;

import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.core.IChargeable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSonicDriver extends ItemScrewdriver implements IChargeable {
    public ItemSonicDriver() {
        this.setMaxDamage(400);
        this.setNoRepair();
    }

    @Override
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
        return !world.isRemote && ist.getItemDamage() != ist.getMaxDamage()
            && super.onItemUseFirst(ist, player, world, x, y, z, side, xp, yp, zp);
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        return HashMultimap.create();
    }

    @Override
    public boolean
    hitEntity(ItemStack ist, EntityLivingBase ent, EntityLivingBase hitter) {
        return false;
    }
}
