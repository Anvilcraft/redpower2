package cofh.api.energy;

public interface IEnergyStorage {
    int receiveEnergy(int var1, boolean var2);

    int extractEnergy(int var1, boolean var2);

    int getEnergyStored();

    int getMaxEnergyStored();
}
