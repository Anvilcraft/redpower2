package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IPipeConnectable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemVoltmeter extends Item {
   public ItemVoltmeter() {
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
      this.setTextureName("rpmachine:voltmeter");
      this.setUnlocalizedName("voltmeter");
   }

   private boolean measureBlue(EntityPlayer player, World world, int x, int y, int z, int side) {
      IBluePowerConnectable ibc = CoreLib.getTileEntity(world, x, y, z, IBluePowerConnectable.class);
      if (ibc == null) {
         return false;
      } else {
         BluePowerConductor bpc = ibc.getBlueConductor(side);
         double v = bpc.getVoltage();
         CoreLib.writeChat(player, String.format("Reading %.2fV %.2fA (%.2fW)", v, bpc.Itot, v * bpc.Itot));
         return true;
      }
   }

   private boolean measurePressure(EntityPlayer player, World world, int x, int y, int z, int side) {
      IPipeConnectable ipc = CoreLib.getTileEntity(world, x, y, z, IPipeConnectable.class);
      if (ipc == null) {
         return false;
      } else {
         int psi = ipc.getPipePressure(side);
         CoreLib.writeChat(player, String.format("Reading %d psi", psi));
         return true;
      }
   }

   private boolean itemUseShared(ItemStack ist, EntityPlayer player, World world, int i, int j, int k, int l) {
      return this.measureBlue(player, world, i, j, k, l) || this.measurePressure(player, world, i, j, k, l);
   }

   public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xp, float yp, float zp) {
      return !player.isSneaking() && this.itemUseShared(ist, player, world, x, y, z, side);
   }

   public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xp, float yp, float zp) {
      return !world.isRemote && player.isSneaking() && this.itemUseShared(ist, player, world, x, y, z, side);
   }
}
