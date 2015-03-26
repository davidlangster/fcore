package com.funkdefino.common.thread;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IWorkUnit<T> {
  public T    onExecute();
  public T    onThreadPoolExhausted();
  public void onExpiry(T tObj, long lTimestamp);
}
