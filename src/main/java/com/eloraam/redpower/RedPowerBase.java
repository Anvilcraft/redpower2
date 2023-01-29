package com.eloraam.redpower;

import com.eloraam.redpower.base.BlockAppliance;
import com.eloraam.redpower.base.BlockMicro;
import com.eloraam.redpower.base.ContainerAdvBench;
import com.eloraam.redpower.base.ContainerAlloyFurnace;
import com.eloraam.redpower.base.ContainerBag;
import com.eloraam.redpower.base.ContainerBusId;
import com.eloraam.redpower.base.GuiAdvBench;
import com.eloraam.redpower.base.GuiAlloyFurnace;
import com.eloraam.redpower.base.GuiBag;
import com.eloraam.redpower.base.GuiBusId;
import com.eloraam.redpower.base.ItemBag;
import com.eloraam.redpower.base.ItemDrawplate;
import com.eloraam.redpower.base.ItemDyeIndigo;
import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.base.ItemMicro;
import com.eloraam.redpower.base.ItemPlan;
import com.eloraam.redpower.base.ItemScrewdriver;
import com.eloraam.redpower.base.RecipeBag;
import com.eloraam.redpower.base.RenderAdvBench;
import com.eloraam.redpower.base.RenderAlloyFurnace;
import com.eloraam.redpower.base.TileAdvBench;
import com.eloraam.redpower.base.TileAlloyFurnace;
import com.eloraam.redpower.core.AchieveLib;
import com.eloraam.redpower.core.BlockMultiblock;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.core.OreStack;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.core.TileMultiblock;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

@Mod(
    modid = "RedPowerBase",
    name = "RedPower Base",
    version = "2.0pr7",
    dependencies = "required-after:RedPowerCore"
)
public class RedPowerBase implements IGuiHandler {
    @Instance("RedPowerBase")
    public static RedPowerBase instance;
    public static BlockAppliance blockAppliance;
    public static Item itemHandsawIron;
    public static Item itemHandsawDiamond;
    public static ItemParts itemLumar;
    public static ItemParts itemResource;
    public static ItemStack itemRuby;
    public static ItemStack itemGreenSapphire;
    public static ItemStack itemSapphire;
    public static ItemStack itemIngotSilver;
    public static ItemStack itemIngotTin;
    public static ItemStack itemIngotCopper;
    public static ItemStack itemIngotTungsten;
    public static ItemStack itemDustTungsten;
    public static ItemStack itemDustSilver;
    public static ItemStack itemNikolite;
    public static ItemParts itemAlloy;
    public static ItemStack itemIngotRed;
    public static ItemStack itemIngotBlue;
    public static ItemStack itemIngotBrass;
    public static ItemStack itemBouleSilicon;
    public static ItemStack itemWaferSilicon;
    public static ItemStack itemWaferBlue;
    public static ItemStack itemWaferRed;
    public static ItemStack itemTinplate;
    public static ItemStack itemFineCopper;
    public static ItemStack itemFineIron;
    public static ItemStack itemCopperCoil;
    public static ItemStack itemMotor;
    public static ItemStack itemCanvas;
    public static ItemParts itemNugget;
    public static ItemStack itemNuggetIron;
    public static ItemStack itemNuggetSilver;
    public static ItemStack itemNuggetTin;
    public static ItemStack itemNuggetCopper;
    public static ItemStack itemNuggetTungsten;
    public static Item itemDyeIndigo;
    public static BlockMicro blockMicro;
    public static BlockMultiblock blockMultiblock;
    public static ItemScrewdriver itemScrewdriver;
    public static Item itemDrawplateDiamond;
    public static Item itemPlanBlank;
    public static Item itemPlanFull;
    public static Item itemBag;
    @SideOnly(Side.CLIENT)
    public static IIcon projectTableTop;
    @SideOnly(Side.CLIENT)
    public static IIcon projectTableBottom;
    @SideOnly(Side.CLIENT)
    public static IIcon projectTableFront;
    @SideOnly(Side.CLIENT)
    public static IIcon projectTableSide;
    @SideOnly(Side.CLIENT)
    public static IIcon alloyFurnaceVert;
    @SideOnly(Side.CLIENT)
    public static IIcon alloyFurnaceSide;
    @SideOnly(Side.CLIENT)
    public static IIcon alloyFurnaceFront;
    @SideOnly(Side.CLIENT)
    public static IIcon alloyFurnaceFrontOn;

