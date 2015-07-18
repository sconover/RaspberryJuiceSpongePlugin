package com.giantpurplekitty.raspberrysponge;

import org.slf4j.Logger;

public class CommandHandler {
  private final ServerWrapper serverWrapper;
  private final Logger logger;
  private final RemoteSession.ToOutQueue toOutQueue;

  public CommandHandler(
      ServerWrapper serverWrapper,
      Logger logger,
      RemoteSession.ToOutQueue toOutQueue) { // TODO: move ToOutQueue to top level

    this.serverWrapper = serverWrapper;
    this.logger = logger;
    this.toOutQueue = toOutQueue;
  }

  public void handleLine(String message) {
    // blah
  }
}
