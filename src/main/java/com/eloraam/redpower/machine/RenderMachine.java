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
public class RenderMachine extends RenderCustomBlock {
    protected RenderContext context = new RenderContext();

    public RenderMachine(Block block) {
        super(block);
    }

    public void renderTileEntityAt(
        TileEntity tile, double x, double y, double z, float partialTicks
    ) {
        TileMachine machine = (TileMachine) tile;
        World world = machine.getWorldObj();
        int metadata = machine.getBlockMetadata();
        GL11.glDisable(2896);
        Tessellator tess = Tessellator.instance;
        this.context.bindBlockTexture();
        this.context.setDefaults();
        this.context.setLocalLights(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
        this.context.setPos(x, y, z);
        this.context.readGlobalLights(
            world, machine.xCoord, machine.yCoord, machine.zCoord
        );
        this.context.setBrightness(this.getMixedBrightness(machine));
        if (machine.getBlockType() == RedPowerMachine.blockMachine) {
            switch (metadata) {
                case 0:
                    this.context.setIcon(
                        RedPowerMachine.deployerBack,
                        machine.Active ? RedPowerMachine.deployerFrontOn
                                       : RedPowerMachine.deployerFront,
                        RedPowerMachine.deployerSideAlt,
                        RedPowerMachine.deployerSideAlt,
                        RedPowerMachine.deployerSide,
                        RedPowerMachine.deployerSide
                    );
                    break;
                case 1:
                case 2:
                case 3:
                case 6:
                case 7:
                case 9:
                case 11:
                default: { //TODO: WTF?
                    IIcon side = metadata == 3
                        ? (machine.Active ? RedPowerMachine.filterSideOn
                                          : RedPowerMachine.filterSide)
                        : (machine.Active ? RedPowerMachine.transposerSideOn
                                          : RedPowerMachine.transposerSide);
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.transposerFront,
                        side,
                        side,
                        side,
                        side
                    );
                    break;
                }
                case 4: {
                    IIcon alt = machine.Active ? RedPowerMachine.detectorSideAltOn
                                               : RedPowerMachine.detectorSideAlt;
                    IIcon side;
                    if (machine.Charged) {
                        side = machine.Active ? RedPowerMachine.detectorSideChargedOn
                                              : RedPowerMachine.detectorSideCharged;
                    } else {
                        side = machine.Active ? RedPowerMachine.detectorSideOn
                                              : RedPowerMachine.detectorSide;
                    }

                    this.context.setIcon(
                        RedPowerMachine.regulatorBack,
                        RedPowerMachine.regulatorFront,
                        alt,
                        alt,
                        side,
                        side
                    );
                    break;
                }
                case 5: {
                    IIcon side;
                    if (machine.Charged) {
                        side = machine.Active ? RedPowerMachine.sorterSideChargedOn
                                              : RedPowerMachine.sorterSideCharged;
                    } else {
                        side = machine.Active ? RedPowerMachine.sorterSideOn
                                              : RedPowerMachine.sorterSide;
                    }

                    this.context.setIcon(
                        machine.Charged
                            ? (machine.Active ? RedPowerMachine.sorterBackChargedOn
                                              : RedPowerMachine.sorterBackCharged)
                            : RedPowerMachine.sorterBack,
                        RedPowerMachine.sorterFront,
                        side,
                        side,
                        side,
                        side
                    );
                    break;
                }
                case 8: {
                    IIcon side;
                    if (machine.Charged) {
                        side = machine.Active ? RedPowerMachine.retrieverSideChargedOn
                                              : RedPowerMachine.retrieverSideCharged;
                    } else {
                        side = machine.Active ? RedPowerMachine.retrieverSideOn
                                              : RedPowerMachine.retrieverSide;
                    }

                    this.context.setIcon(
                        RedPowerMachine.retrieverBack,
                        RedPowerMachine.retrieverFront,
                        side,
                        side,
                        side,
                        side
                    );
                    break;
                }
                case 10: {
                    IIcon alt = machine.Active ? RedPowerMachine.regulatorSideAltCharged
                                               : RedPowerMachine.regulatorSideAlt;
                    IIcon side;
                    if (machine.Powered) {
                        side = machine.Active ? RedPowerMachine.regulatorSideChargedOn
                                              : RedPowerMachine.regulatorSideCharged;
                    } else {
                        side = machine.Active ? RedPowerMachine.regulatorSideOn
                                              : RedPowerMachine.regulatorSide;
                    }

                    this.context.setIcon(
                        RedPowerMachine.regulatorBack,
                        RedPowerMachine.regulatorFront,
                        alt,
                        alt,
                        side,
                        side
                    );
                    break;
                }
                case 12:
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        machine.Active ? RedPowerMachine.igniterFrontOn
                                       : RedPowerMachine.igniterFront,
                        RedPowerMachine.igniterSideAlt,
                        RedPowerMachine.igniterSideAlt,
                        RedPowerMachine.igniterSide,
                        RedPowerMachine.igniterSide
                    );
                    break;
                case 13:
                    this.context.setIcon(
                        machine.Active ? RedPowerMachine.assemblerBackOn
                                       : RedPowerMachine.assemblerBack,
                        machine.Active ? RedPowerMachine.assemblerFrontOn
                                       : RedPowerMachine.assemblerFront,
                        RedPowerMachine.assemblerSideAlt,
                        RedPowerMachine.assemblerSideAlt,
                        RedPowerMachine.assemblerSide,
                        RedPowerMachine.assemblerSide
                    );
                    break;
                case 14: {
                    IIcon side = machine.Active ? RedPowerMachine.ejectorSideOn
                                                : RedPowerMachine.ejectorSide;
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.bufferFront,
                        side,
                        side,
                        RedPowerMachine.relaySideAlt,
                        RedPowerMachine.relaySideAlt
                    );
                    break;
                }
                case 15: {
                    IIcon side = machine.Active ? RedPowerMachine.relaySideOn
                                                : RedPowerMachine.relaySide;
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.bufferFront,
                        side,
                        side,
                        RedPowerMachine.relaySideAlt,
                        RedPowerMachine.relaySideAlt
                    );
                }
            }
        } else if (machine.getBlockType() == RedPowerMachine.blockMachine2) {
            switch (metadata) {
                case 0: {
                    IIcon side = machine.Charged
                        ? (machine.Active ? RedPowerMachine.sortronSideChargedOn
                                          : RedPowerMachine.sortronSideCharged)
                        : (machine.Active ? RedPowerMachine.sortronSideOn
                                          : RedPowerMachine.sortronSide);
                    IIcon alt = machine.Charged ? RedPowerMachine.sortronSideAltCharged
                                                : RedPowerMachine.sortronSideAlt;
                    this.context.setIcon(
                        RedPowerMachine.sortronBack,
                        RedPowerMachine.sortronFront,
                        alt,
                        alt,
                        side,
                        side
                    );
                    break;
                }
                case 1: {
                    IIcon side
                        = (machine.Charged ? RedPowerMachine.managerSideCharged
                                           : RedPowerMachine.managerSide
                        )[(machine.Active ? 1 : 0)
                          + (!machine.Delay && !machine.Powered ? 0 : 2)];
                    this.context.setIcon(
                        RedPowerMachine.managerBack,
                        RedPowerMachine.managerFront,
                        side,
                        side,
                        side,
                        side
                    );
                }
            }
        }

        this.context.setSize(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.context.setupBox();
        this.context.transform();
        this.context.orientTextures(machine.Rotation);
        tess.startDrawingQuads();
        this.context.renderGlobFaces(63);
        tess.draw();
        GL11.glEnable(2896);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Block block = Block.getBlockFromItem(item.getItem());
        int meta = item.getItemDamage();
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
        if (block == RedPowerMachine.blockMachine) {
            switch (meta) {
                case 0:
                    this.context.setIcon(
                        RedPowerMachine.deployerBack,
                        RedPowerMachine.deployerFront,
                        RedPowerMachine.deployerSideAlt,
                        RedPowerMachine.deployerSideAlt,
                        RedPowerMachine.deployerSide,
                        RedPowerMachine.deployerSide
                    );
                    break;
                case 1:
                case 3:
                case 6:
                case 7:
                case 9:
                case 11:
                default:
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.transposerFront,
                        RedPowerMachine.filterSide,
                        RedPowerMachine.filterSide,
                        RedPowerMachine.filterSide,
                        RedPowerMachine.filterSide
                    );
                    break;
                case 2:
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.transposerFront,
                        RedPowerMachine.transposerSide,
                        RedPowerMachine.transposerSide,
                        RedPowerMachine.transposerSide,
                        RedPowerMachine.transposerSide
                    );
                    break;
                case 4:
                    this.context.setIcon(
                        RedPowerMachine.regulatorBack,
                        RedPowerMachine.regulatorFront,
                        RedPowerMachine.regulatorSideAlt,
                        RedPowerMachine.regulatorSideAlt,
                        RedPowerMachine.regulatorSide,
                        RedPowerMachine.regulatorSide
                    );
                    break;
                case 5:
                    this.context.setIcon(
                        RedPowerMachine.sorterBack,
                        RedPowerMachine.sorterFront,
                        RedPowerMachine.sorterSide,
                        RedPowerMachine.sorterSide,
                        RedPowerMachine.sorterSide,
                        RedPowerMachine.sorterSide
                    );
                    break;
                case 8:
                    this.context.setIcon(
                        RedPowerMachine.retrieverBack,
                        RedPowerMachine.retrieverFront,
                        RedPowerMachine.retrieverSide,
                        RedPowerMachine.retrieverSide,
                        RedPowerMachine.retrieverSide,
                        RedPowerMachine.retrieverSide
                    );
                    break;
                case 10:
                    this.context.setIcon(
                        RedPowerMachine.regulatorBack,
                        RedPowerMachine.regulatorFront,
                        RedPowerMachine.regulatorSide,
                        RedPowerMachine.regulatorSide,
                        RedPowerMachine.regulatorSideAlt,
                        RedPowerMachine.regulatorSideAlt
                    );
                    break;
                case 12:
                    this.context.setIcon(
                        RedPowerMachine.deployerBack,
                        RedPowerMachine.igniterFront,
                        RedPowerMachine.igniterSideAlt,
                        RedPowerMachine.igniterSideAlt,
                        RedPowerMachine.igniterSide,
                        RedPowerMachine.igniterSide
                    );
                    break;
                case 13:
                    this.context.setIcon(
                        RedPowerMachine.assemblerBack,
                        RedPowerMachine.assemblerFront,
                        RedPowerMachine.assemblerSideAlt,
                        RedPowerMachine.assemblerSideAlt,
                        RedPowerMachine.assemblerSide,
                        RedPowerMachine.assemblerSide
                    );
                    break;
                case 14:
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.bufferFront,
                        RedPowerMachine.ejectorSide,
                        RedPowerMachine.ejectorSide,
                        RedPowerMachine.relaySideAlt,
                        RedPowerMachine.relaySideAlt
                    );
                    break;
                case 15:
                    this.context.setIcon(
                        RedPowerMachine.breakerBack,
                        RedPowerMachine.bufferFront,
                        RedPowerMachine.relaySide,
                        RedPowerMachine.relaySide,
                        RedPowerMachine.relaySideAlt,
                        RedPowerMachine.relaySideAlt
                    );
            }
        } else if (block == RedPowerMachine.blockMachine2) {
            switch (meta) {
                case 0:
                    this.context.setIcon(
                        RedPowerMachine.sortronBack,
                        RedPowerMachine.sortronFront,
                        RedPowerMachine.sortronSideAlt,
                        RedPowerMachine.sortronSideAlt,
                        RedPowerMachine.sortronSide,
                        RedPowerMachine.sortronSide
                    );
                    break;
                case 1:
                    IIcon side = RedPowerMachine.managerSide[0];
                    this.context.setIcon(
                        RedPowerMachine.managerBack,
                        RedPowerMachine.managerFront,
                        side,
                        side,
                        side,
                        side
                    );
            }
        }

        this.context.renderBox(63, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        tess.draw();
        this.context.useNormal = false;
    }
}
