package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import static com.google.common.base.Preconditions.checkState;

public class GameWrapper {
  private final Game game;
  private final World world;
  private final Server server;
  private Entity entities;

  public GameWrapper(Game game) {
    this.game = game;
    this.server = game.getServer();
    this.world = game.getServer().getWorld("world").get();
  }

  public void broadcastMessage(String chatStr) {
    server.getBroadcastSink().sendMessage(Texts.of(chatStr));
  }

  public Vector3i getSpawnPosition() {
    return world.getSpawnLocation().getBlockPosition();
  }

  public void setSpawnPosition(Vector3i position) {
    world.getProperties().setSpawnPosition(position);
  }

  public boolean hasPlayers() {
    return !game.getServer().getOnlinePlayers().isEmpty();
  }

  public Player getFirstPlayer() {
    checkState(game.getServer().getOnlinePlayers().size()==1,
        "This method only supports one logged in player. " +
        "The problem is that the collection of players is unordered, " +
        "so it's not possible to guarantee that the same player will be returned. " +
        "This is used for dev/testing only, anyway, for now, so it doesn't matter.");
    return game.getServer().getOnlinePlayers().iterator().next();
  }

  public Player getPlayerByName(String playerName) {
    Optional<Player> playerOptional = game.getServer().getPlayer(playerName);
    checkState(playerOptional.isPresent(),
        String.format("no player found with name '%s'", playerName));
    return playerOptional.get();
  }

  public Location getLocation(Vector3i position) {
    return world.getLocation(position);
  }

  public Location getLocation(int x, int y, int z) {
    return world.getLocation(x, y, z);
  }

  public BlockState getBlock(int x, int y, int z) {
    return world.getBlock(x, y, z);
  }

  public BlockState getBlock(Vector3i position) {
    return world.getBlock(position);
  }

  public List<Player> getPlayers() {
    List<Player> playerList = new ArrayList<Player>(game.getServer().getOnlinePlayers());
    Collections.sort(playerList, new Comparator<Player>() {
      @Override public int compare(Player p1, Player p2) {
        return EntityHelper.getEntityId(p1) - EntityHelper.getEntityId(p2);
      }
    });
    return playerList;
  }

  public int getHighestBlockYAt(int x, int z) {
    for (int y=world.getBlockMax().getY(); y>=1; y--) {
      if (!world.getBlock(x, y, z).getType().equals(BlockTypes.AIR)) {
        return y;
      }
    }
    return 0;
  }

  public Collection<Entity> getEntities() {
    return world.getEntities();
  }
}
