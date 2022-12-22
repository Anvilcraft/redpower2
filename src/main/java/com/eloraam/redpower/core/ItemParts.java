package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemParts extends Item {
   private Map<Integer, String> names = new HashMap();
   private Map<Integer, IIcon> icons = new HashMap();
   private Map<Integer, String> iconNames = new HashMap();
   private List<Integer> valid = new ArrayList();

   public ItemParts() {
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public void addItem(int meta, String icon, String name) {
      this.iconNames.put(meta, icon);
      this.names.put(meta, name);
      this.valid.add(meta);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister reg) {
      for(int i = 0; i < this.iconNames.size(); ++i) {
         if (this.iconNames.get(i) != null && !((String)this.iconNames.get(i)).trim().isEmpty()) {
            this.icons.put(i, reg.registerIcon((String)this.iconNames.get(i)));
         } else {
            this.icons.put(i, null);
         }
      }

   }

   public String getUnlocalizedName(ItemStack stack) {
      String tr = (String)this.names.get(stack.getItemDamage());
      if (tr == null) {
         throw new IndexOutOfBoundsException();
      } else {
         return tr;
      }
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta) {
      IIcon tr = (IIcon)this.icons.get(meta);
      if (tr == null) {
         throw new IndexOutOfBoundsException();
      } else {
         return tr;
      }
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs tab, List items) {
      for(int i = 0; i < this.valid.size(); ++i) {
         items.add(new ItemStack(this, 1, i));
      }

   }
}
