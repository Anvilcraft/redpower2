package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
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
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderFrame extends RenderCovers {
    public RenderFrame(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileFrame frame = (TileFrame) tile;
        World world = frame.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        super.context.bindBlockTexture();
        super.context.setDefaults();
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setPos(x, y, z);
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.readGlobalLights(world, frame.xCoord, frame.yCoord, frame.zCoord);
        tess.startDrawingQuads();
        if (frame.CoverSides > 0) {
            short[] sides = new short[6];

            for (int ps = 0; ps < 6; ++ps) {
                sides[ps] = frame.Covers[ps];
                int tx = sides[ps] >> 8;
                if (tx == 1 || tx == 4) {
                    sides[ps] = (short) (sides[ps] - 256);
                }
            }

            super.coverRenderer.render(frame.CoverSides, sides);
        }

        super.context.exactTextureCoordinates = true;
        super.context.setIcon(RedPowerMachine.frameCovered);

        for (int ps = 0; ps < 6; ++ps) {
            int pc = 1 << ps;
            IIcon icon = RedPowerMachine.frameCrossed;
            super.coverRenderer.start();
            if ((frame.CoverSides & pc) > 0) {
                if ((frame.StickySides & pc) > 0) {
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

        tess.draw();
        super.context.exactTextureCoordinates = false;
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
        super.context.setIcon(RedPowerMachine.frameCrossed);
        this.doubleBox(63, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.01F);
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
