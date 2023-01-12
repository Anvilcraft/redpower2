package com.eloraam.redpower.world;

import java.util.Random;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.FractalLib;
import com.eloraam.redpower.core.Vector3;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenRubberTree extends WorldGenerator {
    public void putLeaves(World world, int x, int y, int z) {
        if (world.isAirBlock(x, y, z)) {
            world.setBlock(x, y, z, RedPowerWorld.blockLeaves, 0, 3);
        }
    }

    public boolean fillBlock(World world, int x, int y, int z) {
        if (y >= 0 && y <= 126) {
            Block bl = world.getBlock(x, y, z);
            if (bl != null && bl.isWood(world, x, y, z)) {
                return true;
            } else if (bl != Blocks.air && bl != null && !bl.isLeaves(world, x, y, z) && bl != Blocks.tallgrass && bl != Blocks.grass && bl != Blocks.vine) {
                return false;
            } else {
                world.setBlock(x, y, z, RedPowerWorld.blockLogs, 0, 3);
                this.putLeaves(world, x, y - 1, z);
                this.putLeaves(world, x, y + 1, z);
                this.putLeaves(world, x, y, z - 1);
                this.putLeaves(world, x, y, z + 1);
                this.putLeaves(world, x - 1, y, z);
                this.putLeaves(world, x + 1, y, z);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean generate(World world, Random random, int xPos, int yPos, int zPos) {
        int trh = random.nextInt(6) + 25;
        if (yPos >= 1 && yPos + trh + 2 <= world.getHeight()) {
            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    Block bid = world.getBlock(xPos + x, yPos - 1, zPos + z);
                    if (bid != Blocks.grass && bid != Blocks.dirt) {
                        return false;
                    }
                }
            }

            byte rw = 1;

            for (int org = yPos; org < yPos + trh; ++org) {
                if (org > yPos + 3) {
                    rw = 5;
                }

                for (int x = xPos - rw; x <= xPos + rw; ++x) {
                    for (int z = zPos - rw; z <= zPos + rw; ++z) {
                        Block dest = world.getBlock(x, org, z);
                        if (dest != Blocks.air && dest != null
                            && !dest.isLeaves(world, x, org, z)
                            && !dest.isWood(world, x, org, z) && dest != Blocks.tallgrass
                            && dest != Blocks.grass && dest != Blocks.vine) {
                            return false;
                        }
                    }
                }
            }

            for (int x = -1; x <= 1; ++x) {
                for (int z = -1; z <= 1; ++z) {
                    world.setBlock(xPos + x, yPos - 1, zPos + z, Blocks.dirt);
                }
            }

            for (int var21 = 0; var21 <= 6; ++var21) {
                for (int x = -1; x <= 1; ++x) {
                    for (int z = -1; z <= 1; ++z) {
                        world.setBlock(
                            xPos + x,
                            yPos + var21,
                            zPos + z,
                            RedPowerWorld.blockLogs,
                            1,
                            3
                        );
                    }
                }

                for (int x = -1; x <= 1; ++x) {
                    if (random.nextInt(5) == 1
                        && world.isAirBlock(xPos + x, yPos + var21, zPos - 2)) {
                        world.setBlock(
                            xPos + x, yPos + var21, zPos - 2, Blocks.vine, 1, 3
                        );
                    }

                    if (random.nextInt(5) == 1
                        && world.isAirBlock(xPos + x, yPos + var21, zPos + 2)) {
                        world.setBlock(
                            xPos + x, yPos + var21, zPos + 2, Blocks.vine, 4, 3
                        );
                    }
                }

                for (int z = -1; z <= 1; ++z) {
                    if (random.nextInt(5) == 1
                        && world.isAirBlock(xPos - 2, yPos + var21, zPos + z)) {
                        world.setBlock(
                            xPos - 2, yPos + var21, zPos + z, Blocks.vine, 8, 3
                        );
                    }

                    if (random.nextInt(5) == 1
                        && world.isAirBlock(xPos + 2, yPos + var21, zPos + z)) {
                        world.setBlock(
                            xPos + 2, yPos + var21, zPos + z, Blocks.vine, 2, 3
                        );
                    }
                }
            }

            Vector3 var23 = new Vector3();
            Vector3 var24 = new Vector3();
            int nbr = random.nextInt(100) + 10;

            for (int br = 0; br < nbr; ++br) {
                var24.set(
                    (double) random.nextFloat() - 0.5,
                    (double) random.nextFloat(),
                    (double) random.nextFloat() - 0.5
                );
                var24.normalize();
                double m = ((double) nbr / 10.0 + 4.0)
                    * (double) (1.0F + 1.0F * random.nextFloat());
                var24.x *= m;
                var24.z *= m;
                var24.y = var24.y * (double) (trh - 15) + (double) nbr / 10.0;
                if (nbr < 8) {
                    switch (nbr - 1) {
                        case 0:
                            var23.set(
                                (double) (xPos - 1),
                                (double) (yPos + 6),
                                (double) (zPos - 1)
                            );
                            break;
                        case 1:
                            var23.set(
                                (double) (xPos - 1), (double) (yPos + 6), (double) zPos
                            );
                            break;
                        case 2:
                            var23.set(
                                (double) (xPos - 1),
                                (double) (yPos + 6),
                                (double) (zPos + 1)
                            );
                            break;
                        case 3:
                            var23.set(
                                (double) xPos, (double) (yPos + 6), (double) (zPos + 1)
                            );
                            break;
                        case 4:
                            var23.set(
                                (double) (xPos + 1),
                                (double) (yPos + 6),
                                (double) (zPos + 1)
                            );
                            break;
                        case 5:
                            var23.set(
                                (double) (xPos + 1), (double) (yPos + 6), (double) zPos
                            );
                            break;
                        case 6:
                            var23.set(
                                (double) (xPos + 1),
                                (double) (yPos + 6),
                                (double) (zPos - 1)
                            );
                            break;
                        default:
                            var23.set(
                                (double) xPos, (double) (yPos + 6), (double) (zPos - 1)
                            );
                    }
                } else {
                    var23.set(
                        (double) (xPos + random.nextInt(3) - 1),
                        (double) (yPos + 6),
                        (double) (zPos + random.nextInt(3) - 1)
                    );
                }

                long brseed = random.nextLong();
                FractalLib.BlockSnake bsn
                    = new FractalLib.BlockSnake(var23, var24, brseed);

                while (bsn.iterate()) {
                    Vector3 v = bsn.get();
                    if (!this.fillBlock(
                            world,
                            (int) Math.floor(v.x),
                            (int) Math.floor(v.y),
                            (int) Math.floor(v.z)
                        )) {
                        break;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
