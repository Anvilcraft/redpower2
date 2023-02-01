package com.eloraam.redpower;

import com.eloraam.redpower.control.BlockPeripheral;
import com.eloraam.redpower.control.ContainerCPU;
import com.eloraam.redpower.control.ContainerDisplay;
import com.eloraam.redpower.control.GuiCPU;
import com.eloraam.redpower.control.GuiDisplay;
import com.eloraam.redpower.control.ItemBackplane;
import com.eloraam.redpower.control.ItemDisk;
import com.eloraam.redpower.control.MicroPlacementRibbon;
import com.eloraam.redpower.control.RenderBackplane;
import com.eloraam.redpower.control.RenderCPU;
import com.eloraam.redpower.control.RenderDiskDrive;
import com.eloraam.redpower.control.RenderDisplay;
import com.eloraam.redpower.control.RenderIOExpander;
import com.eloraam.redpower.control.RenderRibbon;
import com.eloraam.redpower.control.TileBackplane;
import com.eloraam.redpower.control.TileCPU;
import com.eloraam.redpower.control.TileDiskDrive;
import com.eloraam.redpower.control.TileDisplay;
import com.eloraam.redpower.control.TileIOExpander;
import com.eloraam.redpower.control.TileRAM;
import com.eloraam.redpower.control.TileRibbon;
import com.eloraam.redpower.core.BlockExtended;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.CreativeExtraTabs;
import com.eloraam.redpower.core.ItemExtended;
import com.eloraam.redpower.core.RenderLib;
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
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = "RedPowerControl",
    name = "RedPower Control",
    version = RedPowerBase.VERSION,
    dependencies = "required-after:RedPowerBase"
)
public class RedPowerControl implements IGuiHandler {
    @Instance("RedPowerControl")
    public static RedPowerControl instance;
    public static BlockExtended blockBackplane;
    public static BlockExtended blockPeripheral;
    public static BlockExtended blockFlatPeripheral;
    public static ItemDisk itemDisk;
    public static IIcon ribbonTop;
    public static IIcon ribbonFace;
    @SideOnly(Side.CLIENT)
    public static IIcon backplaneTop;
    @SideOnly(Side.CLIENT)
    public static IIcon backplaneFace;
    @SideOnly(Side.CLIENT)
    public static IIcon backplaneSide;
    @SideOnly(Side.CLIENT)
    public static IIcon ram8kTop;
    @SideOnly(Side.CLIENT)
    public static IIcon ram8kFace;
    @SideOnly(Side.CLIENT)
    public static IIcon ram8kSide;
    @SideOnly(Side.CLIENT)
    public static IIcon peripheralBottom;
    @SideOnly(Side.CLIENT)
    public static IIcon peripheralTop;
    @SideOnly(Side.CLIENT)
    public static IIcon peripheralSide;
    @SideOnly(Side.CLIENT)
    public static IIcon peripheralBack;
    @SideOnly(Side.CLIENT)
    public static IIcon cpuFront;
    @SideOnly(Side.CLIENT)
    public static IIcon displayFront;
    @SideOnly(Side.CLIENT)
    public static IIcon diskDriveSide;
    @SideOnly(Side.CLIENT)
    public static IIcon diskDriveTop;
    @SideOnly(Side.CLIENT)
    public static IIcon diskDriveFront;
    @SideOnly(Side.CLIENT)
    public static IIcon diskDriveFrontFull;
    @SideOnly(Side.CLIENT)
    public static IIcon diskDriveFrontOn;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        setupBlocks();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.registerRenderers();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    private static void setupBlocks() {
        blockBackplane = new BlockMultipart(CoreLib.materialRedpower);
        GameRegistry.registerBlock(blockBackplane, ItemBackplane.class, "backplane");
        blockBackplane.setCreativeTab(CreativeExtraTabs.tabMachine);
        blockBackplane.setHardness(1.0F);
        blockBackplane.setBlockName(0, "rpbackplane");
        blockBackplane.setBlockName(1, "rpram");
        blockPeripheral = new BlockPeripheral();
        GameRegistry.registerBlock(blockPeripheral, ItemExtended.class, "peripheral");
        blockPeripheral.setHardness(1.0F);
        blockPeripheral.setBlockName(0, "rpdisplay");
        blockPeripheral.setBlockName(1, "rpcpu");
        blockPeripheral.setBlockName(2, "rpdiskdrive");
        blockFlatPeripheral = new BlockMultipart(Material.rock);
        blockFlatPeripheral.setCreativeTab(CreativeExtraTabs.tabMachine);
        GameRegistry.registerBlock(
            blockFlatPeripheral, ItemExtended.class, "peripheralFlat"
        );
        blockFlatPeripheral.setHardness(1.0F);
        blockFlatPeripheral.setBlockName(0, "rpioexp");
        GameRegistry.registerTileEntity(TileBackplane.class, "RPConBP");
        blockBackplane.addTileEntityMapping(0, TileBackplane::new);
        GameRegistry.registerTileEntity(TileRAM.class, "RPConRAM");
        blockBackplane.addTileEntityMapping(1, TileRAM::new);
        GameRegistry.registerTileEntity(TileDisplay.class, "RPConDisp");
        blockPeripheral.addTileEntityMapping(0, TileDisplay::new);
        GameRegistry.registerTileEntity(TileDiskDrive.class, "RPConDDrv");
        blockPeripheral.addTileEntityMapping(2, TileDiskDrive::new);
        GameRegistry.registerTileEntity(TileCPU.class, "RPConCPU");
        blockPeripheral.addTileEntityMapping(1, TileCPU::new);
        GameRegistry.registerTileEntity(TileIOExpander.class, "RPConIOX");
        blockFlatPeripheral.addTileEntityMapping(0, TileIOExpander::new);
        GameRegistry.registerTileEntity(TileRibbon.class, "RPConRibbon");
        RedPowerBase.blockMicro.addTileEntityMapping(12, TileRibbon::new);
        MicroPlacementRibbon imp = new MicroPlacementRibbon();
        RedPowerBase.blockMicro.registerPlacement(12, imp);
        itemDisk = new ItemDisk();
        itemDisk.setCreativeTab(CreativeExtraTabs.tabMachine);
        CraftLib.addOreRecipe(
            new ItemStack(itemDisk, 1),
            "WWW",
            "W W",
            "WIW",
            'I',
            Items.iron_ingot,
            'W',
            "plankWood"
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(itemDisk, 1, 1),
            new Object[] { new ItemStack(itemDisk, 1, 0), Items.redstone }
        );
        GameRegistry.registerItem(itemDisk, "diskette");
        GameRegistry.addShapelessRecipe(
            new ItemStack(itemDisk, 1, 2),
            new Object[] { new ItemStack(itemDisk, 1, 1), Items.redstone }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockBackplane, 1, 0),
            new Object[] { "ICI",
                           "IGI",
                           "ICI",
                           'C',
                           RedPowerBase.itemFineCopper,
                           'I',
                           Blocks.iron_bars,
                           'G',
                           Items.gold_ingot }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockBackplane, 1, 1),
            new Object[] { "IRI",
                           "RDR",
                           "IRI",
                           'I',
                           Blocks.iron_bars,
                           'R',
                           RedPowerBase.itemWaferRed,
                           'D',
                           Items.diamond }
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockPeripheral, 1, 0),
            "GWW",
            "GPR",
            "GBW",
            'P',
            new ItemStack(RedPowerBase.itemLumar, 1, 5),
            'G',
            Blocks.glass,
            'W',
            "plankWood",
            'R',
            RedPowerBase.itemWaferRed,
            'B',
            new ItemStack(RedPowerBase.blockMicro, 1, 3072)
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockPeripheral, 1, 1),
            "WWW",
            "RDR",
            "WBW",
            'W',
            "plankWood",
            'D',
            Blocks.diamond_block,
            'R',
            RedPowerBase.itemWaferRed,
            'B',
            new ItemStack(RedPowerBase.blockMicro, 1, 3072)
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockPeripheral, 1, 2),
            "WWW",
            "WMR",
            "WBW",
            'G',
            Blocks.glass,
            'W',
            "plankWood",
            'M',
            RedPowerBase.itemMotor,
            'R',
            RedPowerBase.itemWaferRed,
            'B',
            new ItemStack(RedPowerBase.blockMicro, 1, 3072)
        );
        CraftLib.addOreRecipe(
            new ItemStack(blockFlatPeripheral, 1, 0),
            "WCW",
            "WRW",
            "WBW",
            'W',
            "plankWood",
            'R',
            RedPowerBase.itemWaferRed,
            'C',
            new ItemStack(RedPowerBase.blockMicro, 1, 768),
            'B',
            new ItemStack(RedPowerBase.blockMicro, 1, 3072)
        );
        GameRegistry.addRecipe(
            new ItemStack(RedPowerBase.blockMicro, 8, 3072),
            new Object[] { "C", "C", "C", 'C', RedPowerBase.itemFineCopper }
        );
        ChestGenHooks.addItem(
            "dungeonChest",
            new WeightedRandomChestContent(new ItemStack(itemDisk, 1, 1), 0, 1, 1)
        );
        ChestGenHooks.addItem(
            "dungeonChest",
            new WeightedRandomChestContent(new ItemStack(itemDisk, 1, 2), 0, 1, 1)
        );
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        RenderLib.setRenderer(blockBackplane, 0, RenderBackplane::new);
        RenderLib.setRenderer(blockBackplane, 1, RenderBackplane::new);
        RenderLib.setRenderer(blockPeripheral, 0, RenderDisplay::new);
        RenderLib.setRenderer(blockPeripheral, 1, RenderCPU::new);
        RenderLib.setRenderer(blockPeripheral, 2, RenderDiskDrive::new);
        RenderLib.setRenderer(blockFlatPeripheral, 0, RenderIOExpander::new);
        RenderLib.setHighRenderer(RedPowerBase.blockMicro, 12, RenderRibbon::new);
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileBackplane.class, new RenderBackplane(blockBackplane)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileRibbon.class, new RenderRibbon(RedPowerBase.blockMicro)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileIOExpander.class, new RenderIOExpander(blockPeripheral)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileCPU.class, new RenderCPU(blockPeripheral)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileDiskDrive.class, new RenderDiskDrive(blockPeripheral)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileDisplay.class, new RenderDisplay(blockPeripheral)
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(Pre evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            ribbonTop = map.registerIcon("rpcontrol:ribbonTop");
            ribbonFace = map.registerIcon("rpcontrol:ribbonFace");
            backplaneTop = map.registerIcon("rpcontrol:backplaneTop");
            backplaneFace = map.registerIcon("rpcontrol:backplaneFace");
            backplaneSide = map.registerIcon("rpcontrol:backplaneSide");
            ram8kTop = map.registerIcon("rpcontrol:ram8kTop");
            ram8kFace = map.registerIcon("rpcontrol:ram8kFace");
            ram8kSide = map.registerIcon("rpcontrol:ram8kSide");
            peripheralBottom = map.registerIcon("rpcontrol:peripheralBottom");
            peripheralTop = map.registerIcon("rpcontrol:peripheralTop");
            peripheralSide = map.registerIcon("rpcontrol:peripheralSide");
            peripheralBack = map.registerIcon("rpcontrol:peripheralBack");
            cpuFront = map.registerIcon("rpcontrol:cpuFront");
            displayFront = map.registerIcon("rpcontrol:displayFront");
            diskDriveSide = map.registerIcon("rpcontrol:diskDriveSide");
            diskDriveTop = map.registerIcon("rpcontrol:diskDriveTop");
            diskDriveFront = map.registerIcon("rpcontrol:diskDriveFront");
            diskDriveFrontFull = map.registerIcon("rpcontrol:diskDriveFrontFull");
            diskDriveFrontOn = map.registerIcon("rpcontrol:diskDriveFrontOn");
        }
    }

    public Object
    getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 1:
                return new GuiDisplay(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, x, y, z, TileDisplay.class)
                );
            case 2:
                return new GuiCPU(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, x, y, z, TileCPU.class)
                );
            default:
                return null;
        }
    }

    public Object
    getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 1:
                return new ContainerDisplay(
                    player.inventory,
                    CoreLib.getTileEntity(world, x, y, z, TileDisplay.class)
                );
            case 2:
                return new ContainerCPU(
                    player.inventory, CoreLib.getTileEntity(world, x, y, z, TileCPU.class)
                );
            default:
                return null;
        }
    }
}
