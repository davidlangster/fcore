package com.funkdefino.common.util.event;
import  com.funkdefino.common.util.UtilException;

/**
 * <p/>
 * <code>$Id: $</code>
 * An event handler interface.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IEventHandler {
  public void post (Object source, Object event, Severity severity) throws UtilException;
  public void close();
}
