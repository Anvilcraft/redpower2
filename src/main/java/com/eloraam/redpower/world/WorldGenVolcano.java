package com.eloraam.redpower.world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockVine;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class WorldGenVolcano extends WorldGenCustomOre {
    private LinkedList<ChunkCoordinates> fillStack = new LinkedList();
    private Map<ChunkCoordIntPair, Integer> fillStackTest = new HashMap();

    public WorldGenVolcano(Block block, int meta, int num) {
        super(block, meta, num);
    }

    private void addBlock(int x, int y, int z, int p) {
        if (p > 0) {
            ChunkCoordIntPair sb = new ChunkCoordIntPair(x, z);
            Integer o = (Integer) this.fillStackTest.get(sb);
            if (o == null || p > o) {
                this.fillStack.addLast(new ChunkCoordinates(x, y, z));
                this.fillStackTest.put(sb, p);
            }
        }
    }

    private void searchBlock(int x, int y, int z, int p, Random random) {
        int rp = random.nextInt(16);
        this.addBlock(x - 1, y, z, (rp & 1) > 0 ? p - 1 : p);
        this.addBlock(x + 1, y, z, (rp & 2) > 0 ? p - 1 : p);
        this.addBlock(x, y, z - 1, (rp & 4) > 0 ? p - 1 : p);
        this.addBlock(x, y, z + 1, (rp & 8) > 0 ? p - 1 : p);
    }

    public boolean canReplace(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == Blocks.air || block == Blocks.flowing_water
            || block == Blocks.water || block instanceof BlockLog
            || block instanceof BlockLeavesBase || block instanceof BlockVine
            || block instanceof BlockBush || block == Blocks.snow
            || block == Blocks.snow_layer || block == Blocks.ice
            || block == Blocks.packed_ice;
    }

    public void eatTree(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.snow) {
            world.setBlockToAir(x, y, z);
        } else if (block instanceof BlockLog || block instanceof BlockLeavesBase || block instanceof BlockVine) {
            world.setBlockToAir(x, y, z);
            this.eatTree(world, x, y + 1, z);
        }
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlock(x, y, z) != Blocks.lava) {
            return false;
        } else {
            int swh = world.getHeightValue(x, z);

            while (swh > 0 && this.canReplace(world, x, swh - 1, z)) {
                --swh;
            }

            int yTop;
            for (yTop = y; yTop < swh; ++yTop) {
                world.setBlock(x, yTop, z, Blocks.flowing_lava);
                world.setBlock(
                    x - 1, yTop, z, super.minableBlock, super.minableBlockMeta, 2
                );
                world.setBlock(
                    x + 1, yTop, z, super.minableBlock, super.minableBlockMeta, 2
                );
                world.setBlock(
                    x, yTop, z - 1, super.minableBlock, super.minableBlockMeta, 2
                );
                world.setBlock(
                    x, yTop, z + 1, super.minableBlock, super.minableBlockMeta, 2
                );
            }

            int head = 3 + random.nextInt(4);
            int spread = random.nextInt(3);

        label67:
            while (super.numberOfBlocks > 0) {
                while (this.fillStack.size() == 0) {
                    world.setBlock(x, yTop, z, Blocks.lava);
                    this.fillStackTest.clear();
                    this.searchBlock(x, yTop, z, head, random);
                    if (++yTop > 125) {
                        break label67;
                    }
                }

                ChunkCoordinates sp = (ChunkCoordinates) this.fillStack.removeFirst();
                if (!world.getChunkFromBlockCoords(sp.posX, sp.posZ).isEmpty()) {
                    int pow
                        = this.fillStackTest.get(new ChunkCoordIntPair(sp.posX, sp.posZ));
                    int hm = world.getHeightValue(sp.posX, sp.posZ) + 1;

                    while (hm > 0 && this.canReplace(world, sp.posX, hm - 1, sp.posZ)) {
                        --hm;
                    }

                    if (hm <= sp.posY && this.canReplace(world, sp.posX, hm, sp.posZ)) {
                        this.eatTree(world, sp.posX, hm, sp.posZ);
                        world.setBlock(
                            sp.posX,
                            hm,
                            sp.posZ,
                            super.minableBlock,
                            super.minableBlockMeta,
                            2
                        );
                        if (sp.posY > hm) {
                            pow = Math.max(pow, spread);
                        }

                        this.searchBlock(sp.posX, hm, sp.posZ, pow, random);
                        --super.numberOfBlocks;
                    }
                }
            }

            world.setBlock(x, yTop, z, Blocks.lava, 0, 2);

            while (yTop > swh && world.getBlock(x, yTop, z) == Blocks.lava) {
                world.markBlockForUpdate(x, yTop, z);
                world.notifyBlocksOfNeighborChange(x, yTop, z, Blocks.lava);
                world.scheduledUpdatesAreImmediate = true;
                Blocks.lava.updateTick(world, x, yTop, z, random);
                world.scheduledUpdatesAreImmediate = false;
                --yTop;
            }

            return true;
        }
    }
}
