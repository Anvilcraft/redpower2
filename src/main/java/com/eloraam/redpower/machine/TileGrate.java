package com.eloraam.redpower.machine;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.FluidBuffer;
import com.eloraam.redpower.core.IPipeConnectable;
import com.eloraam.redpower.core.PipeLib;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

public class TileGrate extends TileMachinePanel implements IPipeConnectable {
    private FluidBuffer gratebuf = new FluidBuffer() {
        @Override
        public TileEntity getParent() {
            return TileGrate.this;
        }

        @Override
        public void onChange() {
            TileGrate.this.markDirty();
        }

        @Override
        public int getMaxLevel() {
            return 1000;
        }
    };
    private TileGrate.GratePathfinder searchPath;
    private int searchState = 0;
    private int pressure;

    @Override
    public int getPartMaxRotation(int part, boolean sec) {
        return sec ? 0 : 5;
    }

    @Override
    public int getPipeConnectableSides() {
        return 1 << super.Rotation;
    }

    @Override
    public int getPipeFlangeSides() {
        return 1 << super.Rotation;
    }

    @Override
    public int getPipePressure(int side) {
        return this.pressure;
    }

    @Override
    public FluidBuffer getPipeBuffer(int side) {
        return this.gratebuf;
    }

    @Override
    public void onFramePickup(IBlockAccess iba) {
        this.restartPath();
    }

    @Override
    public int getExtendedID() {
        return 3;
    }

