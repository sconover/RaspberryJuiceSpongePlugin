package com.giantpurplekitty.raspberrysponge;

import org.spongepowered.api.Server;
import org.spongepowered.api.text.Texts;

public class ServerWrapper {
  private final Server server;

  public ServerWrapper(Server server) {
    this.server = server;
  }

  public void broadcastMessage(String chatStr) {
    server.getBroadcastSink().sendMessage(Texts.of(chatStr));
  }
}