    public static void initBaseItems() {
        itemLumar = new ItemParts();
        itemLumar.setCreativeTab(CreativeTabs.tabMaterials);

        for (int color = 0; color < 16; ++color) {
            itemLumar.addItem(
                color,
                "rpbase:lumar/" + color,
                "item.rplumar." + CoreLib.rawColorNames[color]
            );
            ItemStack dye = new ItemStack(Items.dye, 1, 15 - color);
            GameRegistry.addShapelessRecipe(
                new ItemStack(itemLumar, 2, color),
                new Object[] { Items.redstone, dye, dye, Items.glowstone_dust }
            );
        }

        itemResource = new ItemParts();
        itemAlloy = new ItemParts();
        itemResource.setCreativeTab(CreativeTabs.tabMaterials);
        itemAlloy.setCreativeTab(CreativeTabs.tabMaterials);
        itemResource.addItem(0, "rpbase:ruby", "item.ruby");
        itemResource.addItem(1, "rpbase:greenSapphire", "item.greenSapphire");
        itemResource.addItem(2, "rpbase:sapphire", "item.sapphire");
        itemResource.addItem(3, "rpbase:silverIngot", "item.ingotSilver");
        itemResource.addItem(4, "rpbase:tinIngot", "item.ingotTin");
        itemResource.addItem(5, "rpbase:copperIngot", "item.ingotCopper");
        itemResource.addItem(6, "rpbase:nikolite", "item.nikolite");
        itemResource.addItem(7, "rpbase:ingotTungsten", "item.ingotTungsten");
        itemResource.addItem(8, "rpbase:dustTungsten", "item.dustTungsten");
        itemResource.addItem(9, "rpbase:dustSilver", "item.dustSilver");
        itemAlloy.addItem(0, "rpbase:ingotRed", "item.ingotRed");
        itemAlloy.addItem(1, "rpbase:ingotBlue", "item.ingotBlue");
        itemAlloy.addItem(2, "rpbase:ingotBrass", "item.ingotBrass");
        itemAlloy.addItem(3, "rpbase:bouleSilicon", "item.bouleSilicon");
        itemAlloy.addItem(4, "rpbase:waferSilicon", "item.waferSilicon");
        itemAlloy.addItem(5, "rpbase:waferBlue", "item.waferBlue");
        itemAlloy.addItem(6, "rpbase:waferRed", "item.waferRed");
        itemAlloy.addItem(7, "rpbase:tinPlate", "item.tinplate");
        itemAlloy.addItem(8, "rpbase:fineCopper", "item.finecopper");
        itemAlloy.addItem(9, "rpbase:fineIron", "item.fineiron");
        itemAlloy.addItem(10, "rpbase:copperCoil", "item.coppercoil");
        itemAlloy.addItem(11, "rpbase:btMotor", "item.btmotor");
        itemAlloy.addItem(12, "rpbase:canvas", "item.rpcanvas");
        itemRuby = new ItemStack(itemResource, 1, 0);
        itemGreenSapphire = new ItemStack(itemResource, 1, 1);
        itemSapphire = new ItemStack(itemResource, 1, 2);
        itemIngotSilver = new ItemStack(itemResource, 1, 3);
        itemIngotTin = new ItemStack(itemResource, 1, 4);
        itemIngotCopper = new ItemStack(itemResource, 1, 5);
        itemNikolite = new ItemStack(itemResource, 1, 6);
        itemIngotTungsten = new ItemStack(itemResource, 1, 7);
        itemDustTungsten = new ItemStack(itemResource, 1, 8);
        itemDustSilver = new ItemStack(itemResource, 1, 9);
        itemIngotRed = new ItemStack(itemAlloy, 1, 0);
        itemIngotBlue = new ItemStack(itemAlloy, 1, 1);
        itemIngotBrass = new ItemStack(itemAlloy, 1, 2);
        itemBouleSilicon = new ItemStack(itemAlloy, 1, 3);
        itemWaferSilicon = new ItemStack(itemAlloy, 1, 4);
        itemWaferBlue = new ItemStack(itemAlloy, 1, 5);
        itemWaferRed = new ItemStack(itemAlloy, 1, 6);
        itemTinplate = new ItemStack(itemAlloy, 1, 7);
        itemFineCopper = new ItemStack(itemAlloy, 1, 8);
        itemFineIron = new ItemStack(itemAlloy, 1, 9);
        itemCopperCoil = new ItemStack(itemAlloy, 1, 10);
        itemMotor = new ItemStack(itemAlloy, 1, 11);
        itemCanvas = new ItemStack(itemAlloy, 1, 12);
        itemNugget = new ItemParts();
        itemNugget.setCreativeTab(CreativeTabs.tabMaterials);
        itemNugget.addItem(0, "rpbase:nuggetIron", "item.nuggetIron");
        itemNugget.addItem(1, "rpbase:nuggetSilver", "item.nuggetSilver");
        itemNugget.addItem(2, "rpbase:nuggetTin", "item.nuggetTin");
        itemNugget.addItem(3, "rpbase:nuggetCopper", "item.nuggetCopper");
        itemNugget.addItem(4, "rpbase:nuggetTungsten", "item.nuggetTungsten");
        itemNuggetIron = new ItemStack(itemNugget, 1, 0);
        itemNuggetSilver = new ItemStack(itemNugget, 1, 1);
        itemNuggetTin = new ItemStack(itemNugget, 1, 2);
        itemNuggetCopper = new ItemStack(itemNugget, 1, 3);
        itemNuggetTungsten = new ItemStack(itemNugget, 1, 4);
        itemDrawplateDiamond = new ItemDrawplate();
        itemDrawplateDiamond.setUnlocalizedName("drawplateDiamond")
            .setMaxDamage(255)
            .setTextureName("rpbase:diamondDrawplate");
        GameRegistry.registerItem(itemDrawplateDiamond, "drawplateDiamond");
        itemBag = new ItemBag();
        GameRegistry.addRecipe(
            new ItemStack(itemBag, 1, 0),
            new Object[] { "CCC", "C C", "CCC", 'C', itemCanvas }
        );

        for (int color = 1; color < 16; ++color) {
            GameRegistry.addRecipe(
                new ItemStack(itemBag, 1, color),
                new Object[] { "CCC",
                               "CDC",
                               "CCC",
                               'C',
                               itemCanvas,
                               'D',
                               new ItemStack(Items.dye, 1, 15 - color) }
            );
        }

        GameRegistry.registerItem(itemLumar, "lumar");
        GameRegistry.registerItem(itemResource, "resource");
        OreDictionary.registerOre("gemRuby", itemRuby);
        OreDictionary.registerOre("gemGreenSapphire", itemGreenSapphire);
        OreDictionary.registerOre("gemSapphire", itemSapphire);
        OreDictionary.registerOre("ingotTin", itemIngotTin);
        OreDictionary.registerOre("ingotCopper", itemIngotCopper);
        OreDictionary.registerOre("ingotSilver", itemIngotSilver);
        OreDictionary.registerOre("ingotTungsten", itemIngotTungsten);
        OreDictionary.registerOre("dustNikolite", itemNikolite);
        OreDictionary.registerOre("dustTungsten", itemDustTungsten);
        GameRegistry.registerItem(itemAlloy, "alloy");
        OreDictionary.registerOre("ingotBrass", itemIngotBrass);
        GameRegistry.registerItem(itemNugget, "nugget");
        OreDictionary.registerOre("nuggetIron", itemNuggetIron);
        OreDictionary.registerOre("nuggetSilver", itemNuggetSilver);
        OreDictionary.registerOre("nuggetTin", itemNuggetTin);
        OreDictionary.registerOre("nuggetCopper", itemNuggetCopper);
        OreDictionary.registerOre("nuggetTungsten", itemNuggetTungsten);
        GameRegistry.registerItem(itemBag, "canvasBag");
    }