    @Override
    public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
        super.Rotation
            = ForgeDirection.getOrientation(this.getFacing(ent)).getOpposite().ordinal();
        this.updateBlockChange();
        if (ent instanceof EntityPlayer) {
            super.Owner = ((EntityPlayer) ent).getGameProfile();
        }
    }

    @Override
    public void onBlockNeighborChange(Block block) {}

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!super.worldObj.isRemote) {
            if (!this.isTickScheduled()) {
                this.scheduleTick(5);
            }

            WorldCoord wc = new WorldCoord(this);
            wc.step(super.Rotation);
            Integer pr = PipeLib.getPressure(super.worldObj, wc, super.Rotation ^ 1);
            if (pr != null) {
                this.pressure = pr - Integer.signum(pr);
            }

            if (this.searchState == 1) {
                this.searchPath.tryMapFluid(400);
            }

            PipeLib.movePipeLiquid(
                super.worldObj, this, new WorldCoord(this), 1 << super.Rotation
            );
        }
    }

    public void restartPath() {
        this.searchPath = null;
        this.searchState = 0;
    }

    @Override
    public void onTileTick() {
        if (!super.worldObj.isRemote) {
            if (this.pressure == 0) {
                this.restartPath();
            } else if (this.pressure < -100) {
                if (this.gratebuf.getLevel() >= this.gratebuf.getMaxLevel()) {
                    return;
                }

                if (this.searchState == 2) {
                    this.restartPath();
                }

                if (this.searchState == 0) {
                    this.searchState = 1;
                    this.searchPath = new TileGrate.GratePathfinder(false);
                    if (this.gratebuf.Type == null) {
                        if (!this.searchPath.startSuck(
                                new WorldCoord(this), 63 ^ 1 << super.Rotation
                            )) {
                            this.restartPath();
                            return;
                        }
                    } else {
                        this.searchPath.start(
                            new WorldCoord(this),
                            this.gratebuf.Type,
                            63 ^ 1 << super.Rotation
                        );
                    }
                }

                if (this.searchState == 1) {
                    if (!this.searchPath.tryMapFluid(400)) {
                        return;
                    }

                    Fluid ty = this.searchPath.fluidClass;
                    int fluid = this.searchPath.trySuckFluid(ty.getDensity());
                    if (fluid == 0) {
                        return;
                    }

                    this.gratebuf.addLevel(ty, fluid);
                }
            } else if (this.pressure > 100) {
                Fluid fluid = this.gratebuf.getFluidClass();
                if (fluid == null) {
                    return;
                }

                int fq = fluid.getDensity();
                if (fq == 0) {
                    return;
                }

                if (this.gratebuf.getLevel() < fq) {
                    return;
                }

                if (this.gratebuf.Type == null) {
                    return;
                }

                if (this.searchState == 1) {
                    this.restartPath();
                }

                if (this.searchState == 0) {
                    this.searchState = 2;
                    this.searchPath = new TileGrate.GratePathfinder(true);
                    this.searchPath.start(
                        new WorldCoord(this), this.gratebuf.Type, 63 ^ 1 << super.Rotation
                    );
                }

                if (this.searchState == 2 && RedPowerMachine.AllowGrateDump) {
                    int fr = this.searchPath.tryDumpFluid(fq, 2000);
                    if (fr != fq) {
                        this.gratebuf.addLevel(this.gratebuf.Type, -fq);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.gratebuf.readFromNBT(data, "buf");
        this.pressure = data.getShort("pres");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        this.gratebuf.writeToNBT(data, "buf");
        data.setShort("pres", (short) this.pressure);
    }

    public static class FluidCoord implements Comparable<TileGrate.FluidCoord> {
        public WorldCoord wc;
        public int dist;

        public FluidCoord(WorldCoord w, int d) {
            this.wc = w;
            this.dist = d;
        }

        public int compareTo(TileGrate.FluidCoord wr) {
            return this.wc.y == wr.wc.y ? this.dist - wr.dist : this.wc.y - wr.wc.y;
        }
    }

    public class GratePathfinder {
        WorldCoord startPos;
        Map<WorldCoord, WorldCoord> backlink = new HashMap<>();
        Queue<TileGrate.FluidCoord> workset;
        Queue<TileGrate.FluidCoord> allset
            = new PriorityQueue<>(1024, Collections.reverseOrder());
        public Fluid fluidClass;

        public GratePathfinder(boolean checkVertical) {
            if (checkVertical) {
                this.workset = new PriorityQueue<>();
            } else {
                this.workset
                    = new PriorityQueue<>(1024, Comparator.comparingInt(a -> a.dist));
            }
        }

        public void start(WorldCoord wc, Fluid tp, int sides) {
            this.fluidClass = tp;
            this.startPos = wc;

            for (int i = 0; i < 6; ++i) {
                if ((sides & 1 << i) != 0) {
                    WorldCoord wc2 = wc.coordStep(i);
                    this.backlink.put(wc2, wc);
                    this.workset.add(new TileGrate.FluidCoord(wc2, 0));
                }
            }
        }

        public boolean startSuck(WorldCoord wc, int sides) {
            this.fluidClass = null;
            this.startPos = wc;

            for (int i = 0; i < 6; ++i) {
                if ((sides & 1 << i) != 0) {
                    WorldCoord wc2 = wc.coordStep(i);
                    this.backlink.put(wc2, wc);
                    this.workset.add(new TileGrate.FluidCoord(wc2, 0));
                    Fluid fl = PipeLib.getFluid(TileGrate.this.worldObj, wc2);
                    if (fl != null) {
                        this.fluidClass = fl;
                    }
                }
            }

            return this.fluidClass != null;
        }

        public boolean isConnected(WorldCoord wc) {
            if (wc.compareTo(this.startPos) == 0) {
                return true;
            } else {
                do {
                    wc = (WorldCoord) this.backlink.get(wc);
                    if (wc == null) {
                        return false;
                    }

                    if (wc.compareTo(this.startPos) == 0) {
                        return true;
                    }
                } while (PipeLib.getFluid(TileGrate.this.worldObj, wc) == this.fluidClass
                );

                return false;
            }
        }

        public void stepAdd(TileGrate.FluidCoord nc) {
            for (int i = 0; i < 6; ++i) {
                WorldCoord wc2 = nc.wc.coordStep(i);
                if (!this.backlink.containsKey(wc2)) {
                    this.backlink.put(wc2, nc.wc);
                    this.workset.add(new TileGrate.FluidCoord(wc2, nc.dist + 1));
                }
            }
        }

        public void stepMap(TileGrate.FluidCoord nc) {
            for (int i = 0; i < 6; ++i) {
                WorldCoord wc2 = nc.wc.coordStep(i);
                if (PipeLib.getFluid(TileGrate.this.worldObj, wc2) == this.fluidClass
                    && !this.backlink.containsKey(wc2)) {
                    this.backlink.put(wc2, nc.wc);
                    this.workset.add(new TileGrate.FluidCoord(wc2, nc.dist + 1));
                }
            }
        }

        public int tryDumpFluid(int level, int tries) {
            for (int i = 0; i < tries; ++i) {
                TileGrate.FluidCoord nc = (TileGrate.FluidCoord) this.workset.poll();
                if (nc == null) {
                    TileGrate.this.restartPath();
                    return level;
                }

                if (!this.isConnected(nc.wc)) {
                    TileGrate.this.restartPath();
                    return level;
                }

                if (TileGrate.this.worldObj.isAirBlock(nc.wc.x, nc.wc.y, nc.wc.z)) {
                    if (level == this.fluidClass.getDensity()
                        && TileGrate.this.worldObj.setBlock(
                            nc.wc.x, nc.wc.y, nc.wc.z, this.fluidClass.getBlock()
                        )) {
                        this.stepAdd(nc);
                        return 0;
                    }
                } else if (PipeLib.getFluid(TileGrate.this.worldObj, nc.wc) == this.fluidClass) {
                    this.stepAdd(nc);
                    int lv1 = this.fluidClass.getDensity(
                        TileGrate.this.worldObj, nc.wc.x, nc.wc.y, nc.wc.z
                    );
                    if (lv1 < 1000) {
                        int lv2 = Math.min(lv1 + level, this.fluidClass.getDensity());
                        if (lv2 == this.fluidClass.getDensity()
                            && TileGrate.this.worldObj.setBlock(
                                nc.wc.x, nc.wc.y, nc.wc.z, this.fluidClass.getBlock()
                            )) {
                            level -= lv2 - lv1;
                            if (level == 0) {
                                return 0;
                            }
                        }
                    }
                }
            }

            return level;
        }

        public boolean tryMapFluid(int tries) {
            if (this.allset.size() > 32768) {
                return true;
            } else {
                for (int i = 0; i < tries; ++i) {
                    TileGrate.FluidCoord nc = (TileGrate.FluidCoord) this.workset.poll();
                    if (nc == null) {
                        return true;
                    }

                    Fluid fluid = PipeLib.getFluid(TileGrate.this.worldObj, nc.wc);
                    if (fluid != null) {
                        this.stepMap(nc);
                        if (fluid == this.fluidClass) {
                            int lvl
                                = PipeLib.getFluidAmount(TileGrate.this.worldObj, nc.wc);
                            if (lvl > 0) {
                                this.allset.add(nc);
                            }
                        }
                    }
                }

                return false;
            }
        }

        public int trySuckFluid(int level) {
            int tr = 0;

            while (!this.allset.isEmpty()) {
                TileGrate.FluidCoord nc = (TileGrate.FluidCoord) this.allset.peek();
                if (!this.isConnected(nc.wc)) {
                    TileGrate.this.restartPath();
                    return tr;
                }

                if (PipeLib.getFluid(TileGrate.this.worldObj, nc.wc) != this.fluidClass) {
                    this.allset.poll();
                } else {
                    Fluid fluid = PipeLib.getFluid(TileGrate.this.worldObj, nc.wc);
                    if (fluid != null) {
                        int lvl = PipeLib.getFluidAmount(TileGrate.this.worldObj, nc.wc);
                        if (lvl <= 0) {
                            this.allset.poll();
                        } else if (tr + lvl <= level) {
                            tr += lvl;
                            TileGrate.this.worldObj.setBlockToAir(
                                nc.wc.x, nc.wc.y, nc.wc.z
                            );
                            this.allset.poll();
                            if (tr == level) {
                                return level;
                            }
                        }
                    } else {
                        this.allset.poll();
                    }
                }
            }

            TileGrate.this.restartPath();
            return tr;
        }
    }
}
