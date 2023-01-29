package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityCustomDiggingFX extends EntityFX {
    public EntityCustomDiggingFX(
        World world,
        double x,
        double y,
        double z,
        double mx,
        double my,
        double mz,
        IIcon icon,
        int color
    ) {
        super(world, x, y, z, mx, my, mz);
        this.setParticleIcon(icon);
        super.particleGravity = 1.0F;
        super.particleRed = super.particleGreen = super.particleBlue = 0.6F;
        super.particleScale /= 2.0F;
        super.particleRed *= (float) (color >> 16 & 0xFF) / 255.0F;
        super.particleGreen *= (float) (color >> 8 & 0xFF) / 255.0F;
        super.particleBlue *= (float) (color & 0xFF) / 255.0F;
    }

    public int getFXLayer() {
        return 1;
    }

    public void renderParticle(
        Tessellator tess,
        float p_70539_2_,
        float p_70539_3_,
        float p_70539_4_,
        float p_70539_5_,
        float p_70539_6_,
        float p_70539_7_
    ) {
        float f6
            = ((float) super.particleTextureIndexX + super.particleTextureJitterX / 4.0F)
            / 16.0F;
        float f7 = f6 + 0.015609375F;
        float f8
            = ((float) super.particleTextureIndexY + super.particleTextureJitterY / 4.0F)
            / 16.0F;
        float f9 = f8 + 0.015609375F;
        float f10 = 0.1F * super.particleScale;
        if (super.particleIcon != null) {
            f6 = super.particleIcon.getInterpolatedU((double
            ) (super.particleTextureJitterX / 4.0F * 16.0F));
            f7 = super.particleIcon.getInterpolatedU((double
            ) ((super.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            f8 = super.particleIcon.getInterpolatedV((double
            ) (super.particleTextureJitterY / 4.0F * 16.0F));
            f9 = super.particleIcon.getInterpolatedV((double
            ) ((super.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        float f11 = (float
        ) (super.prevPosX + (super.posX - super.prevPosX) * (double) p_70539_2_
           - EntityFX.interpPosX);
        float f12 = (float
        ) (super.prevPosY + (super.posY - super.prevPosY) * (double) p_70539_2_
           - EntityFX.interpPosY);
        float f13 = (float
        ) (super.prevPosZ + (super.posZ - super.prevPosZ) * (double) p_70539_2_
           - EntityFX.interpPosZ);
        tess.setColorOpaque_F(super.particleRed, super.particleGreen, super.particleBlue);
        tess.addVertexWithUV(
            (double) (f11 - p_70539_3_ * f10 - p_70539_6_ * f10),
            (double) (f12 - p_70539_4_ * f10),
            (double) (f13 - p_70539_5_ * f10 - p_70539_7_ * f10),
            (double) f6,
            (double) f9
        );
        tess.addVertexWithUV(
            (double) (f11 - p_70539_3_ * f10 + p_70539_6_ * f10),
            (double) (f12 + p_70539_4_ * f10),
            (double) (f13 - p_70539_5_ * f10 + p_70539_7_ * f10),
            (double) f6,
            (double) f8
        );
        tess.addVertexWithUV(
            (double) (f11 + p_70539_3_ * f10 + p_70539_6_ * f10),
            (double) (f12 + p_70539_4_ * f10),
            (double) (f13 + p_70539_5_ * f10 + p_70539_7_ * f10),
            (double) f7,
            (double) f8
        );
        tess.addVertexWithUV(
            (double) (f11 + p_70539_3_ * f10 - p_70539_6_ * f10),
            (double) (f12 - p_70539_4_ * f10),
            (double) (f13 + p_70539_5_ * f10 - p_70539_7_ * f10),
            (double) f7,
            (double) f9
        );
    }
}
