package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TubeFlow {
    public LinkedList<TubeItem> contents = new LinkedList();

    public abstract boolean schedule(TubeItem var1, TubeFlow.TubeScheduleContext var2);

    public boolean handleItem(TubeItem item, TubeFlow.TubeScheduleContext context) {
        return false;
    }

    public abstract TileEntity getParent();

    public boolean update() {
        boolean hasChanged = false;
        if (this.contents.size() == 0) {
            return false;
        } else {
            TubeFlow.TubeScheduleContext tsc
                = new TubeFlow.TubeScheduleContext(this.getParent());
            tsc.tii = this.contents.iterator();

            while (tsc.tii.hasNext()) {
                TubeItem tubeItem = (TubeItem) tsc.tii.next();
                tubeItem.progress = (short) (tubeItem.progress + tubeItem.power + 16);
                if (tubeItem.progress >= 128) {
                    if (tubeItem.power > 0) {
                        --tubeItem.power;
                    }

                    hasChanged = true;
                    if (!tubeItem.scheduled) {
                        if (!this.schedule(tubeItem, tsc)) {
                            tsc.tii.remove();
                        }
                    } else {
                        tsc.tii.remove();
                        if (!tsc.world.isRemote) {
                            tsc.tir.add(tubeItem);
                        }
                    }
                }
            }

            if (tsc.world.isRemote) {}

            for (TubeItem ti : tsc.tir) {
                if (ti.side >= 0 && (tsc.cons & 1 << ti.side) != 0) {
                    tsc.dest = tsc.wc.copy();
                    tsc.dest.step(ti.side);
                    ITubeConnectable itc
                        = CoreLib
                              .getTileEntity(tsc.world, tsc.dest, ITubeConnectable.class);
                    if (itc instanceof ITubeFlow) {
                        ITubeFlow itf = (ITubeFlow) itc;
                        itf.addTubeItem(ti);
                    } else if ((itc == null || !itc.tubeItemEnter((ti.side ^ 1) & 63, ti.mode, ti)) && !this.handleItem(ti, tsc)) {
                        ti.progress = 0;
                        ti.scheduled = false;
                        ti.mode = 2;
                        this.contents.add(ti);
                    }
                } else if (tsc.cons == 0) {
                    MachineLib.ejectItem(tsc.world, tsc.wc, ti.item, 1);
                } else {
                    ti.side = (byte) Integer.numberOfTrailingZeros(tsc.cons);
                    ti.progress = 128;
                    ti.scheduled = false;
                    this.contents.add(ti);
                    hasChanged = true;
                }
            }

            return hasChanged;
        }
    }

    public void add(TubeItem ti) {
        ti.progress = 0;
        ti.scheduled = false;
        this.contents.add(ti);
    }

    public void onRemove() {
        TileEntity te = this.getParent();

        for (TubeItem ti : this.contents) {
            if (ti != null && ti.item.stackSize > 0) {
                CoreLib.dropItem(
                    te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, ti.item
                );
            }
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagList items = tag.getTagList("Items", 10);
        if (items.tagCount() > 0) {
            this.contents = new LinkedList();

            for (int i = 0; i < items.tagCount(); ++i) {
                NBTTagCompound item = items.getCompoundTagAt(i);
                this.contents.add(TubeItem.newFromNBT(item));
            }
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList items = new NBTTagList();
        if (this.contents != null) {
            for (TubeItem ti : this.contents) {
                NBTTagCompound item = new NBTTagCompound();
                ti.writeToNBT(item);
                items.appendTag(item);
            }
        }

        tag.setTag("Items", items);
    }

    public static class TubeScheduleContext {
        public World world;
        public WorldCoord wc;
        public int cons;
        public List<TubeItem> tir = new ArrayList();
        public Iterator<TubeItem> tii;
        public WorldCoord dest = null;

        public TubeScheduleContext(TileEntity te) {
            this.world = te.getWorldObj();
            this.wc = new WorldCoord(te);
            this.cons
                = TubeLib.getConnections(this.world, this.wc.x, this.wc.y, this.wc.z);
        }
    }
}
