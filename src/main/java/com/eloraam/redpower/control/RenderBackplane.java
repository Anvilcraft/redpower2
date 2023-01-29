package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.RedPowerCore;
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
public class RenderBackplane extends RenderCustomBlock {
    private RenderContext context = new RenderContext();

    public RenderBackplane(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileBackplane backplane = (TileBackplane) tile;
        World world = backplane.getWorldObj();
        int metadata = backplane.getBlockMetadata();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        this.context.setDefaults();
        this.context.bindBlockTexture();
        this.context.setBrightness(this.getMixedBrightness(backplane));
        this.context.setOrientation(0, backplane.Rotation);
        this.context.setPos(x, y, z);
        this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        tess.startDrawingQuads();
        if (metadata == 0) {
            this.context.setIcon(
                RedPowerCore.missing,
                RedPowerControl.backplaneTop,
                RedPowerControl.backplaneFace,
                RedPowerControl.backplaneFace,
                RedPowerControl.backplaneSide,
                RedPowerControl.backplaneSide
            );
            this.context.renderBox(62, 0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        } else if (metadata == 1) {
            this.context.setIcon(
                RedPowerCore.missing,
                RedPowerControl.ram8kTop,
                RedPowerControl.ram8kFace,
                RedPowerControl.ram8kFace,
                RedPowerControl.ram8kSide,
                RedPowerControl.ram8kSide
            );
            this.context.renderBox(62, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        tess.draw();
        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int meta = item.getItemDamage();
        Tessellator tess = Tessellator.instance;
        super.block.setBlockBoundsForItemRender();
        this.context.setDefaults();
        this.context.useNormal = true;
        this.context.setOrientation(0, 3);
        if (type == ItemRenderType.INVENTORY) {
            this.context.setPos(-0.5, -0.5, -0.5);
        } else {
            this.context.setPos(0.0, 0.0, 0.0);
        }

        this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        tess.startDrawingQuads();
        if (meta == 0) {
            this.context.setIcon(
                RedPowerCore.missing,
                RedPowerControl.backplaneTop,
                RedPowerControl.backplaneFace,
                RedPowerControl.backplaneFace,
                RedPowerControl.backplaneSide,
                RedPowerControl.backplaneSide
            );
            this.context.renderBox(62, 0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        } else if (meta == 1) {
            this.context.setIcon(
                RedPowerCore.missing,
                RedPowerControl.ram8kTop,
                RedPowerControl.ram8kFace,
                RedPowerControl.ram8kFace,
                RedPowerControl.ram8kSide,
                RedPowerControl.ram8kSide
            );
            this.context.renderBox(62, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        tess.draw();
        this.context.useNormal = false;
    }
}
