package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.TubeLib;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFrameTube extends RenderTube {
    public RenderFrameTube(Block block) {
        super(block);
    }

    @Override
    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileFrameTube frameTube = (TileFrameTube) tile;
        World world = frameTube.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        int lv = world.getLightBrightnessForSkyBlocks(
            frameTube.xCoord, frameTube.yCoord, frameTube.zCoord, 0
        );
        tess.setBrightness(lv);
        super.context.bindBlockTexture();
        super.context.setDefaults();
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setPos(x, y, z);
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.readGlobalLights(
            world, frameTube.xCoord, frameTube.yCoord, frameTube.zCoord
        );
        tess.startDrawingQuads();
        if (frameTube.CoverSides > 0) {
            short[] sides = new short[6];

            for (int ps = 0; ps < 6; ++ps) {
                sides[ps] = frameTube.Covers[ps];
                int pc = sides[ps] >> 8;
                if (pc == 1 || pc == 4) {
                    sides[ps] = (short) (sides[ps] - 256);
                }
            }

            super.coverRenderer.render(frameTube.CoverSides, sides);
        }

        int conn = TubeLib.getConnections(
            world, frameTube.xCoord, frameTube.yCoord, frameTube.zCoord
        );
        super.context.exactTextureCoordinates = true;
        super.context.setIcon(RedPowerMachine.frameCovered);
        int sides = frameTube.CoverSides | conn;

        for (int ps = 0; ps < 6; ++ps) {
            int pc = 1 << ps;
            IIcon icon = RedPowerMachine.frameCrossed;
            super.coverRenderer.start();
            if ((sides & pc) > 0) {
                if ((frameTube.StickySides & pc) > 0) {
                    icon = RedPowerMachine.framePaneled;
                } else {
                    icon = RedPowerMachine.frameCovered;
                }
            } else {
                pc |= 1 << (ps ^ 1);
                super.context.setIconNum(ps ^ 1, RedPowerMachine.frameCrossed);
            }

            super.context.setIconNum(ps, icon);
            super.coverRenderer.setSize(ps, 0.0625F);
            super.context.calcBoundsGlobal();
            super.context.renderGlobFaces(pc);
        }

        super.context.exactTextureCoordinates = false;
        super.context.setBrightness(this.getMixedBrightness(frameTube));
        this.renderCenterBlock(
            conn, RedPowerMachine.baseTubeSide, RedPowerMachine.baseTubeFace
        );
        if (frameTube.paintColor > 0) {
            int pcolor = super.paintColors[frameTube.paintColor - 1];
            super.context.setTint(
                (float) (pcolor >> 16) / 255.0F,
                (float) (pcolor >> 8 & 0xFF) / 255.0F,
                (float) (pcolor & 0xFF) / 255.0F
            );
            this.renderBlockPaint(
                conn,
                RedPowerMachine.baseTubeFaceColor,
                RedPowerMachine.baseTubeSideColor,
                frameTube.getBlockMetadata()
            );
        }

        tess.draw();
        super.item.worldObj = world;
        super.item.setPosition(x + 0.5, y + 0.5, z + 0.5);
        RenderItem renderitem = (RenderItem
        ) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
        super.item.age = 0;
        super.item.hoverStart = 0.0F;
        WorldCoord offset = new WorldCoord(0, 0, 0);
        TubeFlow flow = frameTube.getTubeFlow();

        for (TubeItem item : flow.contents) {
            super.item.setEntityItemStack(item.item);
            offset.x = 0;
            offset.y = 0;
            offset.z = 0;
            offset.step(item.side);
            double d = (double) item.progress / 128.0 * 0.5;
            if (!item.scheduled) {
                d = 0.5 - d;
            }

            double yo = 0.0;
            if (Item.getIdFromItem(item.item.getItem()) >= 256) {
                yo += 0.1;
            }

            renderitem.doRender(
                super.item,
                x + 0.5 + (double) offset.x * d,
                y + 0.5 - (double) super.item.yOffset - yo + (double) offset.y * d,
                z + 0.5 + (double) offset.z * d,
                0.0F,
                0.0F
            );
            if (item.color > 0) {
                tess.startDrawingQuads();
                super.context.useNormal = true;
                super.context.setDefaults();
                super.context.setBrightness(lv);
                super.context.setPos(
                    x + (double) offset.x * d,
                    y + (double) offset.y * d,
                    z + (double) offset.z * d
                );
                super.context.setTintHex(super.paintColors[item.color - 1]);
                super.context.setIcon(RedPowerMachine.tubeItemOverlay);
                super.context.renderBox(63, 0.26F, 0.26F, 0.26F, 0.74F, 0.74F, 0.74F);
                tess.draw();
            }
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
        super.context.setIcon(
            RedPowerMachine.frameCovered,
            RedPowerMachine.frameCovered,
            RedPowerMachine.frameCrossed,
            RedPowerMachine.frameCrossed,
            RedPowerMachine.frameCrossed,
            RedPowerMachine.frameCrossed
        );
        this.doubleBox(63, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.01F);
        super.context.setIcon(
            RedPowerMachine.baseTubeFace,
            RedPowerMachine.baseTubeFace,
            RedPowerMachine.baseTubeSide,
            RedPowerMachine.baseTubeSide,
            RedPowerMachine.baseTubeSide,
            RedPowerMachine.baseTubeSide
        );
        super.context.renderBox(63, 0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
        super.context.renderBox(63, 0.74F, 0.99F, 0.74F, 0.26F, 0.01F, 0.26F);
        tess.draw();
        super.context.useNormal = false;
    }

    private void doubleBox(
        int sides, float x1, float y1, float z1, float x2, float y2, float z2, float ino
    ) {
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
            (double) (x2 - ino),
            (double) (y2 - ino),
            (double) (z2 - ino),
            (double) (x1 + ino),
            (double) (y1 + ino),
            (double) (z1 + ino)
        );
    }
}
