package com.giantpurplekitty.raspberrysponge;

import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.dispatch.ApiInvocationHandler;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.LocationHelper;
import com.giantpurplekitty.raspberrysponge.game.ServerWrapper;
import com.giantpurplekitty.raspberrysponge.raspberryserver.RemoteSession;
import java.util.ArrayDeque;
import org.junit.Before;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Intended to be extended by test classes. Provides convenience objects and methods
 * for use in in-world testing.
 */
public abstract class InWorldTestSupport {

  // sorry, I'm not going to mess around with test DI for this, right now, or maybe ever. -sconover
  public static Game game;
  public static Logger logger;

  public static final int PLAYER_PLACEMENT_X_OFFSET = 0;
  public static final int PLAYER_PLACEMENT_Y_OFFSET = 10;
  public static final int PLAYER_PLACEMENT_Z_OFFSET = -30;

  private ServerWrapper serverWrapper;
  private static int xOffset = 2;
  private TestOut testOut;
  private ApiInvocationHandler apiInvocationHandler;

  public ServerWrapper getServerWrapper() {
    return serverWrapper;
  }

  public TestOut getTestOut() {
    return testOut;
  }

  public ApiInvocationHandler getApiInvocationHandler() {
    return apiInvocationHandler;
  }

  @Before
  public void setUp() throws Exception {
    // check that dependencies are setup.
    checkNotNull(game);
    checkNotNull(logger);

    setUpAtPlayerOrigin(new Vector3i(0, 0, 0));
  }

  public void setUpAtPlayerOrigin(Vector3i position) {
    serverWrapper = new ServerWrapper(game.getServer());

    //TODO
    //makeSureChunksHaveBeenGenerated(serverWrapper.getWorld(), position);

    serverWrapper.setSpawnPosition(position);

    testOut = new TestOut();

    apiInvocationHandler = new ApiInvocationHandler(
        serverWrapper,
        logger,
        testOut);

    final RemoteSession session = new RemoteSession(
        logger,
        new RemoteSession.ToOutQueue(new ArrayDeque<String>()),
        apiInvocationHandler,
        null);

    //pluginListener = new CanaryRaspberryJuiceListener(new RemoteSessionsHolder() {
    //  @Override public Iterable<RemoteSession> get() {
    //    return Lists.newArrayList(session);
    //  }
    //});
  }

  //
  //public void makeFirstPlayerWieldItem(Player player, ItemType itemType) {
  //  player.getInventory().setSlot(itemType.getId(), 0, 0);
  //
  //  player.getInventory().setSlot(
  //      player.getInventory().getSelectedHotbarSlotId(),
  //      player.getInventory().getSlot(0));
  //}
  //
  public Vector3i nextTestPosition(String name) {
    Vector3i testPosition = new Vector3i(xOffset, 100, 2);
    Vector3i justBeforeTestPosition = new Vector3i(xOffset-1, 99, 1);
    CuboidReference ref = new CuboidReference(justBeforeTestPosition, 31, 51, 31);
    ref.fetchBlocks(serverWrapper.getWorld()).makeEmpty();
  
    Location block = serverWrapper.getWorld().getLocation(justBeforeTestPosition);
    block.setBlockType(BlockTypes.SEA_LANTERN);

    Location blockAbove = block.add(0, 1, 0);
    blockAbove.setBlockType(BlockTypes.STANDING_SIGN);

    // TODO - write on the sign
    //StandingSignProperties.applyRotation(blockAbove, BlockProperties.Rotation.NORTH);
    //blockAbove.update();
    //
    //CanarySign sign = (CanarySign)blockAbove.getTileEntity();
    //sign.setTextOnLine(name, 0);
    //sign.setTextOnLine("bar", 1);
    //sign.setTextOnLine("zzz", 2);
    //sign.setTextOnLine("yyy", 3);
  
    if (serverWrapper.hasPlayers()) {
      LocationHelper.PositionAndRotation positionAndRotation =
          LocationHelper.getLocationFacingPosition(testPosition,
              PLAYER_PLACEMENT_X_OFFSET, PLAYER_PLACEMENT_Y_OFFSET, PLAYER_PLACEMENT_Z_OFFSET);

      serverWrapper.getFirstPlayer()
          .setLocationAndRotation(
              new Location(serverWrapper.getWorld(), positionAndRotation.position),
              positionAndRotation.rotation);
    }
    xOffset += 30;
  
    return testPosition;
  }
}