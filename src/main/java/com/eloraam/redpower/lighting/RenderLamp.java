package com.eloraam.redpower.lighting;

import com.eloraam.redpower.RedPowerLighting;
import com.eloraam.redpower.core.RenderContext;
import com.eloraam.redpower.core.RenderCustomBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderLamp extends RenderCustomBlock {
   static int[] lightColors = new int[]{
      16777215, 12608256, 11868853, 7308529, 12566272, 7074048, 15812213, 5460819, 9671571, 34695, 6160576, 1250240, 5187328, 558848, 10620678, 2039583
   };
   static int[] lightColorsOff = new int[16];
   private RenderContext context = new RenderContext();

   public RenderLamp(BlockLamp block) {
      super(block);
   }

   public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
      TileLamp lamp = (TileLamp)tile;
      World world = tile.getWorldObj();
      GL11.glDisable(2896);
      boolean lit = lamp.Powered != lamp.Inverted;
      Tessellator tess = Tessellator.instance;
      this.context.bindBlockTexture();
      this.context.setDefaults();
      this.context.setPos(x, y, z);
      this.context.readGlobalLights(world, tile.xCoord, tile.yCoord, tile.zCoord);
      if (MinecraftForgeClient.getRenderPass() == 0) {
         this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         this.context.setupBox();
         this.context.transform();
         this.context.setIcon(lit ? RedPowerLighting.lampOn[lamp.Color] : RedPowerLighting.lampOff[lamp.Color]);
         tess.startDrawingQuads();
         this.context.renderGlobFaces(63);
         tess.draw();
      }

      if (MinecraftForgeClient.getRenderPass() == 1 && lit) {
         GL11.glDisable(3553);
         GL11.glEnable(3008);
         GL11.glAlphaFunc(516, 0.1F);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 1);
         GL11.glDisable(2884);
         this.context.setPos(x, y, z);
         this.context.setTintHex(lightColors[lamp.Color]);
         this.context.setLocalLights(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
         this.context.setSize(-0.05, -0.05, -0.05, 1.05, 1.05, 1.05);
         this.context.setupBox();
         this.context.transform();
         this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         this.context.doMappingBox(0);
         this.context.doLightLocal(63);
         tess.startDrawingQuads();
         this.context.renderAlpha(63, 0.5F);
         tess.draw();
         GL11.glEnable(2884);
         GL11.glDisable(3042);
         GL11.glEnable(3553);
      }

      GL11.glEnable(2896);
   }

   @Override
   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      int meta = item.getItemDamage();
      super.block.setBlockBoundsForItemRender();
      this.context.setDefaults();
      if (type == ItemRenderType.INVENTORY) {
         this.context.setPos(-0.5, -0.5, -0.5);
      } else {
         this.context.setPos(0.0, 0.0, 0.0);
      }

      this.context.useNormal = true;
      boolean lit = (meta & 16) > 0;
      Tessellator tess = Tessellator.instance;
      this.context.setIcon(lit ? RedPowerLighting.lampOn[meta & 15] : RedPowerLighting.lampOff[meta & 15]);
      tess.startDrawingQuads();
      this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      tess.draw();
      this.context.useNormal = false;
      if (lit) {
         GL11.glBlendFunc(770, 1);
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         this.context.setTintHex(lightColors[meta & 15]);
         this.context.setLocalLights(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
         this.context.setSize(-0.05, -0.05, -0.05, 1.05, 1.05, 1.05);
         this.context.setupBox();
         this.context.transform();
         this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         this.context.doMappingBox(0);
         this.context.doLightLocal(63);
         tess.startDrawingQuads();
         this.context.renderAlpha(63, 0.5F);
         tess.draw();
         GL11.glEnable(3553);
         GL11.glEnable(2896);
         GL11.glBlendFunc(770, 771);
      }

   }

   @Override
   public IIcon getParticleIconForSide(World world, int x, int y, int z, TileEntity tile, int side, int meta) {
      if (tile instanceof TileLamp) {
         TileLamp lamp = (TileLamp)tile;
         return lamp.Powered != lamp.Inverted ? RedPowerLighting.lampOn[lamp.Color] : RedPowerLighting.lampOff[lamp.Color];
      } else {
         return super.getParticleIconForSide(world, x, y, z, tile, side, meta);
      }
   }

   static {
      for(int i = 0; i < 16; ++i) {
         int r = lightColors[i] & 0xFF;
         int g = lightColors[i] >> 8 & 0xFF;
         int b = lightColors[i] >> 16 & 0xFF;
         int v = (r + g + b) / 3;
         r = (r + 2 * v) / 5;
         g = (g + 2 * v) / 5;
         b = (b + 2 * v) / 5;
         lightColorsOff[i] = r | g << 8 | b << 16;
      }

   }
}
