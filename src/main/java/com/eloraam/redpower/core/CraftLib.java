package com.eloraam.redpower.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftLib {
   public static List<List<Object>> alloyRecipes = new ArrayList();
   public static HashSet damageOnCraft = new HashSet();
   public static HashMap damageContainer = new HashMap();

   public static void addAlloyResult(ItemStack output, Object... input) {
      alloyRecipes.add(Arrays.asList(input, output));
   }

   public static void addOreRecipe(ItemStack output, Object... input) {
      CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(output, new Object[]{Boolean.TRUE, input}));
   }

   public static void addShapelessOreRecipe(ItemStack output, Object... input) {
      CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(output, input));
   }

   public static boolean isOreClass(ItemStack ist, String ore) {
      for(ItemStack stack : OreDictionary.getOres(ore)) {
         if (stack.isItemEqual(ist)) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack getAlloyResult(ItemStack[] input, int start, int end, boolean take) {
      label134:
      for(List<Object> l : alloyRecipes) {
         Object[] ob = l.toArray();
         Object[] ipt = (Object[]) ob[0];

         for(Object ingredient : ipt) {
            if (ingredient instanceof ItemStack) {
               ItemStack inputStack = (ItemStack)ingredient;
               int rc = inputStack.stackSize;

               for(int i = start; i < end; ++i) {
                  if (input[i] != null) {
                     if (input[i].isItemEqual(inputStack)) {
                        rc -= input[i].stackSize;
                     }

                     if (rc <= 0) {
                        break;
                     }
                  }
               }

               if (rc > 0) {
                  continue label134;
               }
            } else if (ingredient instanceof OreStack) {
               OreStack inputStack = (OreStack)ingredient;
               int rc = inputStack.quantity;

               for(int i = start; i < end; ++i) {
                  if (input[i] != null) {
                     if (isOreClass(input[i], inputStack.material)) {
                        rc -= input[i].stackSize;
                     }

                     if (rc <= 0) {
                        break;
                     }
                  }
               }

               if (rc > 0) {
                  continue label134;
               }
            }
         }

         if (take) {
            for(Object ingredient : ipt) {
               if (ingredient instanceof ItemStack) {
                  ItemStack inputStack = (ItemStack)ingredient;
                  int rc = inputStack.stackSize;

                  for(int i = start; i < end; ++i) {
                     if (input[i] != null && input[i].isItemEqual(inputStack)) {
                        rc -= input[i].stackSize;
                        if (rc < 0) {
                           input[i].stackSize = -rc;
                        } else if (input[i].getItem().hasContainerItem()) {
                           input[i] = new ItemStack(input[i].getItem().getContainerItem());
                        } else {
                           input[i] = null;
                        }

                        if (rc <= 0) {
                           break;
                        }
                     }
                  }
               } else if (ingredient instanceof OreStack) {
                  OreStack inputStack = (OreStack)ingredient;
                  int rc = inputStack.quantity;

                  for(int i = start; i < end; ++i) {
                     if (input[i] != null && isOreClass(input[i], inputStack.material)) {
                        rc -= input[i].stackSize;
                        if (rc < 0) {
                           input[i].stackSize = -rc;
                        } else {
                           input[i] = null;
                        }

                        if (rc <= 0) {
                           break;
                        }
                     }
                  }
               }
            }
         }

         return (ItemStack)ob[1];
      }

      return null;
   }
}
