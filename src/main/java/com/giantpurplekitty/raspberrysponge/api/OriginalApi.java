package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.dispatch.RPC;
import com.giantpurplekitty.raspberrysponge.dispatch.RawArgString;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.DataHelper;
import com.giantpurplekitty.raspberrysponge.game.GameWrapper;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;

import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getBlockTypeForIntegerId;
import static com.giantpurplekitty.raspberrysponge.game.Util.blockPositionRelativeTo;

public class OriginalApi {
  private final GameWrapper gameWrapper;
  //private final ArrayDeque<BlockRightClickHook> blockHitQueue;
  private final Logger logger;

  public OriginalApi(
      GameWrapper gameWrapper,
      //ArrayDeque<BlockRightClickHook> blockHitQueue,
      Logger logger) {
    this.gameWrapper = gameWrapper;
    //this.blockHitQueue = blockHitQueue;
    this.logger = logger;
  }

  @RPC("world.getBlock")
  public BlockType world_getBlock(int x, int y, int z) {
    return CuboidReference.relativeTo(getOrigin(), new Vector3i(x, y, z))
        .fetchBlocks(gameWrapper)
        .firstBlock()
        .getBlockType();
  }

  @RPC("world.getBlockWithData")
  public Pair<BlockType, Integer> world_getBlockWithData(int x, int y, int z) {
    BlockState blockState = gameWrapper.getBlock(x, y, z);
    return ImmutablePair.of(blockState.getType(), DataHelper.getData(blockState));
  }

  @RPC("world.setBlock")
  public void world_setBlock(int x, int y, int z, short blockTypeId) {
    world_setBlock(x, y, z, blockTypeId, (short)0);
  }

  @RPC("world.setBlock")
  public void world_setBlock(int x, int y, int z, short blockTypeId, short blockData) {
    world_setBlocks(
        x, y, z,
        x, y, z,
        blockTypeId, blockData);
  }
  
  @RPC("world.setBlocks")
  public void world_setBlocks(
      int x1, int y1, int z1,
      int x2, int y2, int z2,
      short blockTypeId) {
    world_setBlocks(
        x1, y1, z1,
        x2, y2, z2,
        blockTypeId, (short) 0);
  }
  
  @RPC("world.setBlocks")
  public void world_setBlocks(
      int x1, int y1, int z1,
      int x2, int y2, int z2,
      short blockTypeId, short blockData) {
    CuboidReference.relativeTo(getOrigin(),
        new Vector3i(x1, y1, z1),
        new Vector3i(x2, y2, z2))
        .fetchBlocks(gameWrapper)
        .changeBlocksToTypeWithData(getBlockTypeForIntegerId(blockTypeId), blockData);
  }
  
  @RPC("world.getPlayerEntityIds")
  public Player[] world_getPlayerEntityIds() {
    List<Player> allPlayers = gameWrapper.getPlayers();
    return allPlayers.toArray(new Player[allPlayers.size()]);
  }

  @RPC("world.getHeight")
  public int world_getHeight(int x, int z) {
    int relativeX = getOrigin().getX() + x;
    int relativeZ = getOrigin().getZ() + z;
    int absoluteHeight = gameWrapper.getHighestBlockYAt(relativeX, relativeZ);
    return absoluteHeight - getOrigin().getY();
  }

  @RPC("chat.post")
  public void chat_post(@RawArgString String chatStr) {
    gameWrapper.broadcastMessage(chatStr);
  }

  //@RPC("events.clear")
  //public void events_clear() {
  //  blockHitQueue.clear();
  //}
  //
  //@RPC("events.block.hits")
  //public BlockEvent[] events_block_hits() {
  //  // this doesn't work with multiplayer! need to think about how this should work
  //  // [this was an existing comment -steve]
  //
  //  List<BlockEvent> blockEventList = new ArrayList<BlockEvent>();
  //  BlockRightClickHook event;
  //  while ((event = blockHitQueue.poll()) != null) {
  //    blockEventList.add(BlockEvent.fromBlockRightClock(event, getOrigin()));
  //  }
  //  return blockEventList.toArray(new BlockEvent[blockEventList.size()]);
  //}
  //
  @RPC("player.getTile")
  public Vector3i player_getTile() {
    //TODO: what do we do here if there's no player logged in?
    return player_getTile(gameWrapper.getFirstPlayer().getName());
  }

