package com.eloraam.redpower.machine;

import java.util.ArrayList;
import java.util.List;

import com.eloraam.redpower.RedPowerMachine;
import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.IFrameLink;
import com.eloraam.redpower.core.IFrameSupport;
import com.eloraam.redpower.core.RedPowerLib;
import com.eloraam.redpower.core.TileMultipart;
import com.eloraam.redpower.core.WorldCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

public class TileFrameMoving extends TileMultipart implements IFrameLink {
    private TileFrameMoving.FrameBlockAccess frameBlockAccess
        = new TileFrameMoving.FrameBlockAccess();
    public int motorX;
    public int motorY;
    public int motorZ;
    public Block movingBlock = Blocks.air;
    public int movingBlockMeta = 0;
    public boolean movingCrate = false;
    public TileEntity movingTileEntity = null;
    public byte lastMovePos = 0;

    @Override
    public boolean isFrameMoving() {
        return true;
    }

    @Override
    public boolean canFrameConnectIn(int dir) {
        return true;
    }

    @Override
    public boolean canFrameConnectOut(int dir) {
        return true;
    }

    @Override
    public WorldCoord getFrameLinkset() {
        return new WorldCoord(this.motorX, this.motorY, this.motorZ);
    }

    @Override
    public int getExtendedID() {
        return 1;
    }

    @Override
    public void onBlockNeighborChange(Block block) {}

    public Block getBlockType() {
        return RedPowerMachine.blockFrame;
    }

    @Override
    public int getPartsMask() {
        return this.movingBlock == Blocks.air ? 0 : 536870912;
    }

    @Override
    public int getSolidPartsMask() {
        return this.movingBlock == Blocks.air ? 0 : 536870912;
    }

    @Override
    public boolean blockEmpty() {
        return false;
    }

    @Override
    public void onHarvestPart(EntityPlayer player, int part, boolean willHarvest) {}

    @Override
    public void addHarvestContents(List<ItemStack> ist) {
        super.addHarvestContents(ist);
    }

    @Override
    public float getPartStrength(EntityPlayer player, int part) {
        return 0.0F;
    }

    @Override
    public void setPartBounds(BlockMultipart block, int part) {
        TileMotor tm = CoreLib.getTileEntity(
            super.worldObj, this.motorX, this.motorY, this.motorZ, TileMotor.class
        );
        if (tm != null) {
            float ofs = tm.getMoveScaled();
            switch (tm.MoveDir) {
                case 0:
                    block.setBlockBounds(0.0F, 0.0F - ofs, 0.0F, 1.0F, 1.0F - ofs, 1.0F);
                    break;
                case 1:
                    block.setBlockBounds(0.0F, 0.0F + ofs, 0.0F, 1.0F, 1.0F + ofs, 1.0F);
                    break;
                case 2:
                    block.setBlockBounds(0.0F, 0.0F, 0.0F - ofs, 1.0F, 1.0F, 1.0F - ofs);
                    break;
                case 3:
                    block.setBlockBounds(0.0F, 0.0F, 0.0F + ofs, 1.0F, 1.0F, 1.0F + ofs);
                    break;
                case 4:
                    block.setBlockBounds(0.0F - ofs, 0.0F, 0.0F, 1.0F - ofs, 1.0F, 1.0F);
                    break;
                case 5:
                    block.setBlockBounds(0.0F + ofs, 0.0F, 0.0F, 1.0F + ofs, 1.0F, 1.0F);
            }
        }
    }

    public IBlockAccess getFrameBlockAccess() {
        return this.frameBlockAccess;
    }

    public void setContents(Block bid, int md, int mx, int my, int mz, TileEntity bte) {
        this.movingBlock = bid;
        this.movingBlockMeta = md;
        this.motorX = mx;
        this.motorY = my;
        this.motorZ = mz;
        this.movingTileEntity = bte;
        if (this.movingTileEntity != null) {
            if (RedPowerMachine.FrameAlwaysCrate) {
                this.movingCrate = true;
            }

            if (!(this.movingTileEntity instanceof IFrameSupport)) {
                this.movingCrate = true;
            }
        }
    }

    public void doRefresh(IBlockAccess iba) {
        if (this.movingTileEntity instanceof IFrameSupport) {
            IFrameSupport ifs = (IFrameSupport) this.movingTileEntity;
            ifs.onFrameRefresh(iba);
        }
    }

