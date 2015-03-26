package com.funkdefino.common.thread;

import com.funkdefino.common.util.UtilException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public class WorkerThread<T> implements IWorkerThread<T> {

  //** --------------------------------------------------------------- Constants

  private final static String STARTED           = "Started (%s initial thread(s))";
  private final static String WAIT_FOREVER_STOP = "Waiting for %s thread(s) to stop running ...";
  private final static String WAIT_STOP         = "Waiting %sms for %s thread(s) to stop running ...";
  private final static String NO_WAIT           = "Operating in 'no-wait' mode";
  private final static String NO_IDLE_THREADS   = "No idle threads";
  private final static String CLOSED            = "%s thread(s) still running";

  //** -------------------------------------------------------------------- Data

  private ThreadPoolExecutor executor;
  private static Logger logger = LoggerFactory.getLogger(WorkerThread.class);
  private boolean noWait;
  private long timeout;

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor.
   * @param initial the core pool size.
   * @param maximum the maximum pool size.
   * @param keepAlive when the number of threads is greater than the core, this
   *                  is the maximum time that excess idle threads will wait
   *                  for new tasks before terminating (in secs).
   * @param prefix a thread naimg prefix.
   * @throws UtilException thrown on error.
   */
  public WorkerThread(int initial, int maximum, long timeout, long keepAlive,
                      String prefix) throws UtilException {

    executor = initialise(initial, maximum, timeout, keepAlive, prefix);
    executor.allowCoreThreadTimeOut(false);
    executor.prestartCoreThread();
    logger.debug(String.format(STARTED, initial));
  }

  //** ------------------------------------ Operations (IWorkerThread interface)

  /**
   * This executes a unit of work.
   * @param workUnit the unit of work.
   * @return  the result.
   */
  public T execute(IWorkUnit<T> workUnit) {
    return _execute(workUnit).getResult();
  }

  /**
   * This closes the worker thread.
   */
  public void close() {
    if(executor != null) {
       logger.debug(String.format(WAIT_FOREVER_STOP,  executor.getPoolSize()));
       shutdown(executor, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
  }

  /**
   * This closes the worker thread.
   * @param lWait a wait period (in milliseconds).
   */
  public void close(long lWait) {
    if(executor != null) {
      logger.debug(String.format(WAIT_STOP, lWait, executor.getPoolSize()));
      shutdown(executor, lWait, TimeUnit.MILLISECONDS);
    }
  }

  //** ---------------------------------------------------- Operations (Utility)

  /**
   * This executes a unit of work.
   * @param workUnit the unit of work.
   * @return a result value object.
   */
  protected final ExecuteInfo<T> _execute(IWorkUnit<T> workUnit) {

   _FutureTask<T> ft = new _FutureTask<T>(new _Callable<T>(workUnit));
    ExecuteInfo<T> executeInfo;

    try {
      // Execute the task
      executor.execute(ft);
      executeInfo = ft.get(timeout, TimeUnit.MILLISECONDS);
    }
    catch(TimeoutException excp) {
      executeInfo = new ExecuteInfo<T>(null, false);
      if(!noWait) {
        ft.setExpired();
      }
    }
    catch(RejectedExecutionException excp) {
      logger.debug(NO_IDLE_THREADS);
      T ret = workUnit.onThreadPoolExhausted();
      executeInfo = new ExecuteInfo<T>(ret, true);
    }
    catch(Exception excp) {
      executeInfo = new ExecuteInfo<T>(null, false);
      logger.debug(excp.toString());
    }

    return executeInfo;

  } // _execute()

  //** ---------------------------------------------------------- Implementation

  /**
   * This performs startup initialisation.
   * @param initial the core pool size.
   * @param maximum the maximum pool size.
   * @param keepAlive when the number of threads is greater than the core, this
   *                  is the maximum time that excess idle threads will wait
   *                  for new tasks before terminating (in secs).
   * @param prefix a thread naming prefix.
   * @return the executor service.
   * @throws UtilException thrown on error.
   */
  private ThreadPoolExecutor initialise(int initial, int maximum, long timeout,
                                        long keepAlive, String prefix)
                                        throws UtilException {

    // Zero wait-time? Set to a nominal value to avoid
    // unecessary on-demand thread creation.
    if((this.timeout = timeout * 1000) == 0) {
       logger.debug(NO_WAIT);
       this.timeout = 100L;
       noWait  = true;
    }

    // Non-core thread keep alive
    TimeUnit tu;
    if(keepAlive != -1) tu = TimeUnit.SECONDS;
    else { // Infinite keep alive
      keepAlive = Long.MAX_VALUE;
      tu = TimeUnit.NANOSECONDS;
    }

    return new ThreadPoolExecutor(initial, maximum, keepAlive, tu,
                                  new SynchronousQueue<Runnable>(),
                                  new CThreadFactory(prefix));

  } // initialise()

  /**
   * This shuts down the executor service.
   * @param executor the excutor.
   * @param lTimeout a timeout value.
   * @param tu the unit.
   */
  private void shutdown(ThreadPoolExecutor executor, long lTimeout, TimeUnit tu) {

    executor.shutdown();
    try  {executor.awaitTermination(lTimeout, tu);}
    catch(InterruptedException excp) {
      excp.printStackTrace();
    }

    logger.debug(String.format(CLOSED, executor.getPoolSize()));
    ((CThreadFactory)executor.getThreadFactory()).shutdown();

  } // shutdown()

  //** ------------------------------------------------------------------ Nested

  private static class _FutureTask<T> extends FutureTask<ExecuteInfo<T>>  {

    private boolean expired = false;
    private _Callable<T> callable;
    private long timestamp;
    public _FutureTask(_Callable<T> callable) {
      super(callable);
      this.timestamp = System.currentTimeMillis();
      this.callable = callable;
    }

    public void setExpired() {expired = true;}
    protected void done() {
      if(expired) {
         try  {callable.getWorkUnit().onExpiry(get().getResult(), timestamp);}
         catch(Exception excp) {
           excp.printStackTrace();
         }
      }
    }
  }

  //** ------------------------------------------------------------------ Nested

  private static class _Callable<T> implements Callable<ExecuteInfo<T>> {
    private IWorkUnit<T> workUnit;
    public _Callable(IWorkUnit<T> workUnit) {this.workUnit = workUnit;}
    public  IWorkUnit<T> getWorkUnit() {return workUnit;}
    public  ExecuteInfo<T> call() {
      T ret = null;
      try  {ret = workUnit.onExecute();}
      catch(Throwable th) {
        th.printStackTrace();
      }
      return new ExecuteInfo<T>(ret, false);
    }
  }

  //** ------------------------------------------------------------------ Nested

  protected final static class ExecuteInfo<T>
  {
    private T result;
    private boolean exhausted;
    public  ExecuteInfo(T result, boolean exhausted) {
            this.result = result; this.exhausted = exhausted;
    }
    public  T       getResult  () {return result;   }
    public  boolean isExhausted() {return exhausted;}
  }

} // class WorkerThread
