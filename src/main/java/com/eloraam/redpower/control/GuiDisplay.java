package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDisplay extends GuiContainer {
    private static ResourceLocation screenTextures
        = new ResourceLocation("rpcontrol", "textures/gui/displaygui.png");
    private TileDisplay disp;

    public GuiDisplay(IInventory inv, TileDisplay td) {
        super(new ContainerDisplay(inv, td));
        super.xSize = 350;
        super.ySize = 230;
        this.disp = td;
    }

    private void sendKey(int id) {
        RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
            1, super.inventorySlots.windowId, (byte) id
        ));
    }

    protected void keyTyped(char symbol, int key) {
        if (key == 1) {
            super.mc.thePlayer.closeScreen();
        } else {
            if (symbol == '\n') {
                symbol = '\r';
            }

            byte id = 0;
            if (isShiftKeyDown()) {
                id = (byte) (id | 64);
            }

            if (isCtrlKeyDown()) {
                id = (byte) (id | 32);
            }

            switch (key) {
                case 199:
                    this.sendKey(132 | id);
                    break;
                case 200:
                    this.sendKey(128 | id);
                    break;
                case 201:
                case 202:
                case 204:
                case 206:
                case 209:
                default:
                    if (symbol > 0 && symbol <= 127) {
                        this.sendKey(symbol);
                    }
                    break;
                case 203:
                    this.sendKey(130 | id);
                    break;
                case 205:
                    this.sendKey(131 | id);
                    break;
                case 207:
                    this.sendKey(133 | id);
                    break;
                case 208:
                    this.sendKey(129 | id);
                    break;
                case 210:
                    this.sendKey(134 | id);
            }
        }
    }

    public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.mc.renderEngine.bindTexture(screenTextures);
        int l = (super.width - super.xSize) / 2;
        int m = (super.height - super.ySize) / 2;
        this.drawDoubledRect(
            l, m, super.xSize, super.ySize, 0, 0, super.xSize, super.ySize
        );
        GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);

        for (int y = 0; y < 50; ++y) {
            for (int x = 0; x < 80; ++x) {
                int b = this.disp.screen[y * 80 + x] & 255;
                if (x == this.disp.cursX && y == this.disp.cursY) {
                    if (this.disp.cursMode == 1) {
                        b ^= 128;
                    }

                    if (this.disp.cursMode == 2) {
                        long tm = super.mc.theWorld.getTotalWorldTime();
                        if ((tm >> 2 & 1L) > 0L) {
                            b ^= 128;
                        }
                    }
                }

                if (b != 32) {
                    this.drawDoubledRect(
                        l + 15 + x * 4,
                        m + 15 + y * 4,
                        4,
                        4,
                        350 + (b & 15) * 8,
                        (b >> 4) * 8,
                        8,
                        8
                    );
                }
            }
        }
    }

    public void drawDoubledRect(
        int xPos,
        int yPos,
        int width,
        int heigth,
        int uStart,
        int vStart,
        int uEnd,
        int vEnd
    ) {
        float xm = 0.001953125F;
        float ym = 0.00390625F;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(
            (double) xPos,
            (double) (yPos + heigth),
            (double) super.zLevel,
            (double) ((float) uStart * xm),
            (double) ((float) (vStart + vEnd) * ym)
        );
        tess.addVertexWithUV(
            (double) (xPos + width),
            (double) (yPos + heigth),
            (double) super.zLevel,
            (double) ((float) (uStart + uEnd) * xm),
            (double) ((float) (vStart + vEnd) * ym)
        );
        tess.addVertexWithUV(
            (double) (xPos + width),
            (double) yPos,
            (double) super.zLevel,
            (double) ((float) (uStart + uEnd) * xm),
            (double) ((float) vStart * ym)
        );
        tess.addVertexWithUV(
            (double) xPos,
            (double) yPos,
            (double) super.zLevel,
            (double) ((float) uStart * xm),
            (double) ((float) vStart * ym)
        );
        tess.draw();
    }
}
