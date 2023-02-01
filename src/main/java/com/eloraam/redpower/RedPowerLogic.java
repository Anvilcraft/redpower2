package com.eloraam.redpower;

import com.eloraam.redpower.core.Config;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.ItemParts;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.logic.BlockLogic;
import com.eloraam.redpower.logic.ContainerCounter;
import com.eloraam.redpower.logic.ContainerTimer;
import com.eloraam.redpower.logic.GuiCounter;
import com.eloraam.redpower.logic.GuiTimer;
import com.eloraam.redpower.logic.ItemLogic;
import com.eloraam.redpower.logic.RenderLogicAdv;
import com.eloraam.redpower.logic.RenderLogicArray;
import com.eloraam.redpower.logic.RenderLogicPointer;
import com.eloraam.redpower.logic.RenderLogicSimple;
import com.eloraam.redpower.logic.RenderLogicStorage;
import com.eloraam.redpower.logic.TileLogicAdv;
import com.eloraam.redpower.logic.TileLogicArray;
import com.eloraam.redpower.logic.TileLogicPointer;
import com.eloraam.redpower.logic.TileLogicSimple;
import com.eloraam.redpower.logic.TileLogicStorage;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = "RedPowerLogic",
    name = "RedPower Logic",
    version = RedPowerBase.VERSION,
    dependencies = "required-after:RedPowerBase"
)
public class RedPowerLogic implements IGuiHandler {
    @Instance("RedPowerLogic")
    public static RedPowerLogic instance;
    public static BlockLogic blockLogic;
    public static ItemParts itemParts;
    public static ItemStack itemAnode;
    public static ItemStack itemCathode;
    public static ItemStack itemWire;
    public static ItemStack itemWafer;
    public static ItemStack itemPointer;
    public static ItemStack itemPlate;
    public static ItemStack itemWaferRedwire;
    public static ItemStack itemChip;
    public static ItemStack itemTaintedChip;
    public static ItemStack itemWaferBundle;
    public static boolean soundsEnabled;
    @SideOnly(Side.CLIENT)
    public static IIcon torch;
    @SideOnly(Side.CLIENT)
    public static IIcon torchOn;
    @SideOnly(Side.CLIENT)
    public static IIcon lever;
    @SideOnly(Side.CLIENT)
    public static IIcon cobblestone;
    public static IIcon[] logicOne = new IIcon[232];
    public static IIcon[] logicTwo = new IIcon[256];
    public static IIcon[] logicSensor = new IIcon[23];

