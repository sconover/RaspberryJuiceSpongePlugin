package com.giantpurplekitty.raspberrysponge.api;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.dispatch.RPC;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.GameWrapper;
import com.google.common.base.Optional;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.CanBeOwned;
import org.spongepowered.api.entity.player.Player;

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

  @RPC("v2.entity.spawn")
  public Entity v2_entity_spawn(int x, int y, int z, String entityTypeName) {
    return v2_entity_spawn(x, y, z, entityTypeName, new LinkedHashMap<String,String>());
  }

  @RPC("v2.entity.spawn")
  public Entity v2_entity_spawn(int x, int y, int z, String entityTypeName,
      Map<String,String> propertyNameToValue) {
    EntityType entityType = gameWrapper.supportedEntityTypeForName(entityTypeName);
    Optional<Entity> maybeEntity =
        gameWrapper.tryToSpawnEntity(entityType, new Vector3i(x, y, z));
    if (maybeEntity.isPresent()) {
      Entity entity = maybeEntity.get();

      if (entity instanceof CanBeOwned &&
          propertyNameToValue.containsKey("owner")) {

        Optional<Player> maybePlayer = gameWrapper.maybeGetPlayerByName(propertyNameToValue.get("owner"));
        if (maybePlayer.isPresent()) {
          ((CanBeOwned)entity).setOwnerId(((Living)maybePlayer.get()).getUniqueId().toString());
        } else {
          Optional<Entity> maybeOwner = gameWrapper.getEntityByUuid(propertyNameToValue.get("owner"));
          // consider logging a warning if owner is missing
          if (maybeOwner.isPresent()) {
            ((CanBeOwned)entity).setOwnerId(((Living)maybeOwner.get()).getUniqueId().toString());
          }
        }
      }

      return entity;
    } else {
      return null;
    }
  }

  @RPC("v2.entity.living.startTask")
  public void v2_entity_living_startTask(String entityUuid, String taskName) {
    ((Living)gameWrapper.getEntityByUuid(entityUuid).get()).startTask(taskName);
  }

  @RPC("v2.entity.living.resetTask")
  public void v2_entity_living_resetTask(String entityUuid, String taskName) {
    ((Living)gameWrapper.getEntityByUuid(entityUuid).get()).resetTask(taskName);
  }

  @RPC("v2.entity.getAllInBoundingCube")
  public Entity[] v2_entity_getAllInBoundingCube(
      int x1, int y1, int z1,
      int x2, int y2, int z2) {
    Collection<Entity> entities = CuboidReference.fromCorners(new Vector3i(x1, y1, z1), new Vector3i(x2, y2, z2))
          .fetchEntities(gameWrapper);
    return entities.toArray(new Entity[entities.size()]);
  }
}
