package com.eloraam.redpower.logic;

import com.eloraam.redpower.core.MathLib;
import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.RenderLib;
import com.eloraam.redpower.core.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderLogicStorage extends RenderLogic {
    private static RenderLogic.TorchPos[] torchMapCounter
        = new RenderLogic.TorchPos[] { new RenderLogic.TorchPos(0.0, 0.125, 0.188, 1.0),
                                       new RenderLogic.TorchPos(0.3, -0.3, 0.0, 0.6F),
                                       new RenderLogic.TorchPos(-0.3, -0.3, 0.0, 0.6F) };

    public RenderLogicStorage(Block block) {
        super(block);
    }

    @Override
    protected int getTorchState(TileLogic tileLogic) {
        TileLogicStorage tls = (TileLogicStorage) tileLogic;
        int md = tileLogic.getExtendedMetadata();
        switch (md) {
            case 0:
                TileLogicStorage.LogicStorageCounter lsc
                    = (TileLogicStorage.LogicStorageCounter
                    ) tls.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
                return 1 | (lsc.Count == lsc.CountMax ? 2 : 0) | (lsc.Count == 0 ? 4 : 0);
            default:
                return 0;
        }
    }

    @Override
    protected int getInvTorchState(int metadata) {
        switch (metadata) {
            case 768:
                return 5;
            default:
                return 0;
        }
    }

    @Override
    protected RenderLogic.TorchPos[] getTorchVectors(TileLogic tileLogic) {
        int md = tileLogic.getExtendedMetadata();
        switch (md) {
            case 0:
                return torchMapCounter;
            default:
                return null;
        }
    }

    @Override
    protected RenderLogic.TorchPos[] getInvTorchVectors(int metadata) {
        switch (metadata) {
            case 768:
                return torchMapCounter;
            default:
                return null;
        }
    }

    @Override
    protected void renderWorldPart(
        IBlockAccess iba,
        TileLogic tileLogic,
        double x,
        double y,
        double z,
        float partialTicks
    ) {
        int md = tileLogic.getExtendedMetadata();
        TileLogicStorage tls = (TileLogicStorage) tileLogic;
        switch (md) {
            case 0:
                int tx = 224 + (tileLogic.Deadmap > 0 ? 4 : 0)
                    + (tileLogic.PowerState & 1) + ((tileLogic.PowerState & 4) >> 1);
                this.renderWafer(tx);
                TileLogicStorage.LogicStorageCounter lsc
                    = (TileLogicStorage.LogicStorageCounter
                    ) tls.getLogicStorage(TileLogicStorage.LogicStorageCounter.class);
                if (lsc.CountMax == 0) {
                    lsc.CountMax = 1;
                }

                float dir = 0.58F + 0.34F * ((float) lsc.Count / (float) lsc.CountMax);
                Vector3 pos = new Vector3(0.0, -0.1, 0.188);
                super.context.basis.rotate(pos);
                pos.add(super.context.globalOrigin);
                pos.add(0.5, 0.5, 0.5);
                Quat q = Quat.aroundAxis(0.0, 1.0, 0.0, (double) (-dir) * Math.PI * 2.0);
                q.multiply(
                    MathLib.orientQuat(tileLogic.Rotation >> 2, tileLogic.Rotation & 3)
                );
                RenderLib.renderPointer(pos, q);
        }
    }

    @Override
    protected void renderInvPart(int metadata) {
        switch (metadata) {
            case 768:
                this.renderInvWafer(224);
                Tessellator tess = Tessellator.instance;
                tess.startDrawingQuads();
                tess.setNormal(0.0F, 0.0F, 1.0F);
                Vector3 v = new Vector3(0.0, -0.1, 0.188);
                Quat q = Quat.aroundAxis(0.0, 1.0, 0.0, 3.64424747816416);
                super.context.basis.rotate(v);
                q.multiply(MathLib.orientQuat(0, 1));
                RenderLib.renderPointer(v, q);
                tess.draw();
        }
    }
}
