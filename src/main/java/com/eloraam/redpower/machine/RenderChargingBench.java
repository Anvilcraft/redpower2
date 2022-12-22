package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderChargingBench extends RenderCustomBlock {
   protected RenderContext context = new RenderContext();

   public RenderChargingBench(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileChargingBench charger = (TileChargingBench)tile;
      World world = charger.getWorldObj();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      this.context.bindBlockTexture();
      this.context.setDefaults();
      this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      this.context.setPos(x, y, z);
      this.context.readGlobalLights(world, charger.xCoord, charger.yCoord, charger.zCoord);
      int s = charger.getStorageForRender();
      IIcon front = RedPowerMachine.btChargerFront[s];
      if (charger.Powered) {
         front = RedPowerMachine.btChargerFrontPowered[s];
      }

      if (charger.Active) {
         front = RedPowerMachine.btChargerFrontActive[s];
      }

      this.context
         .setIcon(
            RedPowerMachine.btChargerBottom,
            RedPowerMachine.btChargerTop,
            front,
            RedPowerMachine.btChargerSide,
            RedPowerMachine.btChargerSide,
            RedPowerMachine.btChargerSide
         );
      this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.context.setupBox();
      this.context.transform();
      this.context.rotateTextures(charger.Rotation);
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
      this.context
         .setIcon(
            RedPowerMachine.btChargerBottom,
            RedPowerMachine.btChargerTop,
            RedPowerMachine.btChargerSide,
            RedPowerMachine.btChargerSide,
            RedPowerMachine.btChargerSide,
            RedPowerMachine.btChargerFront[0]
         );
      this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      tess.draw();
      this.context.useNormal = false;
   }
}
