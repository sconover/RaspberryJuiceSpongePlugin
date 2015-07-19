package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.FileHelper;
import com.giantpurplekitty.raspberrysponge.InWorldTestSupport;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.DataHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.DyeColors;
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

  // TODO: test this stuff

  /**
   * .id "The id (or type) of block"
   *
   * AIR                 = Block(0) STONE               = Block(1) GRASS               = Block(2)
   * DIRT                = Block(3) COBBLESTONE         = Block(4) WOOD_PLANKS         = Block(5)
   * SAPLING             = Block(6) BEDROCK             = Block(7) WATER_FLOWING       = Block(8)
   * WATER               = WATER_FLOWING WATER_STATIONARY    = Block(9) LAVA_FLOWING        =
   * Block(10) LAVA                = LAVA_FLOWING LAVA_STATIONARY     = Block(11) SAND
   *  = Block(12) GRAVEL              = Block(13) GOLD_ORE            = Block(14) IRON_ORE
   *  = Block(15) COAL_ORE            = Block(16) WOOD                = Block(17) LEAVES
   *  = Block(18) GLASS               = Block(20) LAPIS_LAZULI_ORE    = Block(21) LAPIS_LAZULI_BLOCK
   *  = Block(22) SANDSTONE           = Block(24) BED                 = Block(26) COBWEB
   *  = Block(30) GRASS_TALL          = Block(31) WOOL                = Block(35) FLOWER_YELLOW
   *  = Block(37) FLOWER_CYAN         = Block(38) MUSHROOM_BROWN      = Block(39) MUSHROOM_RED
   *  = Block(40) GOLD_BLOCK          = Block(41) IRON_BLOCK          = Block(42) STONE_SLAB_DOUBLE
   *  = Block(43) STONE_SLAB          = Block(44) BRICK_BLOCK         = Block(45) TNT
   *  = Block(46) BOOKSHELF           = Block(47) MOSS_STONE          = Block(48) OBSIDIAN
   *  = Block(49) TORCH               = Block(50) FIRE                = Block(51) STAIRS_WOOD
   *  = Block(53) CHEST               = Block(54) DIAMOND_ORE         = Block(56) DIAMOND_BLOCK
   *  = Block(57) CRAFTING_TABLE      = Block(58) FARMLAND            = Block(60) FURNACE_INACTIVE
   *  = Block(61) FURNACE_ACTIVE      = Block(62) DOOR_WOOD           = Block(64) LADDER
   *  = Block(65) STAIRS_COBBLESTONE  = Block(67) DOOR_IRON           = Block(71) REDSTONE_ORE
   *  = Block(73) SNOW                = Block(78) ICE                 = Block(79) SNOW_BLOCK
   *  = Block(80) CACTUS              = Block(81) CLAY                = Block(82) SUGAR_CANE
   *  = Block(83) FENCE               = Block(85) GLOWSTONE_BLOCK     = Block(89) BEDROCK_INVISIBLE
   *  = Block(95) STONE_BRICK         = Block(98) GLASS_PANE          = Block(102) MELON
   *   = Block(103) FENCE_GATE          = Block(107) GLOWING_OBSIDIAN    = Block(246)
   * NETHER_REACTOR_CORE = Block(247)
   *
   * .data "The data (or sub-type) of a block"
   *
   * Data Values of blocks: WOOL: 0: White 1: Orange 2: Magenta 3: Light Blue 4: Yellow 5: Lime 6:
   * Pink 7: Grey 8: Light grey 9: Cyan 10: Purple 11: Blue 12: Brown 13: Green 14: Red 15:Black
   *
   * WOOD: 0: Oak (up/down) 1: Spruce (up/down) 2: Birch (up/down) (below not on Pi) 3: Jungle
   * (up/down) 4: Oak (east/west) 5: Spruce (east/west) 6: Birch (east/west) 7: Jungle (east/west)
   * 8: Oak (north/south) 9: Spruce (north/south) 10: Birch (north/south) 11: Jungle (north/south)
   * 12: Oak (only bark) 13: Spruce (only bark) 14: Birch (only bark) 15: Jungle (only bark)
   *
   * WOOD_PLANKS (Not on Pi): 0: Oak 1: Spruce 2: Birch 3: Jungle
   *
   * SAPLING: 0: Oak 1: Spruce 2: Birch 3: Jungle (Not on Pi)
   *
   * GRASS_TALL: 0: Shrub 1: Grass 2: Fern 3: Grass (color affected by biome) (Not on Pi)
   *
   * TORCH: 1: Pointing east 2: Pointing west 3: Pointing south 4: Pointing north 5: Facing up
   *
   * STONE_BRICK: 0: Stone brick 1: Mossy stone brick 2: Cracked stone brick 3: Chiseled stone
   * brick
   *
   * STONE_SLAB / STONE_SLAB_DOUBLE: 0: Stone 1: Sandstone 2: Wooden 3: Cobblestone 4: Brick 5:
   * Stone Brick Below - not on Pi 6: Nether Brick 7: Quartz
   *
   * Not on Pi SNOW_BLOCK: 0-7: Height of snow, 0 being the lowest, 7 being the highest.
   *
   * TNT: 0: Inactive 1: Ready to explode
   *
   * LEAVES: 1: Oak leaves 2: Spruce leaves 3: Birch leaves
   *
   * SANDSTONE: 0: Sandstone 1: Chiseled sandstone 2: Smooth sandstone
   *
   * STAIRS_[COBBLESTONE, WOOD]: 0: Ascending east 1: Ascending west 2: Ascending south 3: Ascending
   * north 4: Ascending east (upside down) 5: Ascending west (upside down) 6: Ascending south
   * (upside down) 7: Ascending north (upside down)
   *
   * LADDERS, CHESTS, FURNACES: 2: Facing north 3: Facing south 4: Facing west 5: Facing east
   *
   * [WATER, LAVA]_STATIONARY: 0-7: Level of the water, 0 being the highest, 7 the lowest
   *
   * NETHER_REACTOR_CORE: 0: Unused 1: Active 2: Stopped / used up
   */

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
    if (getGameWrapper().hasPlayers()) {
      List<String> playerIds = new ArrayList<String>();
      for(Player p: getGameWrapper().getPlayers()) {
        playerIds.add(p.getIdentifier());
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

  //@Test
  //public void test_world_getHeight() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //    Vector3i p = nextTestPosition("world.getHeight");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    Location block =
  //        getGameWrapper().getLocation(
  //            p.getBlockX() + 3,
  //            p.getBlockY() + 5,
  //            p.getBlockZ() + 7);
  //    block.setBlockType(BlockTypes.REDSTONE_BLOCK);
  //    block.update();
  //
  //    // sanity check of height
  //    assertEquals(
  //        p.getBlockY() + 6, // height of block y, + 1
  //        getGameWrapper().getWorld().getHighestBlockAt(p.getBlockX() + 3, p.getBlockZ() + 7));
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
  //  if (getGameWrapper().hasPlayers()) {
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
  //        getGameWrapper().getFirstPlayer().getID());
  //
  //    assertEquals(
  //        Lists.newArrayList(expectedEventOutput + "|" + expectedEventOutput),
  //        getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_events_clear() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
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
  //            getGameWrapper().getFirstPlayer().getID())),
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
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("player.getTile");
  //
  //    // when name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.getTile()");
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getTile(%s)", getGameWrapper().getFirstPlayer().getName()));
  //
  //    String expected = String.format("%d,%d,%d",
  //        p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //        p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //        p.getZ() + PLAYER_PLACEMENT_Z_OFFSET);
  //
  //    assertEquals(Lists.newArrayList(expected, expected), getTestOut().sends);
  //
  //    // result is relative to player origin
  //
  //    setUpAtPlayerOrigin(new Vector3i(3, 3, 3));
  //
  //    getApiInvocationHandler().handleLine("player.getTile()");
  //
  //    expected = String.format("%d,%d,%d",
  //        p.getX() + PLAYER_PLACEMENT_X_OFFSET - 3,
  //        p.getY() + PLAYER_PLACEMENT_Y_OFFSET - 3,
  //        p.getZ() + PLAYER_PLACEMENT_Z_OFFSET - 3);
  //
  //    assertEquals(Lists.newArrayList(expected), getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_player_setTile() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("player.setTile");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    getGameWrapper().getFirstPlayer().setPitch(-74f);
  //    getGameWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //            p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //            p.getZ() + PLAYER_PLACEMENT_Z_OFFSET),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // move the player diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.setTile(%s,5,5,5)", getGameWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + 5,
  //            p.getY() + 5,
  //            p.getZ() + 5),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getGameWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getGameWrapper().getFirstPlayer().getRotation());
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.setTile(7,7,7)");
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + 7,
  //            p.getY() + 7,
  //            p.getZ() + 7),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //  }
  //}
  //
  //@Test
  //public void test_player_getPos() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("player.getPos");
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
  //        String.format("player.getPos(%s)", getGameWrapper().getFirstPlayer().getName()));
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
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("player.setPos");
  //
  //    Vector3i pMid = new Vector3i(p.getX() + 0.5f, p.getY() + 0.5f, p.getZ() + 0.5f);
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(pMid);
  //
  //    getGameWrapper().getFirstPlayer().setPitch(-74f);
  //    getGameWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + (double) PLAYER_PLACEMENT_X_OFFSET,
  //            p.getY() + (double) PLAYER_PLACEMENT_Y_OFFSET,
  //            p.getZ() + (double) PLAYER_PLACEMENT_Z_OFFSET),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // move the player diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.setPos(%s,5.2,5.2,5.2)",
  //            getGameWrapper().getFirstPlayer().getName()));
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + 5.2f,
  //            p.getY() + 5.2f,
  //            p.getZ() + 5.2f),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getGameWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getGameWrapper().getFirstPlayer().getRotation());
  //
  //    // when player name is blank, default to first player
  //
  //    getApiInvocationHandler().handleLine("player.setPos(7.2,7.2,7.2)");
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + 7.2f,
  //            p.getY() + 7.2f,
  //            p.getZ() + 7.2f),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //  }
  //}
  //
  //@Test
  //public void test_player_getDirection() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    getGameWrapper().getFirstPlayer().setPitch(47f);
  //    getGameWrapper().getFirstPlayer().setRotation(97f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getDirection(%s)", getGameWrapper().getFirstPlayer().getName()));
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
  //  if (getGameWrapper().hasPlayers()) {
  //    getGameWrapper().getFirstPlayer().setPitch(49f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getPitch(%s)", getGameWrapper().getFirstPlayer().getName()));
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
  //  if (getGameWrapper().hasPlayers()) {
  //    getGameWrapper().getFirstPlayer().setRotation(93f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("player.getRotation(%s)", getGameWrapper().getFirstPlayer().getName()));
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
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("entity.getTile");
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getTile(%d)", getGameWrapper().getFirstPlayer().getID()));
  //
  //    String expected = String.format("%d,%d,%d",
  //        p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //        p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //        p.getZ() + PLAYER_PLACEMENT_Z_OFFSET);
  //
  //    assertEquals(Lists.newArrayList(expected), getTestOut().sends);
  //  }
  //}
  //
  //@Test
  //public void test_entity_setTile() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("entity.setTile");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    getGameWrapper().getFirstPlayer().setPitch(-74f);
  //    getGameWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + PLAYER_PLACEMENT_X_OFFSET,
  //            p.getY() + PLAYER_PLACEMENT_Y_OFFSET,
  //            p.getZ() + PLAYER_PLACEMENT_Z_OFFSET),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // move the entity diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.setTile(%d,5,5,5)", getGameWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + 5,
  //            p.getY() + 5,
  //            p.getZ() + 5),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getGameWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getGameWrapper().getFirstPlayer().getRotation());
  //  }
  //}
  //
  //@Test
  //public void test_entity_getPos() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("entity.getPos");
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(p);
  //
  //    // entity.getPos position result is relative to the origin (spawn location)
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getPos(%d)", getGameWrapper().getFirstPlayer().getID()));
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
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    Vector3i p = nextTestPosition("entity.setPos");
  //
  //    Vector3i pMid = new Vector3i(p.getX() + 0.5f, p.getY() + 0.5f, p.getZ() + 0.5f);
  //
  //    // make the origin == p
  //
  //    setUpAtPlayerOrigin(pMid);
  //
  //    getGameWrapper().getFirstPlayer().setPitch(-74f);
  //    getGameWrapper().getFirstPlayer().setRotation(89f);
  //
  //    // initial position
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + (double) PLAYER_PLACEMENT_X_OFFSET,
  //            p.getY() + (double) PLAYER_PLACEMENT_Y_OFFSET,
  //            p.getZ() + (double) PLAYER_PLACEMENT_Z_OFFSET),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // move the entity diagonally
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.setPos(%d,5.2,5.2,5.2)",
  //            getGameWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(
  //        new Vector3i(
  //            p.getX() + 5.2f,
  //            p.getY() + 5.2f,
  //            p.getZ() + 5.2f),
  //        getGameWrapper().getFirstPlayer().getPosition());
  //
  //    // make sure the pitch and yaw are maintained
  //
  //    assertEquals(-74, (int) getGameWrapper().getFirstPlayer().getPitch());
  //    assertEquals(89, (int) getGameWrapper().getFirstPlayer().getRotation());
  //  }
  //}
  //
  //@Test
  //public void test_entity_getDirection() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //
  //    getGameWrapper().getFirstPlayer().setPitch(47f);
  //    getGameWrapper().getFirstPlayer().setRotation(97f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getDirection(%d)", getGameWrapper().getFirstPlayer().getID()));
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
  //  if (getGameWrapper().hasPlayers()) {
  //    getGameWrapper().getFirstPlayer().setPitch(49f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getPitch(%d)", getGameWrapper().getFirstPlayer().getID()));
  //
  //    assertEquals(1, getTestOut().sends.size());
  //    assertEquals(49, (int) Float.parseFloat(getTestOut().sends.get(0)));
  //  }
  //}
  //
  //@Test
  //public void test_entity_getRotation() throws Exception {
  //  if (getGameWrapper().hasPlayers()) {
  //    getGameWrapper().getFirstPlayer().setRotation(93f);
  //
  //    getApiInvocationHandler().handleLine(
  //        String.format("entity.getRotation(%d)", getGameWrapper().getFirstPlayer().getID()));
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
