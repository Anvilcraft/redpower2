package com.eloraam.redpower.core;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;

public class EnvironLib {
    public static double getWindSpeed(World world, WorldCoord wc) {
        if (world.provider.isHellWorld) {
            return 0.5;
        } else {
            double nv = FractalLib.noise1D(
                2576710L, (double) world.getWorldTime() * 1.0E-4, 0.6F, 5
            );
            nv = Math.max(0.0, 1.6 * (nv - 0.5) + 0.5);
            if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
                nv *= Math.sqrt((double) wc.y) / 16.0;
            }

            BiomeGenBase bgb = world.getBiomeGenForCoords(wc.x, wc.z);
            if (bgb.canSpawnLightningBolt()) {
                if (world.isThundering()) {
                    return 4.0 * nv;
                }

                if (world.isRaining()) {
                    return 0.5 + 0.5 * nv;
                }
            }

            return nv;
        }
    }
}
