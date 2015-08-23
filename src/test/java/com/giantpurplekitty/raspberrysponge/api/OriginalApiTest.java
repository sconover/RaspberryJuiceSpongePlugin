package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.FileHelper;
import com.giantpurplekitty.raspberrysponge.InWorldTestSupport;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.DataHelper;
import com.giantpurplekitty.raspberrysponge.game.TypeMappings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.spongepowered.api.block.BlockMetadata;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.BooleanPropertyInfo;
import org.spongepowered.api.block.EnumPropertyInfo;
import org.spongepowered.api.block.IntegerPropertyInfo;
import org.spongepowered.api.block.PropertyInfo;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getColorForIntegerId;
import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getIntegerIdForBlockType;
import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getIntegerIdForColor;
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

    Location block = getGameWrapper().getLocation(p.getX() + 3, p.getY() + 3, p.getZ() + 3);
    block.setBlockType(BlockTypes.REDSTONE_BLOCK);

    // sanity check
    assertEquals(
        BlockTypes.REDSTONE_BLOCK,
        getGameWrapper().getLocation(p.getX() + 3, p.getY() + 3, p.getZ() + 3).getBlockType());

    getApiInvocationHandler().handleLine(
        String.format("world.getBlock(%d,%d,%d)", 3, 3, 3));

    assertEquals(
        Lists.newArrayList(
            String.valueOf(getIntegerIdForBlockType(BlockTypes.REDSTONE_BLOCK))),
        getTestOut().sends);
  }

  // TODO - test returning other types of data
  // TODO - future api where we return string id values (maybe sooner rather than later...this can be here for backward compat)

  // turn the api notes above into enums
  // then iterate through and make sure various types of things match up
  // will probably need to establish a static mapping of internal -> external types
  @Test
  public void test_world_getBlockWithData() throws Exception {
    Vector3i p = nextTestPosition("world.getBlockWithData");

    Location block = getGameWrapper().getLocation(p);
    block.setBlockType(BlockTypes.REDSTONE_BLOCK);

    Location block2 = block.add(1, 0, 0);
    BlockState limeWoolBlock =
        DataHelper.setData(
            BlockTypes.WOOL.getDefaultState(),
            getIntegerIdForColor(DyeColors.LIME.getColor()));
    block2.setBlock(limeWoolBlock);

    getApiInvocationHandler().handleLine(
        String.format("world.getBlockWithData(%d,%d,%d)", p.getX(), p.getY(), p.getZ()));

    assertEquals(
        String.format("%d,%d", getIntegerIdForBlockType(BlockTypes.REDSTONE_BLOCK), 0),
        getTestOut().sends.get(0));

    getApiInvocationHandler().handleLine(
        String.format("world.getBlockWithData(%d,%d,%d)", p.getX() + 1, p.getY(), p.getZ()));

    assertEquals(
        String.format("%d,%d",
            getIntegerIdForBlockType(BlockTypes.WOOL),
            getIntegerIdForColor(DyeColors.LIME.getColor())),
        getTestOut().sends.get(1));
  }

  @Test
  public void test_world_setBlock() throws Exception {
    Vector3i p = nextTestPosition("world.setBlock");

    getApiInvocationHandler().handleLine(
        String.format("world.setBlock(%d,%d,%d,%s)",
            p.getX(),
            p.getY(),
            p.getZ(),
            getIntegerIdForBlockType(BlockTypes.REDSTONE_BLOCK)));

    Location block = getGameWrapper().getLocation(p);
    assertEquals(BlockTypes.REDSTONE_BLOCK, block.getBlockType());

    getApiInvocationHandler().handleLine(
        String.format("world.setBlock(%d,%d,%d,%d,%d)",
            p.getX() + 1,
            p.getY(),
            p.getZ(),
            getIntegerIdForBlockType(BlockTypes.WOOL),
            getIntegerIdForColor(DyeColors.LIME.getColor())));

    Location block2 = getGameWrapper().getLocation(
        p.getX() + 1,
        p.getY(),
        p.getZ());

    assertEquals(BlockTypes.WOOL, block2.getBlockType());
    assertEquals(
        DyeColors.LIME.getColor(),
        getColorForIntegerId(DataHelper.getData(block2.getBlock())));
  }

  @Test
  public void test_world_setBlocks_simple() throws Exception {
    Vector3i cubeCorner = nextTestPosition("world.setBlocks simple");

    Vector3i otherCubeCorner =
        new Vector3i(
            cubeCorner.getX() + 1,
            cubeCorner.getY() + 1,
            cubeCorner.getZ() + 1);

    getApiInvocationHandler().handleLine(
        String.format("world.setBlocks(%d,%d,%d,%d,%d,%d,%d)",
            cubeCorner.getX(),
            cubeCorner.getY(),
            cubeCorner.getZ(),
            otherCubeCorner.getX(),
            otherCubeCorner.getY(),
            otherCubeCorner.getZ(),
            getIntegerIdForBlockType(BlockTypes.REDSTONE_BLOCK)));

    Map<BlockType, List<BlockState>> blockTypeToBlocks =
        CuboidReference.fromCorners(cubeCorner, otherCubeCorner)
            .fetchBlocks(getGameWrapper())
            .blockTypeToBlocks();

    // there's a 2x2x2 set of redstone blocks
    assertEquals(Sets.newHashSet(BlockTypes.REDSTONE_BLOCK), blockTypeToBlocks.keySet());
    assertEquals(8, blockTypeToBlocks.get(BlockTypes.REDSTONE_BLOCK).size());

    Vector3i pastOtherCubeCorner =
        new Vector3i(
            cubeCorner.getX() + 2,
            cubeCorner.getY() + 2,
            cubeCorner.getZ() + 2);

    Map<BlockType, List<BlockState>> blockTypeToBlocks2 =
        CuboidReference.fromCorners(cubeCorner, pastOtherCubeCorner)
            .fetchBlocks(getGameWrapper())
            .blockTypeToBlocks();

    // out of this 3x3x3 cube, there's a 2x2x2 set of redstone blocks,
    // and the rest is air
    assertEquals(Sets.newHashSet(
            BlockTypes.REDSTONE_BLOCK,
            BlockTypes.AIR),
        blockTypeToBlocks2.keySet());
    assertEquals(8, blockTypeToBlocks2.get(BlockTypes.REDSTONE_BLOCK).size());
    assertEquals(27 - 8, blockTypeToBlocks2.get(BlockTypes.AIR).size());
  }

  //TODO: only color "data" works right now
  @Test
  public void test_world_setBlocks_withData_whichIsTheColor() throws Exception {
    Vector3i cubeCorner = nextTestPosition("world.setBlocks with data");

    Vector3i otherCubeCorner =
        new Vector3i(
            cubeCorner.getX() + 1,
            cubeCorner.getY() + 1,
            cubeCorner.getZ() + 1);

    getApiInvocationHandler().handleLine(
        String.format("world.setBlocks(%d,%d,%d,%d,%d,%d,%d,%d)",
            cubeCorner.getX(),
            cubeCorner.getY(),
            cubeCorner.getZ(),
            otherCubeCorner.getX(),
            otherCubeCorner.getY(),
            otherCubeCorner.getZ(),
            getIntegerIdForBlockType(BlockTypes.WOOL),
            getIntegerIdForColor(DyeColors.LIME.getColor())));

    Map<Pair<BlockType, Integer>, List<BlockState>> blockTypeAndDataToBlocks =
        CuboidReference.fromCorners(cubeCorner, otherCubeCorner)
            .fetchBlocks(getGameWrapper())
            .blockTypeAndDataToBlocks();

    // there's a 2x2x2 set of green wool blocks
    ImmutablePair<BlockType, Integer> limeWoolTypeAndData = ImmutablePair.of(
        BlockTypes.WOOL,
        getIntegerIdForColor(DyeColors.LIME.getColor()));
    assertEquals(Sets.newHashSet(limeWoolTypeAndData), blockTypeAndDataToBlocks.keySet());
    assertEquals(8, blockTypeAndDataToBlocks.get(limeWoolTypeAndData).size());
  }

  @Test
  public void test_world_getPlayerEntityIds() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {
      List<Integer> playerIds = new ArrayList<Integer>();
      for (Player p : getGameWrapper().getPlayers()) {
        playerIds.add(p.getEntityId());
      }
      Collections.sort(playerIds);
      String expectedPlayerIdsStr =
          Joiner.on("|").join(playerIds);

      getApiInvocationHandler().handleLine("world.getPlayerEntityIds()");

      assertEquals(
          Lists.newArrayList(expectedPlayerIdsStr),
          getTestOut().sends);
    }
  }

  @Test
  public void test_world_getHeight() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {
      Vector3i p = nextTestPosition("world.getHeight");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      Location block =
          getGameWrapper().getLocation(
              p.getX() + 3,
              p.getY() + 5,
              p.getZ() + 7);
      block.setBlockType(BlockTypes.REDSTONE_BLOCK);

      // sanity check of height
      assertEquals(
          p.getY() + 5,
          getGameWrapper().getHighestBlockYAt(p.getX() + 3, p.getZ() + 7));

      // x and z are relative to the origin
      getApiInvocationHandler().handleLine("world.getHeight(3,7)");

      // the first block before there's just air, at this x,z location,
      // relative to the player's origin
      int expectedWorldHeight = 5;

      assertEquals(
          Lists.newArrayList(String.valueOf(expectedWorldHeight)),
          getTestOut().sends);
    }
  }

  //@Test
  //public void test_events_block_hits() throws Exception {
  //  if (shouldRunBecausePlayerIsLoggedIntoGame()) {
  //
  //    // TODO: make PlayerWrapper?
  //    makeFirstPlayerWieldItem(getGameWrapper().getFirstPlayer(), ItemType.GoldSword);
  //
  //    Vector3i p = nextTestPosition("block hit event");
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("world.setBlock(%d,%d,%d,%d)",
  //            p.getX(),
  //            p.getY(),
  //            p.getZ(),
  //            getIntegerIdForBlockType(BlockTypes.REDSTONE_BLOCK)));
  //
  //    Block b = getGameWrapper().getLocation(p);
  //
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getGameWrapper().getFirstPlayer(), b));
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getGameWrapper().getFirstPlayer(), b));
  //
  //    getApiInvocationHandler().handleLine("events.block.hits()");
  //
  //    int expectedFace = 7;
  //
  //    String expectedEventOutput = String.format("%d,%d,%d,%d,%d",
  //        p.getX(),
  //        p.getY(),
  //        p.getZ(),
  //        expectedFace,
  //        getEntityId(getGameWrapper().getFirstPlayer()));
  //
  //    assertEquals(
  //        Lists.newArrayList(expectedEventOutput + "|" + expectedEventOutput),
  //        getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_events_clear() throws Exception {
  //  if (shouldRunBecausePlayerIsLoggedIntoGame()) {
  //
  //    // TODO: make PlayerWrapper?
  //    makeFirstPlayerWieldItem(getGameWrapper().getFirstPlayer(), ItemType.GoldSword);
  //
  //    Vector3i p = nextTestPosition("block hit event");
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("world.setBlock(%d,%d,%d,%d)",
  //            p.getX(),
  //            p.getY(),
  //            p.getZ(),
  //            getIntegerIdForBlockType(BlockTypes.REDSTONE_BLOCK)));
  //
  //    Block b = getGameWrapper().getLocation(p);
  //
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getGameWrapper().getFirstPlayer(), b));
  //
  //    getApiInvocationHandler().handleLine("events.clear()");
  //
  //    getPluginListener().onBlockHit(
  //        new BlockRightClickHook(getGameWrapper().getFirstPlayer(), b));
  //
  //    getApiInvocationHandler().handleLine("events.block.hits()");
  //
  //    int expectedFace = 7;
  //
  //    assertEquals(
  //        Lists.newArrayList(String.format("%d,%d,%d,%d,%d",
  //            p.getX(),
  //            p.getY(),
  //            p.getZ(),
  //            expectedFace,
  //            getEntityId(getGameWrapper().getFirstPlayer()))),
  //        getTestOut().sends);
  //  }
  //}

  @Test
  public void test_player_getTile() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("player.getTile");

      // when name is blank, default to first player

      getApiInvocationHandler().handleLine("player.getTile()");
      getApiInvocationHandler().handleLine(
          String.format("player.getTile(%s)", getGameWrapper().getFirstPlayer().getName()));

      String expected = String.format("%d,%d,%d",
          p.getX() + PLAYER_PLACEMENT_X_OFFSET,
          p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
          p.getZ() + PLAYER_PLACEMENT_Z_OFFSET);

      assertEquals(Lists.newArrayList(expected, expected), getTestOut().sends);

      // result is relative to player origin

      setUpAtPlayerOrigin(new Vector3i(3, 3, 3));

      getApiInvocationHandler().handleLine("player.getTile()");

      expected = String.format("%d,%d,%d",
          p.getX() + PLAYER_PLACEMENT_X_OFFSET - 3,
          p.getY() + PLAYER_PLACEMENT_Y_OFFSET - 3,
          p.getZ() + PLAYER_PLACEMENT_Z_OFFSET - 3);

      assertEquals(Lists.newArrayList(expected), getTestOut().sends);
    }
  }

  @Test
  public void test_player_setTile() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("player.setTile");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      float yaw = 89f;
      float pitch = -74f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      // initial position

      assertEquals(
          new Vector3i(
              p.getX() + PLAYER_PLACEMENT_X_OFFSET,
              p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
              p.getZ() + PLAYER_PLACEMENT_Z_OFFSET),
          getGameWrapper().getFirstPlayer().getLocation().getBlockPosition());

      // move the player diagonally

      getApiInvocationHandler().handleLine(
          String.format("player.setTile(%s,5,5,5)", getGameWrapper().getFirstPlayer().getName()));

      assertEquals(
          new Vector3i(
              p.getX() + 5,
              p.getY() + 5,
              p.getZ() + 5),
          getGameWrapper().getFirstPlayer().getLocation().getBlockPosition());

      // make sure the pitch and yaw are maintained

      assertEquals(rotation, getGameWrapper().getFirstPlayer().getRotation());

      // when player name is blank, default to first player

      getApiInvocationHandler().handleLine("player.setTile(7,7,7)");

      assertEquals(
          new Vector3i(
              p.getX() + 7,
              p.getY() + 7,
              p.getZ() + 7),
          getGameWrapper().getFirstPlayer().getLocation().getBlockPosition());
    }
  }

  @Test
  public void test_player_getPos() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("player.getPos");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      // TODO: provide comment guidance in the other tests along this lines of this one.

      // player.getPos position result is relative to the origin (spawn location)

      getApiInvocationHandler().handleLine(
          String.format("player.getPos(%s)", getGameWrapper().getFirstPlayer().getName()));

      assertEquals(1, getTestOut().sends.size());
      assertEquals(
          String.format("%.1f,%.1f,%.1f",
              (float) PLAYER_PLACEMENT_X_OFFSET,
              (float) PLAYER_PLACEMENT_Y_OFFSET,
              (float) PLAYER_PLACEMENT_Z_OFFSET),
          getTestOut().sends.get(0));

      // when player name is blank, default to first player

      getApiInvocationHandler().handleLine("player.getPos()");

      assertEquals(2, getTestOut().sends.size());
      assertEquals(
          String.format("%.1f,%.1f,%.1f",
              (float) PLAYER_PLACEMENT_X_OFFSET,
              (float) PLAYER_PLACEMENT_Y_OFFSET,
              (float) PLAYER_PLACEMENT_Z_OFFSET),
          getTestOut().sends.get(1));
    }
  }

  @Test
  public void test_player_setPos() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("player.setPos");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      float yaw = 89f;
      float pitch = -74f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      // initial position

      assertVector3dEquals(
          new Vector3d(
              p.getX() + (double) PLAYER_PLACEMENT_X_OFFSET,
              p.getY() + (double) PLAYER_PLACEMENT_Y_OFFSET,
              p.getZ() + (double) PLAYER_PLACEMENT_Z_OFFSET),
          getGameWrapper().getFirstPlayer().getLocation().getPosition());

      // move the player diagonally

      getApiInvocationHandler().handleLine(
          String.format("player.setPos(%s,5.2,5.2,5.2)",
              getGameWrapper().getFirstPlayer().getName()));

      assertVector3dEquals(
          new Vector3d(
              p.getX() + 5.2d,
              p.getY() + 5.2d,
              p.getZ() + 5.2d),
          getGameWrapper().getFirstPlayer().getLocation().getPosition());

      // make sure the pitch and yaw are maintained

      assertEquals(rotation, getGameWrapper().getFirstPlayer().getRotation());

      // when player name is blank, default to first player

      getApiInvocationHandler().handleLine("player.setPos(7.2,7.2,7.2)");

      assertVector3dEquals(
          new Vector3d(
              p.getX() + 7.2d,
              p.getY() + 7.2d,
              p.getZ() + 7.2d),
          getGameWrapper().getFirstPlayer().getLocation().getPosition());
    }
  }

  @Test
  public void test_player_getDirection() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      float yaw = 97f;
      float pitch = 47f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      getApiInvocationHandler().handleLine(
          String.format("player.getDirection(%s)", getGameWrapper().getFirstPlayer().getName()));

      assertEquals(1, getTestOut().sends.size());

      String[] parts = getTestOut().sends.get(0).split(",", 3);
      double vecX = Double.parseDouble(parts[0]);
      double vecY = Double.parseDouble(parts[1]);
      double vecZ = Double.parseDouble(parts[2]);

      PitchAndRotation pitchAndRotation = vectorToPitchAndRotation(vecX, vecY, vecZ);
      assertEquals(47, (int) pitchAndRotation.pitch);
      assertEquals(97, (int) pitchAndRotation.rotation);

      // when player name is blank, default to first player

      getApiInvocationHandler().handleLine("player.getDirection()");

      assertEquals(2, getTestOut().sends.size());

      parts = getTestOut().sends.get(1).split(",", 3);
      vecX = Double.parseDouble(parts[0]);
      vecY = Double.parseDouble(parts[1]);
      vecZ = Double.parseDouble(parts[2]);

      pitchAndRotation = vectorToPitchAndRotation(vecX, vecY, vecZ);
      assertEquals(47, (int) pitchAndRotation.pitch);
      assertEquals(97, (int) pitchAndRotation.rotation);
    }
  }

  @Test
  public void test_player_getPitch() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      float yaw = 0;
      float pitch = 49f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      getApiInvocationHandler().handleLine(
          String.format("player.getPitch(%s)", getGameWrapper().getFirstPlayer().getName()));

      assertEquals(1, getTestOut().sends.size());
      assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(0)));

      // when player name is blank, default to first player

      getApiInvocationHandler().handleLine("player.getPitch()");

      assertEquals(2, getTestOut().sends.size());
      assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(1)));
    }
  }

  @Test
  public void test_player_getRotation() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {
      float yaw = 93f;
      float pitch = 0;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      getApiInvocationHandler().handleLine(
          String.format("player.getRotation(%s)", getGameWrapper().getFirstPlayer().getName()));

      assertEquals(1, getTestOut().sends.size());
      assertEquals(93, (int) Float.parseFloat(getTestOut().sends.get(0)));

      // when player name is blank, default to first player

      getApiInvocationHandler().handleLine("player.getRotation()");

      assertEquals(2, getTestOut().sends.size());
      assertEquals(93, (int) Float.parseFloat(getTestOut().sends.get(1)));
    }
  }

  @Test
  public void test_entity_getTile() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("entity.getTile");

      getApiInvocationHandler().handleLine(
          String.format("entity.getTile(%d)", getGameWrapper().getFirstPlayer().getEntityId()));

      String expected = String.format("%d,%d,%d",
          p.getX() + PLAYER_PLACEMENT_X_OFFSET,
          p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
          p.getZ() + PLAYER_PLACEMENT_Z_OFFSET);

      assertEquals(Lists.newArrayList(expected), getTestOut().sends);
    }
  }

  @Test
  public void test_entity_setTile() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("entity.setTile");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      float yaw = 89f;
      float pitch = -74f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      // initial position

      assertEquals(
          new Vector3i(
              p.getX() + PLAYER_PLACEMENT_X_OFFSET,
              p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
              p.getZ() + PLAYER_PLACEMENT_Z_OFFSET),
          getGameWrapper().getFirstPlayer().getLocation().getBlockPosition());

      // move the entity diagonally

      getApiInvocationHandler().handleLine(
          String.format("entity.setTile(%d,5,5,5)",
              getGameWrapper().getFirstPlayer().getEntityId()));

      assertEquals(
          new Vector3i(
              p.getX() + 5,
              p.getY() + 5,
              p.getZ() + 5),
          getGameWrapper().getFirstPlayer().getLocation().getBlockPosition());

      // make sure the pitch and yaw are maintained

      assertEquals(rotation, getGameWrapper().getFirstPlayer().getRotation());
    }
  }

  @Test
  public void test_entity_getPos() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("entity.getPos");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      // entity.getPos position result is relative to the origin (spawn location)

      getApiInvocationHandler().handleLine(
          String.format("entity.getPos(%d)", getGameWrapper().getFirstPlayer().getEntityId()));

      assertEquals(1, getTestOut().sends.size());
      assertEquals(
          String.format("%.1f,%.1f,%.1f",
              (float) PLAYER_PLACEMENT_X_OFFSET,
              (float) PLAYER_PLACEMENT_Y_OFFSET,
              (float) PLAYER_PLACEMENT_Z_OFFSET),
          getTestOut().sends.get(0));
    }
  }

  @Test
  public void test_entity_setPos() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      Vector3i p = nextTestPosition("entity.setPos");

      // make the origin == p

      setUpAtPlayerOrigin(p);

      float yaw = 89f;
      float pitch = -74f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      // initial position

      assertVector3dEquals(
          new Vector3d(
              p.getX() + (double) PLAYER_PLACEMENT_X_OFFSET,
              p.getY() + (double) PLAYER_PLACEMENT_Y_OFFSET,
              p.getZ() + (double) PLAYER_PLACEMENT_Z_OFFSET),
          getGameWrapper().getFirstPlayer().getLocation().getPosition());

      // move the entity diagonally

      getApiInvocationHandler().handleLine(
          String.format("entity.setPos(%d,5.2,5.2,5.2)",
              getGameWrapper().getFirstPlayer().getEntityId()));

      assertVector3dEquals(
          new Vector3d(
              p.getX() + 5.2d,
              p.getY() + 5.2d,
              p.getZ() + 5.2d),
          getGameWrapper().getFirstPlayer().getLocation().getPosition());

      // make sure the pitch and yaw are maintained

      assertEquals(rotation, getGameWrapper().getFirstPlayer().getRotation());
    }
  }

  @Test
  public void test_entity_getDirection() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {

      float yaw = 97f;
      float pitch = 47f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      getApiInvocationHandler().handleLine(
          String.format("entity.getDirection(%d)", getGameWrapper().getFirstPlayer().getEntityId()));

      assertEquals(1, getTestOut().sends.size());

      String[] parts = getTestOut().sends.get(0).split(",", 3);
      double vecX = Double.parseDouble(parts[0]);
      double vecY = Double.parseDouble(parts[1]);
      double vecZ = Double.parseDouble(parts[2]);

      PitchAndRotation pitchAndRotation = vectorToPitchAndRotation(vecX, vecY, vecZ);
      assertEquals(47, (int) pitchAndRotation.pitch);
      assertEquals(97, (int) pitchAndRotation.rotation);
    }
  }

  @Test
  public void test_entity_getPitch() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {
      float yaw = 0;
      float pitch = 49f;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      getApiInvocationHandler().handleLine(
          String.format("entity.getPitch(%d)", getGameWrapper().getFirstPlayer().getEntityId()));

      assertEquals(1, getTestOut().sends.size());
      assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(0)));
    }
  }

  @Test
  public void test_entity_getRotation() throws Exception {
    if (shouldRunBecausePlayerIsLoggedIntoGame()) {
      float yaw = 93f;
      float pitch = 0;
      float roll = 0;
      Vector3d rotation = new Vector3d(yaw, pitch, roll);
      getGameWrapper().getFirstPlayer().setRotation(rotation);

      getApiInvocationHandler().handleLine(
          String.format("entity.getRotation(%d)", getGameWrapper().getFirstPlayer().getEntityId()));

      assertEquals(1, getTestOut().sends.size());
      assertEquals(93, (int) Float.parseFloat(getTestOut().sends.get(0)));
    }
  }

  @Test
  public void test_print_block_metadata() throws Exception {
    Vector3i p = nextTestPosition("printBlockMetadata");

    List<BlockMetadata> allMetadata = new ArrayList<BlockMetadata>();
    int count = 0;
    Iterator<Map.Entry<BlockType, Integer>> iter = TypeMappings.blockTypeAndIdIterator();
    while (iter.hasNext()) {
      count++;
      Map.Entry<BlockType, Integer> next = iter.next();
      BlockType blockType = next.getKey();
      Integer blockId = next.getValue();
      allMetadata.add(blockType.getDefaultState().getMetadata());
    }

    List<BlockGson> allBlockInfos = new ArrayList<BlockGson>();
    for (BlockMetadata blockMetadata: allMetadata) {
      BlockGson blockInfo = new BlockGson();
      blockInfo.name = blockMetadata.getType().getName();
      blockInfo.id = TypeMappings.getIntegerIdForBlockType(blockMetadata.getType());
      blockInfo.properties = new ArrayList();
      for (PropertyInfo propertyInfo: blockMetadata.getPropertyInfos()) {
        if (propertyInfo instanceof EnumPropertyInfo) {
          EnumPropertyGson enumPropertyGson = new EnumPropertyGson();
          enumPropertyGson.name = propertyInfo.getName();
          enumPropertyGson.value = (String)propertyInfo.getDefaultValue();
          enumPropertyGson.possible_values = propertyInfo.getAllowedValues();
          blockInfo.properties.add(enumPropertyGson);
        } else if (propertyInfo instanceof IntegerPropertyInfo) {
          IntegerPropertyGson integerPropertyGson = new IntegerPropertyGson();
          integerPropertyGson.name = propertyInfo.getName();
          integerPropertyGson.value = (Integer)propertyInfo.getDefaultValue();
          integerPropertyGson.possible_values = propertyInfo.getAllowedValues();
          blockInfo.properties.add(integerPropertyGson);
        } else if (propertyInfo instanceof BooleanPropertyInfo) {
          BooleanPropertyGson booleanPropertyGson = new BooleanPropertyGson();
          booleanPropertyGson.name = propertyInfo.getName();
          booleanPropertyGson.value = (Boolean)propertyInfo.getDefaultValue();
          blockInfo.properties.add(booleanPropertyGson);
        } else {
          throw new RuntimeException();
        }
      }
      allBlockInfos.add(blockInfo);
    }
    //
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(allBlockInfos));
  }

  static class BlockGson {
    public int id;
    public String name;
    public List properties;
  }

  static class BooleanPropertyGson {
    public final String type = "boolean";
    public String name;
    public Boolean value;
    public List possible_values = Lists.newArrayList(true, false);
  }

  static class IntegerPropertyGson {
    public final String type = "integer";
    public String name;
    public Integer value;
    public List possible_values;
  }

  static class EnumPropertyGson {
    public final String type = "enum";
    public String name;
    public String value;
    public List possible_values;
  }

  static class PitchAndRotation {
    public final double pitch;
    public final double rotation;

    public PitchAndRotation(double pitch, double rotation) {
      this.pitch = pitch;
      this.rotation = rotation;
    }
  }

  @Test
  public void test_print_entity_metadata() throws Exception {
    Vector3i p = nextTestPosition("printEntityMetadata");

    List allLiving = new ArrayList();

    for (Map.Entry<String,EntityType> entry:
        getGameWrapper().getSupportedNameToEntityType().entrySet()) {
      String entityTypeName = entry.getKey();
      EntityType entityType = entry.getValue();

      Living entityLiving = (Living)getGameWrapper().tryToSpawnEntity(entityType, p).get();

      LivingGson livingGson = new LivingGson();
      livingGson.name = entityTypeName;
      livingGson.tasks = entityLiving.getTaskNames();

      allLiving.add(livingGson);
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(allLiving));
  }

  static class LivingGson {
    public String name;
    public List tasks;
  }


  // taken from https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Location.java
  // "setDirection"
  public PitchAndRotation vectorToPitchAndRotation(double vecX, double vecY, double vecZ) {
    /*
     * Sin = Opp / Hyp
     * Cos = Adj / Hyp
     * Tan = Opp / Adj
     *
     * x = -Opp
     * z = Adj
     */
    final double _2PI = 2 * Math.PI;

    if (vecX == 0 && vecZ == 0) {
      double pitch = vecY > 0 ? -90 : 90;
      return new PitchAndRotation(pitch, 0);
    }

    double theta = Math.atan2(-vecX, vecZ);
    double yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

    double x2 = vecX * vecX;
    double z2 = vecZ * vecZ;
    double xz = Math.sqrt(x2 + z2);
    double pitch = (float) Math.toDegrees(Math.atan(-vecY / xz));

    return new PitchAndRotation(pitch, yaw);
  }
}
