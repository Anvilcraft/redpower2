package com.eloraam.redpower.base;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiAlloyFurnace extends GuiContainer {
   private static final ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/afurnacegui.png");
   private TileAlloyFurnace furnace;

   public GuiAlloyFurnace(InventoryPlayer pli, TileAlloyFurnace td) {
      super(new ContainerAlloyFurnace(pli, td));
      this.furnace = td;
   }

   public GuiAlloyFurnace(Container cn) {
      super(cn);
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("tile.rpafurnace.name", new Object[0]), 60, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      if (this.furnace.burntime > 0) {
         int i1 = this.furnace.getBurnScaled(12);
         this.drawTexturedModalRect(j + 17, k + 25 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
      }

      int i1 = this.furnace.getCookScaled(24);
      this.drawTexturedModalRect(j + 107, k + 34, 176, 14, i1 + 1, 16);
   }
}
