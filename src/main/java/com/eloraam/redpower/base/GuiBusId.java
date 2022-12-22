package com.eloraam.redpower.base;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBusId extends GuiContainer {
   private static final ResourceLocation res = new ResourceLocation("rpbase", "textures/gui/idgui.png");
   private IRedbusConnectable rbConn;
   private TileEntity tile;

   public GuiBusId(InventoryPlayer pli, IRedbusConnectable irc, TileEntity tile) {
      super(new ContainerBusId(pli, irc));
      this.rbConn = irc;
      this.tile = tile;
      super.ySize = 81;
      super.xSize = 123;
   }

   public GuiBusId(Container cn) {
      super(cn);
      super.ySize = 81;
      super.xSize = 123;
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      super.fontRendererObj.drawString(I18n.format("gui.busid", new Object[0]), 32, 6, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      super.mc.renderEngine.bindTexture(res);
      int j = (super.width - super.xSize) / 2;
      int k = (super.height - super.ySize) / 2;
      this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
      int bits = this.rbConn.rbGetAddr() & 0xFF;

      for(int n = 0; n < 8; ++n) {
         if ((bits & 1 << n) != 0) {
            this.drawTexturedModalRect(j + 16 + n * 12, k + 25, 123, 0, 8, 16);
         }
      }

      this.drawCenteredString(super.fontRendererObj, String.format("ID: %d", bits), super.width / 2, k + 60, -1);
   }

   private void sendAddr() {
      if (super.mc.theWorld.isRemote) {
         RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, (byte)this.rbConn.rbGetAddr()));
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int button) {
      int x = mouseX - (super.width - super.xSize) / 2;
      int y = mouseY - (super.height - super.ySize) / 2;
      if (y >= 25 && y <= 41) {
         for(int n = 0; n < 8; ++n) {
            if (x >= 16 + n * 12 && x <= 24 + n * 12) {
               this.rbConn.rbSetAddr(this.rbConn.rbGetAddr() ^ 1 << n);
               this.sendAddr();
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, button);
   }
}
