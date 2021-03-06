package org.atomnuke;

import java.util.concurrent.TimeUnit;
import org.atomnuke.task.manager.TaskManager;
import org.atomnuke.task.threading.ExecutionManager;
import org.atomnuke.util.TimeValue;
import org.atomnuke.util.remote.CancellationRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class KernelDelegate implements Runnable {

   private static final Logger LOG = LoggerFactory.getLogger(KernelDelegate.class);
   private static final TimeValue ZERO_NANOSECONDS = new TimeValue(0, TimeUnit.NANOSECONDS);
   private final CancellationRemote crawlerCancellationRemote;
   private final ExecutionManager executionManager;
   private final TaskManager taskManager;
   private int drainMagnitude;

   public KernelDelegate(CancellationRemote crawlerCancellationRemote, ExecutionManager executionManager, TaskManager taskManager) {
      this.crawlerCancellationRemote = crawlerCancellationRemote;
      this.executionManager = executionManager;
      this.taskManager = taskManager;

      drainMagnitude = 1;
   }

   @Override
   public void run() {
      // Run until canceled
      while (!crawlerCancellationRemote.canceled()) {
         final TimeValue now = TimeValue.now();
         TimeValue sleepTime = null;

         // Sleep till the next polling time or for a couple of milliseconds
         if (executionManager.draining()) {
            drainMagnitude += drainMagnitude == 1000 ? 0 : 1;

            sleepTime = new TimeValue(2 * drainMagnitude, TimeUnit.MILLISECONDS);

            LOG.debug("Execution queue too large to continue polling. Yielding " + sleepTime.value() + " millisecond to allow queue to drain.");
         } else {
            drainMagnitude -= drainMagnitude == 0 ? 1 : 0;

            final TimeValue closestPollTime = taskManager.scheduleTasks();
            final TimeValue nextSleep = closestPollTime != null ? closestPollTime.subtract(now) : ZERO_NANOSECONDS;

            if (sleepTime == null || nextSleep.isLessThan(sleepTime)) {
               sleepTime = nextSleep;
            }
         }

         try {
            // Sleep if there's nothing to poll at the moment
            if (sleepTime.value() > 0) {
               sleepTime.sleep();
            } else {
               // Yield if we're not going to sleep
               Thread.yield();
            }
         } catch (InterruptedException ie) {
            LOG.warn("KernelDelegate interrupted. Shutting down right now.", ie);
            break;
         }
      }
   }
}
