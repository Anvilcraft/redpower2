package com.eloraam.redpower.machine;

import com.eloraam.redpower.core.BlockMultipart;

public class TileMagTube extends TileTube {
    @Override
    public int getTubeConnectableSides() {
        int tr = 63;

        for (int i = 0; i < 6; ++i) {
            if ((super.CoverSides & 1 << i) > 0 && super.Covers[i] >> 8 < 3) {
                tr &= ~(1 << i);
            }
        }

        return tr;
    }

    public int getSpeed() {
        return 128;
    }

    @Override
    public int getTubeConClass() {
        return 18 + super.paintColor;
    }

    @Override
    public void setPartBounds(BlockMultipart block, int part) {
        if (part == 29) {
            block.setBlockBounds(0.125F, 0.125F, 0.125F, 0.875F, 0.875F, 0.875F);
        } else {
            super.setPartBounds(block, part);
        }
    }

    @Override
    public int getExtendedID() {
        return 11;
    }
}
