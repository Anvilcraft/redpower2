package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeExtraTabs {
    public static CreativeTabs tabWires
        = new CreativeTabs(CreativeTabs.getNextID(), "RPWires") {
              public ItemStack getIconItemStack() {
                  return new ItemStack(RedPowerBase.blockMicro, 1, 768);
              }

              public Item getTabIconItem() {
                  return null;
              }
          };
    public static CreativeTabs tabMicros
        = new CreativeTabs(CreativeTabs.getNextID(), "RPMicroblocks") {
              public ItemStack getIconItemStack() {
                  return new ItemStack(RedPowerBase.blockMicro, 1, 23);
              }

              public Item getTabIconItem() {
                  return null;
              }
          };
    public static CreativeTabs tabMachine
        = new CreativeTabs(CreativeTabs.getNextID(), "RPMachines") {
              public ItemStack getIconItemStack() {
                  return new ItemStack(RedPowerBase.blockAppliance, 1, 3);
              }

              public Item getTabIconItem() {
                  return null;
              }
          };
}
