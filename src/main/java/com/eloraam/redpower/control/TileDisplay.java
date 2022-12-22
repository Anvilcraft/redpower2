package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.IRedbusConnectable;
import com.eloraam.redpower.core.TileExtended;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;

public class TileDisplay extends TileExtended implements IRedbusConnectable, IFrameSupport {
   public byte[] screen = new byte[4000];
   public int Rotation = 0;
   public int memRow = 0;
   public int cursX = 0;
   public int cursY = 0;
   public int cursMode = 2;
   public int kbstart = 0;
   public int kbpos = 0;
   public int blitXS = 0;
   public int blitYS = 0;
   public int blitXD = 0;
   public int blitYD = 0;
   public int blitW = 0;
   public int blitH = 0;
   public int blitMode = 0;
   public byte[] kbbuf = new byte[16];
   private int rbaddr = 1;

   public TileDisplay() {
      Arrays.fill(this.screen, (byte)32);
   }

   @Override
   public int rbGetAddr() {
      return this.rbaddr;
   }

   @Override
   public void rbSetAddr(int addr) {
      this.rbaddr = addr;
   }

   @Override
   public int rbRead(int reg) {
      if (reg >= 16 && reg < 96) {
         return this.screen[this.memRow * 80 + reg - 16];
      } else {
         switch(reg) {
            case 0:
               return this.memRow;
            case 1:
               return this.cursX;
            case 2:
               return this.cursY;
            case 3:
               return this.cursMode;
            case 4:
               return this.kbstart;
            case 5:
               return this.kbpos;
            case 6:
               return this.kbbuf[this.kbstart] & 0xFF;
            case 7:
               return this.blitMode;
            case 8:
               return this.blitXS;
            case 9:
               return this.blitYS;
            case 10:
               return this.blitXD;
            case 11:
               return this.blitYD;
            case 12:
               return this.blitW;
            case 13:
               return this.blitH;
            default:
               return 0;
         }
      }
   }

   @Override
   public void rbWrite(int reg, int dat) {
      this.markDirty();
      if (reg >= 16 && reg < 96) {
         this.screen[this.memRow * 80 + reg - 16] = (byte)dat;
      } else {
         switch(reg) {
            case 0:
               this.memRow = dat;
               if (this.memRow > 49) {
                  this.memRow = 49;
               }

               return;
            case 1:
               this.cursX = dat;
               return;
            case 2:
               this.cursY = dat;
               return;
            case 3:
               this.cursMode = dat;
               return;
            case 4:
               this.kbstart = dat & 15;
               return;
            case 5:
               this.kbpos = dat & 15;
               return;
            case 6:
               this.kbbuf[this.kbstart] = (byte)dat;
               return;
            case 7:
               this.blitMode = dat;
               return;
            case 8:
               this.blitXS = dat;
               return;
            case 9:
               this.blitYS = dat;
               return;
            case 10:
               this.blitXD = dat;
               return;
            case 11:
               this.blitYD = dat;
               return;
            case 12:
               this.blitW = dat;
               return;
            case 13:
               this.blitH = dat;
               return;
         }
      }

   }

   @Override
   public int getConnectableMask() {
      return 16777215;
   }

   @Override
   public int getConnectClass(int side) {
      return 66;
   }

   @Override
   public int getCornerPowerMode() {
      return 0;
   }

