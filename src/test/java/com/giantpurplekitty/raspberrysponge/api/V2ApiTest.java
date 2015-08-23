package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.InWorldTestSupport;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.DataHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.animal.Ocelot;
import org.spongepowered.api.world.Location;

import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getColorForIntegerId;
import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getIntegerIdForBlockType;
import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getIntegerIdForColor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class V2ApiTest extends InWorldTestSupport {

  @Test
  public void test_v2_world_setBlock() throws Exception {
    Vector3i p = nextTestPosition("v2.world.setBlock");

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlock(%d,%d,%d,redstone_block)",
            p.getX(),
            p.getY(),
            p.getZ()));

    Location block = getGameWrapper().getLocation(p);
    assertEquals(BlockTypes.REDSTONE_BLOCK, block.getBlockType());

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlock(%d,%d,%d,wool,color=lime)",
            p.getX() + 1,
            p.getY(),
            p.getZ()));

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
    Vector3i cubeCorner = nextTestPosition("v2.world.setBlocks simple");

    Vector3i otherCubeCorner =
        new Vector3i(
            cubeCorner.getX() + 1,
            cubeCorner.getY() + 1,
            cubeCorner.getZ() + 1);

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlocks(%d,%d,%d,%d,%d,%d,redstone_block)",
            cubeCorner.getX(),
            cubeCorner.getY(),
            cubeCorner.getZ(),
            otherCubeCorner.getX(),
            otherCubeCorner.getY(),
            otherCubeCorner.getZ()));

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
  public void test_v2_world_setBlock_modify_existing_block_if_same_type() throws Exception {
    Vector3i p = nextTestPosition("v2.world.setBlock");

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlock(%d,%d,%d,piston,extended=false;facing=west)",
            p.getX(),
            p.getY(),
            p.getZ()));

    Location block = getGameWrapper().getLocation(p);
    assertEquals(BlockTypes.PISTON, block.getBlockType());
    assertEquals(ImmutableMap.of("extended", false, "facing", "west"),
        block.getBlock().getPrimitiveProperties());

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlock(%d,%d,%d,piston,extended=true)",
            p.getX(),
            p.getY(),
            p.getZ()));

    Location block2 = getGameWrapper().getLocation(p);
    assertEquals(BlockTypes.PISTON, block2.getBlockType());
    assertEquals(ImmutableMap.of("extended", true, "facing", "west"),
        block2.getBlock().getPrimitiveProperties());
  }

  @Test
  public void test_v2_world_getBlock() throws Exception {
    Vector3i p = nextTestPosition("v2.world.getBlock");

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlock(%d,%d,%d,redstone_block)",
            p.getX(),
            p.getY(),
            p.getZ()));

    Location block = getGameWrapper().getLocation(p);
    assertEquals(BlockTypes.REDSTONE_BLOCK, block.getBlockType());

    getApiInvocationHandler().handleLine(
        String.format("v2.world.setBlock(%d,%d,%d,wool,color=lime)",
            p.getX() + 1,
            p.getY(),
            p.getZ()));

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
  public void test_v2_entity_spawn() throws Exception {
    Vector3i p = nextTestPosition("v2.entity.spawn");

    getApiInvocationHandler().handleLine(
        String.format("v2.entity.spawn(%d,%d,%d,ocelot)",
            p.getX(),
            p.getY(),
            p.getZ()));

    assertEquals(1, getTestOut().sends.size());
    String[] resultParts = getTestOut().sends.get(0).split(",");

    String entityTypeName = resultParts[0];
    assertEquals("ocelot", entityTypeName);

    String ocelotUuid = resultParts[1];

    Optional<Entity> maybeEntity = getGameWrapper().getEntityByUuid(ocelotUuid);
    assertTrue(maybeEntity.isPresent());
    Entity entity = maybeEntity.get();
    assertEquals(p, entity.getLocation().getBlockPosition());
    assertEquals(EntityTypes.OCELOT, entity.getType());
  }

  @Test
  public void test_v2_entity_spawn__and_set_owner() throws Exception {
    Vector3i p = nextTestPosition("v2.entity.spawn");

    getApiInvocationHandler().handleLine(
        String.format("v2.entity.spawn(%d,%d,%d,ocelot)",
            p.getX(),
            p.getY(),
            p.getZ()));

    assertEquals(1, getTestOut().sends.size());
    String[] resultParts = getTestOut().sends.get(0).split(",");
    String ocelotUuid1 = resultParts[1];

    getApiInvocationHandler().handleLine(
        String.format("v2.entity.spawn(%d,%d,%d,ocelot,owner=%s)",
            p.getX(),
            p.getY(),
            p.getZ(),
            ocelotUuid1));

    assertEquals(2, getTestOut().sends.size());
    resultParts = getTestOut().sends.get(1).split(",");
    String ocelotUuid2 = resultParts[1];

    Ocelot ocelot = (Ocelot)getGameWrapper().getEntityByUuid(ocelotUuid2).get();
    assertEquals(ocelotUuid1, ocelot.getOwnerId());
  }
}
