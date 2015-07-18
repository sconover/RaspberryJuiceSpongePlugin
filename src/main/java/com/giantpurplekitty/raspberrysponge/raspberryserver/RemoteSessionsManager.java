package com.giantpurplekitty.raspberrysponge.raspberryserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.spongepowered.api.Game;

public class RemoteSessionsManager {

  private final Game game;
  private final Logger logger;
  private final List<RemoteSession> remoteSessions;

  //TODO: port block hit event

  public RemoteSessionsManager(Game game, Logger logger) {
    this(game, logger, new ArrayList<RemoteSession>());
  }

  public RemoteSessionsManager(Game game, Logger logger, List<RemoteSession> remoteSessions) {
    this.game = game;
    this.logger = logger;
    this.remoteSessions = remoteSessions;
  }

  public void handleNewRemoteConnection(Socket newConnection) {
    synchronized (this.remoteSessions) {
      this.remoteSessions.add(RemoteSession.create(game, logger, newConnection));
    }
  }

  public void onTick() {
    //[NOTE - STRAIGHT PORT - sconover]
    //called each tick of the server it gets all the remote sessions to run
    Iterator<RemoteSession> iterator = remoteSessions.iterator();
    while (iterator.hasNext()) {
      RemoteSession remoteSession = iterator.next();
      if (remoteSession.isPendingRemoval()) {
        remoteSession.close();
        iterator.remove();
      } else {
        remoteSession.tick();
      }
    }
  }

  public void closeAllSessions() {
    for (RemoteSession session : remoteSessions) {
      try {
        session.close();
      } catch (Exception e) {
        logger.warn("Failed to close RemoteSession");
        e.printStackTrace();
      }
    }
  }
}
