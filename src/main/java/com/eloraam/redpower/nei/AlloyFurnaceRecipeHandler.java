package com.eloraam.redpower.nei;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.FurnaceRecipeHandler.FuelPair;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;
import com.eloraam.redpower.base.GuiAlloyFurnace;
import com.eloraam.redpower.core.CraftLib;
import com.eloraam.redpower.core.OreStack;
import cpw.mods.fml.common.FMLCommonHandler;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class AlloyFurnaceRecipeHandler extends FurnaceRecipeHandler implements ICraftingHandler, IUsageHandler {
   public void loadTransferRects() {
      this.transferRects.add(new RecipeTransferRect(new Rectangle(11, 12, 18, 18), "fuel", new Object[0]));
      this.transferRects.add(new RecipeTransferRect(new Rectangle(102, 23, 24, 18), "alloy", new Object[0]));
   }

   public Class<GuiAlloyFurnace> getGuiClass() {
      return GuiAlloyFurnace.class;
   }

   public void loadCraftingRecipes(String outputId, Object... results) {
      if (outputId.equals("alloy") && this.getClass() == AlloyFurnaceRecipeHandler.class) {
         for(List<Object> lrecipe : CraftLib.alloyRecipes) {
            this.arecipes.add(new AlloyFurnaceRecipeHandler.CachedAlloyRecipe(lrecipe));
         }
      } else {
         super.loadCraftingRecipes(outputId, results);
      }

   }

   public void loadCraftingRecipes(ItemStack result) {
      for(List<Object> lrecipe : CraftLib.alloyRecipes) {
         if (NEIServerUtils.areStacksSameTypeCrafting((ItemStack)lrecipe.get(1), result)) {
            this.arecipes.add(new AlloyFurnaceRecipeHandler.CachedAlloyRecipe(lrecipe));
         }
      }

   }

   public void loadUsageRecipes(String inputId, Object... ingredients) {
      if (inputId.equals("fuel") && this.getClass() == AlloyFurnaceRecipeHandler.class) {
         this.loadCraftingRecipes("alloy");
      } else {
         super.loadUsageRecipes(inputId, ingredients);
      }

   }

   public void loadUsageRecipes(ItemStack ingredient) {
      for(List<Object> lrecipe : CraftLib.alloyRecipes) {
         AlloyFurnaceRecipeHandler.CachedAlloyRecipe recipe = new AlloyFurnaceRecipeHandler.CachedAlloyRecipe(lrecipe);
         if (recipe.contains(recipe.ingredients, ingredient)) {
            this.arecipes.add(recipe);
         }
      }

   }

   public String getRecipeName() {
      return I18n.format("tile.rpafurnace.name", new Object[0]);
   }

   public String getGuiTexture() {
      return "rpbase:textures/gui/afurnacegui.png";
   }

   public void drawExtras(int recipe) {
      this.drawProgressBar(12, 14, 176, 0, 14, 14, 48, 7);
      this.drawProgressBar(102, 23, 176, 14, 24, 16, 48, 0);
   }

   public String getOverlayIdentifier() {
      return "alloy";
   }

   public class AlloyDupeComparator implements Comparator<List<Object>> {
      public int compare(List<Object> o1, List<Object> o2) {
         ItemStack result1 = (ItemStack)o1.get(1);
         ItemStack result2 = (ItemStack)o2.get(1);
         int resultcompare = NEIServerUtils.compareStacks(result1, result2);
         if (resultcompare != 0) {
            return resultcompare;
         } else {
            ItemStack[] ingreds1 = (ItemStack[])o1.get(0);
            ItemStack[] ingreds2 = (ItemStack[])o2.get(0);
            int lengthcompare = Integer.valueOf(ingreds1.length).compareTo(ingreds2.length);
            if (lengthcompare != 0) {
               return lengthcompare;
            } else {
               for(int i = 0; i < ingreds1.length; ++i) {
                  int ingredcompare = NEIServerUtils.compareStacks(ingreds1[i], ingreds2[i]);
                  if (ingredcompare != 0) {
                     return ingredcompare;
                  }
               }

               return 0;
            }
         }
      }
   }

   public class CachedAlloyRecipe extends CachedRecipe {
      List<PositionedStack> ingredients = new ArrayList();
      ItemStack result;

      public CachedAlloyRecipe(Object[] ingreds, ItemStack result) {
         super();

         for(int i = 0; i < ingreds.length; ++i) {
            Object ingred = null;
            if (!(ingreds[i] instanceof OreStack)) {
               if (ingreds[i] instanceof ItemStack) {
                  ingred = ingreds[i];
               } else {
                  FMLCommonHandler.instance().raiseException(new ClassCastException("not an ItemStack or OreStack"), "NEI", false);
               }
            } else {
               OreStack ore = (OreStack)ingreds[i];
               List<ItemStack> list = new ArrayList(OreDictionary.getOres(ore.material));

               for(int j = 0; j < list.size(); ++j) {
                  list.set(j, InventoryUtils.copyStack((ItemStack)list.get(j), ore.quantity));
               }

               ingred = list;
            }

            this.ingredients.add(new PositionedStack(ingred, 43 + i * 18, 6 + i / 3 * 18));
         }

         this.result = result;
      }

      public CachedAlloyRecipe(List<Object> lrecipe) {
         this((Object[])lrecipe.get(0), (ItemStack)lrecipe.get(1));
      }

      public PositionedStack getResult() {
         return new PositionedStack(this.result, 136, 24);
      }

      public List<PositionedStack> getIngredients() {
         return this.ingredients;
      }

      public List<PositionedStack> getOtherStacks() {
         List<PositionedStack> slots = new ArrayList();
         slots.add(
            new PositionedStack(
               ((FuelPair)AlloyFurnaceRecipeHandler.afuels.get(AlloyFurnaceRecipeHandler.this.cycleticks / 48 % AlloyFurnaceRecipeHandler.afuels.size())).stack.item,
               12,
               31
            )
         );
         return slots;
      }
   }
}
