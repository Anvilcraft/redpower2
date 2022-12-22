package com.eloraam.redpower.core;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketHandler {
   public SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("Redpower2");

   public void init() {
      this.netHandler.registerMessage(PacketGuiEvent.class, PacketGuiEvent.GuiMessageEvent.class, 1, Side.CLIENT);
      this.netHandler.registerMessage(PacketGuiEvent.class, PacketGuiEvent.GuiMessageEvent.class, 1, Side.SERVER);
   }

   public void sendTo(IMessage message, EntityPlayerMP player) {
      this.netHandler.sendTo(message, player);
   }

   public void sendToAllAround(IMessage message, TargetPoint point) {
      this.netHandler.sendToAllAround(message, point);
   }

   public void sendToDimension(IMessage message, int dimensionId) {
      this.netHandler.sendToDimension(message, dimensionId);
   }

   public void sendToServer(IMessage message) {
      this.netHandler.sendToServer(message);
   }
}
