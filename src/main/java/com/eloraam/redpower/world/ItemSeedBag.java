package com.eloraam.redpower.world;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSeedBag extends Item {
   private IIcon emptyIcon;
   private IIcon fullIcon;

   public ItemSeedBag() {
      this.setMaxDamage(576);
      this.setMaxStackSize(1);
      this.setUnlocalizedName("rpSeedBag");
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta) {
      return meta > 0 ? this.fullIcon : this.emptyIcon;
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister register) {
      this.emptyIcon = register.registerIcon("rpworld:seedBagEmpty");
      this.fullIcon = register.registerIcon("rpworld:seedBagFull");
   }

   public static IInventory getBagInventory(ItemStack ist, EntityPlayer host) {
      return !(ist.getItem() instanceof ItemSeedBag) ? null : new ItemSeedBag.InventorySeedBag(ist, host);
   }

   public static boolean canAdd(IInventory inv, ItemStack ist) {
      if (!(ist.getItem() instanceof IPlantable)) {
         return false;
      } else {
         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack is2 = inv.getStackInSlot(i);
            if (is2 != null && is2.stackSize != 0 && CoreLib.compareItemStack(is2, ist) != 0) {
               return false;
            }
         }

         return true;
      }
   }

   public static ItemStack getPlant(IInventory inv) {
      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack is2 = inv.getStackInSlot(i);
         if (is2 != null && is2.stackSize != 0) {
            return is2;
         }
      }

      return null;
   }

   private static void decrPlant(IInventory inv) {
      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack is2 = inv.getStackInSlot(i);
         if (is2 != null && is2.stackSize != 0) {
            inv.decrStackSize(i, 1);
            break;
         }
      }

   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 1;
   }

   public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
      if (!world.isRemote && player.isSneaking()) {
         player.openGui(RedPowerWorld.instance, 1, world, 0, 0, 0);
      }

      return ist;
   }

   public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
      if (side != 1) {
         return false;
      } else if (world.isRemote) {
         return false;
      } else if (player.isSneaking()) {
         return false;
      } else {
         IInventory baginv = getBagInventory(ist, player);
         ItemSeedBag.SpiralSearch search = new ItemSeedBag.SpiralSearch(new WorldCoord(x, y, z), 5);

         for(boolean st = false; search.again(); search.step()) {
            Block soil = world.getBlock(search.point.x, search.point.y, search.point.z);
            if (soil == Blocks.air) {
               if (!st) {
                  break;
               }
            } else {
               ItemStack plantstk = getPlant(baginv);
               if (plantstk == null || !(plantstk.getItem() instanceof IPlantable)) {
                  break;
               }

               IPlantable plant = (IPlantable)plantstk.getItem();
               if (soil != Blocks.air && soil.canSustainPlant(world, search.point.x, search.point.y, search.point.z, ForgeDirection.UP, plant)) {
                  if (!world.isAirBlock(search.point.x, search.point.y + 1, search.point.z)) {
                     if (!st) {
                        break;
                     }
                  } else {
                     st = true;
                     world.setBlock(
                        search.point.x,
                        search.point.y + 1,
                        search.point.z,
                        plant.getPlant(world, search.point.x, search.point.y + 1, search.point.z),
                        plant.getPlantMetadata(world, search.point.x, search.point.y + 1, search.point.z),
                        3
                     );
                     if (!player.capabilities.isCreativeMode) {
                        decrPlant(baginv);
                     }
                  }
               } else if (!st) {
                  break;
               }
            }
         }

         return true;
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack ist, EntityPlayer player, List lines, boolean par4) {
      if (ist.stackTagCompound != null && ist.getItemDamage() != 0) {
         IInventory baginv = getBagInventory(ist, player);

         for(int i = 0; i < baginv.getSizeInventory(); ++i) {
            ItemStack is2 = baginv.getStackInSlot(i);
            if (is2 != null && is2.stackSize != 0) {
               lines.add(StatCollector.translateToLocal("item." + is2.getItem().getUnlocalizedName(is2) + ".name"));
               return;
            }
         }
      }

   }

   public static class InventorySeedBag implements IInventory {
      ItemStack bagitem;
      ItemStack[] items;
      EntityPlayer player;

      InventorySeedBag(ItemStack ist, EntityPlayer host) {
         this.bagitem = ist;
         this.player = host;
         this.unpackInventory();
      }

      void unpackInventory() {
         this.items = new ItemStack[9];
         if (this.bagitem.stackTagCompound != null) {
            NBTTagList list = this.bagitem.stackTagCompound.getTagList("contents", 10);

            for(int i = 0; i < list.tagCount(); ++i) {
               NBTTagCompound item = list.getCompoundTagAt(i);
               byte slt = item.getByte("Slot");
               if (slt < 9) {
                  this.items[slt] = ItemStack.loadItemStackFromNBT(item);
               }
            }
         }

      }

      private void packInventory() {
         if (this.bagitem.stackTagCompound == null) {
            this.bagitem.setTagCompound(new NBTTagCompound());
         }

         int itc = 0;
         NBTTagList contents = new NBTTagList();

         for(int i = 0; i < 9; ++i) {
            if (this.items[i] != null) {
               itc += this.items[i].stackSize;
               NBTTagCompound cpd = new NBTTagCompound();
               this.items[i].writeToNBT(cpd);
               cpd.setByte("Slot", (byte)i);
               contents.appendTag(cpd);
            }
         }

         this.bagitem.stackTagCompound.setTag("contents", contents);
         this.bagitem.setItemDamage(itc == 0 ? 0 : 577 - itc);
      }

      public int getSizeInventory() {
         return 9;
      }

      public ItemStack getStackInSlot(int slot) {
         return this.items[slot];
      }

      public ItemStack decrStackSize(int slot, int num) {
         if (this.bagitem != this.player.getHeldItem()) {
            this.markDirty();
            this.player.closeScreen();
            return null;
         } else if (this.items[slot] == null) {
            return null;
         } else if (this.items[slot].stackSize <= num) {
            ItemStack tr = this.items[slot];
            this.items[slot] = null;
            this.markDirty();
            return tr;
         } else {
            ItemStack tr = this.items[slot].splitStack(num);
            if (this.items[slot].stackSize == 0) {
               this.items[slot] = null;
            }

            this.markDirty();
            return tr;
         }
      }

      public ItemStack getStackInSlotOnClosing(int slot) {
         if (this.items[slot] == null) {
            return null;
         } else {
            ItemStack tr = this.items[slot];
            this.items[slot] = null;
            return tr;
         }
      }

      public void setInventorySlotContents(int slot, ItemStack ist) {
         if (this.bagitem != this.player.getHeldItem()) {
            this.markDirty();
            this.player.closeScreen();
         } else {
            this.items[slot] = ist;
            if (ist != null && ist.stackSize > this.getInventoryStackLimit()) {
               ist.stackSize = this.getInventoryStackLimit();
            }

            this.markDirty();
         }
      }

      public String getInventoryName() {
         return "item.rpSeedBag.name";
      }

      public int getInventoryStackLimit() {
         return 64;
      }

      public void markDirty() {
         this.packInventory();
      }

      public boolean isUseableByPlayer(EntityPlayer pl) {
         return true;
      }

      public void openInventory() {
      }

      public void closeInventory() {
      }

      public boolean hasCustomInventoryName() {
         return true;
      }

      public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
         return false;
      }
   }

   public static class SpiralSearch {
      int curs;
      int rem;
      int ln;
      int steps;
      public WorldCoord point;

      public SpiralSearch(WorldCoord start, int size) {
         this.point = start;
         this.curs = 0;
         this.rem = 1;
         this.ln = 1;
         this.steps = size * size;
      }

      public boolean again() {
         return this.steps > 0;
      }

      public boolean step() {
         if (--this.steps == 0) {
            return false;
         } else {
            --this.rem;
            switch(this.curs) {
               case 0:
                  this.point.step(2);
                  break;
               case 1:
                  this.point.step(4);
                  break;
               case 2:
                  this.point.step(3);
                  break;
               default:
                  this.point.step(5);
            }

            if (this.rem > 0) {
               return true;
            } else {
               this.curs = this.curs + 1 & 3;
               this.rem = this.ln;
               if ((this.curs & 1) > 0) {
                  ++this.ln;
               }

               return true;
            }
         }
      }
   }
}
