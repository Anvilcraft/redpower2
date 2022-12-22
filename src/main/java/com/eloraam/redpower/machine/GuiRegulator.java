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

public class GuiRegulator extends GuiContainer {
   static int[] paintColors = new int[]{
      16777215, 16744448, 16711935, 7110911, 16776960, 65280, 16737408, 5460819, 9671571, 65535, 8388863, 255, 5187328, 32768, 16711680, 2039583
   };
   private static final ResourceLocation res = new ResourceLocation("rpmachine", "textures/gui/regulator.png");
   private TileRegulator tileRegulator;

   public GuiRegulator(InventoryPlayer pli, TileRegulator reg) {
      super(new ContainerRegulator(pli, reg));
      this.tileRegulator = reg;
      super.xSize = 211;
      super.ySize = 167;
   }

   public GuiRegulator(Container cn) {
      super(cn);
      super.xSize = 211;
      super.ySize = 167;
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("this.tileRegulator.getInventoryName()", new Object[0]), 79, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 25, super.ySize - 96 + 3, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      if (this.tileRegulator.color > 0) {
         this.rect(j + 140, k + 60, 4, 4, paintColors[this.tileRegulator.color - 1]);
      } else {
         this.drawTexturedModalRect(j + 140, k + 60, 212, 0, 4, 4);
      }

      this.drawTexturedModalRect(j + 135, k + 19, 216, 14 * this.tileRegulator.mode, 14, 14);
   }

   private void sendColor() {
      if (super.mc.theWorld.isRemote) {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, (byte)this.tileRegulator.color));
      }

   }

   private void sendMode() {
      if (super.mc.theWorld.isRemote) {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, this.tileRegulator.mode));
      }

   }

   protected void changeColor(boolean incdec) {
      if (incdec) {
         ++this.tileRegulator.color;
         if (this.tileRegulator.color > 16) {
            this.tileRegulator.color = 0;
         }
      } else {
         --this.tileRegulator.color;
         if (this.tileRegulator.color < 0) {
            this.tileRegulator.color = 16;
         }
      }

      this.sendColor();
   }

   protected void mouseClicked(int i, int j, int k) {
      int x = i - (super.width - super.xSize) / 2;
      int y = j - (super.height - super.ySize) / 2;
      if (x >= 136 && x <= 147) {
         if (y >= 56 && y <= 67) {
            this.changeColor(k == 0);
            return;
         }

         if (y >= 19 && y <= 32) {
            if (k == 0) {
               ++this.tileRegulator.mode;
               if (this.tileRegulator.mode > 1) {
                  this.tileRegulator.mode = 0;
               }
            } else {
               --this.tileRegulator.mode;
               if (this.tileRegulator.mode < 0) {
                  this.tileRegulator.mode = 1;
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
