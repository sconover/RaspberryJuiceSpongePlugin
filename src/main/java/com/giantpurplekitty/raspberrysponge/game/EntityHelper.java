package com.giantpurplekitty.raspberrysponge.game;

import org.spongepowered.api.entity.Entity;

public class EntityHelper {

  // this will need to be removed / solved if we want to get away from depending on SpongeCommon.
  // unfortunately, the current raspberry / mcpi api makes extensive use of entity id's
  // (and not uuid's, as one might prefer), so this is necessary.

  public static int getEntityId(Entity entity) {
    return ((net.minecraft.entity.Entity)entity).getEntityId();
  }

  public static Entity getEntityById(GameWrapper gameWrapper, int entityId) {
    for (Entity entity: gameWrapper.getEntities()) {
      if (getEntityId(entity) == entityId) {
        return entity;
      }
    }
    throw new RuntimeException(String.format("Couldn't find entity with id=%d", entityId));
  }
}
