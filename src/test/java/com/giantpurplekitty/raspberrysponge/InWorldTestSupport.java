package com.giantpurplekitty.raspberrysponge;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.dispatch.ApiInvocationHandler;
import com.giantpurplekitty.raspberrysponge.game.CuboidReference;
import com.giantpurplekitty.raspberrysponge.game.GameWrapper;
import com.giantpurplekitty.raspberrysponge.game.LocationHelper;
import com.giantpurplekitty.raspberrysponge.raspberryserver.RemoteSession;
import java.util.ArrayDeque;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.manipulator.tileentity.SignData;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Intended to be extended by test classes. Provides convenience objects and methods
 * for use in in-world testing.
 */
public abstract class InWorldTestSupport {

  // sorry, I'm not going to mess around with test DI for this, right now, or maybe ever. -sconover
  private static Game game;
  private static Logger logger;

  public static void init(Game game, Logger logger) {
    InWorldTestSupport.game = game;
    InWorldTestSupport.logger = logger;
  }

  public static final int PLAYER_PLACEMENT_X_OFFSET = 0;
  public static final int PLAYER_PLACEMENT_Y_OFFSET = 10;
  public static final int PLAYER_PLACEMENT_Z_OFFSET = -30;

  private GameWrapper gameWrapper;
  private static int xOffset = 2;
  private TestOut testOut;
  private ApiInvocationHandler apiInvocationHandler;

  public GameWrapper getGameWrapper() {
    return gameWrapper;
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
    gameWrapper = new GameWrapper(game);

    //TODO
    //makeSureChunksHaveBeenGenerated(serverWrapper.getWorld(), position);

    gameWrapper.setSpawnPosition(position);

    testOut = new TestOut();

    apiInvocationHandler = new ApiInvocationHandler(
        gameWrapper,
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
    ref.fetchBlocks(gameWrapper).makeEmpty();
  
    Location block = gameWrapper.getLocation(justBeforeTestPosition);
    block.setBlockType(BlockTypes.SEA_LANTERN);

    // TODO: rotate the sign in the direction of the player.
    // don't know how to do that in sponge yet

    Location blockAbove = block.add(0, 1, 0);
    blockAbove.setBlockType(BlockTypes.STANDING_SIGN);

    TileEntity signTileEntity = blockAbove.getTileEntity().get();
    SignData sign = signTileEntity.getOrCreate(SignData.class).get();
    sign.setLine(0, Texts.of(name));
    signTileEntity.offer(sign);

    if (gameWrapper.hasPlayers()) {
      LocationHelper.PositionAndRotation positionAndRotation =
          LocationHelper.getLocationFacingPosition(testPosition,
              PLAYER_PLACEMENT_X_OFFSET, PLAYER_PLACEMENT_Y_OFFSET, PLAYER_PLACEMENT_Z_OFFSET);

      gameWrapper.getFirstPlayer()
          .setLocationAndRotation(
              gameWrapper.getLocation(positionAndRotation.position),
              positionAndRotation.rotation);
    }
    xOffset += 30;
  
    return testPosition;
  }

  public boolean shouldRunBecausePlayerIsLoggedIntoGame() {
    boolean result = getGameWrapper().hasPlayers();
    if (!result) {
      logger.warn("Skipping test because there's no player logged into the game!");
    }
    return result;
  }

  public void assertVector3dEquals(Vector3d expected, Vector3d actual) {
    Assert.assertEquals(expected.getX(), actual.getX(), 0.1d);
    Assert.assertEquals(expected.getY(), actual.getY(), 0.1d);
    Assert.assertEquals(expected.getZ(), actual.getZ(), 0.1d);
  }

}