package com.funkdefino.common.util.event;

/**
 * <p/>
 * <code>$Id: $</code>
 * An event manager interface.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IEventManager {
  public String   getID();
  public void     post(String sEvent, Object source, Object event, Severity severity, Object flt);
  public void     post(String sEvent, Object source, Object event, Severity severity);
  public void     post(String sEvent, Object source, Object event);
  public void     post(Object source, Object event,  Severity severity, Object flt);
  public void     post(Object source, Object event,  Severity severity);
  public void     post(Object source, Object event);
  public void     post(Object event);
  public Severity getSeverity(String sEvent);
  public void     close();
}
