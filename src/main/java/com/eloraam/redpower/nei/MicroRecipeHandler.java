package com.eloraam.redpower.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;
import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.base.ItemHandsaw;
import com.eloraam.redpower.core.CoverLib;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MicroRecipeHandler extends ShapedRecipeHandler {
   public static int[] covers = new int[]{0, 16, 27, 17, 28, 29, 30};
   public static int[] strips = new int[]{21, 22, 39, 23, 40, 41, 42};
   public static int[] corners = new int[]{18, 19, 35, 20, 36, 37, 38};
   public static int[] posts = new int[]{-1, 43, -1, 44, -1, 45, -1};
   public static int[] hollow = new int[]{24, 25, 31, 26, 32, 33, 34};
   public static ItemHandsaw[] saws;
   public static int[] materials;
   public static Random rand = new Random();

   public MicroRecipeHandler() {
      load();
   }

   public static void load() {
      if (materials == null) {
         List<Integer> amaterial = new ArrayList();

         for(int i = 0; i < 256; ++i) {
            if (CoverLib.getItemStack(i) != null) {
               amaterial.add(i);
            }
         }

         materials = new int[amaterial.size()];

         for(int i = 0; i < amaterial.size(); ++i) {
            materials[i] = amaterial.get(i);
         }
      }

   }

   public static PositionedStack position(ItemStack item, int row, int col) {
      return new PositionedStack(item, 25 + col * 18, 6 + row * 18);
   }

   public String getRecipeName() {
      return "Microblocks";
   }

   public void loadCraftingRecipes(ItemStack ingred) {
      ingred = ingred.copy();
      ingred.stackSize = 1;
      if (CoverLib.getMaterial(ingred) != null) {
         this.arecipes.add(new MicroRecipeHandler.GluingRecipe(covers, ingred, -1));
         this.arecipes.add(new MicroRecipeHandler.GluingRecipe(hollow, ingred, -1));
      } else if (ingred.getItem() == ItemHandsaw.getItemFromBlock(RedPowerBase.blockMicro) && isValidMicroType(ingred.getItemDamage() >> 8)) {
         int type = ingred.getItemDamage() >> 8;
         int material = ingred.getItemDamage() & 0xFF;
         this.addCuttingRecipe(type, material);
         this.addGluingRecipe(type, material);
         this.addPostRecipe(type, material);
      }

   }

   private void addPostRecipe(int type, int material) {
      int thickness = getThickness(type);
      int[] microclass = getMicroClass(type);
      if (thickness % 2 == 0 && (microclass == posts || microclass == strips)) {
         this.arecipes.add(new MicroRecipeHandler.PostRecipe(getMicro(type, material, 1)));
      }

   }

   private void addGluingRecipe(int type, int material) {
      int thickness = getThickness(type);
      int[] microclass = getMicroClass(type);
      if ((microclass == covers || microclass == hollow) && thickness > 1) {
         this.arecipes.add(new MicroRecipeHandler.GluingRecipe(getMicroClass(type), getMicro(type, material, 1), -1));
      }

      if (thicknessPow2(thickness) && (microclass == covers || microclass == strips)) {
         int[] subclass = getNextMicroClass(getMicroClass(type), false);
         if (microclass == covers) {
            this.arecipes
               .add(new MicroRecipeHandler.GluingRecipe(getMicro(getNextMicroClass(subclass, false)[thickness - 1], material, 4), getMicro(type, material, 1)));
         }

         this.arecipes.add(new MicroRecipeHandler.GluingRecipe(getMicro(subclass[thickness - 1], material, 2), getMicro(type, material, 1)));
      }

   }

   private void addCuttingRecipe(int type, int material) {
      int thickness = getThickness(type);
      int[] microclass = getMicroClass(type);
      if (microclass != covers && microclass != hollow) {
         if (microclass == strips || microclass == corners) {
            this.arecipes
               .add(
                  new MicroRecipeHandler.CuttingRecipe(
                     getMicro(type, material, 2), getMicro(getNextMicroClass(microclass, true)[thickness - 1], material, 1), null
                  )
               );
         }
      } else if (thickness <= 3 || microclass == covers && thickness == 4) {
         this.arecipes.add(new MicroRecipeHandler.CuttingRecipe(getMicro(type, material, 2), getMicro(setThickness(type, thickness * 2), material, 1), null));
      }

   }

   public void loadUsageRecipes(ItemStack result) {
      result = result.copy();
      result.stackSize = 1;
      if (CoverLib.getMaterial(result) != null) {
         this.arecipes.add(new MicroRecipeHandler.CuttingRecipe(getMicro(covers[3], getMaterial(result), 2), result, null));
      } else if (result.getItem() instanceof ItemHandsaw) {
         this.addSawUsage(result);
      } else if (result.getItem() == Item.getItemFromBlock(RedPowerBase.blockMicro) && isValidMicroType(result.getItemDamage() >> 8)) {
         int type = result.getItemDamage() >> 8;
         int material = result.getItemDamage() & 0xFF;
         this.addCuttingUsage(type, material);
         this.addGluingUsage(type, material);
         this.addPostUsage(type, material);
      }

   }

   private void addSawSplitting(int[] microclass, int thicknesses, ItemStack handsaw) {
      for(int i = thicknesses; i >= 0; --i) {
         this.arecipes.add(new MicroRecipeHandler.CuttingRecipe(setThickness(microclass[i], (i + 1) * 2), microclass[i], handsaw));
      }

   }

   private void addSawCutting(int[] microclass, ItemStack handsaw) {
      int[] superclass = getNextMicroClass(microclass, true);

      for(int i = 6; i >= 0; --i) {
         this.arecipes.add(new MicroRecipeHandler.CuttingRecipe(superclass[i], microclass[i], handsaw));
      }

   }

   private void addPostUsage(int type, int material) {
      int thickness = getThickness(type);
      int[] microclass = getMicroClass(type);
      if (thickness % 2 == 0 && (microclass == posts || microclass == strips)) {
         this.arecipes.add(new MicroRecipeHandler.PostRecipe(getMicro(swapPostType(type), material, 1)));
      }

   }

   private void addGluingUsage(int type, int material) {
      int thickness = getThickness(type);
      int[] microclass = getMicroClass(type);
      if (thicknessPow2(thickness) && (microclass == corners || microclass == strips)) {
         int[] superclass = getNextMicroClass(microclass, true);
         if (microclass == corners) {
            this.arecipes
               .add(new MicroRecipeHandler.GluingRecipe(getMicro(type, material, 4), getMicro(getNextMicroClass(superclass, true)[thickness - 1], material, 1)));
         }

         this.arecipes.add(new MicroRecipeHandler.GluingRecipe(getMicro(type, material, 2), getMicro(superclass[thickness - 1], material, 1)));
      }

      if (microclass == covers || microclass == hollow) {
         for(int i = thickness + 1; i <= 8; ++i) {
            this.arecipes.add(new MicroRecipeHandler.GluingRecipe(microclass, getMicro(setThickness(type, i), material, 1), thickness));
         }
      }

   }

   private void addCuttingUsage(int type, int material) {
      int thickness = getThickness(type);
      int[] microclass = getMicroClass(type);
      if (thickness % 2 == 0 && (microclass == covers || microclass == hollow)) {
         this.arecipes
            .add(new MicroRecipeHandler.CuttingRecipe(getMicro(setThickness(type, getThickness(type) / 2), material, 2), getMicro(type, material, 1), null));
      }

      if (microclass == covers || microclass == strips) {
         this.arecipes
            .add(
               new MicroRecipeHandler.CuttingRecipe(
                  getMicro(getNextMicroClass(microclass, false)[getThickness(type) - 1], material, 2), getMicro(type, material, 1), null
               )
            );
      }

   }

   private void addSawUsage(ItemStack ingredient) {
      this.addSawSplitting(covers, 3, ingredient);
      this.addSawSplitting(hollow, 2, ingredient);
      this.addSawCutting(strips, ingredient);
      this.addSawCutting(corners, ingredient);
   }

   public static int swapPostType(int type) {
      return containsInt(posts, type) ? strips[getThickness(type) - 1] : posts[getThickness(type) - 1];
   }

   public static boolean isValidMicroType(int type) {
      return type == 0 || type >= 16 && type <= 45;
   }

   public static int[] getNextMicroClass(int[] microclass, boolean higher) {
      if (higher) {
         if (microclass == corners) {
            return strips;
         }

         if (microclass == strips) {
            return covers;
         }
      } else {
         if (microclass == strips) {
            return corners;
         }

         if (microclass == covers) {
            return strips;
         }
      }

      return null;
   }

   public static int getMaterial(ItemStack stack) {
      return stack.getItem() == Item.getItemFromBlock(CoverLib.blockCoverPlate) ? stack.getItemDamage() & 0xFF : CoverLib.getMaterial(stack);
   }

   public static int getThickness(int type) {
      return type == -1 ? 8 : getIndex(getMicroClass(type), type) + 1;
   }

   public static int[] getMicroClass(int type) {
      return containsInt(covers, type)
         ? covers
         : (containsInt(strips, type) ? strips : (containsInt(corners, type) ? corners : (containsInt(hollow, type) ? hollow : posts)));
   }

   public static int setThickness(int type, int thickness) {
      return thickness == 8 ? -1 : getMicroClass(type)[thickness - 1];
   }

   public static ItemStack getMicro(int type, int material, int quantity) {
      if (type == -1) {
         ItemStack stack = CoverLib.getItemStack(material).copy();
         stack.stackSize = quantity;
         return stack;
      } else {
         return new ItemStack(CoverLib.blockCoverPlate, quantity, type << 8 | material);
      }
   }

   public static int getType(ItemStack stack) {
      return stack.getItem() == Item.getItemFromBlock(CoverLib.blockCoverPlate) ? stack.getItemDamage() >> 8 : -1;
   }

   public static boolean thicknessPow2(int thickness) {
      return thickness == 1 || thickness == 2 || thickness == 4;
   }

   public static boolean containsInt(int[] array, int i) {
      return getIndex(array, i) != -1;
   }

   public static int getIndex(int[] arr, int i) {
      for(int j = 0; j < arr.length; ++j) {
         if (arr[j] == i) {
            return j;
         }
      }

      return -1;
   }

   public class CuttingRecipe extends CachedRecipe {
      ItemStack saw;
      ItemStack ingred;
      ItemStack result;
      int cycletype;
      List<Integer> cyclemap = new ArrayList<>();

      public CuttingRecipe(ItemStack result, ItemStack ingred, ItemStack saw) {
         super();
         this.result = result;
         this.ingred = ingred;
         this.saw = saw;
         this.cycletype = 0;
         this.mapSharpSaws();
      }

      public CuttingRecipe(int typeingred, int typeresult, ItemStack saw) {
         super();
         this.result = MicroRecipeHandler.getMicro(typeingred, 0, 1);
         this.ingred = MicroRecipeHandler.getMicro(typeresult, 0, 2);
         this.saw = saw;
         this.cycletype = 1;
         this.mapSoftMaterials();
      }

      public void mapSharpSaws() {
         for(int i = 0; i < MicroRecipeHandler.saws.length; ++i) {
            if (MicroRecipeHandler.saws[i].getSharpness() >= CoverLib.getHardness(MicroRecipeHandler.getMaterial(this.ingred))) {
               this.cyclemap.add(i);
            }
         }

      }

      public void mapSoftMaterials() {
         for(int i = 0; i < MicroRecipeHandler.materials.length; ++i) {
            if (((ItemHandsaw)this.saw.getItem()).getSharpness() >= CoverLib.getHardness(MicroRecipeHandler.materials[i])) {
               this.cyclemap.add(i);
            }
         }

      }

      public List<PositionedStack> getIngredients() {
         int index = this.cyclemap.get(MicroRecipeHandler.this.cycleticks / 20 % this.cyclemap.size());
         if (this.cycletype == 0) {
            this.saw = new ItemStack(MicroRecipeHandler.saws[index]);
         } else {
            this.ingred = MicroRecipeHandler.getMicro(MicroRecipeHandler.getType(this.ingred), MicroRecipeHandler.materials[index], 1);
         }

         List<PositionedStack> ingreds = new ArrayList();
         int type = this.result.getItemDamage() >> 8;
         if (!MicroRecipeHandler.containsInt(MicroRecipeHandler.covers, type) && !MicroRecipeHandler.containsInt(MicroRecipeHandler.hollow, type)) {
            ingreds.add(MicroRecipeHandler.position(this.saw, 1, 0));
         } else {
            ingreds.add(MicroRecipeHandler.position(this.saw, 0, 1));
         }

         ingreds.add(MicroRecipeHandler.position(this.ingred, 1, 1));
         return ingreds;
      }

      public PositionedStack getResult() {
         int index = this.cyclemap.get(MicroRecipeHandler.this.cycleticks / 20 % this.cyclemap.size());
         if (this.cycletype == 1) {
            this.result = MicroRecipeHandler.getMicro(MicroRecipeHandler.getType(this.result), MicroRecipeHandler.materials[index], 2);
         }

         return new PositionedStack(this.result, 119, 24);
      }
   }

   public class GluingRecipe extends CachedRecipe {
      ItemStack result;
      int[] microclass;
      List<LinkedList<Integer>> gluingcombos;
      List<PositionedStack> ingreds = new ArrayList();
      int cycletype;

      public GluingRecipe(int[] microclass, ItemStack result, int usedthickness) {
         super();
         this.result = result;
         this.microclass = microclass;
         this.gluingcombos = ComboGenerator.generate(MicroRecipeHandler.getThickness(MicroRecipeHandler.getType(result)));
         if (usedthickness != -1) {
            ComboGenerator.removeNotContaining(this.gluingcombos, usedthickness);
         }

         this.cycletype = 0;
      }

      public GluingRecipe(ItemStack micro, ItemStack result) {
         super();
         this.result = result;
         ItemStack m = micro.copy();
         m.stackSize = 1;

         for(int i = 0; i < micro.stackSize; ++i) {
            int pos = i >= 2 ? i + 1 : i;
            this.ingreds.add(MicroRecipeHandler.position(m, pos / 3, pos % 3));
         }

         this.cycletype = -1;
      }

      public List<PositionedStack> getIngredients() {
         if (this.cycletype == 0) {
            this.ingreds.clear();
            int cycle = MicroRecipeHandler.this.cycleticks / 20 % this.gluingcombos.size();
            int material = MicroRecipeHandler.getMaterial(this.result);
            LinkedList<Integer> combo = (LinkedList)this.gluingcombos.get(cycle);
            this.ingreds = new ArrayList(combo.size());

            for(int i = 0; i < combo.size(); ++i) {
               this.ingreds.add(MicroRecipeHandler.position(MicroRecipeHandler.getMicro(this.microclass[combo.get(i) - 1], material, 1), i / 3, i % 3));
            }
         }

         return this.ingreds;
      }

      public PositionedStack getResult() {
         return new PositionedStack(this.result, 119, 24);
      }
   }

   public class PostRecipe extends CachedRecipe {
      int type;
      int material;

      public PostRecipe(ItemStack result) {
         super();
         this.type = MicroRecipeHandler.getType(result);
         this.material = MicroRecipeHandler.getMaterial(result);
      }

      public List<PositionedStack> getIngredients() {
         return new ArrayList(
            Collections.singletonList(
               MicroRecipeHandler.position(MicroRecipeHandler.getMicro(MicroRecipeHandler.swapPostType(this.type), this.material, 1), 1, 1)
            )
         );
      }

      public PositionedStack getResult() {
         return new PositionedStack(MicroRecipeHandler.getMicro(this.type, this.material, 1), 119, 24);
      }
   }
}
