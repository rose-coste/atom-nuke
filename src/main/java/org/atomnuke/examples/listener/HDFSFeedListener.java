package org.atomnuke.examples.listener;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.atomnuke.listener.AtomListener;
import org.atomnuke.listener.AtomListenerResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.atomnuke.atom.model.Entry;
import org.atomnuke.atom.model.Feed;
import org.atomnuke.atom.Writer;
import org.atomnuke.listener.AtomListenerException;
import org.atomnuke.task.context.TaskContext;
import org.atomnuke.task.lifecycle.DestructionException;
import org.atomnuke.task.lifecycle.InitializationException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

/**
 *
 * @author zinic
 */
public class HDFSFeedListener implements AtomListener {

   private final Configuration configuration;
   private final Path targetPath;
   private final String feedName;
   private final Writer writer;
   
   private SequenceFile.Writer fileWriter;
   private boolean writeHeader;
   private FileSystem hdfs;

   public HDFSFeedListener(String feedName, Writer writer) {
      this.feedName = feedName;
      this.writer = writer;

      targetPath = new Path("/data/atom/" + feedName);

      configuration = new Configuration();
      configuration.set("fs.default.name", "hdfs://namenode:9000");

      writeHeader = false;
   }

   @Override
   public void init(TaskContext tc) throws InitializationException {
      try {
         hdfs = FileSystem.get(configuration);

         writeHeader = !hdfs.exists(targetPath);
         fileWriter = SequenceFile.createWriter(hdfs, configuration, targetPath, Text.class, Text.class);
      } catch (IOException ioe) {
         throw new InitializationException(ioe);
      }
   }

   @Override
   public void destroy(TaskContext tc) throws DestructionException {
      try {
         fileWriter.close();
         hdfs.close();
      } catch (IOException ioe) {
         throw new DestructionException(ioe);
      }
   }

   @Override
   public AtomListenerResult entry(Entry entry) throws AtomListenerException {
      try {
         final ByteArrayOutputStream baos = new ByteArrayOutputStream();

         writer.write(baos, entry);
         append(new Text(entry.id().toString()), new Text(baos.toByteArray()));
      } catch (Exception ioe) {
         throw new AtomListenerException(ioe);
      }

      return AtomListenerResult.ok();
   }

   @Override
   public AtomListenerResult feedPage(Feed page) throws AtomListenerException {
      try {
         if (writeHeader) {
            writeFeedHeader(page);
         }

         final ByteArrayOutputStream baos = new ByteArrayOutputStream();

         for (Entry e : page.entries()) {
            writer.write(baos, page);
            append(new Text(e.id().toString()), new Text(baos.toByteArray()));
         }
      } catch (Exception ioe) {
         throw new AtomListenerException(ioe);
      }

      return AtomListenerResult.ok();
   }

   private void append(String key, String value) throws IOException {
      append(new Text(key), new Text(value));
   }

   private void append(Text key, Text value) throws IOException {
      fileWriter.append(key, value);

      final BufferedWriter lookaside = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("/home/zinic/atom.txt"), true)));
      lookaside.write(key.toString());
      lookaside.write("\n");
      lookaside.write(value.toString());
      lookaside.write("\n\n");

      lookaside.close();
   }

   private void writeVariable(String key, String value, StringBuilder builder) {
      builder.append("\"");
      builder.append(key);
      builder.append("\":\"");
      builder.append(value);
      builder.append("\"");
   }

   private void writeFeedHeader(Feed page) throws IOException {
      writeHeader = false;

      final StringBuilder header = new StringBuilder("{");
      writeVariable("id", page.id().toString(), header);
      header.append(",");
      writeVariable("title", page.title().toString(), header);
      header.append("}");

      append(feedName + "-metadata", header.toString());
   }
}