    private static void setupLogic() {
        GameRegistry.registerTileEntity(TileLogicSimple.class, "RPLgSmp");
        GameRegistry.registerTileEntity(TileLogicArray.class, "RPLgAr");
        GameRegistry.registerTileEntity(TileLogicStorage.class, "RPLgStor");
        GameRegistry.registerTileEntity(TileLogicAdv.class, "RPLgAdv");
        GameRegistry.registerTileEntity(TileLogicPointer.class, "RPLgPtr");
        itemParts = new ItemParts();
        itemParts.addItem(0, "rplogic:wafer", "item.irwafer");
        itemParts.addItem(1, "rplogic:wire", "item.irwire");
        itemParts.addItem(2, "rplogic:anode", "item.iranode");
        itemParts.addItem(3, "rplogic:cathode", "item.ircathode");
        itemParts.addItem(4, "rplogic:pointer", "item.irpointer");
        itemParts.addItem(5, "rplogic:redWire", "item.irredwire");
        itemParts.addItem(6, "rplogic:plate", "item.irplate");
        itemParts.addItem(7, "rplogic:chip", "item.irchip");
        itemParts.addItem(8, "rplogic:tchip", "item.irtchip");
        itemParts.addItem(9, "rplogic:bundle", "item.irbundle");
        GameRegistry.registerItem(itemParts, "parts");
        itemWafer = new ItemStack(itemParts, 1, 0);
        itemWire = new ItemStack(itemParts, 1, 1);
        itemAnode = new ItemStack(itemParts, 1, 2);
        itemCathode = new ItemStack(itemParts, 1, 3);
        itemPointer = new ItemStack(itemParts, 1, 4);
        itemWaferRedwire = new ItemStack(itemParts, 1, 5);
        itemPlate = new ItemStack(itemParts, 1, 6);
        itemChip = new ItemStack(itemParts, 1, 7);
        itemTaintedChip = new ItemStack(itemParts, 1, 8);
        itemWaferBundle = new ItemStack(itemParts, 1, 9);
        FurnaceRecipes.smelting().func_151393_a(
            Blocks.stone, new ItemStack(itemParts, 2, 0), 0.1F
        );
        GameRegistry.addRecipe(
            itemWire, new Object[] { "R", "B", 'B', itemWafer, 'R', Items.redstone }
        );
        GameRegistry.addRecipe(
            new ItemStack(itemParts, 3, 2),
            new Object[] { " R ", "RRR", "BBB", 'B', itemWafer, 'R', Items.redstone }
        );
        GameRegistry.addRecipe(
            itemCathode,
            new Object[] { "T", "B", 'B', itemWafer, 'T', Blocks.redstone_torch }
        );
        GameRegistry.addRecipe(
            itemPointer,
            new Object[] { "S",
                           "T",
                           "B",
                           'B',
                           itemWafer,
                           'S',
                           Blocks.stone,
                           'T',
                           Blocks.redstone_torch }
        );
        GameRegistry.addRecipe(
            itemWaferRedwire,
            new Object[] { "W",
                           "B",
                           'B',
                           itemWafer,
                           'W',
                           new ItemStack(RedPowerBase.blockMicro, 1, 256) }
        );
        GameRegistry.addRecipe(
            itemPlate,
            new Object[] { " B ",
                           "SRS",
                           "BCB",
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'R',
                           RedPowerBase.itemIngotRed,
                           'S',
                           Items.stick }
        );
        GameRegistry.addRecipe(
            CoreLib.copyStack(itemChip, 3),
            new Object[] { " R ", "BBB", 'B', itemWafer, 'R', RedPowerBase.itemWaferRed }
        );
        GameRegistry.addShapelessRecipe(
            CoreLib.copyStack(itemTaintedChip, 1),
            new Object[] { itemChip, Items.glowstone_dust }
        );
        GameRegistry.addRecipe(
            itemWaferBundle,
            new Object[] { "W",
                           "B",
                           'B',
                           itemWafer,
                           'W',
                           new ItemStack(RedPowerBase.blockMicro, 1, 768) }
        );
        blockLogic = new BlockLogic();
        GameRegistry.registerBlock(blockLogic, ItemLogic.class, "logic");
        blockLogic.addTileEntityMapping(0, TileLogicPointer::new);
        blockLogic.addTileEntityMapping(1, TileLogicSimple::new);
        blockLogic.addTileEntityMapping(2, TileLogicArray::new);
        blockLogic.addTileEntityMapping(3, TileLogicStorage::new);
        blockLogic.addTileEntityMapping(4, TileLogicAdv::new);
        blockLogic.setBlockName(0, "irtimer");
        blockLogic.setBlockName(1, "irseq");
        blockLogic.setBlockName(2, "irstate");
        blockLogic.setBlockName(256, "irlatch");
        blockLogic.setBlockName(257, "irnor");
        blockLogic.setBlockName(258, "iror");
        blockLogic.setBlockName(259, "irnand");
        blockLogic.setBlockName(260, "irand");
        blockLogic.setBlockName(261, "irxnor");
        blockLogic.setBlockName(262, "irxor");
        blockLogic.setBlockName(263, "irpulse");
        blockLogic.setBlockName(264, "irtoggle");
        blockLogic.setBlockName(265, "irnot");
        blockLogic.setBlockName(266, "irbuf");
        blockLogic.setBlockName(267, "irmux");
        blockLogic.setBlockName(268, "irrepeater");
        blockLogic.setBlockName(269, "irsync");
        blockLogic.setBlockName(270, "irrand");
        blockLogic.setBlockName(271, "irdlatch");
        blockLogic.setBlockName(272, "rplightsensor");
        blockLogic.setBlockName(512, "rpanc");
        blockLogic.setBlockName(513, "rpainv");
        blockLogic.setBlockName(514, "rpaninv");
        blockLogic.setBlockName(768, "ircounter");
        blockLogic.setBlockName(1024, "irbusxcvr");
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 0),
            new Object[] { "BWB",
                           "WPW",
                           "ACA",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode,
                           'P',
                           itemPointer }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 1),
            new Object[] { "BCB",
                           "CPC",
                           "BCB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode,
                           'P',
                           itemPointer }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 2),
            new Object[] { "BAC",
                           "WSP",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode,
                           'P',
                           itemPointer,
                           'S',
                           itemChip }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 256),
            new Object[] { "WWA",
                           "CBC",
                           "AWW",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 257),
            new Object[] { "BAB",
                           "WCW",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 258),
            new Object[] {
                "BCB", "WCW", "BWB", 'W', itemWire, 'B', itemWafer, 'C', itemCathode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 259),
            new Object[] { "AAA",
                           "CCC",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 260),
            new Object[] { "ACA",
                           "CCC",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 261),
            new Object[] { "ACA",
                           "CAC",
                           "WCW",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 262),
            new Object[] { "AWA",
                           "CAC",
                           "WCW",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 263),
            new Object[] { "ACA",
                           "CAC",
                           "WWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 264),
            new Object[] { "BCB",
                           "WLW",
                           "BCB",
                           'L',
                           Blocks.lever,
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 265),
            new Object[] { "BAB",
                           "ACA",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 266),
            new Object[] { "ACA",
                           "WCW",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 267),
            new Object[] { "ACA",
                           "CBC",
                           "ACW",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 268),
            new Object[] { "BCW",
                           "BAW",
                           "BWC",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'A',
                           itemAnode,
                           'C',
                           itemCathode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 269),
            new Object[] { "WCW",
                           "SAS",
                           "WWW",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'A',
                           itemAnode,
                           'C',
                           itemCathode,
                           'S',
                           itemChip }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 270),
            new Object[] {
                "BSB", "WWW", "SWS", 'W', itemWire, 'B', itemWafer, 'S', itemTaintedChip }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 271),
            new Object[] { "ACW",
                           "CCC",
                           "CWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'A',
                           itemAnode }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 272),
            new Object[] { "BWB",
                           "BSB",
                           "BBB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'S',
                           RedPowerBase.itemWaferBlue }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 768),
            new Object[] { "BWB",
                           "CPC",
                           "BWB",
                           'W',
                           itemWire,
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'P',
                           itemPointer }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 512),
            new Object[] { "BRB", "RRR", "BRB", 'B', itemWafer, 'R', itemWaferRedwire }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 513),
            new Object[] { "BRB",
                           "RPR",
                           "BRB",
                           'B',
                           itemWafer,
                           'R',
                           itemWaferRedwire,
                           'P',
                           itemPlate }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 514),
            new Object[] { "BRB",
                           "RPR",
                           "BRC",
                           'B',
                           itemWafer,
                           'C',
                           itemCathode,
                           'R',
                           itemWaferRedwire,
                           'P',
                           itemPlate }
        );
        GameRegistry.addRecipe(
            new ItemStack(blockLogic, 1, 1024),
            new Object[] { "CCC",
                           "WBW",
                           "CCC",
                           'B',
                           itemWafer,
                           'W',
                           RedPowerBase.itemWaferRed,
                           'C',
                           itemWaferBundle }
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
        soundsEnabled = Config.getInt("settings.logic.enableSounds", 1) > 0;
        setupLogic();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.registerRenderers();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, instance);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    public Object
    getClientGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
        switch (ID) {
            case 1:
                return new GuiCounter(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileLogicStorage.class)
                );
            case 2:
                return new GuiTimer(
                    player.inventory,
                    CoreLib.getGuiTileEntity(world, X, Y, Z, TileLogicPointer.class)
                );
            default:
                return null;
        }
    }

    public Object
    getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z) {
        switch (ID) {
            case 1:
                return new ContainerCounter(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileLogicStorage.class)
                );
            case 2:
                return new ContainerTimer(
                    player.inventory,
                    CoreLib.getTileEntity(world, X, Y, Z, TileLogicPointer.class)
                );
            default:
                return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        RenderLib.setHighRenderer(blockLogic, 0, RenderLogicPointer::new);
        RenderLib.setHighRenderer(blockLogic, 1, RenderLogicSimple::new);
        RenderLib.setHighRenderer(blockLogic, 2, RenderLogicArray::new);
        RenderLib.setHighRenderer(blockLogic, 3, RenderLogicStorage::new);
        RenderLib.setHighRenderer(blockLogic, 4, RenderLogicAdv::new);
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileLogicAdv.class, new RenderLogicAdv(blockLogic)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileLogicSimple.class, new RenderLogicSimple(blockLogic)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileLogicArray.class, new RenderLogicArray(blockLogic)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileLogicStorage.class, new RenderLogicStorage(blockLogic)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileLogicPointer.class, new RenderLogicPointer(blockLogic)
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(Pre evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            for (int i = 0; i < 232; ++i) {
                logicOne[i] = map.registerIcon("rplogic:logic1/" + i);
            }

            for (int i = 0; i < 256; ++i) {
                logicTwo[i] = map.registerIcon("rplogic:logic2/" + i);
            }

            for (int i = 0; i < 23; ++i) {
                logicSensor[i] = map.registerIcon("rplogic:sensors/" + i);
            }
        }
    }

    @SubscribeEvent
    public void onTextureStitch(Post evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            torch = map.getAtlasSprite("redstone_torch_off");
            torchOn = map.getAtlasSprite("redstone_torch_on");
            lever = map.getAtlasSprite("lever");
            cobblestone = map.getAtlasSprite("cobblestone");
        }
    }
}
