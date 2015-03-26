package com.funkdefino.common.pool;

import com.funkdefino.common.util.UtilException;
import java.util.Iterator;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IObjectPool<T> {
  public void release(T tObj);
  public void remove (T tObj);
  public void remove (T tObj, Iterator ii);
  public T    checkout() throws UtilException;
  public void iterate(IOPCallback<T> cb);
  public void close();
  public int  size();
}
