package com.eloraam.redpower.core;

import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;

public class RenderLib {
   private static RenderLib.RenderListEntry[] renderers = new RenderLib.RenderListEntry[4096];

   public static void renderSpecialLever(Vector3 pos, Quat rot, IIcon foundation, IIcon lever) {
      Vector3[] pl = new Vector3[8];
      float f8 = 0.0625F;
      float f9 = 0.0625F;
      float f10 = 0.375F;
      pl[0] = new Vector3((double)(-f8), 0.0, (double)(-f9));
      pl[1] = new Vector3((double)f8, 0.0, (double)(-f9));
      pl[2] = new Vector3((double)f8, 0.0, (double)f9);
      pl[3] = new Vector3((double)(-f8), 0.0, (double)f9);
      pl[4] = new Vector3((double)(-f8), (double)f10, (double)(-f9));
      pl[5] = new Vector3((double)f8, (double)f10, (double)(-f9));
      pl[6] = new Vector3((double)f8, (double)f10, (double)f9);
      pl[7] = new Vector3((double)(-f8), (double)f10, (double)f9);

      for(int i = 0; i < 8; ++i) {
         rot.rotate(pl[i]);
         pl[i].add(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
      }

      float uMin = foundation.getMinU();
      float uMax = foundation.getMaxU();
      float vMin = foundation.getMinV();
      float vMax = foundation.getMaxV();
      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      addVectWithUV(pl[0], (double)uMin, (double)vMax);
      addVectWithUV(pl[1], (double)uMax, (double)vMax);
      addVectWithUV(pl[2], (double)uMax, (double)vMin);
      addVectWithUV(pl[3], (double)uMin, (double)vMin);
      addVectWithUV(pl[7], (double)uMin, (double)vMax);
      addVectWithUV(pl[6], (double)uMax, (double)vMax);
      addVectWithUV(pl[5], (double)uMax, (double)vMin);
      addVectWithUV(pl[4], (double)uMin, (double)vMin);
      uMin = lever.getMinU();
      uMax = lever.getMaxU();
      vMin = lever.getMinV();
      vMax = lever.getMaxV();
      addVectWithUV(pl[1], (double)uMin, (double)vMax);
      addVectWithUV(pl[0], (double)uMax, (double)vMax);
      addVectWithUV(pl[4], (double)uMax, (double)vMin);
      addVectWithUV(pl[5], (double)uMin, (double)vMin);
      addVectWithUV(pl[2], (double)uMin, (double)vMax);
      addVectWithUV(pl[1], (double)uMax, (double)vMax);
      addVectWithUV(pl[5], (double)uMax, (double)vMin);
      addVectWithUV(pl[6], (double)uMin, (double)vMin);
      addVectWithUV(pl[3], (double)uMin, (double)vMax);
      addVectWithUV(pl[2], (double)uMax, (double)vMax);
      addVectWithUV(pl[6], (double)uMax, (double)vMin);
      addVectWithUV(pl[7], (double)uMin, (double)vMin);
      addVectWithUV(pl[0], (double)uMin, (double)vMax);
      addVectWithUV(pl[3], (double)uMax, (double)vMax);
      addVectWithUV(pl[7], (double)uMax, (double)vMin);
      addVectWithUV(pl[4], (double)uMin, (double)vMin);
   }

   public static void addVectWithUV(Vector3 vect, double u, double v) {
      Tessellator tess = Tessellator.instance;
      tess.addVertexWithUV(vect.x, vect.y, vect.z, u, v);
   }

   public static void renderPointer(Vector3 pos, Quat rot) {
      Tessellator tess = Tessellator.instance;
      IIcon icon = Blocks.stone.getIcon(0, 0);
      double uMin = (double)icon.getMinU();
      double vMin = (double)icon.getMinV();
      double uMax = (double)icon.getInterpolatedU(7.9) - uMin;
      double vMax = (double)icon.getInterpolatedV(0.12432) - vMin;
      tess.setColorOpaque_F(0.9F, 0.9F, 0.9F);
      Vector3[] vecs = new Vector3[]{
         new Vector3(0.4, 0.0, 0.0),
         new Vector3(0.0, 0.0, 0.2),
         new Vector3(-0.2, 0.0, 0.0),
         new Vector3(0.0, 0.0, -0.2),
         new Vector3(0.4, 0.1, 0.0),
         new Vector3(0.0, 0.1, 0.2),
         new Vector3(-0.2, 0.1, 0.0),
         new Vector3(0.0, 0.1, -0.2)
      };

      for(int i = 0; i < 8; ++i) {
         rot.rotate(vecs[i]);
         vecs[i].add(pos);
      }

      addVectWithUV(vecs[0], uMin, vMin);
      addVectWithUV(vecs[1], uMin + uMax, vMin);
      addVectWithUV(vecs[2], uMin + uMax, vMin + uMax);
      addVectWithUV(vecs[3], uMin, vMin + uMax);
      addVectWithUV(vecs[4], uMin, vMin);
      addVectWithUV(vecs[7], uMin, vMin + uMax);
      addVectWithUV(vecs[6], uMin + uMax, vMin + uMax);
      addVectWithUV(vecs[5], uMin + uMax, vMin);
      tess.setColorOpaque_F(0.6F, 0.6F, 0.6F);
      addVectWithUV(vecs[0], uMin + vMax, vMin);
      addVectWithUV(vecs[4], uMin, vMin);
      addVectWithUV(vecs[5], uMin, vMin + uMax);
      addVectWithUV(vecs[1], uMin + vMax, vMin + uMax);
      addVectWithUV(vecs[0], uMin, vMin + vMax);
      addVectWithUV(vecs[3], uMin + uMax, vMin + vMax);
      addVectWithUV(vecs[7], uMin + uMax, vMin);
      addVectWithUV(vecs[4], uMin, vMin);
      addVectWithUV(vecs[2], uMin + uMax, vMin + uMax - vMax);
      addVectWithUV(vecs[6], uMin + uMax, vMin + uMax);
      addVectWithUV(vecs[7], uMin, vMin + uMax);
      addVectWithUV(vecs[3], uMin, vMin + uMax - vMax);
      addVectWithUV(vecs[2], uMin + uMax, vMin + uMax - vMax);
      addVectWithUV(vecs[1], uMin, vMin + uMax - vMax);
      addVectWithUV(vecs[5], uMin, vMin + uMax);
      addVectWithUV(vecs[6], uMin + uMax, vMin + uMax);
   }

   public static RenderCustomBlock getRenderer(Block bid, int md) {
      RenderLib.RenderListEntry rle = renderers[Block.getIdFromBlock(bid)];
      return rle == null ? null : rle.metaRenders[md];
   }

   public static RenderCustomBlock getInvRenderer(Block bid, int md) {
      RenderLib.RenderListEntry rle = renderers[Block.getIdFromBlock(bid)];
      if (rle == null) {
         return null;
      } else {
         int mdv = rle.mapDamageValue(md);
         return mdv > 15 ? rle.defaultRender : rle.metaRenders[mdv];
      }
   }

   private static <B extends Block> RenderCustomBlock makeRenderer(B bl, Function<B, ? extends RenderCustomBlock> rcl) {
      return (RenderCustomBlock)rcl.apply(bl);
   }

   public static <B extends Block> void setRenderer(B bl, Function<B, ? extends RenderCustomBlock> rcl) {
      RenderCustomBlock rnd = makeRenderer(bl, rcl);
      int bid = Block.getIdFromBlock(bl);
      if (renderers[bid] == null) {
         renderers[bid] = new RenderLib.RenderListEntry();
         MinecraftForgeClient.registerItemRenderer(ItemExtended.getItemFromBlock(bl), renderers[bid]);
      }

      for(int i = 0; i < 16; ++i) {
         renderers[bid].metaRenders[i] = rnd;
      }

   }

   public static <B extends Block> void setRenderer(B bl, int md, Function<B, ? extends RenderCustomBlock> rcl) {
      RenderCustomBlock rnd = makeRenderer(bl, rcl);
      int bid = Block.getIdFromBlock(bl);
      if (renderers[bid] == null) {
         renderers[bid] = new RenderLib.RenderListEntry();
         MinecraftForgeClient.registerItemRenderer(ItemExtended.getItemFromBlock(bl), renderers[bid]);
      }

      renderers[bid].metaRenders[md] = rnd;
   }

   public static <B extends Block> void setHighRenderer(B bl, int md, Function<B, ? extends RenderCustomBlock> rcl) {
      RenderCustomBlock rnd = makeRenderer(bl, rcl);
      int bid = Block.getIdFromBlock(bl);
      if (renderers[bid] == null) {
         renderers[bid] = new RenderLib.RenderShiftedEntry(8);
         MinecraftForgeClient.registerItemRenderer(ItemExtended.getItemFromBlock(bl), renderers[bid]);
      }

      renderers[bid].metaRenders[md] = rnd;
   }

   public static <B extends Block> void setDefaultRenderer(B bl, int shift, Function<B, ? extends RenderCustomBlock> rcl) {
      RenderCustomBlock rnd = makeRenderer(bl, rcl);
      int bid = Block.getIdFromBlock(bl);
      if (renderers[bid] == null) {
         renderers[bid] = new RenderLib.RenderShiftedEntry(shift);
         MinecraftForgeClient.registerItemRenderer(ItemExtended.getItemFromBlock(bl), renderers[bid]);
      }

      for(int i = 0; i < 16; ++i) {
         if (renderers[bid].metaRenders[i] == null) {
            renderers[bid].metaRenders[i] = rnd;
         }
      }

      renderers[Block.getIdFromBlock(bl)].defaultRender = rnd;
   }

   public static <B extends Block> void setShiftedRenderer(B bl, int md, int shift, Function<B, ? extends RenderCustomBlock> rcl) {
      RenderCustomBlock rnd = makeRenderer(bl, rcl);
      int bid = Block.getIdFromBlock(bl);
      if (renderers[bid] == null) {
         renderers[bid] = new RenderLib.RenderShiftedEntry(shift);
         MinecraftForgeClient.registerItemRenderer(ItemExtended.getItemFromBlock(bl), renderers[bid]);
      }

      renderers[bid].metaRenders[md] = rnd;
   }

   private static class RenderListEntry implements IItemRenderer {
      public RenderCustomBlock[] metaRenders = new RenderCustomBlock[16];
      RenderCustomBlock defaultRender;

      private RenderListEntry() {
      }

      public int mapDamageValue(int dmg) {
         return dmg;
      }

      public boolean handleRenderType(ItemStack item, ItemRenderType type) {
         int meta = item.getItemDamage();
         int mdv = this.mapDamageValue(meta);
         RenderCustomBlock renderer = mdv > 15 ? this.defaultRender : this.metaRenders[mdv];
         return renderer != null && renderer.handleRenderType(item, type);
      }

      public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
         int meta = item.getItemDamage();
         int mdv = this.mapDamageValue(meta);
         RenderCustomBlock renderer = mdv > 15 ? this.defaultRender : this.metaRenders[mdv];
         return renderer != null && renderer.shouldUseRenderHelper(type, item, helper);
      }

      public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
         int meta = item.getItemDamage();
         int mdv = this.mapDamageValue(meta);
         RenderCustomBlock renderer = mdv > 15 ? this.defaultRender : this.metaRenders[mdv];
         if (renderer != null) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3008);
            renderer.renderItem(type, item, data);
         }

      }
   }

   private static class RenderShiftedEntry extends RenderLib.RenderListEntry {
      public int shift;

      public RenderShiftedEntry(int sh) {
         this.shift = sh;
      }

      @Override
      public int mapDamageValue(int dmg) {
         return dmg >> this.shift;
      }
   }
}
