package com.eloraam.redpower.base;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBag extends GuiContainer {
   private static ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/baggui.png");

   public GuiBag(InventoryPlayer pli, IInventory td) {
      super(new ContainerBag(pli, td, null));
      super.ySize = 167;
   }

   public GuiBag(Container cn) {
      super(cn);
      super.ySize = 167;
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("item.rpBag.name", new Object[0]), 8, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 94 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int halfWidth = (super.width - super.xSize) / 2;
      int halfHeight = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(halfWidth, halfHeight, 0, 0, super.xSize, super.ySize);
   }
}
