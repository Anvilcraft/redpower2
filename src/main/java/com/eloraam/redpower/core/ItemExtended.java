package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemExtended extends ItemBlock {
   private Map<Integer, String> names = new HashMap();
   private List<Integer> valid = new ArrayList();

   public ItemExtended(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int meta) {
      return meta;
   }

   public void setMetaName(int meta, String name) {
      this.names.put(meta, name);
      this.valid.add(meta);
   }

   public String getUnlocalizedName(ItemStack ist) {
      String tr = (String)this.names.get(ist.getItemDamage());
      return tr != null ? tr : "unnamed";
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs tab, List list) {
      for(int i : this.valid) {
         list.add(new ItemStack(this, 1, i));
      }

   }

   public void placeNoise(World world, int x, int y, int z, Block block) {
   }

   public boolean placeBlockAt(
      ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata
   ) {
      if (!world.setBlock(x, y, z, Block.getBlockFromItem(this), metadata, 3)) {
         return false;
      } else {
         if (world.getBlock(x, y, z) == Block.getBlockFromItem(this)) {
            BlockExtended bex = (BlockExtended)Block.getBlockFromItem(this);
            bex.onBlockPlacedBy(world, x, y, z, side, player, stack);
            this.placeNoise(world, x, y, z, Block.getBlockFromItem(this));
         }

         return true;
      }
   }
}