  @RPC("player.getTile")
  public Vector3i player_getTile(String playerName) {
    return getEntityBlockPositionRelativeToOrigin(gameWrapper.getPlayerByName(playerName));
  }

  //@RPC("player.setTile")
  //public void player_setTile(int relativeX, int relativeY, int relativeZ) {
  //  player_setTile(serverWrapper.getFirstPlayer().getName(), relativeX, relativeY, relativeZ);
  //}
  //
  ////TODO: convert all (int)p.getX to p.getBlockX, etc
  //
  //@RPC("player.setTile")
  //public void player_setTile(String playerName, int relativeX, int relativeY, int relativeZ) {
  //  teleportEntityRelativeToOriginTo(serverWrapper.getPlayerByName(playerName), relativeX,
  //      relativeY, relativeZ);
  //}
  //
  //@RPC("player.getPos")
  //public Vector3i player_getPos() {
  //  //TODO: what do we do here if there's no player logged in?
  //  return player_getPos(serverWrapper.getFirstPlayer().getName());
  //}
  //
  //@RPC("player.getPos")
  //public Vector3i player_getPos(String playerName) {
  //  return getEntityPositionRelativeToOrigin(serverWrapper.getPlayerByName(playerName));
  //}
  //
  //@RPC("player.setPos")
  //public void player_setPos(float relativeX, float relativeY, float relativeZ) {
  //  player_setPos(serverWrapper.getFirstPlayer().getName(), relativeX, relativeY, relativeZ);
  //}
  //
  //@RPC("player.setPos")
  //public void player_setPos(String playerName, float relativeX, float relativeY, float relativeZ) {
  //  teleportEntityRelativeToOriginTo(serverWrapper.getPlayerByName(playerName), relativeX,
  //      relativeY, relativeZ);
  //}
  //
  //// TODO: all of these need javadoc
  //
  //@RPC("player.getDirection")
  //public Vector3D player_getDirection() {
  //  //TODO: what do we do here if there's no player logged in?
  //  return player_getDirection(serverWrapper.getFirstPlayer().getName());
  //}
  //
  //@RPC("player.getDirection")
  //public Vector3D player_getDirection(String playerName) {
  //  return getEntityDirection(serverWrapper.getPlayerByName(playerName));
  //}
  //
  //@RPC("player.getPitch")
  //public float player_getPitch() {
  //  //TODO: what do we do here if there's no player logged in?
  //  return player_getPitch(serverWrapper.getFirstPlayer().getName());
  //}
  //
  //@RPC("player.getPitch")
  //public float player_getPitch(String playerName) {
  //  return serverWrapper.getPlayerByName(playerName).getPitch();
  //}
  //
  //@RPC("player.getRotation")
  //public float player_getRotation() {
  //  //TODO: what do we do here if there's no player logged in?
  //  return player_getRotation(serverWrapper.getFirstPlayer().getName());
  //}
  //
  //@RPC("player.getRotation")
  //public float player_getRotation(String playerName) {
  //  return serverWrapper.getPlayerByName(playerName).getRotation();
  //}
  //
  //@RPC("entity.getTile")
  //public BlockPosition entity_getTile(int entityId) {
  //  return getEntityBlockPositionRelativeToOrigin(serverWrapper.getEntityById(entityId));
  //}
  //
  //@RPC("entity.setTile")
  //public void entity_setTile(int entityId, int relativeX, int relativeY, int relativeZ) {
  //  teleportEntityRelativeToOriginTo(serverWrapper.getEntityById(entityId), relativeX, relativeY,
  //      relativeZ);
  //}
  //
  //@RPC("entity.getPos")
  //public Vector3i entity_getPos(int entityId) {
  //  return getEntityPositionRelativeToOrigin(serverWrapper.getEntityById(entityId));
  //}
  //
  //@RPC("entity.setPos")
  //public void entity_setPos(int entityId, float relativeX, float relativeY, float relativeZ) {
  //  teleportEntityRelativeToOriginTo(serverWrapper.getEntityById(entityId), relativeX, relativeY,
  //      relativeZ);
  //}
  //
  //@RPC("entity.getDirection")
  //public Vector3D entity_getDirection(int entityId) {
  //  return getEntityDirection(serverWrapper.getEntityById(entityId));
  //}
  //
  //@RPC("entity.getPitch")
  //public float entity_getPitch(int entityId) {
  //  return serverWrapper.getEntityById(entityId).getPitch();
  //}
  //
  //@RPC("entity.getRotation")
  //public float entity_getRotation(int entityId) {
  //  return serverWrapper.getEntityById(entityId).getRotation();
  //}
  //
  private Vector3i getOrigin() {
    return gameWrapper.getSpawnPosition();
  }
  //
  //private Vector3D getEntityDirection(Entity entity) {
  //  return calculateDirection(entity.getPitch(), entity.getRotation());
  //}
  //
  //private Vector3d getEntityPositionRelativeToOrigin(Entity entity) {
  //  return blockPositionRelativeTo(entity.getLocation(), getOrigin());
  //}

