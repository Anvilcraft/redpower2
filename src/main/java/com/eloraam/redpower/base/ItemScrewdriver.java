package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.IRotatable;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemScrewdriver extends Item {
   public ItemScrewdriver() {
      this.setMaxDamage(200);
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
      this.setUnlocalizedName("screwdriver");
      this.setTextureName("rpbase:screwdriver");
   }

   public boolean hitEntity(ItemStack ist, EntityLivingBase ent, EntityLivingBase hitter) {
      ist.damageItem(8, hitter);
      return true;
   }

   public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xp, float yp, float zp) {
      if (!world.isRemote) {
         boolean sec = false;
         if (player != null && player.isSneaking()) {
            sec = true;
         }

         Block bid = world.getBlock(x, y, z);
         int md = world.getBlockMetadata(x, y, z);
         if (bid == Blocks.unpowered_repeater || bid == Blocks.powered_repeater) {
            world.setBlock(x, y, z, bid, md & 12 | md + 1 & 3, 3);
            ist.damageItem(1, player);
            return true;
         } else if (bid == Blocks.dispenser) {
            md = md & 3 ^ md >> 2;
            md += 2;
            world.setBlock(x, y, z, bid, md, 3);
            ist.damageItem(1, player);
            return true;
         } else if (bid != Blocks.piston && bid != Blocks.sticky_piston) {
            if (player.isSneaking()) {
               IRedbusConnectable irb = CoreLib.getTileEntity(world, x, y, z, IRedbusConnectable.class);
               if (irb != null && !(irb instanceof TileCPU)) {
                  player.openGui(RedPowerBase.instance, 3, world, x, y, z);
                  return true;
               }
            }

            IRotatable ir = CoreLib.getTileEntity(world, x, y, z, IRotatable.class);
            if (ir == null) {
               return false;
            } else {
               MovingObjectPosition mop = CoreLib.retraceBlock(world, player, x, y, z);
               if (mop == null) {
                  return false;
               } else {
                  int rm = ir.getPartMaxRotation(mop.subHit, sec);
                  if (rm == 0) {
                     return false;
                  } else {
                     int r = ir.getPartRotation(mop.subHit, sec);
                     if (++r > rm) {
                        r = 0;
                     }

                     ir.setPartRotation(mop.subHit, sec, r);
                     ist.damageItem(1, player);
                     return true;
                  }
               }
            }
         } else {
            if (++md > 5) {
               md = 0;
            }

            world.setBlock(x, y, z, bid, md, 3);
            ist.damageItem(1, player);
            return true;
         }
      } else {
         return false;
      }
   }

   public Multimap getAttributeModifiers(ItemStack stack) {
      Multimap map = super.getAttributeModifiers(stack);
      map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(Item.field_111210_e, "Weapon modifier", 4.0, 0));
      return map;
   }
}
