package com.eloraam.redpower.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderHighlight {
   private RenderContext context = new RenderContext();
   private CoverRenderer coverRenderer = new CoverRenderer(this.context);
   private IIcon[] destroyIcons;

   @SubscribeEvent
   public void onTextureStitchEventPost(Post evt) {
      if (evt.map.getTextureType() == 0) {
         CoverRenderer.reInitIcons();
      }

      this.destroyIcons = (IIcon[])ReflectionHelper.getPrivateValue(
         RenderGlobal.class, Minecraft.getMinecraft().renderGlobal, new String[]{"destroyBlockIcons", "field_94141_F"}
      );
   }

   @SubscribeEvent
   public void highlightEvent(DrawBlockHighlightEvent evt) {
      this.onBlockHighlight(evt.context, evt.player, evt.target, evt.subID, evt.currentItem, evt.partialTicks);
   }

   public boolean onBlockHighlight(RenderGlobal render, EntityPlayer pl, MovingObjectPosition mop, int subID, ItemStack ist, float partialTicks) {
      World world = pl.worldObj;
      Block bl = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
      Map<Integer, DestroyBlockProgress> damagedBlocks = (Map)ReflectionHelper.getPrivateValue(
         RenderGlobal.class, render, new String[]{"damagedBlocks", "field_72738_E"}
      );
      if (bl instanceof BlockMultipart) {
         BlockMultipart bm = (BlockMultipart)bl;
         bm.setPartBounds(pl.worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.subHit);
      }

      if (!damagedBlocks.isEmpty()) {
         for(DestroyBlockProgress dbp : damagedBlocks.values()) {
            if (dbp.getPartialBlockX() == mop.blockX && dbp.getPartialBlockY() == mop.blockY && dbp.getPartialBlockZ() == mop.blockZ) {
               if (bl instanceof BlockExtended) {
                  this.drawBreaking(pl.worldObj, render, (BlockExtended)bl, pl, mop, partialTicks, dbp.getPartialBlockDamage());
                  return true;
               }
               break;
            }
         }
      }

      if (ist == null || CoverLib.blockCoverPlate == null || ist.getItem() != Item.getItemFromBlock(CoverLib.blockCoverPlate)) {
         return false;
      } else if (mop.typeOfHit != MovingObjectType.BLOCK) {
         return false;
      } else {
         MovingObjectPosition placement;
         switch(ist.getItemDamage() >> 8) {
            case 0:
            case 16:
            case 17:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
               this.drawSideBox(world, pl, mop, partialTicks);
               placement = CoverLib.getPlacement(world, mop, ist.getItemDamage());
               if (placement != null) {
                  this.drawPreview(pl, placement, partialTicks, ist.getItemDamage());
               }
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
               return false;
            case 18:
            case 19:
            case 20:
            case 35:
            case 36:
            case 37:
            case 38:
               this.drawCornerBox(world, pl, mop, partialTicks);
               placement = CoverLib.getPlacement(world, mop, ist.getItemDamage());
               if (placement != null) {
                  this.drawPreview(pl, placement, partialTicks, ist.getItemDamage());
               }
         }

         return true;
      }
   }

   private void setRawPos(EntityPlayer player, MovingObjectPosition mop, float partialTicks) {
      double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
      double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
      double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
      this.context.setPos((double)mop.blockX - dx, (double)mop.blockY - dy, (double)mop.blockZ - dz);
   }

   private void setCollPos(EntityPlayer player, MovingObjectPosition mop, float partialTicks) {
      this.setRawPos(player, mop, partialTicks);
      switch(mop.sideHit) {
         case 0:
            this.context.setRelPos(0.0, mop.hitVec.yCoord - (double)mop.blockY, 0.0);
            break;
         case 1:
            this.context.setRelPos(0.0, (double)mop.blockY - mop.hitVec.yCoord + 1.0, 0.0);
            break;
         case 2:
            this.context.setRelPos(0.0, mop.hitVec.zCoord - (double)mop.blockZ, 0.0);
            break;
         case 3:
            this.context.setRelPos(0.0, (double)mop.blockZ - mop.hitVec.zCoord + 1.0, 0.0);
            break;
         case 4:
            this.context.setRelPos(0.0, mop.hitVec.xCoord - (double)mop.blockX, 0.0);
            break;
         default:
            this.context.setRelPos(0.0, (double)mop.blockX - mop.hitVec.xCoord + 1.0, 0.0);
      }

   }

   public void drawCornerBox(World world, EntityPlayer player, MovingObjectPosition mop, float partialTicks) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);
      GL11.glLineWidth(3.0F);
      GL11.glDisable(3553);
      GL11.glDepthMask(false);
      float var5 = 0.002F;
      float var6 = 0.25F;
      Block bl = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
      if (bl != Blocks.air) {
         this.context.setSize(0.0, (double)(-var5), 0.0, 1.0, (double)(-var5), 1.0);
         this.context.setupBox();
         this.context.vertices[4].set(0.0, (double)(-var5), 0.5);
         this.context.vertices[5].set(1.0, (double)(-var5), 0.5);
         this.context.vertices[6].set(0.5, (double)(-var5), 0.0);
         this.context.vertices[7].set(0.5, (double)(-var5), 1.0);
         this.context.setOrientation(mop.sideHit, 0);
         this.setCollPos(player, mop, partialTicks);
         this.context.transformRotate();
         Tessellator.instance.startDrawing(3);
         this.context.drawPoints(0, 1, 2, 3, 0);
         Tessellator.instance.draw();
         Tessellator.instance.startDrawing(1);
         this.context.drawPoints(4, 5, 6, 7);
         Tessellator.instance.draw();
      }

      GL11.glDepthMask(true);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      this.context.setRelPos(0.0, 0.0, 0.0);
   }

   public void drawSideBox(World world, EntityPlayer player, MovingObjectPosition mop, float partialTicks) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);
      GL11.glLineWidth(3.0F);
      GL11.glDisable(3553);
      GL11.glDepthMask(false);
      float var5 = 0.002F;
      float var6 = 0.25F;
      Block bl = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
      if (bl != Blocks.air) {
         this.context.setSize(0.0, (double)(-var5), 0.0, 1.0, (double)(-var5), 1.0);
         this.context.setupBox();
         this.context.vertices[4].set((double)(1.0F - var6), (double)(-var5), (double)var6);
         this.context.vertices[5].set((double)var6, (double)(-var5), (double)var6);
         this.context.vertices[6].set((double)var6, (double)(-var5), (double)(1.0F - var6));
         this.context.vertices[7].set((double)(1.0F - var6), (double)(-var5), (double)(1.0F - var6));
         this.context.setOrientation(mop.sideHit, 0);
         this.setCollPos(player, mop, partialTicks);
         this.context.transformRotate();
         Tessellator.instance.startDrawing(3);
         this.context.drawPoints(0, 1, 2, 3, 0);
         Tessellator.instance.draw();
         Tessellator.instance.startDrawing(3);
         this.context.drawPoints(4, 5, 6, 7, 4);
         Tessellator.instance.draw();
         Tessellator.instance.startDrawing(1);
         this.context.drawPoints(0, 4, 1, 5, 2, 6, 3, 7);
         Tessellator.instance.draw();
      }

      GL11.glDepthMask(true);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      this.context.setRelPos(0.0, 0.0, 0.0);
   }

   public void drawBreaking(World world, RenderGlobal render, BlockExtended bl, EntityPlayer pl, MovingObjectPosition mop, float partialTicks, int destroyStage) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(774, 768);
      this.context.bindBlockTexture();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      GL11.glPolygonOffset(-3.0F, -3.0F);
      GL11.glEnable(32823);
      double dx = pl.lastTickPosX + (pl.posX - pl.lastTickPosX) * (double)partialTicks;
      double dy = pl.lastTickPosY + (pl.posY - pl.lastTickPosY) * (double)partialTicks;
      double dz = pl.lastTickPosZ + (pl.posZ - pl.lastTickPosZ) * (double)partialTicks;
      GL11.glEnable(3008);
      this.context.setPos((double)mop.blockX - dx, (double)mop.blockY - dy, (double)mop.blockZ - dz);
      this.context.setIcon(this.destroyIcons[destroyStage]);
      Tessellator.instance.startDrawingQuads();
      this.context
         .setSize(
            bl.getBlockBoundsMinX(),
            bl.getBlockBoundsMinY(),
            bl.getBlockBoundsMinZ(),
            bl.getBlockBoundsMaxX(),
            bl.getBlockBoundsMaxY(),
            bl.getBlockBoundsMaxZ()
         );
      this.context.setupBox();
      this.context.transform();
      this.context.renderFaces(63);
      Tessellator.instance.draw();
      GL11.glPolygonOffset(0.0F, 0.0F);
      GL11.glDisable(32823);
   }

   public void drawPreview(EntityPlayer pl, MovingObjectPosition mop, float partialTicks, int md) {
      this.setRawPos(pl, mop, partialTicks);
      this.context.bindBlockTexture();
      this.coverRenderer.start();
      this.coverRenderer.setupCorners();
      this.coverRenderer.setSize(mop.subHit, CoverLib.getThickness(mop.subHit, CoverLib.damageToCoverValue(md)));
      this.context.setIcon(CoverRenderer.coverIcons[md & 0xFF]);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 1);
      GL11.glDepthMask(false);
      GL11.glPolygonOffset(-3.0F, -3.0F);
      GL11.glEnable(32823);
      Tessellator.instance.startDrawingQuads();
      this.context.setupBox();
      this.context.transform();
      this.context.doMappingBox(63);
      this.context.doLightLocal(63);
      this.context.renderAlpha(63, 0.8F);
      Tessellator.instance.draw();
      GL11.glDisable(32823);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
   }
}
