package com.eloraam.redpower.machine;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiEject extends GuiContainer {
   private static final ResourceLocation res = new ResourceLocation("textures/gui/container/dispenser.png");
   private TileEjectBase tileEject;
   private int inventoryRows = 3;

   public GuiEject(InventoryPlayer pli, TileEjectBase td) {
      super(new ContainerEject(pli, td));
      this.tileEject = td;
   }

   public GuiEject(Container cn) {
      super(cn);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      super.fontRendererObj.drawString(I18n.format(this.tileEject.getInventoryName(), new Object[0]), 60, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
   }
}
