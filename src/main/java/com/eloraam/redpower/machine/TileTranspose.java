package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.eloraam.redpower.core.ITubeConnectable;
import com.eloraam.redpower.core.MachineLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeBuffer;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileTranspose extends TileMachine implements ITubeConnectable {
    TubeBuffer buffer = new TubeBuffer();

    @Override
    public int getTubeConnectableSides() {
        return 3 << (super.Rotation & -2);
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        if (side == super.Rotation && state == 2) {
            this.buffer.addBounce(item);
            super.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            return true;
        } else if (side != (super.Rotation ^ 1) || state != 1) {
            return false;
        } else if (super.Powered) {
            return false;
        } else if (!this.buffer.isEmpty()) {
            return false;
        } else {
            this.addToBuffer(item.item);
            super.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            this.drainBuffer();
            return true;
        }
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        return side == super.Rotation && state == 2
            || side == (super.Rotation ^ 1) && state == 1 && this.buffer.isEmpty()
            && !super.Powered;
    }

    @Override
    public int tubeWeight(int side, int state) {
        return side == super.Rotation && state == 2 ? this.buffer.size() : 0;
    }

    protected void addToBuffer(ItemStack ist) {
        this.buffer.addNew(ist);
    }

    public boolean canSuck(int i, int j, int k) {
        if (super.worldObj.getBlock(i, j, k).isSideSolid(
                super.worldObj, i, j, k, ForgeDirection.getOrientation(super.Rotation)
            )) {
            return false;
        } else {
            TileEntity te = super.worldObj.getTileEntity(i, j, k);
            return te == null
                || !(te instanceof IInventory) && !(te instanceof ITubeConnectable);
        }
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        if (RedPowerLib.isPowered(
                super.worldObj, super.xCoord, super.yCoord, super.zCoord, 16777215, 63
            )) {
            if (!super.Powered) {
                super.Powered = true;
                this.markDirty();
                if (!super.Active) {
                    super.Active = true;
                    WorldCoord wc
                        = new WorldCoord(super.xCoord, super.yCoord, super.zCoord);
                    wc.step(super.Rotation ^ 1);
                    if (this.canSuck(wc.x, wc.y, wc.z)) {
                        this.doSuck();
                        this.updateBlock();
                    } else if (this.handleExtract(wc)) {
                        this.updateBlock();
                    }
                }
            }
        } else {
            if (super.Active && !this.isTickScheduled()) {
                this.scheduleTick(5);
            }

            super.Powered = false;
            this.markDirty();
        }
    }

    protected IInventory getConnectedInventory(boolean push) {
        WorldCoord pos = new WorldCoord(this);
        pos.step(super.Rotation ^ 1);
        return MachineLib.getSideInventory(super.worldObj, pos, super.Rotation, push);
    }

    protected boolean handleExtract(WorldCoord wc) {
        IInventory inv = MachineLib.getInventory(super.worldObj, wc);
        if (inv == null) {
            return false;
        } else {
            int[] slots;
            if (inv instanceof ISidedInventory) {
                ISidedInventory isi = (ISidedInventory) inv;
                slots = isi.getAccessibleSlotsFromSide(super.Rotation);
            } else {
                slots = IntStream.range(0, inv.getSizeInventory()).toArray();
            }

            return this.handleExtract(inv, slots);
        }
    }

    protected boolean handleExtract(IInventory inv, int[] slots) {
        for (int n : slots) {
            ItemStack ist = inv.getStackInSlot(n);
            if (ist != null && ist.stackSize != 0) {
                this.addToBuffer(inv.decrStackSize(n, 1));
                this.drainBuffer();
                return true;
            }
        }

        return false;
    }

    protected boolean handleExtract(IInventory inv, List<Integer> slots) {
        for (int n : slots) {
            ItemStack ist = inv.getStackInSlot(n);
            if (ist != null && ist.stackSize != 0) {
                this.addToBuffer(inv.decrStackSize(n, 1));
                this.drainBuffer();
                return true;
            }
        }

        return false;
    }

    protected AxisAlignedBB getSizeBox(double bw, double bf, double bb) {
        double fx = (double) super.xCoord + 0.5;
        double fy = (double) super.yCoord + 0.5;
        double fz = (double) super.zCoord + 0.5;
        switch (super.Rotation) {
            case 0:
                return AxisAlignedBB.getBoundingBox(
                    fx - bw,
                    (double) super.yCoord - bb,
                    fz - bw,
                    fx + bw,
                    (double) super.yCoord + bf,
                    fz + bw
                );
            case 1:
                return AxisAlignedBB.getBoundingBox(
                    fx - bw,
                    (double) (super.yCoord + 1) - bf,
                    fz - bw,
                    fx + bw,
                    (double) (super.yCoord + 1) + bb,
                    fz + bw
                );
            case 2:
                return AxisAlignedBB.getBoundingBox(
                    fx - bw,
                    fy - bw,
                    (double) super.zCoord - bb,
                    fx + bw,
                    fy + bw,
                    (double) super.zCoord + bf
                );
            case 3:
                return AxisAlignedBB.getBoundingBox(
                    fx - bw,
                    fy - bw,
                    (double) (super.zCoord + 1) - bf,
                    fx + bw,
                    fy + bw,
                    (double) (super.zCoord + 1) + bb
                );
            case 4:
                return AxisAlignedBB.getBoundingBox(
                    (double) super.xCoord - bb,
                    fy - bw,
                    fz - bw,
                    (double) super.xCoord + bf,
                    fy + bw,
                    fz + bw
                );
            default:
                return AxisAlignedBB.getBoundingBox(
                    (double) (super.xCoord + 1) - bf,
                    fy - bw,
                    fz - bw,
                    (double) (super.xCoord + 1) + bb,
                    fy + bw,
                    fz + bw
                );
        }
    }

    protected void doSuck() {
        this.suckEntities(this.getSizeBox(1.55, 3.05, -0.95));
    }

    protected boolean suckFilter(ItemStack ist) {
        return true;
    }

    protected int suckEntity(Entity ent) {
        if (ent instanceof EntityItem) {
            EntityItem em1 = (EntityItem) ent;
            ItemStack ist = em1.getEntityItem();
            if (ist.stackSize == 0 || em1.isDead) {
                return 0;
            } else if (!this.suckFilter(ist)) {
                return 0;
            } else {
                this.addToBuffer(ist);
                em1.setDead();
                return 1;
            }
        } else {
            if (ent instanceof EntityMinecartContainer) {
                if (super.Active) {
                    return 0;
                }

                EntityMinecartContainer em = (EntityMinecartContainer) ent;
                List<Integer> slots = new ArrayList(em.getSizeInventory());

                for (int i = 0; i < em.getSizeInventory(); ++i) {
                    slots.add(i);
                }

                if (this.handleExtract(em, slots)) {
                    return 2;
                }
            }

            return 0;
        }
    }

    protected void suckEntities(AxisAlignedBB bb) {
        boolean trig = false;

        for (Entity ent :
             (List<Entity>) super.worldObj.getEntitiesWithinAABB(Entity.class, bb)) {
            int i = this.suckEntity(ent);
            if (i != 0) {
                trig = true;
                if (i == 2) {
                    break;
                }
            }
        }

        if (trig) {
            if (!super.Active) {
                super.Active = true;
                this.updateBlock();
            }

            this.drainBuffer();
            this.scheduleTick(5);
        }
    }

    public boolean stuffCart(ItemStack ist) {
        WorldCoord wc = new WorldCoord(this);
        wc.step(super.Rotation);
        Block bl = super.worldObj.getBlock(wc.x, wc.y, wc.z);
        if (!(bl instanceof BlockRail)) {
            return false;
        } else {
            for (EntityMinecartContainer em :
                 (List<EntityMinecartContainer>) super.worldObj.getEntitiesWithinAABB(
                     EntityMinecartContainer.class, this.getSizeBox(0.8, 0.05, 1.05)
                 )) {
                int[] slots = IntStream.range(0, em.getSizeInventory()).toArray();
                if (MachineLib.addToInventoryCore(em, ist, slots, true)) {
                    return true;
                }
            }

            return false;
        }
    }

    public void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (this.stuffCart(ti.item)) {
                this.buffer.pop();
            } else {
                if (!this.handleItem(ti)) {
                    this.buffer.plugged = true;
                    return;
                }

                this.buffer.pop();
                if (this.buffer.plugged) {
                    return;
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getSizeBox(0.5, 0.95, 0.0);
    }

    @Override
    public void onEntityCollidedWithBlock(Entity ent) {
        if (!super.worldObj.isRemote && !super.Powered && this.buffer.isEmpty()) {
            this.suckEntities(this.getSizeBox(0.55, 1.05, -0.95));
        }
    }

    @Override
    public void onBlockRemoval() {
        this.buffer.onRemove(this);
    }

    @Override
    public void onTileTick() {
        if (!super.worldObj.isRemote) {
            if (!this.buffer.isEmpty()) {
                this.drainBuffer();
                if (!this.buffer.isEmpty()) {
                    this.scheduleTick(10);
                } else {
                    this.scheduleTick(5);
                }
            } else if (!super.Powered) {
                super.Active = false;
                this.updateBlock();
            }
        }
    }

    @Override
    public int getExtendedID() {
        return 2;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.buffer.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        this.buffer.writeToNBT(data);
    }
}
