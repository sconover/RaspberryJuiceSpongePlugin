package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class Util {
  public static Vector3i blockPositionRelativeTo(Vector3i p, Vector3i relativeTo) {
    return new Vector3i(
        p.getX() - relativeTo.getX(),
        p.getY() - relativeTo.getY(),
        p.getZ() - relativeTo.getZ());
  }

  // creates a unit vector from rotation and pitch
  // origin is https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Location.java
  // "setDirection"
  public static Vector3d calculateDirection(double pitch, double rotation) {
    double rotationRad = Math.toRadians(rotation);
    double pitchRad = Math.toRadians(pitch);
    double x = (Math.sin(rotationRad) * Math.cos(pitchRad)) * -1;
    double y = Math.sin(pitchRad) * -1;
    double z = Math.cos(rotationRad) * Math.cos(pitchRad);
    return new Vector3d(x, y, z);
  }
}
