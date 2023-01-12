package com.eloraam.redpower.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;

public class PipeLib {
    private static boolean isConSide(IBlockAccess iba, int x, int y, int z, int side) {
        TileEntity te = iba.getTileEntity(x, y, z);
        if (te instanceof IPipeConnectable) {
            IPipeConnectable itc1 = (IPipeConnectable) te;
            int ilt1 = itc1.getPipeConnectableSides();
            return (ilt1 & 1 << side) > 0;
        } else {
            if (te instanceof IFluidHandler) {
                IFluidHandler itc = (IFluidHandler) te;
                FluidTankInfo[] info
                    = itc.getTankInfo(ForgeDirection.getOrientation(side));
                if (info != null) {
                    for (FluidTankInfo i : info) {
                        if (i != null && i.capacity > 0) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    public static int getConnections(IBlockAccess iba, int x, int y, int z) {
        IPipeConnectable itc
            = CoreLib.getTileEntity(iba, x, y, z, IPipeConnectable.class);
        if (itc == null) {
            return 0;
        } else {
            int trs = 0;
            int sides = itc.getPipeConnectableSides();
            if ((sides & 1) > 0 && isConSide(iba, x, y - 1, z, 1)) {
                trs |= 1;
            }

            if ((sides & 2) > 0 && isConSide(iba, x, y + 1, z, 0)) {
                trs |= 2;
            }

            if ((sides & 4) > 0 && isConSide(iba, x, y, z - 1, 3)) {
                trs |= 4;
            }

            if ((sides & 8) > 0 && isConSide(iba, x, y, z + 1, 2)) {
                trs |= 8;
            }

            if ((sides & 16) > 0 && isConSide(iba, x - 1, y, z, 5)) {
                trs |= 16;
            }

            if ((sides & 32) > 0 && isConSide(iba, x + 1, y, z, 4)) {
                trs |= 32;
            }

            return trs;
        }
    }

    public static int getFlanges(IBlockAccess iba, WorldCoord wci, int sides) {
        int tr = 0;

        for (int i = 0; i < 6; ++i) {
            if ((sides & 1 << i) != 0) {
                WorldCoord wc = wci.copy();
                wc.step(i);
                TileEntity te = iba.getTileEntity(wc.x, wc.y, wc.z);
                if (te != null) {
                    if (te instanceof IPipeConnectable) {
                        IPipeConnectable itc = (IPipeConnectable) te;
                        if ((itc.getPipeFlangeSides() & 1 << (i ^ 1)) > 0) {
                            tr |= 1 << i;
                        }
                    } else if (te instanceof IFluidHandler) {
                        IFluidHandler itc = (IFluidHandler) te;
                        FluidTankInfo[] info
                            = itc.getTankInfo(ForgeDirection.getOrientation(i ^ 1));
                        if (info != null) {
                            for (FluidTankInfo inf : info) {
                                if (inf != null && inf.capacity > 0) {
                                    tr |= 1 << i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return tr;
    }

    public static Integer getPressure(World world, WorldCoord wc, int side) {
        TileEntity te = world.getTileEntity(wc.x, wc.y, wc.z);
        if (te != null) {
            if (te instanceof IPipeConnectable) {
                IPipeConnectable itc = (IPipeConnectable) te;
                return itc.getPipePressure(side);
            }

            if (te instanceof IFluidHandler) {
                IFluidHandler ifh = (IFluidHandler) te;
                FluidTankInfo[] info
                    = ifh.getTankInfo(ForgeDirection.getOrientation(side));
                if (info != null) {
                    for (FluidTankInfo i : info) {
                        if (i.fluid != null) {
                            return (int
                            ) ((double) i.fluid.amount / (double) i.capacity * 100.0);
                        }
                    }

                    for (FluidTankInfo i : info) {
                        if (i.capacity > 0) {
                            return -100;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static Fluid getFluid(World world, WorldCoord wc) {
        Block bl = world.getBlock(wc.x, wc.y, wc.z);
        if (bl instanceof IFluidBlock) {
            IFluidBlock fcl = (IFluidBlock) bl;
            return fcl.getFluid();
        } else {
            if (bl instanceof BlockLiquid) {
                BlockLiquid blq = (BlockLiquid) bl;
                if (blq.getMaterial() == Material.water) {
                    return FluidRegistry.WATER;
                }

                if (blq.getMaterial() == Material.lava) {
                    return FluidRegistry.LAVA;
                }
            }

            return null;
        }
    }

    public static int getFluidAmount(World world, WorldCoord wc) {
        Block bl = world.getBlock(wc.x, wc.y, wc.z);
        if (bl instanceof IFluidBlock) {
            IFluidBlock fcl = (IFluidBlock) bl;
            float fp = fcl.getFilledPercentage(world, wc.x, wc.y, wc.z);
            return (int) ((float) fcl.getFluid().getDensity() * fp);
        } else {
            if (bl instanceof BlockLiquid) {
                BlockLiquid blq = (BlockLiquid) bl;
                if (blq.getMaterial() == Material.water
                    || blq.getMaterial() == Material.lava) {
                    return 1000;
                }
            }

            return 0;
        }
    }

    public static void
    movePipeLiquid(World world, IPipeConnectable src, WorldCoord wsrc, int sides) {
        for (int side = 0; side < 6; ++side) {
            if ((sides & 1 << side) != 0) {
                WorldCoord wc = wsrc.coordStep(side);
                TileEntity te = world.getTileEntity(wc.x, wc.y, wc.z);
                if (te != null) {
                    if (te instanceof IPipeConnectable) {
                        IPipeConnectable itc = (IPipeConnectable) te;
                        int srcPressure = src.getPipePressure(side);
                        int dstPressure = itc.getPipePressure(side ^ 1);
                        if (srcPressure >= dstPressure) {
                            FluidBuffer srcBuffer = src.getPipeBuffer(side);
                            if (srcBuffer != null) {
                                Fluid srcType = srcBuffer.Type;
                                int srcLevel = srcBuffer.getLevel() + srcBuffer.Delta;
                                if (srcType != null && srcLevel > 0) {
                                    FluidBuffer dstBuffer = itc.getPipeBuffer(side ^ 1);
                                    if (dstBuffer != null) {
                                        Fluid dstType = dstBuffer.Type;
                                        int dstLevel = dstBuffer.getLevel();
                                        if (dstType == null || dstType == srcType) {
                                            int ls = Math.max(
                                                srcPressure > dstPressure ? 25 : 0,
                                                (srcLevel - dstLevel) / 2
                                            );
                                            ls = Math.min(
                                                Math.min(
                                                    ls, dstBuffer.getMaxLevel() - dstLevel
                                                ),
                                                srcLevel
                                            );
                                            if (ls > 0) {
                                                srcBuffer.addLevel(srcType, -ls);
                                                dstBuffer.addLevel(srcType, ls);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (te instanceof IFluidHandler) {
                        IFluidHandler ifh = (IFluidHandler) te;
                        FluidBuffer srcBuffer = src.getPipeBuffer(side);
                        if (srcBuffer != null) {
                            FluidTankInfo[] info
                                = ifh.getTankInfo(ForgeDirection.getOrientation(side ^ 1)
                                );
                            if (info != null) {
                                for (FluidTankInfo i : info) {
                                    Fluid bType = srcBuffer.Type;
                                    int srcLevel = srcBuffer.getLevel() + srcBuffer.Delta;
                                    int srcPressure = src.getPipePressure(side);
                                    if (i.capacity > 0) {
                                        if (i.fluid != null) {
                                            if (i.fluid.getFluid() != bType
                                                && bType != null) {
                                                continue;
                                            }
                                        } else if (bType == null) {
                                            continue;
                                        }

                                        int dstLevel
                                            = i.fluid == null ? 0 : i.fluid.amount;
                                        int dstPressure = dstLevel <= 0
                                            ? -100
                                            : (int
                                            ) ((double) dstLevel / (double) i.capacity
                                               * 100.0);
                                        if (srcPressure < dstPressure && dstLevel > 0) {
                                            int qty = Math.min(
                                                Math.min(
                                                    Math.max(
                                                        25, (dstLevel - srcLevel) / 2
                                                    ),
                                                    srcBuffer.getMaxLevel() - srcLevel
                                                ),
                                                dstLevel
                                            );
                                            if (qty > 0) {
                                                FluidStack drStack = ifh.drain(
                                                    ForgeDirection.getOrientation(
                                                        side ^ 1
                                                    ),
                                                    qty,
                                                    true
                                                );
                                                if (drStack != null) {
                                                    srcBuffer.addLevel(
                                                        drStack.getFluid(), drStack.amount
                                                    );
                                                }
                                            }
                                        } else if (srcPressure > dstPressure && srcLevel > 0) {
                                            int qty = Math.min(
                                                Math.min(
                                                    Math.max(
                                                        25, (srcLevel - dstLevel) / 2
                                                    ),
                                                    i.capacity - dstLevel
                                                ),
                                                srcLevel
                                            );
                                            if (qty > 0) {
                                                qty = ifh.fill(
                                                    ForgeDirection.getOrientation(
                                                        side ^ 1
                                                    ),
                                                    new FluidStack(bType, qty),
                                                    true
                                                );
                                                srcBuffer.addLevel(bType, -qty);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
