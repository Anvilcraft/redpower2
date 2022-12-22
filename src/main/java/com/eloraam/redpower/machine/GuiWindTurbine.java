package com.eloraam.redpower.machine;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiWindTurbine extends GuiContainer {
   private static final ResourceLocation res = new ResourceLocation("rpmachine", "textures/gui/windgui.png");
   private TileWindTurbine tileWT;

   public GuiWindTurbine(InventoryPlayer pli, TileWindTurbine wt) {
      super(new ContainerWindTurbine(pli, wt));
      this.tileWT = wt;
      super.ySize = 167;
   }

   public GuiWindTurbine(Container cn) {
      super(cn);
      super.ySize = 167;
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("gui.windturbine", new Object[0]), 60, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      this.drawTexturedModalRect(j + 55, k + 65 - this.tileWT.getWindScaled(48), 176, 0, 5, 3);
   }
}
