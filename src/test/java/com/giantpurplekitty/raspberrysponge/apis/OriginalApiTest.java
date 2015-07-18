package com.giantpurplekitty.raspberrysponge.apis;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.FileHelper;
import com.giantpurplekitty.raspberrysponge.InWorldTestSupport;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Tests all api methods expected to be available on the Minecraft Pi edition.
 */
public class OriginalApiTest extends InWorldTestSupport {

  /**
   * TODO:
   *
   * - slow things down... take ticks/sec into consideration... adjust to wall clock time. e.g. 200
   * ms / write operation
   *
   * - stay end to end for a while...
   */

  @Test
  public void test_works() {
    System.out.println("HELLO FROM WORKS");
    assertEquals(1, 1);
  }

  @Test
  public void test_chat_post() throws Exception {
    String chatMessage = String.format("this-is-the-chat-message-%d", System.currentTimeMillis());

    getApiInvocationHandler().handleLine(String.format("chat.post(%s)", chatMessage));

    String last20LinesOfLogFile = FileHelper.readEndOfLogfile();

    assertTrue(
        String.format("expected '%s' to be present, but was not. full text:\n\n%s",
            chatMessage,
            last20LinesOfLogFile),
        last20LinesOfLogFile.contains(chatMessage));
  }

  @Test
  public void test_world_getBlock() throws Exception {
    Vector3i p = nextTestPosition("world.getBlock");

    Location block = getGameWrapper().getLocation(p.getX()+3, p.getY()+3, p.getZ()+3);
    block.setBlockType(BlockTypes.REDSTONE_BLOCK);

    // sanity check
    assertEquals(
        BlockTypes.REDSTONE_BLOCK,
        getGameWrapper().getLocation(p.getX() + 3, p.getY() + 3, p.getZ() + 3).getBlockType());

    getApiInvocationHandler().handleLine(
        String.format("world.getBlock(%d,%d,%d)", 3, 3, 3));

    assertEquals(
        Lists.newArrayList(String.valueOf(BlockTypes.REDSTONE_BLOCK.getId())),
        getTestOut().sends);
  }

