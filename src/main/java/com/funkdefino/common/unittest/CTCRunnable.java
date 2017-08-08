package com.funkdefino.common.unittest;

import com.funkdefino.common.util.reflect.Loader;
import java.lang.reflect.InvocationTargetException;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
final class CTCRunnable implements Runnable {

  //** -------------------------------------------------------------------- Data

  private Object       m_obj      = null;  // The parent object
  private String       m_sMethod  = "";    // The name of the method for repeat testing
  private int          m_nRepeats = 0;     // The number of test repeats
  private Thread       m_thread   = null;  // A waiting thread (interrupted on error)
  private ICTCCallback m_callback = null;  // A run() callback

  //** ------------------------------------------------------------ Construction

  /**
   * Constructor.
   * @param obj the parent object.
   * @param sMethod the name of the method for repeat testing.
   * @param nRepeats the number of test repeats.
   * @param thread a waiting thread (interrupted on error).
   * @param callback a run() callback (or null).
   */

  public CTCRunnable(Object obj, String sMethod, int nRepeats,
                     Thread thread, ICTCCallback callback)
  {
    m_obj      = obj;
    m_sMethod  = sMethod;
    m_nRepeats = nRepeats;
    m_thread   = thread;
    m_callback = callback;
  }

  //** ----------------------------------------- Operations (Runnable interface)

  /**
   * This performs repeat test runs; the wating thread is interrupted on
   * exception.
   */

  public void run()
  {
    Throwable th = null;
    try {
      for(int i = 0; i < m_nRepeats; i++) {
        Loader.invokeMethod(m_obj, m_sMethod);
        if(m_callback != null){
           m_callback.onRun( );
        }
      }
    }
    catch(InvocationTargetException excp)  {th = excp.getTargetException();}
    catch(Exception excp) {th = excp;}
    finally {
      // Interrupt the waiting thread on exception
      if(th != null) {
         th.printStackTrace();
         m_thread.interrupt();
      }
    }

  } // run()

} // class CTCRunnable
