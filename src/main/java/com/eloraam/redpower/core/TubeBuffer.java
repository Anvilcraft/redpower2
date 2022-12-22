package com.eloraam.redpower.core;

import java.util.Collection;
import java.util.LinkedList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TubeBuffer {
   LinkedList<TubeItem> buffer = null;
   public boolean plugged = false;

   public boolean isEmpty() {
      return this.buffer == null || this.buffer.size() == 0;
   }

   public TubeItem getLast() {
      return this.buffer == null ? null : (TubeItem)this.buffer.getLast();
   }

   public void add(TubeItem ti) {
      if (this.buffer == null) {
         this.buffer = new LinkedList();
      }

      this.buffer.addFirst(ti);
   }

   public void addNew(ItemStack ist) {
      if (this.buffer == null) {
         this.buffer = new LinkedList();
      }

      this.buffer.addFirst(new TubeItem(0, ist));
   }

   public void addNewColor(ItemStack ist, int col) {
      if (this.buffer == null) {
         this.buffer = new LinkedList();
      }

      TubeItem ti = new TubeItem(0, ist);
      ti.color = (byte)col;
      this.buffer.addFirst(ti);
   }

   public void addAll(Collection<ItemStack> col) {
      if (this.buffer == null) {
         this.buffer = new LinkedList();
      }

      for(ItemStack ist : col) {
         this.buffer.add(new TubeItem(0, ist));
      }

   }

   public void addBounce(TubeItem ti) {
      if (this.buffer == null) {
         this.buffer = new LinkedList();
      }

      this.buffer.addLast(ti);
      this.plugged = true;
   }

   public void pop() {
      this.buffer.removeLast();
      if (this.buffer.size() == 0) {
         this.plugged = false;
      }

   }

   public int size() {
      return this.buffer == null ? 0 : this.buffer.size();
   }

   public void onRemove(TileEntity te) {
      if (this.buffer != null) {
         for(TubeItem ti : this.buffer) {
            if (ti != null && ti.item.stackSize > 0) {
               CoreLib.dropItem(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, ti.item);
            }
         }
      }

   }

   public void readFromNBT(NBTTagCompound data) {
      NBTTagList items = data.getTagList("Buffer", 10);
      if (items.tagCount() > 0) {
         this.buffer = new LinkedList();

         for(int b = 0; b < items.tagCount(); ++b) {
            NBTTagCompound item = items.getCompoundTagAt(b);
            this.buffer.add(TubeItem.newFromNBT(item));
         }
      }

      byte var5 = data.getByte("Plug");
      this.plugged = var5 > 0;
   }

   public void writeToNBT(NBTTagCompound data) {
      NBTTagList items = new NBTTagList();
      if (this.buffer != null) {
         for(TubeItem ti : this.buffer) {
            NBTTagCompound item = new NBTTagCompound();
            ti.writeToNBT(item);
            items.appendTag(item);
         }
      }

      data.setTag("Buffer", items);
      data.setByte("Plug", (byte)(this.plugged ? 1 : 0));
   }
}
