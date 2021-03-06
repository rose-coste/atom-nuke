package org.atomnuke.examples.listener.eventlet;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;
import org.atomnuke.atom.model.Entry;
import org.atomnuke.listener.eps.eventlet.AtomEventletException;
import org.atomnuke.task.context.TaskContext;
import org.atomnuke.task.lifecycle.DestructionException;
import org.atomnuke.task.lifecycle.InitializationException;

/**
 *
 * @author zinic
 */
public class PrintStreamEventlet extends CounterEventlet {

   private final PrintStream out;
   private final String msg;
   private final long creationTime;

   public PrintStreamEventlet(PrintStream out, String msg, AtomicLong events) {
      super(events, true);

      this.out = out;
      this.msg = msg;
      this.creationTime = System.currentTimeMillis();
   }

   @Override
   public void init(TaskContext tc) throws InitializationException {
      out.println("PrintStreamOutputListener(" + toString() + ") initalized.");
   }

   @Override
   public void destroy(TaskContext tc) throws DestructionException {
      out.println("PrintStreamOutputListener(" + toString() + ") destroyed.");
   }

   private void newEvent() {
      final long eventsCaught = entryEvents.incrementAndGet();
      final long nowInMillis = System.currentTimeMillis();

      if (eventsCaught % 10000 == 0) {
         out.println((nowInMillis - creationTime) + "ms elapsed. Events received: " + eventsCaught + " - Events per 10ms: " + (eventsCaught / ((nowInMillis - creationTime) / 10)) + " - (" + msg + ")");
      }
   }

   @Override
   public void entry(Entry entry) throws AtomEventletException {
      newEvent();
   }
}
