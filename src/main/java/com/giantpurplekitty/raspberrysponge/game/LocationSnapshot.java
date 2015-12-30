package com.giantpurplekitty.raspberrysponge.game;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;

public class LocationSnapshot {

  public static LocationSnapshot of(Location l) {
    return new LocationSnapshot(l.getBlockPosition(), l.getBlock());
  }

  private final Vector3i position;
  private final BlockState block;

  private LocationSnapshot(Vector3i position, BlockState block) {
    this.position = position;
    this.block = block;
  }

  //ex: "10,10,10,piston,extended=false;facing=west"
  public String toApiResultString() {
    String result = String.format("%d,%d,%d,%s",
        position.getX(),
        position.getY(),
        position.getZ(),
        block.getType().getName().replace("minecraft:", ""));
    Map<String, Object> blockProperties = block.getPrimitiveProperties();
    if (!blockProperties.isEmpty()) {
      List<String> kvEntries = new ArrayList<String>();
      TreeMap<String, Object> sortedMap = new TreeMap<String, Object>(blockProperties);
      for (Map.Entry<String,Object> entry : sortedMap.entrySet()) {
        kvEntries.add(entry.getKey() + "=" + entry.getValue().toString());
      }
      result += "," + Joiner.on(";").join(kvEntries);
    }
    return result;
  }
}
