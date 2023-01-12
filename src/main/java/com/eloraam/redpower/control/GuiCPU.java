package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCPU extends GuiContainer {
    private static final ResourceLocation res
        = new ResourceLocation("rpcontrol", "textures/gui/cpugui.png");
    private TileCPU tileCPU;

    public GuiCPU(InventoryPlayer pli, TileCPU cpu) {
        super(new ContainerCPU(pli, cpu));
        this.tileCPU = cpu;
        super.ySize = 145;
        super.xSize = 227;
    }

    public GuiCPU(Container cn) {
        super(cn);
        super.ySize = 145;
        super.xSize = 227;
    }

    protected void drawGuiContainerForegroundLayer(int p1, int p2) {}

    protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.mc.renderEngine.bindTexture(res);
        int j = (super.width - super.xSize) / 2;
        int k = (super.height - super.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, super.xSize, super.ySize);
        int bits = this.tileCPU.diskAddr;

        for (int n = 0; n < 8; ++n) {
            if ((bits & 1 << n) != 0) {
                this.drawTexturedModalRect(
                    j + 14 + n * 12, k + 57, 227 + (n >> 2) * 12, 0, 12, 32
                );
            }
        }

        bits = this.tileCPU.displayAddr;

        for (int n = 0; n < 8; ++n) {
            if ((bits & 1 << n) != 0) {
                this.drawTexturedModalRect(
                    j + 118 + n * 12, k + 57, 227 + (n >> 2) * 12, 0, 12, 32
                );
            }
        }

        bits = this.tileCPU.rbaddr;

        for (int n = 0; n < 8; ++n) {
            if ((bits & 1 << n) != 0) {
                this.drawTexturedModalRect(
                    j + 118 + n * 12, k + 101, 227 + (n >> 2) * 12, 0, 12, 32
                );
            }
        }

        if (this.tileCPU.isRunning()) {
            this.drawTexturedModalRect(j + 102, k + 99, 227, 32, 8, 8);
        } else {
            this.drawTexturedModalRect(j + 102, k + 112, 227, 32, 8, 8);
        }

        super.fontRendererObj.drawString(
            I18n.format("gui.cpu.diskid", new Object[] { this.tileCPU.diskAddr & 0xFF }),
            j + 14,
            k + 47,
            -1
        );
        super.fontRendererObj.drawString(
            I18n.format(
                "gui.cpu.consoleid", new Object[] { this.tileCPU.displayAddr & 0xFF }
            ),
            j + 118,
            k + 47,
            -1
        );
        super.fontRendererObj.drawString(
            I18n.format("gui.cpu.selfid", new Object[] { this.tileCPU.rbaddr & 0xFF }),
            j + 118,
            k + 91,
            -1
        );
        super.fontRendererObj.drawString(
            I18n.format("gui.cpu.start", new Object[0]), j + 50, k + 99, -1
        );
        super.fontRendererObj.drawString(
            I18n.format("gui.cpu.halt", new Object[0]), j + 50, k + 112, -1
        );
        super.fontRendererObj.drawString(
            I18n.format("gui.cpu.reset", new Object[0]), j + 50, k + 125, -1
        );
    }

    private void sendSimple(int n, byte m) {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(
                new PacketGuiEvent.GuiMessageEvent(n, super.inventorySlots.windowId, m)
            );
        }
    }

    private boolean sendEvent(int n) {
        if (super.mc.theWorld.isRemote) {
            RedPowerCore.sendPacketToServer(new PacketGuiEvent.GuiMessageEvent(
                n, super.inventorySlots.windowId, (byte) 0
            ));
            return false;
        } else {
            return true;
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int button) {
        int x = mouseX - (super.width - super.xSize) / 2;
        int y = mouseY - (super.height - super.ySize) / 2;
        if (y >= 57 && y <= 89) {
            for (int n = 0; n < 8; ++n) {
                if (x >= 14 + n * 12 && x <= 26 + n * 12) {
                    this.tileCPU.diskAddr ^= 1 << n;
                    this.sendSimple(1, (byte) this.tileCPU.diskAddr);
                    return;
                }
            }

            for (int n = 0; n < 8; ++n) {
                if (x >= 118 + n * 12 && x <= 130 + n * 12) {
                    this.tileCPU.displayAddr ^= 1 << n;
                    this.sendSimple(2, (byte) this.tileCPU.displayAddr);
                    return;
                }
            }
        }

        if (y >= 101 && y <= 133) {
            for (int n = 0; n < 8; ++n) {
                if (x >= 118 + n * 12 && x <= 130 + n * 12) {
                    this.tileCPU.rbaddr ^= 1 << n;
                    this.sendSimple(3, (byte) this.tileCPU.rbaddr);
                    return;
                }
            }
        }

        if (x >= 87 && x <= 96) {
            if (y >= 98 && y <= 107) {
                if (this.sendEvent(4)) {
                    this.tileCPU.warmBootCPU();
                }

                return;
            }

            if (y >= 111 && y <= 120) {
                if (this.sendEvent(5)) {
                    this.tileCPU.haltCPU();
                }

                return;
            }

            if (y >= 124 && y <= 133) {
                if (this.sendEvent(6)) {
                    this.tileCPU.coldBootCPU();
                }

                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
}
