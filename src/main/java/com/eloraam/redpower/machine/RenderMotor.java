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
public class RenderMotor extends RenderCustomBlock {
   protected RenderContext context = new RenderContext();

   public RenderMotor(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileMotor motor = (TileMotor)tile;
      World world = motor.getWorldObj();
      Tessellator tess = Tessellator.instance;
      GL11.glDisable(2896);
      this.context.bindBlockTexture();
      this.context.setDefaults();
      this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
      this.context.setPos(x, y, z);
      this.context.readGlobalLights(world, motor.xCoord, motor.yCoord, motor.zCoord);
      this.context.setTexFlags(64);
      if (motor.Active) {
         this.context
            .setIcon(
               RedPowerMachine.motorBottom,
               RedPowerMachine.motorTopActive,
               RedPowerMachine.motorFrontActive,
               RedPowerMachine.motorFrontActive,
               RedPowerMachine.motorSide,
               RedPowerMachine.motorSide
            );
      } else {
         IIcon tx = motor.Charged ? RedPowerMachine.motorFrontCharged : RedPowerMachine.motorFront;
         this.context.setIcon(RedPowerMachine.motorBottom, RedPowerMachine.motorTop, tx, tx, RedPowerMachine.motorSide, RedPowerMachine.motorSide);
      }

      this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.context.setupBox();
      this.context.transform();
      this.context.orientTextureNew(motor.Rotation);
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
            RedPowerMachine.motorBottom,
            RedPowerMachine.motorTop,
            RedPowerMachine.motorFront,
            RedPowerMachine.motorFront,
            RedPowerMachine.motorSide,
            RedPowerMachine.motorSide
         );
      this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      tess.draw();
      this.context.useNormal = false;
   }
}
