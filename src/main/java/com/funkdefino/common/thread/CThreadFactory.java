package com.funkdefino.common.thread;

import java.util.concurrent.ThreadFactory;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class CThreadFactory implements ThreadFactory {

  //** --------------------------------------------------------------- Constants

  private final static String s_sGroupName  = "CThreadPool";
  private final static String s_sThreadName = "CThreadPool";

  //** --------------------------------------------------- Static initialisation

  private static int  s_nThreadID = 0;                     // Thread identifier
  private static int  s_nGroupID  = 0;                     // Group  identifier

  //** -------------------------------------------------------------------- Data

  private ThreadGroup m_threadGroup;                       // Thread group
  private String      m_sPrefix;                           // Thread prefix
  private int         m_nThreadID;                         // Thread identifier
  private int         m_nGroupID;                          // Group  identifier

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor.
   * @param sPrefix a thread name prefix.
   */
  public CThreadFactory(String sPrefix) {
    m_threadGroup = new ThreadGroup(s_sGroupName + s_nGroupID);
    m_nGroupID = s_nGroupID++;
    m_sPrefix = sPrefix;
  }

  //** ------------------------------------ Operations (ThreadFactory interface)

  /**
   * This creates a new thread.
   * @param runnable a runnable.
   * @return the thread.
   */
  @Override
  public Thread newThread(Runnable runnable) {
    Thread th = new Thread(m_threadGroup, runnable, fmtThreadName());
    th.setPriority(Thread.NORM_PRIORITY);
    return th;
  }

  //** -------------------------------------------------------------- Operations

  /**
   * Called on shutdown.
   */
  public void shutdown()
  {
  }

  //** ---------------------------------------------------------- Implementation

  /**
   * This formats the thread name.
   * @return the name.
   */
  private String fmtThreadName() {
    StringBuffer sb = new StringBuffer(s_sThreadName);
    sb.append(": '").append(Integer.toString(s_nThreadID++)).append("\'");
    if(!m_sPrefix.equals("")) {
       sb.append(" ").append(m_sPrefix).append("(");
       sb.append(Integer.toString(m_nGroupID));
       sb.append("-").append(Integer.toString(m_nThreadID++));
       sb.append(')');
    }

    return sb.toString();

  } // fmtThreadName()

} // class XThreadFactory
