package com.eloraam.redpower;

import codechicken.nei.ItemStackSet;
import codechicken.nei.SubsetWidget.SubsetTag;
import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.DefaultOverlayHandler;
import com.eloraam.redpower.base.GuiAdvBench;
import com.eloraam.redpower.base.GuiAlloyFurnace;
import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.nei.AlloyFurnaceOverlayHandler;
import com.eloraam.redpower.nei.AlloyFurnaceRecipeHandler;
import com.eloraam.redpower.nei.MicroRecipeHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(
   modid = "RedPowerNEIPlugin",
   name = "RedPower NEI Plugin",
   version = "1.4.3.1",
   dependencies = "after:NotEnoughItems;after:RedPowerBase;after:RedPowerCompat;after:RedPowerControl;after:RedPowerCore;after:RedPowerLighting;after:RedPowerLogic;after:RedPowerMachine;after:RedPowerWiring;after:RedPowerWorld"
)
public class RedPowerNEIPlugin {
   @Instance("RedPowerNEIPlugin")
   public static RedPowerNEIPlugin instance;
   public static boolean wiring;
   public static boolean logic;
   public static boolean control;
   public static boolean lighting;
   public static boolean world;
   public static boolean machine;
   public static boolean base;
   public static boolean compat;
   static Block micro;
   private List<RedPowerNEIPlugin.ItemRange> validMicroTypes = new ArrayList();

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
   }

   @EventHandler
   public void load(FMLInitializationEvent event) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      if (FMLCommonHandler.instance().getSide().isServer()) {
         FMLLog.severe("[RedPowerNEIPlugin] Server env detected, disabling...", new Object[0]);
      } else {
         if (Loader.isModLoaded("NotEnoughItems")) {
            wiring = Loader.isModLoaded("RedPowerWiring");
            logic = Loader.isModLoaded("RedPowerLogic");
            control = Loader.isModLoaded("RedPowerControl");
            lighting = Loader.isModLoaded("RedPowerLighting");
            world = Loader.isModLoaded("RedPowerWorld");
            machine = Loader.isModLoaded("RedPowerMachine");
            base = Loader.isModLoaded("RedPowerBase");
            compat = Loader.isModLoaded("RedPowerCompat");
            if (base) {
               this.loadCoverSubSets();
               this.loadSaws();
               API.registerGuiOverlay(GuiAlloyFurnace.class, "alloy");
               API.registerGuiOverlay(GuiAdvBench.class, "crafting", 23, 12);
               API.registerGuiOverlayHandler(GuiAlloyFurnace.class, new AlloyFurnaceOverlayHandler(), "alloy");
               API.registerGuiOverlayHandler(GuiAdvBench.class, new DefaultOverlayHandler(23, 12), "crafting");
               API.hideItem(new ItemStack(RedPowerBase.blockMultiblock));
               API.registerRecipeHandler(new AlloyFurnaceRecipeHandler());
               API.registerUsageHandler(new AlloyFurnaceRecipeHandler());
               API.registerRecipeHandler(new MicroRecipeHandler());
               API.registerUsageHandler(new MicroRecipeHandler());
            }
         } else {
            FMLCommonHandler.instance().getFMLLogger().warn("[RedPowerNEIPlugin] No NEI detected, disabling...");
         }

      }
   }

   private void loadSaws() {
      List<ItemHandsaw> saws = new ArrayList();

      for(Object item : Item.itemRegistry) {
         if (item instanceof ItemHandsaw) {
            saws.add((ItemHandsaw)item);
         }
      }

      MicroRecipeHandler.saws = new ItemHandsaw[saws.size()];

      for(int i = 0; i < saws.size(); ++i) {
         MicroRecipeHandler.saws[i] = (ItemHandsaw)saws.get(i);
      }

      ItemStackSet set = new ItemStackSet().with(MicroRecipeHandler.saws);
      API.addSubset(new SubsetTag("RedPower.Tools.Saws", set));
      API.addSubset(new SubsetTag("Items.Tools.Saws", set));
   }

   private void loadCoverSubSets() {
      if (base) {
         micro = RedPowerBase.blockMicro;
         int startRange = -1;

         for(int i = 0; i < 256; ++i) {
            ItemStack stack = new ItemStack(micro, 1, i);
            String name = GuiContainerManager.itemDisplayNameShort(stack);
            if (!name.endsWith("Unnamed") && !name.endsWith("null")) {
               if (startRange == -1) {
                  startRange = i;
               }
            } else if (startRange != -1) {
               this.validMicroTypes.add(new RedPowerNEIPlugin.ItemRange(micro, startRange, i - 1));
               startRange = -1;
            }
         }

         this.registerMicroSet("MicroBlocks.Cover", 0);
         this.registerMicroSet("MicroBlocks.Panel", 16);
         this.registerMicroSet("MicroBlocks.Slab", 17);
         this.registerMicroSet("MicroBlocks.Hollow Cover", 24);
         this.registerMicroSet("MicroBlocks.Hollow Panel", 25);
         this.registerMicroSet("MicroBlocks.Hollow Slab", 26);
         this.registerMicroSet("MicroBlocks.Cover Corner", 18);
         this.registerMicroSet("MicroBlocks.Panel Corner", 19);
         this.registerMicroSet("MicroBlocks.Slab Corner", 20);
         this.registerMicroSet("MicroBlocks.Cover Strip", 21);
         this.registerMicroSet("MicroBlocks.Panel Strip", 22);
         this.registerMicroSet("MicroBlocks.Slab Strip", 23);
         this.registerMicroSet("MicroBlocks.Triple Cover", 27);
         this.registerMicroSet("MicroBlocks.Cover Slab", 28);
         this.registerMicroSet("MicroBlocks.Triple Panel", 29);
         this.registerMicroSet("MicroBlocks.Anticover", 30);
         this.registerMicroSet("MicroBlocks.Hollow Triple Cover", 31);
         this.registerMicroSet("MicroBlocks.Hollow Cover Slab", 32);
         this.registerMicroSet("MicroBlocks.Hollow Triple Panel", 33);
         this.registerMicroSet("MicroBlocks.Hollow Anticover", 34);
         this.registerMicroSet("MicroBlocks.Triple Cover Corner", 35);
         this.registerMicroSet("MicroBlocks.Cover Slab Corner", 36);
         this.registerMicroSet("MicroBlocks.Triple Panel Corner", 37);
         this.registerMicroSet("MicroBlocks.Anticover Corner", 38);
         this.registerMicroSet("MicroBlocks.Triple Cover Strip", 39);
         this.registerMicroSet("MicroBlocks.Cover Slab Strip", 40);
         this.registerMicroSet("MicroBlocks.Triple Panel Strip", 41);
         this.registerMicroSet("MicroBlocks.Anticover Strip", 42);
         this.registerMicroSet("MicroBlocks.Post", 43);
         this.registerMicroSet("MicroBlocks.Pillar", 44);
         this.registerMicroSet("MicroBlocks.Column", 45);
         if (wiring) {
            this.registerMicroSet("Wiring.Jacketed Wire", 64);
            this.registerMicroSet("Wiring.Jacketed Cable", 65);
            this.registerMicroSet("Bluetricity.Jacketed Bluewire", 66);
         }
      }

   }

   private void registerMicroSet(String RPName, int microID) {
      ItemStackSet set = new ItemStackSet();

      for(RedPowerNEIPlugin.ItemRange type : this.validMicroTypes) {
         set.with(
            (ItemStack[])IntStream.rangeClosed(type.start, type.end)
               .mapToObj(i -> new ItemStack(type.bl, 1, i + microID * 256))
               .toArray(x$0 -> new ItemStack[x$0])
         );
      }

      API.addSubset("RedPower." + RPName, set);
   }

   private class ItemRange {
      private final Block bl;
      private final int start;
      private final int end;

      public ItemRange(Block bl, int start, int end) {
         this.bl = bl;
         this.start = start;
         this.end = end;
      }
   }
}
