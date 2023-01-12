package com.eloraam.redpower.control;

import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderIOExpander extends RenderCustomBlock {
    private RenderContext context = new RenderContext();
    private RenderModel modelModem = RenderModel.loadModel("rpcontrol:models/modem.obj");
    private ResourceLocation modelRes
        = new ResourceLocation("rpcontrol", "models/modem.png");

    public RenderIOExpander(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileIOExpander iox = (TileIOExpander) tile;
        World world = iox.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        this.context.setDefaults();
        this.context.setPos(x, y, z);
        this.context.setOrientation(0, iox.Rotation);
        this.context.readGlobalLights(world, iox.xCoord, iox.yCoord, iox.zCoord);
        this.context.setBrightness(this.getMixedBrightness(iox));
        this.context.bindTexture(this.modelRes);
        tess.startDrawingQuads();
        this.context.bindModelOffset(this.modelModem, 0.5, 0.5, 0.5);
        this.context.renderModelGroup(1, 1 + (CoreLib.rotToSide(iox.Rotation) & 1));
        this.context.renderModelGroup(2, 1 + (iox.WBuf & 15));
        this.context.renderModelGroup(3, 1 + (iox.WBuf >> 4 & 15));
        this.context.renderModelGroup(4, 1 + (iox.WBuf >> 8 & 15));
        this.context.renderModelGroup(5, 1 + (iox.WBuf >> 12 & 15));
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

        this.context.bindTexture(this.modelRes);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        this.context.useNormal = true;
        this.context.setOrientation(0, 3);
        this.context.bindModelOffset(this.modelModem, 0.5, 0.5, 0.5);
        this.context.renderModelGroup(1, 1);
        this.context.renderModelGroup(2, 1);
        this.context.renderModelGroup(3, 1);
        this.context.renderModelGroup(4, 1);
        this.context.renderModelGroup(5, 1);
        this.context.useNormal = false;
        tess.draw();
    }
}
