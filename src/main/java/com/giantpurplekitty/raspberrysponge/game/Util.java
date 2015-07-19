package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;

public class Util {
  public static Vector3i blockPositionRelativeTo(Vector3i p, Vector3i relativeTo) {
    return new Vector3i(
        p.getX() - relativeTo.getX(),
        p.getY() - relativeTo.getY(),
        p.getZ() - relativeTo.getZ());
  }
}
