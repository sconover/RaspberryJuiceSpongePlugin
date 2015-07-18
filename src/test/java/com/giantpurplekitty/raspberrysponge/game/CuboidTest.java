package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.InWorldTestSupport;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CuboidTest extends InWorldTestSupport {
  @Test
  public void testChangeType() {
    Vector3i p = nextTestPosition("testChangeType");
    Cuboid cuboid = new CuboidReference(p, 10, 10, 10)
        .fetchBlocks(getGameWrapper());

    cuboid.makeEmpty();
    assertEquals(BlockTypes.AIR, getGameWrapper().getBlock(p).getType());
    cuboid.changeBlocksToType(BlockTypes.GOLD_BLOCK);
    assertEquals(BlockTypes.GOLD_BLOCK, getGameWrapper().getBlock(p).getType());
  }

  @Test
  public void testCuboid() {
    Vector3i topOfWorld = new Vector3i(1, 250, 1);

    CuboidReference topRef = new CuboidReference(topOfWorld, 1, 1, 1);
    Cuboid cuboid = topRef.fetchBlocks(getGameWrapper());
    List<Relative<Location>> blockLocations = Lists.newArrayList(cuboid);
    assertEquals(1, blockLocations.size());
    assertEquals(new Vector3i(1, 250, 1), blockLocations.get(0).object.getBlockPosition());
    assertEquals(BlockTypes.AIR, blockLocations.get(0).object.getBlockType());

    CuboidReference topLargerRef = new CuboidReference(topOfWorld, 2, 3, 2);
    cuboid = topLargerRef.fetchBlocks(getGameWrapper());
    blockLocations = Lists.newArrayList(cuboid);
    assertEquals(12, blockLocations.size());
    assertEquals(new Vector3i(1, 250, 1), blockLocations.get(0).object.getBlockPosition());
    assertEquals(new Vector3i(1, 250, 2), blockLocations.get(1).object.getBlockPosition());
    assertEquals(new Vector3i(1, 251, 1), blockLocations.get(2).object.getBlockPosition());
    assertEquals(new Vector3i(1, 251, 2), blockLocations.get(3).object.getBlockPosition());
    assertEquals(new Vector3i(1, 252, 1), blockLocations.get(4).object.getBlockPosition());
    assertEquals(new Vector3i(1, 252, 2), blockLocations.get(5).object.getBlockPosition());
    assertEquals(new Vector3i(2, 250, 1), blockLocations.get(6).object.getBlockPosition());
    assertEquals(new Vector3i(2, 250, 2), blockLocations.get(7).object.getBlockPosition());
    assertEquals(new Vector3i(2, 251, 1), blockLocations.get(8).object.getBlockPosition());
    assertEquals(new Vector3i(2, 251, 2), blockLocations.get(9).object.getBlockPosition());
    assertEquals(new Vector3i(2, 252, 1), blockLocations.get(10).object.getBlockPosition());
    assertEquals(new Vector3i(2, 252, 2), blockLocations.get(11).object.getBlockPosition());

    assertTrue(cuboid.isAir());

    Vector3i bottomOfWorld = new Vector3i(0, 0, 0);
    cuboid = new CuboidReference(bottomOfWorld, 2, 4, 2).fetchBlocks(getGameWrapper());
    blockLocations = Lists.newArrayList(cuboid);
    assertEquals(16, blockLocations.size());

    assertFalse(cuboid.isAir());
  }

  @Test
  public void testCuboidCenter() {
    Vector3i topOfWorld = new Vector3i(1, 250, 1);

    assertEquals(
        Arrays.asList(
            new Vector3i(1, 251, 1),
            new Vector3i(1, 251, 2),
            new Vector3i(2, 251, 1),
            new Vector3i(2, 251, 2)),
        new CuboidReference(topOfWorld, 2, 3, 2)
            .center()
            .fetchBlocks(getGameWrapper())
            .toPositions());

    assertEquals(
        Arrays.asList(new Vector3i(1, 250, 1)),
        new CuboidReference(topOfWorld, 1, 1, 1)
            .center()
            .fetchBlocks(getGameWrapper())
            .toPositions());
  }
}