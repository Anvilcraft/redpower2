package com.eloraam.redpower;

import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.ItemPartialCraft;
import com.eloraam.redpower.core.ItemTextured;
import com.eloraam.redpower.world.BlockCustomCrops;
import com.eloraam.redpower.world.BlockCustomFlower;
import com.eloraam.redpower.world.BlockCustomLeaves;
import com.eloraam.redpower.world.BlockCustomLog;
import com.eloraam.redpower.world.BlockCustomOre;
import com.eloraam.redpower.world.BlockCustomStone;
import com.eloraam.redpower.world.BlockStorage;
import com.eloraam.redpower.world.ContainerSeedBag;
import com.eloraam.redpower.world.EnchantmentDisjunction;
import com.eloraam.redpower.world.EnchantmentVorpal;
import com.eloraam.redpower.world.GuiSeedBag;
import com.eloraam.redpower.world.ItemAthame;
import com.eloraam.redpower.world.ItemCustomAxe;
import com.eloraam.redpower.world.ItemCustomFlower;
import com.eloraam.redpower.world.ItemCustomHoe;
import com.eloraam.redpower.world.ItemCustomOre;
import com.eloraam.redpower.world.ItemCustomPickaxe;
import com.eloraam.redpower.world.ItemCustomSeeds;
import com.eloraam.redpower.world.ItemCustomShovel;
import com.eloraam.redpower.world.ItemCustomStone;
import com.eloraam.redpower.world.ItemCustomSword;
import com.eloraam.redpower.world.ItemPaintBrush;
import com.eloraam.redpower.world.ItemPaintCan;
import com.eloraam.redpower.world.ItemSeedBag;
import com.eloraam.redpower.world.ItemSickle;
import com.eloraam.redpower.world.ItemStorage;
import com.eloraam.redpower.world.ItemWoolCard;
import com.eloraam.redpower.world.WorldEvents;
import com.eloraam.redpower.world.WorldGenHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

