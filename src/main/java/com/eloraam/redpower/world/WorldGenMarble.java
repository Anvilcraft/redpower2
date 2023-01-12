package com.eloraam.redpower.world;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class WorldGenMarble extends WorldGenCustomOre {
    private Deque<WorldGenMarble.CoordSearchPath> fillStack = new LinkedList();
    private Set<ChunkCoordinates> fillStackTest = new HashSet();

    public WorldGenMarble(Block block, int meta, int num) {
        super(block, meta, num);
    }

    private void addBlock(int x, int y, int z, int p) {
        ChunkCoordinates sb = new ChunkCoordinates(x, y, z);
        if (!this.fillStackTest.contains(sb)) {
            this.fillStack.addLast(new WorldGenMarble.CoordSearchPath(x, y, z, p));
            this.fillStackTest.add(sb);
        }
    }

    private void searchBlock(World world, int x, int y, int z, int p) {
        if (world.isAirBlock(x - 1, y, z) || world.isAirBlock(x + 1, y, z)
            || world.isAirBlock(x, y - 1, z) || world.isAirBlock(x, y + 1, z)
            || world.isAirBlock(x, y, z - 1) || world.isAirBlock(x, y, z + 1)) {
            p = 6;
        }

        this.addBlock(x - 1, y, z, p);
        this.addBlock(x + 1, y, z, p);
        this.addBlock(x, y - 1, z, p);
        this.addBlock(x, y + 1, z, p);
        this.addBlock(x, y, z - 1, p);
        this.addBlock(x, y, z + 1, p);
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (!world.isAirBlock(x, y, z)) {
            return false;
        } else {
            int l;
            for (l = y; world.getBlock(x, l, z) != Blocks.stone; ++l) {
                if (l > 96) {
                    return false;
                }
            }

            this.addBlock(x, l, z, 6);

            while (this.fillStack.size() > 0 && super.numberOfBlocks > 0) {
                WorldGenMarble.CoordSearchPath sp
                    = (WorldGenMarble.CoordSearchPath) this.fillStack.removeFirst();
                if (world.getBlock(sp.x, sp.y, sp.z) == Blocks.stone) {
                    world.setBlock(
                        sp.x, sp.y, sp.z, super.minableBlock, super.minableBlockMeta, 3
                    );
                    if (sp.p > 0) {
                        this.searchBlock(world, sp.x, sp.y, sp.z, sp.p - 1);
                    }

                    --super.numberOfBlocks;
                }
            }

            return true;
        }
    }

    public static class CoordSearchPath {
        private final int x;
        private final int y;
        private final int z;
        private final int p;

        public CoordSearchPath(int x, int y, int z, int p) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.p = p;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                WorldGenMarble.CoordSearchPath that = (WorldGenMarble.CoordSearchPath) o;
                if (this.x != that.x) {
                    return false;
                } else if (this.y != that.y) {
                    return false;
                } else if (this.z != that.z) {
                    return false;
                } else {
                    return this.p == that.p;
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            int result = this.x;
            result = 31 * result + this.y;
            result = 31 * result + this.z;
            return 31 * result + this.p;
        }
    }
}
