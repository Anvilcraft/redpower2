package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiAssemble extends GuiContainer {
   private static final ResourceLocation res1 = new ResourceLocation("rpmachine", "textures/gui/assembler.png");
   private static final ResourceLocation res2 = new ResourceLocation("rpmachine", "textures/gui/assembler2.png");
   private TileAssemble assemble;

   public GuiAssemble(InventoryPlayer pli, TileAssemble td) {
      super(new ContainerAssemble(pli, td));
      this.assemble = td;
      super.ySize = 195;
   }

   public GuiAssemble(Container cn) {
      super(cn);
      super.ySize = 195;
   }

   protected void drawGuiContainerForegroundLayer(int p1, int p2) {
      super.fontRendererObj.drawString(I18n.format("tile.rpassemble.name", new Object[0]), 65, 6, 4210752);
      super.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, super.ySize - 96 + 2, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(this.assemble.mode == 0 ? res1 : res2);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      this.drawTexturedModalRect(j + 152, k + 37, 196, 14 * this.assemble.mode, 14, 14);
      if (this.assemble.mode == 0) {
         this.drawTexturedModalRect(j + 6 + 18 * (this.assemble.select & 7), k + 16 + 18 * (this.assemble.select >> 3), 176, 0, 20, 20);

         for(int i = 1; i < 16; ++i) {
            if ((this.assemble.skipSlots & 1 << i) != 0) {
               this.drawTexturedModalRect(j + 8 + 18 * (i & 7), k + 18 + 18 * (i >> 3), 176, 20, 16, 16);
            }
         }
      }

   }

   private void sendMode() {
      if (!super.mc.theWorld.isRemote) {
         this.assemble.updateBlockChange();
      } else {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, this.assemble.mode));
      }

   }

   private void sendSkip() {
      if (!super.mc.theWorld.isRemote) {
         this.assemble.updateBlockChange();
      } else {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(2, super.inventorySlots.windowId, (byte)this.assemble.skipSlots));
      }

   }

   protected void mouseClicked(int i, int j, int k) {
      int x = i - (super.width - super.xSize) / 2;
      int y = j - (super.height - super.ySize) / 2;
      if (x >= 152 && y >= 37 && x <= 166 && y <= 51) {
         if (k == 0) {
            ++this.assemble.mode;
            if (this.assemble.mode > 1) {
               this.assemble.mode = 0;
            }
         } else {
            --this.assemble.mode;
            if (this.assemble.mode < 0) {
               this.assemble.mode = 1;
            }
         }

         this.sendMode();
      } else {
         if (this.assemble.mode == 0 && super.mc.thePlayer.inventory.getItemStack() == null) {
            boolean send = false;

            for(int v = 1; v < 16; ++v) {
               int x2 = 8 + 18 * (v & 7);
               int y2 = 18 + 18 * (v >> 3);
               if (x >= x2 && x < x2 + 16 && y >= y2 && y < y2 + 16) {
                  if (super.inventorySlots.getSlot(v).getHasStack()) {
                     break;
                  }

                  this.assemble.skipSlots ^= 1 << v;
                  send = true;
               }
            }

            if (send) {
               this.sendSkip();
               return;
            }
         }

         super.mouseClicked(i, j, k);
      }

   }
}
