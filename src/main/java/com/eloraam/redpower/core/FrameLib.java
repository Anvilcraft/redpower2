package com.eloraam.redpower.core;

import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class FrameLib {
    public static class FrameSolver {
        private HashSet<WorldCoord> scanmap = new HashSet();
        private LinkedList<WorldCoord> scanpos = new LinkedList();
        private HashSet<WorldCoord> framemap = new HashSet();
        private LinkedList<WorldCoord> frameset = new LinkedList();
        private LinkedList<WorldCoord> clearset = new LinkedList();
        private int movedir;
        private WorldCoord motorpos;
        private boolean valid = true;
        private World world;

        public FrameSolver(World world, WorldCoord wc, WorldCoord motorp, int movdir) {
            this.movedir = movdir;
            this.motorpos = motorp;
            this.world = world;
            this.scanmap.add(motorp);
            this.scanmap.add(wc);
            this.scanpos.addLast(wc);
        }

        private boolean step() {
            WorldCoord wc = (WorldCoord) this.scanpos.removeFirst();
            if (wc.y >= 0 && wc.y < this.world.getHeight() - 1) {
                Block block = this.world.getBlock(wc.x, wc.y, wc.z);
                if (this.movedir >= 0 && !this.world.blockExists(wc.x, wc.y, wc.z)) {
                    this.valid = false;
                    return false;
                } else if (this.world.isAirBlock(wc.x, wc.y, wc.z)) {
                    return false;
                } else if (this.movedir >= 0 && block.getBlockHardness(this.world, wc.x, wc.y, wc.z) < 0.0F) {
                    this.valid = false;
                    return false;
                } else {
                    this.framemap.add(wc);
                    this.frameset.addLast(wc);
                    IFrameLink ifl
                        = CoreLib.getTileEntity(this.world, wc, IFrameLink.class);
                    if (ifl == null) {
                        return true;
                    } else if (ifl.isFrameMoving() && this.movedir >= 0) {
                        this.valid = false;
                        return true;
                    } else {
                        for (int i = 0; i < 6; ++i) {
                            if (ifl.canFrameConnectOut(i)) {
                                WorldCoord sp = wc.coordStep(i);
                                if (!this.scanmap.contains(sp)) {
                                    IFrameLink if2 = CoreLib.getTileEntity(
                                        this.world, sp, IFrameLink.class
                                    );
                                    if (if2 != null) {
                                        if (!if2.canFrameConnectIn((i ^ 1) & 0xFF)) {
                                            continue;
                                        }

                                        if (this.movedir < 0) {
                                            WorldCoord wcls = if2.getFrameLinkset();
                                            if (wcls == null
                                                || !wcls.equals(this.motorpos)) {
                                                continue;
                                            }
                                        }
                                    }

                                    this.scanmap.add(sp);
                                    this.scanpos.addLast(sp);
                                }
                            }
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        }

        public boolean solve() {
            while (this.valid && this.scanpos.size() > 0) {
                this.step();
            }

            return this.valid;
        }

        public boolean solveLimit(int limit) {
            while (this.valid && this.scanpos.size() > 0) {
                if (this.step()) {
                    --limit;
                }

                if (limit == 0) {
                    return false;
                }
            }

            return this.valid;
        }

        public boolean addMoved() {
            for (WorldCoord wc : (LinkedList<WorldCoord>) this.frameset.clone()) {
                WorldCoord sp = wc.coordStep(this.movedir);
                if (!this.world.blockExists(sp.x, sp.y, sp.z)) {
                    this.valid = false;
                    return false;
                }

                if (!this.framemap.contains(sp)) {
                    if (!this.world.isAirBlock(wc.x, wc.y, wc.z)) {
                        if (!this.world.canPlaceEntityOnSide(
                                Blocks.stone, sp.x, sp.y, sp.z, true, 0, null, null
                            )) {
                            this.valid = false;
                            return false;
                        }

                        this.clearset.add(sp);
                    }

                    this.framemap.add(sp);
                    this.frameset.addLast(sp);
                }
            }

            return this.valid;
        }

        public void sort(int dir) {
            this.frameset.sort(WorldCoord.getCompareDir(dir));
        }

        public LinkedList<WorldCoord> getFrameSet() {
            return this.frameset;
        }

        public LinkedList<WorldCoord> getClearSet() {
            return this.clearset;
        }
    }
}
