package net.jps.nuke.config;

import net.jps.nuke.config.model.ServerConfiguration;

/**
 *
 * @author zinic
 */
public class ConfigurationHandler {

   private final ConfigurationManager managerReference;
   private final ServerConfiguration configuration;

   public ConfigurationHandler(ConfigurationManager managerReference, ServerConfiguration configuration) {
      this.managerReference = managerReference;
      this.configuration = configuration;
   }

   public void write() throws ConfigurationException {
      managerReference.write(configuration);
   }

   public ServerConfiguration getConfiguration() {
      return configuration;
   }
}
