package com.funkdefino.common.util.event;

/**
 * <p/>
 * <code>$Id: $</code>
 * An event filter interface.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IEventFilter {
  public boolean accept(String sEvent, Object event, Severity severity, Object flt);
}
