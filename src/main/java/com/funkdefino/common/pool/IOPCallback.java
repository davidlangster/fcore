package com.funkdefino.common.pool;

import java.util.Iterator;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IOPCallback<T> {
  public boolean action(T tObj, boolean bInUse, Iterator ii, boolean bLast);
}
