package com.eloraam.redpower.compat;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.RedPowerLib;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.blocks.TileComputerBase;

public class ComputercraftInterop {

    private static int computerConnectClass = 1337;

    public static void initInterop() {
        addComputerConnectMappings();
        RedPowerCore.redPowerAdaptors.add(new ComputerCraftConnectableAdaptor());
        ComputerCraft.registerBundledRedstoneProvider(new RedPowerBundledProvider());
    }

    public static int getComputerConnectClass() {
        return computerConnectClass;
    }

    public static void addComputerConnectMappings() {
        RedPowerLib.addCompatibleMapping(0, computerConnectClass);
        RedPowerLib.addCompatibleMapping(18, computerConnectClass);
        for (int i = 0; i < 16; ++i) {
            RedPowerLib.addCompatibleMapping(1 + i, computerConnectClass);
            RedPowerLib.addCompatibleMapping(19 + i, computerConnectClass);
        }
    }

    public static int getComputerPoweringMask(int ch, TileComputerBase computer, int direction) {
        if (ch == 0) {
            int mask = 0;
            for (int side = 0; side < 6; ++side) {
                if (computer.getRedstoneOutput(side) == 0)
                    continue;
                mask |= RedPowerLib.getConDirMask(side);
            }
            return mask;
        }
        int mask = 0;
        for (int side = 0; side < 6; ++side) {
            int channelMask = computer.getBundledRedstoneOutput(side);
            if ((channelMask & 1 << ch - 1) <= 0)
                continue;
            mask |= RedPowerLib.getConDirMask(side);
        }
        return mask;
    }

}
