package com.eloraam.redpower.logic;

import java.util.List;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.google.common.primitives.Longs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerTimer extends Container implements IHandleGuiEvent {
   private long interval = 0L;
   private TileLogicPointer tileLogic;
   private short[] tmp = new short[4];
   private int tmpcounter;

   public ContainerTimer(IInventory inv, TileLogicPointer tf) {
      this.tileLogic = tf;
   }

   public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
      return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileLogic.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      return null;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();
      long iv = this.tileLogic.getInterval();

      for(ICrafting ic : (List<ICrafting>)super.crafters) {
         if (iv != this.interval) {
            ic.sendProgressBarUpdate(this, 0, (short)((int)(iv >> 48 & 32767L)));
            ic.sendProgressBarUpdate(this, 1, (short)((int)(iv >> 32 & 32767L)));
            ic.sendProgressBarUpdate(this, 2, (short)((int)(iv >> 16 & 32767L)));
            ic.sendProgressBarUpdate(this, 3, (short)((int)(iv & 32767L)));
         }
      }

      this.interval = iv;
   }

   public void updateProgressBar(int id, int value) {
      this.tmp[id] = (short)value;
      if (this.tmpcounter++ >= 3) {
         this.tileLogic.setInterval((long)this.tmp[0] << 48 | (long)this.tmp[1] << 32 | (long)this.tmp[2] << 16 | (long)this.tmp[3]);
         this.tmp[0] = this.tmp[1] = this.tmp[2] = this.tmp[3] = 0;
         this.tmpcounter = 0;
      }

   }

   @Override
   public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
      try {
         switch(message.eventId) {
            case 1:
               long i = Longs.fromByteArray(message.parameters);
               this.tileLogic.setInterval(i);
               if (this.tileLogic.getWorldObj() != null) {
                  this.tileLogic.updateBlock();
               }
         }
      } catch (Throwable var4) {
      }

   }
}
