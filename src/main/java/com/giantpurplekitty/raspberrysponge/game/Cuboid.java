package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;

/**
 * A "materialized" cuboid of blocks from a minecraft world.
 *
 * Intended to make mass block operations easy to perform.
 */
public class Cuboid implements Iterable<Relative<Location>> {
  private final Location[][][] blocks;

  public Cuboid(Location[][][] blocks) {
    this.blocks = blocks;
  }

  public Set<BlockType> blockTypes() {
    Set<BlockType> blockTypes = new LinkedHashSet<BlockType>();
    for (Relative<Location> blockLocation : this) {
      blockTypes.add(blockLocation.object.getBlockType());
    }
    return blockTypes;
  }

  public BlockType[] blockTypeForEachBlock() {
    List<BlockType> blockTypes = new ArrayList<BlockType>();
    for (Relative<Location> blockLocation : this) {
      blockTypes.add(blockLocation.object.getBlockType());
    }
    return blockTypes.toArray(new BlockType[blockTypes.size()]);
  }

  public Map<BlockType, List<Location>> blockTypeToBlocks() {
    Map<BlockType, List<Location>> blockTypeToBlockList = new LinkedHashMap<BlockType, List<Location>>();
    for (Relative<Location> blockLocation : this) {
      if (!blockTypeToBlockList.containsKey(blockLocation.object.getBlockType())) {
        blockTypeToBlockList.put(blockLocation.object.getBlockType(), new ArrayList<Location>());
      }
      blockTypeToBlockList.get(blockLocation.object.getBlockType()).add(blockLocation.object);
    }
    return ImmutableMap.copyOf(blockTypeToBlockList);
  }

  public boolean isUniformType(BlockType blockType) {
    return blockTypes().equals(Sets.newHashSet(blockType));
  }

  public boolean isAir() {
    return isUniformType(BlockTypes.AIR);
  }

  public Iterator<Relative<Location>> iterator() {
    return new RelativeBlockIterator(blocks);
  }

  public Cuboid changeBlocksToType(BlockType newType) {
    for (Relative<Location> relativeBlock : this) {
      relativeBlock.object.setBlockType(newType);
    }
    return this;
  }

  public Cuboid makeEmpty() {
    changeBlocksToType(BlockTypes.AIR);
    return this;
  }

  public Location firstBlock() {
    return blocks[0][0][0];
  }

  public List<Vector3i> toPositions() {
    List<Vector3i> positions = new ArrayList<Vector3i>();
    for (Relative<Location> relativeBlock : this) {
      positions.add(relativeBlock.object.getBlockPosition());
    }
    return ImmutableList.copyOf(positions);
  }

  public static class RelativeBlockIterator implements Iterator<Relative<Location>> {
    private final Location[][][] blocks;
    private int x;
    private int y;
    private int z;
    private Relative next;

    public RelativeBlockIterator(Location[][][] blocks) {
      this.blocks = blocks;

      Preconditions.checkState(
          blocks.length >= 1 &&
              blocks[0].length >= 1 &&
              blocks[0][0].length >= 1,
          "3D block array must be at least 1x1x1");
      this.next = new Relative<Location>(blocks[0][0][0], 0, 0, 0);
    }

    @Override public boolean hasNext() {
      return next != null;
    }

    @Override public Relative<Location> next() {
      Relative<Location> result = next;
      advance();
      return result;
    }

    private void advance() {
      if (x == blocks.length - 1 &&
          y == blocks[0].length - 1 &&
          z == blocks[0][0].length - 1) {
        next = null;
        return;
      } else if (z < blocks[x][y].length - 1) {
        z += 1;
      } else if (y < blocks[x].length - 1) {
        y += 1;
        z = 0;
      } else if (x < blocks.length - 1) {
        x += 1;
        y = 0;
        z = 0;
      }
      next = new Relative<Location>(blocks[x][y][z], x, y, z);
    }
  }
}