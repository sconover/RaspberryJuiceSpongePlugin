package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.World;

import static com.google.common.base.Preconditions.checkState;

//TODO - gamewrapper, not serverwrapper.

public class ServerWrapper {
  private final Server server;

  public ServerWrapper(Server server) {
    this.server = server;
  }

  public void broadcastMessage(String chatStr) {
    server.getBroadcastSink().sendMessage(Texts.of(chatStr));
  }

  // TODO: don't expose this. isolate all world access in here with specific convenience methods.
  public World getWorld() {
    return server.getWorld("world").get();
  }

  public Vector3i getSpawnPosition() {
    return getWorld().getSpawnLocation().getBlockPosition();
  }

  public void setSpawnPosition(Vector3i position) {
    getWorld().getProperties().setSpawnPosition(position);
  }

  public boolean hasPlayers() {
    return !server.getOnlinePlayers().isEmpty();
  }

  public Player getFirstPlayer() {
    checkState(server.getOnlinePlayers().size()==1,
        "This method only supports one logged in player. " +
        "The problem is that the collection of players is unordered, " +
        "so it's not possible to guarantee that the same player will be returned. " +
        "This is used for dev/testing only, anyway, for now, so it doesn't matter.");
    return server.getOnlinePlayers().iterator().next();
  }
}