    public void dropBlock() {
        super.worldObj.setBlock(
            super.xCoord,
            super.yCoord,
            super.zCoord,
            this.movingBlock,
            this.movingBlockMeta,
            3
        );
        if (this.movingTileEntity != null) {
            this.movingTileEntity.xCoord = super.xCoord;
            this.movingTileEntity.yCoord = super.yCoord;
            this.movingTileEntity.zCoord = super.zCoord;
            this.movingTileEntity.validate();
            super.worldObj.setTileEntity(
                super.xCoord, super.yCoord, super.zCoord, this.movingTileEntity
            );
        }

        super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
        CoreLib.markBlockDirty(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
        RedPowerLib.updateIndirectNeighbors(
            super.worldObj, super.xCoord, super.yCoord, super.zCoord, this.movingBlock
        );
    }

    private AxisAlignedBB getAABB(int dir, float dist) {
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            (double) super.xCoord,
            (double) super.yCoord,
            (double) super.zCoord,
            (double) (super.xCoord + 1),
            (double) (super.yCoord + 1),
            (double) (super.zCoord + 1)
        );
        switch (dir) {
            case 0:
                aabb.minY -= (double) dist;
                aabb.maxY -= (double) dist;
                break;
            case 1:
                aabb.minY += (double) dist;
                aabb.maxY += (double) dist;
                break;
            case 2:
                aabb.minZ -= (double) dist;
                aabb.maxZ -= (double) dist;
                break;
            case 3:
                aabb.minZ += (double) dist;
                aabb.maxZ += (double) dist;
                break;
            case 4:
                aabb.minX -= (double) dist;
                aabb.maxX -= (double) dist;
                break;
            case 5:
                aabb.minX += (double) dist;
                aabb.maxX += (double) dist;
        }

        return aabb;
    }

    void pushEntities(TileMotor tm) {
        float prev = (float) this.lastMovePos / 16.0F;
        float cur = (float) tm.MovePos / 16.0F;
        this.lastMovePos = (byte) tm.MovePos;
        float xm = 0.0F;
        float ym = 0.0F;
        float zm = 0.0F;
        switch (tm.MoveDir) {
            case 0:
                ym -= cur - prev;
                break;
            case 1:
                ym += cur - prev;
                break;
            case 2:
                zm -= cur - prev;
                break;
            case 3:
                zm += cur - prev;
                break;
            case 4:
                xm -= cur - prev;
                break;
            case 5:
                xm += cur - prev;
        }

        AxisAlignedBB aabb = this.getAABB(tm.MoveDir, cur);

        for (Entity ent : new ArrayList<Entity>(
                 super.worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb)
             )) {
            ent.moveEntity((double) xm, (double) ym, (double) zm);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        TileMotor tm = CoreLib.getTileEntity(
            super.worldObj, this.motorX, this.motorY, this.motorZ, TileMotor.class
        );
        if (tm != null && tm.MovePos >= 0) {
            this.pushEntities(tm);
        } else if (!super.worldObj.isRemote) {
            this.dropBlock();
        }
    }

