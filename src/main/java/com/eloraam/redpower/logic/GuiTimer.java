package com.eloraam.redpower.logic;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.PacketGuiEvent;
import com.google.common.primitives.Longs;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTimer extends GuiContainer {
    private TileLogicPointer tileLogic;
    private GuiButton[] buttons = new GuiButton[6];
    private ResourceLocation guiRes
        = new ResourceLocation("rplogic", "textures/gui/timersgui.png");

    public GuiTimer(InventoryPlayer pli, TileLogicPointer te) {
        this(new ContainerTimer(pli, te));
        this.tileLogic = te;
    }

    public GuiTimer(Container cn) {
        super(cn);
        super.xSize = 228;
        super.ySize = 82;
    }

    public void initGui() {
        super.initGui();
        int bw = super.xSize - 20;
        int l = (super.width - super.xSize) / 2;
        int m = (super.height - super.ySize) / 2;
        super.buttonList.add(
            this.buttons[0] = new GuiButton(1, l + 10, m + 50, bw / 6, 20, "-10s")
        );
        super.buttonList.add(
            this.buttons[1] = new GuiButton(2, l + 10 + bw / 6, m + 50, bw / 6, 20, "-1s")
        );
        super.buttonList.add(
            this.buttons[2]
            = new GuiButton(3, l + 10 + bw * 2 / 6, m + 50, bw / 6, 20, "-50ms")
        );
        super.buttonList.add(
            this.buttons[3]
            = new GuiButton(4, l + 10 + bw * 3 / 6, m + 50, bw / 6, 20, "+50ms")
        );
        super.buttonList.add(
            this.buttons[4]
            = new GuiButton(5, l + 10 + bw * 4 / 6, m + 50, bw / 6, 20, "+1s")
        );
        super.buttonList.add(
            this.buttons[5]
            = new GuiButton(6, l + 10 + bw * 5 / 6, m + 50, bw / 6, 20, "+10s")
        );
    }

    protected void drawGuiContainerForegroundLayer(int p1, int p2) {}

    protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
        FontRenderer fontrenderer = super.mc.fontRenderer;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.mc.renderEngine.bindTexture(this.guiRes);
        int l = (super.width - super.xSize) / 2;
        int m = (super.height - super.ySize) / 2;
        this.drawTexturedModalRect(l, m, 0, 0, super.xSize, super.ySize);
        String str = String.format(
            "Timer Interval: %.3fs", (double) this.tileLogic.getInterval() / 20.0
        );
        this.drawCenteredString(fontrenderer, str, super.width / 2, m + 10, -1);
    }

    public void changeInterval(int cc) {
        long iv = this.tileLogic.getInterval() + (long) cc;
        if (iv < 4L) {
            iv = 4L;
        }

        this.tileLogic.setInterval(iv);
        if (!super.mc.theWorld.isRemote) {
            this.tileLogic.updateBlock();
        } else {
            byte[] i = Longs.toByteArray(iv);
            RedPowerCore.sendPacketToServer(
                new PacketGuiEvent.GuiMessageEvent(1, super.inventorySlots.windowId, i)
            );
        }
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            switch (button.id) {
                case 1:
                    this.changeInterval(-200);
                    break;
                case 2:
                    this.changeInterval(-20);
                    break;
                case 3:
                    this.changeInterval(-1);
                    break;
                case 4:
                    this.changeInterval(1);
                    break;
                case 5:
                    this.changeInterval(20);
                    break;
                case 6:
                    this.changeInterval(200);
            }
        }
    }
}
