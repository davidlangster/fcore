package com.funkdefino.common.pool;

import com.funkdefino.common.util.UtilException;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IObjectFactory<T> {
  public T    create() throws UtilException;
  public void dispose(T tObj);
  public void close();
}
