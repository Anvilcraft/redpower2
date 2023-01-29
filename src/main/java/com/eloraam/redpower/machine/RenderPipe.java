package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.PipeLib;
import com.eloraam.redpower.core.RenderCovers;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderPipe extends RenderCovers {
    public RenderPipe(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TilePipe pipe = (TilePipe) tile;
        World world = pipe.getWorldObj();
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(2896);
        super.context.bindBlockTexture();
        super.context.exactTextureCoordinates = true;
        super.context.setTexFlags(55);
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setPos(x, y, z);
        tess.startDrawingQuads();
        if (pipe.CoverSides > 0) {
            super.context.readGlobalLights(world, pipe.xCoord, pipe.yCoord, pipe.zCoord);
            this.renderCovers(pipe.CoverSides, pipe.Covers);
        }

        int cons1 = PipeLib.getConnections(world, pipe.xCoord, pipe.yCoord, pipe.zCoord);
        super.context.setBrightness(this.getMixedBrightness(pipe));
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.setPos(x, y, z);
        this.renderCenterBlock(cons1, RedPowerMachine.pipeSide, RedPowerMachine.pipeFace);
        pipe.cacheFlange();
        this.renderFlanges(pipe.Flanges, RedPowerMachine.pipeFlanges);
        tess.draw();
        int lvl = pipe.pipebuf.getLevel();
        Fluid fcl = pipe.pipebuf.Type;
        if (fcl != null && lvl > 0) {
            float lvn = Math.min(1.0F, (float) lvl / (float) pipe.pipebuf.getMaxLevel());
            pipe.cacheCon();
            int sides = pipe.ConCache;
            int lv = world.getLightBrightnessForSkyBlocks(
                pipe.xCoord, pipe.yCoord, pipe.zCoord, 0
            );
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            tess.startDrawingQuads();
            super.context.setBrightness(lv);
            super.context.setPos(x, y, z);
            super.context.setIcon(fcl.getIcon());
            if ((sides & 3) > 0) {
                float y1 = 0.5F;
                float y2 = 0.5F;
                if ((sides & 1) > 0) {
                    y1 = 0.0F;
                }

                if ((sides & 2) > 0) {
                    y2 = 1.0F;
                }

                float n = 0.124F * lvn;
                super.context.renderBox(
                    60,
                    (double) (0.5F - n),
                    (double) y1,
                    (double) (0.5F - n),
                    (double) (0.5F + n),
                    (double) y2,
                    (double) (0.5F + n)
                );
            }

            if ((sides & 12) > 0) {
                float z1 = 0.5F;
                float z2 = 0.5F;
                if ((sides & 4) > 0) {
                    z1 = 0.0F;
                }

                if ((sides & 8) > 0) {
                    z2 = 1.0F;
                }

                float n = 0.248F * lvn;
                super.context.renderBox(
                    51,
                    0.376F,
                    0.376F,
                    (double) z1,
                    0.624F,
                    (double) (0.376F + n),
                    (double) z2
                );
            }

            if ((sides & 48) > 0) {
                float x1 = 0.5F;
                float x2 = 0.5F;
                if ((sides & 16) > 0) {
                    x1 = 0.0F;
                }

                if ((sides & 32) > 0) {
                    x2 = 1.0F;
                }

                float n = 0.248F * lvn;
                super.context.renderBox(
                    15,
                    (double) x1,
                    0.376F,
                    0.376F,
                    (double) x2,
                    (double) (0.376F + n),
                    0.624F
                );
            }

            tess.draw();
            GL11.glDisable(3042);
        }

        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        super.block.setBlockBoundsForItemRender();
        super.context.setDefaults();
        if (type == ItemRenderType.INVENTORY) {
            super.context.setPos(-0.5, -0.5, -0.5);
        } else {
            super.context.setPos(0.0, 0.0, 0.0);
        }

        super.context.useNormal = true;
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        super.context.useNormal = true;
        super.context.setIcon(
            RedPowerMachine.pipeFace,
            RedPowerMachine.pipeFace,
            RedPowerMachine.pipeSide,
            RedPowerMachine.pipeSide,
            RedPowerMachine.pipeSide,
            RedPowerMachine.pipeSide
        );
        super.context.renderBox(60, 0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
        super.context.renderBox(60, 0.624F, 0.999F, 0.624F, 0.376F, 0.001F, 0.376F);
        this.renderFlanges(3, RedPowerMachine.pipeFlanges);
        tess.draw();
        super.context.useNormal = false;
    }

    private void
    doubleBox(int sides, float x1, float y1, float z1, float x2, float y2, float z2) {
        int s2 = sides << 1 & 42 | sides >> 1 & 21;
        super.context.renderBox(
            sides,
            (double) x1,
            (double) y1,
            (double) z1,
            (double) x2,
            (double) y2,
            (double) z2
        );
        super.context.renderBox(
            s2,
            (double) x2,
            (double) y2,
            (double) z2,
            (double) x1,
            (double) y1,
            (double) z1
        );
    }

    public void renderFlanges(int cons, IIcon tex) {
        super.context.setIcon(tex);
        if ((cons & 1) > 0) {
            super.context.setTexFlags(0);
            super.context.renderBox(63, 0.25, 0.0, 0.25, 0.75, 0.125, 0.75);
        }

        if ((cons & 2) > 0) {
            super.context.setTexFlags(112320);
            super.context.renderBox(63, 0.25, 0.875, 0.25, 0.75, 1.0, 0.75);
        }

        if ((cons & 4) > 0) {
            super.context.setTexFlags(217134);
            super.context.renderBox(63, 0.25, 0.25, 0.0, 0.75, 0.75, 0.125);
        }

        if ((cons & 8) > 0) {
            super.context.setTexFlags(188469);
            super.context.renderBox(63, 0.25, 0.25, 0.875, 0.75, 0.75, 1.0);
        }

        if ((cons & 16) > 0) {
            super.context.setTexFlags(2944);
            super.context.renderBox(63, 0.0, 0.25, 0.25, 0.125, 0.75, 0.75);
        }

        if ((cons & 32) > 0) {
            super.context.setTexFlags(3419);
            super.context.renderBox(63, 0.875, 0.25, 0.25, 1.0, 0.75, 0.75);
        }
    }

    public void renderCenterBlock(int cons, IIcon side, IIcon end) {
        if (cons == 0) {
            super.context.setIcon(end);
            this.doubleBox(63, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
        } else if (cons == 3) {
            super.context.setTexFlags(1773);
            super.context.setIcon(end, end, side, side, side, side);
            this.doubleBox(60, 0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
        } else if (cons == 12) {
            super.context.setTexFlags(184365);
            super.context.setIcon(side, side, end, end, side, side);
            this.doubleBox(51, 0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
        } else if (cons == 48) {
            super.context.setTexFlags(187200);
            super.context.setIcon(side, side, side, side, end, end);
            this.doubleBox(15, 0.0F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
        } else {
            super.context.setIcon(end);
            this.doubleBox(63 ^ cons, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
            if ((cons & 1) > 0) {
                super.context.setTexFlags(1773);
                super.context.setIcon(end, end, side, side, side, side);
                this.doubleBox(60, 0.375F, 0.0F, 0.375F, 0.625F, 0.375F, 0.625F);
            }

            if ((cons & 2) > 0) {
                super.context.setTexFlags(1773);
                super.context.setIcon(end, end, side, side, side, side);
                this.doubleBox(60, 0.375F, 0.625F, 0.375F, 0.625F, 1.0F, 0.625F);
            }

            if ((cons & 4) > 0) {
                super.context.setTexFlags(184365);
                super.context.setIcon(side, side, end, end, side, side);
                this.doubleBox(51, 0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 0.375F);
            }

            if ((cons & 8) > 0) {
                super.context.setTexFlags(184365);
                super.context.setIcon(side, side, end, end, side, side);
                this.doubleBox(51, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F, 1.0F);
            }

            if ((cons & 16) > 0) {
                super.context.setTexFlags(187200);
                super.context.setIcon(side, side, side, side, end, end);
                this.doubleBox(15, 0.0F, 0.375F, 0.375F, 0.375F, 0.625F, 0.625F);
            }

            if ((cons & 32) > 0) {
                super.context.setTexFlags(187200);
                super.context.setIcon(side, side, side, side, end, end);
                this.doubleBox(15, 0.625F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
            }
        }
    }
}
