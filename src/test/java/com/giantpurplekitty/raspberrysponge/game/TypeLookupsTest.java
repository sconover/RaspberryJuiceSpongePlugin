package com.giantpurplekitty.raspberrysponge.game;

import com.giantpurplekitty.raspberrysponge.InWorldTestSupport;
import org.junit.Test;
import org.spongepowered.api.block.BlockTypes;

import static org.junit.Assert.assertEquals;

public class TypeLookupsTest extends InWorldTestSupport {

  // see http://www.stuffaboutcode.com/p/minecraft-api-reference.html for mappings

  @Test
  public void test_block_types() {
    assertEquals(169, getGameWrapper().getBlockTypeIntegerId(BlockTypes.SEA_LANTERN));
  }
}
