package com.funkdefino.common.util.event;

import com.funkdefino.common.util.xml.XmlElement;
import com.funkdefino.common.util.xml.XmlValidate;
import com.funkdefino.common.util.*;
import java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * Maps event identifiers to target identifiers.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
final class EventMappings {

  //** --------------------------------------------------------------- Constants

  private final static String s_sNoEventTarget    = "No event target configured for '%s'";
  private final static String s_sDuplicateEventID = "Duplicate event identifier '%s'";
  private final static String s_sDelimiter        = ",; ";

  //** -------------------------------------------------------------------- Data

  private Map<String, List<String>>  m_mpEvents = new HashMap<String, List<String>>(); // Target lists keyed on event
  private List<String> m_lsDfltTargets = new ArrayList<String>(); // A default target list

  //** ------------------------------------------------------------ Construction
  /**
   * Ctor/
   * @param  eConfig a configuration element.
   * @param  mpEventTargets a map of configured EventTarget objects.
   * @throws UtilException on error.
   */
  public EventMappings(XmlElement eConfig, Map mpEventTargets) throws UtilException {
    initialise(eConfig, mpEventTargets);
  }

  //** -------------------------------------------------------------- Operations

  /**
   * This returns the target identifier(s) associated with the incoming event.
   * @param  sEvent the event.
   * @return a list of target identifiers.
   */
  public List<String> getTargets(String sEvent) {
    List<String> lsTargets = m_mpEvents.get(sEvent);
    return lsTargets != null ? lsTargets : m_lsDfltTargets;
  }

  //** ---------------------------------------------------------- Implementation

  /**
   * This loads an event map, a one-to-many mapping of event identifiers to
   * target identifiers - an event may be posted to more than one target.
   * @param  eConfig a configuration element.
   * @param  mpEventTargets a map of configured EventTarget objects.
   * @throws UtilException on error.
   */
  private void initialise(XmlElement eConfig, Map mpEventTargets)
               throws UtilException
  {

    String sDefault = XmlValidate.getAttribute(eConfig, ICConstants.ATTR_DEFAULT);
    if(mpEventTargets.containsKey(sDefault)) m_lsDfltTargets.add(sDefault);
    else {
      String sExcp = String.format(s_sNoEventTarget, sDefault);
      throw new UtilException(sExcp);
    }

    Iterator ii = eConfig.getChildren().iterator();
    while(ii.hasNext()) {
      XmlElement eEvent = (XmlElement)ii.next();
      String sEvent = XmlValidate.getAttribute(eEvent, ICConstants.ATTR_ID);
      String sTargets = XmlValidate.getAttribute(eEvent, ICConstants.ATTR_TARGET);
      // Check for duplicates
      if(m_mpEvents.containsKey(sEvent)) {
         String sExcp = String.format(s_sDuplicateEventID, sEvent);
         throw new UtilException(sExcp);
      }
      // Parse target(s)
      StringTokenizer strtok = new StringTokenizer(sTargets, s_sDelimiter);
      List<String> lsTargets = new ArrayList<String>();
      while(strtok.hasMoreTokens() ) {
        String sTarget = strtok.nextToken();
        if(!mpEventTargets.containsKey(sTarget)) {
           String sExcp = String.format(s_sNoEventTarget, sTarget);
           throw new UtilException(sExcp);
        }
        lsTargets.add(sTarget);
      }
      m_mpEvents.put(sEvent, lsTargets);
    }

  } // initialise()

} // class EventMappings
