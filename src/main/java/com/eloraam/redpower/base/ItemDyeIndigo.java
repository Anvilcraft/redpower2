package com.eloraam.redpower.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDyeIndigo extends Item {
    public ItemDyeIndigo() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("dyeIndigo");
        this.setTextureName("rpbase:dyeIndigo");
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    public boolean itemInteractionForEntity(
        ItemStack ist, EntityPlayer player, EntityLivingBase entity
    ) {
        if (ist.getItemDamage() == 0 && entity instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) entity;
            if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != 11) {
                entitysheep.setFleeceColor(11);
                --ist.stackSize;
                return true;
            }
        }

        return false;
    }
}
