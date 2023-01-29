package com.eloraam.redpower.world;

import java.util.Random;

import com.eloraam.redpower.RedPowerWorld;
import com.eloraam.redpower.core.Config;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class WorldGenHandler implements IWorldGenerator {
    public void generate(
        Random rin,
        int chunkX,
        int chunkZ,
        World world,
        IChunkProvider generator,
        IChunkProvider provider
    ) {
        BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(
            chunkX * 16 + 16, chunkZ * 16 + 16
        );
        Random rand = new Random((long) (chunkX * 31 + chunkZ));
        if (!BiomeDictionary.isBiomeOfType(biome, Type.NETHER)
            && !BiomeDictionary.isBiomeOfType(biome, Type.END)) {
            for (int a = 0; a < 2; ++a) {
                int vc = chunkX * 16 + rand.nextInt(16);
                int bgb = rand.nextInt(48);
                int n = chunkZ * 16 + rand.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres, 0, 7)
                    .generate(world, rand, vc, bgb, n);
            }

            for (int a = 0; a < 2; ++a) {
                int vc = chunkX * 16 + rand.nextInt(16);
                int bgb = rand.nextInt(48);
                int n = chunkZ * 16 + rand.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres, 1, 7)
                    .generate(world, rand, vc, bgb, n);
            }

            for (int a = 0; a < 2; ++a) {
                int vc = chunkX * 16 + rand.nextInt(16);
                int bgb = rand.nextInt(48);
                int n = chunkZ * 16 + rand.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres, 2, 7)
                    .generate(world, rand, vc, bgb, n);
            }

            if (Config.getInt("settings.world.generate.silver", 1) > 0) {
                for (int a = 0; a < 4; ++a) {
                    int vc = chunkX * 16 + rand.nextInt(16);
                    int bgb = rand.nextInt(32);
                    int n = chunkZ * 16 + rand.nextInt(16);
                    new WorldGenCustomOre(RedPowerWorld.blockOres, 3, 8)
                        .generate(world, rand, vc, bgb, n);
                }
            }

            if (Config.getInt("settings.world.generate.tin", 1) > 0) {
                for (int a = 0; a < 10; ++a) {
                    int vc = chunkX * 16 + rand.nextInt(16);
                    int bgb = rand.nextInt(48);
                    int n = chunkZ * 16 + rand.nextInt(16);
                    new WorldGenCustomOre(RedPowerWorld.blockOres, 4, 8)
                        .generate(world, rand, vc, bgb, n);
                }
            }

            if (Config.getInt("settings.world.generate.copper", 1) > 0) {
                for (int a = 0; a < 20; ++a) {
                    int vc = chunkX * 16 + rand.nextInt(16);
                    int bgb = rand.nextInt(64);
                    int n = chunkZ * 16 + rand.nextInt(16);
                    new WorldGenCustomOre(RedPowerWorld.blockOres, 5, 8)
                        .generate(world, rand, vc, bgb, n);
                }
            }

            for (int a = 0; a < 1; ++a) {
                int vc = chunkX * 16 + rand.nextInt(16);
                int bgb = rand.nextInt(16);
                int n = chunkZ * 16 + rand.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres, 6, 4)
                    .generate(world, rand, vc, bgb, n);
            }

            for (int a = 0; a < 4; ++a) {
                int vc = chunkX * 16 + rand.nextInt(16);
                int bgb = rand.nextInt(16);
                int n = chunkZ * 16 + rand.nextInt(16);
                new WorldGenCustomOre(RedPowerWorld.blockOres, 7, 10)
                    .generate(world, rand, vc, bgb, n);
            }

            if (Config.getInt("settings.world.generate.marble", 1) > 0) {
                for (int a = 0; a < 4; ++a) {
                    int vc = chunkX * 16 + rand.nextInt(16);
                    int bgb = 32 + rand.nextInt(32);
                    int n = chunkZ * 16 + rand.nextInt(16);
                    new WorldGenMarble(RedPowerWorld.blockStone, 0, rand.nextInt(4096))
                        .generate(world, rand, vc, bgb, n);
                }
            }

            if (Config.getInt("settings.world.generate.volcano", 1) > 0
                && rand.nextFloat() <= 0.04F) {
                int vc = Math.max(1, rand.nextInt(10) - 6);
                vc *= vc;

                for (int a = 0; a < vc; ++a) {
                    int bgb = chunkX * 16 + rand.nextInt(16);
                    int n = rand.nextInt(32);
                    int x = chunkZ * 16 + rand.nextInt(16);
                    if (new WorldGenVolcano(
                            RedPowerWorld.blockStone, 1, rand.nextInt(65536)
                        )
                            .generate(world, rand, bgb, n, x)) {
                        break;
                    }
                }
            }

            byte ampl = 0;
            if (BiomeDictionary.isBiomeOfType(biome, Type.JUNGLE)) {
                ampl = 1;
            } else if (BiomeDictionary.isBiomeOfType(biome, Type.FOREST)) {
                ampl = 1;
            } else if (BiomeDictionary.isBiomeOfType(biome, Type.PLAINS)) {
                ampl = 4;
            }

            for (int a = 0; a < ampl; ++a) {
                int x = chunkX * 16 + rand.nextInt(16) + 8;
                int z = rand.nextInt(128);
                int y = chunkZ * 16 + rand.nextInt(16) + 8;
                new WorldGenFlowers(RedPowerWorld.blockPlants)
                    .generate(world, rand, x, z, y);
            }

            if (BiomeDictionary.isBiomeOfType(biome, Type.JUNGLE)) {
                for (int a = 0; a < 6; ++a) {
                    int x = chunkX * 16 + rand.nextInt(16) + 8;
                    int z = chunkZ * 16 + rand.nextInt(16) + 8;
                    int y = world.getHeightValue(x, z);
                    new WorldGenRubberTree().generate(world, world.rand, x, y, z);
                }
            }
        }
    }
}
