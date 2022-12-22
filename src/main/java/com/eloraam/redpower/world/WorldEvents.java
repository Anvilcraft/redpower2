package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.MachineLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import java.util.stream.IntStream;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class WorldEvents {
   @SubscribeEvent
   public void onBonemeal(BonemealEvent ev) {
      if (ev.block == RedPowerWorld.blockCrops) {
         int md = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);
         if (md == 4 || md == 5) {
            return;
         }

         if (ev.world.isRemote) {
            ev.setResult(Result.ALLOW);
            return;
         }

         if (RedPowerWorld.blockCrops.fertilize(ev.world, ev.x, ev.y, ev.z)) {
            ev.setResult(Result.ALLOW);
         }
      }

   }

   @SubscribeEvent
   public void onDeath(LivingDeathEvent ev) {
      if (ev.source instanceof EntityDamageSource) {
         EntityDamageSource eds = (EntityDamageSource)ev.source;
         Entity ent = eds.getEntity();
         if (ent instanceof EntityPlayer) {
            EntityPlayer epl = (EntityPlayer)ent;
            ItemStack wpn = epl.getCurrentEquippedItem();
            if (EnchantmentHelper.getEnchantmentLevel(RedPowerWorld.enchantVorpal.effectId, wpn) != 0 && ev.entityLiving.getHealth() <= -20.0F) {
               if (ev.entityLiving instanceof EntitySkeleton) {
                  EntitySkeleton ist = (EntitySkeleton)ev.entityLiving;
                  if (ist.getSkeletonType() == 1) {
                     return;
                  }

                  ev.entityLiving.entityDropItem(new ItemStack(Items.skull, 1, 0), 0.0F);
               } else if (ev.entityLiving instanceof EntityZombie) {
                  ev.entityLiving.entityDropItem(new ItemStack(Items.skull, 1, 2), 0.0F);
               } else if (ev.entityLiving instanceof EntityPlayer) {
                  ItemStack ist1 = new ItemStack(Items.skull, 1, 3);
                  ist1.setTagCompound(new NBTTagCompound());
                  ist1.getTagCompound().setString("SkullOwner", ev.entityLiving.getCommandSenderName());
                  ev.entityLiving.entityDropItem(ist1, 0.0F);
               } else if (ev.entityLiving instanceof EntityCreeper) {
                  ev.entityLiving.entityDropItem(new ItemStack(Items.skull, 1, 4), 0.0F);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onPickupItem(EntityItemPickupEvent ev) {
      for(int i = 0; i < 9; ++i) {
         ItemStack ist = ev.entityPlayer.inventory.getStackInSlot(i);
         if (ist != null && ist.getItem() instanceof ItemSeedBag) {
            IInventory inv = ItemSeedBag.getBagInventory(ist, ev.entityPlayer);
            if (inv != null && ItemSeedBag.getPlant(inv) != null) {
               ItemStack tpi = ev.item.getEntityItem();
               int[] slots = IntStream.range(0, inv.getSizeInventory()).toArray();
               if (ItemSeedBag.canAdd(inv, tpi) && MachineLib.addToInventoryCore(inv, tpi, slots, true)) {
                  ev.item.setDead();
                  ev.setResult(Result.ALLOW);
                  return;
               }
            }
         }
      }

   }
}
