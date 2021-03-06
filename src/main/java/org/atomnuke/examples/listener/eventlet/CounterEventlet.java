package org.atomnuke.examples.listener.eventlet;

import java.util.concurrent.atomic.AtomicLong;
import org.atomnuke.atom.model.Entry;
import org.atomnuke.listener.eps.eventlet.AtomEventlet;
import org.atomnuke.listener.eps.eventlet.AtomEventletException;
import org.atomnuke.task.context.TaskContext;
import org.atomnuke.task.lifecycle.DestructionException;
import org.atomnuke.task.lifecycle.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class CounterEventlet implements AtomEventlet {

   private static final Logger LOG = LoggerFactory.getLogger(CounterEventlet.class);
   
   protected final AtomicLong entryEvents;
   private boolean log;

   public CounterEventlet() {
      this(new AtomicLong(), true);
   }

   public CounterEventlet(AtomicLong entryEvents, boolean log) {
      this.entryEvents = entryEvents;
      this.log = log;
   }

   @Override
   public void init(TaskContext tc) throws InitializationException {
   }

   @Override
   public void destroy(TaskContext tc) throws DestructionException {
      if (log) {
         LOG.info("Processed " + entryEvents.toString() + " events.");
      }
   }

   @Override
   public void entry(Entry entry) throws AtomEventletException {
      final long events = entryEvents.incrementAndGet();
      
      if (log && events % 1000 == 0) {
         LOG.info("Processed " + events + " events.");
      }
   }
}
