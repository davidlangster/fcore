
package com.funkdefino.common.util.event;
import com.funkdefino.common.util.xml.*;
import com.funkdefino.common.util.*;
import com.funkdefino.common.io.IOUtil;

import org.springframework.core.io.Resource;
import java.io.File;
import java.util.*;


/**
 * <p/>
 * <code>$Id: $</code>
 * An event manager implementation.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class EventManager implements IEventManager {

  //** --------------------------------------------------------------- Constants

  private final static String s_sElmntTargets = "Targets";
  private final static String s_sElmntEvents  = "Events";
  private final static String s_sDefault      = "DEFAULT";

  //** -------------------------------------------------------------------- Data

  private String m_sID             = "";   // An identifier
  private EventMappings m_mappings = null; // Event => target identifier mappings
  private Map<String, EventTarget> m_mpEventTargets = new HashMap<String, EventTarget>(); // EventTargets keyed on identifier
  private Map<String, XmlElement > m_mpTargets      = new HashMap<String, XmlElement >(); // <Target> elements keyed on identifier
  private XmlElement m_eEvents     = null;
  private boolean m_bClosed        = false;

  //** ------------------------------------------------------------ Construction

  /**
   * Default ctor
   */
  public EventManager()
  {
  }

  /**
   * Used from Spring in conjunction with default ctor.
   * @param resource a spring resource.
   * @throws Exception on error.
   */
  public void setResource(Resource resource) throws Exception {
    try  {refresh(IOUtil.loadResource(resource).getRootElement());}
    catch(Exception excp){
      close(); throw excp;
    }
  }
  
  /**
   * Constructor.
   * @param sFilename a configuration filename.
   * @throws UtilException on error.
   */
  public EventManager(String sFilename) throws UtilException {
    this(getConfig(sFilename));
  }

  /**
   * Constructor.
   * @param eConfig a configuration element.
   * @throws UtilException on error.
   */
  public EventManager(XmlElement eConfig) throws UtilException {
    try  {refresh(eConfig);}
    catch(UtilException excp){
      close(); throw excp;
    }
  }

  //** -------------------------------------------------------------- Operations

  /**
   * This performs initialisation and refresh.
   * @param eConfig a configuration element.
   * @return true (always).
   * @throws UtilException on error.
   */
  public boolean refresh(XmlElement eConfig) throws UtilException
  {

    // Validate
    m_sID = XmlValidate.getAttribute(eConfig, ICConstants.ATTR_ID, "");
    XmlElement eTargets = XmlValidate.getElement(eConfig, s_sElmntTargets);
    XmlElement eEvents  = XmlValidate.getElement(eConfig, s_sElmntEvents);

    // (Re)load the event targets
    boolean bRefresh = refreshTargets(m_mpEventTargets, eTargets, m_mpTargets);

    // (Re)load the event => target mappings
    if(m_eEvents == null || m_eEvents.hashCode() != eEvents.hashCode() || bRefresh) {
       m_mappings = new EventMappings((m_eEvents = eEvents), m_mpEventTargets);
    }

    return true;

  } // refresh()

  /**
   * This restores the event manager to a previous known good state.
   * @param rhs an event manager implementation.
   */
  public void restore(Object rhs) {
    m_sID            = ((EventManager)rhs).m_sID;
    m_mappings       = ((EventManager)rhs).m_mappings;
    m_mpEventTargets = ((EventManager)rhs).m_mpEventTargets;
    m_mpTargets      = ((EventManager)rhs).m_mpTargets;
    m_eEvents        = ((EventManager)rhs).m_eEvents;
    m_bClosed        = ((EventManager)rhs).m_bClosed;
  }

  //** ------------------------------------ Operations (IEventManager interface)

  public String getID() { return m_sID; }

  /**
   * This posts a 'default' event with INFO severity and no source.
   * @param event the event.
   */
  public void post(Object event) {
    post(null, event);
  }

  /**
   * This method posts a 'default' event with INFO severity.
   * @param source the source object.
   * @param event the event itself.
   */
  public void post(Object source, Object event) {
    post(source, event, Severity.INFO);
  }

  /**
   * This method posts a 'default' event.
   * @param source the source object.
   * @param event the event itself.
   * @param severity a severity.
   */
  public void post(Object source, Object event, Severity severity) {
    post(source, event, severity, null);
  }

  /**
   * This method posts a 'default' event.
   * @param source the source object.
   * @param event the event itself.
   * @param severity a severity.
   * @param flt a filter argument (or null).
   */
  public void post(Object source, Object event, Severity severity, Object flt) {
    post(s_sDefault, source, event, severity, flt);
  }

  /**
   * This method posts an event with INFO severity.
   * @param sEvent the event identifier.
   * @param source the source object.
   * @param event the event itself.
   */
  public void post(String sEvent, Object source, Object event) {
    post(sEvent, source, event, Severity.INFO);
  }

  /**
   * This method posts an event.
   * @param sEvent the event identifier.
   * @param source the source object.
   * @param event the event itself.
   * @param severity a severity.
   */
  public void post(String sEvent, Object source, Object event, Severity severity) {
    post(sEvent, source, event, severity, null);
  }

  /**
   * This method posts an event.
   * @param sEvent the event identifier.
   * @param source the source object.
   * @param event the event itself.
   * @param severity a severity.
   * @param flt a filter argument (or null).
   */
  public void post(String sEvent, Object source, Object event,
                   Severity severity, Object flt) {
    if(m_bClosed) return;
    List lsTargets = m_mappings.getTargets(sEvent);
    Iterator ii = lsTargets.iterator();
    while(ii.hasNext()) {
      String sTarget = (String)ii.next();
      EventTarget   target  = m_mpEventTargets.get(sTarget);
      IEventHandler handler = target.getHandler();
      IEventConverter converter = target.getConverter();
      IEventFilter filter  = target.getFilter();

      if(handler != null) {
         if(severity.compareTo(target.getSeverity()) >= 0) {
            // Filter the event object and apply a converter as required
            if(filter == null || filter != null && filter.accept(sEvent,event,severity,flt)) {
               if(converter != null) event = converter.convert(event);
               try  {handler.post(source,event,severity);}
               catch(Exception excp) {
                 excp.printStackTrace();
               }
            }
         }
      }
    }
  }

  /**
   * This method returns the lowest severity configured in the target(s)
   * associated with the incoming event identifier.
   * @param  sEvent the event identifier.
   * @return the severity.
   */
  public Severity getSeverity(String sEvent) {

    Severity severity = null;
    List lsTargets = m_mappings.getTargets(sEvent);
    if(lsTargets  != null) {
       Iterator ii = lsTargets.iterator();
       while(ii.hasNext()) {
         String sTarget = (String)ii.next();
         EventTarget target = m_mpEventTargets.get(sTarget);
         if(severity == null) severity = target.getSeverity();
         else {
           if(target.getSeverity().compareTo(severity) < 0 ) {
              severity = target.getSeverity( );
           }
         }
       }
    }

    return severity != null ? severity : Severity.INFO;

  } // getSeverity()

  /**
   * This releases all resources.
   */
  public void close() {
    m_bClosed = true;
    if(m_mpEventTargets != null) {
       Iterator ii = m_mpEventTargets.values().iterator();
       while(ii.hasNext())
         ((EventTarget)ii.next()).close();
       m_mpEventTargets.clear();
    }
  }

  //** ---------------------------------------------------------- Implementation

  /**
   * This refreshes event targets.
   * @param  mpET a map of all current EventTarget objects keyed on identifier.
   * @param  mpTargets a map of all current <Target> elements keyed on identifier.
   * @param  eTargets target configuration elements.
   * @return true if refreshed; otherwise false.
   * @throws UtilException on error.
   */
  private boolean refreshTargets(Map<String, EventTarget> mpET, XmlElement eTargets,
                                 Map<String, XmlElement > mpTargets) throws UtilException
  {
    Map<String, XmlElement> map = new HashMap<String, XmlElement>();
    Iterator ii = eTargets.getChildren().iterator();
    boolean bRefresh = false;
    EventTarget target;
    String sID;

    // Check for new and updated entries
    while(ii.hasNext())  {
      XmlElement eTarget = (XmlElement)ii.next();
      map.put((sID = EventTarget.getID(eTarget)), eTarget);
      if(!mpTargets.containsKey(sID)) {
         target = new EventTarget(eTarget);
         mpET.put(target.getID(),target);
         bRefresh = true;
      }
      else {
        if((mpTargets.get(sID)).hashCode() != eTarget.hashCode()) {
          target =  mpET.get(sID);
          target.refresh(eTarget);
          bRefresh = true;
        }
      }
    }

    // Check for removed target entries
    ii = mpTargets.keySet().iterator();
    while(ii.hasNext()) {
      sID = (String)ii.next();
      if(!map.containsKey(sID)) {
        mpET.get(sID).close();
        mpET.remove(sID);
        bRefresh  = true;
      }
    }

    // Update the targets map
    mpTargets.clear ();
    mpTargets.putAll(map);

    return bRefresh;

  } // refreshTargets()

  /**
   * This loads a configuration file.
   * @param sFilename the filename.
   * @return the root element.
   * @throws UtilException on error.
   */
  private static XmlElement getConfig(String sFilename) throws UtilException {
    XmlDocument config = new XmlDocument(new File(sFilename), false);
    return config.getRootElement();
  }

} // class EventManager
