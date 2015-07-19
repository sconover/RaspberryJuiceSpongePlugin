package com.giantpurplekitty.raspberrysponge;

import com.giantpurplekitty.raspberrysponge.raspberryserver.RemoteSessionsManager;
import com.giantpurplekitty.raspberrysponge.raspberryserver.ServerListenerThread;
import java.net.InetSocketAddress;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.scheduler.TaskBuilder;

@Plugin(id = "RaspberryJuiceSpongePlugin", name = "RaspberryJuiceSpongePlugin", version = "0.1")
public class RaspberryJuiceSpongePlugin {

  private final Game game;
  private final Logger logger;
  private final RemoteSessionsManager remoteSessionsManager;
  private ServerListenerThread serverThread;

  @Inject
  public RaspberryJuiceSpongePlugin(Game game, Logger logger) {
    this.game = game;
    this.logger = logger;
    this.remoteSessionsManager = new RemoteSessionsManager(game, logger);
  }

  @Subscribe
  public void onServerStart(ServerStartedEvent event) {
    // TODO: make config file, and make this configurable
    int port = 4711;

    TaskBuilder taskBuilder = game.getScheduler().getTaskBuilder();
    taskBuilder
        .interval(1)
        .execute(new Runnable() {
          public void run() {
            remoteSessionsManager.onTick();
          }
        })
        .name("raspberry-juice-sponge-plugin--game-event-hander--onTick")
        .submit(this);

    //[NOTE - STRAIGHT PORT - sconover]
    //create new tcp listener thread
    try {
      serverThread =
          new ServerListenerThread(
              remoteSessionsManager,
              new InetSocketAddress(port),
              logger);
      new Thread(serverThread).start();
      logger.info("ThreadListener Started");
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn("Failed to start ThreadListener");
    }
  }

  @Subscribe
  public void onServerStopping(ServerStoppingEvent event) {
    //[NOTE - STRAIGHT PORT - sconover]
    remoteSessionsManager.closeAllSessions();
    serverThread.stop();
    serverThread = null;
    logger.info("Raspberry Juice Stopped");
  }
}
