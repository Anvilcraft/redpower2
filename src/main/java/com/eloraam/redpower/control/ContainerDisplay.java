package com.eloraam.redpower.control;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.IHandleGuiEvent;
import com.eloraam.redpower.core.PacketGuiEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerDisplay extends Container implements IHandleGuiEvent {
    private TileDisplay tileDisplay;
    private byte[] screen = new byte[4000];
    private int cursx = 0;
    private int cursy = 0;
    private int cursmode = 0;

    private void decompress(byte[] compress, byte[] out) {
        int opos = 0;
        int i = 0;

        while (i < compress.length) {
            if (opos >= out.length) {
                return;
            }

            int cmd = compress[i++] & 255;
            if ((cmd & 128) == 0) {
                opos += cmd & 127;
            } else if (cmd != 255) {
                int ln = Math.min(
                    Math.min(cmd & 127, out.length - opos), compress.length - i
                );
                System.arraycopy(compress, i, out, opos, ln);
                opos += ln;
                i += ln;
            } else {
                if (i + 2 > compress.length) {
                    return;
                }

                int ln = Math.min(compress[i] & 255, out.length - opos);

                for (int j = 0; j < ln; ++j) {
                    out[opos + j] = compress[i + 1];
                }

                opos += ln;
                i += 2;
            }
        }
    }

    public ContainerDisplay(IInventory inv, TileDisplay td) {
        this.tileDisplay = td;
    }

    public ItemStack slotClick(int a, int b, int c, EntityPlayer player) {
        return !this.canInteractWith(player) ? null : super.slotClick(a, b, c, player);
    }

    public boolean canInteractWith(EntityPlayer player) {
        return this.tileDisplay.isUseableByPlayer(player);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        return null;
    }

    private byte[] getDisplayRLE() {
        ContainerDisplay.RLECompressor rle = new ContainerDisplay.RLECompressor();

        for (int i = 0; i < 4000; ++i) {
            rle.addByte(
                this.tileDisplay.screen[i], this.screen[i] != this.tileDisplay.screen[i]
            );
            this.screen[i] = this.tileDisplay.screen[i];
        }

        rle.flush();
        return rle.getByteArray();
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        byte[] drl = this.getDisplayRLE();

        for (ICrafting ic : (List<ICrafting>) super.crafters) {
            if (this.tileDisplay.cursX != this.cursx) {
                ic.sendProgressBarUpdate(this, 0, this.tileDisplay.cursX);
            }

            if (this.tileDisplay.cursY != this.cursy) {
                ic.sendProgressBarUpdate(this, 1, this.tileDisplay.cursY);
            }

            if (this.tileDisplay.cursMode != this.cursmode) {
                ic.sendProgressBarUpdate(this, 2, this.tileDisplay.cursMode);
            }

            if (drl != null) {
                RedPowerCore.sendPacketToCrafting(
                    ic, new PacketGuiEvent.GuiMessageEvent(2, super.windowId, drl)
                );
            }
        }

        this.cursx = this.tileDisplay.cursX;
        this.cursy = this.tileDisplay.cursY;
        this.cursmode = this.tileDisplay.cursMode;
    }

    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0:
                this.tileDisplay.cursX = value;
                return;
            case 1:
                this.tileDisplay.cursY = value;
                return;
            case 2:
                this.tileDisplay.cursMode = value;
        }
    }

    @Override
    public void handleGuiEvent(PacketGuiEvent.GuiMessageEvent message) {
        try {
            switch (message.eventId) {
                case 1:
                    this.tileDisplay.pushKey(message.parameters[0]);
                    this.tileDisplay.updateBlock();
                    break;
                case 2:
                    this.decompress(message.parameters, this.tileDisplay.screen);
            }
        } catch (Throwable var3) {}
    }

    public class RLECompressor {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        byte[] datbuf = new byte[256];
        byte srledat = 0;
        int rleoffs = 0;
        int srleoffs = 0;
        int datpos = 0;
        boolean changed = false;

        public void writeRLE() {
            this.bas.write((byte) this.rleoffs);
            this.datpos = 0;
            this.rleoffs = 0;
            this.srleoffs = 0;
        }

        public void writeSRLE() {
            this.bas.write(-1);
            this.bas.write((byte) this.srleoffs);
            this.bas.write(this.srledat);
            this.datpos = 0;
            this.rleoffs = 0;
            this.srleoffs = 0;
        }

        public void writeDat(int bytes) {
            if (bytes != 0) {
                this.bas.write((byte) (128 | bytes));
                this.bas.write(this.datbuf, 0, bytes);
                this.datpos -= bytes;
            }
        }

        public void addByte(byte b, boolean diff) {
            if (diff) {
                this.changed = true;
                if (this.rleoffs > 5 && this.rleoffs >= this.srleoffs) {
                    this.writeDat(this.datpos - this.rleoffs);
                    this.writeRLE();
                }

                this.rleoffs = 0;
            } else {
                ++this.rleoffs;
                if (this.rleoffs >= 127) {
                    ++this.datpos;
                    this.writeDat(this.datpos - this.rleoffs);
                    this.writeRLE();
                    return;
                }
            }

            if (this.srleoffs == 0) {
                this.srledat = b;
                this.srleoffs = 1;
            } else if (b == this.srledat) {
                ++this.srleoffs;
                if (this.srleoffs >= 127) {
                    ++this.datpos;
                    this.writeDat(this.datpos - this.srleoffs);
                    this.writeSRLE();
                    return;
                }
            } else {
                if (this.srleoffs > 5 && this.srleoffs >= this.rleoffs) {
                    this.writeDat(this.datpos - this.srleoffs);
                    this.writeSRLE();
                }

                this.srledat = b;
                this.srleoffs = 1;
            }

            this.datbuf[this.datpos] = b;
            ++this.datpos;
            int rem = Math.max(this.srleoffs, this.rleoffs);
            if (rem <= 5 && this.datpos >= 126) {
                this.writeDat(this.datpos);
                this.srleoffs = 0;
                this.rleoffs = 0;
            } else if (this.datpos - rem >= 126) {
                this.writeDat(this.datpos - rem);
            }
        }

        public void flush() {
            this.datpos -= this.rleoffs;
            this.srleoffs = Math.max(0, this.srleoffs - this.rleoffs);
            if (this.datpos != 0) {
                if (this.srleoffs > 5) {
                    this.writeDat(this.datpos - this.srleoffs);
                    this.writeSRLE();
                } else {
                    this.writeDat(this.datpos);
                }
            }
        }

        byte[] getByteArray() {
            return !this.changed ? null : this.bas.toByteArray();
        }
    }
}
