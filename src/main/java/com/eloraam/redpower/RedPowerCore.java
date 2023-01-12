package com.eloraam.redpower;

import java.io.File;

import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreEvents;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverRecipe;
import com.eloraam.redpower.core.PacketHandler;
import com.eloraam.redpower.core.RenderHighlight;
import com.eloraam.redpower.core.RenderSimpleCovered;
import com.eloraam.redpower.core.TileCovered;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "RedPowerCore", name = "RedPower Core", version = "2.0pr6")
public class RedPowerCore {
    @Instance("RedPowerCore")
    public static RedPowerCore instance;
    public static PacketHandler packetHandler = new PacketHandler();
    public static int customBlockModel = -1;
    public static int nullBlockModel = -1;
    @SideOnly(Side.CLIENT)
    public static IIcon missing;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.loadConfig();
        CoreLib.readOres();
        MinecraftForge.EVENT_BUS.register(new CoreEvents());
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        packetHandler.init();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.setupRenderers();
        }

        CraftingManager.getInstance().getRecipeList().add(new CoverRecipe());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Config.saveConfig();
    }

    public static File getSaveDir(World world) {
        return DimensionManager.getCurrentSaveRootDirectory();
    }

    public static void sendPacketToServer(IMessage msg) {
        packetHandler.sendToServer(msg);
    }

    public static void sendPacketToCrafting(ICrafting icr, IMessage msg) {
        if (icr instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) icr;
            packetHandler.sendTo(msg, player);
        }
    }

    @SideOnly(Side.CLIENT)
    public void setupRenderers() {
        customBlockModel = RenderingRegistry.getNextAvailableRenderId();
        nullBlockModel = RenderingRegistry.getNextAvailableRenderId();
        MinecraftForge.EVENT_BUS.register(new RenderHighlight());
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileCovered.class, new RenderSimpleCovered()
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(Pre evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            missing = map.registerIcon("rpcore:missing");
        }
    }
}
