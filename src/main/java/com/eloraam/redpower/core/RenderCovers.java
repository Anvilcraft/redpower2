package com.eloraam.redpower.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;

@SideOnly(Side.CLIENT)
public abstract class RenderCovers extends RenderCustomBlock {
   protected CoverRenderer coverRenderer;
   protected RenderContext context = new RenderContext();

   public RenderCovers(Block block) {
      super(block);
      this.coverRenderer = new CoverRenderer(this.context);
   }

   public void renderCovers(int uc, short[] covs) {
      this.coverRenderer.render(uc, covs);
   }
}
