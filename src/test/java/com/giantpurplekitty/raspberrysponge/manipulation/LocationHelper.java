package com.giantpurplekitty.raspberrysponge.manipulation;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

/**
 * Convenience methods for use in tests, for determining a CanaryMod "Location"
 * (position + pitch + rotation)
 */
public class LocationHelper {
  public static PositionAndRotation getLocationFacingPosition(
      Vector3i p,
      int xOffset, int yOffset, int zOffset) {
    float yaw = 0.0f;

    //TODO this is crude, calculate tangent instead
    if (xOffset < 0) {
      yaw = 270.0f;
    }
    if (xOffset > 0) {
      yaw = 90.0f;
    }
    if (zOffset < 0) {
      yaw = 0.0f;
    }
    if (zOffset > 0) {
      yaw = 180.0f;
    }

    float pitch = 0.0f;
    float roll = 0.0f;

    Vector3i newPosition = new Vector3i(p.getX() + xOffset, p.getY() + yOffset, p.getZ() + zOffset);
    Vector3d rotation = new Vector3d(yaw, pitch, roll);

    return new PositionAndRotation(newPosition, rotation);
  }

  public static class PositionAndRotation {
    public final Vector3i position;
    public final Vector3d rotation;

    public PositionAndRotation(Vector3i position, Vector3d rotation) {
      this.position = position;
      this.rotation = rotation;
    }
  }
}
