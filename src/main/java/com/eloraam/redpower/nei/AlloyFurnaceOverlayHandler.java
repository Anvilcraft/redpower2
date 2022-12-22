package com.eloraam.redpower.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.DefaultOverlayHandler;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

public class AlloyFurnaceOverlayHandler extends DefaultOverlayHandler {
   public Slot[][] mapIngredSlots(GuiContainer gui, List<PositionedStack> ingredients) {
      Slot[][] map = super.mapIngredSlots(gui, ingredients);
      Slot[] ingredSlots = new Slot[9];

      for(int i = 0; i < 9; ++i) {
         ingredSlots[i] = (Slot)gui.inventorySlots.inventorySlots.get(i);
      }

      for(int i = 0; i < map.length; ++i) {
         map[i] = ingredSlots;
      }

      return map;
   }
}
