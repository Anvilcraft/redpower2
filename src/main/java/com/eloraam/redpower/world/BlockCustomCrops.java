package com.eloraam.redpower.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.eloraam.redpower.RedPowerWorld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockCustomCrops extends BlockFlower implements IGrowable {
    private IIcon[] icons = new IIcon[6];

    public BlockCustomCrops() {
        super(0);
        this.setHardness(0.0F);
        this.setStepSound(Block.soundTypeGrass);
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
    }

    public IIcon getIcon(int side, int meta) {
        if (meta > 6) {
            meta = 6;
        }

        return this.icons[meta];
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = register.registerIcon("rpworld:flaxCrop/" + i);
        }
    }

    public int getRenderType() {
        return 6;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    public boolean fertilize(World world, int x, int y, int z) {
        Random random = world.rand;
        if (world.getBlockLightValue(x, y + 1, z) < 9) {
            return false;
        } else {
            int md = world.getBlockMetadata(x, y, z);
            if (md != 4 && md != 5) {
                if (world.getBlock(x, y - 1, z) == Blocks.farmland
                    && world.getBlockMetadata(x, y - 1, z) != 0
                    && world.isAirBlock(x, y + 1, z)) {
                    if (random.nextBoolean()) {
                        world.setBlockMetadataWithNotify(x, y, z, md + 1, 3);
                        if (md == 3) {
                            world.setBlock(x, y + 1, z, this, 1, 3);
                        }

                        return true;
                    }
                } else if (world.getBlock(x, y - 2, z) == Blocks.farmland
               && world.getBlockMetadata(x, y - 2, z) != 0
               && world.isAirBlock(x, y + 1, z)
               && random.nextBoolean()) {
                    if (md + 1 < 4) {
                        world.setBlock(x, y, z, this, md + 1, 3);
                        return true;
                    }

                    if (world.getBlockMetadata(x, y, z) != 5) {
                        world.setBlock(x, y, z, this, 5, 3);
                        return true;
                    }

                    return false;
                }
            } else if (world.getBlock(x, y - 1, z) == Blocks.farmland
            && world.getBlockMetadata(x, y - 1, z) != 0
            && world.isAirBlock(x, y + 2, z)
            && world.getBlock(x, y + 1, z) == this
            && world.getBlockMetadata(x, y + 1, z) <= 3
            && random.nextBoolean()) {
                int mdup = world.getBlockMetadata(x, y + 1, z);
                if (mdup + 1 <= 3) {
                    world.setBlock(x, y + 1, z, this, mdup + 1, 3);
                    return true;
                }

                if (world.getBlockMetadata(x, y + 1, z) != 5) {
                    world.setBlock(x, y + 1, z, this, 5, 3);
                    return true;
                }

                return false;
            }

            return false;
        }
    }

    public ArrayList<ItemStack>
    getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> tr = new ArrayList();
        if (metadata == 5) {
            int n = 1 + world.rand.nextInt(3) + world.rand.nextInt(1 + fortune);

            while (n-- > 0) {
                tr.add(new ItemStack(Items.string));
            }
        } else {
            for (int n = 0; n < 3 + fortune; ++n) {
                if (world.rand.nextInt(8) <= metadata) {
                    tr.add(new ItemStack(RedPowerWorld.itemSeeds, 1, 0));
                }
            }
        }

        return tr;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.getBlockLightValue(x, y + 1, z) >= 9) {
            int md = world.getBlockMetadata(x, y, z);
            if (md != 4 && md != 5) {
                if (world.getBlock(x, y - 1, z) == Blocks.farmland
                    && world.getBlockMetadata(x, y - 1, z) != 0
                    && world.isAirBlock(x, y + 1, z)) {
                    if (random.nextBoolean()) {
                        world.setBlockMetadataWithNotify(x, y, z, md + 1, 3);
                        if (md == 3) {
                            world.setBlock(x, y + 1, z, this, 1, 3);
                        }
                    }
                } else if (world.getBlock(x, y - 2, z) == Blocks.farmland
               && world.getBlockMetadata(x, y - 2, z) != 0
               && world.isAirBlock(x, y + 1, z)
               && random.nextBoolean()) {
                    if (md + 1 < 4) {
                        world.setBlock(x, y, z, this, md + 1, 3);
                    } else if (world.getBlockMetadata(x, y, z) != 5) {
                        world.setBlock(x, y, z, this, 5, 3);
                    }
                }
            }
        }
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (world.getBlock(x, y - 1, z) == Blocks.farmland
            && world.getBlockMetadata(x, y - 1, z) > 0) {
            if (meta != 4) {
                return true;
            } else {
                int upperMeta = world.getBlockMetadata(x, y + 1, z);
                return world.getBlock(x, y + 1, z) == this && upperMeta != 4
                    && world.getBlockLightValue(x, y + 1, z) >= 9;
            }
        } else if (world.getBlock(x, y - 2, z) == Blocks.farmland && world.getBlockMetadata(x, y - 2, z) > 0) {
            int lowerMeta = world.getBlockMetadata(x, y - 1, z);
            return world.getBlock(x, y - 1, z) == this && lowerMeta == 4
                && world.getBlockLightValue(x, y, z) >= 9;
        } else {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List items) {}

    public AxisAlignedBB
    getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        double sy = (double) y;
        double ex = (double) x + 1.0;
        double ey = (double) y + 1.0;
        double ez = (double) z + 1.0;
        if (world.getBlock(x, y - 1, z) == this
            && world.getBlockMetadata(x, y - 1, z) == 4) {
            --sy;
            ey = (double) y + 0.25 * (double) Math.min(4, meta);
        } else if (meta == 4 && world.getBlock(x, y + 1, z) == this) {
            int upperMeta = world.getBlockMetadata(x, y + 1, z);
            ey = (double) y + 1.0
                + 0.25 * (double) Math.min(4, upperMeta == 5 ? 4 : upperMeta);
        } else if (meta < 4) {
            ey = (double) y + 0.25 * (double) meta;
        }

        return AxisAlignedBB.getBoundingBox((double) x, sy, (double) z, ex, ey, ez);
    }

    public ItemStack getPickBlock(
        MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player
    ) {
        return new ItemStack(RedPowerWorld.itemSeeds, 1, 0);
    }

    public boolean
    func_149851_a(World world, int x, int y, int z, boolean isWorldRemote) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 4) {
            return meta < 5;
        } else {
            return world.getBlock(x, y + 1, z) == this
                && world.getBlockMetadata(x, y + 1, z) < 5;
        }
    }

    public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
        return world.rand.nextFloat() < 0.45F;
    }

    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 4 && world.getBlock(x, y + 1, z) == this
            && world.getBlockMetadata(x, y + 1, z) < 5) {
            this.fertilize(world, x, y + 1, z);
        } else {
            this.fertilize(world, x, y, z);
        }
    }
}