   @Override
   public void onBlockPlaced(ItemStack ist, int side, EntityLivingBase ent) {
      this.Rotation = (int)Math.floor((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5) + 1 & 3;
      if (ent instanceof EntityPlayer) {
         super.Owner = ((EntityPlayer)ent).getGameProfile();
      }

   }

   @Override
   public boolean onBlockActivated(EntityPlayer player) {
      if (!super.worldObj.isRemote) {
         player.openGui(RedPowerControl.instance, 1, super.worldObj, super.xCoord, super.yCoord, super.zCoord);
      }

      return true;
   }

   public Block getBlockType() {
      return RedPowerControl.blockPeripheral;
   }

   @Override
   public int getExtendedID() {
      return 0;
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return !this.isInvalid()
         && super.worldObj.getTileEntity(super.xCoord, super.yCoord, super.zCoord) == this
         && player.getDistanceSq((double)super.xCoord + 0.5, (double)super.yCoord + 0.5, (double)super.zCoord + 0.5) <= 64.0;
   }

   public void pushKey(byte b) {
      int np = this.kbpos + 1 & 15;
      if (np != this.kbstart) {
         this.kbbuf[this.kbpos] = b;
         this.kbpos = np;
      }

   }

   @Override
   public void updateEntity() {
      this.runblitter();
   }

   private void runblitter() {
      if (this.blitMode != 0) {
         this.markDirty();
         int w = this.blitW;
         int h = this.blitH;
         w = Math.min(w, 80 - this.blitXD);
         h = Math.min(h, 50 - this.blitYD);
         if (w >= 0 && h >= 0) {
            int doffs = this.blitYD * 80 + this.blitXD;
            switch(this.blitMode) {
               case 1:
                  for(int soffs = 0; soffs < h; ++soffs) {
                     for(int j = 0; j < w; ++j) {
                        this.screen[doffs + 80 * soffs + j] = (byte)this.blitXS;
                     }
                  }

                  this.blitMode = 0;
                  return;
               case 2:
                  for(int soffs = 0; soffs < h; ++soffs) {
                     for(int j = 0; j < w; ++j) {
                        this.screen[doffs + 80 * soffs + j] = (byte)(this.screen[doffs + 80 * soffs + j] ^ 128);
                     }
                  }

                  this.blitMode = 0;
                  return;
            }

            w = Math.min(w, 80 - this.blitXS);
            h = Math.min(h, 50 - this.blitYS);
            if (w >= 0 && h >= 0) {
               int soffs = this.blitYS * 80 + this.blitXS;
               switch(this.blitMode) {
                  case 3:
                     for(int j = 0; j < h; ++j) {
                        for(int i = 0; i < w; ++i) {
                           this.screen[doffs + 80 * j + i] = this.screen[soffs + 80 * j + i];
                        }
                     }

                     this.blitMode = 0;
                     return;
               }
            } else {
               this.blitMode = 0;
            }
         } else {
            this.blitMode = 0;
         }
      }

   }

   @Override
   public void writeFramePacket(NBTTagCompound tag) {
      tag.setInteger("rot", this.Rotation);
   }

   @Override
   public void readFramePacket(NBTTagCompound tag) {
      this.Rotation = tag.getInteger("rot");
   }

   @Override
   public void onFrameRefresh(IBlockAccess iba) {
   }

   @Override
   public void onFramePickup(IBlockAccess iba) {
   }

   @Override
   public void onFrameDrop() {
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.Rotation = data.getByte("rot");
      this.screen = data.getByteArray("fb");
      if (this.screen.length != 4000) {
         this.screen = new byte[4000];
      }

      this.memRow = data.getByte("row") & 255;
      this.cursX = data.getByte("cx") & 255;
      this.cursY = data.getByte("cy") & 255;
      this.cursMode = data.getByte("cm") & 255;
      this.kbstart = data.getByte("kbs");
      this.kbpos = data.getByte("kbp");
      this.kbbuf = data.getByteArray("kbb");
      if (this.kbbuf.length != 16) {
         this.kbbuf = new byte[16];
      }

      this.blitXS = data.getByte("blxs") & 255;
      this.blitYS = data.getByte("blys") & 255;
      this.blitXD = data.getByte("blxd") & 255;
      this.blitYD = data.getByte("blyd") & 255;
      this.blitW = data.getByte("blw") & 255;
      this.blitH = data.getByte("blh") & 255;
      this.blitMode = data.getByte("blmd");
      this.rbaddr = data.getByte("rbaddr") & 255;
   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      data.setByte("rot", (byte)this.Rotation);
      data.setByteArray("fb", this.screen);
      data.setByte("row", (byte)this.memRow);
      data.setByte("cx", (byte)this.cursX);
      data.setByte("cy", (byte)this.cursY);
      data.setByte("cm", (byte)this.cursMode);
      data.setByte("kbs", (byte)this.kbstart);
      data.setByte("kbp", (byte)this.kbpos);
      data.setByteArray("kbb", this.kbbuf);
      data.setByte("blxs", (byte)this.blitXS);
      data.setByte("blys", (byte)this.blitYS);
      data.setByte("blxd", (byte)this.blitXD);
      data.setByte("blyd", (byte)this.blitYD);
      data.setByte("blw", (byte)this.blitW);
      data.setByte("blh", (byte)this.blitH);
      data.setByte("blmd", (byte)this.blitMode);
      data.setByte("rbaddr", (byte)this.rbaddr);
   }

   @Override
   protected void readFromPacket(NBTTagCompound tag) {
      this.Rotation = tag.getInteger("rot");
   }

   @Override
   protected void writeToPacket(NBTTagCompound tag) {
      tag.setInteger("rot", this.Rotation);
   }
}
