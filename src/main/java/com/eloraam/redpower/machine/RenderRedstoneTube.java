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
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderRedstoneTube extends RenderTube {
    public RenderRedstoneTube(Block block) {
        super(block);
    }

    @Override
    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileRedstoneTube redstoneTube = (TileRedstoneTube) tile;
        World world = redstoneTube.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        int lv = world.getLightBrightnessForSkyBlocks(
            redstoneTube.xCoord, redstoneTube.yCoord, redstoneTube.zCoord, 0
        );
        tess.setBrightness(lv);
        tess.startDrawingQuads();
        super.context.bindBlockTexture();
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setPos(x, y, z);
        if (redstoneTube.CoverSides > 0) {
            super.context.readGlobalLights(
                world, redstoneTube.xCoord, redstoneTube.yCoord, redstoneTube.zCoord
            );
            this.renderCovers(redstoneTube.CoverSides, redstoneTube.Covers);
        }

        int cons1
            = TubeLib.getConnections(
                  world, redstoneTube.xCoord, redstoneTube.yCoord, redstoneTube.zCoord
              )
            | redstoneTube.getConnectionMask() >> 24;
        super.context.setBrightness(this.getMixedBrightness(redstoneTube));
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.setPos(x, y, z);
        int ps = (redstoneTube.PowerState + 84) / 85;
        this.renderCenterBlock(
            cons1,
            RedPowerMachine.redstoneTubeSide[ps],
            RedPowerMachine.redstoneTubeFace[ps]
        );
        if (redstoneTube.paintColor > 0) {
            int tc = super.paintColors[redstoneTube.paintColor - 1];
            super.context.setTint(
                (float) (tc >> 16) / 255.0F,
                (float) (tc >> 8 & 0xFF) / 255.0F,
                (float) (tc & 0xFF) / 255.0F
            );
            this.renderBlockPaint(
                cons1,
                RedPowerMachine.baseTubeFaceColor,
                RedPowerMachine.baseTubeSideColor,
                redstoneTube.getBlockMetadata()
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
        TubeFlow flow = redstoneTube.getTubeFlow();

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
                super.context.bindBlockTexture();
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
        super.context.useNormal = true;
        super.context.setIcon(
            RedPowerMachine.redstoneTubeFace[0],
            RedPowerMachine.redstoneTubeFace[0],
            RedPowerMachine.redstoneTubeSide[0],
            RedPowerMachine.redstoneTubeSide[0],
            RedPowerMachine.redstoneTubeSide[0],
            RedPowerMachine.redstoneTubeSide[0]
        );
        super.context.renderBox(63, 0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
        super.context.renderBox(63, 0.74F, 0.99F, 0.74F, 0.26F, 0.01F, 0.26F);
        tess.draw();
        super.context.useNormal = false;
    }
}
