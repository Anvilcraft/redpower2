package com.eloraam.redpower.base;

import java.util.Random;

import com.eloraam.redpower.RedPowerBase;
import com.eloraam.redpower.core.CoreLib;
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
public class RenderAlloyFurnace extends RenderCustomBlock {
    protected RenderContext context = new RenderContext();

    public RenderAlloyFurnace(Block block) {
        super(block);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileAlloyFurnace tb
            = CoreLib.getTileEntity(world, x, y, z, TileAlloyFurnace.class);
        if (tb != null && tb.Active) {
            float f = (float) x + 0.5F;
            float f1 = (float) y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
            float f2 = (float) z + 0.5F;
            float f3 = 0.52F;
            float f4 = random.nextFloat() * 0.6F - 0.3F;
            switch (tb.Rotation) {
                case 0:
                    world.spawnParticle(
                        "smoke",
                        (double) (f + f4),
                        (double) f1,
                        (double) (f2 - f3),
                        0.0,
                        0.0,
                        0.0
                    );
                    world.spawnParticle(
                        "flame",
                        (double) (f + f4),
                        (double) f1,
                        (double) (f2 - f3),
                        0.0,
                        0.0,
                        0.0
                    );
                    break;
                case 1:
                    world.spawnParticle(
                        "smoke",
                        (double) (f + f3),
                        (double) f1,
                        (double) (f2 + f4),
                        0.0,
                        0.0,
                        0.0
                    );
                    world.spawnParticle(
                        "flame",
                        (double) (f + f3),
                        (double) f1,
                        (double) (f2 + f4),
                        0.0,
                        0.0,
                        0.0
                    );
                    break;
                case 2:
                    world.spawnParticle(
                        "smoke",
                        (double) (f + f4),
                        (double) f1,
                        (double) (f2 + f3),
                        0.0,
                        0.0,
                        0.0
                    );
                    world.spawnParticle(
                        "flame",
                        (double) (f + f4),
                        (double) f1,
                        (double) (f2 + f3),
                        0.0,
                        0.0,
                        0.0
                    );
                    break;
                case 3:
                    world.spawnParticle(
                        "smoke",
                        (double) (f - f3),
                        (double) f1,
                        (double) (f2 + f4),
                        0.0,
                        0.0,
                        0.0
                    );
                    world.spawnParticle(
                        "flame",
                        (double) (f - f3),
                        (double) f1,
                        (double) (f2 + f4),
                        0.0,
                        0.0,
                        0.0
                    );
            }
        }
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileAlloyFurnace alloyFurnace = (TileAlloyFurnace) tile;
        World world = alloyFurnace.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        this.context.bindBlockTexture();
        this.context.setDefaults();
        this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        this.context.setPos(x, y, z);
        this.context.readGlobalLights(
            world, alloyFurnace.xCoord, alloyFurnace.yCoord, alloyFurnace.zCoord
        );
        this.context.setIcon(
            RedPowerBase.alloyFurnaceVert,
            RedPowerBase.alloyFurnaceVert,
            alloyFurnace.Active ? RedPowerBase.alloyFurnaceFrontOn
                                : RedPowerBase.alloyFurnaceFront,
            RedPowerBase.alloyFurnaceSide,
            RedPowerBase.alloyFurnaceSide,
            RedPowerBase.alloyFurnaceSide
        );
        this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.context.setupBox();
        this.context.transform();
        this.context.rotateTextures(alloyFurnace.Rotation);
        tess.startDrawingQuads();
        this.context.renderGlobFaces(63);
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
            RedPowerBase.alloyFurnaceVert,
            RedPowerBase.alloyFurnaceVert,
            RedPowerBase.alloyFurnaceSide,
            RedPowerBase.alloyFurnaceSide,
            RedPowerBase.alloyFurnaceSide,
            RedPowerBase.alloyFurnaceFront
        );
        this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        tess.draw();
        this.context.useNormal = false;
    }
}
