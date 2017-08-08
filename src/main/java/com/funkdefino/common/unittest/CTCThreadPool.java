package com.funkdefino.common.unittest;
import  java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
final class CTCThreadPool {

  //** --------------------------------------------------------------- Constants

  private final static String s_sThreadGroup = "CTestCase";

  //** -------------------------------------------------------------------- Data

  private ThreadGroup    m_group      = null;  // The thread group
  private List<Runnable> m_lsRunnable = null;  // A list of current runnables
  private Object         m_mutex      = null;  // A synchronisation object
  private ICTCCallback   m_callback   = null;  // A runnable callback

  //** ------------------------------------------------------------ Construction

  /**
   * Constructor.
   * @param mutex a synchronisation object.
   * @param callback a runnable callback (or null).
   */
  public CTCThreadPool(Object mutex, ICTCCallback callback) {
    m_group = new ThreadGroup(s_sThreadGroup);
    m_lsRunnable = Collections.synchronizedList(new ArrayList<Runnable>());
    m_callback = callback;
    m_mutex = mutex;
  }

  //** -------------------------------------------------------------- Operations

  /**
   * This method creates a thread for use in repeat testing.
   * @param  obj the parent object.
   * @param  sMethod the name of the method for repeat testing.
   * @param  nRepeats the number of test repeats.
   * @return the thread.
   */
  public Thread createThread(Object obj, String sMethod, int nRepeats)
  {
    // Create a runnable & add it to the runnable list - THEN create the
    // thread. The parent object cooperatively acquires and holds a lock
    // on the  mutex until all threads have been created and started, at
    // which point it enters a  WAIT state, and releases the mutex lock.
    // Since runnables must acquire the  mutex lock in turn to  proceed,
    // this ensures that the runnables cant start until ALL threads have
    // been been started, thus ensuring  that the runnable list is fully
    // populated.

    Runnable runnable;
    m_lsRunnable.add((runnable=new CTCRunnable(obj,sMethod, nRepeats,Thread.currentThread(),m_callback)));
    return new Thread(m_group, new RunnableDecorator(runnable,m_lsRunnable,m_mutex));

  } // createThread()

  //** ------------------------------------------------------------------ Nested

  private final static class RunnableDecorator implements Runnable
  {
    private Runnable     m_runnable;
    private List         m_lsRunnable;
    private final Object m_mutex;

    public  RunnableDecorator(Runnable runnable, List lsRunnable, Object mutex) {
      m_lsRunnable = lsRunnable;
      m_runnable = runnable;
      m_mutex = mutex;
    }
    //** -----------------------------------------------------------------------
    public void run() {
      synchronized(m_mutex){}
      try {m_runnable.run();}
      finally {
        m_lsRunnable.remove(m_runnable);
        if(m_lsRunnable.size( ) == 0) {
           synchronized(m_mutex){
                m_mutex.notify();
           }
        }
      }
    }

  } // class RunnableDecorator

} // class CTCThreadPool
