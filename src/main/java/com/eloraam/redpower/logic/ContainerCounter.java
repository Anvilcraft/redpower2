package com.eloraam.redpower.logic;

import java.util.List;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.google.common.primitives.Ints;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerCounter extends Container implements IHandleGuiEvent {
   private int Count = 0;
   private int CountMax = 0;
   private int Inc = 0;
   private int Dec = 0;
   private TileLogicStorage tileLogic;

   public ContainerCounter(IInventory inv, TileLogicStorage tf) {
      this.tileLogic = tf;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileLogic.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      return null;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();
      TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter)this.tileLogic
         .getLogicStorage(TileLogicStorage.LogicStorageCounter.class);

      for(ICrafting ic : (List<ICrafting>)super.crafters) {
         if (this.Count != lsc.Count) {
            ic.sendProgressBarUpdate(this, 0, lsc.Count);
         }

         if (this.CountMax != lsc.CountMax) {
            ic.sendProgressBarUpdate(this, 1, lsc.CountMax);
         }

         if (this.Inc != lsc.Inc) {
            ic.sendProgressBarUpdate(this, 2, lsc.Inc);
         }

         if (this.Dec != lsc.Dec) {
            ic.sendProgressBarUpdate(this, 3, lsc.Dec);
         }
      }

      this.Count = lsc.Count;
      this.CountMax = lsc.CountMax;
      this.Inc = lsc.Inc;
      this.Dec = lsc.Dec;
   }

   public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
      return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
   }

   public void updateProgressBar(int i, int j) {
      TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter)this.tileLogic
         .getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
      switch(i) {
         case 0:
            lsc.Count = j;
            break;
         case 1:
            lsc.CountMax = j;
            break;
         case 2:
            lsc.Inc = j;
            break;
         case 3:
            lsc.Dec = j;
      }

   }

   @Override
   public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
      TileLogicStorage.LogicStorageCounter lsc = (TileLogicStorage.LogicStorageCounter)this.tileLogic
         .getLogicStorage(TileLogicStorage.LogicStorageCounter.class);

      try {
         switch(message.eventId) {
            case 0:
               lsc.Count = Ints.fromByteArray(message.parameters);
               this.tileLogic.updateBlock();
               break;
            case 1:
               lsc.CountMax = Ints.fromByteArray(message.parameters);
               this.tileLogic.updateBlock();
               break;
            case 2:
               lsc.Inc = Ints.fromByteArray(message.parameters);
               this.tileLogic.markDirty();
               break;
            case 3:
               lsc.Dec = Ints.fromByteArray(message.parameters);
               this.tileLogic.markDirty();
         }
      } catch (Throwable var4) {
      }

   }
}
