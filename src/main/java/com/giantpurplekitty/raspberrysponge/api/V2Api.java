package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.dispatch.RPC;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.GameWrapper;
import java.util.HashMap;
import java.util.Map;
import org.spongepowered.api.block.BlockType;

import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getBlockTypeForName;

public class V2Api {
  private final GameWrapper gameWrapper;

  public V2Api(GameWrapper gameWrapper) {
    this.gameWrapper = gameWrapper;
  }

  @RPC("v2.world.setBlock")
  public void v2_world_setBlock(int x, int y, int z, String blockTypeName) {
    v2_world_setBlock(x, y, z, blockTypeName, new HashMap<String, String>());
  }

  @RPC("v2.world.setBlock")
  public void v2_world_setBlock(
      int x, int y, int z,
      String blockTypeName, Map<String,String> propertyNameToValue) {
    v2_world_setBlocks(
        x, y, z,
        x, y, z,
        blockTypeName, propertyNameToValue);
  }

  @RPC("v2.world.setBlocks")
  public void v2_world_setBlocks(
      int x1, int y1, int z1,
      int x2, int y2, int z2,
      String blockTypeName) {
    v2_world_setBlocks(
        x1, y1, z1,
        x2, y2, z2,
        blockTypeName, new HashMap<String, String>());
  }

  @RPC("v2.world.setBlocks")
  public void v2_world_setBlocks(
      int x1, int y1, int z1,
      int x2, int y2, int z2,
      String blockTypeName, Map<String,String> propertyNameToValue) {

    BlockType blockType = getBlockTypeForName("minecraft:" + blockTypeName);

    //TODO: test check property values, w/ test(s)

    CuboidReference.relativeTo(gameWrapper.getSpawnPosition(),
        new Vector3i(x1, y1, z1),
        new Vector3i(x2, y2, z2))
        .fetchBlocks(gameWrapper)
        .changeBlocksToTypeWithProperties(blockType, propertyNameToValue);
  }
}
