package com.eloraam.redpower.compat;

import java.util.List;

import com.eloraam.redpower.core.TileCoverable;
import cpw.mods.fml.common.FMLCommonHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Waila implements IWailaDataProvider {
    public static void register(final IWailaRegistrar registrar) {
        FMLCommonHandler.instance().getFMLLogger().info(
            "[RedPower Compat] registering waila plugin"
        );
        registrar.registerStackProvider(new Waila(), TileCoverable.class);
    }

    @Override
    public ItemStack
    getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileCoverable te = (TileCoverable) accessor.getTileEntity();
        return te.getPickBlock(accessor.getPosition(), accessor.getPlayer());
    }

    @Override
    public List<String> getWailaHead(
        ItemStack itemStack,
        List<String> currenttip,
        IWailaDataAccessor accessor,
        IWailaConfigHandler config
    ) {
        return null;
    }

    @Override
    public List<String> getWailaBody(
        ItemStack itemStack,
        List<String> currenttip,
        IWailaDataAccessor accessor,
        IWailaConfigHandler config
    ) {
        return null;
    }

    @Override
    public List<String> getWailaTail(
        ItemStack itemStack,
        List<String> currenttip,
        IWailaDataAccessor accessor,
        IWailaConfigHandler config
    ) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(
        EntityPlayerMP player,
        TileEntity te,
        NBTTagCompound tag,
        World world,
        int x,
        int y,
        int z
    ) {
        return null;
    }
}
