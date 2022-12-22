package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.TubeFlow;
import com.eloraam.redpower.core.TubeItem;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderAccel extends RenderCustomBlock {
   private RenderModel model = RenderModel.loadModel("rpmachine:models/accel.obj");
   private ResourceLocation modelRes = new ResourceLocation("rpmachine", "models/machine1.png");
   private RenderContext context = new RenderContext();
   private EntityItem item = new EntityItem((World)null);
   private int[] paintColors = new int[]{
      16777215, 16744448, 16711935, 7110911, 16776960, 65280, 16737408, 5460819, 9671571, 65535, 8388863, 255, 5187328, 32768, 16711680, 2039583
   };

   public RenderAccel(Block block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileAccel accel = (TileAccel)tile;
      World world = accel.getWorldObj();
      GL11.glDisable(2896);
      Tessellator tess = Tessellator.instance;
      this.context.setDefaults();
      this.context.setPos(x, y, z);
      this.context.setOrientation(accel.Rotation, 0);
      this.context.readGlobalLights(world, accel.xCoord, accel.yCoord, accel.zCoord);
      if (accel.Charged) {
         this.context.setBrightness(15728880);
      } else {
         this.context.setBrightness(this.getMixedBrightness(accel));
      }

      this.context.bindTexture(this.modelRes);
      tess.startDrawingQuads();
      this.context.bindModelOffset(this.model, 0.5, 0.5, 0.5);
      this.context.renderModelGroup(0, 0);
      this.context.renderModelGroup(1, 1 + (accel.Charged ? 1 : 0));
      if (accel.Charged) {
         this.context.setBrightness(this.getMixedBrightness(accel));
      }

      accel.recache();
      if ((accel.conCache & 1) > 0) {
         this.context.renderModelGroup(2, 2);
      }

      if ((accel.conCache & 2) > 0) {
         this.context.renderModelGroup(2, 1);
      }

      if ((accel.conCache & 4) > 0) {
         this.context.renderModelGroup(3, 2);
      }

      if ((accel.conCache & 8) > 0) {
         this.context.renderModelGroup(3, 1);
      }

      tess.draw();
      this.item.worldObj = world;
      this.item.setPosition((double)accel.xCoord + 0.5, (double)accel.yCoord + 0.5, (double)accel.zCoord + 0.5);
      RenderItem renderitem = (RenderItem)RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
      this.item.age = 0;
      this.item.hoverStart = 0.0F;
      WorldCoord offset = new WorldCoord(0, 0, 0);
      TubeFlow flow = accel.getTubeFlow();
      int lv = accel.getWorldObj().getLightBrightnessForSkyBlocks(accel.xCoord, accel.yCoord, accel.zCoord, 0);

      for(TubeItem item : flow.contents) {
         this.item.setEntityItemStack(item.item);
         offset.x = 0;
         offset.y = 0;
         offset.z = 0;
         offset.step(item.side);
         double d = (double)item.progress / 128.0 * 0.5;
         if (!item.scheduled) {
            d = 0.5 - d;
         }

         double yo = 0.0;
         if (Item.getIdFromItem(item.item.getItem()) >= 256) {
            yo += 0.1;
         }

         renderitem.doRender(
            this.item,
            x + 0.5 + (double)offset.x * d,
            y + 0.5 - (double)this.item.yOffset - yo + (double)offset.y * d,
            z + 0.5 + (double)offset.z * d,
            0.0F,
            0.0F
         );
         if (item.color > 0) {
            this.context.bindBlockTexture();
            tess.startDrawingQuads();
            this.context.useNormal = true;
            this.context.setDefaults();
            tess.setBrightness(lv);
            this.context.setPos(x + (double)offset.x * d, y + (double)offset.y * d, z + (double)offset.z * d);
            this.context.setTintHex(this.paintColors[item.color - 1]);
            this.context.setIcon(RedPowerMachine.tubeItemOverlay);
            this.context.renderBox(63, 0.26F, 0.26F, 0.26F, 0.74F, 0.74F, 0.74F);
            tess.draw();
         }
      }

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

      this.context.setOrientation(2, 0);
      this.context.bindTexture(this.modelRes);
      Tessellator tess = Tessellator.instance;
      tess.startDrawingQuads();
      this.context.useNormal = true;
      this.context.bindModelOffset(this.model, 0.5, 0.5, 0.5);
      this.context.renderModelGroup(0, 0);
      this.context.renderModelGroup(1, 1);
      this.context.useNormal = false;
      tess.draw();
   }
}
