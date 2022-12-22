package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSimpleCovered extends RenderCovers {
   public RenderSimpleCovered() {
      super(null);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileCovered covered = (TileCovered)tile;
      World world = covered.getWorldObj();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      super.context.bindBlockTexture();
      super.context.setBrightness(this.getMixedBrightness(covered));
      super.context.setTexFlags(55);
      super.context.setPos(x, y, z);
      tess.startDrawingQuads();
      if (covered.CoverSides > 0) {
         super.context.setTint(1.0F, 1.0F, 1.0F);
         super.context.readGlobalLights(world, covered.xCoord, covered.yCoord, covered.zCoord);
         this.renderCovers(covered.CoverSides, covered.Covers);
         super.context.forceFlat = false;
         super.context.lockTexture = false;
      }

      tess.draw();
      GL11.glEnable(2896);
   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
   }
}
