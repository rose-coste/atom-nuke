package org.atomnuke.atom.stax;

import org.atomnuke.atom.WriterConfiguration;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author zinic
 */
public class WriterContext {

   private final WriterConfiguration configuration;
   private final XMLStreamWriter writer;

   public WriterContext(XMLStreamWriter writer, WriterConfiguration configuration) {
      this.configuration = configuration;
      this.writer = writer;
   }

   public WriterConfiguration getConfiguration() {
      return configuration;
   }

   public XMLStreamWriter getWriter() {
      return writer;
   }
}
