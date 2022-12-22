package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.TileCovered;
import com.eloraam.redpower.wiring.RenderWiring;
import com.eloraam.redpower.wiring.TileWiring;
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
public class RenderRibbon extends RenderWiring {
   public RenderRibbon(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileCovered covered = (TileCovered)tile;
      World world = covered.getWorldObj();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      super.context.bindBlockTexture();
      super.context.setBrightness(this.getMixedBrightness(covered));
      super.context.setPos(x, y, z);
      if (covered.CoverSides > 0) {
         super.context.setTint(1.0F, 1.0F, 1.0F);
         super.context.readGlobalLights(world, covered.xCoord, covered.xCoord, covered.zCoord);
         this.renderCovers(covered.CoverSides, covered.Covers);
      }

      TileWiring tw = (TileWiring)covered;
      int indcon = tw.getExtConnectionMask();
      int cons = tw.getConnectionMask() | indcon;
      int indconex = tw.EConEMask;
      super.context.setTint(1.0F, 1.0F, 1.0F);
      this.setSideIcon(RedPowerControl.ribbonTop, RedPowerControl.ribbonFace, RedPowerControl.ribbonTop);
      this.setWireSize(0.5F, 0.0625F);
      this.renderWireBlock(tw.ConSides, cons, indcon, indconex);
      tess.draw();
      GL11.glEnable(2896);
   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      Tessellator tess = Tessellator.instance;
      super.block.setBlockBoundsForItemRender();
      super.context.setDefaults();
      super.context.setTexFlags(55);
      if (type == ItemRenderType.INVENTORY) {
         super.context.setPos(-0.5, -0.2F, -0.5);
      } else {
         super.context.setPos(0.0, 0.29999999701976776, 0.0);
      }

      this.setSideIcon(RedPowerControl.ribbonTop, RedPowerControl.ribbonFace, RedPowerControl.ribbonTop);
      this.setWireSize(0.5F, 0.0625F);
      super.context.useNormal = true;
      tess.startDrawingQuads();
      this.renderSideWires(127, 0, 0);
      tess.draw();
      super.context.useNormal = false;
   }
}
