package com.eloraam.redpower.base;

import com.eloraam.redpower.core.CoreLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotCraftRefill extends SlotCrafting {
    private IInventory inv;
    private IInventory craftingMatrix;
    private ContainerAdvBench bench;

    public SlotCraftRefill(
        EntityPlayer player,
        IInventory matrix,
        IInventory result,
        IInventory all,
        ContainerAdvBench evh,
        int id,
        int x,
        int y
    ) {
        super(player, matrix, result, id, x, y);
        this.inv = all;
        this.craftingMatrix = matrix;
        this.bench = evh;
    }

    private int findMatch(ItemStack a) {
        for (int i = 0; i < 18; ++i) {
            ItemStack test = this.inv.getStackInSlot(10 + i);
            if (test != null && test.stackSize != 0
                && CoreLib.matchItemStackOre(a, test)) {
                return 10 + i;
            }
        }

        return -1;
    }

    public boolean isLastUse() {
        int bits = 0;

        for (int i = 0; i < 9; ++i) {
            ItemStack test = this.inv.getStackInSlot(i);
            if (test == null) {
                bits |= 1 << i;
            } else if (!test.isStackable()) {
                bits |= 1 << i;
            } else if (test.stackSize > 1) {
                bits |= 1 << i;
            }
        }

        if (bits == 511) {
            return false;
        } else {
            for (int i = 0; i < 18; ++i) {
                ItemStack test = this.inv.getStackInSlot(10 + i);
                if (test != null && test.stackSize != 0) {
                    int sc = test.stackSize;

                    for (int j = 0; j < 9; ++j) {
                        if ((bits & 1 << j) <= 0) {
                            ItemStack st = this.inv.getStackInSlot(j);
                            if (st != null && CoreLib.matchItemStackOre(st, test)) {
                                bits |= 1 << j;
                                if (--sc == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            return bits != 511;
        }
    }

    public void onPickupFromSlot(EntityPlayer player, ItemStack ist) {
        ItemStack[] plan = this.bench.getPlanItems();
        ItemStack[] cur = new ItemStack[9];

        for (int i = 0; i < 9; ++i) {
            ItemStack idx = this.inv.getStackInSlot(i);
            if (idx == null) {
                cur[i] = null;
            } else {
                cur[i] = idx.copy();
            }
        }

        boolean lastUse = this.isLastUse();
        if (plan != null) {
            for (int i = 0; i < 9; ++i) {
                if (cur[i] == null && plan[i] != null) {
                    int m = this.findMatch(plan[i]);
                    if (m >= 0) {
                        ItemStack ch = this.inv.getStackInSlot(m);
                        if (ch != null) {
                            this.inv.decrStackSize(m, 1);
                            if (ch.getItem().getContainerItem() != null) {
                                ItemStack s = ch.getItem().getContainerItem(ch);
                                this.inv.setInventorySlotContents(m, s);
                            }
                        }
                    }
                }
            }
        }

        super.onPickupFromSlot(player, ist);
        if (!lastUse) {
            for (int i = 0; i < 9; ++i) {
                if (cur[i] != null) {
                    ItemStack nsl = this.inv.getStackInSlot(i);
                    if (plan == null || plan[i] == null) {
                        if (nsl != null) {
                            if (!CoreLib.matchItemStackOre(nsl, cur[i])
                                && cur[i].getItem().getContainerItem() != null) {
                                ItemStack ctr = cur[i].getItem().getContainerItem(cur[i]);
                                if (ctr != null && ctr.getItem() == nsl.getItem()) {
                                    int id = this.findMatch(cur[i]);
                                    if (id >= 0) {
                                        this.inv.setInventorySlotContents(id, nsl);
                                    }
                                }
                            }
                        } else {
                            int id = this.findMatch(cur[i]);
                            if (id >= 0) {
                                this.inv.setInventorySlotContents(
                                    i, this.inv.decrStackSize(id, 1)
                                );
                            }
                        }
                    }
                }
            }
        }

        this.bench.onCraftMatrixChanged(this.craftingMatrix);
    }
}
