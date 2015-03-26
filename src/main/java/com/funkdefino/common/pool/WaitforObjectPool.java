package com.funkdefino.common.pool;

import com.funkdefino.common.util.event.Severity;
import com.funkdefino.common.util.event.IEventManager;
import com.funkdefino.common.util.xml.XmlValidate;
import com.funkdefino.common.util.xml.XmlElement;
import com.funkdefino.common.util.UtilException;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class WaitforObjectPool<T> extends ObjectPool<T> {

  //** --------------------------------------------------------------- Constants

  private final static String s_sCheckOut    = "Object checked out after %sms delay";
  private final static String s_sInvalidWait = "Invalid wait parameter";
  private final static String s_sAttrbWait   = "wait";
  private final static long   SPIN_WAIT      = 25L;

  //** -------------------------------------------------------------------- Data

  private long m_lMaxWait;                            // Total maximum wait (ms)

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor.
   * @param nInitial the initial pool size.
   * @param nMaximum the maximum pool size.
   * @param lWait the wait period (in ms).
   * @param factory an object factory.
   * @param eventMgr an event manager implementation (or null).
   * @throws UtilException on error.
   */
  public WaitforObjectPool(int nInitial, int nMaximum, long lWait, IObjectFactory<T> factory,
                          IEventManager eventMgr) throws UtilException {
    super(nInitial, nMaximum, factory, eventMgr);
    if((m_lMaxWait = lWait) < 0 ) {
      throw new UtilException((s_sInvalidWait));
    }
  }

  /**
   * Ctor.
   * @param eConfig a configuration element.
   * @param eventMgr an event manager implementation (or null).
   * @throws UtilException on error.
   */
  public WaitforObjectPool(XmlElement eConfig, IEventManager eventMgr) throws UtilException {
    super(eConfig, eventMgr);
    String sWait = XmlValidate.getAttribute(eConfig, s_sAttrbWait);
    if((m_lMaxWait = Long.parseLong(sWait)) < 0 ) {
      throw new UtilException((s_sInvalidWait));
    }
  }

  //** -------------------------------------------------------------- Operations

  /**
   * This gets the next free object. NOTE - non-synchronized.
   * @return the object (or null if none available).
   * @throws UtilException on error.
   */
  public T checkout() throws UtilException
  {
    long lWait = 0L;
    Object mutex = new Object();
    T tObj;

    while(((tObj = super.checkout()) == null) && lWait < m_lMaxWait) {
      synchronized(mutex) {
        try  {
          mutex.wait(SPIN_WAIT);
          lWait += SPIN_WAIT;
        }
        catch(InterruptedException excp) {
        }
      }
    }

    if(tObj != null && lWait > 0) {
       log(String.format(s_sCheckOut, lWait), Severity.INFO);
    }
    
    return tObj;

  } // checkout()

} // class WaitforObjectPool 
