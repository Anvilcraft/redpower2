package com.eloraam.redpower.world;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemCustomFlower extends ItemBlock {
    private BlockCustomFlower bl;

    public ItemCustomFlower(Block block) {
        super((BlockCustomFlower) block);
        this.bl = (BlockCustomFlower) block;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public IIcon getIconFromDamage(int damage) {
        return this.bl.icons[damage];
    }

    public int getPlacedBlockMetadata(int i) {
        return i;
    }

    public int getMetadata(int i) {
        return i;
    }

    public String getUnlocalizedName(ItemStack itemstack) {
        switch (itemstack.getItemDamage()) {
            case 0:
                return "tile.indigo";
            case 1:
                return "tile.rubbersapling";
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List itemList) {
        this.bl.getSubBlocks(item, tab, itemList);
    }
}
