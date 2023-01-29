package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ItemCustomPickaxe extends ItemPickaxe {
    public ItemCustomPickaxe(ToolMaterial mat) {
        super(mat);
    }

    public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
        return super.toolMaterial == RedPowerWorld.toolMaterialRuby
            && ist2.isItemEqual(RedPowerBase.itemRuby)
            || super.toolMaterial == RedPowerWorld.toolMaterialSapphire
            && ist2.isItemEqual(RedPowerBase.itemSapphire)
            || super.toolMaterial == RedPowerWorld.toolMaterialGreenSapphire
            && ist2.isItemEqual(RedPowerBase.itemGreenSapphire)
            || super.getIsRepairable(ist1, ist2);
    }
}
