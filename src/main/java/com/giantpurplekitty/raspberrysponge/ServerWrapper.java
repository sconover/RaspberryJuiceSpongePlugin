package com.giantpurplekitty.raspberrysponge;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Server;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.World;

public class ServerWrapper {
  private final Server server;

  public ServerWrapper(Server server) {
    this.server = server;
  }

  public void broadcastMessage(String chatStr) {
    server.getBroadcastSink().sendMessage(Texts.of(chatStr));
  }

  public World getWorld() {
    return server.getWorld("world").get();
  }

  public Vector3i getSpawnPosition() {
    return getWorld().getSpawnLocation().getBlockPosition();
  }
}
