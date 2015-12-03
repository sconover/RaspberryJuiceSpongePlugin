package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import static com.google.common.base.Preconditions.checkState;

public class GameWrapper {
  private final Game game;
  private final World world;
  private final Server server;
  private final Map<EntityType, String> entityTypeToName;
  private final Map<String, EntityType> nameToEntityType;

  public GameWrapper(Game game) {
    this.game = game;
    this.server = game.getServer();
    this.world = game.getServer().getWorld("world").get();

    nameToEntityType = new LinkedHashMap<String, EntityType>();
    entityTypeToName = new LinkedHashMap<EntityType, String>();
    try {
      Field[] fields = EntityTypes.class.getDeclaredFields();
      for (Field f: fields) {
        Object value = f.get(EntityTypes.class);
        if (value != null) {
          EntityType entityType = (EntityType) value;

          if (extendsEntityLiving(entityType.getEntityClass())) {
            nameToEntityType.put(f.getName().toLowerCase(), entityType);
            entityTypeToName.put(entityType, f.getName().toLowerCase());
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
  }

  private static boolean extendsEntityLiving(Class c) {
    while (c != null) {
      if (c.getName().equals("net.minecraft.entity.EntityLiving")) {
        return true;
      } else {
        c = c.getSuperclass();
      }
    }
    return false;
  }

  public static Entity getEntityById(GameWrapper gameWrapper, int entityId) {
    for (Entity entity: gameWrapper.getEntities()) {
      if (entity.getEntityId() == entityId) {
        return entity;
      }
    }
    throw new RuntimeException(String.format("Couldn't find entity with id=%d", entityId));
  }

  public Optional<Entity> tryToSpawnEntity(EntityType entityType, Vector3i position) {
    Optional<Entity> maybeEntity = world.createEntity(entityType, position);
    if (maybeEntity.isPresent()) {
      Entity entity = maybeEntity.get();
      if (world.spawnEntity(entity)) {
        return Optional.of(entity);
      } else {
        return Optional.absent();
      }
    } else {
      return Optional.absent();
    }
  }

  public Optional<Entity> getEntityByUuid(String uuid) {
    return world.getEntity(UUID.fromString(uuid));
  }

  public void broadcastMessage(String chatStr) {
    server.getBroadcastSink().sendMessage(Texts.of(chatStr));
  }

  public Vector3i getSpawnPosition() {
    //return world.getSpawnLocation().getBlockPosition();
    return Vector3i.ZERO;
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
    Optional<Player> playerOptional = maybeGetPlayerByName(playerName);
    checkState(playerOptional.isPresent(),
        String.format("no player found with name '%s'", playerName));
    return playerOptional.get();
  }

  public Optional<Player> maybeGetPlayerByName(String playerName) {
    return game.getServer().getPlayer(playerName);
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
        return p1.getEntityId() - p2.getEntityId();
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

  public Map<String,EntityType> getSupportedNameToEntityType() {
    return nameToEntityType;
  }

  public Map<EntityType,String> getSupportedEntityTypeToName() {
    return entityTypeToName;
  }

  public EntityType supportedEntityTypeForName(String entityTypeName) {
    Map<String,EntityType> supportedEntityTypes = getSupportedNameToEntityType();
    List<String> allTypeNames = new ArrayList<String>(supportedEntityTypes.keySet());
    Collections.sort(allTypeNames);

    checkState(supportedEntityTypes.containsKey(entityTypeName),
        String.format("entity type '%s' not found or not supported. " +
        "Supported entity types are: %s",
            entityTypeName,
            Joiner.on(",").join(allTypeNames)));

    return supportedEntityTypes.get(entityTypeName);
  }

  public Collection<Entity> getEntities(Predicate filter) {
    return world.getEntities(filter);
  }
}
