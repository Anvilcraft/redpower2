package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderShapedLamp extends RenderCustomBlock {
    private RenderContext context = new RenderContext();
    private RenderModel modelLamp1
        = RenderModel.loadModel("rplighting:models/shlamp1.obj");
    private RenderModel modelLamp2
        = RenderModel.loadModel("rplighting:models/shlamp2.obj");
    private ResourceLocation lampRes
        = new ResourceLocation("rplighting", "models/shlamp.png");

    public RenderShapedLamp(BlockShapedLamp lamp) {
        super(lamp);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileShapedLamp shapedLamp = (TileShapedLamp) tile;
        World world = shapedLamp.getWorldObj();
        GL11.glDisable(2896);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        Tessellator tess = Tessellator.instance;
        boolean lit = shapedLamp.Powered != shapedLamp.Inverted;
        this.context.setDefaults();
        this.context.setPos(x, y, z);
        this.context.setOrientation(shapedLamp.Rotation, 0);
        this.context.readGlobalLights(world, tile.xCoord, tile.yCoord, tile.zCoord);
        switch (shapedLamp.Style) {
            case 0:
                this.context.bindModelOffset(this.modelLamp1, 0.5, 0.5, 0.5);
                break;
            case 1:
                this.context.bindModelOffset(this.modelLamp2, 0.5, 0.5, 0.5);
        }

        this.context.bindTexture(this.lampRes);
        this.context.setBrightness(this.getMixedBrightness(tile));
        if (MinecraftForgeClient.getRenderPass() == 0) {
            tess.startDrawingQuads();
            this.context.renderModelGroup(0, 0);
            if (lit) {
                this.context.setTintHex(RenderLamp.lightColors[shapedLamp.Color & 15]);
                this.context.setBrightness(15728880);
            } else {
                this.context.setTintHex(RenderLamp.lightColorsOff[shapedLamp.Color & 15]);
            }

            this.context.renderModelGroup(1, 0);
            tess.draw();
        }

        if (MinecraftForgeClient.getRenderPass() == 1 && lit) {
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            GL11.glDisable(2884);
            this.context.setTintHex(RenderLamp.lightColors[shapedLamp.Color & 15]);
            this.context.setAlpha(0.3F);
            tess.startDrawingQuads();
            this.context.renderModelGroup(2, 0);
            tess.draw();
            GL11.glEnable(2884);
            GL11.glDisable(3042);
            GL11.glEnable(3553);
        }

        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int meta = item.getItemDamage();
        Tessellator tess = Tessellator.instance;
        super.block.setBlockBoundsForItemRender();
        this.context.setDefaults();
        if (type == ItemRenderType.INVENTORY) {
            this.context.setPos(-0.5, -0.5, -0.5);
        } else {
            this.context.setPos(0.0, 0.0, 0.0);
        }

        this.context.bindTexture(this.lampRes);
        tess.startDrawingQuads();
        this.context.useNormal = true;
        switch (meta >> 5) {
            case 0:
                this.context.bindModelOffset(this.modelLamp1, 0.5, 0.5, 0.5);
                break;
            case 1:
                this.context.bindModelOffset(this.modelLamp2, 0.5, 0.5, 0.5);
        }

        this.context.renderModelGroup(0, 0);
        if ((meta & 16) > 0) {
            this.context.setTintHex(RenderLamp.lightColors[meta & 15]);
        } else {
            this.context.setTintHex(RenderLamp.lightColorsOff[meta & 15]);
        }

        this.context.renderModelGroup(1, 0);
        this.context.useNormal = false;
        tess.draw();
        if ((meta & 16) > 0) {
            GL11.glBlendFunc(770, 1);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            this.context.setTintHex(RenderLamp.lightColors[meta & 15]);
            this.context.setAlpha(0.3F);
            tess.startDrawingQuads();
            this.context.renderModelGroup(2, 0);
            tess.draw();
            GL11.glDisable(3008);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glBlendFunc(770, 771);
        }
    }

    @Override
    public IIcon getParticleIconForSide(
        World world, int x, int y, int z, TileEntity tile, int side, int meta
    ) {
        if (tile instanceof TileShapedLamp) {
            TileShapedLamp lamp = (TileShapedLamp) tile;
            return lamp.Powered != lamp.Inverted ? RedPowerLighting.lampOn[lamp.Color]
                                                 : RedPowerLighting.lampOff[lamp.Color];
        } else {
            return super.getParticleIconForSide(world, x, y, z, tile, side, meta);
        }
    }

    @Override
    public int getParticleColorForSide(
        World world, int x, int y, int z, TileEntity tile, int side, int meta
    ) {
        if (tile instanceof TileShapedLamp) {
            TileShapedLamp lamp = (TileShapedLamp) tile;
            return (
                lamp.Powered != lamp.Inverted ? RenderLamp.lightColors
                                              : RenderLamp.lightColorsOff
            )[lamp.Color];
        } else {
            return super.getParticleColorForSide(world, x, y, z, tile, side, meta);
        }
    }
}
