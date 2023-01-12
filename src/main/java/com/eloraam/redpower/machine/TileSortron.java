package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BluePowerConductor;
import com.eloraam.redpower.core.BluePowerEndpoint;
import com.eloraam.redpower.core.IBluePowerConnectable;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TubeItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileSortron
    extends TileTranspose implements IBluePowerConnectable, IRedbusConnectable {
    BluePowerEndpoint cond = new BluePowerEndpoint() {
        @Override
        public TileEntity getParent() {
            return TileSortron.this;
        }
    };
    public int ConMask = -1;
    private int rbaddr = 4;
    private int cmdDelay = 0;
    private int command = 0;
    private int itemSlot = 0;
    private int itemType = 0;
    private int itemDamage = 0;
    private int itemDamageMax = 0;
    private int itemQty = 0;
    private int itemColor = 0;
    private int itemInColor = 0;

    @Override
    public int getConnectableMask() {
        return 1073741823;
    }

    @Override
    public int getConnectClass(int side) {
        return 67;
    }

    @Override
    public int getCornerPowerMode() {
        return 0;
    }

    @Override
    public BluePowerConductor getBlueConductor(int side) {
        return this.cond;
    }

    @Override
    public int rbGetAddr() {
        return this.rbaddr;
    }

    @Override
    public void rbSetAddr(int addr) {
        this.rbaddr = addr;
    }

    @Override
    public int rbRead(int reg) {
        switch (reg) {
            case 0:
                return this.command & 0xFF;
            case 1:
                return this.itemQty & 0xFF;
            case 2:
                return this.itemSlot & 0xFF;
            case 3:
                return this.itemSlot >> 8 & 0xFF;
            case 4:
                return this.itemType & 0xFF;
            case 5:
                return this.itemType >> 8 & 0xFF;
            case 6:
                return this.itemType >> 16 & 0xFF;
            case 7:
                return this.itemType >> 24 & 0xFF;
            case 8:
                return this.itemDamage & 0xFF;
            case 9:
                return this.itemDamage >> 8 & 0xFF;
            case 10:
                return this.itemDamageMax & 0xFF;
            case 11:
                return this.itemDamageMax >> 8 & 0xFF;
            case 12:
                return this.itemColor & 0xFF;
            case 13:
                return this.itemInColor & 0xFF;
            default:
                return 0;
        }
    }

    @Override
    public void rbWrite(int reg, int dat) {
        this.markDirty();
        switch (reg) {
            case 0:
                this.command = dat;
                this.cmdDelay = 2;
                break;
            case 1:
                this.itemQty = dat;
                break;
            case 2:
                this.itemSlot = this.itemSlot & 0xFF00 | dat;
                break;
            case 3:
                this.itemSlot = this.itemSlot & 0xFF | dat << 8;
                break;
            case 4:
                this.itemType = this.itemType & -256 | dat;
                break;
            case 5:
                this.itemType = this.itemType & -65281 | dat << 8;
                break;
            case 6:
                this.itemType = this.itemType & -16711681 | dat << 16;
                break;
            case 7:
                this.itemType = this.itemType & 16777215 | dat << 24;
                break;
            case 8:
                this.itemDamage = this.itemDamage & 0xFF00 | dat;
                break;
            case 9:
                this.itemDamage = this.itemDamage & 0xFF | dat << 8;
                break;
            case 10:
                this.itemDamageMax = this.itemDamageMax & 0xFF00 | dat;
                break;
            case 11:
                this.itemDamageMax = this.itemDamageMax & 0xFF | dat << 8;
                break;
            case 12:
                this.itemColor = dat;
                break;
            case 13:
                this.itemInColor = dat;
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!super.worldObj.isRemote) {
            if (this.ConMask < 0) {
                this.ConMask = RedPowerLib.getConnections(
                    super.worldObj, this, super.xCoord, super.yCoord, super.zCoord
                );
                this.cond.recache(this.ConMask, 0);
            }

            this.cond.iterate();
            this.markDirty();
            if (this.cmdDelay > 0 && --this.cmdDelay == 0) {
                this.processCommand();
            }

            if (this.cond.Flow == 0) {
                if (super.Charged) {
                    super.Charged = false;
                    this.updateBlock();
                }
            } else if (!super.Charged) {
                super.Charged = true;
                this.updateBlock();
            }
        }
    }

    @Override
    public Block getBlockType() {
        return RedPowerMachine.blockMachine2;
    }

    @Override
    public int getExtendedID() {
        return 0;
    }

    @Override
    public void onBlockNeighborChange(Block block) {
        this.ConMask = -1;
    }

    @Override
    public void onTileTick() {
        if (!super.worldObj.isRemote && super.Active) {
            if (!super.buffer.isEmpty()) {
                this.drainBuffer();
                if (!super.buffer.isEmpty()) {
                    this.scheduleTick(10);
                } else {
                    this.scheduleTick(5);
                }
            } else {
                super.Active = false;
                this.updateBlock();
            }
        }
    }

    public static int hashItem(ItemStack ist) {
        String in = ist.getItem().getUnlocalizedName();
        int hc;
        if (in == null) {
            hc = ist.getItem().hashCode();
        } else {
            hc = in.hashCode();
        }

        if (ist.getHasSubtypes()) {
            hc ^= ist.getItemDamage();
        }

        return hc;
    }

    private void processCommand() {
        if (this.cond.getVoltage() < 60.0) {
            this.cmdDelay = 20;
        } else {
            IInventory inv;
            switch (this.command) {
                case 0:
                    break;
                case 1:
                    inv = this.getConnectedInventory(false);
                    if (inv == null) {
                        this.command = 255;
                    } else {
                        this.itemSlot = inv.getSizeInventory();
                        this.command = 0;
                    }
                    break;
                case 2:
                    inv = this.getConnectedInventory(false);
                    if (inv == null) {
                        this.command = 255;
                    } else if (this.itemSlot >= inv.getSizeInventory()) {
                        this.command = 255;
                    } else {
                        ItemStack ist = inv.getStackInSlot(this.itemSlot);
                        if (ist != null && ist.stackSize != 0) {
                            this.itemQty = ist.stackSize;
                            this.itemType = hashItem(ist);
                            if (ist.isItemStackDamageable()) {
                                this.itemDamage = ist.getItemDamage();
                                this.itemDamageMax = ist.getMaxDamage();
                            } else {
                                this.itemDamage = 0;
                                this.itemDamageMax = 0;
                            }

                            this.command = 0;
                        } else {
                            this.itemQty = 0;
                            this.itemType = 0;
                            this.itemDamage = 0;
                            this.itemDamageMax = 0;
                            this.command = 0;
                        }
                    }
                    break;
                case 3:
                    if (super.Active) {
                        this.cmdDelay = 2;
                        return;
                    }

                    inv = this.getConnectedInventory(false);
                    if (inv == null) {
                        this.command = 255;
                    } else if (this.itemSlot >= inv.getSizeInventory()) {
                        this.command = 255;
                    } else {
                        ItemStack ist = inv.getStackInSlot(this.itemSlot);
                        if (ist != null && ist.stackSize != 0) {
                            int i = Math.min(this.itemQty, ist.stackSize);
                            this.itemQty = i;
                            if (this.itemColor > 16) {
                                this.itemColor = 0;
                            }

                            super.buffer.addNewColor(
                                inv.decrStackSize(this.itemSlot, i), this.itemColor
                            );
                            this.cond.drawPower((double) (50 * ist.stackSize));
                            this.drainBuffer();
                            super.Active = true;
                            this.command = 0;
                            this.updateBlock();
                            this.scheduleTick(5);
                        } else {
                            this.itemQty = 0;
                            this.command = 0;
                        }
                    }
                    break;
                case 4:
                    if (this.itemQty == 0) {
                        this.command = 0;
                    }
                    break;
                default:
                    this.command = 255;
            }
        }
    }

    @Override
    protected boolean handleExtract(IInventory inv, int[] slots) {
        return false;
    }

    @Override
    protected void addToBuffer(ItemStack ist) {
        if (this.itemColor > 16) {
            this.itemColor = 0;
        }

        super.buffer.addNewColor(ist, this.itemColor);
    }

    @Override
    protected int suckEntity(Entity ent) {
        if (ent instanceof EntityItem) {
            EntityItem ei = (EntityItem) ent;
            ItemStack ist = ei.getEntityItem();
            if (ist.stackSize != 0 && !ei.isDead) {
                int st = ist.stackSize;
                if (!this.suckFilter(ist)) {
                    return st == ist.stackSize ? 0 : 2;
                } else {
                    this.addToBuffer(ist);
                    ei.setDead();
                    return 1;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    protected boolean suckFilter(ItemStack ist) {
        if (this.command != 4) {
            return false;
        } else if (this.cond.getVoltage() < 60.0) {
            return false;
        } else if (this.itemType != 0 && this.itemType != hashItem(ist)) {
            return false;
        } else {
            boolean tr = true;
            if (this.itemQty < ist.stackSize) {
                tr = false;
                ist = ist.splitStack(this.itemQty);
                if (this.itemColor > 16) {
                    this.itemColor = 0;
                }

                super.buffer.addNewColor(ist, this.itemColor);
            }

            this.itemQty -= ist.stackSize;
            if (this.itemQty == 0) {
                this.command = 0;
            }

            this.cond.drawPower((double) (50 * ist.stackSize));
            return tr;
        }
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        if (side == super.Rotation && state == 2) {
            return super.tubeItemEnter(side, state, item);
        } else if (side != (super.Rotation ^ 1) || state != 1) {
            return false;
        } else if (this.command != 4) {
            return false;
        } else if (this.cond.getVoltage() < 60.0) {
            return false;
        } else if (this.itemType != 0 && this.itemType != hashItem(item.item)) {
            return false;
        } else if (this.itemInColor != 0 && this.itemInColor != item.color) {
            return false;
        } else {
            boolean tr = true;
            ItemStack ist = item.item;
            if (this.itemQty < ist.stackSize) {
                tr = false;
                ist = ist.splitStack(this.itemQty);
            }

            this.itemQty -= ist.stackSize;
            if (this.itemQty == 0) {
                this.command = 0;
            }

            if (this.itemColor > 16) {
                this.itemColor = 0;
            }

            super.buffer.addNewColor(ist, this.itemColor);
            this.cond.drawPower((double) (50 * ist.stackSize));
            this.drainBuffer();
            super.Active = true;
            this.updateBlock();
            this.scheduleTick(5);
            return tr;
        }
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        return side == super.Rotation && state == 2
            || side == (super.Rotation ^ 1) && state == 1 && this.command == 4
            && this.cond.getVoltage() >= 60.0
            && (this.itemType == 0 || this.itemType == hashItem(item.item))
            && (this.itemInColor == 0 || this.itemInColor == item.color);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.cond.readFromNBT(data);
        this.rbaddr = data.getByte("rbaddr") & 255;
        this.cmdDelay = data.getByte("cmddelay") & 255;
        this.command = data.getByte("cmd") & 255;
        this.itemSlot = data.getShort("itemslot");
        this.itemType = data.getInteger("itemtype");
        this.itemDamage = data.getShort("itemdmg");
        this.itemDamageMax = data.getShort("itemdmgmax");
        this.itemQty = data.getByte("itemqty") & 255;
        this.itemInColor = data.getByte("itemincolor") & 255;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        this.cond.writeToNBT(data);
        data.setByte("rbaddr", (byte) this.rbaddr);
        data.setByte("cmddelay", (byte) this.cmdDelay);
        data.setByte("cmd", (byte) this.command);
        data.setShort("itemslot", (short) this.itemSlot);
        data.setInteger("itemtype", this.itemType);
        data.setShort("itemdmg", (short) this.itemDamage);
        data.setShort("itemdmgmax", (short) this.itemDamageMax);
        data.setByte("itemqty", (byte) this.itemQty);
        data.setByte("itemcolor", (byte) this.itemColor);
        data.setByte("itemincolor", (byte) this.itemInColor);
    }
}
