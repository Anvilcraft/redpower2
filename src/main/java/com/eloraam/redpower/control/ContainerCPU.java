package com.eloraam.redpower.control;

import java.util.List;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerCPU extends Container implements IHandleGuiEvent {
   private TileCPU tileCPU;
   private int byte0 = 0;
   private int byte1 = 0;
   private int rbaddr = 0;
   private boolean isrun = false;

   public ContainerCPU(IInventory inv, TileCPU cpu) {
      this.tileCPU = cpu;
   }

   public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
      return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileCPU.isUseableByPlayer(player);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int i) {
      return null;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for(ICrafting ic : (List<ICrafting>)super.crafters) {
         if (this.tileCPU.diskAddr != this.byte0) {
            ic.sendProgressBarUpdate(this, 0, this.tileCPU.diskAddr);
         }

         if (this.tileCPU.displayAddr != this.byte1) {
            ic.sendProgressBarUpdate(this, 1, this.tileCPU.displayAddr);
         }

         if (this.tileCPU.rbaddr != this.rbaddr) {
            ic.sendProgressBarUpdate(this, 2, this.tileCPU.rbaddr);
         }

         if (this.tileCPU.isRunning() != this.isrun) {
            ic.sendProgressBarUpdate(this, 3, this.tileCPU.isRunning() ? 1 : 0);
         }
      }

      this.byte0 = this.tileCPU.diskAddr;
      this.byte1 = this.tileCPU.displayAddr;
      this.rbaddr = this.tileCPU.rbaddr;
      this.isrun = this.tileCPU.isRunning();
   }

   public void updateProgressBar(int id, int value) {
      switch(id) {
         case 0:
            this.tileCPU.diskAddr = value;
            break;
         case 1:
            this.tileCPU.displayAddr = value;
            break;
         case 2:
            this.tileCPU.rbaddr = value;
            break;
         case 3:
            this.tileCPU.sliceCycles = value > 0 ? 0 : -1;
      }

   }

   @Override
   public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
      try {
         switch(message.eventId) {
            case 1:
               this.tileCPU.diskAddr = message.parameters[0];
               break;
            case 2:
               this.tileCPU.displayAddr = message.parameters[0];
               break;
            case 3:
               this.tileCPU.rbaddr = message.parameters[0];
               break;
            case 4:
               this.tileCPU.warmBootCPU();
               break;
            case 5:
               this.tileCPU.haltCPU();
               break;
            case 6:
               this.tileCPU.coldBootCPU();
         }
      } catch (Throwable var3) {
      }

   }
}
