package com.eloraam.redpower;

import com.eloraam.redpower.core.AchieveLib;
import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.core.ItemTextured;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.machine.BlockFrame;
import com.eloraam.redpower.machine.BlockMachine;
import com.eloraam.redpower.machine.BlockMachinePanel;
import com.eloraam.redpower.machine.ContainerAssemble;
import com.eloraam.redpower.machine.ContainerBatteryBox;
import com.eloraam.redpower.machine.ContainerBlueAlloyFurnace;
import com.eloraam.redpower.machine.ContainerBlueFurnace;
import com.eloraam.redpower.machine.ContainerBufferChest;
import com.eloraam.redpower.machine.ContainerChargingBench;
import com.eloraam.redpower.machine.ContainerDeploy;
import com.eloraam.redpower.machine.ContainerEject;
import com.eloraam.redpower.machine.ContainerFilter;
import com.eloraam.redpower.machine.ContainerItemDetect;
import com.eloraam.redpower.machine.ContainerManager;
import com.eloraam.redpower.machine.ContainerRegulator;
import com.eloraam.redpower.machine.ContainerRetriever;
import com.eloraam.redpower.machine.ContainerSorter;
import com.eloraam.redpower.machine.ContainerWindTurbine;
import com.eloraam.redpower.machine.GuiAssemble;
import com.eloraam.redpower.machine.GuiBatteryBox;
import com.eloraam.redpower.machine.GuiBlueAlloyFurnace;
import com.eloraam.redpower.machine.GuiBlueFurnace;
import com.eloraam.redpower.machine.GuiBufferChest;
import com.eloraam.redpower.machine.GuiChargingBench;
import com.eloraam.redpower.machine.GuiDeploy;
import com.eloraam.redpower.machine.GuiEject;
import com.eloraam.redpower.machine.GuiFilter;
import com.eloraam.redpower.machine.GuiItemDetect;
import com.eloraam.redpower.machine.GuiManager;
import com.eloraam.redpower.machine.GuiRegulator;
import com.eloraam.redpower.machine.GuiRetriever;
import com.eloraam.redpower.machine.GuiSorter;
import com.eloraam.redpower.machine.GuiWindTurbine;
import com.eloraam.redpower.machine.ItemBattery;
import com.eloraam.redpower.machine.ItemMachinePanel;
import com.eloraam.redpower.machine.ItemSonicDriver;
import com.eloraam.redpower.machine.ItemVoltmeter;
import com.eloraam.redpower.machine.ItemWindmill;
import com.eloraam.redpower.machine.MicroPlacementTube;
import com.eloraam.redpower.machine.RenderAccel;
import com.eloraam.redpower.machine.RenderBatteryBox;
import com.eloraam.redpower.machine.RenderBlueAlloyFurnace;
import com.eloraam.redpower.machine.RenderBlueFurnace;
import com.eloraam.redpower.machine.RenderBreaker;
import com.eloraam.redpower.machine.RenderBufferChest;
import com.eloraam.redpower.machine.RenderChargingBench;
import com.eloraam.redpower.machine.RenderFrame;
import com.eloraam.redpower.machine.RenderFrameMoving;
import com.eloraam.redpower.machine.RenderFrameRedstoneTube;
import com.eloraam.redpower.machine.RenderFrameTube;
import com.eloraam.redpower.machine.RenderGrate;
import com.eloraam.redpower.machine.RenderMachine;
import com.eloraam.redpower.machine.RenderMotor;
import com.eloraam.redpower.machine.RenderPipe;
import com.eloraam.redpower.machine.RenderPump;
import com.eloraam.redpower.machine.RenderRedstoneTube;
import com.eloraam.redpower.machine.RenderSolarPanel;
import com.eloraam.redpower.machine.RenderThermopile;
import com.eloraam.redpower.machine.RenderTransformer;
import com.eloraam.redpower.machine.RenderTube;
import com.eloraam.redpower.machine.RenderWindTurbine;
import com.eloraam.redpower.machine.TileAccel;
import com.eloraam.redpower.machine.TileAssemble;
import com.eloraam.redpower.machine.TileBatteryBox;
import com.eloraam.redpower.machine.TileBlueAlloyFurnace;
import com.eloraam.redpower.machine.TileBlueFurnace;
import com.eloraam.redpower.machine.TileBreaker;
import com.eloraam.redpower.machine.TileBufferChest;
import com.eloraam.redpower.machine.TileChargingBench;
import com.eloraam.redpower.machine.TileDeploy;
import com.eloraam.redpower.machine.TileEject;
import com.eloraam.redpower.machine.TileEjectBase;
import com.eloraam.redpower.machine.TileFilter;
import com.eloraam.redpower.machine.TileFrame;
import com.eloraam.redpower.machine.TileFrameMoving;
import com.eloraam.redpower.machine.TileFrameRedstoneTube;
import com.eloraam.redpower.machine.TileFrameTube;
import com.eloraam.redpower.machine.TileGrate;
import com.eloraam.redpower.machine.TileIgniter;
import com.eloraam.redpower.machine.TileItemDetect;
import com.eloraam.redpower.machine.TileMachine;
import com.eloraam.redpower.machine.TileMagTube;
import com.eloraam.redpower.machine.TileManager;
import com.eloraam.redpower.machine.TileMotor;
import com.eloraam.redpower.machine.TilePipe;
import com.eloraam.redpower.machine.TilePump;
import com.eloraam.redpower.machine.TileRedstoneTube;
import com.eloraam.redpower.machine.TileRegulator;
import com.eloraam.redpower.machine.TileRelay;
import com.eloraam.redpower.machine.TileRestrictTube;
import com.eloraam.redpower.machine.TileRetriever;
import com.eloraam.redpower.machine.TileSolarPanel;
import com.eloraam.redpower.machine.TileSorter;
import com.eloraam.redpower.machine.TileSortron;
import com.eloraam.redpower.machine.TileThermopile;
import com.eloraam.redpower.machine.TileTransformer;
import com.eloraam.redpower.machine.TileTranspose;
import com.eloraam.redpower.machine.TileTube;
import com.eloraam.redpower.machine.TileWindTurbine;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = "RedPowerMachine",
    name = "RedPower Machine",
    version = "2.0pr7",
    dependencies = "required-after:RedPowerBase"
)
public class RedPowerMachine implements IGuiHandler {
    @Instance("RedPowerMachine")
    public static RedPowerMachine instance;
    public static BlockMachine blockMachine;
    public static BlockMachine blockMachine2;
    public static BlockMachinePanel blockMachinePanel;
    public static BlockFrame blockFrame;
    public static ItemVoltmeter itemVoltmeter;
    public static ItemSonicDriver itemSonicDriver;
    public static Item itemBatteryEmpty;
    public static Item itemBatteryPowered;
    public static ItemParts itemMachineParts;
    public static ItemStack itemWoodSail;
    public static Item itemWoodTurbine;
    public static Item itemWoodWindmill;
    public static boolean FrameAlwaysCrate;
    public static int FrameLinkSize;
    public static boolean AllowGrateDump;
    @SideOnly(Side.CLIENT)
    public static IIcon frameCrossed;
    @SideOnly(Side.CLIENT)
    public static IIcon frameCovered;
    @SideOnly(Side.CLIENT)
    public static IIcon framePaneled;
    @SideOnly(Side.CLIENT)
    public static IIcon crate;
    @SideOnly(Side.CLIENT)
    public static IIcon baseTubeSide;
    @SideOnly(Side.CLIENT)
    public static IIcon baseTubeFace;
    @SideOnly(Side.CLIENT)
    public static IIcon baseTubeSideColor;
    @SideOnly(Side.CLIENT)
    public static IIcon baseTubeFaceColor;
    public static IIcon[] redstoneTubeSide = new IIcon[4];
    public static IIcon[] redstoneTubeFace = new IIcon[4];
    @SideOnly(Side.CLIENT)
    public static IIcon pipeSide;
    @SideOnly(Side.CLIENT)
    public static IIcon pipeFace;
    @SideOnly(Side.CLIENT)
    public static IIcon pipeFlanges;
    @SideOnly(Side.CLIENT)
    public static IIcon restrictTubeSide;
    @SideOnly(Side.CLIENT)
    public static IIcon restrictTubeFace;
    @SideOnly(Side.CLIENT)
    public static IIcon restrictTubeSideColor;
    @SideOnly(Side.CLIENT)
    public static IIcon restrictTubeFaceColor;
    @SideOnly(Side.CLIENT)
    public static IIcon magTubeSide;
    @SideOnly(Side.CLIENT)
    public static IIcon magTubeRing;
    @SideOnly(Side.CLIENT)
    public static IIcon magTubeFace;
    @SideOnly(Side.CLIENT)
    public static IIcon magTubeSideNR;
    @SideOnly(Side.CLIENT)
    public static IIcon magTubeFaceNR;
    @SideOnly(Side.CLIENT)
    public static IIcon tubeItemOverlay;
    @SideOnly(Side.CLIENT)
    public static IIcon electronicsBottom;
    @SideOnly(Side.CLIENT)
    public static IIcon batteryTop;
    public static IIcon[] batterySide = new IIcon[9];
    @SideOnly(Side.CLIENT)
    public static IIcon retrieverFront;
    @SideOnly(Side.CLIENT)
    public static IIcon retrieverBack;
    @SideOnly(Side.CLIENT)
    public static IIcon retrieverSide;
    @SideOnly(Side.CLIENT)
    public static IIcon retrieverSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon retrieverSideCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon retrieverSideChargedOn;
    @SideOnly(Side.CLIENT)
    public static IIcon transposerFront;
    @SideOnly(Side.CLIENT)
    public static IIcon transposerSide;
    @SideOnly(Side.CLIENT)
    public static IIcon transposerSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon filterSide;
    @SideOnly(Side.CLIENT)
    public static IIcon filterSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon breakerFront;
    @SideOnly(Side.CLIENT)
    public static IIcon breakerFrontOn;
    @SideOnly(Side.CLIENT)
    public static IIcon breakerBack;
    @SideOnly(Side.CLIENT)
    public static IIcon breakerSide;
    @SideOnly(Side.CLIENT)
    public static IIcon breakerSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon deployerBack;
    @SideOnly(Side.CLIENT)
    public static IIcon deployerFront;
    @SideOnly(Side.CLIENT)
    public static IIcon deployerFrontOn;
    @SideOnly(Side.CLIENT)
    public static IIcon deployerSide;
    @SideOnly(Side.CLIENT)
    public static IIcon deployerSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon motorBottom;
    @SideOnly(Side.CLIENT)
    public static IIcon motorSide;
    @SideOnly(Side.CLIENT)
    public static IIcon motorFront;
    @SideOnly(Side.CLIENT)
    public static IIcon motorFrontCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon motorFrontActive;
    @SideOnly(Side.CLIENT)
    public static IIcon motorTop;
    @SideOnly(Side.CLIENT)
    public static IIcon motorTopActive;
    @SideOnly(Side.CLIENT)
    public static IIcon turbineFront;
    @SideOnly(Side.CLIENT)
    public static IIcon turbineSide;
    @SideOnly(Side.CLIENT)
    public static IIcon turbineSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon thermopileFront;
    @SideOnly(Side.CLIENT)
    public static IIcon thermopileSide;
    @SideOnly(Side.CLIENT)
    public static IIcon thermopileTop;
    @SideOnly(Side.CLIENT)
    public static IIcon btFurnaceTop;
    @SideOnly(Side.CLIENT)
    public static IIcon btFurnaceSide;
    @SideOnly(Side.CLIENT)
    public static IIcon btFurnaceFront;
    @SideOnly(Side.CLIENT)
    public static IIcon btFurnaceFrontOn;
    @SideOnly(Side.CLIENT)
    public static IIcon btAlloyFurnaceTop;
    @SideOnly(Side.CLIENT)
    public static IIcon btAlloyFurnaceSide;
    @SideOnly(Side.CLIENT)
    public static IIcon btAlloyFurnaceFront;
    @SideOnly(Side.CLIENT)
    public static IIcon btAlloyFurnaceFrontOn;
    @SideOnly(Side.CLIENT)
    public static IIcon btChargerTop;
    @SideOnly(Side.CLIENT)
    public static IIcon btChargerTopOn;
    @SideOnly(Side.CLIENT)
    public static IIcon btChargerBottom;
    @SideOnly(Side.CLIENT)
    public static IIcon btChargerSide;
    @SideOnly(Side.CLIENT)
    public static IIcon btChargerSideOn;
    public static IIcon[] btChargerFront = new IIcon[6];
    public static IIcon[] btChargerFrontPowered = new IIcon[5];
    public static IIcon[] btChargerFrontActive = new IIcon[5];
    @SideOnly(Side.CLIENT)
    public static IIcon bufferFront;
    @SideOnly(Side.CLIENT)
    public static IIcon bufferBack;
    @SideOnly(Side.CLIENT)
    public static IIcon bufferSide;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterFront;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterBack;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterBackCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterBackChargedOn;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterSide;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterSideCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon sorterSideChargedOn;
    @SideOnly(Side.CLIENT)
    public static IIcon detectorSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon detectorSideAltOn;
    @SideOnly(Side.CLIENT)
    public static IIcon detectorSide;
    @SideOnly(Side.CLIENT)
    public static IIcon detectorSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon detectorSideCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon detectorSideChargedOn;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorFront;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorBack;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorSideAltCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorSide;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorSideCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon regulatorSideChargedOn;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronFront;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronBack;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronSideAltCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronSide;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronSideCharged;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon sortronSideChargedOn;
    @SideOnly(Side.CLIENT)
    public static IIcon managerFront;
    @SideOnly(Side.CLIENT)
    public static IIcon managerBack;
    public static IIcon[] managerSide = new IIcon[4];
    public static IIcon[] managerSideCharged = new IIcon[4];
    @SideOnly(Side.CLIENT)
    public static IIcon assemblerFront;
    @SideOnly(Side.CLIENT)
    public static IIcon assemblerFrontOn;
    @SideOnly(Side.CLIENT)
    public static IIcon assemblerBack;
    @SideOnly(Side.CLIENT)
    public static IIcon assemblerBackOn;
    @SideOnly(Side.CLIENT)
    public static IIcon igniterFront;
    @SideOnly(Side.CLIENT)
    public static IIcon igniterFrontOn;
    @SideOnly(Side.CLIENT)
    public static IIcon igniterSide;
    @SideOnly(Side.CLIENT)
    public static IIcon igniterSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon assemblerSide;
    @SideOnly(Side.CLIENT)
    public static IIcon assemblerSideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon ejectorSide;
    @SideOnly(Side.CLIENT)
    public static IIcon ejectorSideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon relaySide;
    @SideOnly(Side.CLIENT)
    public static IIcon relaySideOn;
    @SideOnly(Side.CLIENT)
    public static IIcon relaySideAlt;
    @SideOnly(Side.CLIENT)
    public static IIcon solarPanelTop;
    @SideOnly(Side.CLIENT)
    public static IIcon solarPanelSide;
    @SideOnly(Side.CLIENT)
    public static IIcon grateSide;
    @SideOnly(Side.CLIENT)
    public static IIcon grateBack;
    @SideOnly(Side.CLIENT)
    public static IIcon grateMossySide;
    @SideOnly(Side.CLIENT)
    public static IIcon grateEmptyBack;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {}

