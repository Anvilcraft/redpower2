package com.eloraam.redpower.world;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStorage extends Block {
    private IIcon[] icons = new IIcon[8];

    public BlockStorage() {
        super(Material.iron);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundTypeMetal);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = register.registerIcon("rpworld:storage/" + i);
        }
    }

    public IIcon getIcon(int side, int meta) {
        return this.icons[meta];
    }

    public int damageDropped(int meta) {
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List items) {
        for (int i = 0; i < 8; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }
}
