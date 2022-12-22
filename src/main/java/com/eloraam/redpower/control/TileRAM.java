package com.eloraam.redpower.control;

import com.eloraam.redpower.RedPowerControl;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileRAM extends TileBackplane {
   public byte[] memory = new byte[8192];

   @Override
   public int readBackplane(int addr) {
      return this.memory[addr] & 0xFF;
   }

   @Override
   public void writeBackplane(int addr, int val) {
      this.memory[addr] = (byte)val;
   }

   @Override
   public Block getBlockType() {
      return RedPowerControl.blockBackplane;
   }

   @Override
   public int getExtendedID() {
      return 1;
   }

   @Override
   public void addHarvestContents(List<ItemStack> ist) {
      ist.add(new ItemStack(RedPowerControl.blockBackplane, 1, 0));
      ist.add(new ItemStack(RedPowerControl.blockBackplane, 1, 1));
   }

   @Override
   public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {
      if (willHarvest) {
         CoreLib.dropItem(super.worldObj, super.xCoord, super.yCoord, super.zCoord, new ItemStack(RedPowerControl.blockBackplane, 1, 1));
      }

      super.worldObj.setBlock(super.xCoord, super.yCoord, super.zCoord, RedPowerControl.blockBackplane);
      TileBackplane tb = CoreLib.getTileEntity(super.worldObj, super.xCoord, super.yCoord, super.zCoord, TileBackplane.class);
      if (tb != null) {
         tb.Rotation = super.Rotation;
      }

      this.updateBlockChange();
   }

   @Override
   public void setPartBounds(BlockMultipart block, int part) {
      if (part == 0) {
         super.setPartBounds(block, part);
      } else {
         block.setBlockBounds(0.0F, 0.125F, 0.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   @Override
   public int getSolidPartsMask() {
      return 3;
   }

   @Override
   public int getPartsMask() {
      return 3;
   }

   @Override
   public void readFromNBT(NBTTagCompound data) {
      super.readFromNBT(data);
      this.memory = data.getByteArray("ram");
      if (this.memory.length != 8192) {
         this.memory = new byte[8192];
      }

   }

   @Override
   public void writeToNBT(NBTTagCompound data) {
      super.writeToNBT(data);
      data.setByteArray("ram", this.memory);
   }
}
