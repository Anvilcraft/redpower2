package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiRetriever extends GuiContainer {
   static int[] paintColors = new int[]{
      16777215, 16744448, 16711935, 7110911, 16776960, 65280, 16737408, 5460819, 9671571, 65535, 8388863, 255, 5187328, 32768, 16711680, 2039583
   };
   private static final ResourceLocation res = new ResourceLocation("rpmachine", "textures/gui/retriever.png");
   private TileRetriever tileRetriever;

   public GuiRetriever(InventoryPlayer pli, TileRetriever retr) {
      super(new ContainerRetriever(pli, retr));
      this.tileRetriever = retr;
   }

   public GuiRetriever(Container cn) {
      super(cn);
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("tile.rpretriever.name", new Object[0]), 65, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      int s = this.tileRetriever.cond.getChargeScaled(48);
      this.drawTexturedModalRect(j + 10, k + 69 - s, 176, 48 - s, 5, s);
      s = this.tileRetriever.cond.getFlowScaled(48);
      this.drawTexturedModalRect(j + 17, k + 69 - s, 176, 48 - s, 5, s);
      if (this.tileRetriever.cond.Charge > 600) {
         this.drawTexturedModalRect(j + 11, k + 13, 181, 0, 3, 6);
      }

      if (this.tileRetriever.cond.Flow == -1) {
         this.drawTexturedModalRect(j + 18, k + 13, 184, 0, 3, 6);
      }

      if (this.tileRetriever.color > 0) {
         this.rect(j + 122, k + 59, 4, 4, paintColors[this.tileRetriever.color - 1]);
      } else {
         this.drawTexturedModalRect(j + 122, k + 59, 187, 0, 4, 4);
      }

      this.drawTexturedModalRect(j + 45, k + 54, 211, 14 * this.tileRetriever.mode, 14, 14);
      if (this.tileRetriever.mode == 0) {
         this.drawTexturedModalRect(j + 60 + 18 * (this.tileRetriever.select % 3), k + 15 + 18 * (this.tileRetriever.select / 3), 191, 0, 20, 20);
      }

   }

   private void sendColor() {
      if (super.mc.theWorld.isRemote) {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, this.tileRetriever.color));
      }

   }

   private void sendMode() {
      if (super.mc.theWorld.isRemote) {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, this.tileRetriever.mode));
      }

   }

   protected void changeColor(boolean incdec) {
      if (incdec) {
         ++this.tileRetriever.color;
         if (this.tileRetriever.color > 16) {
            this.tileRetriever.color = 0;
         }
      } else {
         --this.tileRetriever.color;
         if (this.tileRetriever.color < 0) {
            this.tileRetriever.color = 16;
         }
      }

      this.sendColor();
   }

   protected void mouseClicked(int i, int j, int k) {
      int x = i - (super.width - super.xSize) / 2;
      int y = j - (super.height - super.ySize) / 2;
      if (y >= 55 && y <= 66) {
         if (x >= 118 && x <= 129) {
            this.changeColor(k == 0);
            return;
         }

         if (x >= 45 && x <= 58) {
            if (k == 0) {
               ++this.tileRetriever.mode;
               if (this.tileRetriever.mode > 1) {
                  this.tileRetriever.mode = 0;
               }
            } else {
               --this.tileRetriever.mode;
               if (this.tileRetriever.mode < 0) {
                  this.tileRetriever.mode = 1;
               }
            }

            this.sendMode();
         }
      }

      super.mouseClicked(i, j, k);
   }

   private void rect(int x, int y, int w, int h, int c) {
      w += x;
      h += y;
      float r = (float)(c >> 16 & 0xFF) / 255.0F;
      float g = (float)(c >> 8 & 0xFF) / 255.0F;
      float b = (float)(c & 0xFF) / 255.0F;
      Tessellator tess = Tessellator.instance;
      GL11.glDisable(3553);
      GL11.glColor4f(r, g, b, 1.0F);
      tess.startDrawingQuads();
      tess.addVertex((double)x, (double)h, 0.0);
      tess.addVertex((double)w, (double)h, 0.0);
      tess.addVertex((double)w, (double)y, 0.0);
      tess.addVertex((double)x, (double)y, 0.0);
      tess.draw();
      GL11.glEnable(3553);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
   }
}
