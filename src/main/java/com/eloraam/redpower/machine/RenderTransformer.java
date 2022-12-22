package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
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
public class RenderTransformer extends RenderCustomBlock {
   protected RenderModel model = RenderModel.loadModel("rpmachine:models/transform.obj").scale(0.0625);
   protected ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/machine2.png");
   protected RenderContext context = new RenderContext();

   public RenderTransformer(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileTransformer transformer = (TileTransformer)tile;
      World world = transformer.getWorldObj();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      this.context.setDefaults();
      this.context.setPos(x, y, z);
      this.context.setOrientation(transformer.Rotation >> 2, transformer.Rotation + 3 & 3);
      this.context.readGlobalLights(world, transformer.xCoord, transformer.yCoord, transformer.zCoord);
      this.context.setBrightness(this.getMixedBrightness(transformer));
      this.context.bindTexture(this.modelRes);
      tess.startDrawingQuads();
      this.context.bindModelOffset(this.model, 0.5, 0.5, 0.5);
      this.context.renderModelGroup(0, 0);
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

      this.context.bindTexture(this.modelRes);
      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      this.context.useNormal = true;
      this.context.bindModelOffset(this.model, 0.5, 0.5, 0.5);
      this.context.renderModelGroup(0, 0);
      this.context.useNormal = false;
      tess.draw();
   }
}
