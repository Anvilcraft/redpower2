package com.eloraam.redpower;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.lighting.BlockLamp;
import com.eloraam.redpower.lighting.BlockShapedLamp;
import com.eloraam.redpower.lighting.ItemLamp;
import com.eloraam.redpower.lighting.RenderLamp;
import com.eloraam.redpower.lighting.RenderShapedLamp;
import com.eloraam.redpower.lighting.TileLamp;
import com.eloraam.redpower.lighting.TileShapedLamp;
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;

@Mod(
   modid = "RedPowerLighting",
   name = "RedPower Lighting",
   version = "2.0pr6",
   dependencies = "required-after:RedPowerBase"
)
public class RedPowerLighting {
   @Instance("RedPowerLighting")
   public static RedPowerLighting instance;
   public static BlockLamp blockLamp;
   public static BlockShapedLamp blockShapedLamp;
   public static CreativeTabs tabLamp = new CreativeTabs(CreativeTabs.getNextID(), "RPLights") {
      public ItemStack getIconItemStack() {
         return new ItemStack(RedPowerLighting.blockLamp, 1, 16);
      }

      public Item getTabIconItem() {
         return null;
      }
   };
   public static IIcon[] lampOff;
   public static IIcon[] lampOn;

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      if (FMLCommonHandler.instance().getSide().isClient()) {
         MinecraftForge.EVENT_BUS.register(instance);
      }

   }

   @EventHandler
   public void load(FMLInitializationEvent event) {
      setupLighting();
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.registerRenderers();
      }

   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }

   public static void setupLighting() {
      blockLamp = new BlockLamp();
      blockLamp.setBlockName("rplamp");
      GameRegistry.registerBlock(blockLamp, ItemLamp.class, "lampo");
      GameRegistry.registerTileEntity(TileLamp.class, "RPLamp");
      blockLamp.addTileEntityMapping(0, TileLamp::new);

      for(int color = 0; color < 16; ++color) {
         String nm = "rplamp." + CoreLib.rawColorNames[color];
         blockLamp.setBlockName(color, nm);
         GameRegistry.addRecipe(
            new ItemStack(blockLamp, 1, color),
            new Object[]{"GLG", "GLG", "GRG", 'G', Blocks.glass_pane, 'L', new ItemStack(RedPowerBase.itemLumar, 1, color), 'R', Items.redstone}
         );
      }

      for(int color = 0; color < 16; ++color) {
         String nm = "rpilamp." + CoreLib.rawColorNames[color];
         blockLamp.setBlockName(color + 16, nm);
         GameRegistry.addRecipe(
            new ItemStack(blockLamp, 1, 16 + color),
            new Object[]{"GLG", "GLG", "GRG", 'G', Blocks.glass_pane, 'L', new ItemStack(RedPowerBase.itemLumar, 1, color), 'R', Blocks.redstone_torch}
         );
      }

      blockShapedLamp = new BlockShapedLamp();
      GameRegistry.registerBlock(blockShapedLamp, ItemLamp.class, "shlamp");
      GameRegistry.registerTileEntity(TileShapedLamp.class, "RPShLamp");
      blockShapedLamp.addTileEntityMapping(0, TileShapedLamp::new);

      for(int color = 0; color < 16; ++color) {
         String nm = "rpshlamp." + CoreLib.rawColorNames[color];
         blockShapedLamp.setBlockName(color, nm);
         GameRegistry.addRecipe(
            new ItemStack(blockShapedLamp, 1, color),
            new Object[]{
               "GLG", "GLG", "SRS", 'G', Blocks.glass_pane, 'L', new ItemStack(RedPowerBase.itemLumar, 1, color), 'R', Items.redstone, 'S', Blocks.stone_slab
            }
         );
      }

      for(int color = 0; color < 16; ++color) {
         String nm = "rpishlamp." + CoreLib.rawColorNames[color];
         blockShapedLamp.setBlockName(color + 16, nm);
         GameRegistry.addRecipe(
            new ItemStack(blockShapedLamp, 1, 16 + color),
            new Object[]{
               "GLG",
               "GLG",
               "SRS",
               'G',
               Blocks.glass_pane,
               'L',
               new ItemStack(RedPowerBase.itemLumar, 1, color),
               'R',
               Blocks.redstone_torch,
               'S',
               new ItemStack(Blocks.stone_slab, 1, 0)
            }
         );
      }

      for(int color = 0; color < 16; ++color) {
         String nm = "rpshlamp2." + CoreLib.rawColorNames[color];
         blockShapedLamp.setBlockName(color + 32, nm);
         GameRegistry.addRecipe(
            new ItemStack(blockShapedLamp, 1, 32 + color),
            new Object[]{
               "ILI",
               "GLG",
               "SRS",
               'G',
               Blocks.glass_pane,
               'L',
               new ItemStack(RedPowerBase.itemLumar, 1, color),
               'R',
               Items.redstone,
               'I',
               Blocks.iron_bars,
               'S',
               new ItemStack(Blocks.stone_slab, 1, 0)
            }
         );
      }

      for(int color = 0; color < 16; ++color) {
         String nm = "rpishlamp2." + CoreLib.rawColorNames[color];
         blockShapedLamp.setBlockName(color + 48, nm);
         GameRegistry.addRecipe(
            new ItemStack(blockShapedLamp, 1, 48 + color),
            new Object[]{
               "ILI",
               "GLG",
               "SRS",
               'G',
               Blocks.glass_pane,
               'L',
               new ItemStack(RedPowerBase.itemLumar, 1, color),
               'R',
               Blocks.redstone_torch,
               'I',
               Blocks.iron_bars,
               'S',
               Blocks.stone_slab
            }
         );
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerRenderers() {
      RenderLib.setDefaultRenderer(blockLamp, 10, RenderLamp::new);
      RenderLib.setDefaultRenderer(blockShapedLamp, 10, RenderShapedLamp::new);
      ClientRegistry.bindTileEntitySpecialRenderer(TileLamp.class, new RenderLamp(blockLamp));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShapedLamp.class, new RenderShapedLamp(blockShapedLamp));
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onTextureStitch(Pre evt) {
      TextureMap map = evt.map;
      if (map.getTextureType() == 0) {
         if (lampOff == null) {
            lampOff = new IIcon[16];
         }

         if (lampOn == null) {
            lampOn = new IIcon[16];
         }

         for(int i = 0; i < 16; ++i) {
            lampOff[i] = map.registerIcon("rplighting:lampOff/" + i);
            lampOn[i] = map.registerIcon("rplighting:lampOn/" + i);
         }
      }

   }
}
