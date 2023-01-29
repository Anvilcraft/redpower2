package com.eloraam.redpower.logic;

import java.util.Random;

import com.eloraam.redpower.RedPowerLogic;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.RenderCovers;
import com.eloraam.redpower.core.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderLogic extends RenderCovers {
    public RenderLogic(Block block) {
        super(block);
    }

    public void renderCovers(IBlockAccess iba, TileLogic tileLogic) {
        if (tileLogic.Cover != 255) {
            super.context.readGlobalLights(
                iba, tileLogic.xCoord, tileLogic.yCoord, tileLogic.zCoord
            );
            this.renderCover(tileLogic.Rotation, tileLogic.Cover);
        }
    }

    public TileLogic getTileEntity(IBlockAccess iba, int i, int j, int k) {
        TileEntity te = iba.getTileEntity(i, j, k);
        return !(te instanceof TileLogic) ? null : (TileLogic) te;
    }

    public void setMatrixDisplayTick(int i, int j, int k, int rot, Random random) {
        float x = (float) i + 0.5F + (random.nextFloat() - 0.5F) * 0.2F;
        float y = (float) j + 0.7F + (random.nextFloat() - 0.5F) * 0.2F;
        float z = (float) k + 0.5F + (random.nextFloat() - 0.5F) * 0.2F;
        super.context.setOrientation(0, rot);
        super.context.setPos((double) x, (double) y, (double) z);
    }

    public void setMatrixInv(ItemRenderType type) {
        super.context.setOrientation(0, 3);
        if (type == ItemRenderType.INVENTORY) {
            super.context.setPos(-0.5, -0.5, -0.5);
        } else {
            super.context.setPos(0.0, 0.0, 0.0);
        }
    }

    public void renderWafer(int tx) {
        IIcon[] icons;
        switch (tx >> 8) {
            case 1:
                icons = RedPowerLogic.logicTwo;
                break;
            case 2:
                icons = RedPowerLogic.logicSensor;
                break;
            default:
                icons = RedPowerLogic.logicOne;
        }

        super.context.setRelPos(0.0, 0.0, 0.0);
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setTexFlags(0);
        super.context.setSize(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        super.context.setIcon(
            icons[0], icons[tx & 0xFF], icons[0], icons[0], icons[0], icons[0]
        );
        super.context.calcBounds();
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.renderFaces(62);
    }

    public void renderInvWafer(int tx) {
        super.context.useNormal = true;
        IIcon[] icons;
        switch (tx >> 8) {
            case 1:
                icons = RedPowerLogic.logicTwo;
                break;
            case 2:
                icons = RedPowerLogic.logicSensor;
                break;
            default:
                icons = RedPowerLogic.logicOne;
        }

        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setTexFlags(0);
        super.context.setSize(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        super.context.setIcon(
            icons[0], icons[tx & 0xFF], icons[0], icons[0], icons[0], icons[0]
        );
        super.context.calcBounds();
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.renderFaces(63);
        tess.draw();
        super.context.useNormal = false;
    }

    public void renderCover(int rot, int cov) {
        if (cov != 255) {
            rot >>= 2;
            rot ^= 1;
            short[] rs = new short[] { 0, 0, 0, 0, 0, 0 };
            rs[rot] = (short) cov;
            super.context.setTint(1.0F, 1.0F, 1.0F);
            this.renderCovers(1 << rot, rs);
        }
    }

    public void
    renderRedstoneTorch(double x, double y, double z, double h, boolean state) {
        super.context.setTexFlags(0);
        super.context.setRelPos(x, y, z);
        super.context.setIcon(state ? RedPowerLogic.torchOn : RedPowerLogic.torch);
        super.context.setLocalLights(1.0F);
        super.context.setTint(1.0F, 1.0F, 1.0F);
        super.context.setSize(0.4375, 1.0 - h, 0.0, 0.5625, 1.0, 1.0);
        super.context.calcBounds();
        super.context.renderFaces(48);
        super.context.setSize(0.0, 1.0 - h, 0.4375, 1.0, 1.0, 0.5625);
        super.context.calcBounds();
        super.context.renderFaces(12);
        super.context.setSize(0.375, 0.0, 0.4375, 0.5, 1.0, 0.5625);
        super.context.setRelPos(x + 0.0625, y - 0.375, z);
        super.context.calcBounds();
        super.context.setTexFlags(24);
        super.context.renderFaces(2);
        super.context.setRelPos(0.0, 0.0, 0.0);
    }

    public void renderTorchPuff(World world, String name, double x, double y, double z) {
        Vector3 v = new Vector3(x, y, z);
        super.context.basis.rotate(v);
        v.add(super.context.globalOrigin);
        world.spawnParticle(name, v.x, v.y, v.z, 0.0, 0.0, 0.0);
    }

    public void renderChip(double x, double y, double z, int tex) {
        super.context.setTexFlags(0);
        super.context.setRelPos(x, y, z);
        super.context.setIcon(RedPowerLogic.logicOne[tex]);
        super.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        super.context.renderBox(62, 0.375, 0.0625, 0.375, 0.625, 0.1875, 0.625);
    }

    protected int getTorchState(TileLogic tileLogic) {
        return 0;
    }

    protected int getInvTorchState(int metadata) {
        return 0;
    }

    protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tileLogic) {
        return null;
    }

    protected RenderLogic.TorchPos[] getInvTorchVectors(int metadata) {
        return null;
    }

    protected void renderWorldPart(
        IBlockAccess iba,
        TileLogic tileLogic,
        double x,
        double y,
        double z,
        float partialTicks
    ) {}

    protected void renderInvPart(int metadata) {}

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileLogic logic = CoreLib.getTileEntity(world, x, y, z, TileLogic.class);
        if (logic != null) {
            int ts = this.getTorchState(logic);
            if (ts != 0) {
                this.setMatrixDisplayTick(x, y, z, logic.Rotation, random);
                RenderLogic.TorchPos[] tpv = this.getTorchVectors(logic);
                if (tpv != null) {
                    int rv = random.nextInt(tpv.length);
                    if ((ts & 1 << rv) != 0) {
                        this.renderTorchPuff(
                            world, "reddust", tpv[rv].x, tpv[rv].y, tpv[rv].z
                        );
                    }
                }
            }
        }
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileLogic logic = (TileLogic) tile;
        World world = logic.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        super.context.bindBlockTexture();
        super.context.setDefaults();
        super.context.setPos(x, y, z);
        tess.startDrawingQuads();
        this.renderCovers(world, logic);
        tess.draw();
        super.context.setBrightness(this.getMixedBrightness(logic));
        super.context.setOrientation(logic.Rotation >> 2, logic.Rotation & 3);
        super.context.setPos(x, y, z);
        tess.startDrawingQuads();
        this.renderWorldPart(world, logic, x, y, z, partialTicks);
        tess.draw();
        super.context.bindBlockTexture();
        int ts = this.getTorchState(logic);
        RenderLogic.TorchPos[] tpv = this.getTorchVectors(logic);
        if (tpv != null) {
            tess.startDrawingQuads();

            for (int n = 0; n < tpv.length; ++n) {
                this.renderRedstoneTorch(
                    tpv[n].x, tpv[n].y, tpv[n].z, tpv[n].h, (ts & 1 << n) > 0
                );
            }

            tess.draw();
        }

        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int meta = item.getItemDamage();
        super.block.setBlockBoundsForItemRender();
        super.context.setDefaults();
        this.setMatrixInv(type);
        this.renderInvPart(meta);
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        int ts = this.getInvTorchState(meta);
        RenderLogic.TorchPos[] tpv = this.getInvTorchVectors(meta);
        if (tpv != null) {
            for (int n = 0; n < tpv.length; ++n) {
                this.renderRedstoneTorch(
                    tpv[n].x, tpv[n].y, tpv[n].z, tpv[n].h, (ts & 1 << n) > 0
                );
            }
        }

        tess.draw();
        GL11.glEnable(2896);
    }

    @Override
    protected IIcon getParticleIconForSide(
        World world, int x, int y, int z, TileEntity tile, int side, int meta
    ) {
        return Blocks.stone_slab.getIcon(0, 0);
    }

    public static class TorchPos {
        double x;
        double y;
        double z;
        double h;

        public TorchPos(double x, double y, double z, double h) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.h = h;
        }
    }
}
