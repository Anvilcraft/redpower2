package cofh.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyTransport extends IEnergyProvider, IEnergyReceiver {
   @Override
   int getEnergyStored(ForgeDirection var1);

   IEnergyTransport.InterfaceType getTransportState(ForgeDirection var1);

   boolean setTransportState(IEnergyTransport.InterfaceType var1, ForgeDirection var2);

   public static enum InterfaceType {
      SEND,
      RECEIVE,
      BALANCE;

      public IEnergyTransport.InterfaceType getOpposite() {
         return this == BALANCE ? BALANCE : (this == SEND ? RECEIVE : SEND);
      }

      public IEnergyTransport.InterfaceType rotate() {
         return this.rotate(true);
      }

      public IEnergyTransport.InterfaceType rotate(boolean forward) {
         if (forward) {
            return this == BALANCE ? RECEIVE : (this == RECEIVE ? SEND : BALANCE);
         } else {
            return this == BALANCE ? SEND : (this == SEND ? RECEIVE : BALANCE);
         }
      }
   }
}
