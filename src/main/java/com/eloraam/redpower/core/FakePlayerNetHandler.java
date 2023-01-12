package com.eloraam.redpower.core;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

public class FakePlayerNetHandler extends NetHandlerPlayServer {
    public FakePlayerNetHandler(MinecraftServer server, FakePlayer player) {
        super(server, new NetworkManager(false), player);
    }
}
