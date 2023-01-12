package com.eloraam.redpower.wiring;

import java.util.List;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MicroPlacementWire implements IMicroPlacement {
    private void blockUsed(World world, WorldCoord wc, ItemStack ist) {
        --ist.stackSize;
        CoreLib.placeNoise(
            world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem())
        );
        world.markBlockForUpdate(wc.x, wc.y, wc.z);
        RedPowerLib.updateIndirectNeighbors(
            world, wc.x, wc.y, wc.z, Block.getBlockFromItem(ist.getItem())
        );
    }

    private boolean
    initialPlace(ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int l) {
        int md = ist.getItemDamage() >> 8;
        Block bid = Block.getBlockFromItem(ist.getItem());
        if (!world.canPlaceEntityOnSide(bid, wc.x, wc.y, wc.z, false, l, player, null)) {
            return false;
        } else if (!RedPowerLib.canSupportWire(world, wc.x, wc.y, wc.z, l ^ 1)) {
            return false;
        } else if (!world.setBlock(wc.x, wc.y, wc.z, bid, md, 3)) {
            return true;
        } else {
            TileWiring tw = CoreLib.getTileEntity(world, wc, TileWiring.class);
            if (tw == null) {
                return false;
            } else {
                tw.ConSides = 1 << (l ^ 1);
                tw.Metadata = ist.getItemDamage() & 0xFF;
                this.blockUsed(world, wc, ist);
                return true;
            }
        }
    }

    @Override
    public boolean onPlaceMicro(
        ItemStack ist, EntityPlayer player, World world, WorldCoord wc, int size
    ) {
        wc.step(size);
        Block bid = world.getBlock(wc.x, wc.y, wc.z);
        if (bid != Block.getBlockFromItem(ist.getItem())) {
            return this.initialPlace(ist, player, world, wc, size);
        } else {
            TileCovered tc = CoreLib.getTileEntity(world, wc, TileCovered.class);
            if (tc == null) {
                return false;
            } else {
                int d = 1 << (size ^ 1);
                if ((tc.CoverSides & d) > 0) {
                    return false;
                } else {
                    int hb = ist.getItemDamage();
                    if (!CoverLib.tryMakeCompatible(
                            world, wc, Block.getBlockFromItem(ist.getItem()), hb
                        )) {
                        return false;
                    } else {
                        TileWiring tw
                            = CoreLib.getTileEntity(world, wc, TileWiring.class);
                        if (tw == null) {
                            return false;
                        } else if (!RedPowerLib.canSupportWire(
                                       world, wc.x, wc.y, wc.z, size ^ 1
                                   )) {
                            return false;
                        } else if (((tw.ConSides | tw.CoverSides) & d) > 0) {
                            return false;
                        } else {
                            d |= tw.ConSides;
                            int t = d & 63;
                            if (t == 3 || t == 12 || t == 48) {
                                return false;
                            } else if (!CoverLib.checkPlacement(
                                           tw.CoverSides,
                                           tw.Covers,
                                           t,
                                           (tw.ConSides & 64) > 0
                                       )) {
                                return false;
                            } else {
                                tw.ConSides = d;
                                tw.uncache();
                                this.blockUsed(world, wc, ist);
                                return true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getMicroName(int hb, int lb) {
        switch (hb) {
            case 1:
                switch (lb) {
                    case 0:
                        return "tile.rpwire";
                    default:
                        return null;
                }
            case 2:
                return "tile.rpinsulated." + CoreLib.rawColorNames[lb];
            case 3:
                switch (lb) {
                    case 0:
                        return "tile.rpcable";
                    default:
                        return "tile.rpcable." + CoreLib.rawColorNames[lb - 1];
                }
            case 4:
            default:
                break;
            case 5:
                switch (lb) {
                    case 0:
                        return "tile.bluewire";
                    case 1:
                        return "tile.bluewire10";
                    case 2:
                        return "tile.bluewire1M";
                }
        }

        return null;
    }

    @Override
    public void addCreativeItems(int hb, CreativeTabs tab, List<ItemStack> items) {
        if (tab == CreativeExtraTabs.tabWires || tab == CreativeTabs.tabAllSearch) {
            switch (hb) {
                case 1:
                    items.add(new ItemStack(CoverLib.blockCoverPlate, 1, 256));
                    break;
                case 2:
                    for (int i = 0; i < 16; ++i) {
                        items.add(new ItemStack(CoverLib.blockCoverPlate, 1, 512 + i));
                    }

                    return;
                case 3:
                    for (int i = 0; i < 17; ++i) {
                        items.add(new ItemStack(CoverLib.blockCoverPlate, 1, 768 + i));
                    }
                case 4:
                default:
                    break;
                case 5:
                    items.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1280));
                    items.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1281));
                    items.add(new ItemStack(CoverLib.blockCoverPlate, 1, 1282));
            }
        }
    }
}
