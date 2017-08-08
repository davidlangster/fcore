package com.funkdefino.common.unittest;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class CTCCounter implements ICTCCallback {

  //** -------------------------------------------------------------------- Data

  private volatile int m_nCount = 0;

  //** ------------------------------------------------------------ Construction

  /**
   * Default ctor.
   */
  public CTCCounter()
  {
  }

  //** ------------------------------------- Operations (ICTCCallbcak interface)

  public void onRun()    {++m_nCount;     }
  public int  getCount() {return m_nCount;}

} // class CTCCounter
