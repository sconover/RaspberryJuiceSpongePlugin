package com.giantpurplekitty.raspberrysponge.game;

import org.spongepowered.api.entity.Entity;

public class EntityHelper {

  // this will need to be removed / solved if we want to get away from depending on SpongeCommon.
  // unfortunately, the current raspberry / mcpi api makes extensive use of entity id's
  // (and not uuid's, as one might prefer), so this is necessary.

  public static int getEntityId(Entity entity) {
    return ((net.minecraft.entity.player.EntityPlayerMP)entity).getEntityId();
  }
}
