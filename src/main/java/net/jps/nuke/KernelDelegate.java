package net.jps.nuke;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.jps.nuke.util.remote.CancellationRemote;
import net.jps.nuke.task.ManagedTask;
import net.jps.nuke.task.threading.ExecutionManager;
import net.jps.nuke.util.TimeValue;
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
   private final List<ManagedTask> crawlerTasks;

   public KernelDelegate(CancellationRemote crawlerCancellationRemote, ExecutionManager executionManager) {
      this.crawlerCancellationRemote = crawlerCancellationRemote;
      this.executionManager = executionManager;

      this.crawlerTasks = new LinkedList<ManagedTask>();
   }

   public synchronized void addTask(ManagedTask state) {
      crawlerTasks.add(state);
   }

   private synchronized void removeTasks(List<ManagedTask> tasks) {
      crawlerTasks.removeAll(tasks);
   }

   private synchronized List<ManagedTask> copyTasks() {
      return new LinkedList<ManagedTask>(crawlerTasks);
   }
   
   @Override
   public void run() {
      // Run until canceled
      while (!crawlerCancellationRemote.canceled()) {
         final List<ManagedTask> tasks = copyTasks();
         final TimeValue now = TimeValue.now();
         TimeValue closestPollTime = null;

         for (Iterator<ManagedTask> itr = tasks.iterator(); itr.hasNext();) {
            final ManagedTask managedTask = itr.next();

            if (!managedTask.canceled()) {
               final TimeValue nextPollTime = managedTask.nextPollTime();

               // Sould this task be scheduled? If so, is the task already in the execution queue?
               if (now.isGreaterThan(nextPollTime)) {
                  
                  // If we just scheduled this task to be run then it shouldn't
                  // be considered for the closest polling time
                  if (!executionManager.submitted(managedTask)) {
                     executionManager.submit(managedTask);
                  }
               } else if (closestPollTime == null || closestPollTime.isGreaterThan(nextPollTime)) {
                  // If the closest polling time is null or later than this task's
                  // next polling time, it becomes the next time the kernel wakes

                  closestPollTime = nextPollTime;
               }

               // Remove this task from the list copy so it doesn't get removed 
               // from active duty in the cleanup phase of this run method
               itr.remove();
            }
         }
         
         // Remove canceled tasks
         removeTasks(tasks);

         // Sleep till the next polling time or for a couple of milliseconds
         final TimeValue sleepTime = closestPollTime != null ? now.subtract(closestPollTime) : ZERO_NANOSECONDS;

         if (sleepTime.value() > 0) {
            try {
               // Sleeeeep...
               sleepTime.sleep();
            } catch (InterruptedException ie) {
               // Suppress this - someone may have added a new task
            }
         }
      }

      executionManager.destroy();
   }
}