@Mod(
    modid = "RedPowerWorld",
    name = "RedPower World",
    version = RedPowerBase.VERSION,
    dependencies = "required-after:RedPowerBase"
)
public class RedPowerWorld implements IGuiHandler {
    @Instance("RedPowerWorld")
    public static RedPowerWorld instance;
    public static BlockCustomFlower blockPlants;
    public static BlockCustomOre blockOres;
    public static BlockCustomLeaves blockLeaves;
    public static BlockCustomLog blockLogs;
    public static BlockCustomStone blockStone;
    public static BlockCustomCrops blockCrops;
    public static BlockStorage blockStorage;
    public static ItemStack itemOreRuby;
    public static ItemStack itemOreGreenSapphire;
    public static ItemStack itemOreSapphire;
    public static ItemStack itemMarble;
    public static ItemStack itemBasalt;
    public static ItemStack itemBasaltCobble;
    public static ToolMaterial toolMaterialRuby;
    public static ToolMaterial toolMaterialGreenSapphire;
    public static ToolMaterial toolMaterialSapphire;
    public static ItemSickle itemSickleWood;
    public static ItemSickle itemSickleStone;
    public static ItemSickle itemSickleIron;
    public static ItemSickle itemSickleDiamond;
    public static ItemSickle itemSickleGold;
    public static ItemSickle itemSickleRuby;
    public static ItemSickle itemSickleGreenSapphire;
    public static ItemSickle itemSickleSapphire;
    public static ItemCustomPickaxe itemPickaxeRuby;
    public static ItemCustomPickaxe itemPickaxeGreenSapphire;
    public static ItemCustomPickaxe itemPickaxeSapphire;
    public static ItemCustomShovel itemShovelRuby;
    public static ItemCustomShovel setUnlocalizedName;
    public static ItemCustomShovel itemShovelSapphire;
    public static ItemCustomShovel itemShovelGreenSapphire;
    public static ItemCustomAxe itemAxeRuby;
    public static ItemCustomAxe itemAxeGreenSapphire;
    public static ItemCustomAxe itemAxeSapphire;
    public static ItemCustomSword itemSwordRuby;
    public static ItemCustomSword itemSwordGreenSapphire;
    public static ItemCustomSword itemSwordSapphire;
    public static ItemAthame itemAthame;
    public static ItemCustomHoe itemHoeRuby;
    public static ItemCustomHoe itemHoeGreenSapphire;
    public static ItemCustomHoe itemHoeSapphire;
    public static ItemCustomSeeds itemSeeds;
    public static Item itemHandsawRuby;
    public static Item itemHandsawGreenSapphire;
    public static Item itemHandsawSapphire;
    public static Item itemBrushDry;
    public static Item itemPaintCanEmpty;
    public static Item[] itemBrushPaint = new Item[16];
    public static ItemPartialCraft[] itemPaintCanPaint = new ItemPartialCraft[16];
    public static Item itemWoolCard;
    public static Item itemSeedBag;
    public static Enchantment enchantDisjunction;
    public static Enchantment enchantVorpal;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new WorldEvents());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WorldGenHandler(), 1);
        this.setupOres();
        this.setupPlants();
        this.setupTools();
        this.setupMisc();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    public void setupPlants() {
        blockPlants
            = new BlockCustomFlower("rpworld:indigoFlower", "rpworld:rubberSapling");
        blockPlants.setBlockName("plant");
        GameRegistry.registerBlock(blockPlants, ItemCustomFlower.class, "plants");
        GameRegistry.addShapelessRecipe(
            new ItemStack(RedPowerBase.itemDyeIndigo, 2, 0), new Object[] { blockPlants }
        );
        itemSeeds = new ItemCustomSeeds();
        MinecraftForge.addGrassSeed(new ItemStack(itemSeeds, 1, 0), 5);
        blockCrops = new BlockCustomCrops();
        GameRegistry.registerBlock(blockCrops, "flax");
        GameRegistry.registerItem(itemSeeds, "flaxseeds");
        blockLeaves = new BlockCustomLeaves(
            "rpworld:rubberLeaves_opaque", "rpworld:rubberLeaves_transparent"
        );
        blockLeaves.setBlockName("rpleaves");
        GameRegistry.registerBlock(blockLeaves, "leaves");
        blockLogs = new BlockCustomLog("rpworld:rubberLogSide", "rpworld:rubberLogTop");
        blockLogs.setBlockName("rplog");
        GameRegistry.registerBlock(blockLogs, "logs");
        blockLogs.setHarvestLevel("axe", 0, 0);
        OreDictionary.registerOre("woodRubber", new ItemStack(blockLogs));
        GameRegistry.addRecipe(
            new ItemStack(Items.stick, 8), new Object[] { "W", 'W', blockLogs }
        );
        GameRegistry.addSmelting(
            new ItemStack(blockLogs, 1, 0), new ItemStack(Items.coal, 1, 1), 0.15F
        );
        CoverLib.addMaterial(53, 0, blockLogs, 0, "rplog");
    }

    public void setupOres() {
        blockStone = new BlockCustomStone();
        blockStone.setBlockName("rpstone");
        GameRegistry.registerBlock(blockStone, ItemCustomStone.class, "stone");
        itemMarble = new ItemStack(blockStone, 0);
        itemBasalt = new ItemStack(blockStone, 1);
        itemBasaltCobble = new ItemStack(blockStone, 3);
        blockStone.setHarvestLevel("pickaxe", 0);
        blockStone.setBlockTexture(0, "rpworld:marble");
        blockStone.setBlockTexture(1, "rpworld:basalt");
        blockStone.setBlockTexture(2, "rpworld:marbleBrick");
        blockStone.setBlockTexture(3, "rpworld:basaltCobble");
        blockStone.setBlockTexture(4, "rpworld:basaltBrick");
        blockStone.setBlockTexture(5, "rpworld:chiseledBasaltBrick");
        blockStone.setBlockTexture(6, "rpworld:basaltPaver");
        CoverLib.addMaterial(48, 1, blockStone, 0, "marble");
        CoverLib.addMaterial(49, 1, blockStone, 1, "basalt");
        CoverLib.addMaterial(50, 1, blockStone, 2, "marbleBrick");
        CoverLib.addMaterial(51, 1, blockStone, 3, "basaltCobble");
        CoverLib.addMaterial(52, 1, blockStone, 4, "basaltBrick");
        CoverLib.addMaterial(57, 1, blockStone, 5, "basaltCircle");
        CoverLib.addMaterial(58, 1, blockStone, 6, "basaltPaver");
        blockOres = new BlockCustomOre();
        GameRegistry.registerBlock(blockOres, ItemCustomOre.class, "ores");
        itemOreRuby = new ItemStack(blockOres, 1, 0);
        itemOreGreenSapphire = new ItemStack(blockOres, 1, 1);
        itemOreSapphire = new ItemStack(blockOres, 1, 2);
        blockOres.setHarvestLevel("pickaxe", 2, 0);
        blockOres.setHarvestLevel("pickaxe", 2, 1);
        blockOres.setHarvestLevel("pickaxe", 2, 2);
        blockOres.setHarvestLevel("pickaxe", 1, 3);
        blockOres.setHarvestLevel("pickaxe", 0, 4);
        blockOres.setHarvestLevel("pickaxe", 0, 5);
        blockOres.setHarvestLevel("pickaxe", 2, 6);
        blockOres.setHarvestLevel("pickaxe", 2, 7);
        GameRegistry.addSmelting(
            new ItemStack(blockOres, 1, 3), RedPowerBase.itemIngotSilver, 1.0F
        );
        GameRegistry.addSmelting(
            new ItemStack(blockOres, 1, 4), RedPowerBase.itemIngotTin, 0.7F
        );
        GameRegistry.addSmelting(
            new ItemStack(blockOres, 1, 5), RedPowerBase.itemIngotCopper, 0.7F
        );
        GameRegistry.addSmelting(
            new ItemStack(blockOres, 1, 6), RedPowerBase.itemIngotTungsten, 1.2F
        );
        GameRegistry.addSmelting(
            new ItemStack(RedPowerBase.itemResource, 2, 9),
            RedPowerBase.itemIngotSilver,
            1.0F
        );
        GameRegistry.addSmelting(
            new ItemStack(RedPowerBase.itemResource, 2, 8),
            RedPowerBase.itemIngotTungsten,
            1.2F
        );
        OreDictionary.registerOre("oreRuby", new ItemStack(blockOres, 1, 0));
        OreDictionary.registerOre("oreGreenSapphire", new ItemStack(blockOres, 1, 1));
        OreDictionary.registerOre("oreSapphire", new ItemStack(blockOres, 1, 2));
        OreDictionary.registerOre("oreSilver", new ItemStack(blockOres, 1, 3));
        OreDictionary.registerOre("oreTin", new ItemStack(blockOres, 1, 4));
        OreDictionary.registerOre("oreCopper", new ItemStack(blockOres, 1, 5));
        OreDictionary.registerOre("oreTungsten", new ItemStack(blockOres, 1, 6));
        OreDictionary.registerOre("oreNikolite", new ItemStack(blockOres, 1, 7));
        GameRegistry.addRecipe(
            new ItemStack(blockStone, 4, 2),
            new Object[] { "SS", "SS", 'S', new ItemStack(blockStone, 1, 0) }
        );
        GameRegistry.addSmelting(
            new ItemStack(blockStone, 1, 3), new ItemStack(blockStone, 1, 1), 0.2F
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStone, 4, 4),
            new Object[] { "SS", "SS", 'S', new ItemStack(blockStone, 1, 1) }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStone, 4, 5),
            new Object[] { "SS", "SS", 'S', new ItemStack(blockStone, 1, 4) }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStone, 1, 6),
            new Object[] { "S", 'S', new ItemStack(blockStone, 1, 1) }
        );
        blockStorage = new BlockStorage();
        GameRegistry.registerBlock(blockStorage, ItemStorage.class, "orestorage");
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 0),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemRuby }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 1),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemGreenSapphire }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 2),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemSapphire }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 3),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemIngotSilver }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 4),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemIngotTin }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 5),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemIngotCopper }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 6),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemIngotTungsten }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockStorage, 1, 7),
            new Object[] { "GGG", "GGG", "GGG", 'G', RedPowerBase.itemNikolite }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemRuby, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 0) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemGreenSapphire, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 1) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemSapphire, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 2) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemIngotSilver, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 3) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemIngotTin, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 4) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemIngotCopper, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 5) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemIngotTungsten, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 6) }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(RedPowerBase.itemNikolite, 9),
            new Object[] { "G", 'G', new ItemStack(blockStorage, 1, 7) }
        );
        blockStorage.setHarvestLevel("pickaxe", 2, 0);
        blockStorage.setHarvestLevel("pickaxe", 2, 1);
        blockStorage.setHarvestLevel("pickaxe", 2, 2);
        blockStorage.setHarvestLevel("pickaxe", 2, 3);
        blockStorage.setHarvestLevel("pickaxe", 2, 4);
        blockStorage.setHarvestLevel("pickaxe", 2, 5);
        blockStorage.setHarvestLevel("pickaxe", 3, 6);
        blockStorage.setHarvestLevel("pickaxe", 2, 7);
        CoverLib.addMaterial(54, 2, blockStorage, 0, "rubyBlock");
        CoverLib.addMaterial(55, 2, blockStorage, 1, "greenSapphireBlock");
        CoverLib.addMaterial(56, 2, blockStorage, 2, "sapphireBlock");
        CoverLib.addMaterial(66, 2, blockStorage, 3, "silverBlock");
        CoverLib.addMaterial(67, 2, blockStorage, 4, "tinBlock");
        CoverLib.addMaterial(68, 2, blockStorage, 5, "copperBlock");
        CoverLib.addMaterial(69, 2, blockStorage, 6, "tungstenBlock");
    }

    public void setupTools() {
        toolMaterialRuby = EnumHelper.addToolMaterial("RUBY", 2, 500, 8.0F, 3.0F, 12);
        toolMaterialGreenSapphire
            = EnumHelper.addToolMaterial("GREENSAPPHIRE", 2, 500, 8.0F, 3.0F, 12);
        toolMaterialSapphire
            = EnumHelper.addToolMaterial("SAPPHIRE", 2, 500, 8.0F, 3.0F, 12);
        itemPickaxeRuby = new ItemCustomPickaxe(toolMaterialRuby);
        itemPickaxeRuby.setUnlocalizedName("pickaxeRuby");
        itemPickaxeRuby.setTextureName("rpworld:pickaxeRuby");
        GameRegistry.registerItem(itemPickaxeRuby, "rubyPickaxe");
        itemPickaxeGreenSapphire = new ItemCustomPickaxe(toolMaterialGreenSapphire);
        itemPickaxeGreenSapphire.setUnlocalizedName("pickaxeGreenSapphire");
        itemPickaxeGreenSapphire.setTextureName("rpworld:pickaxeGreenSapphire");
        GameRegistry.registerItem(itemPickaxeGreenSapphire, "greenSapphirePickaxe");
        itemPickaxeSapphire = new ItemCustomPickaxe(toolMaterialSapphire);
        itemPickaxeSapphire.setUnlocalizedName("pickaxeSapphire");
        itemPickaxeSapphire.setTextureName("rpworld:pickaxeSapphire");
        GameRegistry.registerItem(itemPickaxeSapphire, "sapphirePickaxe");
        itemPickaxeRuby.setHarvestLevel("pickaxe", 2);
        itemPickaxeGreenSapphire.setHarvestLevel("pickaxe", 2);
        itemPickaxeSapphire.setHarvestLevel("pickaxe", 2);
        GameRegistry.addRecipe(
            new ItemStack(itemPickaxeRuby, 1),
            new Object[] {
                "GGG", " W ", " W ", 'G', RedPowerBase.itemRuby, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemPickaxeGreenSapphire, 1),
            new Object[] { "GGG",
                           " W ",
                           " W ",
                           'G',
                           RedPowerBase.itemGreenSapphire,
                           'W',
                           Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemPickaxeSapphire, 1),
            new Object[] {
                "GGG", " W ", " W ", 'G', RedPowerBase.itemSapphire, 'W', Items.stick }
        );
        itemShovelRuby = new ItemCustomShovel(toolMaterialRuby);
        itemShovelRuby.setUnlocalizedName("shovelRuby");
        itemShovelRuby.setTextureName("rpworld:shovelRuby");
        GameRegistry.registerItem(itemShovelRuby, "rubyShovel");
        itemShovelGreenSapphire = new ItemCustomShovel(toolMaterialGreenSapphire);
        itemShovelGreenSapphire.setUnlocalizedName("shovelGreenSapphire");
        itemShovelGreenSapphire.setTextureName("rpworld:shovelGreenSapphire");
        GameRegistry.registerItem(itemShovelGreenSapphire, "greenSapphireShovel");
        itemShovelSapphire = new ItemCustomShovel(toolMaterialSapphire);
        itemShovelSapphire.setUnlocalizedName("shovelSapphire");
        itemShovelSapphire.setTextureName("rpworld:shovelSapphire");
        GameRegistry.registerItem(itemShovelSapphire, "sapphireShovel");
        itemShovelRuby.setHarvestLevel("shovel", 2);
        itemShovelGreenSapphire.setHarvestLevel("shovel", 2);
        itemShovelSapphire.setHarvestLevel("shovel", 2);
        GameRegistry.addRecipe(
            new ItemStack(itemShovelRuby, 1),
            new Object[] { "G", "W", "W", 'G', RedPowerBase.itemRuby, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemShovelGreenSapphire, 1),
            new Object[] {
                "G", "W", "W", 'G', RedPowerBase.itemGreenSapphire, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemShovelSapphire, 1),
            new Object[] {
                "G", "W", "W", 'G', RedPowerBase.itemSapphire, 'W', Items.stick }
        );
        itemAxeRuby = new ItemCustomAxe(toolMaterialRuby);
        itemAxeRuby.setUnlocalizedName("axeRuby");
        itemAxeRuby.setTextureName("rpworld:axeRuby");
        GameRegistry.registerItem(itemAxeRuby, "rubyAxe");
        itemAxeGreenSapphire = new ItemCustomAxe(toolMaterialGreenSapphire);
        itemAxeGreenSapphire.setUnlocalizedName("axeGreenSapphire");
        itemAxeGreenSapphire.setTextureName("rpworld:axeGreenSapphire");
        GameRegistry.registerItem(itemAxeGreenSapphire, "greenSapphireAxe");
        itemAxeSapphire = new ItemCustomAxe(toolMaterialSapphire);
        itemAxeSapphire.setUnlocalizedName("axeSapphire");
        itemAxeSapphire.setTextureName("rpworld:axeSapphire");
        GameRegistry.registerItem(itemAxeSapphire, "sapphireAxe");
        itemAxeRuby.setHarvestLevel("axe", 2);
        itemAxeGreenSapphire.setHarvestLevel("axe", 2);
        itemAxeSapphire.setHarvestLevel("axe", 2);
        GameRegistry.addRecipe(
            new ItemStack(itemAxeRuby, 1),
            new Object[] {
                "GG", "GW", " W", 'G', RedPowerBase.itemRuby, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemAxeGreenSapphire, 1),
            new Object[] {
                "GG", "GW", " W", 'G', RedPowerBase.itemGreenSapphire, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemAxeSapphire, 1),
            new Object[] {
                "GG", "GW", " W", 'G', RedPowerBase.itemSapphire, 'W', Items.stick }
        );
        itemSwordRuby = new ItemCustomSword(toolMaterialRuby);
        itemSwordRuby.setUnlocalizedName("swordRuby");
        itemSwordRuby.setTextureName("rpworld:swordRuby");
        GameRegistry.registerItem(itemSwordRuby, "rubySword");
        itemSwordGreenSapphire = new ItemCustomSword(toolMaterialGreenSapphire);
        itemSwordGreenSapphire.setUnlocalizedName("swordGreenSapphire");
        itemSwordGreenSapphire.setTextureName("rpworld:swordGreenSapphire");
        GameRegistry.registerItem(itemSwordGreenSapphire, "greenSapphireSword");
        itemSwordSapphire = new ItemCustomSword(toolMaterialSapphire);
        itemSwordSapphire.setUnlocalizedName("swordSapphire");
        itemSwordSapphire.setTextureName("rpworld:swordSapphire");
        GameRegistry.registerItem(itemSwordSapphire, "sapphireSword");
        itemAthame = new ItemAthame();
        itemAthame.setUnlocalizedName("athame");
        GameRegistry.registerItem(itemAthame, "athame");
        CraftLib.addOreRecipe(
            new ItemStack(itemAthame, 1), "S", "W", 'S', "ingotSilver", 'W', Items.stick
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSwordRuby, 1),
            new Object[] { "G", "G", "W", 'G', RedPowerBase.itemRuby, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSwordGreenSapphire, 1),
            new Object[] {
                "G", "G", "W", 'G', RedPowerBase.itemGreenSapphire, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSwordSapphire, 1),
            new Object[] {
                "G", "G", "W", 'G', RedPowerBase.itemSapphire, 'W', Items.stick }
        );
        itemHoeRuby = new ItemCustomHoe(toolMaterialRuby);
        itemHoeRuby.setUnlocalizedName("hoeRuby");
        itemHoeRuby.setTextureName("rpworld:hoeRuby");
        itemHoeRuby.setMaxDamage(500);
        GameRegistry.registerItem(itemHoeRuby, "rubyHoe");
        itemHoeGreenSapphire = new ItemCustomHoe(toolMaterialGreenSapphire);
        itemHoeGreenSapphire.setUnlocalizedName("hoeGreenSapphire");
        itemHoeGreenSapphire.setTextureName("rpworld:hoeGreenSapphire");
        itemHoeGreenSapphire.setMaxDamage(500);
        GameRegistry.registerItem(itemHoeGreenSapphire, "greenSapphireHoe");
        itemHoeSapphire = new ItemCustomHoe(toolMaterialSapphire);
        itemHoeSapphire.setUnlocalizedName("hoeSapphire");
        itemHoeSapphire.setTextureName("rpworld:hoeSapphire");
        itemHoeSapphire.setMaxDamage(500);
        GameRegistry.registerItem(itemHoeSapphire, "sapphireHoe");
        itemHoeRuby.setHarvestLevel("hoe", 2);
        itemHoeGreenSapphire.setHarvestLevel("hoe", 2);
        itemHoeSapphire.setHarvestLevel("hoe", 2);
        GameRegistry.addRecipe(
            new ItemStack(itemHoeRuby, 1),
            new Object[] {
                "GG", " W", " W", 'G', RedPowerBase.itemRuby, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemHoeGreenSapphire, 1),
            new Object[] {
                "GG", " W", " W", 'G', RedPowerBase.itemGreenSapphire, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemHoeSapphire, 1),
            new Object[] {
                "GG", " W", " W", 'G', RedPowerBase.itemSapphire, 'W', Items.stick }
        );
        itemSickleWood = new ItemSickle(ToolMaterial.WOOD);
        itemSickleWood.setUnlocalizedName("sickleWood");
        itemSickleWood.setTextureName("rpworld:sickleWood");
        GameRegistry.registerItem(itemSickleWood, "woodenSickle");
        itemSickleStone = new ItemSickle(ToolMaterial.STONE);
        itemSickleStone.setUnlocalizedName("sickleStone");
        itemSickleStone.setTextureName("rpworld:sickleStone");
        GameRegistry.registerItem(itemSickleStone, "stoneSickle");
        itemSickleIron = new ItemSickle(ToolMaterial.IRON);
        itemSickleIron.setUnlocalizedName("sickleIron");
        itemSickleIron.setTextureName("rpworld:sickleIron");
        GameRegistry.registerItem(itemSickleIron, "ironSickle");
        itemSickleDiamond = new ItemSickle(ToolMaterial.EMERALD);
        itemSickleDiamond.setUnlocalizedName("sickleDiamond");
        itemSickleDiamond.setTextureName("rpworld:sickleDiamond");
        GameRegistry.registerItem(itemSickleDiamond, "diamondSickle");
        itemSickleGold = new ItemSickle(ToolMaterial.GOLD);
        itemSickleGold.setUnlocalizedName("sickleGold");
        itemSickleGold.setTextureName("rpworld:sickleGold");
        GameRegistry.registerItem(itemSickleGold, "goldSickle");
        itemSickleRuby = new ItemSickle(toolMaterialRuby);
        itemSickleRuby.setUnlocalizedName("sickleRuby");
        itemSickleRuby.setTextureName("rpworld:sickleRuby");
        GameRegistry.registerItem(itemSickleRuby, "rubySickle");
        itemSickleGreenSapphire = new ItemSickle(toolMaterialGreenSapphire);
        itemSickleGreenSapphire.setUnlocalizedName("sickleGreenSapphire");
        itemSickleGreenSapphire.setTextureName("rpworld:sickleGreenSapphire");
        GameRegistry.registerItem(itemSickleGreenSapphire, "greenSapphireSickle");
        itemSickleSapphire = new ItemSickle(toolMaterialSapphire);
        itemSickleSapphire.setUnlocalizedName("sickleSapphire");
        itemSickleSapphire.setTextureName("rpworld:sickleSapphire");
        GameRegistry.registerItem(itemSickleSapphire, "sapphireSickle");
        CraftLib.addOreRecipe(
            new ItemStack(itemSickleWood, 1),
            " I ",
            "  I",
            "WI ",
            'I',
            "plankWood",
            'W',
            Items.stick
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleStone, 1),
            new Object[] {
                " I ", "  I", "WI ", 'I', Blocks.cobblestone, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleIron, 1),
            new Object[] { " I ", "  I", "WI ", 'I', Items.iron_ingot, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleDiamond, 1),
            new Object[] { " I ", "  I", "WI ", 'I', Items.diamond, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleGold, 1),
            new Object[] { " I ", "  I", "WI ", 'I', Items.gold_ingot, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleRuby, 1),
            new Object[] {
                " I ", "  I", "WI ", 'I', RedPowerBase.itemRuby, 'W', Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleGreenSapphire, 1),
            new Object[] { " I ",
                           "  I",
                           "WI ",
                           'I',
                           RedPowerBase.itemGreenSapphire,
                           'W',
                           Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemSickleSapphire, 1),
            new Object[] {
                " I ", "  I", "WI ", 'I', RedPowerBase.itemSapphire, 'W', Items.stick }
        );
        itemHandsawRuby = new ItemHandsaw(1);
        itemHandsawGreenSapphire = new ItemHandsaw(1);
        itemHandsawSapphire = new ItemHandsaw(1);
        itemHandsawRuby.setUnlocalizedName("handsawRuby")
            .setTextureName("rpworld:handsawRuby");
        itemHandsawGreenSapphire.setUnlocalizedName("handsawGreenSapphire")
            .setTextureName("rpworld:handsawGreenSapphire");
        itemHandsawSapphire.setUnlocalizedName("handsawSapphire")
            .setTextureName("rpworld:handsawSapphire");
        itemHandsawRuby.setMaxDamage(640);
        itemHandsawGreenSapphire.setMaxDamage(640);
        itemHandsawSapphire.setMaxDamage(640);
        GameRegistry.registerItem(itemHandsawRuby, "rubyHandshaw");
        GameRegistry.registerItem(itemHandsawGreenSapphire, "greenSapphireHandshaw");
        GameRegistry.registerItem(itemHandsawSapphire, "sapphireHandshaw");
        GameRegistry.addRecipe(
            new ItemStack(itemHandsawRuby, 1),
            new Object[] { "WWW",
                           " II",
                           " GG",
                           'I',
                           Items.iron_ingot,
                           'G',
                           RedPowerBase.itemRuby,
                           'W',
                           Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemHandsawGreenSapphire, 1),
            new Object[] { "WWW",
                           " II",
                           " GG",
                           'I',
                           Items.iron_ingot,
                           'G',
                           RedPowerBase.itemGreenSapphire,
                           'W',
                           Items.stick }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemHandsawSapphire, 1),
            new Object[] { "WWW",
                           " II",
                           " GG",
                           'I',
                           Items.iron_ingot,
                           'G',
                           RedPowerBase.itemSapphire,
                           'W',
                           Items.stick }
        );
        itemWoolCard = new ItemWoolCard();
        GameRegistry.registerItem(itemWoolCard, "woolCard");
        CraftLib.addOreRecipe(
            new ItemStack(itemWoolCard, 1),
            "W",
            "P",
            "S",
            'W',
            RedPowerBase.itemFineIron,
            'P',
            "plankWood",
            'S',
            Items.stick
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(Items.string, 4),
            new Object[] { new ItemStack(itemWoolCard, 1, 32767),
                           new ItemStack(Blocks.wool, 1, 32767) }
        );
        itemBrushDry = new ItemTextured("rpworld:brushDry");
        itemBrushDry.setCreativeTab(CreativeTabs.tabTools);
        itemBrushDry.setUnlocalizedName("paintbrush.dry");
        GameRegistry.registerItem(itemBrushDry, "dryBush");
        GameRegistry.addRecipe(
            new ItemStack(itemBrushDry),
            new Object[] { "W ", " S", 'S', Items.stick, 'W', Blocks.wool }
        );
        itemPaintCanEmpty = new ItemTextured("rpworld:paintCanEmpty");
        itemPaintCanEmpty.setCreativeTab(CreativeTabs.tabTools);
        itemPaintCanEmpty.setUnlocalizedName("paintcan.empty");
        GameRegistry.registerItem(itemPaintCanEmpty, "emptyPainCan");
        GameRegistry.addRecipe(
            new ItemStack(itemPaintCanEmpty, 3),
            new Object[] { "T T", "T T", "TTT", 'T', RedPowerBase.itemTinplate }
        );

        for (int color = 0; color < 16; ++color) {
            itemPaintCanPaint[color] = new ItemPaintCan(color);
            itemPaintCanPaint[color].setUnlocalizedName(
                "paintcan." + CoreLib.rawColorNames[color]
            );
            itemPaintCanPaint[color].setEmptyItem(new ItemStack(itemPaintCanEmpty));
            GameRegistry.registerItem(
                itemPaintCanPaint[color], CoreLib.rawColorNames[color] + "PainCan"
            );
            GameRegistry.addShapelessRecipe(
                new ItemStack(itemPaintCanPaint[color]),
                new Object[] { itemPaintCanEmpty,
                               new ItemStack(Items.dye, 1, 15 - color),
                               new ItemStack(itemSeeds, 1, 0),
                               new ItemStack(itemSeeds, 1, 0) }
            );
        }

        for (int color = 0; color < 16; ++color) {
            itemBrushPaint[color] = new ItemPaintBrush(color);
            itemBrushPaint[color].setUnlocalizedName(
                "paintbrush." + CoreLib.rawColorNames[color]
            );
            GameRegistry.registerItem(
                itemBrushPaint[color], CoreLib.rawColorNames[color] + "PainBrush"
            );
            GameRegistry.addShapelessRecipe(
                new ItemStack(itemBrushPaint[color]),
                new Object[] { new ItemStack(itemPaintCanPaint[color], 1, 32767),
                               itemBrushDry }
            );
        }

        CraftLib.addShapelessOreRecipe(
            new ItemStack(itemPaintCanPaint[11]),
            itemPaintCanEmpty,
            "dyeBlue",
            new ItemStack(itemSeeds, 1, 0),
            new ItemStack(itemSeeds, 1, 0)
        );
        itemSeedBag = new ItemSeedBag();
        GameRegistry.registerItem(itemSeedBag, "seedBag");
        GameRegistry.addRecipe(
            new ItemStack(itemSeedBag, 1, 0),
            new Object[] {
                " S ", "C C", "CCC", 'S', Items.string, 'C', RedPowerBase.itemCanvas }
        );
    }

    public void setupMisc() {
        if (Config.getInt("settings.world.tweaks.spreadmoss", 1) > 0) {}

        if (Config.getInt("settings.world.tweaks.craftcircle", 1) > 0) {
            GameRegistry.addRecipe(
                new ItemStack(Blocks.stonebrick, 4, 3),
                new Object[] { "BB", "BB", 'B', new ItemStack(Blocks.stonebrick, 1, 0) }
            );
        }

        if (Config.getInt("settings.world.tweaks.unbricks", 1) > 0) {
            GameRegistry.addShapelessRecipe(
                new ItemStack(Items.brick, 4, 0),
                new Object[] { new ItemStack(Blocks.brick_block, 1, 0) }
            );
        }

        enchantDisjunction
            = new EnchantmentDisjunction(Config.getInt("enchant.disjunction.id", 79), 10);
        enchantVorpal = new EnchantmentVorpal(Config.getInt("enchant.vorpal.id", 80), 10);
    }

    public Object
    getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 1:
                return new GuiSeedBag(player.inventory, new InventoryBasic("", true, 9));
            default:
                return null;
        }
    }

    public Object
    getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 1:
                ItemStack heldItem = player.getHeldItem();
                return new ContainerSeedBag(
                    player.inventory,
                    ItemSeedBag.getBagInventory(heldItem, player),
                    heldItem
                );
            default:
                return null;
        }
    }
}
