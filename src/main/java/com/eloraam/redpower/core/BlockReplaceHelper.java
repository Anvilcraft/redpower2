package com.eloraam.redpower.core;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;

public class BlockReplaceHelper {
   public static void replaceBlock(Block toReplace, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemBlockClass) {
      Class<?>[] classTest = new Class[4];
      Exception exception = null;

      try {
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);

         for(Field blockField : Blocks.class.getDeclaredFields()) {
            if (Block.class.isAssignableFrom(blockField.getType())) {
               Block block = (Block)blockField.get(null);
               if (block == toReplace) {
                  String registryName = Block.blockRegistry.getNameForObject(block);
                  int id = Block.getIdFromBlock(block);
                  Block newBlock = (Block)blockClass.newInstance();
                  FMLControlledNamespacedRegistry<Block> registryBlocks = GameData.getBlockRegistry();
                  Field map1 = RegistrySimple.class.getDeclaredFields()[1];
                  map1.setAccessible(true);
                  ((Map)map1.get(registryBlocks)).put(registryName, newBlock);
                  Field map2 = RegistryNamespaced.class.getDeclaredFields()[0];
                  map2.setAccessible(true);
                  ((ObjectIntIdentityMap)map2.get(registryBlocks)).func_148746_a(newBlock, id);
                  blockField.setAccessible(true);
                  modifiersField.setInt(blockField, blockField.getModifiers() & -17);
                  blockField.set(null, newBlock);
                  ItemBlock itemBlock = (ItemBlock)itemBlockClass.getConstructor(Block.class).newInstance(newBlock);
                  FMLControlledNamespacedRegistry<Item> registryItems = GameData.getItemRegistry();
                  ((Map)map1.get(registryItems)).put(registryName, itemBlock);
                  ((ObjectIntIdentityMap)map2.get(registryItems)).func_148746_a(itemBlock, id);
                  classTest[0] = blockField.get(null).getClass();
                  classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
                  classTest[2] = ((ItemBlock)Item.getItemFromBlock(newBlock)).field_150939_a.getClass();
                  classTest[3] = Item.getItemFromBlock(newBlock).getClass();
               }
            }
         }
      } catch (Exception var19) {
         exception = var19;
      }

      if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null || classTest[3] != itemBlockClass) {
         throw new RuntimeException(
            "RedPower was unable to replace block "
               + toReplace.getUnlocalizedName()
               + "! Debug info to report: "
               + classTest[0]
               + ","
               + classTest[1]
               + ","
               + classTest[2]
               + ","
               + classTest[3],
            exception
         );
      }
   }
}
