package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

import com.eloraam.redpower.RedPowerCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockExtended extends BlockContainer {
    private Supplier<? extends TileExtended>[] tileEntityMap = new Supplier[16];

    public BlockExtended(Material m) {
        super(m);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public int damageDropped(int i) {
        return i;
    }

    public float getHardness() {
        return super.blockHardness;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {}

    public ArrayList<ItemStack>
    getDrops(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> ist = new ArrayList();
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        if (tl == null) {
            return ist;
        } else {
            tl.addHarvestContents(ist);
            return ist;
        }
    }

    public Item getItemDropped(int i, Random random, int j) {
        return Item.getItemFromBlock(Blocks.air);
    }

    public void
    harvestBlock(World world, EntityPlayer player, int x, int y, int z, int side) {}

    public boolean removedByPlayer(
        World world, EntityPlayer player, int x, int y, int z, boolean willHarvest
    ) {
        if (!world.isRemote) {
            Block bl = world.getBlock(x, y, z);
            int md = world.getBlockMetadata(x, y, z);
            if (bl == null) {
                return false;
            } else {
                if (bl.canHarvestBlock(player, md) && willHarvest) {
                    for (ItemStack it : this.getDrops(
                             world,
                             x,
                             y,
                             z,
                             md,
                             EnchantmentHelper.getFortuneModifier(player)
                         )) {
                        CoreLib.dropItem(world, x, y, z, it);
                    }
                }

                world.setBlockToAir(x, y, z);
                return true;
            }
        } else {
            return true;
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        if (tl == null) {
            world.setBlockToAir(x, y, z);
        } else {
            tl.onBlockNeighborChange(block);
        }
    }

    public int onBlockPlaced(
        World world,
        int x,
        int y,
        int z,
        int side,
        float hitX,
        float hitY,
        float hitZ,
        int meta
    ) {
        return super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    }

    public void onBlockPlacedBy(
        World world, int x, int y, int z, int side, EntityLivingBase ent, ItemStack ist
    ) {
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        if (tl != null) {
            tl.onBlockPlaced(ist, side, ent);
        }
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int md) {
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        if (tl != null) {
            tl.onBlockRemoval();
            super.breakBlock(world, x, y, z, block, md);
        }
    }

    public int isProvidingStrongPower(IBlockAccess iba, int x, int y, int z, int side) {
        TileExtended tl = CoreLib.getTileEntity(iba, x, y, z, TileExtended.class);
        return tl != null && tl.isBlockStrongPoweringTo(side) ? 15 : 0;
    }

    public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int side) {
        TileExtended tl = CoreLib.getTileEntity(iba, x, y, z, TileExtended.class);
        return tl != null && tl.isBlockWeakPoweringTo(side) ? 15 : 0;
    }

    public boolean onBlockActivated(
        World world,
        int x,
        int y,
        int z,
        EntityPlayer player,
        int side,
        float xp,
        float yp,
        float zp
    ) {
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        return tl != null && tl.onBlockActivated(player);
    }

    public void
    onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        if (tl != null) {
            tl.onEntityCollidedWithBlock(entity);
        }
    }

    public AxisAlignedBB
    getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileExtended tl = CoreLib.getTileEntity(world, x, y, z, TileExtended.class);
        if (tl != null) {
            AxisAlignedBB bb = tl.getCollisionBoundingBox();
            if (bb != null) {
                return bb;
            }
        }

        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public AxisAlignedBB
    getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    public int getRenderType() {
        return RedPowerCore.customBlockModel;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int md = world.getBlockMetadata(x, y, z);
        RenderCustomBlock rend = RenderLib.getRenderer(this, md);
        if (rend != null) {
            rend.randomDisplayTick(world, x, y, z, random);
        }
    }

    public void addTileEntityMapping(int md, Supplier<? extends TileExtended> cl) {
        this.tileEntityMap[md] = cl;
    }

    public void setBlockName(int md, String name) {
        Item item = Item.getItemFromBlock(this);
        ((ItemExtended) item).setMetaName(md, "tile." + name);
    }

    public TileEntity createNewTileEntity(World world, int md) {
        return this.tileEntityMap[md] != null ? (TileEntity) this.tileEntityMap[md].get()
                                              : null;
    }

    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(
        World world, MovingObjectPosition target, EffectRenderer effectRenderer
    ) {
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        int meta = world.getBlockMetadata(x, y, z);
        int side = target.sideHit;
        RenderCustomBlock renderer = RenderLib.getRenderer(this, meta);
        return renderer != null
            && renderer.renderHit(effectRenderer, world, target, x, y, z, side, meta);
    }

    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer effectRenderer
    ) {
        RenderCustomBlock renderer = RenderLib.getRenderer(this, meta);
        return renderer != null
            && renderer.renderDestroy(effectRenderer, world, x, y, z, meta);
    }
}
