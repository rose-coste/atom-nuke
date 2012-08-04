package org.atomnuke.cli.command.eps.eventlet;

import org.atomnuke.cli.command.eps.relay.*;
import org.atomnuke.cli.command.source.*;
import org.atomnuke.cli.command.AbstractNukeCommand;
import org.atomnuke.config.ConfigurationHandler;
import org.atomnuke.config.ConfigurationReader;
import org.atomnuke.config.model.Source;
import org.atomnuke.util.cli.command.result.CommandResult;
import org.atomnuke.util.cli.command.result.MessageResult;

/**
 *
 * @author zinic
 */
public class ListEventlets extends AbstractNukeCommand {

   public ListEventlets(ConfigurationReader configurationReader) {
      super(configurationReader);
   }

   @Override
   public String getCommandToken() {
      return "list";
   }

   @Override
   public String getCommandDescription() {
      return "Lists all registered sinks.";
   }

   @Override
   public CommandResult perform(String[] arguments) throws Exception {
      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
      final StringBuilder output = new StringBuilder();

      for (Source source : getSources(cfgHandler)) {
         output.append("Source ").append(source.getId()).append(" binds ").append(source.getHref()).append(" as language type ").append(source.getType().name()).append("\n");
      }

      return new MessageResult(output.toString());
   }
}