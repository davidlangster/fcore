package com.funkdefino.common.thread;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IWorkerThread<T> {
  public T     execute(IWorkUnit<T> workUnit);
  public void  close(long lWait);
  public void  close();
}
