package com.eloraam.redpower;

import com.eloraam.redpower.compat.BlockMachineCompat;
import com.eloraam.redpower.compat.ComputercraftInterop;
import com.eloraam.redpower.compat.ItemMachineCompat;
import com.eloraam.redpower.compat.RenderBlueEngine;
import com.eloraam.redpower.compat.TileBlueEngine;
import com.eloraam.redpower.compat.Waila;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.core.RenderLib;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import ic2.api.recipe.ICraftingRecipeManager;
//import ic2.api.recipe.Recipes;
//import ic2.core.Ic2Items;
//import ic2.core.block.machine.tileentity.TileEntityMacerator;
//import ic2.core.block.machine.tileentity.TileEntityRotary;
//import ic2.core.block.machine.tileentity.TileEntitySingularity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mod(
    modid = "RedPowerCompat",
    name = "RedPower Compat",
    version = RedPowerBase.VERSION,
    dependencies
    = "required-after:RedPowerBase;required-after:RedPowerMachine;required-after:RedPowerWorld;after:IC2;after:Waila"
)
public class RedPowerCompat implements IGuiHandler {
    @Instance("RedPowerCompat")
    public static RedPowerCompat instance;
    public static BlockMachineCompat blockMachineCompat;
    public static ItemParts itemCompatParts;
    public static ItemStack itemGearBrass;
    public static ItemStack itemDenseTungstenPlate;
    static boolean ic2reworked;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        for (ModContainer modContainer : Loader.instance().getActiveModList()) {
            if (modContainer.getName().equalsIgnoreCase("Industrial Craft Reworked")) {
                ic2reworked = true;
                break;
            }
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        this.setupBlocks();
        if (event.getSide().isClient()) {
            this.registerRenderers();
        }
        if (Loader.isModLoaded("ComputerCraft")) {
            ComputercraftInterop.initInterop();
        }

        if (Loader.isModLoaded("Waila")) {
            FMLInterModComms.sendMessage(
                "Waila", "register", Waila.class.getName() + ".register"
            );
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        RenderLib.setRenderer(blockMachineCompat, 0, RenderBlueEngine::new);
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBlueEngine.class, new RenderBlueEngine(blockMachineCompat)
        );
    }

    private void setupBlocks() {
        GameRegistry.registerTileEntity(TileBlueEngine.class, "RPBTEngine");
        blockMachineCompat = new BlockMachineCompat();
        GameRegistry.registerBlock(blockMachineCompat, ItemMachineCompat.class, "compat");
        blockMachineCompat.setBlockName(0, "rpbtengine");
        blockMachineCompat.addTileEntityMapping(0, TileBlueEngine::new);
        itemCompatParts = new ItemParts();
        itemCompatParts.addItem(0, "rpcompat:gear", "item.rpbgear");
        itemCompatParts.addItem(
            1, "rpcompat:densePlateTungsten", "item.densePlateTungsten"
        );
        itemCompatParts.setCreativeTab(CreativeTabs.tabMaterials);
        GameRegistry.registerItem(itemCompatParts, "parts");
        itemGearBrass = new ItemStack(itemCompatParts, 1, 0);
        itemDenseTungstenPlate = new ItemStack(itemCompatParts, 1, 1);
        CraftLib.addOreRecipe(
            new ItemStack(itemCompatParts, 1, 0),
            " B ",
            "BIB",
            " B ",
            'B',
            "ingotBrass",
            'I',
            new ItemStack(RedPowerBase.blockMicro, 1, 5649)
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachineCompat, 1, 0),
            "BBB",
            " G ",
            "ZMZ",
            'B',
            "ingotBrass",
            'G',
            Blocks.glass,
            'Z',
            itemGearBrass,
            'M',
            RedPowerBase.itemMotor
        );
        //TODO: IC2 Classic compat
        /*if (ic2reworked) {
           TileEntityRotary.addRecipe(new ItemStack(RedPowerWorld.blockOres, 1, 6), new
        ItemStack(RedPowerBase.itemResource, 2, 8)); TileEntityMacerator.addRecipe(new
        ItemStack(RedPowerWorld.blockOres, 1, 3), new ItemStack(RedPowerBase.itemResource,
        2, 9)); TileEntityRotary.addRecipe(RedPowerBase.itemIngotTungsten, new
        ItemStack(RedPowerBase.itemResource, 1, 8));
           TileEntitySingularity.addRecipe(CoreLib.copyStack(RedPowerBase.itemIngotTungsten,
        8), itemDenseTungstenPlate); ICraftingRecipeManager advRecipes =
        Recipes.advRecipes; advRecipes.addRecipe(RedPowerBase.itemRuby, new Object[]{"
        MM", "MMM", "MM ", 'M', Ic2Items.matter, true});
           advRecipes.addRecipe(RedPowerBase.itemSapphire, new Object[]{"MM ", "MMM", "
        MM", 'M', Ic2Items.matter, true});
           advRecipes.addRecipe(RedPowerBase.itemGreenSapphire, new Object[]{" MM", "MMM",
        " MM", 'M', Ic2Items.matter, true});
           advRecipes.addRecipe(RedPowerBase.itemNikolite, new Object[]{"MMM", " M ", 'M',
        Ic2Items.matter, true}); advRecipes.addRecipe(RedPowerBase.itemDustSilver, new
        Object[]{"  M", " MM", "  M", 'M', Ic2Items.matter, true});
           advRecipes.addRecipe(RedPowerBase.itemDustTungsten, new Object[]{"MMM", "MDM",
        "MMM", 'M', Ic2Items.matter, 'D', Items.diamond, true});
           advRecipes.addRecipe(RedPowerWorld.itemMarble, new Object[]{"M  ", "   ", " ",
        'M', Ic2Items.matter, true}); advRecipes.addRecipe(RedPowerWorld.itemBasalt, new
        Object[]{"  M", "   ", "   ", 'M', Ic2Items.matter, true});
        }*/
    }

    public Object
    getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public Object
    getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
