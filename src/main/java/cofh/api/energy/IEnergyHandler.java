package cofh.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyHandler extends IEnergyProvider, IEnergyReceiver {
    @Override
    int receiveEnergy(ForgeDirection var1, int var2, boolean var3);

    @Override
    int extractEnergy(ForgeDirection var1, int var2, boolean var3);

    @Override
    int getEnergyStored(ForgeDirection var1);

    @Override
    int getMaxEnergyStored(ForgeDirection var1);
}