  private Vector3i getEntityBlockPositionRelativeToOrigin(Entity entity) {
    return blockPositionRelativeTo(entity.getLocation().getBlockPosition(), getOrigin());
  }
  //
  //private void teleportEntityRelativeToOriginTo(Entity entity, double x, double y, double z) {
  //  Vector3i newPosition =
  //      new Vector3i(
  //          getOrigin().getX() + x,
  //          getOrigin().getY() + y,
  //          getOrigin().getZ() + z);
  //
  //  // maintain existing entity pitch/yaw
  //  Location newLocation = new Location(serverWrapper.getWorld(), newPosition);
  //  newLocation.setPitch(entity.getPitch());
  //  newLocation.setRotation(entity.getRotation());
  //  entity.teleportTo(newLocation);
  //}
  //
  //public static class BlockEvent {
  //  public static BlockEvent fromBlockRightClock(
  //      BlockRightClickHook blockRightClick,
  //      Vector3i relativeToPosition) {
  //
  //    Block block = blockRightClick.getBlockClicked();
  //    return new BlockEvent(
  //        blockPositionRelativeTo(block.getLocation(), relativeToPosition),
  //        block.getFaceClicked(),
  //        blockRightClick.getPlayer());
  //  }
  //
  //  private final Vector3i pos;
  //  private final BlockFace face;
  //  private final Entity entity;
  //
  //  public BlockEvent(Vector3i pos, BlockFace face, Entity entity) {
  //    this.pos = pos;
  //    this.face = face;
  //    this.entity = entity;
  //  }
  //
  //  public String toApiResult() {
  //    StringBuilder sb = new StringBuilder();
  //    sb.append(BlockPosition.fromPosition(pos).toApiResult());
  //    sb.append(",");
  //    sb.append(RemoteSession.blockFaceToNotch(face));
  //    sb.append(",");
  //    sb.append(entity.getID());
  //    return sb.toString();
  //  }
  //}
  //
  //public static class BlockPosition {
  //  public static BlockPosition fromPosition(Vector3i p) {
  //    return new BlockPosition(p.getBlockX(), p.getBlockY(), p.getBlockZ());
  //  }
  //
  //  private final int x;
  //  private final int y;
  //  private final int z;
  //
  //  public BlockPosition(int x, int y, int z) {
  //    this.x = x;
  //    this.y = y;
  //    this.z = z;
  //  }
  //
  //  public String toApiResult() {
  //    return String.format("%d,%d,%d", x, y, z);
  //  }
  //}
}