package net.jps.nuke;

import net.jps.nuke.threading.ExecutionManagerImpl;
import net.jps.nuke.util.remote.CancellationRemote;
import net.jps.nuke.util.remote.AtomicCancellationRemote;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.jps.nuke.task.submission.TaskManager;
import net.jps.nuke.task.submission.TaskManagerImpl;
import net.jps.nuke.task.submission.Tasker;
import net.jps.nuke.threading.ExecutionManager;

/**
 *
 * @author zinic
 */
public class NukeKernel implements Nuke {

   private static final ThreadFactory DEFAULT_THREAD_FACTORY = new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
         return new Thread(r, "nuke-worker-" + TID.incrementAndGet());
      }
   };
   private static final int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();
   private static final AtomicLong TID = new AtomicLong(0);
   private final CancellationRemote kernelCancellationRemote;
   private final ExecutionManager executionManager;
   private final TaskManager taskManager;
   private final KernelDelegate logic;
   private final Thread controlThread;

   /**
    * Initializes a new Nuke kernel.
    *
    * This kernel will retain a core execution pool size equal to the number of
    * the processors available on the system. The max size for the execution
    * pool will be equal to the number of processors available to the system
    * multiplied by four.
    */
   public NukeKernel() {
      // Gimme all the processors :E
      this(NUM_PROCESSORS, NUM_PROCESSORS * 4);
   }

   /**
    * Initializes a new Nuke kernel.
    *
    * @param corePoolSize sets the number of threads that the execution pool
    * will retain during normal operation.
    * @param maxPoolsize sets the maximum number of threads that the execution
    * pool may spawn.
    */
   public NukeKernel(int corePoolSize, int maxPoolsize) {
      this(new ThreadPoolExecutor(corePoolSize, maxPoolsize, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), DEFAULT_THREAD_FACTORY, new NukeRejectionHandler()), maxPoolsize);
   }

   /**
    * Initializes a new Nuke kernel.
    *
    * @param executorService sets the execution service that the kernel will
    * utilize for scheduling.
    */
   public NukeKernel(ExecutorService executorService, int maxPoolsize) {
      kernelCancellationRemote = new AtomicCancellationRemote();      
      executionManager = new ExecutionManagerImpl(maxPoolsize, executorService);
      taskManager = new TaskManagerImpl(executionManager);
      logic = new KernelDelegate(kernelCancellationRemote, taskManager, executionManager);
      controlThread = new Thread(logic, "nuke-kernel-" + TID.incrementAndGet());
   }

   @Override
   public Tasker submitter() {
      return taskManager;
   }

   @Override
   public void start() {
      if (controlThread.getState() != Thread.State.NEW) {
         throw new IllegalStateException("Crawler already started or destroyed.");
      }

      controlThread.start();
   }

   @Override
   public void destroy() {
      kernelCancellationRemote.cancel();

      try {
         controlThread.join();
      } catch (InterruptedException ie) {
         controlThread.interrupt();
      }
      
      executionManager.destroy();
      taskManager.destroy();
   }
}
