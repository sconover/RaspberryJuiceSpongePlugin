package com.giantpurplekitty.raspberrysponge;

import com.giantpurplekitty.raspberrysponge.raspberryserver.RemoteSessionsManager;
import com.giantpurplekitty.raspberrysponge.raspberryserver.ServerListenerThread;
import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.service.scheduler.TaskBuilder;

@Plugin(id = "RaspberryJuiceSpongePlugin", name = "RaspberryJuiceSpongePlugin", version = "0.1")
public class RaspberryJuiceSpongePlugin {

  public static final int DEFAULT_PORT = 4711;
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

  @Inject
  @DefaultConfig(sharedRoot = true)
  private File defaultConfig;

  @Inject
  @DefaultConfig(sharedRoot = true)
  private ConfigurationLoader<CommentedConfigurationNode> configManager;

  @Subscribe
  public void onServerStart(ServerStartedEvent event) {
    int port = loadPortFromConfigFile();

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
      logger.info(String.format("ThreadListener Started on port %d", port));
    } catch (Exception e) {
      e.printStackTrace();
      logger.warn(String.format("Failed to start ThreadListener on port %d", port));
    }
  }

  private int loadPortFromConfigFile() {
    int port;
    try {
      if (!defaultConfig.exists()) {
        defaultConfig.createNewFile();
        CommentedConfigurationNode config = configManager.load();
        config.getNode("port").setValue(DEFAULT_PORT); // default port
        configManager.save(config);
      }
      CommentedConfigurationNode config = configManager.load();
      port = config.getNode("port").getInt();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
    return port;
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
