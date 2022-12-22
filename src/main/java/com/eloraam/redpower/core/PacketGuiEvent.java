package com.eloraam.redpower.core;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

public class PacketGuiEvent implements IMessageHandler<PacketGuiEvent.GuiMessageEvent, IMessage> {
   public IMessage onMessage(PacketGuiEvent.GuiMessageEvent message, MessageContext context) {
      if (context.netHandler instanceof NetHandlerPlayServer) {
         EntityPlayerMP player = ((NetHandlerPlayServer)context.netHandler).playerEntity;
         if (player.openContainer != null && player.openContainer.windowId == message.windowId && player.openContainer instanceof IHandleGuiEvent) {
            IHandleGuiEvent ihge = (IHandleGuiEvent)player.openContainer;
            ihge.handleGuiEvent(message);
         }
      } else if (context.netHandler instanceof NetHandlerPlayClient) {
         EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
         if (player.openContainer != null && player.openContainer.windowId == message.windowId && player.openContainer instanceof IHandleGuiEvent) {
            IHandleGuiEvent ihge = (IHandleGuiEvent)player.openContainer;
            ihge.handleGuiEvent(message);
         }
      }

      return null;
   }

   public static class GuiMessageEvent implements IMessage {
      public int eventId = -1;
      public int windowId = -1;
      public byte[] parameters;

      public GuiMessageEvent() {
      }

      public GuiMessageEvent(int eventId, int windowId, byte... params) {
         this.eventId = eventId;
         this.windowId = windowId;
         this.parameters = params;
      }

      public void fromBytes(ByteBuf dataStream) {
         this.eventId = dataStream.readInt();
         this.windowId = dataStream.readInt();
         this.parameters = new byte[dataStream.readableBytes()];
         dataStream.readBytes(this.parameters);
      }

      public void toBytes(ByteBuf dataStream) {
         dataStream.writeInt(this.eventId);
         dataStream.writeInt(this.windowId);
         dataStream.writeBytes(this.parameters);
      }
   }
}
