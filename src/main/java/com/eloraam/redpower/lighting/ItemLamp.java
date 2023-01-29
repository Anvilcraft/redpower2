package com.eloraam.redpower.lighting;

import com.eloraam.redpower.core.ItemExtended;
import net.minecraft.block.Block;

public class ItemLamp extends ItemExtended {
    public ItemLamp(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int meta) {
        return meta << 10;
    }
}
