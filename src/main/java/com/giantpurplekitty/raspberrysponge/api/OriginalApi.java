package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3d;
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

import static com.giantpurplekitty.raspberrysponge.game.EntityHelper.getEntityById;
import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getBlockTypeForIntegerId;
import static com.giantpurplekitty.raspberrysponge.game.Util.blockPositionRelativeTo;
import static com.giantpurplekitty.raspberrysponge.game.Util.calculateDirection;

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
    world_setBlock(x, y, z, blockTypeId, (short) 0);
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

  @RPC("player.setTile")
  public void player_setTile(int relativeX, int relativeY, int relativeZ) {
    player_setTile(gameWrapper.getFirstPlayer().getName(), relativeX, relativeY, relativeZ);
  }

  @RPC("player.setTile")
  public void player_setTile(String playerName, int relativeX, int relativeY, int relativeZ) {
    teleportEntityRelativeToOriginTo(gameWrapper.getPlayerByName(playerName),
        relativeX, relativeY, relativeZ);
  }

  @RPC("player.getPos")
  public Vector3d player_getPos() {
    //TODO: what do we do here if there's no player logged in?
    return player_getPos(gameWrapper.getFirstPlayer().getName());
  }

  @RPC("player.getPos")
  public Vector3d player_getPos(String playerName) {
    return getEntityBlockPositionRelativeToOrigin(
        gameWrapper.getPlayerByName(playerName)).toDouble();
  }

  @RPC("player.setPos")
  public void player_setPos(float relativeX, float relativeY, float relativeZ) {
    player_setPos(gameWrapper.getFirstPlayer().getName(), relativeX, relativeY, relativeZ);
  }

  @RPC("player.setPos")
  public void player_setPos(String playerName, float relativeX, float relativeY, float relativeZ) {
    teleportEntityRelativeToOriginTo(gameWrapper.getPlayerByName(playerName),
        relativeX, relativeY, relativeZ);
  }

  //// TODO: all of these need javadoc
  //
  @RPC("player.getDirection")
  public Direction player_getDirection() {
    //TODO: what do we do here if there's no player logged in?
    return player_getDirection(gameWrapper.getFirstPlayer().getName());
  }

  @RPC("player.getDirection")
  public Direction player_getDirection(String playerName) {
    return getEntityDirection(gameWrapper.getPlayerByName(playerName));
  }

  @RPC("player.getPitch")
  public float player_getPitch() {
    //TODO: what do we do here if there's no player logged in?
    return player_getPitch(gameWrapper.getFirstPlayer().getName());
  }

  @RPC("player.getPitch")
  public float player_getPitch(String playerName) {
    return (float) gameWrapper.getPlayerByName(playerName).getRotation().getY();
  }

  @RPC("player.getRotation")
  public float player_getRotation() {
    //TODO: what do we do here if there's no player logged in?
    return player_getRotation(gameWrapper.getFirstPlayer().getName());
  }

  @RPC("player.getRotation")
  public float player_getRotation(String playerName) {
    return (float) gameWrapper.getPlayerByName(playerName).getRotation().getX();
  }

  @RPC("entity.getTile")
  public Vector3i entity_getTile(int entityId) {
    return getEntityBlockPositionRelativeToOrigin(getEntityById(gameWrapper, entityId));
  }

  @RPC("entity.setTile")
  public void entity_setTile(int entityId, int relativeX, int relativeY, int relativeZ) {
    teleportEntityRelativeToOriginTo(getEntityById(gameWrapper, entityId), relativeX, relativeY,
        relativeZ);
  }

  @RPC("entity.getPos")
  public Vector3d entity_getPos(int entityId) {
    return getEntityBlockPositionRelativeToOrigin(getEntityById(gameWrapper, entityId)).toDouble();
  }

  @RPC("entity.setPos")
  public void entity_setPos(int entityId, float relativeX, float relativeY, float relativeZ) {
    teleportEntityRelativeToOriginTo(getEntityById(gameWrapper, entityId),
        relativeX, relativeY, relativeZ);
  }

  @RPC("entity.getDirection")
  public Direction entity_getDirection(int entityId) {
    return getEntityDirection(getEntityById(gameWrapper, entityId));
  }

  @RPC("entity.getPitch")
  public float entity_getPitch(int entityId) {
    return (float) getEntityById(gameWrapper, entityId).getRotation().getY();
  }

  @RPC("entity.getRotation")
  public float entity_getRotation(int entityId) {
    return (float) getEntityById(gameWrapper, entityId).getRotation().getX();
  }

  private Vector3i getOrigin() {
    return gameWrapper.getSpawnPosition();
  }

  private Direction getEntityDirection(Entity entity) {
    return new Direction(
        calculateDirection(
            entity.getRotation().getY(),
            entity.getRotation().getX()));
  }

  // Direction is just a marker class so we know to render doubles to a different precision
  // than normal, with Vector3d's
  public static class Direction {
    public final Vector3d vector;

    public Direction(Vector3d vector) {
      this.vector = vector;
    }
  }

  //private Vector3d getEntityPositionRelativeToOrigin(Entity entity) {
  //  return blockPositionRelativeTo(entity.getLocation(), getOrigin());
  //}

  private Vector3i getEntityBlockPositionRelativeToOrigin(Entity entity) {
    return blockPositionRelativeTo(entity.getLocation().getBlockPosition(), getOrigin());
  }

  private void teleportEntityRelativeToOriginTo(Entity entity, float x, float y, float z) {
    entity.setLocation(entity.getLocation().setPosition(getOrigin().toDouble().add(x, y, z)));
  }

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