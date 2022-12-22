package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.Matrix3;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.WorldCoord;
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
public class RenderWindTurbine extends RenderCustomBlock {
   private RenderContext turbineContext = new RenderContext();
   private RenderContext bladesContext = new RenderContext();
   private RenderModel modelWoodTurbine = RenderModel.loadModel("rpmachine:models/vawt.obj").scale(0.0625);
   private RenderModel modelWoodWindmill = RenderModel.loadModel("rpmachine:models/windmill.obj").scale(0.0625);
   private ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/vawt.png");

   public RenderWindTurbine(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileWindTurbine windTurbine = (TileWindTurbine)tile;
      World world = windTurbine.getWorldObj();
      Tessellator tess = Tessellator.instance;
      GL11.glDisable(2896);
      this.turbineContext.bindBlockTexture();
      this.turbineContext.setDefaults();
      this.turbineContext.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      this.turbineContext.setPos(x, y, z);
      this.turbineContext.readGlobalLights(world, windTurbine.xCoord, windTurbine.yCoord, windTurbine.zCoord);
      this.turbineContext
         .setIcon(
            RedPowerMachine.motorBottom,
            RedPowerMachine.turbineFront,
            RedPowerMachine.turbineSide,
            RedPowerMachine.turbineSide,
            RedPowerMachine.turbineSideAlt,
            RedPowerMachine.turbineSideAlt
         );
      this.turbineContext.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.turbineContext.setupBox();
      this.turbineContext.transform();
      this.turbineContext.orientTextures(windTurbine.Rotation);
      tess.startDrawingQuads();
      this.turbineContext.renderGlobFaces(63);
      tess.draw();
      if (windTurbine.hasBlades) {
         byte wtt = windTurbine.windmillType;
         this.bladesContext.bindTexture(this.modelRes);
         this.bladesContext.setDefaults();
         tess.startDrawingQuads();
         WorldCoord wc = new WorldCoord(windTurbine);
         wc.step(windTurbine.Rotation ^ 1);
         tess.setBrightness(world.getLightBrightnessForSkyBlocks(wc.x, wc.y, wc.z, 0));
         this.bladesContext.useNormal = true;
         if (windTurbine.hasBrakes) {
            partialTicks = (float)((double)partialTicks * 0.1);
         }

         double tm = (double)(partialTicks * (float)windTurbine.speed + (float)windTurbine.phase);
         if (wtt == 2) {
            tm = -tm;
         }

         this.bladesContext.setOrientation(windTurbine.Rotation, 0);
         this.bladesContext.basis = Matrix3.getRotY(-4.0E-6 * tm).multiply(this.bladesContext.basis);
         this.bladesContext.setPos(x, y, z);
         this.bladesContext.setRelPos(0.5, 0.875, 0.5);
         switch(wtt) {
            case 1:
               this.bladesContext.bindModelOffset(this.modelWoodTurbine, 0.5, 0.5, 0.5);
               break;
            case 2:
               this.bladesContext.bindModelOffset(this.modelWoodWindmill, 0.5, 0.5, 0.5);
               break;
            default:
               return;
         }

         this.bladesContext.setTint(1.0F, 1.0F, 1.0F);
         this.bladesContext.renderModelGroup(0, 0);
         switch(wtt) {
            case 1:
               this.bladesContext.setTint(1.0F, 1.0F, 1.0F);
               this.bladesContext.renderModelGroup(1, 1);
               this.bladesContext.renderModelGroup(1, 3);
               this.bladesContext.renderModelGroup(1, 5);
               this.bladesContext.setTint(1.0F, 0.1F, 0.1F);
               this.bladesContext.renderModelGroup(1, 2);
               this.bladesContext.renderModelGroup(1, 4);
               this.bladesContext.renderModelGroup(1, 6);
               break;
            default:
               this.bladesContext.setTint(1.0F, 1.0F, 1.0F);
               this.bladesContext.renderModelGroup(1, 1);
               this.bladesContext.renderModelGroup(1, 3);
               this.bladesContext.setTint(1.0F, 0.1F, 0.1F);
               this.bladesContext.renderModelGroup(1, 2);
               this.bladesContext.renderModelGroup(1, 4);
         }

         tess.draw();
      }

      GL11.glEnable(2896);
   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      super.block.setBlockBoundsForItemRender();
      this.turbineContext.setDefaults();
      if (type == ItemRenderType.INVENTORY) {
         this.turbineContext.setPos(-0.5, -0.5, -0.5);
      } else {
         this.turbineContext.setPos(0.0, 0.0, 0.0);
      }

      this.turbineContext.useNormal = true;
      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      this.turbineContext
         .setIcon(
            RedPowerMachine.motorBottom,
            RedPowerMachine.turbineFront,
            RedPowerMachine.turbineSide,
            RedPowerMachine.turbineSide,
            RedPowerMachine.turbineSideAlt,
            RedPowerMachine.turbineSideAlt
         );
      this.turbineContext.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      tess.draw();
      this.turbineContext.useNormal = false;
   }
}
