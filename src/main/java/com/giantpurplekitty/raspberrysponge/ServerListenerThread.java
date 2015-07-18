package com.giantpurplekitty.raspberrysponge;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import org.slf4j.Logger;

public class ServerListenerThread implements Runnable {
  private final ServerSocket serverSocket;
  private boolean running = true;
  private final RemoteSessionsManager remoteSessionsManager;
  private final Logger logger;

  public ServerListenerThread(
      RemoteSessionsManager remoteSessionsManager,
      SocketAddress bindAddress,
      Logger logger)
      throws IOException {
    this.remoteSessionsManager = remoteSessionsManager;
    this.logger = logger;
    this.serverSocket = new ServerSocket();
    serverSocket.setReuseAddress(true);
    serverSocket.bind(bindAddress);
  }

  public void run() {
    while (running) {
      try {
        Socket newConnection = serverSocket.accept();
        if (!running) return;
        remoteSessionsManager.handleNewRemoteConnection(newConnection);
      } catch (Exception e) {
        // if the server thread is still running raise an error
        if (running) {
          logger.warn("Error creating new connection");
          e.printStackTrace();
        }
      }
    }
    try {
      serverSocket.close();
    } catch (Exception e) {
      logger.warn("Error closing server socket");
      e.printStackTrace();
    }
  }

  public void stop() {
    running = false;
    try {
      serverSocket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
