package com.giantpurplekitty.raspberrysponge.manipulation;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A "potential" cuboid (collection of blocks) in a minecraft world.
 *
 * Intentionally does not depend on any Minecraft server connection.
 */
public class CuboidReference {

  public static CuboidReference relativeTo(Vector3i origin, Vector3i p1) {
    return relativeTo(origin, p1, p1);
  }

  public static CuboidReference relativeTo(Vector3i origin, Vector3i p1, Vector3i p2) {
    Vector3i relativeP1 = new Vector3i(
        origin.getX() + p1.getX(),
        origin.getY() + p1.getY(),
        origin.getZ() + p1.getZ());
    Vector3i relativeP2 = new Vector3i(
        origin.getX() + p2.getX(),
        origin.getY() + p2.getY(),
        origin.getZ() + p2.getZ());
    return fromCorners(relativeP1, relativeP2);
  }

  public static CuboidReference fromCorners(Vector3i p1, Vector3i p2) {
    int minX = p1.getX() < p2.getX() ? p1.getX() : p2.getX();
    int maxX = p1.getX() >= p2.getX() ? p1.getX() : p2.getX();
    int minY = p1.getY() < p2.getY() ? p1.getY() : p2.getY();
    int maxY = p1.getY() >= p2.getY() ? p1.getY() : p2.getY();
    int minZ = p1.getZ() < p2.getZ() ? p1.getZ() : p2.getZ();
    int maxZ = p1.getZ() >= p2.getZ() ? p1.getZ() : p2.getZ();

    Vector3i start = new Vector3i(minX, minY, minZ);

    return new CuboidReference(start, maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
  }

  private final Vector3i start;
  private final int xSize;
  private final int ySize;
  private final int zSize;

  public CuboidReference(Vector3i start, int xSize, int ySize, int zSize) {
    Preconditions.checkArgument(
        xSize >= 1 && ySize >= 1 && zSize >= 1,
        "cuboid must be at least 1x1x1");
    this.start = start;
    this.xSize = xSize;
    this.ySize = ySize;
    this.zSize = zSize;
  }

  public Cuboid fetchBlocks(World world) {
    //TODO real-world bounds checking

    //TODO: consider wrapping world, with something that only exposes a minimal set of
    // read and write methods, that make sure reads and writes are "safe"
    // Make all code use this instead of World, directly.

    //TODO
    //makeSureChunksHaveBeenGenerated(world, start, xSize, zSize);

    Location corner = world.getLocation(start);

    Location[][][] result = new Location[xSize][ySize][zSize];
    for (int x = 0; x < xSize; x++) {
      for (int y = 0; y < ySize; y++) {
        for (int z = 0; z < zSize; z++) {
          result[x][y][z] = corner.add(x, y, z);
        }
      }
    }
    return new Cuboid(result);
  }

  public CuboidReference center() {
    int centerXStart = xSize % 2 == 0 ? xSize / 2 - 1 : xSize / 2;
    int centerXSize = xSize % 2 == 0 ? 2 : 1;

    int centerYStart = ySize % 2 == 0 ? ySize / 2 - 1 : ySize / 2;
    int centerYSize = ySize % 2 == 0 ? 2 : 1;

    int centerZStart = zSize % 2 == 0 ? zSize / 2 - 1 : zSize / 2;
    int centerZSize = zSize % 2 == 0 ? 2 : 1;

    Vector3i newStart = new Vector3i(
        start.getX() + centerXStart,
        start.getY() + centerYStart,
        start.getZ() + centerZStart);

    return new CuboidReference(newStart, centerXSize, centerYSize, centerZSize);
  }
}
