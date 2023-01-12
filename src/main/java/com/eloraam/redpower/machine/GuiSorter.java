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

public class GuiSorter extends GuiContainer {
    static int[] paintColors
        = new int[] { 16777215, 16744448, 16711935, 7110911, 16776960, 65280,
                      16737408, 5460819,  9671571,  65535,   8388863,  255,
                      5187328,  32768,    16711680, 2039583 };
    private static final ResourceLocation res
        = new ResourceLocation("rpmachine", "textures/gui/sortmachine.png");
    private TileSorter sorter;

    public GuiSorter(InventoryPlayer pli, TileSorter td) {
        super(new ContainerSorter(pli, td));
        this.sorter = td;
        super.ySize = 222;
    }

    public GuiSorter(Container cn) {
        super(cn);
        super.ySize = 222;
    }

    protected void drawGuiContainerForegroundLayer(int p1, int p2) {
        super.fontRendererObj.drawString(
            I18n.format("tile.rpsorter.name", new Object[0]), 50, 6, 4210752
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
        if (this.sorter.mode < 2) {
            this.drawTexturedModalRect(
                j + 24 + 18 * this.sorter.column, k + 16, 176, 0, 20, 92
            );
        }

        for (int s = 0; s < 8; ++s) {
            if (this.sorter.colors[s] > 0) {
                this.rect(
                    j + 32 + s * 18, k + 114, 4, 4, paintColors[this.sorter.colors[s] - 1]
                );
            } else {
                this.drawTexturedModalRect(j + 32 + s * 18, k + 114, 187, 92, 4, 4);
            }
        }

        int var7 = this.sorter.cond.getChargeScaled(48);
        this.drawTexturedModalRect(j + 8, k + 68 - var7, 176, 140 - var7, 5, var7);
        var7 = this.sorter.cond.getFlowScaled(48);
        this.drawTexturedModalRect(j + 15, k + 68 - var7, 176, 140 - var7, 5, var7);
        if (this.sorter.cond.Charge > 600) {
            this.drawTexturedModalRect(j + 9, k + 12, 181, 92, 3, 6);
        }

        if (this.sorter.cond.Flow == -1) {
            this.drawTexturedModalRect(j + 16, k + 12, 184, 92, 3, 6);
        }

        this.drawTexturedModalRect(j + 7, k + 73, 210, 14 * this.sorter.automode, 14, 14);
        this.drawTexturedModalRect(j + 7, k + 91, 196, 14 * this.sorter.mode, 14, 14);
        if (this.sorter.mode == 4 || this.sorter.mode == 6) {
            this.drawTexturedModalRect(j + 7, k + 109, 27, 109, 14, 14);
            if (this.sorter.defcolor > 0) {
                this.rect(j + 12, k + 114, 4, 4, paintColors[this.sorter.defcolor - 1]);
            } else {
                this.drawTexturedModalRect(j + 12, k + 114, 187, 92, 4, 4);
            }
        }
    }

    private void sendMode() {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                1, super.inventorySlots.windowId, this.sorter.mode
            ));
        }
    }

    private void sendAutoMode() {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                4, super.inventorySlots.windowId, this.sorter.automode
            ));
        }
    }

    private void sendColor(int n) {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                2, super.inventorySlots.windowId, (byte) n, this.sorter.colors[n]
            ));
        }
    }

    private void sendDefColor() {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                3, super.inventorySlots.windowId, this.sorter.defcolor
            ));
        }
    }

    protected void changeColor(int n, boolean incdec) {
        if (incdec) {
            ++this.sorter.colors[n];
            if (this.sorter.colors[n] > 16) {
                this.sorter.colors[n] = 0;
            }
        } else {
            --this.sorter.colors[n];
            if (this.sorter.colors[n] < 0) {
                this.sorter.colors[n] = 16;
            }
        }

        this.sendColor(n);
    }

    protected void changeDefColor(boolean incdec) {
        if (incdec) {
            ++this.sorter.defcolor;
            if (this.sorter.defcolor > 16) {
                this.sorter.defcolor = 0;
            }
        } else {
            --this.sorter.defcolor;
            if (this.sorter.defcolor < 0) {
                this.sorter.defcolor = 16;
            }
        }

        this.sendDefColor();
    }

    protected void mouseClicked(int i, int j, int k) {
        int x = i - (super.width - super.xSize) / 2;
        int y = j - (super.height - super.ySize) / 2;
        if (x <= 21 && x >= 7) {
            if (y <= 105 && y >= 91) {
                if (k == 0) {
                    ++this.sorter.mode;
                    if (this.sorter.mode > 6) {
                        this.sorter.mode = 0;
                    }
                } else {
                    --this.sorter.mode;
                    if (this.sorter.mode < 0) {
                        this.sorter.mode = 6;
                    }
                }

                this.sendMode();
            }

            if (y <= 87 && y >= 73) {
                if (k == 0) {
                    ++this.sorter.automode;
                    if (this.sorter.automode > 2) {
                        this.sorter.automode = 0;
                    }
                } else {
                    --this.sorter.automode;
                    if (this.sorter.automode < 0) {
                        this.sorter.automode = 2;
                    }
                }

                this.sendAutoMode();
            }
        }

        if (y >= 110 && y <= 121) {
            for (int n = 0; n < 8; ++n) {
                if (x >= 28 + n * 18 && x <= 39 + n * 18) {
                    this.changeColor(n, k == 0);
                    return;
                }
            }

            if ((this.sorter.mode == 4 || this.sorter.mode == 6) && x >= 7 && x <= 21) {
                this.changeDefColor(k == 0);
                return;
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
