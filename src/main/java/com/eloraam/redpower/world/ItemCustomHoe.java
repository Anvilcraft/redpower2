package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;

public class ItemCustomHoe extends ItemHoe {
   public ItemCustomHoe(ToolMaterial mat) {
      super(mat);
   }

   public boolean getIsRepairable(ItemStack ist1, ItemStack ist2) {
      return super.theToolMaterial == RedPowerWorld.toolMaterialRuby && ist2.isItemEqual(RedPowerBase.itemRuby)
         || super.theToolMaterial == RedPowerWorld.toolMaterialSapphire && ist2.isItemEqual(RedPowerBase.itemSapphire)
         || super.theToolMaterial == RedPowerWorld.toolMaterialGreenSapphire && ist2.isItemEqual(RedPowerBase.itemGreenSapphire)
         || super.getIsRepairable(ist1, ist2);
   }
}
