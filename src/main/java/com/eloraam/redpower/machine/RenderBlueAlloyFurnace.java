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
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBlueAlloyFurnace extends RenderCustomBlock {
   protected RenderContext context = new RenderContext();

   public RenderBlueAlloyFurnace(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileBlueAlloyFurnace blueAlloyFurnace = (TileBlueAlloyFurnace)tile;
      World world = blueAlloyFurnace.getWorldObj();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      this.context.bindBlockTexture();
      this.context.setDefaults();
      this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      this.context.setPos(x, y, z);
      this.context.readGlobalLights(world, blueAlloyFurnace.xCoord, blueAlloyFurnace.yCoord, blueAlloyFurnace.zCoord);
      this.context
         .setIcon(
            RedPowerMachine.electronicsBottom,
            RedPowerMachine.btAlloyFurnaceTop,
            blueAlloyFurnace.Active ? RedPowerMachine.btAlloyFurnaceFrontOn : RedPowerMachine.btAlloyFurnaceFront,
            RedPowerMachine.btAlloyFurnaceSide,
            RedPowerMachine.btAlloyFurnaceSide,
            RedPowerMachine.btAlloyFurnaceSide
         );
      this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.context.setupBox();
      this.context.transform();
      this.context.rotateTextures(blueAlloyFurnace.Rotation);
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
            RedPowerMachine.electronicsBottom,
            RedPowerMachine.btAlloyFurnaceTop,
            RedPowerMachine.btAlloyFurnaceSide,
            RedPowerMachine.btAlloyFurnaceSide,
            RedPowerMachine.btAlloyFurnaceSide,
            RedPowerMachine.btAlloyFurnaceFront
         );
      this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      tess.draw();
      this.context.useNormal = false;
   }
}
