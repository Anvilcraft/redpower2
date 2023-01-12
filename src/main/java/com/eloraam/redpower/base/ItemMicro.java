package com.eloraam.redpower.base;

import java.util.List;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.ICoverable;
import com.eloraam.redpower.core.IMicroPlacement;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class ItemMicro extends ItemBlock {
    private IMicroPlacement[] placers = new IMicroPlacement[256];

    public ItemMicro(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    private boolean useCover(
        ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side
    ) {
        MovingObjectPosition pos = CoreLib.retraceBlock(world, player, x, y, z);
        if (pos == null) {
            return false;
        } else if (pos.typeOfHit != MovingObjectType.BLOCK) {
            return false;
        } else {
            pos = CoverLib.getPlacement(world, pos, ist.getItemDamage());
            if (pos == null) {
                return false;
            } else {
                Block oldBlock = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
                if (world.canPlaceEntityOnSide(
                        oldBlock,
                        pos.blockX,
                        pos.blockY,
                        pos.blockZ,
                        false,
                        side,
                        player,
                        ist
                    )) {
                    world.setBlock(
                        pos.blockX, pos.blockY, pos.blockZ, RedPowerBase.blockMicro, 0, 3
                    );
                }

                TileEntity te = world.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
                Block newBlock = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
                int newMeta = world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ);
                if (!(te instanceof ICoverable)) {
                    return false;
                } else {
                    ICoverable icv = (ICoverable) te;
                    PlayerInteractEvent event = new PlayerInteractEvent(
                        player,
                        Action.RIGHT_CLICK_BLOCK,
                        pos.blockX,
                        pos.blockY,
                        pos.blockZ,
                        side,
                        world
                    );
                    if (!MinecraftForge.EVENT_BUS.post(event)) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        te.writeToNBT(nbt);
                        BlockSnapshot snapshot = new BlockSnapshot(
                            world,
                            pos.blockX,
                            pos.blockY,
                            pos.blockZ,
                            newBlock,
                            newMeta,
                            nbt
                        );
                        PlaceEvent plvt = new PlaceEvent(snapshot, oldBlock, player);
                        if (!MinecraftForge.EVENT_BUS.post(plvt)) {
                            if (icv.tryAddCover(
                                    pos.subHit,
                                    CoverLib.damageToCoverValue(ist.getItemDamage())
                                )) {
                                if (!player.capabilities.isCreativeMode) {
                                    --ist.stackSize;
                                }

                                CoreLib.placeNoise(
                                    world,
                                    pos.blockX,
                                    pos.blockY,
                                    pos.blockZ,
                                    CoverLib.getBlock(ist.getItemDamage() & 0xFF)
                                );
                                RedPowerLib.updateIndirectNeighbors(
                                    world,
                                    pos.blockX,
                                    pos.blockY,
                                    pos.blockZ,
                                    RedPowerBase.blockMicro
                                );
                                world.markBlockForUpdate(
                                    pos.blockX, pos.blockY, pos.blockZ
                                );
                                return true;
                            }

                            return false;
                        }
                    }

                    return false;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean func_150936_a(
        World world, int x, int y, int z, int side, EntityPlayer player, ItemStack ist
    ) {
        return true;
    }

    public boolean onItemUse(
        ItemStack ist,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z,
        int side,
        float xp,
        float yp,
        float zp
    ) {
        return player != null && !player.isSneaking()
            && this.itemUseShared(ist, player, world, x, y, z, side);
    }

    public boolean onItemUseFirst(
        ItemStack ist,
        EntityPlayer player,
        World world,
        int x,
        int y,
        int z,
        int side,
        float xp,
        float yp,
        float zp
    ) {
        return !world.isRemote && player.isSneaking()
            && this.itemUseShared(ist, player, world, x, y, z, side);
    }

    private boolean itemUseShared(
        ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side
    ) {
        int hb = ist.getItemDamage();
        hb >>= 8;
        return hb != 0 && (hb < 16 || hb > 45)
            ? this.placers[hb] != null
                && this.placers[hb].onPlaceMicro(
                    ist, player, world, new WorldCoord(x, y, z), side
                )
            : this.useCover(ist, player, world, x, y, z, side);
    }

    private String getMicroName(int hb) {
        switch (hb) {
            case 0:
                return "rpcover";
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                return null;
            case 16:
                return "rppanel";
            case 17:
                return "rpslab";
            case 18:
                return "rpcovc";
            case 19:
                return "rppanc";
            case 20:
                return "rpslabc";
            case 21:
                return "rpcovs";
            case 22:
                return "rppans";
            case 23:
                return "rpslabs";
            case 24:
                return "rphcover";
            case 25:
                return "rphpanel";
            case 26:
                return "rphslab";
            case 27:
                return "rpcov3";
            case 28:
                return "rpcov5";
            case 29:
                return "rpcov6";
            case 30:
                return "rpcov7";
            case 31:
                return "rphcov3";
            case 32:
                return "rphcov5";
            case 33:
                return "rphcov6";
            case 34:
                return "rphcov7";
            case 35:
                return "rpcov3c";
            case 36:
                return "rpcov5c";
            case 37:
                return "rpcov6c";
            case 38:
                return "rpcov7c";
            case 39:
                return "rpcov3s";
            case 40:
                return "rpcov5s";
            case 41:
                return "rpcov6s";
            case 42:
                return "rpcov7s";
            case 43:
                return "rppole1";
            case 44:
                return "rppole2";
            case 45:
                return "rppole3";
        }
    }

    public String getUnlocalizedName(ItemStack ist) {
        int hb = ist.getItemDamage();
        int lb = hb & 0xFF;
        hb >>= 8;
        String stub = this.getMicroName(hb);
        if (stub != null) {
            String name = CoverLib.getName(lb);
            if (name == null) {
                throw new IndexOutOfBoundsException();
            } else {
                return "tile." + stub + "." + name;
            }
        } else if (this.placers[hb] == null) {
            throw new IndexOutOfBoundsException();
        } else {
            String name = this.placers[hb].getMicroName(hb, lb);
            if (name == null) {
                throw new IndexOutOfBoundsException();
            } else {
                return name;
            }
        }
    }

    public void registerPlacement(int md, IMicroPlacement imp) {
        this.placers[md] = imp;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        // NEI passes null as tab
        if (tab != null && tab != CreativeExtraTabs.tabWires
            && tab != CreativeExtraTabs.tabMachine) {
            if (tab == CreativeExtraTabs.tabMicros) {
                for (int i = 0; i < 255; ++i) {
                    String stub = CoverLib.getName(i);
                    if (stub != null) {
                        list.add(new ItemStack(RedPowerBase.blockMicro, 1, i));
                    }
                }

                for (int i = 1; i < 255; ++i) {
                    String stub = this.getMicroName(i);
                    if (stub != null) {
                        list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8));
                    }
                }

                for (int i = 1; i < 255; ++i) {
                    String stub = this.getMicroName(i);
                    if (stub != null) {
                        list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8 | 2));
                    }
                }

                for (int i = 1; i < 255; ++i) {
                    String stub = this.getMicroName(i);
                    if (stub != null) {
                        list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8 | 23));
                    }
                }

                for (int i = 1; i < 255; ++i) {
                    String stub = this.getMicroName(i);
                    if (stub != null) {
                        list.add(new ItemStack(RedPowerBase.blockMicro, 1, i << 8 | 26));
                    }
                }
            }
        } else {
            for (int i = 0; i < 255; ++i) {
                if (this.placers[i] != null) {
                    this.placers[i].addCreativeItems(i, tab, list);
                }
            }
        }
    }

    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[] { CreativeExtraTabs.tabWires,
                                    CreativeExtraTabs.tabMicros,
                                    CreativeExtraTabs.tabMachine };
    }
}
