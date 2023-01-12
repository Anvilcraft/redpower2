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

public class GuiManager extends GuiContainer {
    static int[] paintColors
        = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, 65280,
                      16737408, 5460819,  9671571,  65535,   8388863,  255,
                      5187328,  32768,    16711680, 2039583 };
    private static final ResourceLocation res
        = new ResourceLocation("rpmachine", "textures/gui/manager.png");
    private TileManager manager;

    public GuiManager(InventoryPlayer pli, TileManager td) {
        super(new ContainerManager(pli, td));
        this.manager = td;
        super.ySize = 186;
    }

    public GuiManager(Container cn) {
        super(cn);
        super.ySize = 186;
    }

    protected void drawGuiContainerForegroundLayer(int p1, int p2) {
        super.fontRendererObj.drawString(
            I18n.format("tile.rpmanager.name", new Object[0]), 68, 6, 4210752
        );
        super.fontRendererObj.drawString(
            I18n.format("container.inventory", new Object[0]),
            8,
            super.ySize - 96 + 2,
            4210752
        );
    }

    protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.mc.renderEngine.bindTexture(res);
        int j = (super.width - super.xSize) / 2;
        int k = (super.height - super.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
        int s = this.manager.cond.getChargeScaled(48);
        this.drawTexturedModalRect(j + 17, k + 76 - s, 176, 48 - s, 5, s);
        s = this.manager.cond.getFlowScaled(48);
        this.drawTexturedModalRect(j + 24, k + 76 - s, 176, 48 - s, 5, s);
        if (this.manager.cond.Charge > 600) {
            this.drawTexturedModalRect(j + 18, k + 20, 181, 0, 3, 6);
        }

        if (this.manager.cond.Flow == -1) {
            this.drawTexturedModalRect(j + 25, k + 20, 184, 0, 3, 6);
        }

        this.drawTexturedModalRect(j + 153, k + 37, 191, 14 * this.manager.mode, 14, 14);
        if (this.manager.color > 0) {
            this.rect(j + 158, k + 78, 4, 4, paintColors[this.manager.color - 1]);
        } else {
            this.drawTexturedModalRect(j + 158, k + 78, 187, 0, 4, 4);
        }

        String nm = String.format("%d", this.manager.priority);
        super.fontRendererObj.drawStringWithShadow(
            nm, j + 160 - super.fontRendererObj.getStringWidth(nm) / 2, k + 58, 16777215
        );
    }

    private void sendMode() {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                1, super.inventorySlots.windowId, this.manager.mode
            ));
        }
    }

    private void sendColor() {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                2, super.inventorySlots.windowId, this.manager.color
            ));
        }
    }

    private void sendPriority() {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                3, super.inventorySlots.windowId, (byte) this.manager.priority
            ));
        }
    }

    protected void changeColor(boolean incdec) {
        if (incdec) {
            ++this.manager.color;
            if (this.manager.color > 16) {
                this.manager.color = 0;
            }
        } else {
            --this.manager.color;
            if (this.manager.color < 0) {
                this.manager.color = 16;
            }
        }

        this.sendColor();
    }

    protected void mouseClicked(int i, int j, int k) {
        int x = i - (super.width - super.xSize) / 2;
        int y = j - (super.height - super.ySize) / 2;
        if (x >= 154 && x <= 165) {
            if (y >= 38 && y <= 50) {
                if (k == 0) {
                    ++this.manager.mode;
                    if (this.manager.mode > 1) {
                        this.manager.mode = 0;
                    }
                } else {
                    --this.manager.mode;
                    if (this.manager.mode < 0) {
                        this.manager.mode = 1;
                    }
                }

                this.sendMode();
            }

            if (y >= 56 && y <= 68) {
                if (k == 0) {
                    ++this.manager.priority;
                    if (this.manager.priority > 9) {
                        this.manager.priority = 0;
                    }
                } else {
                    --this.manager.priority;
                    if (this.manager.priority < 0) {
                        this.manager.priority = 9;
                    }
                }

                this.sendPriority();
            }

            if (y >= 74 && y <= 86) {
                this.changeColor(k == 0);
            }
        }

        super.mouseClicked(i, j, k);
    }

    private void rect(int x, int y, int w, int h, int c) {
        w += x;
        h += y;
        float r = (float) (c >> 16 & 0xFF) / 255.0F;
        float g = (float) (c >> 8 & 0xFF) / 255.0F;
        float b = (float) (c & 0xFF) / 255.0F;
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glColor4f(r, g, b, 1.0F);
        tess.startDrawingQuads();
        tess.addVertex((double) x, (double) h, 0.0);
        tess.addVertex((double) w, (double) h, 0.0);
        tess.addVertex((double) w, (double) y, 0.0);
        tess.addVertex((double) x, (double) y, 0.0);
        tess.draw();
        GL11.glEnable(3553);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }
}
