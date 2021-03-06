package org.atomnuke.task.manager;

import java.util.UUID;
import org.atomnuke.listener.RegisteredListener;
import org.atomnuke.listener.driver.AtomListenerDriver;
import org.atomnuke.listener.driver.RegisteredListenerDriver;
import org.atomnuke.listener.manager.ListenerManager;
import org.atomnuke.source.AtomSource;
import org.atomnuke.source.AtomSourceResult;
import org.atomnuke.task.Task;
import org.atomnuke.task.context.TaskContext;
import org.atomnuke.task.lifecycle.DestructionException;
import org.atomnuke.task.threading.ExecutionManager;
import org.atomnuke.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class ManagedTaskImpl implements ManagedTask {

   private static final Logger LOG = LoggerFactory.getLogger(ManagedTaskImpl.class);
   
   private final ExecutionManager executorService;
   private final ListenerManager listenerManager;
   private final AtomSource atomSource;
   private final Task task;
   private final UUID id;
   private TimeValue timestamp;

   public ManagedTaskImpl(Task task, ListenerManager listenerManager, TimeValue interval, ExecutionManager executorService, AtomSource atomSource) {
      this.task = task;

      this.listenerManager = listenerManager;
      this.executorService = executorService;
      this.atomSource = atomSource;

      timestamp = TimeValue.now();
      id = UUID.randomUUID();
   }

   @Override
   public boolean canceled() {
      return task.canceled();
   }

   @Override
   public void cancel() {
      task.cancel();
   }

   @Override
   public boolean isReentrant() {
      return listenerManager.isReentrant();
   }

   @Override
   public UUID id() {
      return id;
   }

   @Override
   public void scheduled() {
      timestamp = TimeValue.now();
   }

   @Override
   public TimeValue nextPollTime() {
      return timestamp.add(task.interval());
   }

   @Override
   public void destroy(TaskContext taskContext) {
      LOG.debug("Destroying task: " + task);
      
      for (RegisteredListener registeredListener : listenerManager.listeners()) {
         try {
            registeredListener.listener().destroy(taskContext);
         } catch (DestructionException sde) {
            LOG.error(sde.getMessage(), sde);
         }
      }
   }

   @Override
   public void run() {
      // Only poll if we have listeners
      if (listenerManager.hasListeners()) {
         try {
            final AtomSourceResult pollResult = atomSource.poll();

            for (RegisteredListener listener : listenerManager.listeners()) {
               RegisteredListenerDriver listenerDriver;

               if (pollResult.isFeedPage()) {
                  listenerDriver = new AtomListenerDriver(listener, pollResult.feed());
               } else {
                  listenerDriver = new AtomListenerDriver(listener, pollResult.entry());
               }

               executorService.queue(listenerDriver);
            }
         } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
         }
      }
   }
}
