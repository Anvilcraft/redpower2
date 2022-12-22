package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBag extends Item {
   private IIcon[] icons = new IIcon[16];

   public ItemBag() {
      this.setMaxStackSize(1);
      this.setHasSubtypes(true);
      this.setUnlocalizedName("rpBag");
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   public static IInventory getBagInventory(ItemStack ist, EntityPlayer player) {
      return !(ist.getItem() instanceof ItemBag) ? null : ((ItemBag)ist.getItem()).new InventoryBag(ist, player);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister registerer) {
      for(int color = 0; color < 16; ++color) {
         this.icons[color] = registerer.registerIcon("rpbase:bag/" + color);
      }

   }

   public int getMaxItemUseDuration(ItemStack ist) {
      return 1;
   }

   public IIcon getIconFromDamage(int meta) {
      return this.icons[meta % this.icons.length];
   }

   public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
      if (!world.isRemote && !player.isSneaking()) {
         player.openGui(RedPowerBase.instance, 4, world, 0, 0, 0);
      }

      return ist;
   }

   public class InventoryBag implements IInventory {
      ItemStack bagitem;
      ItemStack[] items;
      EntityPlayer player;

      InventoryBag(ItemStack ist, EntityPlayer host) {
         this.bagitem = ist;
         this.player = host;
         this.unpackInventory();
      }

      private void unpackInventory() {
         this.items = new ItemStack[27];
         if (this.bagitem.stackTagCompound != null) {
            NBTTagList list = this.bagitem.stackTagCompound.getTagList("contents", 10);

            for(int i = 0; i < list.tagCount(); ++i) {
               NBTTagCompound item = list.getCompoundTagAt(i);
               byte slt = item.getByte("Slot");
               if (slt < 27) {
                  this.items[slt] = ItemStack.loadItemStackFromNBT(item);
               }
            }
         }

      }

      private void packInventory() {
         if (this.bagitem.stackTagCompound == null) {
            this.bagitem.setTagCompound(new NBTTagCompound());
         }

         NBTTagList contents = new NBTTagList();

         for(int i = 0; i < 27; ++i) {
            if (this.items[i] != null) {
               NBTTagCompound cpd = new NBTTagCompound();
               this.items[i].writeToNBT(cpd);
               cpd.setByte("Slot", (byte)i);
               contents.appendTag(cpd);
            }
         }

         this.bagitem.stackTagCompound.setTag("contents", contents);
      }

      public int getSizeInventory() {
         return 27;
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

         }
      }

      public String getInventoryName() {
         return "item.rpBag.name";
      }

      public int getInventoryStackLimit() {
         return 64;
      }

      public void markDirty() {
         this.packInventory();
      }

      public boolean isUseableByPlayer(EntityPlayer player) {
         return this.bagitem == this.player.getHeldItem();
      }

      public void openInventory() {
      }

      public void closeInventory() {
      }

      public boolean hasCustomInventoryName() {
         return false;
      }

      public boolean isItemValidForSlot(int slotID, ItemStack stack) {
         return this.bagitem != null && stack.getItem() != ItemBag.this;
      }
   }
}