    public void validate() {
        super.validate();
        if (this.movingTileEntity != null) {
            this.movingTileEntity.setWorldObj(super.worldObj);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.motorX = data.getInteger("mx");
        this.motorY = data.getInteger("my");
        this.motorZ = data.getInteger("mz");
        this.movingBlock = Block.getBlockById(data.getInteger("mbid"));
        this.movingBlockMeta = data.getInteger("mbmd");
        this.lastMovePos = data.getByte("lmp");
        if (data.hasKey("mte")) {
            NBTTagCompound mte = data.getCompoundTag("mte");
            this.movingTileEntity = TileEntity.createAndLoadEntity(mte);
        } else {
            this.movingTileEntity = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("mx", this.motorX);
        data.setInteger("my", this.motorY);
        data.setInteger("mz", this.motorZ);
        data.setInteger("mbid", Block.getIdFromBlock(this.movingBlock));
        data.setInteger("mbmd", this.movingBlockMeta);
        data.setByte("lmp", this.lastMovePos);
        if (this.movingTileEntity != null) {
            NBTTagCompound mte = new NBTTagCompound();
            this.movingTileEntity.writeToNBT(mte);
            data.setTag("mte", mte);
        }
    }

    @Override
    protected void readFromPacket(NBTTagCompound tag) {
        this.motorX = tag.getInteger("mx");
        this.motorY = tag.getInteger("my");
        this.motorZ = tag.getInteger("mz");
        this.movingBlock = Block.getBlockById(tag.getInteger("mbid"));
        this.movingBlockMeta = tag.getInteger("mbmd");
        if (this.movingBlock != Blocks.air) {
            this.movingTileEntity
                = this.movingBlock.createTileEntity(super.worldObj, this.movingBlockMeta);
            if (this.movingTileEntity != null) {
                if (!(this.movingTileEntity instanceof IFrameSupport)) {
                    this.movingCrate = true;
                    return;
                }

                this.movingTileEntity.setWorldObj(super.worldObj);
                this.movingTileEntity.xCoord = super.xCoord;
                this.movingTileEntity.yCoord = super.yCoord;
                this.movingTileEntity.zCoord = super.zCoord;
                IFrameSupport ifs = (IFrameSupport) this.movingTileEntity;
                ifs.readFramePacket(tag);
            }
        }
    }

    @Override
    protected void writeToPacket(NBTTagCompound tag) {
        tag.setInteger("mx", this.motorX);
        tag.setInteger("my", this.motorY);
        tag.setInteger("mz", this.motorZ);
        tag.setInteger("mbid", Block.getIdFromBlock(this.movingBlock));
        tag.setInteger("mbmd", this.movingBlockMeta);
        if (this.movingTileEntity instanceof IFrameSupport) {
            IFrameSupport ifs = (IFrameSupport) this.movingTileEntity;
            ifs.writeFramePacket(tag);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        TileMotor tm = CoreLib.getTileEntity(
            super.worldObj, this.motorX, this.motorY, this.motorZ, TileMotor.class
        );
        if (tm != null && tm.MovePos >= 0) {
            float prev = (float) this.lastMovePos / 16.0F;
            float cur = (float) tm.MovePos / 16.0F;
            this.lastMovePos = (byte) tm.MovePos;
            float xm = 0.0F;
            float ym = 0.0F;
            float zm = 0.0F;
            switch (tm.MoveDir) {
                case 0:
                    ym -= cur - prev;
                    break;
                case 1:
                    ym += cur - prev;
                    break;
                case 2:
                    zm -= cur - prev;
                    break;
                case 3:
                    zm += cur - prev;
                    break;
                case 4:
                    xm -= cur - prev;
                    break;
                case 5:
                    xm += cur - prev;
            }

            return super.getRenderBoundingBox().addCoord(
                (double) xm, (double) ym, (double) zm
            );
        } else {
            return super.getRenderBoundingBox();
        }
    }

    private class FrameBlockAccess implements IBlockAccess {
        private FrameBlockAccess() {}

        private TileFrameMoving getFrame(int x, int y, int z) {
            TileFrameMoving tfm = CoreLib.getTileEntity(
                TileFrameMoving.this.worldObj, x, y, z, TileFrameMoving.class
            );
            return tfm == null ? null
                               : (tfm.motorX == TileFrameMoving.this.motorX
                                          && tfm.motorY == TileFrameMoving.this.motorY
                                          && tfm.motorZ == TileFrameMoving.this.motorZ
                                      ? tfm
                                      : null);
        }

        public Block getBlock(int x, int y, int z) {
            TileFrameMoving tfm = this.getFrame(x, y, z);
            return tfm == null ? Blocks.air : tfm.movingBlock;
        }

        public TileEntity getTileEntity(int x, int y, int z) {
            TileFrameMoving tfm = this.getFrame(x, y, z);
            return tfm == null ? null : tfm.movingTileEntity;
        }

        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int value) {
            return TileFrameMoving.this.worldObj.getLightBrightnessForSkyBlocks(
                x, y, z, value
            );
        }

        public int getBlockMetadata(int x, int y, int z) {
            TileFrameMoving tfm = this.getFrame(x, y, z);
            return tfm == null ? 0 : tfm.movingBlockMeta;
        }

        public boolean isAirBlock(int i, int j, int k) {
            Block bid = this.getBlock(i, j, k);
            return bid == Blocks.air || bid.isAir(TileFrameMoving.this.worldObj, i, j, k);
        }

        public int getHeight() {
            return TileFrameMoving.this.worldObj.getHeight();
        }

        public boolean extendedLevelsInChunkCache() {
            return false;
        }

        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            return TileFrameMoving.this.worldObj.getBiomeGenForCoords(x, z);
        }

        public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
            return 0;
        }

        public boolean
        isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
            if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
                Chunk chunk
                    = TileFrameMoving.this.worldObj.getChunkProvider().provideChunk(
                        x >> 4, z >> 4
                    );
                return chunk != null && !chunk.isEmpty()
                    ? this.getBlock(x, y, z).isSideSolid(this, x, y, z, side)
                    : _default;
            } else {
                return _default;
            }
        }
    }
}
