package com.eloraam.redpower.wiring;

import com.eloraam.redpower.RedPowerWiring;
import com.eloraam.redpower.core.CoverRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderRedwire extends RenderWiring {
    public RenderRedwire(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileWiring wiring = (TileWiring) tile;
        World world = wiring.getWorldObj();
        int metadata = wiring.getBlockMetadata();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        super.context.bindBlockTexture();
        super.context.setBrightness(this.getMixedBrightness(wiring));
        super.context.setTexFlags(55);
        super.context.setPos(x, y, z);
        tess.startDrawingQuads();
        if (wiring.CoverSides > 0) {
            super.context.setTint(1.0F, 1.0F, 1.0F);
            super.context.readGlobalLights(
                world, wiring.xCoord, wiring.yCoord, wiring.zCoord
            );
            this.renderCovers(wiring.CoverSides, wiring.Covers);
            super.context.forceFlat = false;
            super.context.lockTexture = false;
        }

        if (metadata != 0) {
            int indcon = wiring.getExtConnectionMask();
            int cons = wiring.getConnectionMask() | indcon;
            int indconex = wiring.EConEMask;
            switch (metadata) {
                case 1:
                    TileRedwire tx = (TileRedwire) wiring;
                    super.context.setTint(
                        0.3F + 0.7F * ((float) tx.PowerState / 255.0F), 0.0F, 0.0F
                    );
                    this.setSideIcon(
                        RedPowerWiring.redwireTop,
                        RedPowerWiring.redwireFace,
                        RedPowerWiring.redwireTop
                    );
                    this.setWireSize(0.125F, 0.125F);
                    break;
                case 2:
                    TileInsulatedWire tx1 = (TileInsulatedWire) wiring;
                    super.context.setTint(1.0F, 1.0F, 1.0F);
                    this.setSideIcon(
                        RedPowerWiring.insulatedTop[wiring.Metadata],
                        tx1.PowerState > 0
                            ? RedPowerWiring.insulatedFaceOn[wiring.Metadata]
                            : RedPowerWiring.insulatedFaceOff[wiring.Metadata],
                        RedPowerWiring.insulatedTop[wiring.Metadata]
                    );
                    this.setWireSize(0.25F, 0.188F);
                    break;
                case 3:
                    super.context.setTint(1.0F, 1.0F, 1.0F);
                    if (wiring.Metadata == 0) {
                        this.setSideIcon(
                            RedPowerWiring.bundledTop,
                            RedPowerWiring.bundledFace,
                            RedPowerWiring.bundledTop
                        );
                    } else {
                        this.setSideIcon(
                            RedPowerWiring.bundledColTop[wiring.Metadata - 1],
                            RedPowerWiring.bundledColFace[wiring.Metadata - 1],
                            RedPowerWiring.bundledTop
                        );
                    }

                    this.setWireSize(0.375F, 0.25F);
                case 4:
                default:
                    break;
                case 5:
                    super.context.setTint(1.0F, 1.0F, 1.0F);
                    switch (wiring.Metadata) {
                        case 0:
                            this.setSideIcon(
                                RedPowerWiring.powerTop,
                                RedPowerWiring.powerFace,
                                RedPowerWiring.powerTop
                            );
                            this.setWireSize(0.25F, 0.188F);
                            break;
                        case 1:
                            this.setSideIcon(
                                RedPowerWiring.highPowerTop,
                                RedPowerWiring.highPowerFace,
                                RedPowerWiring.highPowerTop
                            );
                            this.setWireSize(0.375F, 0.25F);
                            break;
                        case 2:
                            this.setSideIconJumbo(
                                RedPowerWiring.jumboSides,
                                RedPowerWiring.jumboTop,
                                RedPowerWiring.jumboCent,
                                RedPowerWiring.jumboCentSide,
                                RedPowerWiring.jumboEnd,
                                RedPowerWiring.jumboCorners
                            );
                            this.setWireSize(0.5F, 0.3125F);
                    }
            }

            this.renderWireBlock(wiring.ConSides, cons, indcon, indconex);
            if ((metadata == 1 || metadata == 3 || metadata == 5)
                && (wiring.ConSides & 64) != 0) {
                super.context.setTexFlags(0);
                super.context.setOrientation(0, 0);
                super.context.setTint(1.0F, 1.0F, 1.0F);
                super.context.setLocalLights(0.5F, 1.0F, 0.7F, 0.7F, 0.7F, 0.7F);
                IIcon icon;
                switch (metadata) {
                    case 1:
                        icon = ((TileRedwire) wiring).PowerState > 0
                            ? RedPowerWiring.redwireCableOn
                            : RedPowerWiring.redwireCableOff;
                        break;
                    case 3:
                        icon = RedPowerWiring.bundledCable;
                        break;
                    default:
                        icon = RedPowerWiring.bluewireCable;
                }

                this.renderCenterBlock(
                    cons >> 24 | wiring.ConSides & 63,
                    CoverRenderer.coverIcons[wiring.CenterPost],
                    icon
                );
            }
        }

        tess.draw();
        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int meta = item.getItemDamage();
        Tessellator tess = Tessellator.instance;
        super.block.setBlockBoundsForItemRender();
        int bid = meta >> 8;
        meta &= 255;
        super.context.setDefaults();
        super.context.setTexFlags(55);
        if (type == ItemRenderType.INVENTORY) {
            super.context.setPos(-0.5, -0.5, -0.5);
        } else {
            super.context.setPos(0.0, 0.0, 0.0);
        }
        float th;
        switch (bid) {
            case 0:
            case 16:
            case 17:
            case 27:
            case 28:
            case 29:
            case 30:
                switch (bid) {
                    case 0:
                        th = 0.063F;
                        break;
                    case 16:
                        th = 0.125F;
                        break;
                    case 17:
                        th = 0.25F;
                        break;
                    case 27:
                        th = 0.188F;
                        break;
                    case 28:
                        th = 0.313F;
                        break;
                    case 29:
                        th = 0.375F;
                        break;
                    case 30:
                        th = 0.438F;
                        break;
                    default:
                        return;
                }

                super.context.setIcon(CoverRenderer.coverIcons[meta]);
                super.context.setSize(
                    0.0, 0.0, (double) (0.5F - th), 1.0, 1.0, (double) (0.5F + th)
                );
                super.context.calcBounds();
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.renderFaces(63);
                super.context.useNormal = false;
                tess.draw();
                return;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            default:
                if (type == ItemRenderType.INVENTORY) {
                    super.context.setPos(-0.5, -0.2F, -0.5);
                } else {
                    super.context.setPos(0.0, 0.29999999701976776, 0.0);
                }

                super.context.setOrientation(0, 0);
                switch (bid) {
                    case 1:
                        this.setSideIcon(
                            RedPowerWiring.redwireTop,
                            RedPowerWiring.redwireFace,
                            RedPowerWiring.redwireTop
                        );
                        this.setWireSize(0.125F, 0.125F);
                        super.context.setTint(1.0F, 0.0F, 0.0F);
                        break;
                    case 2:
                        this.setSideIcon(
                            RedPowerWiring.insulatedTop[meta],
                            RedPowerWiring.insulatedFaceOff[meta],
                            RedPowerWiring.insulatedTop[meta]
                        );
                        this.setWireSize(0.25F, 0.188F);
                        break;
                    case 3:
                        switch (meta) {
                            case 0:
                                this.setSideIcon(
                                    RedPowerWiring.bundledTop,
                                    RedPowerWiring.bundledFace,
                                    RedPowerWiring.bundledTop
                                );
                                break;
                            default:
                                this.setSideIcon(
                                    RedPowerWiring.bundledColTop[meta - 1],
                                    RedPowerWiring.bundledColFace[meta - 1],
                                    RedPowerWiring.bundledTop
                                );
                        }

                        this.setWireSize(0.375F, 0.25F);
                        break;
                    default:
                        if (bid != 5) {
                            return;
                        }

                        switch (meta) {
                            case 0:
                                this.setSideIcon(
                                    RedPowerWiring.powerTop,
                                    RedPowerWiring.powerFace,
                                    RedPowerWiring.powerTop
                                );
                                this.setWireSize(0.25F, 0.188F);
                                break;
                            case 1:
                                this.setSideIcon(
                                    RedPowerWiring.highPowerTop,
                                    RedPowerWiring.highPowerFace,
                                    RedPowerWiring.highPowerTop
                                );
                                this.setWireSize(0.375F, 0.25F);
                                break;
                            case 2:
                                this.setSideIconJumbo(
                                    RedPowerWiring.jumboSides,
                                    RedPowerWiring.jumboTop,
                                    RedPowerWiring.jumboCent,
                                    RedPowerWiring.jumboCentSide,
                                    RedPowerWiring.jumboEnd,
                                    RedPowerWiring.jumboCorners
                                );
                                this.setWireSize(0.5F, 0.3125F);
                        }
                }

                super.context.useNormal = true;
                tess.startDrawingQuads();
                this.renderSideWires(127, 0, 0);
                tess.draw();
                super.context.useNormal = false;
                return;
            case 18:
            case 19:
            case 20:
            case 35:
            case 36:
            case 37:
            case 38:
                switch (bid) {
                    case 18:
                        th = 0.063F;
                        break;
                    case 19:
                        th = 0.125F;
                        break;
                    case 20:
                        th = 0.25F;
                        break;
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    default:
                        return;
                    case 35:
                        th = 0.188F;
                        break;
                    case 36:
                        th = 0.313F;
                        break;
                    case 37:
                        th = 0.375F;
                        break;
                    case 38:
                        th = 0.438F;
                }

                super.context.setIcon(CoverRenderer.coverIcons[meta]);
                super.context.setSize(
                    (double) (0.5F - th),
                    (double) (0.5F - th),
                    (double) (0.5F - th),
                    (double) (0.5F + th),
                    (double) (0.5F + th),
                    (double) (0.5F + th)
                );
                super.context.calcBounds();
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.renderFaces(63);
                super.context.useNormal = false;
                tess.draw();
                return;
            case 21:
            case 22:
            case 23:
            case 39:
            case 40:
            case 41:
            case 42:
                switch (bid) {
                    case 21:
                        th = 0.063F;
                        break;
                    case 22:
                        th = 0.125F;
                        break;
                    case 23:
                        th = 0.25F;
                        break;
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    default:
                        return;
                    case 39:
                        th = 0.188F;
                        break;
                    case 40:
                        th = 0.313F;
                        break;
                    case 41:
                        th = 0.375F;
                        break;
                    case 42:
                        th = 0.438F;
                }

                super.context.setIcon(CoverRenderer.coverIcons[meta]);
                super.context.setSize(
                    (double) (0.5F - th),
                    0.0,
                    (double) (0.5F - th),
                    (double) (0.5F + th),
                    1.0,
                    (double) (0.5F + th)
                );
                super.context.calcBounds();
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.renderFaces(63);
                super.context.useNormal = false;
                tess.draw();
                return;
            case 24:
            case 25:
            case 26:
            case 31:
            case 32:
            case 33:
            case 34:
                switch (bid) {
                    case 24:
                        th = 0.063F;
                        break;
                    case 25:
                        th = 0.125F;
                        break;
                    case 26:
                        th = 0.25F;
                        break;
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    default:
                        return;
                    case 31:
                        th = 0.188F;
                        break;
                    case 32:
                        th = 0.313F;
                        break;
                    case 33:
                        th = 0.375F;
                        break;
                    case 34:
                        th = 0.438F;
                }

                super.context.setIcon(CoverRenderer.coverIcons[meta]);
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.renderBox(
                    63, 0.0, 0.0, (double) (0.5F - th), 0.25, 1.0, (double) (0.5F + th)
                );
                super.context.renderBox(
                    63, 0.75, 0.0, (double) (0.5F - th), 1.0, 1.0, (double) (0.5F + th)
                );
                super.context.renderBox(
                    15, 0.25, 0.0, (double) (0.5F - th), 0.75, 0.25, (double) (0.5F + th)
                );
                super.context.renderBox(
                    15, 0.25, 0.75, (double) (0.5F - th), 0.75, 1.0, (double) (0.5F + th)
                );
                super.context.useNormal = false;
                tess.draw();
                return;
            case 43:
            case 44:
            case 45:
                switch (bid) {
                    case 43:
                        th = 0.125F;
                        break;
                    case 44:
                        th = 0.25F;
                        break;
                    case 45:
                        th = 0.375F;
                        break;
                    default:
                        return;
                }

                super.context.setIcon(CoverRenderer.coverIcons[meta]);
                super.context.setSize(
                    (double) (0.5F - th),
                    0.125,
                    (double) (0.5F - th),
                    (double) (0.5F + th),
                    0.875,
                    (double) (0.5F + th)
                );
                super.context.calcBounds();
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.renderFaces(63);
                super.context.setSize(
                    (double) (0.45F - th),
                    0.0,
                    (double) (0.45F - th),
                    (double) (0.55F + th),
                    0.125,
                    (double) (0.55F + th)
                );
                super.context.calcBounds();
                super.context.renderFaces(63);
                super.context.setSize(
                    (double) (0.45F - th),
                    0.875,
                    (double) (0.45F - th),
                    (double) (0.55F + th),
                    1.0,
                    (double) (0.55F + th)
                );
                super.context.calcBounds();
                super.context.renderFaces(63);
                super.context.useNormal = false;
                tess.draw();
                return;
            case 64:
            case 65:
            case 66:
                super.context.setIcon(CoverRenderer.coverIcons[meta]);
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.renderBox(60, 0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
                super.context.renderBox(15, 0.0, 0.25, 0.25, 1.0, 0.75, 0.75);
                super.context.renderBox(51, 0.25, 0.25, 0.0, 0.75, 0.75, 1.0);
                tess.draw();
                tess.startDrawingQuads();
                switch (bid) {
                    case 64:
                        super.context.setIcon(RedPowerWiring.redwireCableOff);
                        break;
                    case 66:
                        super.context.setIcon(RedPowerWiring.bluewireCable);
                        break;
                    default:
                        super.context.setIcon(RedPowerWiring.bundledCable);
                }

                super.context.renderBox(3, 0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
                super.context.renderBox(48, 0.0, 0.25, 0.25, 1.0, 0.75, 0.75);
                super.context.renderBox(12, 0.25, 0.25, 0.0, 0.75, 0.75, 1.0);
                tess.draw();
                super.context.useNormal = false;
        }
    }
}
