package com.eloraam.redpower.base;

import java.util.List;

import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerBusId extends Container implements IHandleGuiEvent {
    private IRedbusConnectable rbConn;
    private int addr = 0;

    public ContainerBusId(IInventory inv, IRedbusConnectable irc) {
        this.rbConn = irc;
    }

    public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
        return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
    }

    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        return null;
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (ICrafting ic : (List<ICrafting>) super.crafters) {
            if (this.rbConn.rbGetAddr() != this.addr) {
                ic.sendProgressBarUpdate(this, 0, this.rbConn.rbGetAddr());
            }
        }

        this.addr = this.rbConn.rbGetAddr();
    }

    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                this.rbConn.rbSetAddr(value);
                return;
        }
    }

    @Override
    public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
        try {
            if (message.eventId != 1) {
                return;
            }

            this.rbConn.rbSetAddr(message.parameters[0]);
        } catch (Throwable var3) {}
    }
}
