package com.eloraam.redpower.control;

import com.eloraam.redpower.core.CoreLib;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemDisk extends Item {
   private IIcon emptyIcon;
   private IIcon forthIcon;
   private IIcon forthExtIcon;

   public ItemDisk() {
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
      this.setMaxStackSize(1);
   }

   public void registerIcons(IIconRegister reg) {
      this.emptyIcon = reg.registerIcon("rpcontrol:disk");
      this.forthIcon = reg.registerIcon("rpcontrol:diskForth");
      this.forthExtIcon = reg.registerIcon("rpcontrol:diskForthExtended");
   }

   public String getUnlocalizedName(ItemStack stack) {
      switch(stack.getItemDamage()) {
         case 0:
            return "item.disk";
         case 1:
            return "item.disk.forth";
         case 2:
            return "item.disk.forthxp";
         default:
            return null;
      }
   }

   @SideOnly(Side.CLIENT)
   public String getItemStackDisplayName(ItemStack ist) {
      return ist.stackTagCompound == null
         ? super.getItemStackDisplayName(ist)
         : (!ist.stackTagCompound.hasKey("label") ? super.getItemStackDisplayName(ist) : ist.stackTagCompound.getString("label"));
   }

   @SideOnly(Side.CLIENT)
   public EnumRarity getRarity(ItemStack ist) {
      return ist.getItemDamage() >= 1 ? EnumRarity.uncommon : EnumRarity.common;
   }

   public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
      if (!world.isRemote) {
         TileDiskDrive tdd = CoreLib.getTileEntity(world, x, y, z, TileDiskDrive.class);
         if (tdd != null && tdd.setDisk(ist.copy())) {
            ist.stackSize = 0;
            tdd.updateBlock();
            return true;
         }
      }

      return false;
   }

   public boolean getShareTag() {
      return true;
   }

   public IIcon getIconFromDamage(int dmg) {
      switch(dmg) {
         case 1:
            return this.forthIcon;
         case 2:
            return this.forthExtIcon;
         default:
            return this.emptyIcon;
      }
   }

   public void getSubItems(Item item, CreativeTabs tab, List items) {
      for(int i = 0; i <= 2; ++i) {
         items.add(new ItemStack(this, 1, i));
      }

   }
}
