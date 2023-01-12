package com.eloraam.redpower.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemPlan extends Item {
    public ItemPlan() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("planFull");
        this.setTextureName("rpbase:planFull");
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack ist) {
        if (ist.stackTagCompound == null) {
            return super.getItemStackDisplayName(ist);
        } else if (!ist.stackTagCompound.hasKey("result")) {
            return super.getItemStackDisplayName(ist);
        } else {
            NBTTagCompound res = ist.stackTagCompound.getCompoundTag("result");
            ItemStack result = ItemStack.loadItemStackFromNBT(res);
            return result.getItem().getItemStackDisplayName(result) + " Plan";
        }
    }

    public void
    addInformation(ItemStack ist, EntityPlayer player, List lines, boolean par4) {
        if (ist.stackTagCompound != null) {
            NBTTagList require = ist.stackTagCompound.getTagList("requires", 10);
            if (require != null) {
                HashMap<HashMap<Item, Integer>, Integer> counts = new HashMap();

                for (int i = 0; i < require.tagCount(); ++i) {
                    NBTTagCompound kv = require.getCompoundTagAt(i);
                    ItemStack li = ItemStack.loadItemStackFromNBT(kv);
                    HashMap<Item, Integer> i2d = new HashMap();
                    i2d.put(li.getItem(), li.getItemDamage());
                    Integer lc = (Integer) counts.get(i2d);
                    if (lc == null) {
                        lc = 0;
                    }

                    counts.put(i2d, lc + 1);
                }

                for (Entry<HashMap<Item, Integer>, Integer> entry : counts.entrySet()) {
                    HashMap<Item, Integer> keySet = (HashMap) entry.getKey();
                    ItemStack itemStack = new ItemStack(
                        (Item) keySet.keySet().iterator().next(),
                        1,
                        keySet.values().iterator().next()
                    );
                    lines.add(
                        entry.getValue() + " x "
                        + itemStack.getItem().getItemStackDisplayName(itemStack)
                    );
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack ist) {
        return EnumRarity.rare;
    }

    public boolean getShareTag() {
        return true;
    }
}
