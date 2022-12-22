package com.eloraam.redpower.machine;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.IConnectable;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.WorldCoord;
import cpw.mods.fml.common.eventhandler.Event.Result;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public abstract class TileDeployBase extends TileMachine implements IFrameLink, IConnectable {
   @Override
   public boolean isFrameMoving() {
      return false;
   }

   @Override
   public boolean canFrameConnectIn(int dir) {
      return dir != (super.Rotation ^ 1);
   }

   @Override
   public boolean canFrameConnectOut(int dir) {
      return false;
   }

   @Override
   public WorldCoord getFrameLinkset() {
      return null;
   }

   @Override
   public int getConnectableMask() {
      return 1073741823 ^ RedPowerLib.getConDirMask(super.Rotation ^ 1);
   }

   @Override
   public int getConnectClass(int side) {
      return 0;
   }

   @Override
   public int getCornerPowerMode() {
      return 0;
   }

   @Override
   public Block getBlockType() {
      return RedPowerMachine.blockMachine;
   }

   protected static Entity traceEntities(World world, Entity exclude, Vec3 vs, Vec3 vlook) {
      AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(vs.xCoord, vs.yCoord, vs.zCoord, vs.xCoord, vs.yCoord, vs.zCoord);
      List<? extends Entity> elist = world.getEntitiesWithinAABBExcludingEntity(
         exclude, aabb.addCoord(vlook.xCoord, vlook.yCoord, vlook.zCoord).expand(1.0, 1.0, 1.0)
      );
      Vec3 v2 = vs.addVector(vlook.xCoord, vlook.yCoord, vlook.zCoord);
      Entity entHit = null;
      double edis = 0.0;

      for(Entity ent : elist) {
         if (ent.canBeCollidedWith()) {
            float cbs = ent.getCollisionBorderSize();
            AxisAlignedBB ab2 = ent.boundingBox.expand((double)cbs, (double)cbs, (double)cbs);
            if (ab2.isVecInside(vs)) {
               entHit = ent;
               break;
            }

            MovingObjectPosition mop = ab2.calculateIntercept(vs, v2);
            if (mop != null) {
               double d = vs.distanceTo(mop.hitVec);
               if (d < edis || edis == 0.0) {
                  entHit = ent;
                  edis = d;
               }
            }
         }
      }

      return entHit;
   }

   protected boolean useOnEntity(Entity ent, FakePlayer player) {
      if (ent.interactFirst(player)) {
         return true;
      } else {
         ItemStack ist = player.getCurrentEquippedItem();
         if (ist != null && ent instanceof EntityLiving) {
            int iss = ist.stackSize;
            ist.interactWithEntity(player, (EntityLivingBase)ent);
            if (ist.stackSize != iss) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean tryUseItemStack(ItemStack ist, int x, int y, int z, int slot, FakePlayer player) {
      player.inventory.currentItem = slot;
      WorldCoord wc = new WorldCoord(this);
      wc.step(super.Rotation ^ 1);
      if (!ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, wc.x, wc.y, wc.z, super.Rotation ^ 1, super.worldObj).isCanceled()) {
         if (ist.getItem() != Items.dye && ist.getItem() != Items.minecart && ist.getItem() != Items.furnace_minecart && ist.getItem() != Items.chest_minecart) {
            if (ist.getItem().onItemUseFirst(ist, player, super.worldObj, x, y, z, 1, 0.5F, 0.5F, 0.5F)) {
               return true;
            }

            if (ist.getItem().onItemUse(ist, player, super.worldObj, x, y - 1, z, 1, 0.5F, 0.5F, 0.5F)) {
               return true;
            }
         } else if (ist.getItem().onItemUse(ist, player, super.worldObj, x, y, z, 1, 0.5F, 0.5F, 0.5F)) {
            return true;
         }

         int iss = ist.stackSize;
         PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, super.worldObj);
         if (event.useItem != Result.DENY) {
            ItemStack ost = ist.useItemRightClick(super.worldObj, player);
            if (ost == ist && ost.stackSize == iss) {
               Vec3 lv = player.getLook(1.0F);
               lv.xCoord *= 2.5;
               lv.yCoord *= 2.5;
               lv.zCoord *= 2.5;
               Vec3 sv = Vec3.createVectorHelper((double)super.xCoord + 0.5, (double)super.yCoord + 0.5, (double)super.zCoord + 0.5);
               Entity ent = traceEntities(super.worldObj, player, sv, lv);
               return ent != null && this.useOnEntity(ent, player);
            }

            player.inventory.setInventorySlotContents(slot, ost);
            return true;
         }
      }

      return false;
   }

   public abstract void enableTowards(WorldCoord var1);

   @Override
   public void onBlockNeighborChange(Block block) {
      int cm = this.getConnectableMask();
      if (!RedPowerLib.isPowered(super.worldObj, super.xCoord, super.yCoord, super.zCoord, cm, cm >> 24)) {
         if (super.Active) {
            this.scheduleTick(5);
         }
      } else if (!super.Active) {
         super.Active = true;
         this.updateBlock();
         WorldCoord wc = new WorldCoord(this);
         wc.step(super.Rotation ^ 1);
         this.enableTowards(wc);
      }

   }

   @Override
   public void onTileTick() {
      super.Active = false;
      this.updateBlock();
   }
}
