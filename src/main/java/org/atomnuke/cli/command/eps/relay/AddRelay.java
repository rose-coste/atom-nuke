package org.atomnuke.cli.command.eps.relay;

import org.atomnuke.cli.command.source.*;
import org.atomnuke.cli.command.AbstractNukeCommand;
import org.atomnuke.config.ConfigurationHandler;
import org.atomnuke.config.ConfigurationReader;
import org.atomnuke.config.model.LanguageType;
import org.atomnuke.config.model.Source;
import org.atomnuke.util.cli.command.result.CommandFailure;
import org.atomnuke.util.cli.command.result.CommandResult;
import org.atomnuke.util.cli.command.result.CommandSuccess;

/**
 *
 * @author zinic
 */
public class AddRelay extends AbstractNukeCommand {

   private static final int SOURCE_ID = 0, SINK_LANGUAGE = 1, SINK_REFERENCE = 2;

   public AddRelay(ConfigurationReader configurationReader) {
      super(configurationReader);
   }

   @Override
   public String getCommandToken() {
      return "add";
   }

   @Override
   public String getCommandDescription() {
      return "Adds a new source definition.";
   }

   @Override
   public CommandResult perform(String[] arguments) throws Exception {
      if (arguments.length != 3) {
         return new CommandFailure("Adding a source requires three arguments: <source-id> <language> <ref>");
      }
      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();

      if (findSource(arguments[SOURCE_ID]) != null) {
         return new CommandFailure("A source with the id \"" + arguments[SOURCE_ID] + "\" already exists.");
      }

      final LanguageType sinkLanguageType;

      try {
         sinkLanguageType = LanguageType.fromValue(arguments[SINK_LANGUAGE]);
      } catch (IllegalArgumentException iae) {
         return new CommandFailure("Language \"" + arguments[SINK_LANGUAGE] + "\" not valid. Language must be one of: java, javascript, python.");
      }

      if (sinkLanguageType == null) {
         return new CommandFailure("Language must be one of: java, javascript, python.");
      }

      final Source newSource = new Source();
      newSource.setId(arguments[SOURCE_ID]);
      newSource.setType(sinkLanguageType);
      newSource.setHref(arguments[SINK_REFERENCE]);

      getSources(cfgHandler).add(newSource);
      cfgHandler.write();

      return new CommandSuccess();
   }
}