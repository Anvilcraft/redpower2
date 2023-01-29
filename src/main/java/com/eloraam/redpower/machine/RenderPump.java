package com.eloraam.redpower.machine;

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
public class RenderPump extends RenderCustomBlock {
    private RenderModel modelBase = RenderModel.loadModel("rpmachine:models/pump1.obj");
    private RenderModel modelSlide = RenderModel.loadModel("rpmachine:models/pump2.obj");
    private ResourceLocation modelRes
        = new ResourceLocation("rpmachine", "models/machine1.png");
    private RenderContext context = new RenderContext();
    private float lastPumpTick;

    public RenderPump(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TilePump pump = (TilePump) tile;
        World world = pump.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        this.context.setDefaults();
        this.context.setPos(x, y, z);
        this.context.setOrientation(0, pump.Rotation);
        this.context.readGlobalLights(world, pump.xCoord, pump.yCoord, pump.zCoord);
        this.context.setBrightness(this.getMixedBrightness(pump));
        this.context.bindTexture(this.modelRes);
        tess.startDrawingQuads();
        this.context.bindModelOffset(this.modelBase, 0.5, 0.5, 0.5);
        this.context.renderModelGroup(0, 0);
        this.context.renderModelGroup(1, pump.Charged ? (pump.Active ? 3 : 2) : 1);
        tess.draw();
        int lv = world.getLightBrightnessForSkyBlocks(
            pump.xCoord, pump.yCoord, pump.zCoord, 0
        );
        this.context.bindTexture(this.modelRes);
        tess.startDrawingQuads();
        tess.setBrightness(lv);
        float pumpTick = 0.0F;
        if (pump.Active) {
            pumpTick += (float) pump.PumpTick;
            if (pumpTick > 8.0F) {
                pumpTick = 16.0F - pumpTick;
            }

            pumpTick = (float) ((double) pumpTick / 8.0);
        }

        this.lastPumpTick = pumpTick;
        this.context.useNormal = true;
        this.context.setPos(x, y, z);
        this.context.setOrientation(0, pump.Rotation);
        float mod = this.lastPumpTick + (pumpTick - this.lastPumpTick) * partialTicks;
        this.context.setRelPos(0.375 + 0.3125 * (double) mod, 0.0, 0.0);
        this.context.bindModelOffset(this.modelSlide, 0.5, 0.5, 0.5);
        this.context.renderModelGroup(0, 0);
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
        this.context.bindModelOffset(this.modelBase, 0.5, 0.5, 0.5);
        this.context.renderModelGroup(0, 0);
        this.context.renderModelGroup(1, 1);
        this.context.setRelPos(0.375, 0.0, 0.0);
        this.context.bindModelOffset(this.modelSlide, 0.5, 0.5, 0.5);
        this.context.renderModelGroup(0, 0);
        this.context.useNormal = false;
        tess.draw();
    }
}