    @EventHandler
    public void load(FMLInitializationEvent event) {
        FrameAlwaysCrate = Config.getInt("settings.machine.frame.alwayscrate", 0) > 0;
        FrameLinkSize = Config.getInt("settings.machine.frame.linksize", 1000);
        AllowGrateDump = Config.getInt("settings.machine.frame.allowgratedump", 1) > 0;
        setupItems();
        setupBlocks();
        initAchievements();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.registerRenderers();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    private static void setupItems() {
        itemVoltmeter = new ItemVoltmeter();
        itemBatteryEmpty = new ItemTextured("rpmachine:battery")
                               .setUnlocalizedName("btbattery")
                               .setCreativeTab(CreativeTabs.tabRedstone);
        itemBatteryPowered = new ItemBattery();
        CraftLib.addOreRecipe(
            new ItemStack(itemVoltmeter),
            "WWW",
            "WNW",
            "CCC",
            'W',
            "plankWood",
            'N',
            RedPowerBase.itemNikolite,
            'C',
            "ingotCopper"
        );
        GameRegistry.registerItem(itemVoltmeter, "voltmeter");
        CraftLib.addOreRecipe(
            new ItemStack(itemBatteryEmpty, 1),
            "NCN",
            "NTN",
            "NCN",
            'N',
            RedPowerBase.itemNikolite,
            'C',
            "ingotCopper",
            'T',
            "ingotTin"
        );
        GameRegistry.registerItem(itemBatteryEmpty, "batteryEmpty");
        GameRegistry.registerItem(itemBatteryPowered, "batteryPowered");
        itemSonicDriver = new ItemSonicDriver();
        itemSonicDriver.setUnlocalizedName("sonicDriver")
            .setTextureName("rpmachine:sonicScrewdriver");
        GameRegistry.addRecipe(
            new ItemStack(itemSonicDriver, 1, itemSonicDriver.getMaxDamage()),
            new Object[] { "E  ",
                           " R ",
                           "  B",
                           'R',
                           RedPowerBase.itemIngotBrass,
                           'E',
                           RedPowerBase.itemGreenSapphire,
                           'B',
                           itemBatteryEmpty }
        );
        GameRegistry.registerItem(itemSonicDriver, "sonicDriver");
        itemWoodTurbine = new ItemWindmill(1);
        itemWoodWindmill = new ItemWindmill(2)
                               .setUnlocalizedName("windmillWood")
                               .setTextureName("rpmachine:windmill");
        itemMachineParts = new ItemParts();
        itemMachineParts.addItem(0, "rpmachine:windSailWood", "item.windSailWood");
        itemWoodSail = new ItemStack(itemMachineParts, 1, 0);
        GameRegistry.registerItem(itemMachineParts, "machineParts");
        CraftLib.addOreRecipe(
            itemWoodSail,
            "CCS",
            "CCW",
            "CCS",
            'C',
            RedPowerBase.itemCanvas,
            'W',
            "plankWood",
            'S',
            Items.stick
        );
        GameRegistry.addRecipe(
            new ItemStack(itemWoodTurbine),
            new Object[] { "SAS",
                           "SAS",
                           "SAS",
                           'S',
                           itemWoodSail,
                           'A',
                           new ItemStack(RedPowerBase.blockMicro, 1, 5905) }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemWoodWindmill),
            new Object[] { " S ",
                           "SAS",
                           " S ",
                           'S',
                           itemWoodSail,
                           'A',
                           new ItemStack(RedPowerBase.blockMicro, 1, 5905) }
        );
        GameRegistry.registerItem(itemWoodTurbine, "woodTurbine");
        GameRegistry.registerItem(itemWoodWindmill, "woodWindmill");
    }

