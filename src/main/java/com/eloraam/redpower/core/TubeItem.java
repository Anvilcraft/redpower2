package com.eloraam.redpower.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TubeItem {
    public short progress = 0;
    public byte mode = 1;
    public byte side;
    public byte color = 0;
    public short power = 0;
    public boolean scheduled = false;
    public short priority = 0;
    public ItemStack item;

    public TubeItem() {}

    public TubeItem(int s, ItemStack stk) {
        this.item = stk;
        this.side = (byte) s;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.item = ItemStack.loadItemStackFromNBT(nbt);
        this.side = nbt.getByte("side");
        this.progress = nbt.getShort("pos");
        this.mode = nbt.getByte("mode");
        this.color = nbt.getByte("col");
        this.priority = nbt.getShort("prio");
        if (this.progress < 0) {
            this.scheduled = true;
            this.progress = (short) (-this.progress - 1);
        }

        this.power = (short) (nbt.getByte("pow") & 255);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        this.item.writeToNBT(nbt);
        nbt.setByte("side", this.side);
        nbt.setShort(
            "pos", (short) (this.scheduled ? -this.progress - 1 : this.progress)
        );
        nbt.setByte("mode", this.mode);
        nbt.setByte("col", this.color);
        nbt.setByte("pow", (byte) this.power);
        nbt.setShort("prio", this.priority);
    }

    public static TubeItem newFromNBT(NBTTagCompound nbt) {
        TubeItem ti = new TubeItem();
        ti.readFromNBT(nbt);
        return ti;
    }

    public void readFromPacket(NBTTagCompound tag) {
        this.side = tag.getByte("side");
        this.progress = (short) tag.getByte("pos");
        if (this.progress < 0) {
            this.scheduled = true;
            this.progress = (short) (-this.progress - 1);
        }

        this.color = tag.getByte("col");
        this.power = (short) tag.getByte("pow");
        this.item = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.getTag("itm"));
    }

    public void writeToPacket(NBTTagCompound tag) {
        tag.setByte("side", this.side);
        tag.setByte(
            "pos",
            (byte) ((int
            ) (this.scheduled ? (long) (-this.progress - 1) : (long) this.progress))
        );
        tag.setByte("col", this.color);
        tag.setByte("pow", (byte) this.power);
        NBTTagCompound itag = new NBTTagCompound();
        this.item.writeToNBT(itag);
        tag.setTag("itm", itag);
    }

    public static TubeItem newFromPacket(NBTTagCompound tag) {
        TubeItem ti = new TubeItem();
        ti.readFromPacket(tag);
        return ti;
    }
}