    public static void initIndigo() {
        itemDyeIndigo = new ItemDyeIndigo();
        GameRegistry.registerItem(itemDyeIndigo, "dyeIndigo");
        OreDictionary.registerOre("dyeBlue", new ItemStack(itemDyeIndigo));
        GameRegistry.addShapelessRecipe(
            new ItemStack(Blocks.wool, 1, 11), new Object[] { itemDyeIndigo, Blocks.wool }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.dye, 2, 12),
            new Object[] { itemDyeIndigo, new ItemStack(Items.dye, 1, 15) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.dye, 2, 6),
            new Object[] { itemDyeIndigo, new ItemStack(Items.dye, 1, 2) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.dye, 2, 5),
            new Object[] { itemDyeIndigo, new ItemStack(Items.dye, 1, 1) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.dye, 3, 13),
            new Object[] { itemDyeIndigo,
                           new ItemStack(Items.dye, 1, 1),
                           new ItemStack(Items.dye, 1, 9) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.dye, 4, 13),
            new Object[] { itemDyeIndigo,
                           new ItemStack(Items.dye, 1, 1),
                           new ItemStack(Items.dye, 1, 1),
                           new ItemStack(Items.dye, 1, 15) }
        );
        CraftLib.addShapelessOreRecipe(
            new ItemStack(itemLumar, 2, 11),
            Items.redstone,
            "dyeBlue",
            "dyeBlue",
            Items.glowstone_dust
        );
        CraftLib.addOreRecipe(
            new ItemStack(itemBag, 1, 11),
            "CCC",
            "CDC",
            "CCC",
            'C',
            itemCanvas,
            'D',
            "dyeBlue"
        );
        itemPlanBlank = new Item().setTextureName("rpbase:planBlank");
        itemPlanBlank.setUnlocalizedName("planBlank");
        itemPlanBlank.setCreativeTab(CreativeTabs.tabMisc);
        GameRegistry.addShapelessRecipe(
            new ItemStack(itemPlanBlank), new Object[] { Items.paper, itemDyeIndigo }
        );
        GameRegistry.registerItem(itemPlanBlank, "planBlank");
        itemPlanFull = new ItemPlan();
        GameRegistry.registerItem(itemPlanFull, "planFull");
    }

    public static void initAlloys() {
        CraftLib.addAlloyResult(
            itemIngotRed,
            new ItemStack(Items.redstone, 4),
            new ItemStack(Items.iron_ingot, 1)
        );
        CraftLib.addAlloyResult(
            itemIngotRed, new ItemStack(Items.redstone, 4), new OreStack("ingotCopper")
        );
        CraftLib.addAlloyResult(
            CoreLib.copyStack(itemIngotBrass, 4),
            new OreStack("ingotTin"),
            new OreStack("ingotCopper", 3)
        );
        CraftLib.addAlloyResult(
            CoreLib.copyStack(itemTinplate, 4),
            new OreStack("ingotTin"),
            new ItemStack(Items.iron_ingot, 2)
        );
        CraftLib.addAlloyResult(
            itemIngotBlue, new OreStack("ingotSilver"), new OreStack("dustNikolite", 4)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 3), new ItemStack(Blocks.rail, 8)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 3), new ItemStack(Items.bucket, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 5), new ItemStack(Items.minecart, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 6), new ItemStack(Items.iron_door, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 3), new ItemStack(Blocks.iron_bars, 8)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 31), new ItemStack(Blocks.anvil, 1, 0)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 31), new ItemStack(Blocks.anvil, 1, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 31), new ItemStack(Blocks.anvil, 1, 2)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.iron_sword, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 3), new ItemStack(Items.iron_pickaxe, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 3), new ItemStack(Items.iron_axe, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 1), new ItemStack(Items.iron_shovel, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.iron_hoe, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.golden_sword, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 3), new ItemStack(Items.golden_pickaxe, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 3), new ItemStack(Items.golden_axe, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 1), new ItemStack(Items.golden_shovel, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 2), new ItemStack(Items.golden_hoe, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 5), new ItemStack(Items.iron_helmet, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 8), new ItemStack(Items.iron_chestplate, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 7), new ItemStack(Items.iron_leggings, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 4), new ItemStack(Items.iron_boots, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 5), new ItemStack(Items.iron_horse_armor, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 5), new ItemStack(Items.golden_helmet, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 8), new ItemStack(Items.golden_chestplate, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 7), new ItemStack(Items.golden_leggings, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 4), new ItemStack(Items.golden_boots, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 5), new ItemStack(Items.golden_horse_armor, 1)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.gold_ingot, 1), new ItemStack(Items.gold_nugget, 9)
        );
        CraftLib.addAlloyResult(
            new ItemStack(Items.iron_ingot, 1), CoreLib.copyStack(itemNuggetIron, 9)
        );
        CraftLib.addAlloyResult(itemIngotSilver, CoreLib.copyStack(itemNuggetSilver, 9));
        CraftLib.addAlloyResult(itemIngotCopper, CoreLib.copyStack(itemNuggetCopper, 9));
        CraftLib.addAlloyResult(itemIngotTin, CoreLib.copyStack(itemNuggetTin, 9));
        CraftLib.addAlloyResult(
            itemIngotTungsten, CoreLib.copyStack(itemNuggetTungsten, 9)
        );
        CraftLib.addAlloyResult(itemIngotCopper, itemFineCopper);
        CraftLib.addAlloyResult(new ItemStack(Items.iron_ingot, 1), itemFineIron);
        CraftLib.addAlloyResult(
            itemBouleSilicon,
            new ItemStack(Items.coal, 8, 0),
            new ItemStack(Blocks.sand, 8)
        );
        CraftLib.addAlloyResult(
            itemBouleSilicon,
            new ItemStack(Items.coal, 8, 1),
            new ItemStack(Blocks.sand, 8)
        );
        CraftLib.addAlloyResult(
            itemWaferBlue,
            CoreLib.copyStack(itemWaferSilicon, 1),
            new OreStack("dustNikolite", 4)
        );
        CraftLib.addAlloyResult(
            itemWaferRed,
            CoreLib.copyStack(itemWaferSilicon, 1),
            new ItemStack(Items.redstone, 4)
        );
    }

    public static void initMicroblocks() {
        blockMicro = new BlockMicro();
        blockMicro.setBlockName("rpwire");
        GameRegistry.registerBlock(blockMicro, ItemMicro.class, "microblock");
        blockMicro.addTileEntityMapping(0, TileCovered::new);
        CoverLib.blockCoverPlate = blockMicro;
    }

    public static void initCoverMaterials() {
        CoverLib.addMaterial(0, 1, Blocks.cobblestone, "cobble");
        CoverLib.addMaterial(1, 1, Blocks.stone, "stone");
        CoverLib.addMaterial(2, 0, Blocks.planks, "planks");
        CoverLib.addMaterial(3, 1, Blocks.sandstone, "sandstone");
        CoverLib.addMaterial(4, 1, Blocks.mossy_cobblestone, "moss");
        CoverLib.addMaterial(5, 1, Blocks.brick_block, "brick");
        CoverLib.addMaterial(6, 2, Blocks.obsidian, "obsidian");
        CoverLib.addMaterial(7, 1, true, Blocks.glass, "glass");
        CoverLib.addMaterial(8, 0, Blocks.dirt, "dirt");
        CoverLib.addMaterial(9, 0, Blocks.clay, "clay");
        CoverLib.addMaterial(10, 0, Blocks.bookshelf, "books");
        CoverLib.addMaterial(11, 0, Blocks.netherrack, "netherrack");
        CoverLib.addMaterial(12, 0, Blocks.log, 0, "wood");
        CoverLib.addMaterial(13, 0, Blocks.log, 1, "wood1");
        CoverLib.addMaterial(14, 0, Blocks.log, 2, "wood2");
        CoverLib.addMaterial(15, 0, Blocks.soul_sand, "soul");
        CoverLib.addMaterial(16, 1, Blocks.stone_slab, "slab");
        CoverLib.addMaterial(17, 1, Blocks.iron_block, "iron");
        CoverLib.addMaterial(18, 1, Blocks.gold_block, "gold");
        CoverLib.addMaterial(19, 2, Blocks.diamond_block, "diamond");
        CoverLib.addMaterial(20, 1, Blocks.lapis_block, "lapis");
        CoverLib.addMaterial(21, 0, Blocks.snow, "snow");
        CoverLib.addMaterial(22, 0, Blocks.pumpkin, "pumpkin");
        CoverLib.addMaterial(23, 1, Blocks.stonebrick, 0, "stonebrick");
        CoverLib.addMaterial(24, 1, Blocks.stonebrick, 1, "stonebrick1");
        CoverLib.addMaterial(25, 1, Blocks.stonebrick, 2, "stonebrick2");
        CoverLib.addMaterial(26, 1, Blocks.nether_brick, "netherbrick");
        CoverLib.addMaterial(27, 1, Blocks.stonebrick, 3, "stonebrick3");
        CoverLib.addMaterial(28, 0, Blocks.planks, 1, "planks1");
        CoverLib.addMaterial(29, 0, Blocks.planks, 2, "planks2");
        CoverLib.addMaterial(30, 0, Blocks.planks, 3, "planks3");
        CoverLib.addMaterial(31, 1, Blocks.sandstone, 1, "sandstone1");

        for (int color = 0; color < 16; ++color) {
            CoverLib.addMaterial(
                32 + color, 0, Blocks.wool, color, "wool." + CoreLib.rawColorNames[color]
            );
        }

        CoverLib.addMaterial(64, 1, Blocks.sandstone, 2, "sandstone2");
        CoverLib.addMaterial(65, 0, Blocks.log, 3, "wood3");
    }

    public static void initAchievements() {
        AchieveLib.registerAchievement(
            "rpMakeAlloy",
            0,
            0,
            new ItemStack(blockAppliance, 1, 0),
            AchievementList.buildFurnace
        );
        AchieveLib.registerAchievement(
            "rpMakeSaw", 4, 0, new ItemStack(itemHandsawDiamond), AchievementList.diamonds
        );
        AchieveLib.registerAchievement("rpIngotRed", 2, 2, itemIngotRed, "rpMakeAlloy");
        AchieveLib.registerAchievement("rpIngotBlue", 2, 4, itemIngotBlue, "rpMakeAlloy");
        AchieveLib.registerAchievement(
            "rpIngotBrass", 2, 6, itemIngotBrass, "rpMakeAlloy"
        );
        AchieveLib.registerAchievement(
            "rpAdvBench",
            -2,
            0,
            new ItemStack(blockAppliance, 1, 3),
            AchievementList.buildWorkBench
        );
        AchieveLib.addCraftingAchievement(
            new ItemStack(blockAppliance, 1, 0), "rpMakeAlloy"
        );
        AchieveLib.addCraftingAchievement(
            new ItemStack(blockAppliance, 1, 3), "rpAdvBench"
        );
        AchieveLib.addCraftingAchievement(new ItemStack(itemHandsawDiamond), "rpMakeSaw");
        AchieveLib.addAlloyAchievement(itemIngotRed, "rpIngotRed");
        AchieveLib.addAlloyAchievement(itemIngotBlue, "rpIngotBlue");
        AchieveLib.addAlloyAchievement(itemIngotBrass, "rpIngotBrass");
        AchievementPage.registerAchievementPage(AchieveLib.achievepage);
    }

    public static void initBlocks() {
        blockMultiblock = new BlockMultiblock();
        GameRegistry.registerBlock(blockMultiblock, "multiblock");
        GameRegistry.registerTileEntity(TileMultiblock.class, "RPMulti");
        blockAppliance = new BlockAppliance();
        GameRegistry.registerBlock(blockAppliance, ItemExtended.class, "appliance");
        GameRegistry.registerTileEntity(TileAlloyFurnace.class, "RPAFurnace");
        blockAppliance.addTileEntityMapping(0, TileAlloyFurnace::new);
        blockAppliance.setBlockName(0, "rpafurnace");
        GameRegistry.addRecipe(
            new ItemStack(blockAppliance, 1, 0),
            new Object[] { "BBB", "B B", "BBB", 'B', Blocks.brick_block }
        );
        GameRegistry.registerTileEntity(TileAdvBench.class, "RPAdvBench");
        blockAppliance.addTileEntityMapping(3, TileAdvBench::new);
        blockAppliance.setBlockName(3, "rpabench");
        CraftLib.addOreRecipe(
            new ItemStack(blockAppliance, 1, 3),
            "SSS",
            "WTW",
            "WCW",
            'S',
            Blocks.stone,
            'W',
            "plankWood",
            'T',
            Blocks.crafting_table,
            'C',
            Blocks.chest
        );
        itemHandsawIron = new ItemHandsaw(0);
        itemHandsawIron.setUnlocalizedName("handsawIron");
        itemHandsawIron.setTextureName("rpworld:handsawIron");
        itemHandsawIron.setMaxDamage(320);
        GameRegistry.registerItem(itemHandsawIron, "ironHandshaw");
        itemHandsawDiamond = new ItemHandsaw(2);
        itemHandsawDiamond.setUnlocalizedName("handsawDiamond");
        itemHandsawDiamond.setTextureName("rpworld:handsawDiamond");
        itemHandsawDiamond.setMaxDamage(1280);
        GameRegistry.registerItem(itemHandsawDiamond, "diamondHandshaw");
        GameRegistry.addRecipe(
            new ItemStack(itemHandsawIron, 1),
            new Object[] { "WWW", " II", " II", 'I', Items.iron_ingot, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemHandsawDiamond, 1),
            new Object[] { "WWW",
                           " II",
                           " DD",
                           'I',
                           Items.iron_ingot,
                           'D',
                           Items.diamond,
                           'W',
                           Items.stick }
        );
        GameRegistry.addShapelessRecipe(
            CoreLib.copyStack(itemWaferSilicon, 16),
            new Object[] { itemBouleSilicon, new ItemStack(itemHandsawDiamond, 1, 32767) }
        );
        itemScrewdriver = new ItemScrewdriver();
        GameRegistry.addRecipe(
            new ItemStack(itemScrewdriver, 1),
            new Object[] { "I ", " W", 'I', Items.iron_ingot, 'W', Items.stick }
        );
        GameRegistry.registerItem(itemScrewdriver, "screwdriver");
        GameRegistry.addRecipe(
            new ItemStack(itemDrawplateDiamond, 1),
            new Object[] { " I ",
                           "IDI",
                           " I ",
                           'I',
                           new ItemStack(blockMicro, 1, 5649),
                           'D',
                           new ItemStack(blockMicro, 1, 4115) }
        );
        GameRegistry.addShapelessRecipe(
            itemFineIron,
            new Object[] { Items.iron_ingot,
                           new ItemStack(itemDrawplateDiamond, 1, 32767) }
        );
        CraftLib.addShapelessOreRecipe(
            itemFineCopper, "ingotCopper", new ItemStack(itemDrawplateDiamond, 1, 32767)
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(itemNuggetIron, 9),
            new Object[] { "I", 'I', Items.iron_ingot }
        );
        CraftLib.addOreRecipe(
            CoreLib.copyStack(itemNuggetCopper, 9), "I", 'I', "ingotCopper"
        );
        CraftLib.addOreRecipe(CoreLib.copyStack(itemNuggetTin, 9), "I", 'I', "ingotTin");
        CraftLib.addOreRecipe(
            CoreLib.copyStack(itemNuggetSilver, 9), "I", 'I', "ingotSilver"
        );
        CraftLib.addOreRecipe(
            CoreLib.copyStack(itemNuggetTungsten, 9), "I", 'I', "ingotTungsten"
        );
        GameRegistry.addRecipe(
            new ItemStack(Items.iron_ingot, 1, 0),
            new Object[] { "III", "III", "III", 'I', itemNuggetIron }
        );
        GameRegistry.addRecipe(
            itemIngotSilver, new Object[] { "III", "III", "III", 'I', itemNuggetSilver }
        );
        GameRegistry.addRecipe(
            itemIngotTin, new Object[] { "III", "III", "III", 'I', itemNuggetTin }
        );
        GameRegistry.addRecipe(
            itemIngotCopper, new Object[] { "III", "III", "III", 'I', itemNuggetCopper }
        );
        GameRegistry.addRecipe(
            itemIngotTungsten,
            new Object[] { "III", "III", "III", 'I', itemNuggetTungsten }
        );
        GameRegistry.addRecipe(
            itemCanvas,
            new Object[] { "SSS", "SWS", "SSS", 'S', Items.string, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(Items.diamond, 2),
            new Object[] { "D", 'D', new ItemStack(blockMicro, 1, 4115) }
        );
        GameRegistry.addRecipe(
            new ItemStack(Items.diamond, 1),
            new Object[] { "D", 'D', new ItemStack(blockMicro, 1, 19) }
        );
        GameRegistry.addRecipe(
            new ItemStack(Items.iron_ingot, 2),
            new Object[] { "I", 'I', new ItemStack(blockMicro, 1, 4113) }
        );
        GameRegistry.addRecipe(
            new ItemStack(Items.iron_ingot, 1),
            new Object[] { "I", 'I', new ItemStack(blockMicro, 1, 17) }
        );
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        initBaseItems();
        initAlloys();
        initIndigo();
        initMicroblocks();
        initCoverMaterials();
        initBlocks();
        initAchievements();
        CraftingManager.getInstance().getRecipeList().add(new RecipeBag());
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.registerRenderers();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        RenderLib.setRenderer(blockAppliance, 0, RenderAlloyFurnace::new);
        RenderLib.setRenderer(blockAppliance, 3, RenderAdvBench::new);
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileAlloyFurnace.class, new RenderAlloyFurnace(blockAppliance)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileAdvBench.class, new RenderAdvBench(blockAppliance)
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(Pre evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            projectTableTop = map.registerIcon("rpbase:projectTableTop");
            projectTableBottom = map.registerIcon("rpbase:projectTableBottom");
            projectTableFront = map.registerIcon("rpbase:projectTableFront");
            projectTableSide = map.registerIcon("rpbase:projectTableSide");
            alloyFurnaceVert = map.registerIcon("rpbase:alloyFurnaceVert");
            alloyFurnaceSide = map.registerIcon("rpbase:alloyFurnaceSide");
            alloyFurnaceFront = map.registerIcon("rpbase:alloyFurnaceFront");
            alloyFurnaceFrontOn = map.registerIcon("rpbase:alloyFurnaceFrontOn");
        }
    }

    public Object
    getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 1:
                return new GuiAlloyFurnace(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, x, y, z, TileAlloyFurnace.class)
                );
            case 2:
                return new GuiAdvBench(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, x, y, z, TileAdvBench.class)
                );
            case 3:
                return new GuiBusId(
                    player.inventory,
                    new IRedbusConnectable.Dummy(),
                    CoreLib.getGuiTileEntity(world, x, y, z, TileEntity.class)
                );
            case 4:
                return new GuiBag(player.inventory, new InventoryBasic("", true, 27));
            default:
                return null;
        }
    }

    public Object
    getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 1:
                return new ContainerAlloyFurnace(
                    player.inventory,
                    CoreLib.getTileEntity(world, x, y, z, TileAlloyFurnace.class)
                );
            case 2:
                return new ContainerAdvBench(
                    player.inventory,
                    CoreLib.getTileEntity(world, x, y, z, TileAdvBench.class)
                );
            case 3:
                return new ContainerBusId(
                    player.inventory,
                    CoreLib.getTileEntity(world, x, y, z, IRedbusConnectable.class)
                );
            case 4:
                ItemStack heldItem = player.getHeldItem();
                return new ContainerBag(
                    player.inventory, ItemBag.getBagInventory(heldItem, player), heldItem
                );
            default:
                return null;
        }
    }
}