    private static void setupBlocks() {
        blockMachine = new BlockMachine();
        blockMachine.setBlockName("rpmachine");
        GameRegistry.registerBlock(blockMachine, ItemExtended.class, "machine");
        blockMachine.setBlockName(0, "rpdeploy");
        blockMachine.setBlockName(1, "rpbreaker");
        blockMachine.setBlockName(2, "rptranspose");
        blockMachine.setBlockName(3, "rpfilter");
        blockMachine.setBlockName(4, "rpitemdet");
        blockMachine.setBlockName(5, "rpsorter");
        blockMachine.setBlockName(6, "rpbatbox");
        blockMachine.setBlockName(7, "rpmotor");
        blockMachine.setBlockName(8, "rpretriever");
        blockMachine.setBlockName(9, "rpkgen");
        blockMachine.setBlockName(10, "rpregulate");
        blockMachine.setBlockName(11, "rpthermo");
        blockMachine.setBlockName(12, "rpignite");
        blockMachine.setBlockName(13, "rpassemble");
        blockMachine.setBlockName(14, "rpeject");
        blockMachine.setBlockName(15, "rprelay");
        GameRegistry.registerTileEntity(TileWindTurbine.class, "RPWind");
        GameRegistry.registerTileEntity(TilePipe.class, "RPPipe");
        GameRegistry.registerTileEntity(TilePump.class, "RPPump");
        GameRegistry.registerTileEntity(TileTube.class, "RPTube");
        GameRegistry.registerTileEntity(TileRedstoneTube.class, "RPRSTube");
        GameRegistry.registerTileEntity(TileRestrictTube.class, "RPRTube");
        GameRegistry.registerTileEntity(TileMagTube.class, "RPMTube");
        GameRegistry.registerTileEntity(TileAccel.class, "RPAccel");
        GameRegistry.registerTileEntity(TileDeploy.class, "RPDeploy");
        GameRegistry.registerTileEntity(TileBreaker.class, "RPBreaker");
        GameRegistry.registerTileEntity(TileTranspose.class, "RPTranspose");
        GameRegistry.registerTileEntity(TileFilter.class, "RPFilter");
        GameRegistry.registerTileEntity(TileItemDetect.class, "RPItemDet");
        GameRegistry.registerTileEntity(TileSorter.class, "RPSorter");
        GameRegistry.registerTileEntity(TileBatteryBox.class, "RPBatBox");
        GameRegistry.registerTileEntity(TileMotor.class, "RPMotor");
        GameRegistry.registerTileEntity(TileRetriever.class, "RPRetrieve");
        GameRegistry.registerTileEntity(TileRegulator.class, "RPRegulate");
        GameRegistry.registerTileEntity(TileThermopile.class, "RPThermo");
        GameRegistry.registerTileEntity(TileIgniter.class, "RPIgnite");
        GameRegistry.registerTileEntity(TileAssemble.class, "RPAssemble");
        GameRegistry.registerTileEntity(TileEject.class, "RPEject");
        GameRegistry.registerTileEntity(TileRelay.class, "RPRelay");
        blockMachine.addTileEntityMapping(0, TileDeploy::new);
        blockMachine.addTileEntityMapping(1, TileBreaker::new);
        blockMachine.addTileEntityMapping(2, TileTranspose::new);
        blockMachine.addTileEntityMapping(3, TileFilter::new);
        blockMachine.addTileEntityMapping(4, TileItemDetect::new);
        blockMachine.addTileEntityMapping(5, TileSorter::new);
        blockMachine.addTileEntityMapping(6, TileBatteryBox::new);
        blockMachine.addTileEntityMapping(7, TileMotor::new);
        blockMachine.addTileEntityMapping(8, TileRetriever::new);
        blockMachine.addTileEntityMapping(9, TileWindTurbine::new);
        blockMachine.addTileEntityMapping(10, TileRegulator::new);
        blockMachine.addTileEntityMapping(11, TileThermopile::new);
        blockMachine.addTileEntityMapping(12, TileIgniter::new);
        blockMachine.addTileEntityMapping(13, TileAssemble::new);
        blockMachine.addTileEntityMapping(14, TileEject::new);
        blockMachine.addTileEntityMapping(15, TileRelay::new);
        blockMachine2 = new BlockMachine();
        blockMachine.setBlockName("rpmachine2");
        GameRegistry.registerBlock(blockMachine2, ItemExtended.class, "machine2");
        blockMachine2.setBlockName(0, "rpsortron");
        blockMachine2.setBlockName(1, "rpmanager");
        GameRegistry.registerTileEntity(TileSortron.class, "RPSortron");
        GameRegistry.registerTileEntity(TileManager.class, "RPManager");
        blockMachine2.addTileEntityMapping(0, TileSortron::new);
        blockMachine2.addTileEntityMapping(1, TileManager::new);
        blockMachinePanel = new BlockMachinePanel();
        GameRegistry.registerBlock(
            blockMachinePanel, ItemMachinePanel.class, "machinePanel"
        );
        GameRegistry.registerTileEntity(TileSolarPanel.class, "RPSolar");
        GameRegistry.registerTileEntity(TileGrate.class, "RPGrate");
        GameRegistry.registerTileEntity(TileTransformer.class, "RPXfmr");
        blockMachinePanel.addTileEntityMapping(0, TileSolarPanel::new);
        blockMachinePanel.addTileEntityMapping(1, TilePump::new);
        blockMachinePanel.addTileEntityMapping(2, TileAccel::new);
        blockMachinePanel.addTileEntityMapping(3, TileGrate::new);
        blockMachinePanel.addTileEntityMapping(4, TileTransformer::new);
        blockMachinePanel.setBlockName(0, "rpsolar");
        blockMachinePanel.setBlockName(1, "rppump");
        blockMachinePanel.setBlockName(2, "rpaccel");
        blockMachinePanel.setBlockName(3, "rpgrate");
        blockMachinePanel.setBlockName(4, "rptransformer");
        GameRegistry.registerTileEntity(TileBlueFurnace.class, "RPBFurnace");
        GameRegistry.registerTileEntity(TileBufferChest.class, "RPBuffer");
        GameRegistry.registerTileEntity(TileBlueAlloyFurnace.class, "RPBAFurnace");
        GameRegistry.registerTileEntity(TileChargingBench.class, "RPCharge");
        RedPowerBase.blockAppliance.setBlockName(1, "rpbfurnace");
        RedPowerBase.blockAppliance.addTileEntityMapping(1, TileBlueFurnace::new);
        RedPowerBase.blockAppliance.setBlockName(2, "rpbuffer");
        RedPowerBase.blockAppliance.addTileEntityMapping(2, TileBufferChest::new);
        RedPowerBase.blockAppliance.setBlockName(4, "rpbafurnace");
        RedPowerBase.blockAppliance.addTileEntityMapping(4, TileBlueAlloyFurnace::new);
        RedPowerBase.blockAppliance.setBlockName(5, "rpcharge");
        RedPowerBase.blockAppliance.addTileEntityMapping(5, TileChargingBench::new);
        blockFrame = new BlockFrame();
        GameRegistry.registerBlock(blockFrame, ItemExtended.class, "frame");
        blockFrame.setBlockName("rpframe");
        blockFrame.setBlockName(0, "rpframe");
        blockFrame.setBlockName(2, "rptframe");
        blockFrame.setBlockName(3, "rprtframe");
        GameRegistry.registerTileEntity(TileFrame.class, "RPFrame");
        GameRegistry.registerTileEntity(TileFrameMoving.class, "RPMFrame");
        GameRegistry.registerTileEntity(TileFrameTube.class, "RPTFrame");
        GameRegistry.registerTileEntity(TileFrameRedstoneTube.class, "RPRTFrame");
        blockFrame.addTileEntityMapping(0, TileFrame::new);
        blockFrame.addTileEntityMapping(1, TileFrameMoving::new);
        blockFrame.addTileEntityMapping(2, TileFrameTube::new);
        blockFrame.addTileEntityMapping(3, TileFrameRedstoneTube::new);
        MicroPlacementTube imp = new MicroPlacementTube();
        RedPowerBase.blockMicro.registerPlacement(7, imp);
        RedPowerBase.blockMicro.registerPlacement(8, imp);
        RedPowerBase.blockMicro.registerPlacement(9, imp);
        RedPowerBase.blockMicro.registerPlacement(10, imp);
        RedPowerBase.blockMicro.registerPlacement(11, imp);
        RedPowerBase.blockMicro.addTileEntityMapping(7, TilePipe::new);
        RedPowerBase.blockMicro.addTileEntityMapping(8, TileTube::new);
        RedPowerBase.blockMicro.addTileEntityMapping(9, TileRedstoneTube::new);
        RedPowerBase.blockMicro.addTileEntityMapping(10, TileRestrictTube::new);
        RedPowerBase.blockMicro.addTileEntityMapping(11, TileMagTube::new);
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 0),
            new Object[] { "SCS",
                           "SPS",
                           "SRS",
                           'S',
                           Blocks.cobblestone,
                           'C',
                           Blocks.chest,
                           'R',
                           Items.redstone,
                           'P',
                           Blocks.piston }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 1),
            new Object[] { "SAS",
                           "SPS",
                           "SRS",
                           'S',
                           Blocks.cobblestone,
                           'A',
                           Items.iron_pickaxe,
                           'R',
                           Items.redstone,
                           'P',
                           Blocks.piston }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 2),
            "SSS",
            "WPW",
            "SRS",
            'S',
            Blocks.cobblestone,
            'R',
            Items.redstone,
            'P',
            Blocks.piston,
            'W',
            "plankWood"
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 3),
            new Object[] { "SSS",
                           "GPG",
                           "SRS",
                           'S',
                           Blocks.cobblestone,
                           'R',
                           RedPowerBase.itemWaferRed,
                           'P',
                           Blocks.piston,
                           'G',
                           Items.gold_ingot }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 4),
            "BTB",
            "RPR",
            "WTW",
            'B',
            "ingotBrass",
            'T',
            new ItemStack(RedPowerBase.blockMicro, 1, 2048),
            'R',
            RedPowerBase.itemWaferRed,
            'W',
            "plankWood",
            'P',
            Blocks.wooden_pressure_plate
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 5),
            new Object[] { "III",
                           "RFR",
                           "IBI",
                           'B',
                           RedPowerBase.itemIngotBlue,
                           'R',
                           RedPowerBase.itemWaferRed,
                           'F',
                           new ItemStack(blockMachine, 1, 3),
                           'I',
                           Items.iron_ingot }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 8),
            new Object[] { "BLB",
                           "EFE",
                           "INI",
                           'N',
                           RedPowerBase.itemIngotBlue,
                           'B',
                           RedPowerBase.itemIngotBrass,
                           'E',
                           Items.ender_pearl,
                           'L',
                           Items.leather,
                           'F',
                           new ItemStack(blockMachine, 1, 3),
                           'I',
                           Items.iron_ingot }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 9),
            new Object[] { "IBI",
                           "IMI",
                           "IUI",
                           'I',
                           Items.iron_ingot,
                           'B',
                           RedPowerBase.itemIngotBrass,
                           'M',
                           RedPowerBase.itemMotor,
                           'U',
                           RedPowerBase.itemIngotBlue }
        );
        CraftLib.addOreRecipe(
            new ItemStack(RedPowerBase.blockAppliance, 1, 2),
            "BWB",
            "W W",
            "BWB",
            'B',
            Blocks.iron_bars,
            'W',
            "plankWood"
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 10),
            "BCB",
            "RDR",
            "WCW",
            'R',
            RedPowerBase.itemWaferRed,
            'B',
            "ingotBrass",
            'D',
            new ItemStack(blockMachine, 1, 4),
            'W',
            "plankWood",
            'C',
            new ItemStack(RedPowerBase.blockAppliance, 1, 2)
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 11),
            "CIC",
            "WBW",
            "CIC",
            'I',
            Items.iron_ingot,
            'B',
            RedPowerBase.itemIngotBlue,
            'W',
            RedPowerBase.itemWaferBlue,
            'C',
            "ingotCopper"
        );
        CraftLib.addOreRecipe(
            new ItemStack(RedPowerBase.blockMicro, 8, 2048),
            "BGB",
            'G',
            Blocks.glass,
            'B',
            "ingotBrass"
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(RedPowerBase.blockMicro, 1, 2304),
            new Object[] { Items.redstone,
                           new ItemStack(RedPowerBase.blockMicro, 1, 2048) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(RedPowerBase.blockMicro, 1, 2560),
            new Object[] { Items.iron_ingot,
                           new ItemStack(RedPowerBase.blockMicro, 1, 2048) }
        );
        GameRegistry.addRecipe(
            new ItemStack(RedPowerBase.blockMicro, 8, 2816),
            new Object[] { "CCC",
                           "OGO",
                           "CCC",
                           'G',
                           Blocks.glass,
                           'O',
                           Blocks.obsidian,
                           'C',
                           RedPowerBase.itemFineCopper }
        );
        GameRegistry.addRecipe(
            new ItemStack(RedPowerBase.blockAppliance, 1, 1),
            new Object[] { "CCC",
                           "C C",
                           "IBI",
                           'C',
                           Blocks.clay,
                           'B',
                           RedPowerBase.itemIngotBlue,
                           'I',
                           Items.iron_ingot }
        );
        GameRegistry.addRecipe(
            new ItemStack(RedPowerBase.blockAppliance, 1, 4),
            new Object[] { "CCC",
                           "C C",
                           "IBI",
                           'C',
                           Blocks.brick_block,
                           'B',
                           RedPowerBase.itemIngotBlue,
                           'I',
                           Items.iron_ingot }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachinePanel, 1, 0),
            new Object[] { "WWW",
                           "WBW",
                           "WWW",
                           'W',
                           RedPowerBase.itemWaferBlue,
                           'B',
                           RedPowerBase.itemIngotBlue }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachinePanel, 1, 2),
            new Object[] { "BOB",
                           "O O",
                           "BOB",
                           'O',
                           Blocks.obsidian,
                           'B',
                           RedPowerBase.itemIngotBlue }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 6),
            "BWB",
            "BIB",
            "IAI",
            'I',
            Items.iron_ingot,
            'W',
            "plankWood",
            'A',
            RedPowerBase.itemIngotBlue,
            'B',
            itemBatteryEmpty
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachinePanel, 1, 4),
            new Object[] { "III",
                           "CIC",
                           "BIB",
                           'I',
                           Items.iron_ingot,
                           'C',
                           RedPowerBase.itemCopperCoil,
                           'B',
                           RedPowerBase.itemIngotBlue }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine2, 1, 0),
            new Object[] { "IDI",
                           "RSR",
                           "IWI",
                           'D',
                           Items.diamond,
                           'I',
                           Items.iron_ingot,
                           'R',
                           RedPowerBase.itemWaferRed,
                           'W',
                           new ItemStack(RedPowerBase.blockMicro, 1, 3072),
                           'S',
                           new ItemStack(blockMachine, 1, 5) }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine2, 1, 1),
            "IMI",
            "RSR",
            "WBW",
            'I',
            Items.iron_ingot,
            'R',
            RedPowerBase.itemWaferRed,
            'S',
            new ItemStack(blockMachine, 1, 5),
            'M',
            new ItemStack(blockMachine, 1, 10),
            'W',
            "plankWood",
            'B',
            RedPowerBase.itemIngotBlue
        );
        CraftLib.addOreRecipe(
            new ItemStack(RedPowerBase.blockAppliance, 1, 5),
            "OQO",
            "BCB",
            "WUW",
            'O',
            Blocks.obsidian,
            'W',
            "plankWood",
            'U',
            RedPowerBase.itemIngotBlue,
            'C',
            Blocks.chest,
            'Q',
            RedPowerBase.itemCopperCoil,
            'B',
            itemBatteryEmpty
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 12),
            new Object[] { "NFN",
                           "SDS",
                           "SRS",
                           'N',
                           Blocks.netherrack,
                           'F',
                           Items.flint_and_steel,
                           'D',
                           new ItemStack(blockMachine, 1, 0),
                           'S',
                           Blocks.cobblestone,
                           'R',
                           Items.redstone }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachine, 1, 13),
            new Object[] { "BIB",
                           "CDC",
                           "IRI",
                           'I',
                           Items.iron_ingot,
                           'D',
                           new ItemStack(blockMachine, 1, 0),
                           'C',
                           new ItemStack(RedPowerBase.blockMicro, 1, 768),
                           'R',
                           RedPowerBase.itemWaferRed,
                           'B',
                           RedPowerBase.itemIngotBrass }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 14),
            "WBW",
            "WTW",
            "SRS",
            'R',
            Items.redstone,
            'T',
            new ItemStack(blockMachine, 1, 2),
            'W',
            "plankWood",
            'B',
            new ItemStack(RedPowerBase.blockAppliance, 1, 2),
            'S',
            Blocks.cobblestone
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 15),
            "WBW",
            "WTW",
            "SRS",
            'R',
            RedPowerBase.itemWaferRed,
            'T',
            new ItemStack(blockMachine, 1, 2),
            'W',
            "plankWood",
            'B',
            new ItemStack(RedPowerBase.blockAppliance, 1, 2),
            'S',
            Blocks.cobblestone
        );
        GameRegistry.addRecipe(
            RedPowerBase.itemCopperCoil,
            new Object[] { "FBF",
                           "BIB",
                           "FBF",
                           'F',
                           RedPowerBase.itemFineCopper,
                           'B',
                           Blocks.iron_bars,
                           'I',
                           Items.iron_ingot }
        );
        GameRegistry.addRecipe(
            RedPowerBase.itemMotor,
            new Object[] { "ICI",
                           "ICI",
                           "IBI",
                           'C',
                           RedPowerBase.itemCopperCoil,
                           'B',
                           RedPowerBase.itemIngotBlue,
                           'I',
                           Items.iron_ingot }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockFrame, 1),
            "SSS",
            "SBS",
            "SSS",
            'S',
            Items.stick,
            'B',
            "ingotBrass"
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(blockFrame, 1, 2),
            new Object[] { new ItemStack(blockFrame, 1),
                           new ItemStack(RedPowerBase.blockMicro, 1, 2048) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(blockFrame, 1, 3),
            new Object[] { new ItemStack(blockFrame, 1),
                           new ItemStack(RedPowerBase.blockMicro, 1, 2304) }
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(blockFrame, 1, 3),
            new Object[] { new ItemStack(blockFrame, 1, 2), Items.redstone }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockMachine, 1, 7),
            "III",
            "BMB",
            "IAI",
            'I',
            Items.iron_ingot,
            'A',
            RedPowerBase.itemIngotBlue,
            'B',
            "ingotBrass",
            'M',
            RedPowerBase.itemMotor
        );
        CraftLib.addOreRecipe(
            new ItemStack(RedPowerBase.blockMicro, 16, 1792),
            "B B",
            "BGB",
            "B B",
            'G',
            Blocks.glass,
            'B',
            "ingotBrass"
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachinePanel, 1, 3),
            new Object[] { "III",
                           "I I",
                           "IPI",
                           'P',
                           new ItemStack(RedPowerBase.blockMicro, 1, 1792),
                           'I',
                           Blocks.iron_bars }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockMachinePanel, 1, 1),
            new Object[] { "III",
                           "PMP",
                           "IAI",
                           'I',
                           Items.iron_ingot,
                           'A',
                           RedPowerBase.itemIngotBlue,
                           'P',
                           new ItemStack(RedPowerBase.blockMicro, 1, 1792),
                           'M',
                           RedPowerBase.itemMotor }
        );
    }

    public static void initAchievements() {
        AchieveLib.registerAchievement(
            "rpTranspose",
            -2,
            2,
            new ItemStack(blockMachine, 1, 2),
            AchievementList.acquireIron
        );
        AchieveLib.registerAchievement(
            "rpBreaker",
            -2,
            4,
            new ItemStack(blockMachine, 1, 1),
            AchievementList.acquireIron
        );
        AchieveLib.registerAchievement(
            "rpDeploy",
            -2,
            6,
            new ItemStack(blockMachine, 1, 0),
            AchievementList.acquireIron
        );
        AchieveLib.addCraftingAchievement(
            new ItemStack(blockMachine, 1, 2), "rpTranspose"
        );
        AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 1), "rpBreaker");
        AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 0), "rpDeploy");
        AchieveLib.registerAchievement(
            "rpFrames", 4, 4, new ItemStack(blockMachine, 1, 7), "rpIngotBlue"
        );
        AchieveLib.registerAchievement(
            "rpPump", 4, 5, new ItemStack(blockMachinePanel, 1, 1), "rpIngotBlue"
        );
        AchieveLib.addCraftingAchievement(new ItemStack(blockMachine, 1, 7), "rpFrames");
        AchieveLib.addCraftingAchievement(
            new ItemStack(blockMachinePanel, 1, 1), "rpPump"
        );
    }

    public Object
    getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
        switch (ID) {
            case 1:
                return new GuiDeploy(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileDeploy.class)
                );
            case 2:
                return new GuiFilter(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileFilter.class)
                );
            case 3:
                return new GuiBlueFurnace(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileBlueFurnace.class)
                );
            case 4:
                return new GuiBufferChest(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileBufferChest.class)
                );
            case 5:
                return new GuiSorter(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileSorter.class)
                );
            case 6:
                return new GuiItemDetect(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileItemDetect.class)
                );
            case 7:
                return new GuiRetriever(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileRetriever.class)
                );
            case 8:
                return new GuiBatteryBox(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileBatteryBox.class)
                );
            case 9:
                return new GuiRegulator(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileRegulator.class)
                );
            case 10:
                return new GuiBlueAlloyFurnace(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileBlueAlloyFurnace.class)
                );
            case 11:
                return new GuiAssemble(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileAssemble.class)
                );
            case 12:
                return new GuiEject(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileEjectBase.class)
                );
            case 13:
                return new GuiEject(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileRelay.class)
                );
            case 14:
                return new GuiChargingBench(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileChargingBench.class)
                );
            case 15:
                return new GuiWindTurbine(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileWindTurbine.class)
                );
            case 16:
                return new GuiManager(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileManager.class)
                );
            default:
                return null;
        }
    }

    public Object
    getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
        switch (ID) {
            case 1:
                return new ContainerDeploy(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileDeploy.class)
                );
            case 2:
                return new ContainerFilter(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileFilter.class)
                );
            case 3:
                return new ContainerBlueFurnace(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileBlueFurnace.class)
                );
            case 4:
                return new ContainerBufferChest(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileBufferChest.class)
                );
            case 5:
                return new ContainerSorter(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileSorter.class)
                );
            case 6:
                return new ContainerItemDetect(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileItemDetect.class)
                );
            case 7:
                return new ContainerRetriever(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileRetriever.class)
                );
            case 8:
                return new ContainerBatteryBox(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileBatteryBox.class)
                );
            case 9:
                return new ContainerRegulator(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileRegulator.class)
                );
            case 10:
                return new ContainerBlueAlloyFurnace(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileBlueAlloyFurnace.class)
                );
            case 11:
                return new ContainerAssemble(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileAssemble.class)
                );
            case 12:
                return new ContainerEject(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileEjectBase.class)
                );
            case 13:
                return new ContainerEject(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileRelay.class)
                );
            case 14:
                return new ContainerChargingBench(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileChargingBench.class)
                );
            case 15:
                return new ContainerWindTurbine(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileWindTurbine.class)
                );
            case 16:
                return new ContainerManager(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileManager.class)
                );
            default:
                return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        RenderLib.setRenderer(blockMachine, 0, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 1, RenderBreaker::new);
        RenderLib.setRenderer(blockMachine, 2, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 3, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 4, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 5, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 6, RenderBatteryBox::new);
        RenderLib.setRenderer(blockMachine, 7, RenderMotor::new);
        RenderLib.setRenderer(blockMachine, 8, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 9, RenderWindTurbine::new);
        RenderLib.setRenderer(blockMachine, 10, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 11, RenderThermopile::new);
        RenderLib.setRenderer(blockMachine, 12, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 13, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 14, RenderMachine::new);
        RenderLib.setRenderer(blockMachine, 15, RenderMachine::new);
        RenderLib.setRenderer(blockMachine2, 0, RenderMachine::new);
        RenderLib.setRenderer(blockMachine2, 1, RenderMachine::new);
        RenderLib.setRenderer(RedPowerBase.blockAppliance, 1, RenderBlueFurnace::new);
        RenderLib.setRenderer(RedPowerBase.blockAppliance, 2, RenderBufferChest::new);
        RenderLib.setRenderer(
            RedPowerBase.blockAppliance, 4, RenderBlueAlloyFurnace::new
        );
        RenderLib.setRenderer(RedPowerBase.blockAppliance, 5, RenderChargingBench::new);
        RenderLib.setHighRenderer(RedPowerBase.blockMicro, 7, RenderPipe::new);
        RenderLib.setHighRenderer(RedPowerBase.blockMicro, 8, RenderTube::new);
        RenderLib.setHighRenderer(RedPowerBase.blockMicro, 9, RenderRedstoneTube::new);
        RenderLib.setHighRenderer(RedPowerBase.blockMicro, 10, RenderTube::new);
        RenderLib.setHighRenderer(RedPowerBase.blockMicro, 11, RenderTube::new);
        RenderLib.setRenderer(blockMachinePanel, 0, RenderSolarPanel::new);
        RenderLib.setRenderer(blockMachinePanel, 1, RenderPump::new);
        RenderLib.setRenderer(blockMachinePanel, 2, RenderAccel::new);
        RenderLib.setRenderer(blockMachinePanel, 3, RenderGrate::new);
        RenderLib.setRenderer(blockMachinePanel, 4, RenderTransformer::new);
        RenderLib.setRenderer(blockFrame, 0, RenderFrame::new);
        RenderLib.setRenderer(blockFrame, 1, RenderFrameMoving::new);
        RenderLib.setRenderer(blockFrame, 2, RenderFrameTube::new);
        RenderLib.setRenderer(blockFrame, 3, RenderFrameRedstoneTube::new);
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBreaker.class, new RenderBreaker(blockMachine)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileFrame.class, new RenderFrame(blockFrame)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileFrameTube.class, new RenderFrameTube(blockFrame)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileFrameRedstoneTube.class, new RenderFrameRedstoneTube(blockFrame)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileFrameMoving.class, new RenderFrameMoving(blockFrame)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileMachine.class, new RenderMachine(blockMachine)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileTube.class, new RenderTube(RedPowerBase.blockMicro)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileRedstoneTube.class, new RenderRedstoneTube(RedPowerBase.blockMicro)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileMotor.class, new RenderMotor(blockMachine)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileAccel.class, new RenderAccel(blockMachinePanel)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TilePump.class, new RenderPump(blockMachinePanel)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileTransformer.class, new RenderTransformer(blockMachinePanel)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileThermopile.class, new RenderThermopile(blockMachine)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TilePipe.class, new RenderPipe(RedPowerBase.blockMicro)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileWindTurbine.class, new RenderWindTurbine(blockMachine)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileGrate.class, new RenderGrate(blockMachinePanel)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileSolarPanel.class, new RenderSolarPanel(blockMachinePanel)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBatteryBox.class, new RenderBatteryBox(blockMachine)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBlueFurnace.class, new RenderBlueFurnace(RedPowerBase.blockAppliance)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBlueAlloyFurnace.class,
            new RenderBlueAlloyFurnace(RedPowerBase.blockAppliance)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileChargingBench.class, new RenderChargingBench(RedPowerBase.blockAppliance)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBufferChest.class, new RenderBufferChest(RedPowerBase.blockAppliance)
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(Pre evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            frameCrossed = map.registerIcon("rpmachine:frameCrossed");
            frameCovered = map.registerIcon("rpmachine:frameCovered");
            framePaneled = map.registerIcon("rpmachine:framePaneled");
            crate = map.registerIcon("rpmachine:crate");
            electronicsBottom = map.registerIcon("rpmachine:electronicsBottom");
            batteryTop = map.registerIcon("rpmachine:batteryTop");

            for (int i = 0; i < 9; ++i) {
                batterySide[i] = map.registerIcon("rpmachine:batterySide/" + i);
            }

            retrieverFront = map.registerIcon("rpmachine:retrieverFront");
            retrieverBack = map.registerIcon("rpmachine:retrieverBack");
            retrieverSide = map.registerIcon("rpmachine:retrieverSide");
            retrieverSideOn = map.registerIcon("rpmachine:retrieverSideOn");
            retrieverSideCharged = map.registerIcon("rpmachine:retrieverSideCharged");
            retrieverSideChargedOn = map.registerIcon("rpmachine:retrieverSideChargedOn");
            transposerFront = map.registerIcon("rpmachine:transposerFront");
            transposerSide = map.registerIcon("rpmachine:transposerSide");
            transposerSideOn = map.registerIcon("rpmachine:transposerSideOn");
            filterSide = map.registerIcon("rpmachine:filterSide");
            filterSideOn = map.registerIcon("rpmachine:filterSideOn");
            breakerFront = map.registerIcon("rpmachine:breakerFront");
            breakerFrontOn = map.registerIcon("rpmachine:breakerFrontOn");
            breakerBack = map.registerIcon("rpmachine:breakerBack");
            breakerSide = map.registerIcon("rpmachine:breakerSide");
            breakerSideOn = map.registerIcon("rpmachine:breakerSideOn");
            deployerBack = map.registerIcon("rpmachine:deployerBack");
            deployerFront = map.registerIcon("rpmachine:deployerFront");
            deployerFrontOn = map.registerIcon("rpmachine:deployerFrontOn");
            deployerSide = map.registerIcon("rpmachine:deployerSide");
            deployerSideAlt = map.registerIcon("rpmachine:deployerSideAlt");
            motorBottom = map.registerIcon("rpmachine:motorBottom");
            motorSide = map.registerIcon("rpmachine:motorSide");
            motorFront = map.registerIcon("rpmachine:motorFront");
            motorFrontActive = map.registerIcon("rpmachine:motorFrontActive");
            motorFrontCharged = map.registerIcon("rpmachine:motorFrontCharged");
            motorTop = map.registerIcon("rpmachine:motorTop");
            motorTopActive = map.registerIcon("rpmachine:motorTopActive");
            turbineFront = map.registerIcon("rpmachine:turbineFront");
            turbineSide = map.registerIcon("rpmachine:turbineSide");
            turbineSideAlt = map.registerIcon("rpmachine:turbineSideAlt");
            thermopileFront = map.registerIcon("rpmachine:thermopileFront");
            thermopileSide = map.registerIcon("rpmachine:thermopileSide");
            thermopileTop = map.registerIcon("rpmachine:thermopileTop");
            btFurnaceTop = map.registerIcon("rpmachine:btFurnaceTop");
            btFurnaceSide = map.registerIcon("rpmachine:btFurnaceSide");
            btFurnaceFront = map.registerIcon("rpmachine:btFurnaceFront");
            btFurnaceFrontOn = map.registerIcon("rpmachine:btFurnaceFrontOn");
            btAlloyFurnaceTop = map.registerIcon("rpmachine:btAlloyFurnaceTop");
            btAlloyFurnaceSide = map.registerIcon("rpmachine:btAlloyFurnaceSide");
            btAlloyFurnaceFront = map.registerIcon("rpmachine:btAlloyFurnaceFront");
            btAlloyFurnaceFrontOn = map.registerIcon("rpmachine:btAlloyFurnaceFrontOn");
            btChargerTop = map.registerIcon("rpmachine:btChargerTop");
            btChargerTopOn = map.registerIcon("rpmachine:btChargerTopOn");
            btChargerBottom = map.registerIcon("rpmachine:btChargerBottom");
            btChargerSide = map.registerIcon("rpmachine:btChargerSide");
            btChargerSideOn = map.registerIcon("rpmachine:btChargerSideOn");

            for (int i = 0; i < 5; ++i) {
                btChargerFront[i] = map.registerIcon("rpmachine:btChargerFront/" + i);
                btChargerFrontPowered[i]
                    = map.registerIcon("rpmachine:btChargerFrontPowered/" + i);
                btChargerFrontActive[i]
                    = map.registerIcon("rpmachine:btChargerFrontActive/" + i);
            }

            bufferFront = map.registerIcon("rpmachine:bufferFront");
            bufferBack = map.registerIcon("rpmachine:bufferBack");
            bufferSide = map.registerIcon("rpmachine:bufferSide");
            igniterFront = map.registerIcon("rpmachine:igniterFront");
            igniterFrontOn = map.registerIcon("rpmachine:igniterFrontOn");
            igniterSide = map.registerIcon("rpmachine:igniterSide");
            igniterSideAlt = map.registerIcon("rpmachine:igniterSideAlt");
            sorterFront = map.registerIcon("rpmachine:sorterFront");
            sorterBack = map.registerIcon("rpmachine:sorterBack");
            sorterBackCharged = map.registerIcon("rpmachine:sorterBackCharged");
            sorterBackChargedOn = map.registerIcon("rpmachine:sorterBackChargedOn");
            sorterSide = map.registerIcon("rpmachine:sorterSide");
            sorterSideOn = map.registerIcon("rpmachine:sorterSideOn");
            sorterSideCharged = map.registerIcon("rpmachine:sorterSideCharged");
            sorterSideChargedOn = map.registerIcon("rpmachine:sorterSideChargedOn");
            detectorSideAlt = map.registerIcon("rpmachine:detectorSideAlt");
            detectorSideAltOn = map.registerIcon("rpmachine:detectorSideAltOn");
            detectorSide = map.registerIcon("rpmachine:detectorSide");
            detectorSideOn = map.registerIcon("rpmachine:detectorSideOn");
            detectorSideCharged = map.registerIcon("rpmachine:detectorSideCharged");
            detectorSideChargedOn = map.registerIcon("rpmachine:detectorSideChargedOn");
            regulatorFront = map.registerIcon("rpmachine:regulatorFront");
            regulatorBack = map.registerIcon("rpmachine:regulatorBack");
            regulatorSideAlt = map.registerIcon("rpmachine:regulatorSideAlt");
            regulatorSideAltCharged
                = map.registerIcon("rpmachine:regulatorSideAltCharged");
            regulatorSide = map.registerIcon("rpmachine:regulatorSide");
            regulatorSideOn = map.registerIcon("rpmachine:regulatorSideOn");
            regulatorSideCharged = map.registerIcon("rpmachine:regulatorSideCharged");
            regulatorSideChargedOn = map.registerIcon("rpmachine:regulatorSideChargedOn");
            sortronFront = map.registerIcon("rpmachine:sortronFront");
            sortronBack = map.registerIcon("rpmachine:sortronBack");
            sortronSideAlt = map.registerIcon("rpmachine:sortronSideAlt");
            sortronSideAltCharged = map.registerIcon("rpmachine:sortronSideAltCharged");
            sortronSide = map.registerIcon("rpmachine:sortronSide");
            sortronSideOn = map.registerIcon("rpmachine:sortronSideOn");
            sortronSideCharged = map.registerIcon("rpmachine:sortronSideCharged");
            sortronSideChargedOn = map.registerIcon("rpmachine:sortronSideChargedOn");
            managerFront = map.registerIcon("rpmachine:managerFront");
            managerBack = map.registerIcon("rpmachine:managerBack");

            for (int i = 0; i < 4; ++i) {
                managerSide[i] = map.registerIcon("rpmachine:managerSide/" + i);
            }

            for (int i = 0; i < 4; ++i) {
                managerSideCharged[i]
                    = map.registerIcon("rpmachine:managerSideCharged/" + i);
            }

            assemblerFront = map.registerIcon("rpmachine:assemblerFront");
            assemblerFrontOn = map.registerIcon("rpmachine:assemblerFrontOn");
            assemblerBack = map.registerIcon("rpmachine:assemblerBack");
            assemblerBackOn = map.registerIcon("rpmachine:assemblerBackOn");
            assemblerSide = map.registerIcon("rpmachine:assemblerSide");
            assemblerSideAlt = map.registerIcon("rpmachine:assemblerSideAlt");
            ejectorSide = map.registerIcon("rpmachine:ejectorSide");
            ejectorSideOn = map.registerIcon("rpmachine:ejectorSideOn");
            relaySide = map.registerIcon("rpmachine:relaySide");
            relaySideOn = map.registerIcon("rpmachine:relaySideOn");
            relaySideAlt = map.registerIcon("rpmachine:relaySideAlt");
            pipeSide = map.registerIcon("rpmachine:pipeSide");
            pipeFace = map.registerIcon("rpmachine:pipeFace");
            pipeFlanges = map.registerIcon("rpmachine:pipeFlanges");
            baseTubeSide = map.registerIcon("rpmachine:tubeSide");
            baseTubeFace = map.registerIcon("rpmachine:tubeFace");
            baseTubeSideColor = map.registerIcon("rpmachine:tubeSideColor");
            baseTubeFaceColor = map.registerIcon("rpmachine:tubeFaceColor");

            for (int i = 0; i < 4; ++i) {
                redstoneTubeSide[i] = map.registerIcon("rpmachine:redstoneTubeSide/" + i);
                redstoneTubeFace[i] = map.registerIcon("rpmachine:redstoneTubeFace/" + i);
            }

            restrictTubeSide = map.registerIcon("rpmachine:restrictionTubeSide");
            restrictTubeFace = map.registerIcon("rpmachine:restrictionTubeFace");
            restrictTubeSideColor
                = map.registerIcon("rpmachine:restrictionTubeSideColor");
            restrictTubeFaceColor
                = map.registerIcon("rpmachine:restrictionTubeFaceColor");
            magTubeSide = map.registerIcon("rpmachine:magneticTubeSide");
            magTubeRing = map.registerIcon("rpmachine:magneticTubeRing");
            magTubeFace = map.registerIcon("rpmachine:magneticTubeFace");
            magTubeSideNR = map.registerIcon("rpmachine:magneticTubeSideNR");
            magTubeFaceNR = map.registerIcon("rpmachine:magneticTubeFaceNR");
            tubeItemOverlay = map.registerIcon("rpmachine:tubeItemOverlay");
            solarPanelTop = map.registerIcon("rpmachine:solarPanelTop");
            solarPanelSide = map.registerIcon("rpmachine:solarPanelSide");
            grateSide = map.registerIcon("rpmachine:grateSide");
            grateMossySide = map.registerIcon("rpmachine:grateMossySide");
            grateBack = map.registerIcon("rpmachine:grateBack");
            grateEmptyBack = map.registerIcon("rpmachine:grateEmptyBack");
        }
    }
}
