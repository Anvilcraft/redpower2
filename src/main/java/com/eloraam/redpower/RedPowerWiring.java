package com.eloraam.redpower;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.CoverLib;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.wiring.MicroPlacementJacket;
import com.eloraam.redpower.wiring.MicroPlacementWire;
import com.eloraam.redpower.wiring.RenderRedwire;
import com.eloraam.redpower.wiring.TileBluewire;
import com.eloraam.redpower.wiring.TileCable;
import com.eloraam.redpower.wiring.TileInsulatedWire;
import com.eloraam.redpower.wiring.TileRedwire;
import com.eloraam.redpower.wiring.TileWiring;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = "RedPowerWiring",
    name = "RedPower Wiring",
    version = "2.0pr6",
    dependencies = "required-after:RedPowerBase"
)
public class RedPowerWiring {
    @Instance("RedPowerWiring")
    public static RedPowerWiring instance;
    @SideOnly(Side.CLIENT)
    public static IIcon redwireTop;
    @SideOnly(Side.CLIENT)
    public static IIcon redwireFace;
    @SideOnly(Side.CLIENT)
    public static IIcon bundledTop;
    @SideOnly(Side.CLIENT)
    public static IIcon bundledFace;
    @SideOnly(Side.CLIENT)
    public static IIcon powerTop;
    @SideOnly(Side.CLIENT)
    public static IIcon powerFace;
    @SideOnly(Side.CLIENT)
    public static IIcon highPowerTop;
    @SideOnly(Side.CLIENT)
    public static IIcon highPowerFace;
    @SideOnly(Side.CLIENT)
    public static IIcon jumboSides;
    @SideOnly(Side.CLIENT)
    public static IIcon jumboTop;
    @SideOnly(Side.CLIENT)
    public static IIcon jumboCent;
    @SideOnly(Side.CLIENT)
    public static IIcon jumboCentSide;
    @SideOnly(Side.CLIENT)
    public static IIcon jumboEnd;
    @SideOnly(Side.CLIENT)
    public static IIcon jumboCorners;
    @SideOnly(Side.CLIENT)
    public static IIcon redwireCableOff;
    @SideOnly(Side.CLIENT)
    public static IIcon redwireCableOn;
    @SideOnly(Side.CLIENT)
    public static IIcon bluewireCable;
    @SideOnly(Side.CLIENT)
    public static IIcon bundledCable;
    public static IIcon[] insulatedTop = new IIcon[16];
    public static IIcon[] insulatedFaceOff = new IIcon[16];
    public static IIcon[] insulatedFaceOn = new IIcon[16];
    public static IIcon[] bundledColTop = new IIcon[16];
    public static IIcon[] bundledColFace = new IIcon[16];

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        initJacketRecipes();
        setupWires();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.registerRenderers();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    private static void initJacketRecipes() {
        CoverLib.addMaterialHandler(material -> {
            if (!CoverLib.isTransparent(material)) {
                GameRegistry.addRecipe(
                    new ItemStack(RedPowerBase.blockMicro, 4, 16384 + material),
                    new Object[] { "SSS",
                                   "SRS",
                                   "SSS",
                                   'S',
                                   new ItemStack(RedPowerBase.blockMicro, 1, material),
                                   'R',
                                   RedPowerBase.itemIngotRed }
                );
                GameRegistry.addRecipe(
                    new ItemStack(RedPowerBase.blockMicro, 1, 16640 + material),
                    new Object[] { "SSS",
                                   "SCS",
                                   "SSS",
                                   'S',
                                   new ItemStack(RedPowerBase.blockMicro, 1, material),
                                   'C',
                                   new ItemStack(RedPowerBase.blockMicro, 1, 768) }
                );
                GameRegistry.addRecipe(
                    new ItemStack(RedPowerBase.blockMicro, 4, 16896 + material),
                    new Object[] { "SSS",
                                   "SBS",
                                   "SSS",
                                   'S',
                                   new ItemStack(RedPowerBase.blockMicro, 1, material),
                                   'B',
                                   RedPowerBase.itemIngotBlue }
                );
                CraftLib.addAlloyResult(
                    CoreLib.copyStack(RedPowerBase.itemIngotRed, 1),
                    new ItemStack(RedPowerBase.blockMicro, 4, 16384 + material)
                );
                CraftLib.addAlloyResult(
                    CoreLib.copyStack(RedPowerBase.itemIngotRed, 5),
                    new ItemStack(RedPowerBase.blockMicro, 8, 16640 + material)
                );
                CraftLib.addAlloyResult(
                    CoreLib.copyStack(RedPowerBase.itemIngotBlue, 1),
                    new ItemStack(RedPowerBase.blockMicro, 4, 16896 + material)
                );
            }
        });
    }

