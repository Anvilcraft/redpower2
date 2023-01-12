package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSolarPanel extends RenderCustomBlock {
    protected RenderContext context = new RenderContext();

    public RenderSolarPanel(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileSolarPanel solarPanel = (TileSolarPanel) tile;
        World world = solarPanel.getWorldObj();
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(2896);
        this.context.bindBlockTexture();
        this.context.setDefaults();
        this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        this.context.setPos(x, y, z);
        this.context.readGlobalLights(
            world, solarPanel.xCoord, solarPanel.yCoord, solarPanel.zCoord
        );
        this.context.setIcon(
            RedPowerMachine.electronicsBottom,
            RedPowerMachine.solarPanelTop,
            RedPowerMachine.solarPanelSide,
            RedPowerMachine.solarPanelSide,
            RedPowerMachine.solarPanelSide,
            RedPowerMachine.solarPanelSide
        );
        this.context.setSize(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
        this.context.setupBox();
        this.context.transform();
        tess.startDrawingQuads();
        this.context.renderGlobFaces(62);
        tess.draw();
        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        super.block.setBlockBoundsForItemRender();
        this.context.setDefaults();
        if (type == ItemRenderType.INVENTORY) {
            this.context.setPos(-0.5, -0.5, -0.5);
        } else {
            this.context.setPos(0.0, 0.0, 0.0);
        }

        this.context.useNormal = true;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        this.context.setIcon(
            RedPowerMachine.electronicsBottom,
            RedPowerMachine.solarPanelTop,
            RedPowerMachine.solarPanelSide,
            RedPowerMachine.solarPanelSide,
            RedPowerMachine.solarPanelSide,
            RedPowerMachine.solarPanelSide
        );
        this.context.renderBox(62, 0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
        tess.draw();
        this.context.useNormal = false;
    }
}