  //@Test
  //public void test_world_getBlockWithData() throws Exception {
  //  Position p = nextTestPosition("world.getBlockWithData");
  //
  //  Block block = getServerWrapper().getWorld().getBlockAt(p);
  //  block.setType(BlockType.RedstoneBlock);
  //  block.update();
  //
  //  Block block2 = block.getRelative(1, 0, 0);
  //  block2.setType(BlockType.LimeWool);
  //  block2.update();
  //
  //  getApiInvocationHandler().handleLine(
  //      String.format("world.getBlockWithData(%d,%d,%d)",
  //          (int) p.getX(),
  //          (int) p.getY(),
  //          (int) p.getZ()));
  //
  //  assertEquals(
  //      String.format("%d,%d",
  //          BlockType.RedstoneBlock.getId(),
  //          BlockType.RedstoneBlock.getData()),
  //      getTestOut().sends.get(0));
  //
  //  getApiInvocationHandler().handleLine(
  //      String.format("world.getBlockWithData(%d,%d,%d)",
  //          (int) p.getX() + 1,
  //          (int) p.getY(),
  //          (int) p.getZ()));
  //
  //  assertEquals(
  //      String.format("%d,%d",
  //          BlockType.LimeWool.getId(),
  //          BlockType.LimeWool.getData()),
  //      getTestOut().sends.get(1));
  //}
  //
  //@Test
  //public void test_world_setBlock() throws Exception {
  //  Position p = nextTestPosition("world.setBlock");
  //
  //  getApiInvocationHandler().handleLine(
  //      String.format("world.setBlock(%d,%d,%d,%d)",
  //          (int) p.getX(),
  //          (int) p.getY(),
  //          (int) p.getZ(),
  //          BlockType.RedstoneBlock.getId()));
  //
  //  Block block = getServerWrapper().getWorld().getBlockAt(p);
  //  assertEquals(BlockType.RedstoneBlock, block.getType());
  //
  //  getApiInvocationHandler().handleLine(
  //      String.format("world.setBlock(%d,%d,%d,%d,%d)",
  //          (int) p.getX() + 1,
  //          (int) p.getY(),
  //          (int) p.getZ(),
  //          BlockType.LimeWool.getId(),
  //          BlockType.LimeWool.getData()));
  //
  //  Block block2 = getServerWrapper().getWorld().getBlockAt(
  //      (int) p.getX() + 1,
  //      (int) p.getY(),
  //      (int) p.getZ());
  //
  //  assertEquals(BlockType.LimeWool, block2.getType());
  //  assertEquals(BlockType.LimeWool.getData(), block2.getType().getData());
  //}
  //
  //@Test
  //public void test_world_setBlocks_simple() throws Exception {
  //  Position cubeCorner = nextTestPosition("world.setBlocks simple");
  //
  //  Position otherCubeCorner =
  //      new Position(
  //          cubeCorner.getX() + 1,
  //          cubeCorner.getY() + 1,
  //          cubeCorner.getZ() + 1);
  //
  //  getApiInvocationHandler().handleLine(
  //      String.format("world.setBlocks(%d,%d,%d,%d,%d,%d,%d)",
  //          (int) cubeCorner.getX(),
  //          (int) cubeCorner.getY(),
  //          (int) cubeCorner.getZ(),
  //          (int) otherCubeCorner.getX(),
  //          (int) otherCubeCorner.getY(),
  //          (int) otherCubeCorner.getZ(),
  //          BlockType.RedstoneBlock.getId()));
  //
  //  Map<BlockType, List<Block>> blockTypeToBlocks =
  //      CuboidReference.fromCorners(cubeCorner, otherCubeCorner)
  //          .fetchBlocks(getServerWrapper().getWorld())
  //          .blockTypeToBlocks();
  //
  //  // there's a 2x2x2 set of redstone blocks
  //  assertEquals(Sets.newHashSet(BlockType.RedstoneBlock), blockTypeToBlocks.keySet());
  //  assertEquals(8, blockTypeToBlocks.get(BlockType.RedstoneBlock).size());
  //
  //  Position pastOtherCubeCorner =
  //      new Position(
  //          cubeCorner.getX() + 2,
  //          cubeCorner.getY() + 2,
  //          cubeCorner.getZ() + 2);
  //
  //  Map<BlockType, List<Block>> blockTypeToBlocks2 =
  //      CuboidReference.fromCorners(cubeCorner, pastOtherCubeCorner)
  //          .fetchBlocks(getServerWrapper().getWorld())
  //          .blockTypeToBlocks();
  //
  //  // out of this 3x3x3 cube, there's a 2x2x2 set of redstone blocks,
  //  // and the rest is air
  //  assertEquals(Sets.newHashSet(
  //          BlockType.RedstoneBlock,
  //          BlockType.Air),
  //      blockTypeToBlocks2.keySet());
  //  assertEquals(8, blockTypeToBlocks2.get(BlockType.RedstoneBlock).size());
  //  assertEquals(27 - 8, blockTypeToBlocks2.get(BlockType.Air).size());
  //}
  //
  //@Test
  //public void test_world_setBlocks_withData_whichIsTheColor() throws Exception {
  //  Position cubeCorner = nextTestPosition("world.setBlocks with data");
  //
  //  Position otherCubeCorner =
  //      new Position(
  //          cubeCorner.getX() + 1,
  //          cubeCorner.getY() + 1,
  //          cubeCorner.getZ() + 1);
  //
  //  getApiInvocationHandler().handleLine(
  //      String.format("world.setBlocks(%d,%d,%d,%d,%d,%d,%d,%d)",
  //          (int) cubeCorner.getX(),
  //          (int) cubeCorner.getY(),
  //          (int) cubeCorner.getZ(),
  //          (int) otherCubeCorner.getX(),
  //          (int) otherCubeCorner.getY(),
  //          (int) otherCubeCorner.getZ(),
  //          BlockType.LimeWool.getId(),
  //          BlockType.LimeWool.getData()));
  //
  //  Map<BlockType, List<Block>> blockTypeToBlocks =
  //      CuboidReference.fromCorners(cubeCorner, otherCubeCorner)
  //          .fetchBlocks(getServerWrapper().getWorld())
  //          .blockTypeToBlocks();
  //
  //  // there's a 2x2x2 set of green wool blocks
  //  assertEquals(Sets.newHashSet(BlockType.LimeWool), blockTypeToBlocks.keySet());
  //  assertEquals(8, blockTypeToBlocks.get(BlockType.LimeWool).size());
  //}
  //
  //@Test
  //public void test_world_getPlayerEntityIds() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    String expectedPlayerIdsStr = getServerWrapper().getPlayers().stream()
  //        .map(Player::getID)
  //        .map(String::valueOf)
  //        .collect(Collectors.joining("|"));
  //
  //    getApiInvocationHandler().handleLine("world.getPlayerEntityIds()");
  //
  //    assertEquals(
  //        Lists.newArrayList(expectedPlayerIdsStr),
  //        getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_world_getHeight() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //    Position p = nextTestPosition("world.getHeight");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    Block block =
  //        getServerWrapper().getWorld().getBlockAt(
  //            p.getBlockX() + 3,
  //            p.getBlockY() + 5,
  //            p.getBlockZ() + 7);
  //    block.setType(BlockType.RedstoneBlock);
  //    block.update();
  //
  //    // sanity check of height
  //    assertEquals(
  //        p.getBlockY() + 6, // height of block y, + 1
  //        getServerWrapper().getWorld().getHighestBlockAt(p.getBlockX() + 3, p.getBlockZ() + 7));
  //
  //    // x and z are relative to the origin
  //    getApiInvocationHandler().handleLine("world.getHeight(3,7)");
  //
  //    // the first block before there's just air, at this x,z location,
  //    // relative to the player's origin
  //    int expectedWorldHeight = 6;
  //
  //    assertEquals(
  //        Lists.newArrayList(String.valueOf(expectedWorldHeight)),
  //        getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_events_block_hits() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    // TODO: make PlayerWrapper?
  //    makeFirstPlayerWieldItem(getServerWrapper().getFirstPlayer(), ItemType.GoldSword);
  //
  //    Position p = nextTestPosition("block hit event");
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("world.setBlock(%d,%d,%d,%d)",
  //            (int) p.getX(),
  //            (int) p.getY(),
  //            (int) p.getZ(),
  //            BlockType.RedstoneBlock.getId()));
  //
  //    Block b = getServerWrapper().getWorld().getBlockAt(p);
  //
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getServerWrapper().getFirstPlayer(), b));
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getServerWrapper().getFirstPlayer(), b));
  //
  //    getApiInvocationHandler().handleLine("events.block.hits()");
  //
  //    int expectedFace = 7;
  //
  //    String expectedEventOutput = String.format("%d,%d,%d,%d,%d",
  //        (int) p.getX(),
  //        (int) p.getY(),
  //        (int) p.getZ(),
  //        expectedFace,
  //        getServerWrapper().getFirstPlayer().getID());
  //
  //    assertEquals(
  //        Lists.newArrayList(expectedEventOutput + "|" + expectedEventOutput),
  //        getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_events_clear() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    // TODO: make PlayerWrapper?
  //    makeFirstPlayerWieldItem(getServerWrapper().getFirstPlayer(), ItemType.GoldSword);
  //
  //    Position p = nextTestPosition("block hit event");
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("world.setBlock(%d,%d,%d,%d)",
  //            (int) p.getX(),
  //            (int) p.getY(),
  //            (int) p.getZ(),
  //            BlockType.RedstoneBlock.getId()));
  //
  //    Block b = getServerWrapper().getWorld().getBlockAt(p);
  //
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getServerWrapper().getFirstPlayer(), b));
  //
  //    getApiInvocationHandler().handleLine("events.clear()");
  //
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getServerWrapper().getFirstPlayer(), b));
  //
  //    getApiInvocationHandler().handleLine("events.block.hits()");
  //
  //    int expectedFace = 7;
  //
  //    assertEquals(
  //        Lists.newArrayList(String.format("%d,%d,%d,%d,%d",
  //            (int) p.getX(),
  //            (int) p.getY(),
  //            (int) p.getZ(),
  //            expectedFace,
  //            getServerWrapper().getFirstPlayer().getID())),
  //        getTestOut().sends);
  //  }
  //}
  //
  ////TODO: "entity" methods were clearly intended to be for all entities, not just players.
  ////convert these tests to test non-player entities.
  ////then consider basing the player methods on entity methods.
  //
  //@Test
  //public void test_player_getTile() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("player.getTile");
  //
  //    // when name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.getTile()");
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getTile(%s)", getServerWrapper().getFirstPlayer().getName()));
  //
  //    String expected = String.format("%d,%d,%d",
  //        (int) p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //        (int) p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //        (int) p.getZ() + PLAYER_PLACEMENT_Z_OFFSET);
  //
  //    assertEquals(Lists.newArrayList(expected, expected), getTestOut().sends);
  //
  //    // result is relative to player origin
  //
  //    setUpAtPlayerOrigin(new Position(3, 3, 3));
  //
  //    getApiInvocationHandler().handleLine("player.getTile()");
  //
  //    expected = String.format("%d,%d,%d",
  //        (int) p.getX() + PLAYER_PLACEMENT_X_OFFSET - 3,
  //        (int) p.getY() + PLAYER_PLACEMENT_Y_OFFSET - 3,
  //        (int) p.getZ() + PLAYER_PLACEMENT_Z_OFFSET - 3);
  //
  //    assertEquals(Lists.newArrayList(expected), getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_player_setTile() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("player.setTile");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    getServerWrapper().getFirstPlayer().setPitch(-74f);
  //    getServerWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Position(
  //            (int) p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //            (int) p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //            (int) p.getZ() + PLAYER_PLACEMENT_Z_OFFSET),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // move the player diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.setTile(%s,5,5,5)", getServerWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(
  //        new Position(
  //            (int) p.getX() + 5,
  //            (int) p.getY() + 5,
  //            (int) p.getZ() + 5),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getServerWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getServerWrapper().getFirstPlayer().getRotation());
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.setTile(7,7,7)");
  //
  //    assertEquals(
  //        new Position(
  //            (int) p.getX() + 7,
  //            (int) p.getY() + 7,
  //            (int) p.getZ() + 7),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //  }
  //}
  //
  //@Test
  //public void test_player_getPos() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("player.getPos");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    // TODO: provide comment guidance in the other tests along this lines of this one.
  //
  //    // player.getPos position result is relative to the origin (spawn location)
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getPos(%s)", getServerWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(
  //        String.format("%.1f,%.1f,%.1f",
  //            (float) PLAYER_PLACEMENT_X_OFFSET,
  //            (float) PLAYER_PLACEMENT_Y_OFFSET,
  //            (float) PLAYER_PLACEMENT_Z_OFFSET),
  //        getTestOut().sends.get(0));
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.getPos()");
  //
  //    assertEquals(2, getTestOut().sends.size());
  //    assertEquals(
  //        String.format("%.1f,%.1f,%.1f",
  //            (float) PLAYER_PLACEMENT_X_OFFSET,
  //            (float) PLAYER_PLACEMENT_Y_OFFSET,
  //            (float) PLAYER_PLACEMENT_Z_OFFSET),
  //        getTestOut().sends.get(1));
  //  }
  //}
  //
  //@Test
  //public void test_player_setPos() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("player.setPos");
  //
  //    Position pMid = new Position(p.getX() + 0.5f, p.getY() + 0.5f, p.getZ() + 0.5f);
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(pMid);
  //
  //    getServerWrapper().getFirstPlayer().setPitch(-74f);
  //    getServerWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Position(
  //            p.getX() + (double) PLAYER_PLACEMENT_X_OFFSET,
  //            p.getY() + (double) PLAYER_PLACEMENT_Y_OFFSET,
  //            p.getZ() + (double) PLAYER_PLACEMENT_Z_OFFSET),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // move the player diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.setPos(%s,5.2,5.2,5.2)",
  //            getServerWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(
  //        new Position(
  //            p.getX() + 5.2f,
  //            p.getY() + 5.2f,
  //            p.getZ() + 5.2f),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getServerWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getServerWrapper().getFirstPlayer().getRotation());
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.setPos(7.2,7.2,7.2)");
  //
  //    assertEquals(
  //        new Position(
  //            p.getX() + 7.2f,
  //            p.getY() + 7.2f,
  //            p.getZ() + 7.2f),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //  }
  //}
  //
  //@Test
  //public void test_player_getDirection() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    getServerWrapper().getFirstPlayer().setPitch(47f);
  //    getServerWrapper().getFirstPlayer().setRotation(97f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getDirection(%s)", getServerWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //
  //    String[] parts = getTestOut().sends.get(0).split(",", 3);
  //    double vecX = Double.parseDouble(parts[0]);
  //    double vecY = Double.parseDouble(parts[1]);
  //    double vecZ = Double.parseDouble(parts[2]);
  //
  //    PitchAndRotation pitchAndRotation = vectorToPitchAndRotation(vecX, vecY, vecZ);
  //    assertEquals(47, (int) pitchAndRotation.pitch);
  //    assertEquals(97, (int) pitchAndRotation.rotation);
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.getDirection()");
  //
  //    assertEquals(2, getTestOut().sends.size());
  //
  //    parts = getTestOut().sends.get(1).split(",", 3);
  //    vecX = Double.parseDouble(parts[0]);
  //    vecY = Double.parseDouble(parts[1]);
  //    vecZ = Double.parseDouble(parts[2]);
  //
  //    pitchAndRotation = vectorToPitchAndRotation(vecX, vecY, vecZ);
  //    assertEquals(47, (int) pitchAndRotation.pitch);
  //    assertEquals(97, (int) pitchAndRotation.rotation);
  //  }
  //}
  //
  //@Test
  //public void test_player_getPitch() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //    getServerWrapper().getFirstPlayer().setPitch(49f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getPitch(%s)", getServerWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(0)));
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.getPitch()");
  //
  //    assertEquals(2, getTestOut().sends.size());
  //    assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(1)));
  //  }
  //}
  //
  //@Test
  //public void test_player_getRotation() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //    getServerWrapper().getFirstPlayer().setRotation(93f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getRotation(%s)", getServerWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(93, (int) Float.parseFloat(getTestOut().sends.get(0)));
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.getRotation()");
  //
  //    assertEquals(2, getTestOut().sends.size());
  //    assertEquals(93, (int) Float.parseFloat(getTestOut().sends.get(1)));
  //  }
  //}
  //
  //@Test
  //public void test_entity_getTile() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("entity.getTile");
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getTile(%d)", getServerWrapper().getFirstPlayer().getID()));
  //
  //    String expected = String.format("%d,%d,%d",
  //        (int) p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //        (int) p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //        (int) p.getZ() + PLAYER_PLACEMENT_Z_OFFSET);
  //
  //    assertEquals(Lists.newArrayList(expected), getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_entity_setTile() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("entity.setTile");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    getServerWrapper().getFirstPlayer().setPitch(-74f);
  //    getServerWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Position(
  //            (int) p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //            (int) p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //            (int) p.getZ() + PLAYER_PLACEMENT_Z_OFFSET),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // move the entity diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.setTile(%d,5,5,5)", getServerWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(
  //        new Position(
  //            (int) p.getX() + 5,
  //            (int) p.getY() + 5,
  //            (int) p.getZ() + 5),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getServerWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getServerWrapper().getFirstPlayer().getRotation());
  //  }
  //}
  //
  //@Test
  //public void test_entity_getPos() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("entity.getPos");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    // entity.getPos position result is relative to the origin (spawn location)
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getPos(%d)", getServerWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(
  //        String.format("%.1f,%.1f,%.1f",
  //            (float) PLAYER_PLACEMENT_X_OFFSET,
  //            (float) PLAYER_PLACEMENT_Y_OFFSET,
  //            (float) PLAYER_PLACEMENT_Z_OFFSET),
  //        getTestOut().sends.get(0));
  //  }
  //}
  //
  //@Test
  //public void test_entity_setPos() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    Position p = nextTestPosition("entity.setPos");
  //
  //    Position pMid = new Position(p.getX() + 0.5f, p.getY() + 0.5f, p.getZ() + 0.5f);
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(pMid);
  //
  //    getServerWrapper().getFirstPlayer().setPitch(-74f);
  //    getServerWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Position(
  //            p.getX() + (double) PLAYER_PLACEMENT_X_OFFSET,
  //            p.getY() + (double) PLAYER_PLACEMENT_Y_OFFSET,
  //            p.getZ() + (double) PLAYER_PLACEMENT_Z_OFFSET),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // move the entity diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.setPos(%d,5.2,5.2,5.2)",
  //            getServerWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(
  //        new Position(
  //            p.getX() + 5.2f,
  //            p.getY() + 5.2f,
  //            p.getZ() + 5.2f),
  //        getServerWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getServerWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getServerWrapper().getFirstPlayer().getRotation());
  //  }
  //}
  //
  //@Test
  //public void test_entity_getDirection() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //
  //    getServerWrapper().getFirstPlayer().setPitch(47f);
  //    getServerWrapper().getFirstPlayer().setRotation(97f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getDirection(%d)", getServerWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //
  //    String[] parts = getTestOut().sends.get(0).split(",", 3);
  //    double vecX = Double.parseDouble(parts[0]);
  //    double vecY = Double.parseDouble(parts[1]);
  //    double vecZ = Double.parseDouble(parts[2]);
  //
  //    PitchAndRotation pitchAndRotation = vectorToPitchAndRotation(vecX, vecY, vecZ);
  //    assertEquals(47, (int) pitchAndRotation.pitch);
  //    assertEquals(97, (int) pitchAndRotation.rotation);
  //  }
  //}
  //
  //@Test
  //public void test_entity_getPitch() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //    getServerWrapper().getFirstPlayer().setPitch(49f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getPitch(%d)", getServerWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(0)));
  //  }
  //}
  //
  //@Test
  //public void test_entity_getRotation() throws Exception {
  //  if (getServerWrapper().hasPlayers()) {
  //    getServerWrapper().getFirstPlayer().setRotation(93f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getRotation(%d)", getServerWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(93, (int) Float.parseFloat(getTestOut().sends.get(0)));
  //  }
  //}
  //
  //
  //static class PitchAndRotation {
  //  public final double pitch;
  //  public final double rotation;
  //
  //  public PitchAndRotation(double pitch, double rotation) {
  //    this.pitch = pitch;
  //    this.rotation = rotation;
  //  }
  //}
  //
  //// taken from https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Location.java
  //// "setDirection"
  //public PitchAndRotation vectorToPitchAndRotation(double vecX, double vecY, double vecZ) {
  //  /*
  //   * Sin = Opp / Hyp
  //   * Cos = Adj / Hyp
  //   * Tan = Opp / Adj
  //   *
  //   * x = -Opp
  //   * z = Adj
  //   */
  //  final double _2PI = 2 * Math.PI;
  //
  //  if (vecX == 0 && vecZ == 0) {
  //    double pitch = vecY > 0 ? -90 : 90;
  //    return new PitchAndRotation(pitch, 0);
  //  }
  //
  //  double theta = Math.atan2(-vecX, vecZ);
  //  double yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);
  //
  //  double x2 = vecX * vecX;
  //  double z2 = vecZ * vecZ;
  //  double xz = Math.sqrt(x2 + z2);
  //  double pitch = (float) Math.toDegrees(Math.atan(-vecY / xz));
  //
  //  return new PitchAndRotation(pitch, yaw);
  //}
}
