package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.RedPowerWorld;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ItemSickle extends ItemTool {
   public int cropRadius = 2;
   public int leafRadius = 1;

   public ItemSickle(ToolMaterial mat) {
      super(3.0F, mat, new HashSet());
      this.setMaxStackSize(1);
   }

   public float func_150893_a(ItemStack ist, Block bl) {
      return !(bl instanceof BlockLeavesBase) && !(bl instanceof BlockBush) ? super.func_150893_a(ist, bl) : super.efficiencyOnProperMaterial;
   }

   public boolean onBlockDestroyed(ItemStack ist, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
      boolean used = false;
      if (!(entity instanceof EntityPlayer)) {
         return false;
      } else {
         EntityPlayer player = (EntityPlayer)entity;
         if (block != null && block instanceof BlockLeavesBase) {
            for(int q = -this.leafRadius; q <= this.leafRadius; ++q) {
               for(int r = -this.leafRadius; r <= this.leafRadius; ++r) {
                  for(int s = -this.leafRadius; s <= this.leafRadius; ++s) {
                     Block bl = world.getBlock(x + q, y + r, z + s);
                     int md = world.getBlockMetadata(x + q, y + r, z + s);
                     if (bl != null && bl instanceof BlockLeavesBase) {
                        BreakEvent event = new BreakEvent(x + q, y + r, z + s, world, bl, md, player);
                        if (!MinecraftForge.EVENT_BUS.post(event)) {
                           if (bl.canHarvestBlock(player, md)) {
                              bl.harvestBlock(world, player, x + q, y + r, z + s, md);
                           }

                           world.setBlockToAir(x + q, y + r, z + s);
                           used = true;
                        }
                     }
                  }
               }
            }
         } else if (block != null && block instanceof BlockBush) {
            for(int q = -this.cropRadius; q <= this.cropRadius; ++q) {
               for(int r = -this.cropRadius; r <= this.cropRadius; ++r) {
                  Block bl = world.getBlock(x + q, y, z + r);
                  int md = world.getBlockMetadata(x + q, y, z + r);
                  if (bl != null && bl instanceof BlockBush) {
                     BreakEvent event = new BreakEvent(x + q, y, z + r, world, bl, md, player);
                     if (!MinecraftForge.EVENT_BUS.post(event)) {
                        if (bl.canHarvestBlock(player, md)) {
                           bl.harvestBlock(world, player, x + q, y, z + r, md);
                        }

                        world.setBlockToAir(x + q, y, z + r);
                        used = true;
                     }
                  }
               }
            }
         }

         if (used) {
            ist.damageItem(1, entity);
         }

         return used;
      }
   }

   public boolean getIsRepairable(ItemStack self, ItemStack repairMaterial) {
      return super.toolMaterial == RedPowerWorld.toolMaterialRuby && repairMaterial.isItemEqual(RedPowerBase.itemRuby)
         || super.toolMaterial == RedPowerWorld.toolMaterialSapphire && repairMaterial.isItemEqual(RedPowerBase.itemSapphire)
         || super.toolMaterial == RedPowerWorld.toolMaterialGreenSapphire && repairMaterial.isItemEqual(RedPowerBase.itemGreenSapphire)
         || super.getIsRepairable(self, repairMaterial);
   }

   public int getItemEnchantability() {
      return 20;
   }
}
