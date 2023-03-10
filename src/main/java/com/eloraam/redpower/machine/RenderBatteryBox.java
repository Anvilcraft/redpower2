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
public class RenderBatteryBox extends RenderCustomBlock {
    protected RenderContext context = new RenderContext();

    public RenderBatteryBox(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileBatteryBox battery = (TileBatteryBox) tile;
        World world = tile.getWorldObj();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        this.context.bindBlockTexture();
        this.context.setDefaults();
        this.context.setPos(x, y, z);
        this.context.readGlobalLights(world, tile.xCoord, tile.yCoord, tile.zCoord);
        this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.context.setupBox();
        this.context.transform();
        IIcon side = RedPowerMachine.batterySide[battery.getStorageForRender()];
        this.context.setIcon(
            RedPowerMachine.electronicsBottom,
            RedPowerMachine.batteryTop,
            side,
            side,
            side,
            side
        );
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
        short bat = 0;
        if (item.stackTagCompound != null) {
            bat = item.stackTagCompound.getShort("batLevel");
        }

        IIcon side = RedPowerMachine.batterySide[bat * 8 / 6000];
        this.context.setIcon(
            RedPowerMachine.electronicsBottom,
            RedPowerMachine.batteryTop,
            side,
            side,
            side,
            side
        );
        this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        tess.draw();
        this.context.useNormal = false;
    }
}
