package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiItemDetect extends GuiContainer {
   private static final ResourceLocation res = new ResourceLocation("rpmachine", "textures/gui/itemdet.png");
   private TileItemDetect tileDetect;

   public GuiItemDetect(InventoryPlayer pli, TileItemDetect filter) {
      super(new ContainerItemDetect(pli, filter));
      this.tileDetect = filter;
   }

   public GuiItemDetect(Container cn) {
      super(cn);
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("tile.rpitemdet.name", new Object[0]), 60, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      this.drawTexturedModalRect(j + 117, k + 54, 176, 14 * this.tileDetect.mode, 14, 14);
   }

   private void sendButton(byte n) {
      RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, n));
   }

   protected void mouseClicked(int i, int j, int k) {
      int x = i - (super.width - super.xSize) / 2;
      int y = j - (super.height - super.ySize) / 2;
      if (x >= 117 && y >= 54 && x <= 131 && y <= 68) {
         if (k == 0) {
            ++this.tileDetect.mode;
            if (this.tileDetect.mode > 2) {
               this.tileDetect.mode = 0;
            }
         } else {
            --this.tileDetect.mode;
            if (this.tileDetect.mode < 0) {
               this.tileDetect.mode = 2;
            }
         }

         if (super.mc.theWorld.isRemote) {
            this.sendButton(this.tileDetect.mode);
         }
      }

      super.mouseClicked(i, j, k);
   }
}
