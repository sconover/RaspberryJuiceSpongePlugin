package com.giantpurplekitty.raspberrysponge;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "raspberry-juice-sponge-plugin", name = "RaspberryJuiceSpongePlugin", version = "0.1")
public class HelloPlugin {
  @Subscribe
  public void onServerStart(ServerStartedEvent event) {
    System.out.println("XXXXXX STARTED PLUGIN 2 XXXXX");
  }

  @Subscribe
  public void onCommand(CommandEvent event) {
    System.out.println("XXXXXX COMMAND 1 XXXXX");
  }
}
