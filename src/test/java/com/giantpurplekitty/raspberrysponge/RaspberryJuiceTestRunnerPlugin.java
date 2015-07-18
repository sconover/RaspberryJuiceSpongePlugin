package com.giantpurplekitty.raspberrysponge;

import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

@Plugin(id = "RaspberryJuiceTestRunnerPlugin", name = "RaspberryJuiceTestRunnerPlugin", version = "0.1")
public class RaspberryJuiceTestRunnerPlugin {

  public RaspberryJuiceTestRunnerPlugin() {
    System.out.println("XXXXXXX RaspberryJuiceTestRunnerPlugin XXXXXXXX");
    System.out.println("XXXXXXX RaspberryJuiceTestRunnerPlugin XXXXXXXX");
    System.out.println("XXXXXXX RaspberryJuiceTestRunnerPlugin XXXXXXXX");
  }

  @Subscribe
  public void onServerStarting(ServerStartingEvent event) {
    System.out.println("XXXXXXX REGISTERING COMMAND");
    event.getGame().getCommandDispatcher().register(this, new TestCommand(), "testfoo");
  }

  public class TestCommand implements CommandCallable {
    public Optional<CommandResult> process(CommandSource commandSource, String arguments)
        throws CommandException {
      return Optional.absent();
    }

    public List<String> getSuggestions(CommandSource commandSource, String arguments)
        throws CommandException {
      return new ArrayList<String>();
    }

    public boolean testPermission(CommandSource commandSource) {
      return true;
    }

    public Optional<? extends Text> getShortDescription(CommandSource commandSource) {
      return Optional.of(Texts.of("foo description"));
    }

    public Optional<? extends Text> getHelp(CommandSource commandSource) {
      return Optional.of(Texts.of("foo help"));
    }

    public Text getUsage(CommandSource commandSource) {
      return Texts.of("/test");
    }
  }
}