    public static void setupWires() {
        GameRegistry.registerTileEntity(TileRedwire.class, "Redwire");
        GameRegistry.registerTileEntity(TileInsulatedWire.class, "InsRedwire");
        GameRegistry.registerTileEntity(TileCable.class, "RedCable");
        GameRegistry.registerTileEntity(TileCovered.class, "Covers");
        GameRegistry.registerTileEntity(TileBluewire.class, "Bluewire");
        MicroPlacementWire wre = new MicroPlacementWire();
        RedPowerBase.blockMicro.registerPlacement(1, wre);
        RedPowerBase.blockMicro.registerPlacement(2, wre);
        RedPowerBase.blockMicro.registerPlacement(3, wre);
        RedPowerBase.blockMicro.registerPlacement(5, wre);
        MicroPlacementJacket jkt = new MicroPlacementJacket();
        RedPowerBase.blockMicro.registerPlacement(64, jkt);
        RedPowerBase.blockMicro.registerPlacement(65, jkt);
        RedPowerBase.blockMicro.registerPlacement(66, jkt);
        RedPowerBase.blockMicro.addTileEntityMapping(1, TileRedwire::new);
        RedPowerBase.blockMicro.addTileEntityMapping(2, TileInsulatedWire::new);
        RedPowerBase.blockMicro.addTileEntityMapping(3, TileCable::new);
        RedPowerBase.blockMicro.addTileEntityMapping(5, TileBluewire::new);
        GameRegistry.addRecipe(
            new ItemStack(RedPowerBase.blockMicro, 12, 256),
            new Object[] { "R", "R", "R", 'R', RedPowerBase.itemIngotRed }
        );
        CraftLib.addAlloyResult(
            RedPowerBase.itemIngotRed, new ItemStack(RedPowerBase.blockMicro, 4, 256)
        );
        CraftLib.addAlloyResult(
            CoreLib.copyStack(RedPowerBase.itemIngotRed, 5),
            new ItemStack(RedPowerBase.blockMicro, 8, 768)
        );
        GameRegistry.addRecipe(
            new ItemStack(RedPowerBase.blockMicro, 12, 1280),
            new Object[] {
                "WBW", "WBW", "WBW", 'B', RedPowerBase.itemIngotBlue, 'W', Blocks.wool }
        );
        CraftLib.addAlloyResult(
            RedPowerBase.itemIngotBlue, new ItemStack(RedPowerBase.blockMicro, 4, 1280)
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(RedPowerBase.blockMicro, 1, 1281),
            new Object[] { new ItemStack(RedPowerBase.blockMicro, 1, 1280), Blocks.wool }
        );
        CraftLib.addAlloyResult(
            RedPowerBase.itemIngotBlue, new ItemStack(RedPowerBase.blockMicro, 4, 1281)
        );

        for (int color = 0; color < 16; ++color) {
            GameRegistry.addRecipe(
                new ItemStack(RedPowerBase.blockMicro, 12, 512 + color),
                new Object[] { "WRW",
                               "WRW",
                               "WRW",
                               'R',
                               RedPowerBase.itemIngotRed,
                               'W',
                               new ItemStack(Blocks.wool, 1, color) }
            );

            for (int j = 0; j < 16; ++j) {
                if (color != j) {
                    GameRegistry.addShapelessRecipe(
                        new ItemStack(RedPowerBase.blockMicro, 1, 512 + color),
                        new Object[] { new ItemStack(RedPowerBase.blockMicro, 1, 512 + j),
                                       new ItemStack(Items.dye, 1, 15 - color) }
                    );
                    GameRegistry.addShapelessRecipe(
                        new ItemStack(RedPowerBase.blockMicro, 1, 769 + color),
                        new Object[] { new ItemStack(RedPowerBase.blockMicro, 1, 769 + j),
                                       new ItemStack(Items.dye, 1, 15 - color) }
                    );
                }
            }

            CraftLib.addAlloyResult(
                RedPowerBase.itemIngotRed,
                new ItemStack(RedPowerBase.blockMicro, 4, 512 + color)
            );
            GameRegistry.addRecipe(
                new ItemStack(RedPowerBase.blockMicro, 2, 768),
                new Object[] { "SWS",
                               "WWW",
                               "SWS",
                               'W',
                               new ItemStack(RedPowerBase.blockMicro, 1, 512 + color),
                               'S',
                               Items.string }
            );
            GameRegistry.addShapelessRecipe(
                new ItemStack(RedPowerBase.blockMicro, 1, 769 + color),
                new Object[] { new ItemStack(RedPowerBase.blockMicro, 1, 768),
                               new ItemStack(Items.dye, 1, 15 - color),
                               Items.paper }
            );
            CraftLib.addAlloyResult(
                CoreLib.copyStack(RedPowerBase.itemIngotRed, 5),
                new ItemStack(RedPowerBase.blockMicro, 8, 769 + color)
            );
        }

        for (int i = 0; i < 16; ++i) {
            if (i != 11) {
                CraftLib.addShapelessOreRecipe(
                    new ItemStack(RedPowerBase.blockMicro, 1, 523),
                    new ItemStack(RedPowerBase.blockMicro, 1, 512 + i),
                    "dyeBlue"
                );
                CraftLib.addShapelessOreRecipe(
                    new ItemStack(RedPowerBase.blockMicro, 1, 780),
                    new ItemStack(RedPowerBase.blockMicro, 1, 769 + i),
                    "dyeBlue"
                );
            }
        }

        CraftLib.addShapelessOreRecipe(
            new ItemStack(RedPowerBase.blockMicro, 1, 780),
            new ItemStack(RedPowerBase.blockMicro, 1, 768),
            "dyeBlue",
            Items.paper
        );
        RedPowerLib.addCompatibleMapping(0, 1);

        for (int i = 0; i < 16; ++i) {
            RedPowerLib.addCompatibleMapping(0, 2 + i);
            RedPowerLib.addCompatibleMapping(1, 2 + i);
            RedPowerLib.addCompatibleMapping(65, 2 + i);

            for (int j = 0; j < 16; ++j) {
                RedPowerLib.addCompatibleMapping(19 + j, 2 + i);
            }

            RedPowerLib.addCompatibleMapping(18, 2 + i);
            RedPowerLib.addCompatibleMapping(18, 19 + i);
        }

        RedPowerLib.addCompatibleMapping(0, 65);
        RedPowerLib.addCompatibleMapping(1, 65);
        RedPowerLib.addCompatibleMapping(64, 65);
        RedPowerLib.addCompatibleMapping(64, 67);
        RedPowerLib.addCompatibleMapping(65, 67);
        RedPowerLib.addCompatibleMapping(66, 67);
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        RenderLib.setDefaultRenderer(RedPowerBase.blockMicro, 8, RenderRedwire::new);
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileWiring.class, new RenderRedwire(RedPowerBase.blockMicro)
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(Pre evt) {
        TextureMap map = evt.map;
        if (map.getTextureType() == 0) {
            redwireTop = map.registerIcon("rpwiring:redwireTop");
            redwireFace = map.registerIcon("rpwiring:redwireFace");
            bundledTop = map.registerIcon("rpwiring:bundledTop");
            bundledFace = map.registerIcon("rpwiring:bundledFace");
            powerTop = map.registerIcon("rpwiring:powerTop");
            powerFace = map.registerIcon("rpwiring:powerFace");
            highPowerTop = map.registerIcon("rpwiring:highPowerTop");
            highPowerFace = map.registerIcon("rpwiring:highPowerFace");
            jumboSides = map.registerIcon("rpwiring:jumboSides");
            jumboTop = map.registerIcon("rpwiring:jumboTop");
            jumboCent = map.registerIcon("rpwiring:jumboCent");
            jumboCentSide = map.registerIcon("rpwiring:jumboCentSide");
            jumboEnd = map.registerIcon("rpwiring:jumboEnd");
            jumboCorners = map.registerIcon("rpwiring:jumboCorners");
            redwireCableOff = map.registerIcon("rpwiring:redwireCableOff");
            redwireCableOn = map.registerIcon("rpwiring:redwireCableOn");
            bluewireCable = map.registerIcon("rpwiring:bluewireCable");
            bundledCable = map.registerIcon("rpwiring:bundledCable");

            for (int col = 0; col < 16; ++col) {
                insulatedTop[col] = map.registerIcon("rpwiring:insulatedTop/" + col);
                insulatedFaceOff[col]
                    = map.registerIcon("rpwiring:insulatedFaceOff/" + col);
                insulatedFaceOn[col]
                    = map.registerIcon("rpwiring:insulatedFaceOn/" + col);
                bundledColTop[col] = map.registerIcon("rpwiring:bundledColTop/" + col);
                bundledColFace[col] = map.registerIcon("rpwiring:bundledColFace/" + col);
            }
        }
    }
}
